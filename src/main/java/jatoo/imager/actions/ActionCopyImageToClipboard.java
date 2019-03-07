package jatoo.imager.actions;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.KeyStroke;

import jatoo.image.ImageUtils;
import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;
import jatoo.ui.UIUtils;

@SuppressWarnings("serial")
public class ActionCopyImageToClipboard extends JaTooImagerAction {

  public ActionCopyImageToClipboard(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {

    BufferedImage image = imager.viewer.viewer.getImage();

    int clipboardWidth = UIUtils.getSmallestScreenWidth();
    int clipboardHeight = UIUtils.getSmallestScreenHeight();

    if (image != null && image.getWidth() > clipboardWidth || image.getHeight() > clipboardHeight) {
      image = ImageUtils.resizeToFit(image, clipboardWidth, clipboardHeight);
    }

    ImageUtils.copyToClipboard(image);
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
  }

}
