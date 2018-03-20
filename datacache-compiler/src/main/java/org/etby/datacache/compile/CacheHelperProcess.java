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

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.etby.datacache.AbstractCacheHelper;
import org.etby.datacache.CacheField;

import static org.etby.datacache.compile.LogUtil.LOG;

/**
 * Created by etby on 10/25/17.
 */

public class CacheHelperProcess extends BaseProcess {

  private final String mHelperQualifiedName;
  private final String mHelperSimpleName;
  private TypeSpec.Builder mClassBuilder;
  private final String mHelperPackageName;
  private final String mTargetQualifiedName;
  TypeElement targetElement;
  private ClassName targetClassName;

  private Map<String, CacheFieldHolder> mHolders = new HashMap<>();

  public CacheHelperProcess(RoundEnvironment roundEnv, TypeElement targetElement,
      ProcessingEnvironment processEnv) {
    super(roundEnv, processEnv);
    this.targetElement = targetElement;
    mHelperSimpleName = org.etby.datacache.compile.HelperUtils.getHelperSimpleName(targetElement);
    mHelperPackageName =
        org.etby.datacache.compile.HelperUtils.getHelperPackageName(getElementUtils(),
            targetElement);
    mHelperQualifiedName =
        org.etby.datacache.compile.HelperUtils.getHelperQualifiedName(getElementUtils(),
            targetElement);
    mTargetQualifiedName = targetElement.getQualifiedName().toString();
    targetClassName = ClassName.get(targetElement);
  }

  CacheFieldHolder getHolder(String cfName) {
    CacheFieldHolder holder = mHolders.get(cfName);
    if (holder == null) {
      holder = new CacheFieldHolder();
      mHolders.put(cfName, holder);
    }
    return holder;
  }

