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
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jna.platform.FileUtils;

import jatoo.image.ImageFileFilter;
import jatoo.ui.AppFrame;
import jatoo.ui.ImageLoaderV2;
import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The application.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 3.1, January 25, 2018
 */
@SuppressWarnings("serial")
public class JaTooImager extends AppFrame {

  static {

    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
    System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "trace");

    UIUtils.setSystemLookAndFeel();
    UIResources.setResourcesBaseClass(JaTooImager.class);
  }

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

  private final Log logger = LogFactory.getLog(getClass());

  private final JaTooImagerViewer viewer;
  private final ImageLoaderV2 loader;

  private final List<File> images = new ArrayList<>();
  private int imagesIndex;

  public JaTooImager() {

    //
    // canvas & loader

    viewer = new JaTooImagerViewer();
    loader = new ImageLoaderV2(viewer);

    addDropTargetListener(new TheDropTargetListener());

    // UIUtils.forwardDragAsMove(canvas, this);
    // UIUtils.disableDecorations(this);

    UIUtils.setActionForEscapeKeyStroke(viewer, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    });

    UIUtils.setActionForRightKeyStroke(viewer, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        showNextImage();
      }
    });

    UIUtils.setActionForLeftKeyStroke(viewer, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        showPrevImage();
      }
    });

    UIUtils.setActionForDeleteKeyStroke(viewer, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        deleteImage();
      }
    });

    //
    // frame

    setContentPane(viewer);
    setVisible(true);
  }

  public JaTooImager(final File file) {
    this();

    if (file.isDirectory()) {

      images.addAll(Arrays.asList(file.getAbsoluteFile().listFiles(ImageFileFilter.getInstance())));

      if (images.size() > 0) {
        imagesIndex = 0;
        showImage(images.get(0));
      }
    }

    else {
      images.addAll(Arrays.asList(file.getAbsoluteFile().getParentFile().listFiles(ImageFileFilter.getInstance())));
      imagesIndex = images.indexOf(file.getAbsoluteFile());
      showImage(file);
    }
  }

  public synchronized void setImages(List<File> files) {

    images.clear();

    for (File file : files) {

      if (file.isDirectory()) {
        images.addAll(Arrays.asList(file.getAbsoluteFile().listFiles(ImageFileFilter.getInstance())));
      }

      else if (ImageFileFilter.getInstance().accept(file)) {
        images.add(file);
      }
    }

    if (this.images.size() > 0) {
      imagesIndex = 0;
      showImage(images.get(0));
    }
  }

  private synchronized void showImage(final File file) {

    if (file != null) {
      setTitle(file.getName());
      loader.startLoading(file);
    }

    else {
      setTitle(null);
      viewer.onImageLoaded(null, null);
    }
  }

  private synchronized void showNextImage() {

    if (images.size() == 0) {
      showImage(null);
      return;
    }

    imagesIndex++;
    if (imagesIndex >= images.size()) {
      imagesIndex = 0;
    }

    showImage(images.get(imagesIndex));
  }

  private synchronized void showPrevImage() {

    if (images.size() == 0) {
      return;
    }

    imagesIndex--;
    if (imagesIndex < 0) {
      imagesIndex = images.size() - 1;
    }

    showImage(images.get(imagesIndex));
  }

  private synchronized void deleteImage() {

    if (images.size() == 0) {
      return;
    }

    File image = images.remove(imagesIndex);
    imagesIndex--;

    try {
      FileUtils.getInstance().moveToTrash(new File[] { image });
      System.out.println("info: trash (hopefully)");
    }

    catch (IOException ex) {
      if (showConfirmationWarning(UIResources.getText("detele.warning.title"), UIResources.getText("delete.warning.message"))) {
        image.delete();
      }
    }

    showNextImage();
  }

  private class TheDropTargetListener extends DropTargetAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent event) {

      event.acceptDrop(DnDConstants.ACTION_COPY);

      Transferable transferable = event.getTransferable();

      if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

        try {
          setImages((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
        }

        catch (UnsupportedFlavorException | IOException e) {
          logger.error("failed to get the dragged data", e);
        }
      }
    }

  }
}
