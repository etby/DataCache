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

package org.etby.datacache.compile;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

/**
 * Created by etby on 10/25/17.
 */

public class BaseProcess {

  protected RoundEnvironment roundEnv;
  protected ProcessingEnvironment processEnv;

  public BaseProcess(RoundEnvironment roundEnv, ProcessingEnvironment processEnv) {
    this.roundEnv = roundEnv;
    this.processEnv = processEnv;
  }

  protected Elements getElementUtils() {
    return processEnv.getElementUtils();
  }
}
