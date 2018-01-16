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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import jatoo.image.ImageFileFilter;
import jatoo.ui.ImageLoaderV2;
import jatoo.ui.UIUtils;

@SuppressWarnings("serial")
public class JaTooImager extends JFrame {

  static {
    UIUtils.setSystemLookAndFeel();
  }

  public static void main(String[] args) {

    if (new File("src/main/java").exists()) {
      new JaTooImager();
    }

    else {
      if (args.length == 0) {
        new JaTooImager();
      } else {
        new JaTooImager(new File(args[0]));
      }
    }
  }

  private final JaTooImagerCanvas canvas;
  private final ImageLoaderV2 loader;

  private final List<File> images = new ArrayList<>();
  private int imagesIndex;

  public JaTooImager() {

    //
    // canvas & loader

    canvas = new JaTooImagerCanvas();
    loader = new ImageLoaderV2(canvas);

    canvas.setDropTargetListener(new JaTooImagerDropTargetListener(this));

    UIUtils.forwardDragAsMove(canvas, this);
    // UIUtils.disableDecorations(this);

    UIUtils.setActionForEscapeKeyStroke(canvas, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        JaTooImager.this.setVisible(false);
        JaTooImager.this.dispose();
      }
    });

    UIUtils.setActionForLeftKeyStroke(canvas, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        imagesIndex--;
        if (imagesIndex < 0) {
          imagesIndex = images.size() - 1;
        }

        showImage(images.get(imagesIndex));
      }
    });

    UIUtils.setActionForRightKeyStroke(canvas, new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        imagesIndex++;
        if (imagesIndex >= images.size()) {
          imagesIndex = 0;
        }

        showImage(images.get(imagesIndex));
      }
    });

    //
    // frame

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    UIUtils.centerWindowOnScreen(this, 25, 25);

    setIconImages(Arrays.asList(new ImageIcon(getClass().getResource("icon-016.png")).getImage(), new ImageIcon(getClass().getResource("icon-032.png")).getImage()));

    setContentPane(canvas);

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

  public void setImages(List<File> files) {

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
    setTitle(file.getName());
    loader.startLoading(file);
  }

}
