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

import java.util.HashMap;
import org.etby.datacache.Cacheable;
import org.etby.datacache.api.CacheManager;
import org.etby.datacache.api.CachePool;

/**
 * Created by etby on 18-1-30.
 */

class CacheManagerImpl implements CacheManager {

  private HashMap<Class, CachePool> mPools = new HashMap<>();

  @Override public CachePool getCachePool(Class<? extends Cacheable> type) {
    if (type == null) {
      throw new NullPointerException();
    }

    CachePool pool = mPools.get(type);
    if (pool == null) {
      synchronized (mPools) {
        pool = mPools.get(type);
        if (pool == null) {
          pool = createPool(type);
          mPools.put(type, pool);
        }
      }
    }

    return pool;
  }

  private CachePool createPool(Class type) {
    return new CachePoolImpl(type);
  }
}
