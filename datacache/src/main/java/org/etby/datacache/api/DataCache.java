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

package org.etby.datacache.api;

import org.etby.datacache.Cacheable;
import org.etby.datacache.api.impl.CacheManagerFactory;

/**
 * Created by etby on 18-1-30.
 */

public class DataCache {

  private static DataCache sDataCache = new DataCache();
  private final CacheManager mCacheManager;

  public DataCache() {
    mCacheManager = CacheManagerFactory.createInstance();
  }

  /**
   * 获得单例对象
   */
  public static DataCache getInstance() {
    return sDataCache;
  }

  /**
   * 获取针对某个Type的缓存池
   */
  public CachePool getCachePool(Class<? extends Cacheable> type) {
    return mCacheManager.getCachePool(type);
  }

  /**
   * 更新数据对象, 获取唯一缓存的对象
   */
  public <T extends Cacheable> T updateCacheable(T cacheable) {
    if (cacheable == null) {
      return null;
    }

    return (T) mCacheManager.getCachePool(cacheable.getClass()).update(cacheable);
  }

  /**
   * 获取缓存在内存中的对象
   */
  public <T extends Cacheable> T getCacheable(Class<? extends Cacheable> type, String key) {
    return (T) mCacheManager.getCachePool(type).getCache(key);
  }
}
