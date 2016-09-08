/*
 * Copyright (C) 2014 Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.app.test;

import jatoo.app.AppContentPane;
import jatoo.app.AppInitializer;

import java.io.File;

/**
 * Test App Initializer
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 1.1, May 21, 2014
 */
public class TestAppInitializer implements AppInitializer {

	@Override
	public File getWorkingDirectory() {
		return new File("target");
	}

	@Override
	public boolean isDialog() {
		return false;
	}

	@Override
	public boolean isUndecorated() {
		return false;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public AppContentPane getContentPane() {
		return new TestAppContentPane();
	}

}
