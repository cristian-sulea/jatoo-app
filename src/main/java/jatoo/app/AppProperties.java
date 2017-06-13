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

package jatoo.app;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import jatoo.properties.FileProperties;

/**
 * JaToo Application Properties
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 6.2, July 3, 2014
 */
@SuppressWarnings("serial")
public class AppProperties extends FileProperties {

  public AppProperties(File file) {
    super(file);
  }

  public Point getLocation(Point defaultLocation) {
    return getPropertyAsPoint("location", defaultLocation);
  }

  public void setLocation(Point location) {
    setProperty("location", location);
  }

  public Dimension getSize(Dimension defaultSize) {
    return getPropertyAsDimension("size", defaultSize);
  }

  public void setSize(Dimension size) {
    setProperty("size", size);
  }

  public boolean isVisible() {
    return getPropertyAsBoolean("visible", true);
  }

  public void setVisible(boolean visible) {
    setProperty("visible", visible);
  }

  public boolean isAlwaysOnTop() {
    return getPropertyAsBoolean("alwaysOnTop", false);
  }

  public void setAlwaysOnTop(boolean alwaysOnTop) {
    setProperty("alwaysOnTop", alwaysOnTop);
  }

  public boolean isHideWhenMinimized() {
    return getPropertyAsBoolean("hideWhenMinimized", false);
  }

  public void setHideWhenMinimized(boolean hideWhenMinimized) {
    setProperty("hideWhenMinimized", hideWhenMinimized);
  }

  public int getTransparency(int defaultTransparency) {
    return getPropertyAsInt("transparency", defaultTransparency);
  }

  public void setTransparency(int transparency) {
    setProperty("transparency", transparency);
  }

  public String getLastLookAndFeel(String defaultLastLookAndFeel) {
    return getPropertyAsString("lastLookAndFeel", defaultLastLookAndFeel);
  }

  public void setLastLookAndFeel(String lastLookAndFeel) {
    setProperty("lastLookAndFeel", lastLookAndFeel);
  }

  public boolean getLastUndecorated(boolean defaultUndecorated) {
    return getPropertyAsBoolean("lastUndecorated", defaultUndecorated);
  }

  public void setLastUndecorated(boolean lastUndecorated) {
    setProperty("lastUndecorated", lastUndecorated);
  }

}
