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
import jatoo.ui.ImageLoaderV2Listener;
import jatoo.ui.ImageViewerV4;
import jatoo.ui.UIResources;
import jatoo.ui.UIUtils;

/**
 * The "viewer" ({@link ImageViewerV4}) component.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 3.0, January 19, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerCanvas extends JComponent implements ImageLoaderV2Listener {

  private ImageViewerV4 viewer;
  private JLabel loader;
  private JTextArea error;

  public JaTooImagerCanvas() {

    viewer = new ImageViewerV4();

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

  @Override
  public void onStartLoading(File file) {
    viewer.setImage(null);
    loader.setVisible(true);
    error.setVisible(false);
  }

  @Override
  public void onImageLoaded(File file, BufferedImage image) {
    viewer.setImage(image);
    loader.setVisible(false);
    error.setVisible(false);
  }

  @Override
  public void onImageError(File file, Throwable t) {

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
}
