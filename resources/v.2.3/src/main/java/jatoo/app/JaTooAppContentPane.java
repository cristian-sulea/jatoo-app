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

import java.awt.Insets;

import javax.swing.JComponent;

import frameworks.spring.SpringComponent;

/**
 * JaToo Application Content Pane
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 5.0 January 28, 2014
 */
@SuppressWarnings("serial")
public abstract class JaTooAppContentPane extends SpringComponent {

	private final Insets marginsGlueGaps = new Insets(0, 0, 0, 0);

	public Insets getMarginsGlueGaps() {
		return marginsGlueGaps;
	}

	@Override
	protected final void initComponent() throws Throwable {
		initContentPane();
	}

	protected abstract void initContentPane() throws Throwable;

	protected JComponent getDragComponent() {
		return this;
	}

}
