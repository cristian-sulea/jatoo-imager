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

import java.io.File;

import jatoo.properties.FileProperties;

/**
 * The settings.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.0, March 7, 2018
 */
@SuppressWarnings("serial")
public class JaTooImagerSettings extends FileProperties {

  public JaTooImagerSettings(final File workingFolder) {
    super(new File(workingFolder, "settings.properties"));
    loadSilently();
  }

  private boolean autoRotateOnLoadAccordingToEXIF;
  private boolean showInfo;

  @Override
  protected void beforeSave() {
    super.beforeSave();

    setProperty("autoRotateOnLoadAccordingToEXIF", autoRotateOnLoadAccordingToEXIF);
    setProperty("showInfo", showInfo);
  }

  @Override
  protected void afterLoad() {
    super.afterLoad();

    autoRotateOnLoadAccordingToEXIF = getPropertyAsBoolean("autoRotateOnLoadAccordingToEXIF", true);
    showInfo = getPropertyAsBoolean("showInfo", true);
  }

  public boolean isAutoRotateOnLoadAccordingToEXIF() {
    return autoRotateOnLoadAccordingToEXIF;
  }

  public void setAutoRotateOnLoadAccordingToEXIF(boolean autoRotateOnLoadAccordingToEXIF) {
    this.autoRotateOnLoadAccordingToEXIF = autoRotateOnLoadAccordingToEXIF;
  }

  public boolean isShowInfo() {
    return showInfo;
  }

  public void setShowInfo(boolean showInfo) {
    this.showInfo = showInfo;
  }

}
