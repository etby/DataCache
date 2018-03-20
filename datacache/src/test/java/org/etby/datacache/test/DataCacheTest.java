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

import org.etby.datacache.api.DataCache;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by etby on 18-1-30.
 */
public class DataCacheTest {

  private DataCache mCache;
  private String oneKey = "one";
  private CacheTest oneTestChange;
  private CacheTest oneTest;

  @Before public void setUp() throws Exception {
    mCache = DataCache.getInstance();
    oneTest = TestUtils.createTest(oneKey, 1, true);
    oneTestChange = TestUtils.createTest(oneKey, 2, false);
  }

  @Test public void whenUpdateThenDataChanged() throws Exception {
    CacheTest test = null;

    test = mCache.updateCacheable(oneTest);
    assertTrue(test == oneTest);

    oneTestChange.getHelper().notifyCacheChanged(TCF.age);
    oneTestChange.getHelper().notifyCacheChanged(TCF.male);

    test = mCache.updateCacheable(oneTestChange);
    assertTrue(test == oneTest);
    assertEquals(oneTestChange.getAge(), test.getAge());
    assertEquals(oneTestChange.isMale(), test.isMale());
  }
}