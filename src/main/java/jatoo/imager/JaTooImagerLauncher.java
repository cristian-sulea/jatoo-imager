/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jatoo.imager;

import java.io.File;

/**
 * The launcher.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.0, February 2, 2018
 */
public class JaTooImagerLauncher {

  public static void main(String[] args) {

    if (new File("src/main/java").exists()) {
      // new JaTooImager();
      new JaTooImager(new File("d:\\Temp\\xxx\\"));
    }

    else {
      if (args.length == 0) {
        new JaTooImager();
      } else {
        new JaTooImager(new File(args[0]));
      }
    }
  }
}
