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

import jatoo.properties.FileProperties;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

/**
 * JaToo Application Properties
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 5.0 January 27, 2014
 */
@SuppressWarnings("serial")
public class JaTooAppProperties extends FileProperties {

	public JaTooAppProperties(File file) {
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

}
