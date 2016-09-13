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

import javax.swing.UIManager;

/**
 * Test App with {@link #main(String[])} method.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.0, May 19, 2014
 */
public class TestApp {

  public static void main(String[] args) throws Throwable {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//    App.launch(false, TestAppContentPane.class);
  }

}
