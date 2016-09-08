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

import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * JaToo Application Content Pane
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 9.1, May 21, 2014
 */
@SuppressWarnings("serial")
public abstract class AppContentPane extends JPanel {

	private final Insets marginsGlueGaps = new Insets(0, 0, 0, 0);

	public Insets getMarginsGlueGaps() {
		return marginsGlueGaps;
	}

	protected JComponent getDragComponent() {
		return this;
	}

	protected abstract void init(App app);

}
