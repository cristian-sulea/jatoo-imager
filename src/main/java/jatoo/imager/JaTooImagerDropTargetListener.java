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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JaTooImagerDropTargetListener extends DropTargetAdapter {

  private final Log logger = LogFactory.getLog(getClass());

  private final JaTooImager imager;

  public JaTooImagerDropTargetListener(final JaTooImager imager) {
    this.imager = imager;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void drop(DropTargetDropEvent event) {
    event.acceptDrop(DnDConstants.ACTION_COPY);

    Transferable transferable = event.getTransferable();

    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

      try {
        imager.setImages((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
      }

      catch (UnsupportedFlavorException | IOException e) {
        logger.error("failed to get the dragged data", e);
      }
    }
  }

}
