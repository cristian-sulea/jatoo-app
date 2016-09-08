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

import jatoo.app.App;
import jatoo.app.AppContentPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * Test App Content Pane.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.1, May 21, 2014
 */
@SuppressWarnings("serial")
public class TestAppContentPane extends AppContentPane {

	@Override
	protected void init(final App app) {

		app.getLogger().debug("debug");
		app.getLogger().info("info");
		app.getLogger().warn("warn");
		app.getLogger().error("error");
		app.getLogger().fatal("fatal");

		JButton showTrayMessage = new JButton("Show Tray Message");
		showTrayMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showTrayMessage("1 ncerc sa configurez un system de test pentru Migros. POS", "1 mesPOS-ul face restart la linia din system.log:\n“lisysld: System Shutdown requested by 00000 Loc 011”sage");
			}
		});

		JButton showTrayInformationMessage = new JButton("Show Information Tray Message");
		showTrayInformationMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showTrayInformationMessage("22 ncerc sa configurez un system de test pentru Migros.", "22 mesPOS-ul face restart la .log:\n“lisysld: requested by 00000 Loc 011”sage");
			}
		});

		JButton showTrayWarningMessage = new JButton("Show Warning Tray Message");
		showTrayWarningMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showTrayWarningMessage("333 ncerc sa configurez un system de test pentru Migros. POS", "333 mesPOS-ul face restart la linia din system.log:\n“lisysld: System Shutdown requested by 00000 Loc 011”sage");
			}
		});

		JButton showTrayErrorMessage = new JButton("Show Error Tray Message");
		showTrayErrorMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showTrayErrorMessage("4444 ncerc sa configurez un system de test pentru Migros.", "4444 mesPOS-ul face restart la .log:\n“lisysld: requested by 00000 Loc 011”sage");
			}
		});

		add(showTrayMessage);
		add(showTrayInformationMessage);
		add(showTrayWarningMessage);
		add(showTrayErrorMessage);
	}

}
