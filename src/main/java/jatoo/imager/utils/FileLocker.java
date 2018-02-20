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

package jatoo.imager.utils;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

/**
 * File locker utility.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.0, February 19, 2018
 */
public class FileLocker {

  private final File file;

  public FileLocker(final File file) {
    this.file = file;
  }

  public void lock() throws IOException {

    if (!file.exists()) {
      file.createNewFile();
    }

    RandomAccessFile lockRandomAccessFile = null;
    FileChannel lockChannel = null;

    try {

      lockRandomAccessFile = new RandomAccessFile(file, "rw");
      lockChannel = lockRandomAccessFile.getChannel();

      boolean isLocked = false;

      try {
        isLocked = lockChannel.tryLock() == null;
      } catch (OverlappingFileLockException e) {
        isLocked = true;
      }

      if (isLocked) {
        lockChannel.close();
        lockRandomAccessFile.close();
      }
    }

    catch (IOException e) {

      if (lockChannel != null) {
        try {
          lockChannel.close();
        } catch (IOError e2) {}
      }
      if (lockRandomAccessFile != null) {
        try {
          lockRandomAccessFile.close();
        } catch (Exception e2) {}
      }

      throw e;
    }
  }

  public boolean isLocked() throws IOException {

    if (!file.exists()) {
      return false;
    }

    RandomAccessFile lockRandomAccessFile = null;
    FileChannel lockChannel = null;

    try {

      lockRandomAccessFile = new RandomAccessFile(file, "rw");
      lockChannel = lockRandomAccessFile.getChannel();

      try {
        if (lockChannel.tryLock() == null) {
          return true;
        }
      } catch (OverlappingFileLockException e) {
        return true;
      }

      return false;
    }

    catch (IOException e) {
      throw e;
    }

    finally {

      if (lockChannel != null) {
        try {
          lockChannel.close();
        } catch (IOError e2) {}
      }
      if (lockRandomAccessFile != null) {
        try {
          lockRandomAccessFile.close();
        } catch (Exception e2) {}
      }
    }
  }

}
