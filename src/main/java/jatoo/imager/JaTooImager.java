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

import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import jatoo.image.ImageMemoryCache;
import jatoo.image.ImageThumbnails;
import jatoo.image.ImageUtils;
import jatoo.ui.ImageLoader;
import jatoo.ui.UIUtils;

@SuppressWarnings("serial")
public class JaTooImager extends JFrame {

  private static final int RESIZE_MOUSE_WHEEL_WIDTH_RATIO = (int) (UIUtils.getScreenWidth() * 0.01f);
  private static final int RESIZE_MOUSE_WHEEL_HEIGHT_RATIO = (int) (UIUtils.getScreenHeight() * 0.01f);

  static {
    UIUtils.setSystemLookAndFeel();
    // UITheme2.setTheme(JaTooImager.class);
  }

  public static void main(String[] args) throws Exception {
    // new JaTooImagerViewer(new File("src\\test\\resources\\image.jpg"));
    new JaTooImager(new File("d:\\OneDrive - PSS Prosoft Solutions\\Cristi - Personal\\Photos\\Share\\2013.10.21 - USA\\FB\\IMG_20131024_085105.jpg"));
  }

  private ImageMemoryCache imagesMemoryCache = new ImageMemoryCache();
  private ImageMemoryCache iconsMemoryCache = new ImageMemoryCache();

  private ImageThumbnails thumbnails = new ImageThumbnails();

  private List<File> files;
  private int filesIndex;

  private JaTooImagerCanvas canvas;

  private ImageLoader imageLoader;

  public JaTooImager(File file) throws Exception {

    //
    // files

    files = new ArrayList<>(Arrays.asList(file.getParentFile().listFiles(new FileFilter() {
      public boolean accept(File file) {
        return file.isFile();
      }
    })));
    filesIndex = files.indexOf(file);

    //
    // canvas

    canvas = new JaTooImagerCanvas();

    canvas.addMouseWheelListener(new ResizeMouseWheelListener());

    UIUtils.forwardDragAsMove(canvas, this);
    UIUtils.disableDecorations(JaTooImager.this);

    UIUtils.setActionForEscapeKeyStroke(canvas, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        JaTooImager.this.setVisible(false);
        JaTooImager.this.dispose();
      }
    });

    UIUtils.setActionForLeftKeyStroke(canvas, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        filesIndex--;
        if (filesIndex < 0) {
          filesIndex = files.size() - 1;
        }

        showImage(files.get(filesIndex));
      }
    });

    UIUtils.setActionForRightKeyStroke(canvas, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        synchronized (files) {

          filesIndex++;
          if (filesIndex >= files.size()) {
            filesIndex = 0;
          }

          showImage(files.get(filesIndex));
        }
      }
    });

    setContentPane(canvas);

    //
    // frame

    setTitle(file.getName());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    UIUtils.centerWindowOnScreen(this, 0, 25, 25);
    setVisible(true);

    //
    // show the image

    showImage(file);
  }

  private void showImage(final File file) {

    //
    // first of all check the image in the memory cache

    BufferedImage imageCached = imagesMemoryCache.get(file);

    if (imageCached != null) {

      if (imageLoader != null) {
        imageLoader.forceStop();
      }

      showImage(file, imageCached, true);

      return;
    }

    //
    // start a new image loader
    // but only after the previous one is stopped

    canvas.showLoader();

    if (imageLoader != null) {
      imageLoader.forceStop();
    }

    imageLoader = new ImageLoader(file) {

      @Override
      protected void onLoaderStart(File file, boolean isForceStop) {

        //
        // get the thumbnail, but only if there is one already created
        // will be created with the image already loaded

        BufferedImage thumbnail = thumbnails.get(file, 32, 32, false, true, ImageUtils.FORMAT.JPG);

        if (thumbnail != null) {
          showImage(file, thumbnail, false);
        } else {
          showImage(file, null, false);
        }
      }

      @Override
      protected void onImageLoaded(File file, BufferedImage image) {

        //
        // - show the image
        // - update the cache
        // - force the creation of the thumbnail

        showImage(file, image, false);

        imagesMemoryCache.put(file, image);
        thumbnails.get(file, image, 32, 32, true, true, ImageUtils.FORMAT.JPG);
      }

      @Override
      protected void onLoaderStop(File file, boolean isForceStop) {

        //
        // don't hide the loading indicator on a force stop
        // it means that a new loading operation started

        if (!isForceStop) {
          canvas.hideLoader();
        }
      }
    };
  }

  private void showImage(final File file, final BufferedImage image, final boolean hideLoader) {

    canvas.setImage(image);

    if (image == null) {
      setIconImage(null);
    }

    else {

      BufferedImage icon = iconsMemoryCache.get(file);

      if (icon == null) {
        icon = thumbnails.get(file, 32, 32, true, false, ImageUtils.FORMAT.JPG);
        iconsMemoryCache.put(file, icon);
      }

      setIconImage(icon);
    }

    if (hideLoader) {
      canvas.hideLoader();
    }
  }

  private class ResizeMouseWheelListener implements MouseWheelListener {

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

      final int widthRatio, heightRatio;

      if (e.getPreciseWheelRotation() > 0) {
        widthRatio = -RESIZE_MOUSE_WHEEL_WIDTH_RATIO;
        heightRatio = -RESIZE_MOUSE_WHEEL_HEIGHT_RATIO;
      }

      else {
        widthRatio = +RESIZE_MOUSE_WHEEL_WIDTH_RATIO;
        heightRatio = +RESIZE_MOUSE_WHEEL_HEIGHT_RATIO;
      }

      setSize(getWidth() + widthRatio, getHeight() + heightRatio);
    }
  }

}
