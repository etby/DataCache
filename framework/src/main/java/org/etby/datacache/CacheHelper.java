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

package org.etby.datacache;

/**
 * Created by etby on 10/25/17.
 */

public interface CacheHelper {

  /**
   * 设置字段被更新, 字段的ID必须用{@link CacheField}注释的Field自动生成的
   */
  void notifyCacheChanged(int cf);

  /**
   * 判断对象是否有此字段进行更新
   */
  boolean isAvailableField(int cf);

  /**
   * 获得所有可缓存的Field的列表
   */
  int[] getAvailableCaches();

  /**
   * 获得改变的ID的数组
   */
  int[] getChangedCaches();

  /**
   * 重置cache, 会将之前{@link CacheHelper#notifyCacheChanged(int)}设置的ID清空
   */
  void resetCache();

  /**
   * 将当前字段的改变更新到远程
   *
   * @param cfs 需要更新的字段
   * @param target 要更新的对象
   */
  void updateTo(int[] cfs, Cacheable target);

  /**
   * 使用当前Helper中更改的字段更新远程对象
   */
  void updateTo(Cacheable target);

  /**
   * 根据Field的名称获得值
   */
  int getCF(String fieldName);

  /**
   * 获得此field上的值
   */
  Object getField(int cf);

  /**
   * 更新自身的字段
   */
  boolean updateField(int cf, Object object);
}
