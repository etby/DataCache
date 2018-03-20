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

package org.etby.datacache.api.impl;

/**
 * Created by etby on 18-1-30.
 */

public class CacheSign {

  private Class<?> type;
  private String key;

  public CacheSign() {
  }

  public CacheSign(Class<?> type, String key) {
    this.type = type;
    this.key = key;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CacheSign cacheSign = (CacheSign) o;

    if (type != null ? !type.equals(cacheSign.type) : cacheSign.type != null) {
      return false;
    }
    return key != null ? key.equals(cacheSign.key) : cacheSign.key == null;
  }

  @Override public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (key != null ? key.hashCode() : 0);
    return result;
  }
}
