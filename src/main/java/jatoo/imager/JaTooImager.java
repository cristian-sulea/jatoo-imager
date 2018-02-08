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
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The launcher.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.1, February 5, 2018
 */
public class JaTooImager {

  static {

    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
    System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "trace");

    UIUtils.setSystemLookAndFeel();
    UIResources.setResourcesBaseClass(JaTooImagerWindow.class);
  }

  private static final Log logger = LogFactory.getLog(JaTooImager.class);

  public static void main(String[] args) {

    try {

      if (args.length > 0) {
        new JaTooImagerWindow(new File(args[0]));
      }

      else if (new File("src/main/java").exists()) {
        // new JaTooImager();
        new JaTooImagerWindow(new File("d:\\Temp\\xxx\\"));
      }

      File images = new File(System.getProperty("user.home"), ".jatoo" + File.separatorChar + ".imager" + File.separatorChar + "images");
      images.mkdirs();
      for (File file : images.listFiles()) {
        file.delete();
      }

      //
      // uneori read file este prea rapid si fisierul este gol
      // celalalt proces nu a apucat sa termine de scris

      WatchService watcher = FileSystems.getDefault().newWatchService();
      Path dir = images.toPath();
      WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

      for (;;) {

        key = watcher.take();

        for (WatchEvent<?> event : key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          if (kind == StandardWatchEventKinds.OVERFLOW) {
            continue;
          }

          @SuppressWarnings("unchecked")
          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path path = ev.context();
          File file = dir.resolve(path).toFile();

          try {
            new JaTooImagerWindow(new File(FileUtils.readFileToString(file).trim()));
            file.delete();
          }

          catch (IOException e) {
            logger.error("failed to read the file with the image path", e);
          }
        }

        boolean valid = key.reset();
        if (!valid) {
          break;
        }
      }
    }

    catch (IOException | InterruptedException e) {
      logger.fatal("failed to watch and wait for new images", e);
    }
  }
}
