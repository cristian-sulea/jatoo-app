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
