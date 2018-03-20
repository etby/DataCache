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

import org.etby.datacache.api.CachePool;
import org.etby.datacache.api.impl.CachePoolImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by etby on 18-1-30.
 */
public class CachePoolTest {

  private CachePool mPool;

  private CacheTest oneTest;
  private CacheTest twoTest;
  private String oneKey = "one";
  private String twoKey = "two";
  private CacheTest oneTestChange;

  @Before public void setUp() throws Exception {
    mPool = new CachePoolImpl(CacheTest.class);
    oneTest = TestUtils.createTest(oneKey, 1, true);
    oneTestChange = TestUtils.createTest(oneKey, 1, true);
    twoTest = TestUtils.createTest(twoKey, 2, false);
  }

  @Test public void whenNullThenNull() throws Exception {
    assertNull(mPool.getCache(null));
  }

  @Test public void whenUpdateDataChange() throws Exception {
    assertNull(mPool.getCache(oneKey));
    mPool.update(oneTest);
    assertNotNull(mPool.getCache(oneKey));

    oneTestChange.setAge(2);
    oneTestChange.getHelper().notifyCacheChanged(TCF.age);

    mPool.update(oneTestChange);
    CacheTest cache = (CacheTest) mPool.getCache(oneKey);
    assertEquals(cache.getAge(), oneTestChange.getAge());
  }

  @Test public void givenOneObjectWhenChangeThenChange() throws Exception {
    CacheTest test = null;
    mPool.update(oneTest);

    test = (CacheTest) mPool.getCache(oneKey);
    assertEquals(test.getAge(), 1);

    oneTest.setAge(2);
    test = (CacheTest) mPool.getCache(oneKey);
    assertEquals(test.getAge(), 2);
  }

  @Test public void whenForceChangeThenObjectChanged() throws Exception {
    mPool.update(oneTest);
    assertTrue(mPool.getCache(oneKey) == oneTest);

    mPool.update(oneTestChange);
    assertTrue(mPool.getCache(oneKey) == oneTest);

    mPool.forceUpdate(oneTestChange);
    assertTrue(mPool.getCache(oneKey) == oneTestChange);
  }

  @Test public void whenTrimSizeDeleteOld() throws Exception {
    mPool.update(oneTest);
    mPool.update(twoTest);
    assertEquals(mPool.getCacheCount(), 2);

    mPool.setMaxSize(1);
    assertEquals(mPool.getCacheCount(), 1);

    assertNull(mPool.getCache(oneKey));
    assertNotNull(mPool.getCache(twoKey));
  }

  @Test public void whenCleanDeleteAll() throws Exception {
    mPool.update(oneTest);
    mPool.update(twoTest);
    assertEquals(mPool.getCacheCount(), 2);

    mPool.cleanCache();
    assertEquals(mPool.getCacheCount(), 0);
  }
}