/*
 * Copyright (C) Cristian Sulea ( http://cristian.sulea.net )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jatoo.app;

import java.awt.AWTException;
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

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
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

import jatoo.resources.ResourcesImages;
import jatoo.resources.ResourcesTexts;
import jatoo.ui.ActionGlueMarginBottom;
import jatoo.ui.ActionGlueMarginLeft;
import jatoo.ui.ActionGlueMarginRight;
import jatoo.ui.ActionGlueMarginTop;
import jatoo.ui.UIUtils;

/**
 * This is the heart of the JaToo App Library, a Java Open Source library
 * created to ease the start of the java applications.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.1, August 20, 2014
 */
public abstract class App {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final Log logger = LogFactory.getLog(getClass());

  private final String title;

  private AppProperties properties;

  private ResourcesTexts texts;
  private ResourcesImages images;

  private Window window;
  private JPopupMenu windowPopup;

  private boolean hideWhenMinimized;
  private int transparency = 100;

  public App(final String title) {

    this.title = title;

    //
    // load properties

    properties = new AppProperties(new File(getWorkingDirectory(), "app.properties"));
    properties.loadSilently();

    //
    // resources

    texts = new ResourcesTexts(getClass(), new ResourcesTexts(App.class));
    images = new ResourcesImages(getClass(), new ResourcesImages(App.class));

    //
    // create & initialize the window

    if (isDialog()) {

      window = new JDialog((Frame) null, getTitle());

      if (isUndecorated()) {
        ((JDialog) window).setUndecorated(true);
        ((JDialog) window).setBackground(new Color(0, 0, 0, 0));
      }

      ((JDialog) window).setResizable(isResizable());
    }

    else {

      window = new JFrame(getTitle());

      if (isUndecorated()) {
        ((JFrame) window).setUndecorated(true);
        ((JFrame) window).setBackground(new Color(0, 0, 0, 0));
      }

      ((JFrame) window).setResizable(isResizable());
    }

    window.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(final WindowEvent e) {
        System.exit(0);
      }

      @Override
      public void windowIconified(final WindowEvent e) {
        if (isHideWhenMinimized()) {
          hide();
        }
      }
    });

    //
    // set icon images
    // is either all icons from initializer package
    // or all icons from app package

    List<Image> windowIconImages = new ArrayList<>();
    String[] windowIconImagesNames = new String[] { "016", "020", "022", "024", "032", "048", "064", "128", "256", "512" };

    for (String size : windowIconImagesNames) {
      try {
        windowIconImages.add(new ImageIcon(getClass().getResource("app-icon-" + size + ".png")).getImage());
      } catch (Exception e) {}
    }

    if (windowIconImages.size() == 0) {

      for (String size : windowIconImagesNames) {
        try {
          windowIconImages.add(new ImageIcon(App.class.getResource("app-icon-" + size + ".png")).getImage());
        } catch (Exception e) {}
      }
    }

    window.setIconImages(windowIconImages);

    //
    // this is the place for to call init method
    // after all local components are created
    // from now (after init) is only configuration

    init();

    //
    // set content pane ( and ansure transparency )

    JComponent contentPane = getContentPane();
    contentPane.setOpaque(false);

    ((RootPaneContainer) window).setContentPane(contentPane);

    //
    // pack the window right after the set of the content pane

    window.pack();

    //
    // center window (as default in case restore fails)
    // and try to restore the last location

    window.setLocationRelativeTo(null);
    window.setLocation(properties.getLocation(window.getLocation()));

    if (!isUndecorated()) {

      if (properties.getLastUndecorated(isUndecorated()) == isUndecorated()) {

        if (isResizable() && isSizePersistent()) {

          final String currentLookAndFeel = String.valueOf(UIManager.getLookAndFeel());

          if (properties.getLastLookAndFeel(currentLookAndFeel).equals(currentLookAndFeel)) {
            window.setSize(properties.getSize(window.getSize()));
          }
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
    // null is also a good value for margins glue gaps (is [0,0,0,0])

    Insets marginsGlueGaps = getMarginsGlueGaps();
    if (marginsGlueGaps == null) {
      marginsGlueGaps = new Insets(0, 0, 0, 0);
    }

    //
    // glue to margins on Ctrl + ARROWS

    if (isGlueToMarginsOnCtrlArrows()) {

      UIUtils.setActionForCtrlDownKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginBottom(window, marginsGlueGaps.bottom));
      UIUtils.setActionForCtrlLeftKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginLeft(window, marginsGlueGaps.left));
      UIUtils.setActionForCtrlRightKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginRight(window, marginsGlueGaps.right));
      UIUtils.setActionForCtrlUpKeyStroke(((RootPaneContainer) window).getRootPane(), new ActionGlueMarginTop(window, marginsGlueGaps.top));
    }

    //
    // drag to move ( when provided component is not null )

    JComponent dragToMoveComponent = getDragToMoveComponent();

    if (dragToMoveComponent != null) {

      //
      // move the window by dragging the UI component

      int marginsGlueRange = Math.min(window.getGraphicsConfiguration().getBounds().width, window.getGraphicsConfiguration().getBounds().height);
      marginsGlueRange /= 60;
      marginsGlueRange = Math.max(marginsGlueRange, 15);

      UIUtils.forwardDragAsMove(dragToMoveComponent, window, marginsGlueRange, marginsGlueGaps);

      //
      // window popup

      dragToMoveComponent.addMouseListener(new MouseAdapter() {
        public void mouseReleased(final MouseEvent e) {
          if (SwingUtilities.isRightMouseButton(e)) {
            windowPopup = getWindowPopup(e.getLocationOnScreen());
            windowPopup.setVisible(true);
          }
        }
      });

      //
      // always dispose the popup when window lose the focus

      window.addFocusListener(new FocusAdapter() {
        public void focusLost(final FocusEvent e) {
          if (windowPopup != null) {
            windowPopup.setVisible(false);
            windowPopup = null;
          }
        }
      });
    }

    //
    // tray icon

    if (hasTrayIcon()) {

      if (SystemTray.isSupported()) {

        Image trayIconImage = windowIconImages.get(0);
        Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();

        for (Image windowIconImage : windowIconImages) {

          if (Math.abs(trayIconSize.width - windowIconImage.getWidth(null)) < Math.abs(trayIconImage.getWidth(null) - windowIconImage.getWidth(null))) {
            trayIconImage = windowIconImage;
          }
        }

        final TrayIcon trayIcon = new TrayIcon(trayIconImage);
        trayIcon.setPopupMenu(getTrayIconPopup());

        trayIcon.addMouseListener(new MouseAdapter() {
          public void mouseClicked(final MouseEvent e) {

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
        } catch (AWTException e) {
          logger.error("unexpected exception trying to add the tray icon ( the desktop system tray is missing? )", e);
        }
      }

      else {
        logger.error("the system tray is not supported on the current platform");
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

    //
    // after init

    afterInit();
  }

  public void destroy() {

    //
    // save properties

    properties.setLocation(window.getLocation());
    properties.setSize(window.getSize());
    properties.setAlwaysOnTop(isAlwaysOnTop());
    properties.setTransparency(getTransparency());
    properties.setHideWhenMinimized(isHideWhenMinimized());
    properties.setVisible(window.isVisible());
    properties.setLastLookAndFeel(String.valueOf(UIManager.getLookAndFeel()));
    properties.setLastUndecorated(isUndecorated());

    properties.saveSilently();
  }

  protected abstract void init();

  protected abstract void afterInit();

  protected abstract File getWorkingDirectory();

  protected abstract JComponent getContentPane();

  protected abstract JComponent getDragToMoveComponent();

  protected abstract boolean isDialog();

  protected abstract boolean isUndecorated();

  protected abstract boolean isResizable();

  protected abstract boolean isSizePersistent();

  protected abstract boolean hasTrayIcon();

  protected abstract boolean isGlueToMarginsOnCtrlArrows();

  protected abstract Insets getMarginsGlueGaps();

  public String getText(final String key) {
    return texts.getText(key);
  }

  public ImageIcon getIcon(final String name) {
    return images.getImageIcon(name);
  }

  public Image getImage(final String name) {
    return images.getImage(name);
  }

  public Log getLogger() {
    return logger;
  }

  public Window getWindow() {
    return window;
  }

  public void show() {

    window.setVisible(true);

    if (!isDialog()) {
      ((JFrame) window).setState(JFrame.NORMAL);
    }
  }

  public final String getTitle() {
    return title;
  }

  public final void setWindowTitle(final String title) {

    final String newWindowTitle;
    if (title == null) {
      newWindowTitle = getTitle();
    } else {
      newWindowTitle = getTitle() + " - " + title;
    }

    if (isDialog()) {
      ((JDialog) window).setTitle(newWindowTitle);
    } else {
      ((JFrame) window).setTitle(newWindowTitle);
    }
  }

  public final String getWindowTitle(String title) {
    if (isDialog()) {
      return ((JDialog) window).getTitle();
    } else {
      return ((JFrame) window).getTitle();
    }
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

  public void setAlwaysOnTop(final boolean alwaysOnTop) {
    window.setAlwaysOnTop(alwaysOnTop);
  }

  public boolean isAlwaysOnTop() {
    return window.isAlwaysOnTop();
  }

  public void setHideWhenMinimized(final boolean hideWhenMinimized) {
    this.hideWhenMinimized = hideWhenMinimized;
  }

  public boolean isHideWhenMinimized() {
    return hideWhenMinimized;
  }

  public void setTransparency(final int transparency) {
    this.transparency = transparency;
    window.setOpacity(transparency / 100f);
  }

  public int getTransparency() {
    return transparency;
  }

  public void setActionForLeftKeyStroke(final Action action) {
    UIUtils.setActionForLeftKeyStroke(((RootPaneContainer) window).getRootPane(), action);
  }

  public void setActionForRightKeyStroke(final Action action) {
    UIUtils.setActionForRightKeyStroke(((RootPaneContainer) window).getRootPane(), action);
  }

  public void setActionForUpKeyStroke(final Action action) {
    UIUtils.setActionForUpKeyStroke(((RootPaneContainer) window).getRootPane(), action);
  }

  public void setActionForDownKeyStroke(final Action action) {
    UIUtils.setActionForDownKeyStroke(((RootPaneContainer) window).getRootPane(), action);
  }

  /**
   * Displays a YES/NO question.
   * 
   * @param question
   *          the question to be displayed
   * 
   * @return <code>true</code> if YES was selected, <code>false</code> if NO was
   *         selected
   */
  public boolean showQuestion(final String question) {
    return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(window, question, getTitle(), JOptionPane.YES_NO_OPTION);
  }

  /**
   * Displays a message.
   * 
   * @param message
   *          the message to display
   */
  public void showMessage(final String message) {
    JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.PLAIN_MESSAGE);
  }

  /**
   * Displays some messages, every message on a line.
   * 
   * @param messages
   *          the messages to display
   */
  public void showMessages(final List<String> messages) {

    final StringBuilder sb = new StringBuilder();

    for (String message : messages) {
      sb.append(message).append(LINE_SEPARATOR);
    }

    showMessage(sb.toString());
  }

  /**
   * Displays an error message.
   * 
   * @param message
   *          the message to display
   */
  public void showErrorMessage(final String message) {
    JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Displays some error messages, every message on a line.
   * 
   * @param messages
   *          the messages to display
   */
  public void showErrorMessages(final List<String> messages) {

    final StringBuilder sb = new StringBuilder();

    for (String message : messages) {
      sb.append(message).append(LINE_SEPARATOR);
    }

    showErrorMessage(sb.toString());
  }

  /**
   * Displays a warning message.
   * 
   * @param message
   *          the message to display
   */
  public void showWarningMessage(final String message) {
    JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Displays some warning messages, every message on a line.
   * 
   * @param messages
   *          the messages to display
   */
  public void showWarningMessages(final List<String> messages) {

    final StringBuilder sb = new StringBuilder();

    for (String message : messages) {
      sb.append(message).append(LINE_SEPARATOR);
    }

    showWarningMessage(sb.toString());
  }

  /**
   * Displays an information message.
   * 
   * @param message
   *          the message to display
   */
  public void showInformationMessage(final String message) {
    JOptionPane.showMessageDialog(window, message, getTitle(), JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays some information messages, every message on a line.
   * 
   * @param messages
   *          the messages to display
   */
  public void showInformationMessages(final List<String> messages) {

    final StringBuilder sb = new StringBuilder();

    for (String message : messages) {
      sb.append(message).append(LINE_SEPARATOR);
    }

    showInformationMessage(sb.toString());
  }

  /**
   * Displays a message near the tray icon.
   * 
   * @param message
   *          the message to display
   */
  public void showTrayMessage(final String message) {
    showTrayMessage(getTitle(), message);
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
  public void showTrayMessage(final String title, final String message) {
    showTrayMessage(title, message, null);
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
  public void showTrayMessage(final String title, final String message, final ActionListener actionListener) {
    new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.NONE, actionListener);
  }

  /**
   * Displays an error message near the tray icon.
   * 
   * @param message
   *          the message to display
   */
  public void showTrayErrorMessage(final String message) {
    showTrayErrorMessage(getTitle(), message);
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
  public void showTrayErrorMessage(final String title, final String message) {
    new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.ERROR);
  }

  /**
   * Displays a warning message near the tray icon.
   * 
   * @param message
   *          the message to display
   */
  public void showTrayWarningMessage(final String message) {
    showTrayWarningMessage(getTitle(), message);
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
  public void showTrayWarningMessage(final String title, final String message) {
    new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.WARNING);
  }

  /**
   * Displays an information message near the tray icon.
   * 
   * @param message
   *          the message to display
   */
  public void showTrayInformationMessage(final String message) {
    showTrayInformationMessage(getTitle(), message);
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
  public void showTrayInformationMessage(final String title, final String message) {
    new AppTrayMessageWindow(this, title, message, AppTrayMessageWindow.MessageType.INFORMATION);
  }

  /**
   * Provides a simple mechanism for the user to select a file.
   * 
   * @param currentDirectory
   *          the current directory to point to
   */
  public File selectFile(final File currentDirectory) {
    return select(currentDirectory, JFileChooser.FILES_ONLY);
  }

  /**
   * Provides a simple mechanism for the user to select a directory.
   * 
   * @param currentDirectory
   *          the current directory to point to
   */
  public File selectDirectory(final File currentDirectory) {
    return select(currentDirectory, JFileChooser.DIRECTORIES_ONLY);
  }

  /**
   * Provides a simple mechanism for the user to select a file or a directory.
   * 
   * @param currentDirectory
   *          the current directory to point to
   */
  public File selectFileOrDirectory(final File currentDirectory) {
    return select(currentDirectory, JFileChooser.FILES_AND_DIRECTORIES);
  }

  private File select(final File currentDirectory, final int fileSelectionMode) {

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

  private JPopupMenu getWindowPopup(final Point location) {

    //
    // hide

    final JMenuItem hideItem = new JMenuItem(getText("popup.hide"));
    hideItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        hide();
      }
    });

    //
    // send to back

    final JMenuItem sendToBackItem = new JMenuItem(getText("popup.send_to_back"));
    sendToBackItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        sendToBack();
      }
    });

    //
    // always on top

    final JCheckBoxMenuItem alwaysOnTopItem = new JCheckBoxMenuItem(getText("popup.always_on_top"), isAlwaysOnTop());
    alwaysOnTopItem.addItemListener(new ItemListener() {
      public void itemStateChanged(final ItemEvent e) {
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
      public void stateChanged(final ChangeEvent e) {
        setTransparency(transparencySlider.getValue());
      }
    });

    final JMenu transparencyItem = new JMenu(getText("popup.transparency"));
    transparencyItem.add(transparencySlider);

    //
    // close

    final JMenuItem closeItem = new JMenuItem(getText("popup.close"), getIcon("close-016.png"));
    closeItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        System.exit(0);
      }
    });

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

    MenuItem openItem = new MenuItem(getText("popup.open") + " " + getTitle());
    openItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        show();
      }
    });

    MenuItem hideItem = new MenuItem(getText("popup.hide") + " " + getTitle());
    hideItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        hide();
      }
    });

    CheckboxMenuItem hideWhenMinimizedItem = new CheckboxMenuItem(getText("popup.hide_when_minimized"), isHideWhenMinimized());
    hideWhenMinimizedItem.addItemListener(new ItemListener() {
      public void itemStateChanged(final ItemEvent e) {
        setHideWhenMinimized(e.getStateChange() == ItemEvent.SELECTED);
      }
    });

    MenuItem sendToBackItem = new MenuItem(getText("popup.send_to_back"));
    sendToBackItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        sendToBack();
      }
    });

    MenuItem closeItem = new MenuItem(getText("popup.close"));
    closeItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
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
