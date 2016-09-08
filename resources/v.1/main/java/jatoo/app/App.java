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

import jatoo.log4j.Log4jUtils;
import jatoo.ui.ActionGlueMarginBottom;
import jatoo.ui.ActionGlueMarginLeft;
import jatoo.ui.ActionGlueMarginRight;
import jatoo.ui.ActionGlueMarginTop;
import jatoo.ui.UIUtils;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JaToo Application
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 9.1, May 21, 2014
 */
@SuppressWarnings("serial")
public class App {

	private AppInitializer initializer;

	private Log logger;

	private AppProperties properties;
	private AppTexts texts;

	private Window window;
	private JPopupMenu windowPopup;

	private boolean hideWhenMinimized;
	private int transparency = 100;

	private TrayIcon trayIcon;

	public App(AppInitializer initializer) {

		this.initializer = initializer;

		//
		// get the logger after Log4j initialization

		Log4jUtils.init(initializer.getWorkingDirectory());

		logger = LogFactory.getLog(getClass());

		//
		// load properties

		properties = new AppProperties(new File(initializer.getWorkingDirectory(), "app.properties"));
		properties.loadSilently();

		//
		// local texts

		texts = new AppTexts(initializer.getClass(), getLogger(), new AppTexts(App.class, getLogger()));

		//
		// create & initialize the window

		if (initializer.isDialog()) {

			window = new JDialog((Frame) null, getTitle());

			if (initializer.isUndecorated()) {
				((JDialog) window).setUndecorated(true);
				((JDialog) window).setBackground(new Color(0, 0, 0, 0));
			}

			((JDialog) window).setResizable(initializer.isResizable());
		}

		else {

			window = new JFrame(getTitle());

			if (initializer.isUndecorated()) {
				((JFrame) window).setUndecorated(true);
				((JFrame) window).setBackground(new Color(0, 0, 0, 0));
			}

			((JFrame) window).setResizable(initializer.isResizable());
		}

		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				if (isHideWhenMinimized()) {
					hide();
				}
			}
		});

		//
		// set icon images

		List<Image> windowIconImages = new ArrayList<>();
		String[] windowIconImagesNames = new String[] { "016", "020", "022", "024", "032", "048", "064", "128", "256", "512" };

		for (String size : windowIconImagesNames) {
			try {
				windowIconImages.add(new ImageIcon(initializer.getClass().getResource("icons/" + size + ".png")).getImage());
			} catch (Exception e) {}
		}

		if (windowIconImages.size() == 0) {

			for (String size : windowIconImagesNames) {
				try {
					windowIconImages.add(new ImageIcon(App.class.getResource("icons/" + size + ".png")).getImage());
				} catch (Exception e) {}
			}
		}

		window.setIconImages(windowIconImages);

		//
		// get/init/set content pane

		AppContentPane contentPane = initializer.getContentPane();
		contentPane.init(this);

		((RootPaneContainer) window).setContentPane(contentPane);

		window.pack();

		//
		// center window (as default in case restore fails)
		// and try to restore the last location

		window.setLocationRelativeTo(null);
		window.setLocation(properties.getLocation(window.getLocation()));

		if (!initializer.isUndecorated()) {
			if (initializer.isResizable()) {

				String currentLookAndFeel = String.valueOf(UIManager.getLookAndFeel());

				if (properties.getLastLookAndFeel(currentLookAndFeel).equals(currentLookAndFeel)) {
					window.setSize(properties.getSize(window.getSize()));
				}
			}
		}

		//
		// fix location if out of screen

		Rectangle intersection = window.getGraphicsConfiguration().getBounds().intersection(window.getBounds());

		if ((intersection.width < window.getWidth() * 1 / 2) || (intersection.height < window.getHeight() * 1 / 2)) {
			UIUtils.setWindowLocationRelativeToScreen(window);
		}

		//
		// restore some properties

		setAlwaysOnTop(properties.isAlwaysOnTop());
		setHideWhenMinimized(properties.isHideWhenMinimized());
		setTransparency(properties.getTransparency(transparency));

		//
		// if the window is undecorated

		if (initializer.isUndecorated()) {

			//
			// glue to margins on Ctrl + ARROWS

			Insets marginsGlueGaps = contentPane.getMarginsGlueGaps();

			UIUtils.setActionForCtrlDownKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginBottom(window, marginsGlueGaps.bottom));
			UIUtils.setActionForCtrlLeftKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginLeft(window, marginsGlueGaps.left));
			UIUtils.setActionForCtrlRightKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginRight(window, marginsGlueGaps.right));
			UIUtils.setActionForCtrlUpKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginTop(window, marginsGlueGaps.top));

			//
			// move 1 px on ARROWS

			UIUtils.setActionForDownKeyStroke(((RootPaneContainer) window).getRootPane(), new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					window.setLocation(window.getX(), window.getY() + 1);
				}
			});
			UIUtils.setActionForLeftKeyStroke(((RootPaneContainer) window).getRootPane(), new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					window.setLocation(window.getX() - 1, window.getY());
				}
			});
			UIUtils.setActionForRightKeyStroke(((RootPaneContainer) window).getRootPane(), new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					window.setLocation(window.getX() + 1, window.getY());
				}
			});
			UIUtils.setActionForUpKeyStroke(((RootPaneContainer) window).getRootPane(), new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					window.setLocation(window.getX(), window.getY() - 1);
				}
			});

			//
			// move the window by dragging the UI component

			int marginsGlueRange = Math.min(window.getGraphicsConfiguration().getBounds().width, window.getGraphicsConfiguration().getBounds().height);
			marginsGlueRange /= 60;
			marginsGlueRange = Math.max(marginsGlueRange, 15);

			UIUtils.forwardDragAsMove(contentPane.getDragComponent(), window, marginsGlueRange, marginsGlueGaps);

			//
			// window popup

			contentPane.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						windowPopup = getWindowPopup(e.getLocationOnScreen());
						windowPopup.setVisible(true);
					}
				}
			});

			//
			// always dispose the popup when window lose the focus

			window.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if (windowPopup != null) {
						windowPopup.setVisible(false);
						windowPopup = null;
					}
				}
			});
		}

		//
		// tray icon

		if (SystemTray.isSupported()) {

			Image trayIconImage = windowIconImages.get(0);
			Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();

			for (Image windowIconImage : windowIconImages) {

				if (Math.abs(trayIconSize.width - windowIconImage.getWidth(null)) < Math.abs(trayIconImage.getWidth(null) - windowIconImage.getWidth(null))) {
					trayIconImage = windowIconImage;
				}
			}

			trayIcon = new TrayIcon(trayIconImage);
			trayIcon.setPopupMenu(getTrayIconPopup());

			trayIcon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					if (SwingUtilities.isLeftMouseButton(e)) {
						if (e.getClickCount() >= 2) {
							if (window.isVisible()) {
								hide();
							} else {
								show();
							}
						}
					}
				}
			});

			try {
				SystemTray.getSystemTray().add(trayIcon);
			}

			catch (Throwable t) {
				logger.error("unexpected exception trying to add the tray icon", t);
			}
		}

		//
		// hidden or not

		if (properties.isVisible()) {
			window.setVisible(true);
		}

		//
		// close the splash screen
		// if there is one

		try {

			SplashScreen splash = SplashScreen.getSplashScreen();

			if (splash != null) {
				splash.close();
			}
		}

		catch (UnsupportedOperationException e) {
			getLogger().info("splash screen not supported", e);
		}

		//
		// add shutdown hook for #destroy()

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				App.this.destroy();
			}
		});
	}

	public final void destroy() {

		//
		// save properties

		properties.setLocation(window.getLocation());
		properties.setSize(window.getSize());
		properties.setAlwaysOnTop(isAlwaysOnTop());
		properties.setTransparency(getTransparency());
		properties.setHideWhenMinimized(isHideWhenMinimized());
		properties.setVisible(window.isVisible());
		properties.setLastLookAndFeel(String.valueOf(UIManager.getLookAndFeel()));

		properties.saveSilently();
	}

	public Log getLogger() {
		return logger;
	}

	public String getText(String key) {
		return texts.getText(key);
	}

	public Window getWindow() {
		return window;
	}

	public void show() {

		window.setVisible(true);

		if (!initializer.isDialog()) {
			((JFrame) window).setState(JFrame.NORMAL);
		}
	}

	public String getTitle() {
		return texts.getText("app.title");
	}

	public void hide() {
		window.setVisible(false);
	}

	public void sendToBack() {
		window.toBack();
	}

	public void sendToFront() {
		window.toFront();
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		window.setAlwaysOnTop(alwaysOnTop);
	}

	public boolean isAlwaysOnTop() {
		return window.isAlwaysOnTop();
	}

	public void setHideWhenMinimized(boolean hideWhenMinimized) {
		this.hideWhenMinimized = hideWhenMinimized;
	}

	public boolean isHideWhenMinimized() {
		return hideWhenMinimized;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
		window.setOpacity(transparency / 100f);
	}

	public int getTransparency() {
		return transparency;
	}

	/**
	 * Displays a message.
	 * 
	 * @param message
	 *          the message to display
	 */
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Displays an error message.
	 * 
	 * @param message
	 *          the message to display
	 */
	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Displays a warning message.
	 * 
	 * @param message
	 *          the message to display
	 */
	public void showWarningMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Displays an information message.
	 * 
	 * @param message
	 *          the message to display
	 */
	public void showInformationMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays a message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	public void showTrayMessage(String title, String message) {
		new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.NONE);
	}

	/**
	 * Displays a message near the tray icon and will execute the provided action.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param actionListener
	 *          the action to be executed on message click
	 * @param message
	 *          the message to display
	 */
	public void showTrayMessage(String title, String message, ActionListener actionListener) {
		new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.NONE, actionListener);
	}

	/**
	 * Displays an error message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	public void showTrayErrorMessage(String title, String message) {
		new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.ERROR);
	}

	/**
	 * Displays a warning message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	public void showTrayWarningMessage(String title, String message) {
		new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.WARNING);
	}

	/**
	 * Displays an information message near the tray icon.
	 * 
	 * @param title
	 *          the caption displayed above the message, usually in bold; may be
	 *          <code>null</code>
	 * @param message
	 *          the message to display
	 */
	public void showTrayInformationMessage(String title, String message) {
		new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.INFORMATION);
	}

	/**
	 * Provides a simple mechanism for the user to select a file.
	 * 
	 * @param currentDirectory
	 *          the current directory to point to
	 */
	public File selectFile(File currentDirectory) {
		return select(currentDirectory, JFileChooser.FILES_ONLY);
	}

	/**
	 * Provides a simple mechanism for the user to select a directory.
	 * 
	 * @param currentDirectory
	 *          the current directory to point to
	 */
	public File selectDirectory(File currentDirectory) {
		return select(currentDirectory, JFileChooser.DIRECTORIES_ONLY);
	}

	/**
	 * Provides a simple mechanism for the user to select a file or a directory.
	 * 
	 * @param currentDirectory
	 *          the current directory to point to
	 */
	public File selectFileOrDirectory(File currentDirectory) {
		return select(currentDirectory, JFileChooser.FILES_AND_DIRECTORIES);
	}

	private File select(File currentDirectory, int fileSelectionMode) {

		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setFileSelectionMode(fileSelectionMode);

		int returnValue = fileChooser.showOpenDialog(window);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}

		return null;
	}

	//
	//

	private JPopupMenu getWindowPopup(Point location) {

		//
		// hide

		final JMenuItem hideItem = new JMenuItem(texts.getText("popup.hide"));
		hideItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});

		//
		// send to back

		final JMenuItem sendToBackItem = new JMenuItem(texts.getText("popup.send_to_back"));
		sendToBackItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToBack();
			}
		});

		//
		// always on top

		final JCheckBoxMenuItem alwaysOnTopItem = new JCheckBoxMenuItem(texts.getText("popup.always_on_top"), isAlwaysOnTop());
		alwaysOnTopItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setAlwaysOnTop(alwaysOnTopItem.isSelected());
			}
		});

		//
		// transparency

		final JSlider transparencySlider = new JSlider(JSlider.VERTICAL, 0, 100, getTransparency());
		transparencySlider.setMajorTickSpacing(25);
		transparencySlider.setMinorTickSpacing(5);
		transparencySlider.setSnapToTicks(true);
		transparencySlider.setPaintTicks(true);
		transparencySlider.setPaintLabels(true);
		transparencySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setTransparency(transparencySlider.getValue());
			}
		});

		final JMenu transparencyItem = new JMenu(texts.getText("popup.transparency"));
		transparencyItem.add(transparencySlider);

		//
		// close

		final JMenuItem closeItem = new JMenuItem(texts.getText("popup.close"));
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		try {
			closeItem.setIcon(new ImageIcon(initializer.getClass().getResource("icons/popup.close.png")));
		} catch (Throwable t1) {
			try {
				closeItem.setIcon(new ImageIcon(App.class.getResource("icons/popup.close.png")));
			} catch (Throwable t2) {
				getLogger().warn("failed to read popup close icon", t2);
			}
		}

		//
		// the popup

		JPopupMenu popup = new JPopupMenu(getTitle());

		popup.add(hideItem);
		popup.addSeparator();
		popup.add(sendToBackItem);
		popup.add(alwaysOnTopItem);
		popup.add(transparencyItem);
		popup.addSeparator();
		popup.add(closeItem);

		popup.setInvoker(popup);
		popup.setLocation(location);

		return popup;
	}

	private PopupMenu getTrayIconPopup() {

		MenuItem openItem = new MenuItem(texts.getText("popup.open") + " " + getTitle());
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				show();
			}
		});

		MenuItem hideItem = new MenuItem(texts.getText("popup.hide") + " " + getTitle());
		hideItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});

		CheckboxMenuItem hideWhenMinimizedItem = new CheckboxMenuItem(texts.getText("popup.hide_when_minimized"), isHideWhenMinimized());
		hideWhenMinimizedItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setHideWhenMinimized(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		MenuItem sendToBackItem = new MenuItem(texts.getText("popup.send_to_back"));
		sendToBackItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToBack();
			}
		});

		MenuItem closeItem = new MenuItem(texts.getText("popup.close"));
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		//
		//

		Font font = new JMenuItem().getFont();

		if (font != null) {
			openItem.setFont(font.deriveFont(Font.BOLD));
			hideItem.setFont(font);
			hideWhenMinimizedItem.setFont(font);
			sendToBackItem.setFont(font);
			closeItem.setFont(font);
		}

		//
		// the popup

		PopupMenu popup = new PopupMenu(getTitle());

		popup.add(openItem);
		popup.add(hideItem);
		popup.addSeparator();
		popup.add(hideWhenMinimizedItem);
		popup.addSeparator();
		popup.add(sendToBackItem);
		popup.addSeparator();
		popup.add(closeItem);

		return popup;
	}

}
