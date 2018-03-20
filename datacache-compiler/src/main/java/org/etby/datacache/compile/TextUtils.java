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

class TextUtils {
  /**
   * Returns true if a and b are equal, including if they are both null.
   * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
   * both the arguments were instances of String.</i></p>
   *
   * @param a first CharSequence to check
   * @param b second CharSequence to check
   * @return true if a and b are equal
   */
  static boolean equals(CharSequence a, CharSequence b) {
    if (a == b) return true;
    int length;
    if (a != null && b != null && (length = a.length()) == b.length()) {
      if (a instanceof String && b instanceof String) {
        return a.equals(b);
      } else {
        for (int i = 0; i < length; i++) {
          if (a.charAt(i) != b.charAt(i)) return false;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * 判断字符串为空
   */
  static boolean isEmpty(CharSequence str) {
    if (str == null || str.length() == 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 判断是否为某个字符串开头
   */
  static boolean prefixes(CharSequence sequence, String prefix) {
    boolean prefixes = false;
    if (sequence.length() > prefix.length()) {
      int count = prefix.length();
      prefixes = true;
      for (int i = 0; i < count; i++) {
        if (sequence.charAt(i) != prefix.charAt(i)) {
          prefixes = false;
          break;
        }
      }
    }
    return prefixes;
  }
}