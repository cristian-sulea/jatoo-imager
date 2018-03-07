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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import jatoo.image.ImageUtils;
import jatoo.ui.ImageViewer;
import jatoo.ui.UIResources;

/**
 * The "viewer" ({@link ImageViewer}) component.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 4.1, March 2, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerViewer extends JComponent {

  private static final Font FONT_LABEL = new JLabel().getFont();

  private static final Color COLOR_LABEL_TEXT = Color.WHITE;
  private static final Color COLOR_LABEL_SHADOW = Color.BLACK;

  private static final BufferedImage IMAGE_LABEL_LOADER;

  private static final BufferedImage IMAGE_LABEL_NO_TEXT;
  private static final BufferedImage IMAGE_LABEL_DATE_TAKEN;
  private static final BufferedImage IMAGE_LABEL_ORIENTATION;

  static {

    IMAGE_LABEL_LOADER = ImageUtils.addShadow(ImageUtils.create(UIResources.getText("loader.text"), FONT_LABEL, COLOR_LABEL_TEXT), 30, 1, 1, 1f, COLOR_LABEL_SHADOW);

    IMAGE_LABEL_NO_TEXT = createImageForLabel("-");
    IMAGE_LABEL_DATE_TAKEN = createImageForLabel("Date taken:");
    IMAGE_LABEL_ORIENTATION = createImageForLabel("Orientation:");
  }

  private static BufferedImage createImageForLabel(String text) {
    return ImageUtils.addShadow(ImageUtils.create(text, FONT_LABEL, COLOR_LABEL_TEXT), 30, 1, 1, 1f, COLOR_LABEL_SHADOW);
  }

  private final JLabel loader;
  private final ImageViewer viewer;

  private final JTextArea error;

  private final JComponent info;
  private final JLabel infoDateTaken;
  private final JLabel infoOrientation;

  public JaTooImagerViewer() {

    loader = new JLabel(new ImageIcon(IMAGE_LABEL_LOADER));
    loader.setSize(loader.getPreferredSize());

    viewer = new ImageViewer();

    error = new JTextArea();
    error.setOpaque(false);
    error.setLineWrap(true);
    error.setFocusable(false);

    info = new JComponent() {

      @Override
      protected void paintComponent(Graphics g) {

        final int width = getWidth();
        final int height = getHeight();

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        g2d.setColor(COLOR_LABEL_SHADOW);
        g2d.fillRect(0, 0, width, height);

        super.paintComponent(g);
      }
    };

    JComponent info1 = new JComponent() {};
    info1.setLayout(new GridLayout(2, 1));
    info1.add(new JLabel(new ImageIcon(IMAGE_LABEL_DATE_TAKEN), JLabel.LEFT));
    info1.add(new JLabel(new ImageIcon(IMAGE_LABEL_ORIENTATION), JLabel.LEFT));

    JComponent info2 = new JComponent() {};
    info2.setLayout(new GridLayout(2, 1));
    info2.add(infoDateTaken = new JLabel(new ImageIcon(IMAGE_LABEL_NO_TEXT), JLabel.LEFT));
    info2.add(infoOrientation = new JLabel(new ImageIcon(IMAGE_LABEL_NO_TEXT), JLabel.LEFT));

    info.setLayout(new BorderLayout(5, 5));
    info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    info.add(info1, BorderLayout.WEST);
    info.add(info2, BorderLayout.CENTER);

    loader.setVisible(false);
    error.setVisible(false);
    info.setVisible(false);

    setLayout(new TheLayoutManager());

    add(error);
    add(loader);
    add(info);
    add(viewer);
  }

  public void showLoader() {

    if (loader.isVisible()) {
      viewer.setImage(null);
      // repaint();
    }

    loader.setVisible(true);
    error.setVisible(false);

    repaint();
  }

  public void showImage(final BufferedImage image) {

    error.setVisible(false);
    viewer.setImage(image);
    info.setVisible(false);
    loader.setVisible(false);

    repaint();
  }

  public void showImage(final BufferedImage image, final String dateTaken, final String orientation) {

    error.setVisible(false);
    viewer.setImage(image);

    infoDateTaken.setIcon(new ImageIcon(dateTaken == null ? IMAGE_LABEL_NO_TEXT : createImageForLabel(dateTaken)));
    infoOrientation.setIcon(new ImageIcon(orientation == null ? IMAGE_LABEL_NO_TEXT : createImageForLabel(orientation)));

    info.setVisible(true);

    loader.setVisible(false);

    repaint();
  }

  public void hideInfo() {
    info.setVisible(false);
    repaint();
  }

  public void showError(File file, Throwable t) {

    error.setText(file + System.lineSeparator() + String.valueOf(t));

    viewer.setImage(null);
    info.setVisible(false);
    loader.setVisible(false);
    error.setVisible(true);

    repaint();
  }

  private class TheLayoutManager implements LayoutManager {

    @Override
    public void layoutContainer(final Container container) {
      synchronized (container.getTreeLock()) {

        Rectangle containerBounds = container.getBounds();

        viewer.setBounds(containerBounds);
        loader.setLocation((containerBounds.width - loader.getWidth()) / 2, (containerBounds.height - loader.getHeight()) / 2);
        error.setBounds(containerBounds);
        info.setBounds(new Rectangle(new Point(5, 5), info.getPreferredSize()));
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
