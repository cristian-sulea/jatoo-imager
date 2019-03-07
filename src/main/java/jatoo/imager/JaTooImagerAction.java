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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * This class provides the base for implementing various actions.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.1, 28 February, 2019
 */
@SuppressWarnings("serial")
public abstract class JaTooImagerAction extends AbstractAction {

  private final JaTooImager imager;

  public JaTooImagerAction(final JaTooImager imager) {
    this.imager = imager;
    init();
  }

  private boolean initialized = false;

  public synchronized void init() {

    if (initialized) {
      throw new IllegalStateException("already initialized");
    }

    //
    // key stroke triggers

    final KeyStroke keyStroke = getKeyStroke();
    final KeyStroke[] extraKeyStrokes = getExtraKeyStrokes();

    if (keyStroke != null || (extraKeyStrokes != null && extraKeyStrokes.length > 0)) {

      final JComponent imagerRootPane = imager.getRootPane();

      final Object actionMapKey = getClass().getName();
      imagerRootPane.getActionMap().put(actionMapKey, this);

      if (keyStroke != null) {
        imagerRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionMapKey);
      }

      if (extraKeyStrokes != null) {
        for (KeyStroke actionExtraKeyStroke : extraKeyStrokes) {
          imagerRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(actionExtraKeyStroke, actionMapKey);
        }
      }
    }

    //
    // GUI triggers

    final String text = getText();
    if (text != null) {
      putValue(Action.NAME, text);
    }

    final Icon smallIcon = getSmallIcon();
    if (smallIcon != null) {
      putValue(Action.SMALL_ICON, smallIcon);
    }

    final Icon largeIcon = getLargeIcon();
    if (largeIcon != null) {
      putValue(Action.LARGE_ICON_KEY, largeIcon);
    }

    final String toolTipText = getToolTipText();
    if (toolTipText != null) {
      putValue(Action.SHORT_DESCRIPTION, toolTipText);
    }

    //
    // done

    initialized = true;
  }

  protected KeyStroke getKeyStroke() {
    return null;
  }

  protected KeyStroke[] getExtraKeyStrokes() {
    return null;
  }

  protected String getText() {
    return null;
  }

  protected Icon getSmallIcon() {
    return null;
  }

  protected Icon getLargeIcon() {
    return null;
  }

  protected String getToolTipText() {
    return null;
  }

  @Override
  public final void actionPerformed(final ActionEvent e) {
    actionPerformed(imager);
  }

  public final void actionPerformed() {
    actionPerformed(imager);
  }

  protected abstract void actionPerformed(final JaTooImager imager);

}
