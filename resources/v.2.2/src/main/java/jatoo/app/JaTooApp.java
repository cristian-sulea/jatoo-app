/*
 * Copyright (C) 2013 Cristian Sulea ( http://cristian.sulea.net )
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

import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;

import org.apache.commons.logging.Log;

/**
 * JaToo Application
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 7.1 February 20, 2014
 */
public interface JaTooApp {

	File WORKING_DIRECTORY = new File(new File(System.getProperty("user.home")), ".jatoo");

	Log getLogger();
	
	Window getWindow();

	void show();

	void hide();

	void sendToBack();

	void sendToFront();

	void setAlwaysOnTop(boolean alwaysOnTop);

	boolean isAlwaysOnTop();

	void setHideWhenMinimized(boolean hideWhenMinimized);

	boolean isHideWhenMinimized();

	void setTransparency(int transparency);

	int getTransparency();

	/**
	 * Displays a message.
	 * 
	 * @param message
	 *          the message to display
	 */
	void showMessage(String message);

	/**
	 * Displays an error message.
	 * 
	 * @param message
	 *          the message to display
	 */
	void showErrorMessage(String message);

	/**
	 * Displays a warning message.
	 * 
	 * @param message
	 *          the message to display
	 */
	void showWarningMessage(String message);

	/**
	 * Displays an information message.
	 * 
	 * @param message
	 *          the message to display
	 */
	void showInformationMessage(String message);

	/**
	 * Displays a message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	void showTrayMessage(String title, String message);

	void showTrayMessage(String title, String message, ActionListener actionListener);

	/**
	 * Displays an error message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	void showTrayErrorMessage(String title, String message);

	/**
	 * Displays a warning message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	void showTrayWarningMessage(String title, String message);

	/**
	 * Displays an information message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	void showTrayInformationMessage(String title, String message);

	/**
	 * Provides a simple mechanism for the user to select a file.
	 * 
	 * @param currentDirectory
	 *          the current directory to point to
	 */
	File selectFile(File currentDirectory);

	/**
	 * Provides a simple mechanism for the user to select a directory.
	 * 
	 * @param currentDirectory
	 *          the current directory to point to
	 */
	File selectDirectory(File currentDirectory);

	/**
	 * Provides a simple mechanism for the user to select a file or a directory.
	 * 
	 * @param currentDirectory
	 *          the current directory to point to
	 */
	File selectFileOrDirectory(File currentDirectory);

}
