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

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;

/**
 * Created by etby on 10/25/17.
 */

public class HelperUtils {

  static final String HELPER_EXT = "_CacheHelper";
  static final String HELPER_PKG = "helper";

  static String getHelperSimpleName(Element target) {
    return target.getSimpleName().toString() + HELPER_EXT;
  }

  static String getHelperPackageName(Elements utils, Element target) {
    PackageElement packageElement = utils.getPackageOf(target);
    return packageElement.getQualifiedName().toString() + "." + HELPER_PKG;
  }

  static String getHelperQualifiedName(Elements utils, Element target) {
    return getHelperPackageName(utils, target) + "." + getHelperSimpleName(target);
  }
}
