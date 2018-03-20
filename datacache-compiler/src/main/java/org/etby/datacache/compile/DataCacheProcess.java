/*
 *    Copyright 2018 Etby
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.etby.datacache.compile;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import org.etby.datacache.CacheClass;
import org.etby.datacache.CacheField;
import org.etby.datacache.CacheHelper;
import org.etby.datacache.Cacheable;
import org.etby.datacache.HelperCreator;

import static org.etby.datacache.compile.LogUtil.LOG;

@AutoService(Processor.class) @SupportedAnnotationTypes({
    "org.etby.datacache.CacheField", "org.etby.datacache.CacheClass"
}) public class DataCacheProcess extends AbstractProcessor {

  static String BASE_PKG = "org.etby.datacache";

  static String CF_NAME = "CF";
  static String CF_CLASS = BASE_PKG + "." + CF_NAME;

  static String CREATOR_NAME = "CacheHelperCreate";
  static String CREATOR_CLASS = BASE_PKG + "." + CREATOR_NAME;

  static String S_CREATOR_MAP = "S_CREATOR_MAP";

  @Override public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
    LOG("DataCacheProcess 开始处理");
    // initConfig

    // 生成 CF 文件
    generateCFFile(roundEnv);
    // 生成 Class 相关文件
    generateCCFile(roundEnv);
    // 生成 CacheHelperCreate
    generateCHCFile(roundEnv);

    return true;
  }

  /**
   * 生成CF文件
   * 所有{@link CacheField}所标注的Field的id
   */
  private void generateCFFile(RoundEnvironment roundEnv) {
    LOG("开始生成CF文件");

    Set<String> nameSet = new LinkedHashSet<>();

    List<Element> elements = AnnotationUtil.getElementsAnnotatedWith(roundEnv, CacheField.class);
    if (elements == null || elements.isEmpty()) {
      return;
    }
    LOG("CF字段数量 :" + elements.size());

    for (Element element : elements) {
      switch (element.getKind()) {
        case FIELD:
          String fieldName = JavaUtils.stripPrefixFromField((VariableElement) element);
          if (fieldName != null) {
            nameSet.add(fieldName);
          }
          break;
      }
    }

    // 创建类
    TypeSpec.Builder cfClassBuilder =
        TypeSpec.classBuilder(CF_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    // 增加字段
    ArrayList<String> nameList = new ArrayList<>(nameSet);
    for (int i = 0; i < nameList.size(); i++) {
      String name = nameList.get(i);
      FieldSpec fieldSpec =
          FieldSpec.builder(int.class, name, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer(String.valueOf(i + 1))
              .build();
      cfClassBuilder.addField(fieldSpec);
    }

    // 创建文件
    JavaFile javaFile = JavaFile.builder(BASE_PKG, cfClassBuilder.build()).build();

    try {
      // 写入文件
      javaFile.writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      e.printStackTrace();
    }

    LOG("CF文件 生成完成");
  }

  /**
   * 生成Helper文件
   */
  private void generateCCFile(RoundEnvironment roundEnv) {
    LOG("开始处理缓存类");

    List<Element> classElements =
        AnnotationUtil.getElementsAnnotatedWith(roundEnv, CacheClass.class);
    if (classElements == null || classElements.isEmpty()) {
      return;
    }
    LOG("缓存类数量 : " + classElements.size());

    // 生成 Helper 文件
    for (Element element : classElements) {
      if (element instanceof TypeElement) {
        LOG("生成 CacheHelper :" + ((TypeElement) element).getQualifiedName().toString());
        CacheHelperProcess helperProcess =
            new CacheHelperProcess(roundEnv, (TypeElement) element, processingEnv);
        helperProcess.generate();
      }
    }

    // 生成 Mapping 文件

    // 生成 inject 文件

  }

  /**
   * 生成CacheHelperCreate 用来根据对象改变
   */
  private void generateCHCFile(RoundEnvironment roundEnv) {
    LOG("开始生成 CacheHelperCreate");

    List<Element> elements = AnnotationUtil.getElementsAnnotatedWith(roundEnv, CacheClass.class);
    if (elements == null || elements.isEmpty()) {
      return;
    }
    LOG("缓存类数量 :" + elements.size());

    ArrayList<TypeElement> cacheList = new ArrayList<>();
    for (Element element : elements) {
      cacheList.add((TypeElement) element);
    }

    // 创建类
    TypeSpec.Builder classBuilder =
        TypeSpec.classBuilder(CREATOR_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    // S_CREATOR_MAP
    FieldSpec creatorMap =
        FieldSpec.builder(ParameterizedTypeName.get(Map.class, Class.class, HelperCreator.class),
            S_CREATOR_MAP, Modifier.PRIVATE, Modifier.STATIC).build();
    classBuilder.addField(creatorMap);

    // putCreator
    MethodSpec putCreator = MethodSpec.methodBuilder("putCreator")
        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
        .addParameter(Class.class, "cls")
        .addParameter(HelperCreator.class, "creator")
        .addStatement("S_CREATOR_MAP.put(cls, creator)")
        .build();
    classBuilder.addMethod(putCreator);

    // 创建静态块
    CodeBlock.Builder staticCode = CodeBlock.builder();
    staticCode.add("S_CREATOR_MAP = new $T<>();", HashMap.class);//初始化map

    for (TypeElement element : cacheList) {
      staticCode.add("S_CREATOR_MAP.put($L.class, new $L() {",
          element.getQualifiedName().toString(), HelperCreator.class.getCanonicalName());

      staticCode.add(" @Override ");

      staticCode.add("public $L create($L target) {", CacheHelper.class.getCanonicalName(),
          Cacheable.class.getCanonicalName());

      staticCode.add("return new $L(($L) target);",
          org.etby.datacache.compile.HelperUtils.getHelperQualifiedName(getElementUtils(), element),
          element.getQualifiedName().toString());

      staticCode.add(" }});");
    }

    classBuilder.addStaticBlock(staticCode.build());

    // newHelper

    MethodSpec.Builder newHelper = MethodSpec.methodBuilder("newHelper")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(CacheHelper.class)
        .addParameter(Object.class, "target");

    CodeBlock.Builder code = CodeBlock.builder();
    code.add("if (target != null && target instanceof $T) {", Cacheable.class);
    // try start
    code.add("try {");

    code.add("return $L.get(target.getClass()).create(($T) target);", S_CREATOR_MAP,
        Cacheable.class);

    code.add(" } catch ($T e) { e.printStackTrace();}", Throwable.class);
    // try end
    code.add("}");
    code.add("return null;");
    newHelper.addCode(code.build());

    classBuilder.addMethod(newHelper.build());

    // 创建文件
    JavaFile javaFile = JavaFile.builder(BASE_PKG, classBuilder.build()).build();

    try {
      // 写入文件
      javaFile.writeTo(processingEnv.getFiler());
      //            javaFile.writeTo(System.out);
    } catch (IOException e) {
      e.printStackTrace();
    }

    LOG("CacheHelperCreate 文件 生成完成");
  }

  private Elements getElementUtils() {
    return processingEnv.getElementUtils();
  }
}
