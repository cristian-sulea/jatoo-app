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

package jatoo.app;

import java.io.File;

/**
 * JaToo Application Initializer
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.0, May 20, 2014
 */
public interface AppInitializer {

	File getWorkingDirectory();

	AppContentPane getContentPane();

	boolean isDialog();

	boolean isUndecorated();

	boolean isResizable();

}
