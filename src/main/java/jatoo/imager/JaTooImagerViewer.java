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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import jatoo.image.ImageUtils;
import jatoo.ui.ImageViewer;
import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The "viewer" ({@link ImageViewer}) component.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 4.0, February 13, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerViewer extends JComponent {

  private final ImageViewer viewer;
  private final JLabel loader;
  private final JTextArea error;

  public JaTooImagerViewer() {

    viewer = new ImageViewer();

    float loaderImageFontSize = UIUtils.getSmallestScreenHeight() / 90f;
    BufferedImage loaderImage = ImageUtils.create(UIResources.getText("loader.text"), new JLabel().getFont().deriveFont(loaderImageFontSize), Color.WHITE);
    BufferedImage loaderImageWithShadow = ImageUtils.addShadow(loaderImage, 30, 1, 1, 1f, Color.BLACK);

    loader = new JLabel(new ImageIcon(loaderImageWithShadow));
    loader.setSize(loader.getPreferredSize());

    error = new JTextArea();
    error.setOpaque(false);
    error.setLineWrap(true);
    error.setFocusable(false);

    loader.setVisible(false);
    error.setVisible(false);

    setLayout(new TheLayoutManager());
    add(error);
    add(loader);
    add(viewer);
  }

  public void showLoader() {

    if (loader.isVisible()) {
      viewer.setImage(null);
      repaint();
    }

    loader.setVisible(true);
    error.setVisible(false);
  }

  public void showImage(final BufferedImage image) {
    viewer.setImage(image);
    loader.setVisible(false);
    error.setVisible(false);
  }

  public void showError(File file, Throwable t) {

    error.setText(file + System.lineSeparator() + String.valueOf(t));

    viewer.setImage(null);
    loader.setVisible(false);
    error.setVisible(true);
  }

  private class TheLayoutManager implements LayoutManager {

    @Override
    public void layoutContainer(final Container container) {
      synchronized (container.getTreeLock()) {

        Rectangle containerBounds = container.getBounds();

        viewer.setBounds(containerBounds);
        loader.setLocation((containerBounds.width - loader.getWidth()) / 2, (containerBounds.height - loader.getHeight()) / 2);
        error.setBounds(containerBounds);
      }
    }

    @Override
    public Dimension preferredLayoutSize(final Container container) {
      synchronized (container.getTreeLock()) {
        return new Dimension(getPreferredSize());
      }
    }

    @Override
    public Dimension minimumLayoutSize(final Container container) {
      synchronized (container.getTreeLock()) {
        return new Dimension(getMinimumSize());
      }
    }

    public void addLayoutComponent(final String name, final Component comp) {}

    public void removeLayoutComponent(final Component comp) {}
  }

  public void zoomIn() {
    viewer.zoomIn();
  }

  public void zoomOut() {
    viewer.zoomOut();
  }

  public void zoomToBestFit() {
    viewer.setBestFit();
  }

  public void zoomToRealSize() {
    viewer.setRealSize();
  }

}
