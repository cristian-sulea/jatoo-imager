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
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import jatoo.image.ImageMetadata;
import jatoo.image.ImageMetadataHandler;
import jatoo.image.ImageUtils;
import jatoo.ui.AppWindowFrame;
import jatoo.ui.ImageLoaderListener;
import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The window.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 5.4, March 15, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerWindow extends AppWindowFrame implements ImageLoaderListener {

  private final JaTooImagerViewer viewer;
  private final JaTooImagerButtons buttons;

  public JaTooImagerWindow(final JaTooImager imager, final File workingFolder) {
    super(workingFolder);

    //
    // canvas & loader

    viewer = new JaTooImagerViewer();
    buttons = new JaTooImagerButtons(imager);

    //
    // content pane

    JPanel contentPane = new JPanel(new BorderLayout(0, 0));
    contentPane.add(viewer, BorderLayout.CENTER);
    contentPane.add(buttons, BorderLayout.SOUTH);

    setContentPane(contentPane);
    setVisible(true);

    contentPane.setFocusable(true);
    contentPane.requestFocusInWindow();

    //
    // actions

    UIUtils.setActionForRightKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        imager.showNext();
      }
    });
    UIUtils.setActionForLeftKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        imager.showPrev();
      }
    });

    UIUtils.setActionForDeleteKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        imager.delete();
      }
    });

    UIUtils.setActionForCtrlLeftKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        imager.rotateLeft();
      }
    });
    UIUtils.setActionForCtrlRightKeyStroke(contentPane, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        imager.rotateRight();
      }
    });
  }

  private BufferedImage image;

  public void showImage(final BufferedImage image) {
    viewer.showImage(this.image = image);
  }

  public void showImage(final BufferedImage image, final String dateTaken, final String orientation) {
    viewer.showImage(this.image = image, dateTaken, orientation);
  }

  public BufferedImage getImage() {
    return image;
  }

  public void hideInfo() {
    viewer.hideInfo();
  }

  @Override
  public void onStartLoading(final File file) {

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
  public void onImageLoaded(final File file, final BufferedImage image) {

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

    if (JaTooImager.SETTINGS.isShowInfo()) {

      ImageMetadata metadata = ImageMetadataHandler.getInstance().getMetadata(file);
      String dateTaken = metadata.getDateTaken() == null ? null : new SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm:ss").format(metadata.getDateTaken());

      showImage(imageToShow, dateTaken, metadata.getOrientationText());
    }

    else {
      showImage(imageToShow);
    }

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
  public void onImageError(final File file, final Throwable t) {
    setTitle(file.getName() + " (" + UIResources.getText("title.text.loading.error") + ")");
    viewer.showError(file, t);
  }

  public void setTitle(final File file, final BufferedImage image) {
    super.setTitle(file.getName() + " (" + image.getWidth() + "x" + image.getHeight() + ")");
  }

  public void zoomIn() {
    viewer.zoomIn();
  }

  public void zoomOut() {
    viewer.zoomOut();
  }

  public void zoomToBestFit() {
    viewer.zoomToBestFit();
  }

  public void zoomToRealSize() {
    viewer.zoomToRealSize();
  }

}
