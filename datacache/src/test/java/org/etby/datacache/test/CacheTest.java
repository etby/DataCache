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

import java.util.List;
import org.etby.datacache.CacheClass;
import org.etby.datacache.CacheField;
import org.etby.datacache.CacheHelper;
import org.etby.datacache.Cacheable;

/**
 * Created by etby on 10/25/17.
 */

@CacheClass class CacheTest implements Cacheable {

  private CacheHelper mCacheHelper;
  @CacheField private String name;
  @CacheField private int age;
  @CacheField private boolean male;
  @CacheField private int[] numbers;
  @CacheField private List<Object> obj_list;

  public CacheTest() {
    mCacheHelper = CacheHelperCreate.newHelper(this);
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

  public boolean isMale() {
    return male;
  }

  public void setMale(boolean male) {
    this.male = male;
  }

  public int[] getNumbers() {
    return numbers;
  }

  public void setNumbers(int[] numbers) {
    this.numbers = numbers;
  }

  public CacheHelper getCacheHelper() {
    return mCacheHelper;
  }

  public void setCacheHelper(CacheHelper cacheHelper) {
    mCacheHelper = cacheHelper;
  }

  public List<Object> getObj_list() {
    return obj_list;
  }

  public void setObj_list(List<Object> obj_list) {
    this.obj_list = obj_list;
  }

  @Override public CacheHelper getHelper() {
    return mCacheHelper;
  }

  @Override public String getCacheKey() {
    return name;
  }
}
