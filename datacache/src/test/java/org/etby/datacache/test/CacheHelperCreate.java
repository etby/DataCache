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

import java.util.HashMap;
import java.util.Map;
import org.etby.datacache.CacheHelper;
import org.etby.datacache.Cacheable;
import org.etby.datacache.HelperCreator;

/**
 * Created by etby on 10/26/17.
 */

class CacheHelperCreate {

  private static Map<Class, HelperCreator> S_CREATOR_MAP;

  private static void putCreator(Class cls, HelperCreator creator) {
    S_CREATOR_MAP.put(cls, creator);
  }

  static {
    S_CREATOR_MAP = new HashMap<>();
    //将对象注入
    S_CREATOR_MAP.put(CacheTest.class, new HelperCreator() {
      @Override public CacheHelper create(Cacheable target) {
        return new CacheTest_CacheHelper((CacheTest) target);
      }
    });
  }

  public static CacheHelper newHelper(Object target) {
    if (target != null && target instanceof Cacheable) {
      try {
        return S_CREATOR_MAP.get(target.getClass()).create((Cacheable) target);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}
