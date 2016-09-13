/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
