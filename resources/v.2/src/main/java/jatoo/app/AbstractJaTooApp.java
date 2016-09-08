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

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import frameworks.spring.SpringBean;
import frameworks.spring.SpringTexts;

/**
 * Abstract implementation for JaToo Application. This provides a convenient
 * base class from which other applications can be easily derived.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 3.1 December 10, 2013
 */
@SuppressWarnings("serial")
public abstract class AbstractJaTooApp<PROPERTIES extends JaTooAppProperties> extends SpringBean implements JaTooApp<PROPERTIES>, ApplicationContextAware {

	private SpringTexts texts;

	private Map<String, Class<? extends JaTooAppUIComponent>> themes;

	private PROPERTIES properties;

	private Window window;
	private JaTooAppUIComponent uiComponent;
	private Class<? extends JaTooAppUIComponent> uiComponentClass;
	private JPopupMenu popup;

	private boolean isOpened = false;
	private int transparency = 100;

	@Override
	protected final void initBean() throws Throwable {

		//
		// local texts

		texts = new SpringTexts(AbstractJaTooApp.class, getLogger());

		//
		// load themes

		themes = new HashMap<>();
		themes.put(texts.getText("popup.themes.default"), getDefaultUIComponentClass());

		try {
			for (URL url : Collections.list(getClass().getClassLoader().getResources("META-INF/" + getClass().getSimpleName() + ".themes"))) {
				new Properties() {

					@SuppressWarnings("unchecked")
					@Override
					public synchronized Object put(Object key, Object value) {

						try {
							themes.put((String) key, (Class<? extends JaTooAppUIComponent>) Class.forName((String) value));
						}

						catch (Throwable t) {
							getLogger().error("failed to get the theme: " + key + " = " + value, t);
						}

						return super.put(key, value);
					}
				}.load(url.openStream());
			}
		}

		catch (Throwable t) {
			getLogger().error("failed to get the theme resources (files)", t);
		}

		//
		// load properties

		properties = createProperties();
		properties.loadSilently();

		//
		// default UI component class

		if (themes.size() > 1) {
			uiComponentClass = properties.getUIComponentClass(getDefaultUIComponentClass());
		} else {
			uiComponentClass = getDefaultUIComponentClass();
		}

		//
		// initialize the application

		initApp();

		//
		// if it was open,
		// open'it again

		if (properties.isOpened()) {
			open();
		}
	}

	/**
	 * Initialize the application.
	 * 
	 * @throws Throwable
	 *           if the application should not start
	 */
	protected void initApp() throws Throwable {}

	@Override
	public void destroy() {

		//
		// save all properties

		if (isOpened()) {
			saveProperties();
		}

		if (isExitOnClose()) {
			properties.setOpened(true);
		} else {
			properties.setOpened(isOpened());
		}

		try {
			properties.save();
		} catch (IOException e) {
			getLogger().warn("failed to save the properties", e);
		}
	}

	protected abstract PROPERTIES createProperties();

	@Override
	public PROPERTIES getProperties() {
		return properties;
	}

	@Override
	public void setUIComponentClass(Class<? extends JaTooAppUIComponent> uiComponentClass) {

		this.uiComponentClass = uiComponentClass;

		close(false);
		open();
	}

	@Override
	public Class<? extends JaTooAppUIComponent> getUIComponentClass() {
		return uiComponentClass;
	}

	protected abstract Class<? extends JaTooAppUIComponent> getDefaultUIComponentClass();

