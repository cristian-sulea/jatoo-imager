package jatoo.imager.actions;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import jatoo.image.ImageUtils;
import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;

@SuppressWarnings("serial")
public class ActionCopyViewportToClipboard extends JaTooImagerAction {

  public ActionCopyViewportToClipboard(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {

    JComponent viewport = imager.viewer.viewer.getViewport();

    BufferedImage image = ImageUtils.create(viewport.getWidth(), viewport.getHeight(), true);
    Graphics2D g = image.createGraphics();
    viewport.paint(g);
    g.dispose();

    ImageUtils.copyToClipboard(image);
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK);
  }

}
