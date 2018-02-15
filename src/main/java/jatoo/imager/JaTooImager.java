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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import info.debatty.java.stringsimilarity.JaroWinkler;
import jatoo.image.ImageFileFilter;
import jatoo.image.ImageUtils;
import jatoo.ui.ImageLoader;
import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The app/launcher.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.1, February 14, 2018
 */
@SuppressWarnings("serial")
public class JaTooImager {

  static {

    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
    System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "trace");

    UIUtils.setSystemLookAndFeel();
    UIResources.setResourcesBaseClass(JaTooImager.class);
  }

  private static final Log logger = LogFactory.getLog(JaTooImager.class);

  public static void main(String[] args) {

    try {

      if (args.length > 0) {
        new JaTooImager(new File(args[0]));
      }

      else if (new File("src/main/java").exists()) {
        new JaTooImager(new File("D:\\Temp\\xxx\\re?eta dermatita seboreica.jpg"));
        // new JaTooImager(new File("d:\\Temp\\xxx\\"));
      }

      else {
        new JaTooImager();
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
            new JaTooImager(new File(FileUtils.readFileToString(file).trim()));
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

  private final JaTooImagerWindow window;
  private final ImageLoader loader;

  private final List<File> images;
  private int imagesIndex;

  public JaTooImager() {

    window = new JaTooImagerWindow(this);
    window.addDropTargetListener(new TheDropTargetListener());

    loader = new ImageLoader(window);

    images = new ArrayList<>();

    //
    //

    UIUtils.setActionForEscapeKeyStroke(window.getContentPane(), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        closeWindow();
      }
    });

    window.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closeWindow();
      }
    });
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

      if (imagesIndex != -1) {
        showImage(file);
      }

      else {

        imagesIndex = 0;

        JaroWinkler jw = new JaroWinkler();
        double jwSimilarity = jw.similarity(file.getName(), images.get(imagesIndex).getName());
        for (int i = imagesIndex + 1; i < images.size(); i++) {
          double jwSimilarityTmp = jw.similarity(file.getName(), images.get(i).getName());
          if (jwSimilarityTmp > jwSimilarity) {
            jwSimilarity = jwSimilarityTmp;
            imagesIndex = i;
          }
        }

        showImage(images.get(imagesIndex));
      }
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

  private void showImage(final File file) {

    if (file != null) {
      loader.startLoading(file);
    }

    else {

      loader.stopLoading();

      window.setTitle(null);
      window.showImage(null);
    }
  }

  public synchronized void showNext() {

    if (images.size() == 0) {

      window.setTitle(null);
      window.showImage(null);

      return;
    }

    imagesIndex++;
    if (imagesIndex >= images.size()) {
      imagesIndex = 0;
    }

    showImage(images.get(imagesIndex));
  }

  public synchronized void showPrev() {

    if (images.size() == 0) {

      window.setTitle(null);
      window.showImage(null);

      return;
    }

    imagesIndex--;
    if (imagesIndex < 0) {
      imagesIndex = images.size() - 1;
    }

    showImage(images.get(imagesIndex));
  }

  public synchronized void delete() {

    if (images.size() == 0) {
      return;
    }

    File image = images.remove(imagesIndex);
    imagesIndex--;

    try {
      com.sun.jna.platform.FileUtils.getInstance().moveToTrash(new File[] { image });
      logger.info("image (" + image + ") deleted (moved to trash)");
    }

    catch (IOException ex) {
      if (window.showConfirmationWarning(UIResources.getText("detele.warning.title"), UIResources.getText("delete.warning.message"))) {
        image.delete();
        logger.info("image (" + image + ") deleted (permanently)");
      }
    }

    showNext();
  }

  public synchronized void zoomIn() {
    window.zoomIn();
  }

  public synchronized void zoomOut() {
    window.zoomOut();
  }

  public synchronized void zoomToBestFit() {
    window.zoomToBestFit();
  }

  public synchronized void zoomToRealSize() {
    window.zoomToRealSize();
  }

  public synchronized void rotateLeft() {

    BufferedImage image = window.getImage();

    if (image != null) {
      window.showImage(ImageUtils.rotate(image, 270));
    }
  }

  public synchronized void rotateRight() {

    BufferedImage image = window.getImage();

    if (image != null) {
      window.showImage(ImageUtils.rotate(image, 90));
    }
  }

  public synchronized void closeWindow() {

    window.setVisible(false);
    window.saveProperties();
    window.dispose();

    loader.stopThread();

    System.gc();
  }

  public synchronized void exit() {
    closeWindow();
    System.exit(0);
  }

  //
  //

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
