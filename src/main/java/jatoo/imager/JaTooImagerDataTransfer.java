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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class provides the base for implementing various transferable handler.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.0, 7 March, 2019
 */
public class JaTooImagerDataTransfer {

  private final JaTooImager imager;

  public JaTooImagerDataTransfer(final JaTooImager imager) {
    this.imager = imager;
  }

  @SuppressWarnings("unchecked")
  public final void process(final Transferable transferable) throws UnsupportedFlavorException, IOException {

    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      process((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
    }

    else {
      throw new UnsupportedFlavorException(transferable.getTransferDataFlavors()[0]);
    }
  }

  private void process(List<File> files) {
    imager.setImages(files);
  }

}
