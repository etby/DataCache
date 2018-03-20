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

public interface CachePool {

  /**
   * 默认的存储大小 超过存储大小会将对象删除
   */
  int DEFAULT_TYPE_MAX_SIZE = 500;
  /**
   * 默认的对象大小 默认大小为1
   */
  int DEFAULT_OBJECT_SIZE = 1;

  /**
   * 获得当前的池大小
   */
  int getMaxSize();

  /**
   * 设置当前的池大小
   */
  void setMaxSize(int size);

  /**
   * 获得当前缓存的数量
   */
  int getCacheCount();

  /**
   * 根据Key获得对象
   */
  Cacheable getCache(String cacheKey);

  /**
   * 根据当前的对象更新全局的对象, 并且返回全局对象
   * 如果全局对象为空, 则将当前对象设为全局对象并返回
   */
  Cacheable update(Cacheable cacheable);

  /**
   * 强制将当前对象设置为缓存中的对象
   */
  Cacheable forceUpdate(Cacheable cacheable);

  /**
   * 清空所有缓存
   */
  void cleanCache();
}
