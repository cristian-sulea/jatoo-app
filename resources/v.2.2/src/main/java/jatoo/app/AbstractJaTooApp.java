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

import jatoo.image.BufferedImageIcon;
import jatoo.image.ImageUtils;
import jatoo.swing.JaTooSwingUtils;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.beans.factory.annotation.Autowired;

import frameworks.spring.SpringBean;
import frameworks.spring.SpringTexts;

/**
 * Abstract implementation for JaToo Application. This provides a convenient
 * base class from which other applications can be easily derived.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 7.0 February 20, 2014
 */
@SuppressWarnings("serial")
public abstract class AbstractJaTooApp extends SpringBean implements JaTooApp {

	@Autowired
	private JaTooAppContentPane appContentPane;

	private JaTooAppProperties properties;
	private SpringTexts texts;

	private Window window;
	private JPopupMenu windowPopup;

	private boolean hideWhenMinimized;
	private int transparency = 100;

	private TrayIcon trayIcon;

	@Override
	protected final void initBean() throws Throwable {

		//
		// local texts

		texts = new SpringTexts(AbstractJaTooApp.class, getLogger());

		//
		// load properties

		properties = new JaTooAppProperties(new File(getWorkingDirectory(), "app.properties"));
		properties.loadSilently();

		//
		// create & initialize the window

		if (isDialog()) {

			window = new JDialog((Frame) null, getAppTitle());

			if (isUndecorated()) {
				((JDialog) window).setUndecorated(true);
				((JDialog) window).setBackground(new Color(0, 0, 0, 0));
			}
		}

		else {

			window = new JFrame(getAppTitle());

			if (isUndecorated()) {
				((JFrame) window).setUndecorated(true);
				((JFrame) window).setBackground(new Color(0, 0, 0, 0));
			}
		}

		((JFrame) window).setResizable(isResizable());

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

		List<BufferedImage> windowIconImages = new ArrayList<>();
		String[] windowIconImagesNames = new String[] { "016", "020", "022", "024", "032", "048", "064", "128", "256", "512" };

		for (String size : windowIconImagesNames) {
			try {
				windowIconImages.add(ImageUtils.read(getClass().getResource("icons/" + size + ".png")));
			} catch (Exception e) {}
		}

		if (windowIconImages.size() == 0) {

			for (String size : windowIconImagesNames) {
				try {
					windowIconImages.add(ImageUtils.read(AbstractJaTooApp.class.getResource("icons/" + size + ".png")));
				} catch (Exception e) {}
			}
		}

		window.setIconImages(windowIconImages);

		//
		// set content pane

		((RootPaneContainer) window).setContentPane(appContentPane);
		window.pack();

		//
		// center window (as default in case restore fails)
		// and try to restore the last location

		window.setLocationRelativeTo(null);
		window.setLocation(properties.getLocation(window.getLocation()));

		if (!isUndecorated()) {
			if (isResizable()) {
				window.setSize(properties.getSize(window.getSize()));
			}
		}

		//
		// fix location if out of screen

		Rectangle intersection = window.getGraphicsConfiguration().getBounds().intersection(window.getBounds());

		if ((intersection.width < window.getWidth() * 1 / 2) || (intersection.height < window.getHeight() * 1 / 2)) {
			JaTooSwingUtils.setWindowLocationRelativeToScreen(window);
		}

		//
		// restore some properties

		setAlwaysOnTop(properties.isAlwaysOnTop());
		setHideWhenMinimized(properties.isHideWhenMinimized());
		setTransparency(properties.getTransparency(transparency));

		//
		// if the window is undecorated

		if (isUndecorated()) {

			//
			// glue to margins on Ctrl + ARROWS

			Insets marginsGlueGaps = appContentPane.getMarginsGlueGaps();

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

			UIUtils.forwardDragAsMove(appContentPane.getDragComponent(), window, marginsGlueRange, marginsGlueGaps);

			//
			// window popup

			appContentPane.addMouseListener(new MouseAdapter() {
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

			BufferedImage trayIconImage = windowIconImages.get(0);
			Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();

			for (BufferedImage windowIconImage : windowIconImages) {

				if (Math.abs(trayIconSize.width - windowIconImage.getWidth()) < Math.abs(trayIconImage.getWidth() - windowIconImage.getWidth())) {
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

			SystemTray.getSystemTray().add(trayIcon);
		}

		//
		// initialize and show the application

		initApp();

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
	}

	@Override
	public void destroy() {

		//
		// save properties

		properties.setLocation(window.getLocation());
		properties.setSize(window.getSize());
		properties.setAlwaysOnTop(isAlwaysOnTop());
		properties.setTransparency(getTransparency());
		properties.setHideWhenMinimized(isHideWhenMinimized());
		properties.setVisible(window.isVisible());

		properties.saveSilently();
	}

	/**
	 * Initialize the application.
	 * 
	 * @throws Throwable
	 *           if the application should not start
	 */
	protected void initApp() throws Throwable {}

	protected abstract File getWorkingDirectory();

	protected abstract boolean isDialog();

	protected abstract boolean isUndecorated();

	protected abstract boolean isResizable();

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public void show() {

		window.setVisible(true);

		if (!isDialog()) {
			((JFrame) window).setState(JFrame.NORMAL);
		}
	}

	@Override
	public void hide() {
		window.setVisible(false);
	}

	@Override
	public void sendToBack() {
		window.toBack();
	}

	@Override
	public void sendToFront() {
		window.toFront();
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		window.setAlwaysOnTop(alwaysOnTop);
	}

	@Override
	public boolean isAlwaysOnTop() {
		return window.isAlwaysOnTop();
	}

	@Override
	public void setHideWhenMinimized(boolean hideWhenMinimized) {
		this.hideWhenMinimized = hideWhenMinimized;
	}

	@Override
	public boolean isHideWhenMinimized() {
		return hideWhenMinimized;
	}

	@Override
	public void setTransparency(int transparency) {
		this.transparency = transparency;
		window.setOpacity(transparency / 100f);
	}

	@Override
	public int getTransparency() {
		return transparency;
	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getAppTitle(), JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getAppTitle(), JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showWarningMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getAppTitle(), JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void showInformationMessage(String message) {
		JOptionPane.showMessageDialog(window, message, getAppTitle(), JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void showTrayMessage(String title, String message) {
		new JaTooAppTrayMessageWindow(this, title, message, JaTooAppTrayMessageWindow.MessageType.NONE);
	}
	
	@Override
	public void showTrayMessage(String title, String message, ActionListener actionListener) {
		new JaTooAppTrayMessageWindow(this, title, message, JaTooAppTrayMessageWindow.MessageType.NONE, actionListener);
	}

	@Override
	public void showTrayErrorMessage(String title, String message) {
		new JaTooAppTrayMessageWindow(this, title, message, JaTooAppTrayMessageWindow.MessageType.ERROR);
	}

	@Override
	public void showTrayWarningMessage(String title, String message) {
		new JaTooAppTrayMessageWindow(this, title, message, JaTooAppTrayMessageWindow.MessageType.WARNING);
	}

	@Override
	public void showTrayInformationMessage(String title, String message) {
		new JaTooAppTrayMessageWindow(this, title, message, JaTooAppTrayMessageWindow.MessageType.INFORMATION);
	}

	@Override
	public File selectFile(File currentDirectory) {
		return select(currentDirectory, JFileChooser.FILES_ONLY);
	}

	@Override
	public File selectDirectory(File currentDirectory) {
		return select(currentDirectory, JFileChooser.DIRECTORIES_ONLY);
	}

	@Override
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

	private String getAppTitle() {
		return getText("app.title");
	}

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
			closeItem.setIcon(new BufferedImageIcon(ImageUtils.read(getClass().getResource("icons/popup.close.png"))));
		} catch (Throwable t1) {
			try {
				closeItem.setIcon(new BufferedImageIcon(ImageUtils.read(AbstractJaTooApp.class.getResource("icons/popup.close.png"))));
			} catch (Throwable t2) {
				getLogger().warn("failed to read popup close icon", t2);
			}
		}

		//
		// the popup

		JPopupMenu popup = new JPopupMenu(getAppTitle());

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

		MenuItem openItem = new MenuItem(texts.getText("popup.open") + " " + getAppTitle());
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				show();
			}
		});

		Font showItemFont = openItem.getFont();
		if (showItemFont == null) {
			showItemFont = appContentPane.getFont();
		}
		if (showItemFont == null) {
			showItemFont = window.getFont();
		}
		if (showItemFont != null) {
			openItem.setFont(showItemFont.deriveFont(Font.BOLD));
		}

		MenuItem hideItem = new MenuItem(texts.getText("popup.hide") + " " + getAppTitle());
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
		// the popup

		PopupMenu popup = new PopupMenu(getAppTitle());

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
