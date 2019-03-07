package jatoo.imager.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;

@SuppressWarnings("serial")
public class ActionCloseWindow extends JaTooImagerAction {

  public ActionCloseWindow(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {
    imager.closeWindow();
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  }

  @Override
  protected KeyStroke[] getExtraKeyStrokes() {
    return new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK) };
  }

}
