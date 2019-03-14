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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
import javax.swing.JComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import info.debatty.java.stringsimilarity.JaroWinkler;
import jatoo.image.ImageCache;
import jatoo.image.ImageCacheFile;
import jatoo.image.ImageCacheMemory;
import jatoo.image.ImageFileFilter;
import jatoo.image.ImageMetadataHandler;
import jatoo.image.ImageUtils;
import jatoo.imager.actions.ActionCloseWindow;
import jatoo.imager.actions.ActionCopyImageToClipboard;
import jatoo.imager.actions.ActionCopyVisibleImageToClipboard;
import jatoo.imager.actions.ActionExit;
import jatoo.imager.actions.ActionPasteFromClipboard;
import jatoo.imager.utils.FileLocker;
import jatoo.ui.AppWindowFrame;
import jatoo.ui.ImageCanvas;
import jatoo.ui.ImageLoader;
import jatoo.ui.ImageLoaderListener;
import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The app/launcher.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.2, February 19, 2018
 */
@SuppressWarnings("serial")
public class JaTooImager extends AppWindowFrame implements ImageLoaderListener  {

  private static final File WORKING_FOLDER = new File(new File(System.getProperty("user.home"), ".jatoo"), "imager");
  static {
    WORKING_FOLDER.mkdirs();
  }

  static {

    // System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
    // System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
    // System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "trace");

    System.setProperty("logs.folder", new File(WORKING_FOLDER, "logs").getAbsolutePath());

    UIUtils.setSystemLookAndFeel();
    UIResources.setResourcesBaseClass(JaTooImager.class);
  }

  private static final Log logger = LogFactory.getLog(JaTooImager.class);

  public static void main(String[] args) {

    if (logger.isInfoEnabled()) {
      logger.info("args: " + Arrays.asList(args));
    }

    try {

      //
      // exit if another instance is already running

      File argsFolder = new File(WORKING_FOLDER, "args");
      argsFolder.mkdirs();

      FileLocker locker = new FileLocker(new File(WORKING_FOLDER, "lock.file"));
      if (locker.isLocked()) {
        logger.info("another instace already started... (check for lock file)");
        return;
      }

      locker.lock();

      //
      //

      if (args.length > 0) {
        new JaTooImager(new File(args[0]));
      }

      else if (new File("src/main/java").exists()) {
        // new JaTooImager(new File("D:\\Temp\\xxx\\re?eta dermatita seboreica.jpg"));
        new JaTooImager(new File("d:\\Temp\\xxx\\Translate Service Sequence Diagram.png"));
        // new JaTooImager(new File("D:\\Temp\\xxx\\20180114_185056.jpg"));
      }

      else {
        new JaTooImager();
      }

      // new FileLocker(new File(images, ".lock")).lock();

      for (File file : argsFolder.listFiles()) {
        file.delete();
      }

      //
      // uneori read file este prea rapid si fisierul este gol
      // celalalt proces nu a apucat sa termine de scris

      WatchService watcher = FileSystems.getDefault().newWatchService();
      Path dir = argsFolder.toPath();
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
      logger.error("failed to watch and wait for new images", e);
    }

    catch (Throwable e) {
      logger.fatal("unexpected exception", e);
    }
  }

  public static final JaTooImagerSettings SETTINGS = new JaTooImagerSettings(WORKING_FOLDER);

  public static final ImageCache IMAGE_MEMORY_CACHE = new ImageCacheMemory();

  public static final ImageCache IMAGE_PREVIEW_MEMORY_CACHE = new ImageCacheMemory();
  public static final ImageCache IMAGE_PREVIEW_FILE_CACHE = new ImageCacheFile(new File(WORKING_FOLDER, "cache"));

  private final ActionCloseWindow actionCloseWindow = new ActionCloseWindow(this);
  private final ActionCopyImageToClipboard actionCopyImageToClipboard = new ActionCopyImageToClipboard(this);
  private final ActionCopyVisibleImageToClipboard actionCopyViewportToClipboard = new ActionCopyVisibleImageToClipboard(this);
  private final ActionExit actionExit = new ActionExit(this);
  private final ActionPasteFromClipboard actionPasteFromClipboard = new ActionPasteFromClipboard(this);

  public final JaTooImagerViewer viewer = new JaTooImagerViewer();
  private final JaTooImagerButtons buttons = new JaTooImagerButtons(this);
  
//  public final JaTooImagerWindow window = new JaTooImagerWindow(this, WORKING_FOLDER);
  private final ImageLoader loader = new ImageLoader(this);
  
