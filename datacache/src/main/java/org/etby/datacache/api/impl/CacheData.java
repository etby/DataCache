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

import java.lang.ref.WeakReference;
import org.etby.datacache.CacheHelper;
import org.etby.datacache.Cacheable;

/**
 * Created by etby on 18-1-30.
 */

public class CacheData {

  /**
   * 缓存对象, 使用软引用保存
   */
  private String key;
  private WeakReference<Cacheable> target;
  private int hitCnt;
  private int missCnt;
  private int updateCnt;

  public CacheData(String key) {
    this.key = key;
  }

  public CacheData(String key, Cacheable cacheable) {
    this.key = key;
    this.target = new WeakReference<>(cacheable);
  }

  public String getKey() {
    return key;
  }

  public Cacheable getCacheable() {
    Cacheable result = target != null ? target.get() : null;
    if (result == null) {
      missCnt++;
    } else {
      hitCnt++;
    }
    return result;
  }

  public synchronized Cacheable updateCacheable(Cacheable cacheable) {
    Cacheable result = getCacheable();
    if (result != null) {
      CacheHelper helper = cacheable.getHelper();
      helper.updateTo(result);
    } else {
      target = new WeakReference<>(cacheable);
      result = cacheable;
    }
    updateCnt++;
    return result;
  }

  public int getHitCnt() {
    return hitCnt;
  }

  public int getMissCnt() {
    return missCnt;
  }

  public int getUpdateCnt() {
    return updateCnt;
  }

  @Override public String toString() {
    return "CacheData{"
        + "key='"
        + key
        + '\''
        + ", hitCnt="
        + hitCnt
        + ", missCnt="
        + missCnt
        + ", updateCnt="
        + updateCnt
        + '}';
  }
}
