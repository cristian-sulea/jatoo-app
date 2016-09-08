/*
 * Copyright (C) 2014 Cristian Sulea ( http://cristian.sulea.net )
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
 * JaToo App Implementation
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 1.2 January 27, 2014
 */
@SuppressWarnings("serial")
public class JaTooAppImpl extends AbstractJaTooApp {

	public static final File WORKING_DIRECTORY = new File(JaTooApp.WORKING_DIRECTORY, ".test");

	@Override
	protected File getWorkingDirectory() {
		return WORKING_DIRECTORY;
	}

	@Override
	protected boolean isDialog() {
		return false;
	}

	@Override
	protected boolean isUndecorated() {
		return false;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
