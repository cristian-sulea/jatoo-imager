package jatoo.imager.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;

@SuppressWarnings("serial")
public class ActionCopyImageToClipboard extends JaTooImagerAction {

  public ActionCopyImageToClipboard(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {
    imager.copyImageToClipboard();
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
  }

}
