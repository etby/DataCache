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

package org.etby.binddatasync.po;

import org.etby.datacache.CacheClass;
import org.etby.datacache.CacheField;
import org.etby.datacache.CacheHelper;
import org.etby.datacache.CacheHelperCreate;
import org.etby.datacache.Cacheable;

/**
 * Created by etby on 1/26/18.
 */

@CacheClass public class Foo implements Cacheable {

  @CacheField private String name;
  @CacheField private int age;

  private final CacheHelper helper;

  {
    helper = CacheHelperCreate.newHelper(this);
  }

  @Override public CacheHelper getHelper() {
    return helper;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override public String getCacheKey() {
    return name;
  }
}
