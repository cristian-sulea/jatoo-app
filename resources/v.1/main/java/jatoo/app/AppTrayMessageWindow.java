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

import jatoo.ui.UIUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.UIManager;

/**
 * Tray message window implementation for JaToo Application.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.0, May 20, 2014
 */
@SuppressWarnings("serial")
class AppTrayMessageWindow extends JWindow {

	enum MessageType {
		NONE,
		ERROR,
		WARNING,
		INFORMATION
	};

	private int closingCounter = 0;
	private boolean isClosed = false;

	public AppTrayMessageWindow(App app, JComponent message, MessageType messageType) {
		init(app, message, messageType);
	}

	public AppTrayMessageWindow(App app, String title, String message, MessageType messageType, final ActionListener actionListener) {

		JTextArea messageComponent = new JTextArea(message);
		messageComponent.setEditable(false);
		messageComponent.setOpaque(false);

		JScrollPane messageComponentScrollPane = new JScrollPane(messageComponent);
		messageComponentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messageComponentScrollPane.setBorder(BorderFactory.createEmptyBorder());

		JButton closeButton = new JButton();
		closeButton.setFocusable(false);
		closeButton.setMargin(new Insets(5, 5, 5, 5));
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeMessageWindow();
			}
		});
		try {
			closeButton.setIcon(new ImageIcon(getClass().getResource("icons/tray_message.close.png")));
		} catch (Throwable t1) {
			try {
				closeButton.setIcon(new ImageIcon(App.class.getResource("icons/tray_message.close.png")));
			} catch (Throwable t2) {
				closeButton.setText("x");
				app.getLogger().warn("failed to read popup close icon", t2);
			}
		}

		JLabel titleComponent = new JLabel(title);
		titleComponent.setFont(titleComponent.getFont().deriveFont(Font.BOLD));

		JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
		headerPanel.add(titleComponent, BorderLayout.CENTER);
		headerPanel.add(closeButton, BorderLayout.EAST);

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(messageComponentScrollPane, BorderLayout.CENTER);

		//
		// the listener for close on mouse press
		// and for action

		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				closeMessageWindow();

				if (actionListener != null) {
					actionListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), null, e.getWhen(), e.getModifiers()));
				}
			}
		};

		titleComponent.addMouseListener(mouseListener);
		messageComponent.addMouseListener(mouseListener);

		headerPanel.addMouseListener(mouseListener);
		panel.addMouseListener(mouseListener);

		//
		// init

		init(app, panel, messageType);
	}

	public AppTrayMessageWindow(App app, String title, String message, MessageType messageType) {
		this(app, title, message, messageType, null);
	}

	private void init(App app, JComponent message, MessageType messageType) {

		//
		// layout

		JPanel contentPane = new JPanel(new BorderLayout(5, 5));
		contentPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		contentPane.add(message, BorderLayout.CENTER);

		//
		// maybe there is a specific type

		Icon icon = getIconFromOptionPane(messageType);
		if (icon != null) {
			JLabel iconLabel = new JLabel(icon);
			iconLabel.setVerticalAlignment(JLabel.TOP);
			contentPane.add(iconLabel, BorderLayout.WEST);
		}

		//
		// configure

		setIconImages(app.getWindow().getIconImages());
		setIconImages(null);
		setContentPane(contentPane);

		pack();

		//
		// max width/height

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxWidth = screenSize.width / 3;
		int maxHeight = screenSize.height / 5;

		if (getWidth() > maxWidth) {
			setSize(maxWidth, getHeight());
		}
		if (getHeight() > maxHeight) {
			setSize(getWidth(), maxHeight);
		}

		//
		// after pack, but before show

		MessageWindowsManager.add(this);

		//
		// detect the closing

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isClosed = true;
			}
		});

		//
		// close on escape

		UIUtils.setActionForEscapeKeyStroke(getRootPane(), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				closeMessageWindow();
			}
		});

		//
		// show (with FADE-IN)

		setAlwaysOnTop(true);

		new Thread() {
			public void run() {

				setOpacity(0f);
				setVisible(true);

				for (int i = 1; i <= 10; i++) {

					setOpacity(i / 10f);

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {}
				}
			}
		}.start();

		//
		// watch thread to close (with FADE-OUT) after some time
		// if there is no mouse movement over the window

		new Thread() {
			public void run() {

				closingCounter = 0;

				while (true) {

					if (isClosed) {
						break;
					}

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}

					if (getBounds().contains(MouseInfo.getPointerInfo().getLocation())) {
						closingCounter = 0;
						setOpacity(1f);
						continue;
					}

					closingCounter++;

					if (closingCounter >= 50) {
						setOpacity((100 - 2 * (closingCounter - 50)) / 100f);
					}

					if (closingCounter >= 100) {
						break;
					}
				}

				closeMessageWindow();
			}
		}.start();
	}

	private void closeMessageWindow() {
		dispose();
		MessageWindowsManager.remove(this);
	}

	private static Icon getIconFromOptionPane(MessageType messageType) {

		switch (messageType) {

			case ERROR:
				return UIManager.getIcon("OptionPane.errorIcon");
			case WARNING:
				return UIManager.getIcon("OptionPane.warningIcon");
			case INFORMATION:
				return UIManager.getIcon("OptionPane.informationIcon");
			default:
				return null;
		}
	}

	private static final class MessageWindowsManager {

		private static final List<AppTrayMessageWindow> windows = new ArrayList<>();

		private static synchronized void add(AppTrayMessageWindow window) {
			windows.add(window);
			updateLocations();
		}

		private static synchronized void remove(AppTrayMessageWindow window) {
			windows.remove(window);
			updateLocations();
		}

		private static void updateLocations() {

			Rectangle maxWinBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

			int y = 0;

			for (AppTrayMessageWindow window : windows) {

				y += window.getHeight();

				window.setLocation(maxWinBounds.x + maxWinBounds.width - window.getWidth(), maxWinBounds.y + maxWinBounds.height - y);
			}
		}
	}

}
