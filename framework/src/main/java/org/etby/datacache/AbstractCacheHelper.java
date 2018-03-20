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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by etby on 10/25/17.
 */

public abstract class AbstractCacheHelper<T extends Cacheable> implements CacheHelper {

  protected T data;

  private HashSet<Integer> mCFs = new HashSet<>();

  public AbstractCacheHelper(T data) {
    this.data = data;
  }

  @Override public boolean isAvailableField(int cf) {
    int[] cfs = getAvailableCaches();
    if (cfs != null && cfs.length != 0) {
      Arrays.sort(cfs);
      if (Arrays.binarySearch(cfs, cf) > -1) {
        return true;
      }
    }
    return false;
  }

  @Override public void notifyCacheChanged(int cf) {
    mCFs.add(cf);
  }

  @Override public int[] getChangedCaches() {
    int[] result = new int[mCFs.size()];

    int i = 0;
    Iterator<Integer> iterator = mCFs.iterator();
    while (iterator.hasNext()) {
      result[i] = iterator.next();
      i++;
    }

    return result;
  }

  @Override public void resetCache() {
    mCFs.clear();
  }

  @Override public void updateTo(int[] cfs, Cacheable target) {
    if (cfs == null || cfs.length == 0) {
      return;
    }
    CacheHelper helper = target.getHelper();
    if (helper == null) {
      return;
    }

    for (int cf : cfs) {
      helper.updateField(cf, getField(cf));
    }
  }

  @Override public void updateTo(Cacheable target) {
    updateTo(getChangedCaches(), target);
  }
}
