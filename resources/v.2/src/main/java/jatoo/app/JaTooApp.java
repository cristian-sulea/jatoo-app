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

import java.io.File;

/**
 * JaToo Application
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.0 December 6, 2013
 */
public interface JaTooApp<PROPERTIES extends JaTooAppProperties> {

	File JATOO_APP_DIRECTORY = new File(new File(new File(System.getProperty("user.home")), ".jatoo"), ".app");

	String getName();

	PROPERTIES getProperties();

	Class<? extends JaTooAppUIComponent> getUIComponentClass();

	void setUIComponentClass(Class<? extends JaTooAppUIComponent> contentPaneClass);

	void open();

	boolean isOpened();

	void close();

	boolean isClosed();

	boolean isExitOnClose();

	void sendToBack();

	void sendToFront();

	void setAlwaysOnTop(boolean alwaysOnTop);

	boolean isAlwaysOnTop();

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