  void generate() {
    // 生成类定义
    initClass();

    // 生成构造方法
    addConstructor();

    // 得到cf对象
    initCacheFields();

    // 生成 getAvailableCaches
    getAvailableCaches();

    // 生成 getField
    getField();

    // 生成 updateField
    updateField();

    // 生成 getCF
    getCF();

    // 产出文件
    JavaFile javaFile = JavaFile.builder(mHelperPackageName, mClassBuilder.build()).build();
    try {
      javaFile.writeTo(processEnv.getFiler());
      //            javaFile.writeTo(System.out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 类定义
   */
  private void initClass() {
    // 创建继承对象
    ParameterizedTypeName extendHelper =
        ParameterizedTypeName.get(ClassName.get(AbstractCacheHelper.class), targetClassName);

    //创建类
    mClassBuilder =
        TypeSpec.classBuilder(mHelperSimpleName).addOriginatingElement(targetElement)// import data
            .superclass(extendHelper).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
  }

  /**
   * 构造方法
   */
  private void addConstructor() {
    MethodSpec constructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ClassName.get(targetElement), "data")
        .addCode("super(data);")
        .build();
    mClassBuilder.addMethod(constructor);
  }

  private void initCacheFields() {
    // 所有对象
    List<Element> allMembers = new ArrayList<>(getElementUtils().getAllMembers(targetElement));
    List<Element> fieldMembers =
        AnnotationUtil.getElementsAnnotatedWith(allMembers, CacheField.class);

    // 过滤所有CacheField
    for (Element element : fieldMembers) {
      switch (element.getKind()) {
        case FIELD:
          if (AnnotationUtil.isAnnotationElement(element, CacheField.class)) {
            String cfName = JavaUtils.stripPrefixFromField((VariableElement) element);
            CacheFieldHolder holder = getHolder(cfName);
            holder.name = cfName;
            holder.element = (VariableElement) element;
          }
          break;
      }
    }

    // 可用的cf
    Set<String> availableName = mHolders.keySet();
    LOG("得到当前类的CF对象 :" + availableName.size());

    // 获得get/set
    for (Element element : allMembers) {
      switch (element.getKind()) {
        case METHOD:
          LOG("处理GET/SET " + element.getSimpleName().toString());
          ExecutableElement execute = (ExecutableElement) element;
          String cfName = JavaUtils.stripPrefixFromMethod(execute);
          if (TextUtils.isEmpty(cfName) || !availableName.contains(cfName)) {
            continue;
          }
          CacheFieldHolder holder = getHolder(cfName);
          if (JavaUtils.isGetter(execute) || JavaUtils.isBooleanGetter(execute)) {
            holder.get = execute;
          } else if (JavaUtils.isSetter(execute)) {
            holder.set = execute;
          }
          break;
      }
    }
    LOG("GET/SET 获取完成");
  }

  /**
   * getAvailableCaches 方法
   */
  private void getAvailableCaches() {
    // 类名
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("getAvailableCaches").addModifiers(Modifier.PUBLIC);
    // 返回值
    builder.returns(TypeName.get(int[].class));
    // 注解
    builder.addAnnotation(ClassName.get(Override.class));

    //代码
    builder.addCode("return new int[]{");

    ArrayList<String> names = new ArrayList<>(mHolders.keySet());

    for (int i = 0; i < names.size(); i++) {
      builder.addCode(AnnotationUtil.getCFQualifiedName(names.get(i)));
      if (i != names.size() - 1) {
        builder.addCode(",");
      }
    }

    builder.addCode("};");

    mClassBuilder.addMethod(builder.build());
  }

  /**
   * getField 方法
   */
  private void getField() {
    // 类名
    MethodSpec.Builder builder = MethodSpec.methodBuilder("getField").addModifiers(Modifier.PUBLIC);
    // 返回值
    builder.returns(ArrayTypeName.get(Object.class));
    // 注解
    builder.addAnnotation(ClassName.get(Override.class));
    // 参数
    builder.addParameter(TypeName.get(int.class), "cf");

    //代码
    builder.addCode("switch(cf) {");

    ArrayList<String> names = new ArrayList<>(mHolders.keySet());

    // case cf return data
    for (int i = 0; i < names.size(); i++) {
      String cfName = names.get(i);
      CacheFieldHolder holder = mHolders.get(cfName);
      if (holder == null || holder.get == null) {
        continue;
      }
      builder.addCode("case $L:", AnnotationUtil.getCFQualifiedName(cfName));
      builder.addCode("return data.$L();", holder.get.getSimpleName());
    }

    builder.addCode("}");
    builder.addCode("return null;");

    mClassBuilder.addMethod(builder.build());
  }

  private void updateField() {
    // 类名
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("updateField").addModifiers(Modifier.PUBLIC);
    // 返回值
    builder.returns(ArrayTypeName.get(boolean.class));
    // 注解
    builder.addAnnotation(ClassName.get(Override.class));
    // 参数
    builder.addParameter(TypeName.get(int.class), "cf");
    builder.addParameter(TypeName.get(Object.class), "obj");

    //代码

    // try start
    builder.addCode("try {");

    // switch start
    builder.addCode("switch(cf) {");
    ArrayList<String> names = new ArrayList<>(mHolders.keySet());

    // case cf return data
    for (int i = 0; i < names.size(); i++) {
      String cfName = names.get(i);
      CacheFieldHolder holder = mHolders.get(cfName);
      if (holder == null || holder.set == null || holder.element == null) {
        continue;
      }

      builder.addCode("case $L:", AnnotationUtil.getCFQualifiedName(cfName));

      builder.addCode("data.$L(($L) obj);", holder.set.getSimpleName(),
          ParameterizedTypeName.get(holder.element.asType()));

      builder.addCode("return true;");
    }

    builder.addCode("}");
    // switch end

    // try end
    builder.addCode("} catch (Throwable e) { e.printStackTrace();}");

    builder.addCode("return false;");

    mClassBuilder.addMethod(builder.build());
  }

  private void getCF() {
    String fieldName = "fieldName";

    // 类名
    MethodSpec.Builder builder = MethodSpec.methodBuilder("getCF").addModifiers(Modifier.PUBLIC);
    // 返回值
    builder.returns(int.class);
    // 注解
    builder.addAnnotation(ClassName.get(Override.class));
    // 参数
    builder.addParameter(TypeName.get(String.class), fieldName);

    //代码
    builder.addCode("int result = 0;");

    // if
    builder.addCode("if (fieldName != null) {");

    // switch start
    builder.addCode("switch(fieldName) {");
    ArrayList<String> names = new ArrayList<>(mHolders.keySet());
    // cases
    for (int i = 0; i < names.size(); i++) {
      String cfName = names.get(i);
      builder.addCode("case $S:", cfName);
      builder.addCode("result = $L;", AnnotationUtil.getCFQualifiedName(cfName));
      builder.addCode("break;");
    }
    builder.addCode("}");
    // switch end

    builder.addCode("}");
    // if end

    builder.addCode("return result;");

    mClassBuilder.addMethod(builder.build());
  }
}
