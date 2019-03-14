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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jatoo.ui.UIResources;

/**
 * The "buttons bar" component.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.3, March 7, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerButtons extends JComponent implements ActionListener {
  private static final Log logger = LogFactory.getLog(JaTooImagerButtons.class);

  private final JaTooImager imager;

  private JButton zoomIn;
  private JButton zoomOut;

  private JButton zoomToBestFit;
  private JButton zoomToRealSize;

  private JButton showPrev;
  private JButton showNext;

  private JButton info;

  private JButton rotateLeft;
  private JButton rotateRight;

  private JButton resize;

  private JButton delete;

  private JButton tools;

  public JaTooImagerButtons(final JaTooImager imager) {
    this.imager = imager;

    zoomIn = createButton(UIResources.getImageIcon("zoomIn-16.png"), UIResources.getText("zoomIn.button.toolTipText"));
    zoomOut = createButton(UIResources.getImageIcon("zoomOut-16.png"), UIResources.getText("zoomOut.button.toolTipText"));

    zoomToBestFit = createButton(UIResources.getImageIcon("zoomToBestFit-16.png"), UIResources.getText("zoomToBestFit.button.toolTipText"));
    zoomToRealSize = createButton(UIResources.getImageIcon("zoomToRealSize-16.png"), UIResources.getText("zoomToRealSize.button.toolTipText"));

    showPrev = createButton(UIResources.getImageIcon("showPrev.png"), UIResources.getText("showPrev.button.toolTipText"));
    showNext = createButton(UIResources.getImageIcon("showNext.png"), UIResources.getText("showNext.button.toolTipText"));

    info = createButton(UIResources.getImageIcon("info-16.png"), UIResources.getText("info.button.toolTipText"));
    // info.setEnabled(false);

    rotateLeft = createButton(UIResources.getImageIcon("rotateLeft-16.png"), UIResources.getText("rotateLeft.button.toolTipText"));
    rotateRight = createButton(UIResources.getImageIcon("rotateRight-16.png"), UIResources.getText("rotateRight.button.toolTipText"));

    resize = createButton(UIResources.getImageIcon("resize-16.png"), UIResources.getText("resize.button.toolTipText"));
    resize.setEnabled(false);

    delete = createButton(UIResources.getImageIcon("delete-16.png"), UIResources.getText("delete.button.toolTipText"));

    tools = createButton(UIResources.getImageIcon("tools-16.png"), UIResources.getText("buttons.tools.toolTipText"));
    tools.setEnabled(false);

    JToolBar barL = new JToolBar();
    barL.setBorder(null);
    barL.setFloatable(false);

    barL.add(zoomIn);
    barL.add(zoomOut);
    barL.addSeparator();
    barL.add(zoomToBestFit);
    barL.add(zoomToRealSize);
    barL.addSeparator();
    barL.add(info);

    JToolBar barC = new JToolBar();
    barC.setBorder(null);
    barC.setFloatable(false);

    barC.addSeparator();
    barC.add(showPrev);
    barC.add(showNext);
    barC.addSeparator();

    JToolBar barR = new JToolBar();
    barR.setBorder(null);
    barR.setFloatable(false);

    barR.add(rotateLeft);
    barR.add(rotateRight);
    barR.addSeparator();
    barR.add(resize);
    barR.addSeparator();
    barR.add(delete);

    barR.addSeparator();
    barR.add(tools);
    barR.add(createButtonFromAction(imager.getActionCopyImageToClipboard()));

    JToolBar barExit = new JToolBar();
    barExit.setBorder(null);
    barExit.setFloatable(false);
    barExit.add(createButtonFromAction(imager.getActionExit()));

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    buttons.add(barL);
    buttons.add(barC);
    buttons.add(barR);

    setLayout(new BorderLayout());
    add(buttons, BorderLayout.CENTER);
    add(barExit, BorderLayout.EAST);
  }

  private JButton createButtonFromAction(JaTooImagerAction action) {
    JButton button = new JButton(action);
    // button.setHideActionText(true);

    if (button.getIcon() != null) {
      button.setText(null);
    }

    else {

      logger.error("no icon for action " + action.getClass().getName());

      if (button.getText() == null) {
        button.setText("#" + action.getClass().getSimpleName());
      }
    }

    button.setFocusable(false);
    return button;
  }

  private JButton createButton(Icon icon, String toolTipText) {
    JButton button = new JButton(icon);
    initButton(button, toolTipText);
    return button;
  }

  // private JToggleButton createToggleButton(Icon icon, String toolTipText) {
  // JToggleButton button = new JToggleButton(icon);
  // initButton(button, toolTipText);
  // return button;
  // }

  private void initButton(AbstractButton button, String toolTipText) {

    if (toolTipText != null) {
      button.setToolTipText(toolTipText);
    }

    button.setFocusable(false);
    button.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();

    if (source == zoomIn) {
      imager.zoomIn();
    } else if (source == zoomOut) {
      imager.zoomOut();
    }

    else if (source == zoomToBestFit) {
      imager.zoomToBestFit();
    } else if (source == zoomToRealSize) {
      imager.zoomToRealSize();
    }

    else if (source == info) {
      imager.setShowInfo(!JaTooImager.SETTINGS.isShowInfo());
    }

    else if (source == showPrev) {
      imager.showPrev();
    } else if (source == showNext) {
      imager.showNext();
    }

    else if (source == rotateLeft) {
      imager.rotateLeft();
    } else if (source == rotateRight) {
      imager.rotateRight();
    }

    else if (source == resize) {

    }

    else if (source == delete) {
      imager.delete();
    }

    else if (source == tools) {

    }
  }

}
