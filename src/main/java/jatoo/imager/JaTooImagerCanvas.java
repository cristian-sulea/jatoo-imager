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
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jatoo.image.ImageUtils;
import jatoo.ui.ImageCanvas;
import jatoo.ui.UITheme2;
import jatoo.ui.UIUtils;

@SuppressWarnings("serial")
public class JaTooImagerCanvas extends ImageCanvas {

  private JComponent loader;

  public JaTooImagerCanvas() {

    setLayout(new JaTooImagerCanvasLayout());

    float loaderImageFontSize = UIUtils.getSmallestScreenHeight() / 90f;
    BufferedImage loaderImage = ImageUtils.create(UITheme2.getText(getClass(), "loader.text"), new JLabel().getFont().deriveFont(loaderImageFontSize), Color.WHITE);
    BufferedImage loaderImageWithShadow = ImageUtils.addShadow(loaderImage, 30, 1, 1, 1f, Color.BLACK);

    loader = new JLabel(new ImageIcon(loaderImageWithShadow));
    loader.setSize(loader.getPreferredSize());

    hideLoader();

    add(loader);
  }

  // @Override
  // protected void paintImage(Graphics2D g, BufferedImage image, int canvasWidth, int canvasHeight) {
  //
  // BufferedImage imageDrawing = image;
  //
  // imageDrawing = ImageUtils.resizeTo(true, imageDrawing, canvasWidth - 14, canvasHeight - 12, true);
  // imageDrawing = ImageUtils.addShadow(imageDrawing);
  //
  // int x = (canvasWidth - imageDrawing.getWidth()) / 2;
  // int y = (canvasHeight - imageDrawing.getHeight()) / 2;
  //
  // g.drawImage(imageDrawing, x, y, null);
  // }

  @Override
  protected void paintImage(Graphics2D g, BufferedImage image, int x, int y, int width, int height) {
    super.paintImage(g, image, x, y, width, height);

    g.setColor(Color.BLACK);
    g.drawRect(x, y, width - 1, height - 1);
  }

  public void showLoader() {
    loader.setVisible(true);
  }

  public void hideLoader() {
    loader.setVisible(false);
  }

  private class JaTooImagerCanvasLayout implements LayoutManager {

    @Override
    public void layoutContainer(final Container container) {
      synchronized (container.getTreeLock()) {
        loader.setLocation((container.getWidth() - loader.getWidth()) / 2, (container.getHeight() - loader.getHeight()) / 2);
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
