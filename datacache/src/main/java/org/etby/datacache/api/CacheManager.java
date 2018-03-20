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

/**
 * Created by etby on 18-1-30.
 */

public interface CacheManager {

  /**
   * 获取一个缓存池, 会根据类型创建
   */
  CachePool getCachePool(Class<? extends Cacheable> type);
}
