package jatoo.imager.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;

@SuppressWarnings("serial")
public class ActionCopyVisibleImageToClipboard extends JaTooImagerAction {

  public ActionCopyVisibleImageToClipboard(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {
    imager.copyVisibleImageToClipboard();
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK);
  }

}
