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

package org.etby.datacache.test;

import org.etby.datacache.AbstractCacheHelper;

/**
 * Created by etby on 10/25/17.
 */

class CacheTest_CacheHelper extends AbstractCacheHelper<CacheTest> {

  public CacheTest_CacheHelper(CacheTest data) {
    super(data);
  }

  @Override public int[] getAvailableCaches() {
    return new int[] {
        TCF.name, TCF.age, TCF.male, TCF.numbers, TCF.obj_list
    };
  }

  @Override public Object getField(int cf) {
    switch (cf) {
      case TCF.name:
        return data.getName();
      case TCF.age:
        return data.getAge();
      case TCF.male:
        return data.isMale();
    }
    return null;
  }

  @Override public boolean updateField(int cf, Object obj) {
    try {
      switch (cf) {
        case TCF.name:
          data.setName((String) obj);
          return true;
        case TCF.age:
          data.setAge((Integer) obj);
          return true;
        case TCF.male:
          data.setMale((Boolean) obj);
          return true;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override public int getCF(String fieldName) {
    int result = 0;
    if (fieldName != null) {
      switch (fieldName) {
        case "name":
          result = TCF.name;
          break;
        case "age":
          result = TCF.age;
          break;
        case "male":
          result = TCF.male;
          break;
      }
    }
    return result;
  }
}
