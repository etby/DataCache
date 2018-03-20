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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * Created by etby on 10/25/17.
 */

class JavaUtils {
  /**
   * 通过field的对象得到应该设置的id的名字
   */
  static String stripPrefixFromField(VariableElement element) {
    Name name = element.getSimpleName();
    if (name.length() >= 2) {
      char firstChar = name.charAt(0);
      char secondChar = name.charAt(1);
      if (name.length() > 2 && firstChar == 'm' && secondChar == '_') {
        char thirdChar = name.charAt(2);
        if (Character.isJavaIdentifierStart(thirdChar)) {
          return "" + Character.toLowerCase(thirdChar) + name.subSequence(3, name.length());
        }
      } else if ((firstChar == 'm' && Character.isUpperCase(secondChar)) || (firstChar == '_'
          && Character.isJavaIdentifierStart(secondChar))) {
        return "" + Character.toLowerCase(secondChar) + name.subSequence(2, name.length());
      }
    }
    return name.toString();
  }

  /**
   * 通过Java特定get/set/is方法得到真实的字段名
   */
  static String stripPrefixFromMethod(ExecutableElement element) {
    Name name = element.getSimpleName();
    CharSequence propertyName;
    if (JavaUtils.isGetter(element) || JavaUtils.isSetter(element)) {
      propertyName = name.subSequence(3, name.length());
    } else if (JavaUtils.isBooleanGetter(element)) {
      propertyName = name.subSequence(2, name.length());
    } else {
      return null;
    }
    char firstChar = propertyName.charAt(0);
    return "" + Character.toLowerCase(firstChar) + propertyName.subSequence(1,
        propertyName.length());
  }

  /**
   * 判断是否为Set方法
   */
  static boolean isSetter(ExecutableElement element) {
    Name name = element.getSimpleName();
    return TextUtils.prefixes(name, "set")
        && Character.isJavaIdentifierStart(name.charAt(3))
        && element.getParameters().size() == 1
        && element.getReturnType().getKind() == TypeKind.VOID;
  }

  /**
   * 判断是否为Get方法
   */
  static boolean isGetter(ExecutableElement element) {
    Name name = element.getSimpleName();
    return TextUtils.prefixes(name, "get")
        && Character.isJavaIdentifierStart(name.charAt(3))
        && element.getParameters().isEmpty()
        && element.getReturnType().getKind() != TypeKind.VOID;
  }

  /**
   * 判断是否为Bool值的get
   */
  static boolean isBooleanGetter(ExecutableElement element) {
    Name name = element.getSimpleName();
    return TextUtils.prefixes(name, "is")
        && Character.isJavaIdentifierStart(name.charAt(2))
        && element.getParameters().isEmpty()
        && element.getReturnType().getKind() == TypeKind.BOOLEAN;
  }
}