	@Override
	public void open() {

		if (!isOpened) {
			synchronized (this) {
				if (!isOpened) {

					//
					// create & initialize the window

					uiComponent = applicationContext.getBean(uiComponentClass);

					window = createWindow(uiComponent);

					((RootPaneContainer) window).setContentPane(uiComponent);
					window.pack();

					//
					// window icon images

					List<BufferedImage> windowIconImages = new ArrayList<>();

					for (String size : new String[] { "016", "022", "032", "048", "064", "128", "256" }) {
						try {
							windowIconImages.add(ImageUtils.read(getClass().getResource("icons/" + size + ".png")));
						} catch (Exception e) {}
					}

					if (windowIconImages.size() > 0) {
						window.setIconImages(windowIconImages);
					}

					//
					// center window (as default in case restore fails)
					// and try to restore the last location

					window.setLocationRelativeTo(null);
					window.setLocation(properties.getLocation(window.getLocation()));
					window.setSize(properties.getSize(window.getSize()));
					window.setVisible(true);

					//
					// fix location if out of screen

					Rectangle intersection = window.getGraphicsConfiguration().getBounds().intersection(window.getBounds());

					if ((intersection.width < window.getWidth() * 1 / 2) || (intersection.height < window.getHeight() * 1 / 2)) {
						JaTooSwingUtils.setWindowLocationRelativeToScreen(window);
					}

					//
					// restore some properties

					setAlwaysOnTop(properties.isAlwaysOnTop());
					setTransparency(properties.getTransparency(transparency));

					//
					// add "things"

					Insets marginsGlueGaps = uiComponent.getMarginsGlueGaps();

					//
					// glue to margins on Ctrl + ARROWS

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
					// if the window is undecorated

					boolean isUndecorated = false;

					isUndecorated = isUndecorated || (window instanceof JFrame && ((JFrame) window).isUndecorated());
					isUndecorated = isUndecorated || (window instanceof JDialog && ((JDialog) window).isUndecorated());

					if (isUndecorated) {

						//
						// move the window by dragging the UI component

						int marginsGlueRange = Math.min(window.getGraphicsConfiguration().getBounds().width, window.getGraphicsConfiguration().getBounds().height);
						marginsGlueRange /= 60;
						marginsGlueRange = Math.max(marginsGlueRange, 15);

						UIUtils.forwardDragAsMove(uiComponent, window, marginsGlueRange, uiComponent.getMarginsGlueGaps());

						//
						// add a popup

						uiComponent.addMouseListener(new MouseAdapter() {
							public void mouseReleased(MouseEvent e) {

								if (SwingUtilities.isRightMouseButton(e)) {

									popup = new JPopupMenu(getName());

									//
									// send to back

									final JMenuItem popupSendToBackItem = new JMenuItem(texts.getText("popup.send_to_back"));
									popupSendToBackItem.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											sendToBack();
										}
									});

									//
									// always on top

									final JCheckBoxMenuItem popupAlwaysOnTopItem = new JCheckBoxMenuItem(texts.getText("popup.always_on_top"), isAlwaysOnTop());
									popupAlwaysOnTopItem.addItemListener(new ItemListener() {
										public void itemStateChanged(ItemEvent e) {
											setAlwaysOnTop(popupAlwaysOnTopItem.isSelected());
										}
									});

									//
									// transparency

									final JSlider popupTransparencySlider = new JSlider(JSlider.VERTICAL, 0, 100, getTransparency());
									popupTransparencySlider.setMajorTickSpacing(25);
									popupTransparencySlider.setMinorTickSpacing(5);
									popupTransparencySlider.setSnapToTicks(true);
									popupTransparencySlider.setPaintTicks(true);
									popupTransparencySlider.setPaintLabels(true);
									popupTransparencySlider.addChangeListener(new ChangeListener() {
										public void stateChanged(ChangeEvent e) {
											setTransparency(popupTransparencySlider.getValue());
										}
									});

									final JMenu popupTransparencyItem = new JMenu(texts.getText("popup.transparency"));
									popupTransparencyItem.add(popupTransparencySlider);

									//
									// close

									final JMenuItem popupCloseItem = new JMenuItem(texts.getText("popup.close"));
									popupCloseItem.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											close();
										}
									});
									try {
										popupCloseItem.setIcon(new BufferedImageIcon(ImageUtils.read(AbstractJaTooApp.this.getClass().getResource("icons/popup.close.png"))));
									} catch (Throwable t1) {
										try {
											popupCloseItem.setIcon(new BufferedImageIcon(ImageUtils.read(AbstractJaTooApp.class.getResource("icons/popup.close.png"))));
										} catch (Throwable t2) {
											getLogger().warn("failed to read popup close icon", t2);
										}
									}

									//
									// themes

									JMenu popupThemesMenu = new JMenu(texts.getText("popup.themes"));

									List<String> themesNames = new ArrayList<>(themes.keySet());
									Collections.sort(themesNames);

									for (final String themeName : themesNames) {

										JCheckBoxMenuItem popupThemesItem = new JCheckBoxMenuItem(themeName, uiComponentClass.equals(themes.get(themeName)));

										popupThemesItem.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												setUIComponentClass(themes.get(themeName));
											}
										});

										popupThemesMenu.add(popupThemesItem);
									}

									popup.add(popupSendToBackItem);
									popup.add(popupAlwaysOnTopItem);
									popup.add(popupTransparencyItem);

									if (themes.size() > 1) {
										popup.add(new JPopupMenu.Separator());
										popup.add(popupThemesMenu);

									}

									completePopupMenu(popup);

									popup.add(new JPopupMenu.Separator());
									popup.add(popupCloseItem);

									popup.setInvoker(popup);
									popup.setLocation(e.getLocationOnScreen());

									popup.setVisible(true);
								}
							}
						});

						//
						// always dispose the popup when window lose the focus

						window.addFocusListener(new FocusAdapter() {
							public void focusLost(FocusEvent e) {
								if (popup != null) {
									popup.setVisible(false);
									popup = null;
								}
							}
						});
					}

					//
					// save state

					isOpened = true;
				}
			}
		}
	}

	protected abstract Window createWindow(JaTooAppUIComponent uiComponent);

	protected void completePopupMenu(JPopupMenu popup) {}

	@Override
	public boolean isOpened() {
		synchronized (this) {
			return isOpened;
		}
	}

	@Override
	public void close() {
		close(true);
	}

	private void close(boolean checkIfIsExitOnClose) {

		if (isOpened) {
			synchronized (this) {
				if (isOpened) {

					//
					// save state

					isOpened = false;

					//
					// save some properties

					saveProperties();

					//
					// release resources

					window.removeAll();

					if (popup != null) {
						popup.setVisible(false);
						popup = null;
					}

					uiComponent.destroy();
					uiComponent = null;

					window.dispose();
					window = null;

					System.gc();

					if (checkIfIsExitOnClose) {

						if (isExitOnClose()) {

							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									System.exit(0);
								}
							});
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isClosed() {
		synchronized (this) {
			return !isOpened;
		}
	}

	@Override
	public boolean isExitOnClose() {
		return true;
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
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(window, message, getName(), JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void showErrorMessage(String message) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(window, message, getName(), JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showWarningMessage(String message) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(window, message, getName(), JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void showInformationMessage(String message) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(window, message, getName(), JOptionPane.INFORMATION_MESSAGE);
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
	// we want to be notified of the {@link ApplicationContext}
	// to get beans ( very useful for PROTOTYPE beans )

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	//
	//

	private void saveProperties() {
		properties.setLocation(window.getLocation());
		properties.setSize(window.getSize());
		properties.setAlwaysOnTop(isAlwaysOnTop());
		properties.setTransparency(getTransparency());
		properties.setUIComponentClass(uiComponentClass);
	}

}
