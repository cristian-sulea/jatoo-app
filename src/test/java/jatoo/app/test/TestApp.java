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

package jatoo.app.test;

import java.awt.Insets;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.UIManager;

import jatoo.app.App;

/**
 * Test App with {@link #main(String[])} method.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 3.0, September 13, 2016
 */
public class TestApp extends App {

  static final File WORKING_DIRECTORY = new File("target");
  static final String LOOK_AND_FEEL = UIManager.getSystemLookAndFeelClassName();

  public static void main(String[] args) throws Throwable {
    UIManager.setLookAndFeel(LOOK_AND_FEEL);
    new TestApp();
  }

  public TestApp() {
    super("Test App");
  }

  @Override
  protected void init() {

  }

  @Override
  protected JComponent getContentPane() {
    return new TestAppContentPane(this);
  }

  @Override
  protected final File getWorkingDirectory() {
    return WORKING_DIRECTORY;
  }

  @Override
  protected final boolean isDialog() {
    return false;
  }

  @Override
  protected final boolean isUndecorated() {
    return false;
  }

  @Override
  protected final boolean isResizable() {
    return true;
  }

  @Override
  protected boolean isSizePersistent() {
    return false;
  }

  @Override
  protected final boolean hasTrayIcon() {
    return false;
  }

  @Override
  protected final boolean isGlueToMarginsOnCtrlArrows() {
    return false;
  }

  @Override
  protected final Insets getMarginsGlueGaps() {
    return null;
  }

  @Override
  protected final JComponent getDragToMoveComponent() {
    return null;
  }

  @Override
  protected final void afterInit() {}

}
