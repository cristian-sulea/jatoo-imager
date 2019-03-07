package jatoo.imager.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jatoo.imager.JaTooImager;
import jatoo.imager.JaTooImagerAction;

@SuppressWarnings("serial")
public class ActionPasteFromClipboard extends JaTooImagerAction {
  private static final Log logger = LogFactory.getLog(JaTooImager.class);

  public ActionPasteFromClipboard(JaTooImager imager) {
    super(imager);

    imager.addDropTargetListener(new DropTargetAdapter() {

      @Override
      public void drop(DropTargetDropEvent event) {

        event.acceptDrop(DnDConstants.ACTION_COPY);
        Transferable transferable = event.getTransferable();

        actionPerformed(imager, transferable);
      }
    });
  }

  @Override
  public void actionPerformed(final JaTooImager imager) {
    actionPerformed(imager, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));
  }

  @SuppressWarnings("unchecked")
  public void actionPerformed(final JaTooImager imager, final Transferable transferable) {

    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

      try {
        imager.setImages((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
      }

      catch (UnsupportedFlavorException | IOException e) {
        logger.error("failed to get the dragged data", e);
      }
    }

    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      try {
        System.out.println(transferable.getTransferData(DataFlavor.javaFileListFlavor));
      } catch (UnsupportedFlavorException | IOException e) {
        e.printStackTrace();
      }
    }

    else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
      try {
        System.out.println(transferable.getTransferData(DataFlavor.imageFlavor));
      } catch (UnsupportedFlavorException | IOException e) {
        e.printStackTrace();
      }
    }

    else {
      System.out.println(transferable);
      try {
        System.out.println(transferable.getTransferData(DataFlavor.stringFlavor));
      } catch (UnsupportedFlavorException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
  }
}
