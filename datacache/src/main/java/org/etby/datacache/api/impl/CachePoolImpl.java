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

import org.etby.datacache.Cacheable;
import org.etby.datacache.api.CachePool;

/**
 * Created by etby on 18-1-30.
 */

public class CachePoolImpl extends LruCache<CacheSign, CacheData> implements CachePool {

  private final Class mTargetType;

  public CachePoolImpl(Class targetType) {
    super(CachePool.DEFAULT_TYPE_MAX_SIZE);
    this.mTargetType = targetType;
  }

  @Override public int getMaxSize() {
    return maxSize();
  }

  @Override public void setMaxSize(int size) {
    trimToSize(size);
  }

  @Override public int getCacheCount() {
    return size();
  }

  @Override protected int sizeOf(CacheSign key, CacheData value) {
    return DEFAULT_OBJECT_SIZE;
  }

  @Override public Cacheable getCache(String cacheKey) {
    if (emptyKey(cacheKey)) {
      return null;
    }
    CacheData data = get(getSign(cacheKey));
    return data != null ? data.getCacheable() : null;
  }

  @Override public Cacheable update(Cacheable cacheable) {
    if (cacheable == null || emptyKey(cacheable.getCacheKey())) {
      return null;
    }

    CacheData data = getOrCreateData(cacheable.getCacheKey());
    return data.updateCacheable(cacheable);
  }

  @Override public Cacheable forceUpdate(Cacheable cacheable) {
    if (cacheable == null || emptyKey(cacheable.getCacheKey())) {
      return null;
    }

    String cacheKey = cacheable.getCacheKey();
    put(getSign(cacheKey), new CacheData(cacheKey, cacheable));
    return cacheable;
  }

  @Override public void cleanCache() {
    evictAll();
  }

  private CacheData getOrCreateData(String key) {
    CacheSign sign = getSign(key);
    CacheData data = get(sign);
    if (data == null) {
      data = new CacheData(key);
      put(sign, data);
    }
    return data;
  }

  private CacheSign getSign(String key) {
    return new CacheSign(mTargetType, key);
  }

  public boolean emptyKey(String cacheKey) {
    return cacheKey == null || "".equals(cacheKey.trim());
  }
}
