package jatoo.imager.actions;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;
import jatoo.ui.UIResources;

@SuppressWarnings("serial")
public class ActionExit extends JaTooImagerAction {

  public ActionExit(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {
    imager.exit();
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
  }

  @Override
  protected KeyStroke[] getExtraKeyStrokes() {
    return new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK) };
  }

  @Override
  protected String getText() {
    return UIResources.getText("exit.text");
  }

  @Override
  protected Icon getSmallIcon() {
    return UIResources.getImageIcon("exit-16.png");
  }

  @Override
  protected Icon getLargeIcon() {
    return UIResources.getImageIcon("exit-32.png");
  }

  @Override
  protected String getToolTipText() {
    return UIResources.getText("exit.toolTipText");
  }

}