  private final List<File> images = new ArrayList<>();
  private int imagesIndex;


  public JaTooImager() {
    super(WORKING_FOLDER);

    //
    // init GUI

    JComponent contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout(0, 0));
    contentPane.add(viewer, BorderLayout.CENTER);
    contentPane.add(buttons, BorderLayout.SOUTH);

    //
    // more actions
    //TODO: to be replaced by JaTooImagerAction

    UIUtils.setActionForRightKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        showNext();
      }
    });
    UIUtils.setActionForLeftKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        showPrev();
      }
    });

    UIUtils.setActionForDeleteKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        delete();
      }
    });

    UIUtils.setActionForCtrlLeftKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        rotateLeft();
      }
    });
    UIUtils.setActionForCtrlRightKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        rotateRight();
      }
    });

    //
    // add drag and drop listener

    addDragAndDropListener(new DragAndDropListener() {

      @Override
      public void onDrop(Transferable transferable) {
        try {
          handleDataTransfer(transferable);
        } catch (UnsupportedFlavorException | IOException e) {
          showMessageError(UIResources.getText("dnd.error.title"), e.getMessage());
          logger.error("failed to get and process the dragged data", e);
        }
      }
    });

    //
    // intercept window closing effect and use close window action

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closeWindow();
      }
    });

    //
    // shows the application window

    setVisible(true);
    contentPane.setFocusable(true);
    contentPane.requestFocusInWindow();
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

      if (images.size() > 0) {

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
  }

  //
  // ---
  //

  public ActionCloseWindow getActionCloseWindow() {
    return actionCloseWindow;
  }

  public ActionCopyImageToClipboard getActionCopyImageToClipboard() {
    return actionCopyImageToClipboard;
  }

  public ActionCopyVisibleImageToClipboard getActionCopyViewportToClipboard() {
    return actionCopyViewportToClipboard;
  }

  public ActionExit getActionExit() {
    return actionExit;
  }

  public ActionPasteFromClipboard getActionPasteFromClipboard() {
    return actionPasteFromClipboard;
  }

  //
  // ---
  //

  public synchronized void copyImageToClipboard() {

    BufferedImage image = viewer.viewer.getImage();

    int clipboardWidth = UIUtils.getSmallestScreenWidth();
    int clipboardHeight = UIUtils.getSmallestScreenHeight();

    if (image != null && image.getWidth() > clipboardWidth || image.getHeight() > clipboardHeight) {
      image = ImageUtils.resizeToFit(image, clipboardWidth, clipboardHeight);
    }

    ImageUtils.copyToClipboard(image);
  }

  public synchronized void copyVisibleImageToClipboard() {

    ImageCanvas canvas = viewer.viewer.getCanvas();
    Rectangle visibleImageBounds = canvas.getImageBounds().intersection(canvas.getVisibleRect());

    BufferedImage visibleImage = ImageUtils.create(visibleImageBounds.width, visibleImageBounds.height, true);
    Graphics2D g = visibleImage.createGraphics();
    g.translate(-visibleImageBounds.x, -visibleImageBounds.y);
    canvas.paint(g);
    g.dispose();

    ImageUtils.copyToClipboard(visibleImage);
  }

  @SuppressWarnings("unchecked")
  public synchronized void handleDataTransfer(final Transferable transferable) throws UnsupportedFlavorException, IOException {

    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      setImages((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
    }

    else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
      System.out.println(transferable.getTransferData(DataFlavor.imageFlavor));
    }

    else {
      throw new UnsupportedFlavorException(transferable.getTransferDataFlavors()[0]);
    }
  }

  //
  // ---
  //
  
  @Override
  public void onStartLoading(File file) {

    setTitle(file.getName() + " (" + UIResources.getText("title.text.loading") + ")");
    viewer.showLoader();

    BufferedImage preview = JaTooImager.IMAGE_PREVIEW_MEMORY_CACHE.get(file);
    if (preview == null) {
      preview = JaTooImager.IMAGE_PREVIEW_FILE_CACHE.get(file);
    }
    if (preview != null) {
      viewer.showImagePreview(preview);
      JaTooImager.IMAGE_PREVIEW_MEMORY_CACHE.add(preview, file);
    }
  }
  
  @Override
  public void onImageLoaded(File file, BufferedImage image) {

    final BufferedImage imageToShow;

    if (JaTooImager.SETTINGS.isAutoRotateOnLoadAccordingToEXIF()) {

      switch (ImageMetadataHandler.getInstance().getOrientation(file)) {

        case 3:
          imageToShow = ImageUtils.rotate(image, 180, Color.BLACK);
          break;
        case 6:
          imageToShow = ImageUtils.rotate(image, 90, Color.BLACK);
          break;
        case 8:
          imageToShow = ImageUtils.rotate(image, 270, Color.BLACK);
          break;

        default:
          imageToShow = image;
          break;
      }
    }

    else {
      imageToShow = image;
    }

    setTitle(file, imageToShow);

    //
    // show image (with or without info)

    System.out.println(JaTooImager.SETTINGS.isShowInfo());

//    if (JaTooImager.SETTINGS.isShowInfo()) {
//
//      ImageMetadata metadata = ImageMetadataHandler.getInstance().getMetadata(file);
//      String dateTaken = metadata.getDateTaken() == null ? null : new SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm:ss").format(metadata.getDateTaken());
//
//      showImage(imageToShow, dateTaken, metadata.getOrientationText());
//    }
//
//    else {
    viewer.showImage(imageToShow);
//    }

    //
    // add to the cache(s)

    JaTooImager.IMAGE_MEMORY_CACHE.add(imageToShow, file);

    if (!JaTooImager.IMAGE_PREVIEW_FILE_CACHE.contains(file)) {

      int max = Math.max(imageToShow.getWidth(), imageToShow.getHeight());
      int min = Math.max(UIUtils.getBiggestScreenWidth(), UIUtils.getBiggestScreenHeight());

      if (max > min) {
        new Thread() {
          public void run() {
            BufferedImage preview = ImageUtils.resizeToFit(ImageUtils.resizeToFit(imageToShow, 200), Math.min(min, max));
            JaTooImager.IMAGE_PREVIEW_FILE_CACHE.add(preview, file);
            JaTooImager.IMAGE_PREVIEW_MEMORY_CACHE.add(preview, file);
          }
        }.start();
      }
    }
  }
  
  @Override
  public void onImageError(File file, Throwable t) {
    setTitle(file.getName() + " (" + UIResources.getText("title.text.loading.error") + ")");
    viewer.showError(file, t);
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

  // private final ImageCache cache = new ImageCacheMemory();

  private void showImage(final File file) {

    BufferedImage image = IMAGE_MEMORY_CACHE.get(file);

    if (image != null) {
      setTitle(file, image);
      viewer.showImage(image);
    }

    else {

      if (file != null) {
        loader.startLoading(file);
      }

      else {

        loader.stopLoading();

        setTitle(null);
        viewer.showImage(null);
      }
    }
  }

  private void setTitle(final File file, final String text) {
    if (text == null) {
      super.setTitle(file.getName());
    } else {
      super.setTitle(file.getName() + " (" + text + ")");
    }
  }
  
  public void setTitle(final File file, final BufferedImage image) {
    setTitle(file, image.getWidth() + "x" + image.getHeight());
  }

  public synchronized void reload() {
    loader.startReloading();
  }

  public synchronized void showNext() {

    if (images.size() == 0) {

      setTitle(null);
      viewer.showImage(null);

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

      setTitle(null);
      viewer.showImage(null);

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
      if (showConfirmationWarning(UIResources.getText("detele.warning.title"), UIResources.getText("delete.warning.message"))) {
        image.delete();
        logger.info("image (" + image + ") deleted (permanently)");
      }
    }

    showNext();
  }

  public synchronized void zoomIn() {
    viewer.viewer.zoomIn();
  }

  public synchronized void zoomOut() {
    viewer.viewer.zoomOut();
  }

  public synchronized void zoomToBestFit() {
    viewer.viewer.setBestFit();
  }

  public synchronized void zoomToRealSize() {
    viewer.viewer.setRealSize();
  }

  public synchronized void rotateLeft() {

    BufferedImage image = viewer.viewer.getImage();

    if (image != null) {
      viewer.showImage(ImageUtils.rotate(image, 270));
    }
  }

  public synchronized void rotateRight() {

    BufferedImage image = viewer.viewer.getImage();

    if (image != null) {
      viewer.showImage(ImageUtils.rotate(image, 90));
    }
  }

  public synchronized void closeWindow() {

    setVisible(false);
    saveProperties();
    dispose();

    loader.stopThread();

    System.gc();
  }

  public synchronized void exit() {
    closeWindow();
    SETTINGS.saveSilently();
    System.exit(0);
  }

  public synchronized void setShowInfo(boolean showInfo) {

//    JaTooImager.SETTINGS.setShowInfo(showInfo);
//
//    if (showInfo) {
//      loader.startReloading();
//    } else {
//      window.hideInfo();
//    }
  }


}
