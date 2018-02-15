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
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

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
 * @version 5.1, February 14, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerWindow extends AppWindowFrame implements ImageLoaderListener {

  private final JaTooImagerViewer viewer;
  private final JaTooImagerButtons buttons;

  public JaTooImagerWindow(final JaTooImager imager) {

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

  public BufferedImage getImage() {
    return image;
  }

  @Override
  public void onStartLoading(final File file) {
    setTitle(file.getName() + " (" + UIResources.getText("title.text.loading") + ")");
    viewer.showLoader();
  }

  @Override
  public void onImageLoaded(final File file, final BufferedImage image) {

    final BufferedImage imageRotated;

    switch (ImageMetadataHandler.getInstance().getOrientation(file)) {

//      case 3:
//        imageRotated = ImageUtils.rotate(image, 180);
//        break;
      case 6:
        imageRotated = ImageUtils.rotate(image, 90);
        break;
//      case 8:
//        imageRotated = ImageUtils.rotate(image, 270);
//        break;

      default:
        imageRotated = image;
        break;
    }

    setTitle(file.getName() + " (" + image.getWidth() + "x" + image.getHeight() + ")");
    showImage(imageRotated);
  }

  @Override
  public void onImageError(final File file, final Throwable t) {
    setTitle(file.getName() + " (" + UIResources.getText("title.text.loading.error") + ")");
    viewer.showError(file, t);
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
