package jatoo.imager.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;
import jatoo.ui.UIResources;

@SuppressWarnings("serial")
public class ActionPasteFromClipboard extends JaTooImagerAction {
  private static final Log logger = LogFactory.getLog(JaTooImager.class);

  public ActionPasteFromClipboard(JaTooImager imager) {
    super(imager);
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {
    try {
      imager.handleDataTransfer(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));
    } catch (UnsupportedFlavorException | IOException e) {
      imager.showMessageError(UIResources.getText("cnp.error.title"), e.getMessage());
      logger.error("failed to get the transferable (copy/paste) data", e);
    }
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
  }
}
