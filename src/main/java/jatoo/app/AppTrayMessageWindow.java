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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.UIManager;

import jatoo.ui.UIUtils;

/**
 * Tray message window.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 2.1, July 3, 2014
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

  AppTrayMessageWindow(final App app, final JComponent message, final MessageType messageType) {
    init(app, message, messageType);
  }

  AppTrayMessageWindow(final App app, final String title, final String message, final MessageType messageType, final ActionListener actionListener) {

    JTextArea messageComponent = new JTextArea(message);
    messageComponent.setEditable(false);
    messageComponent.setOpaque(false);

    JScrollPane messageComponentScrollPane = new JScrollPane(messageComponent);
    messageComponentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    messageComponentScrollPane.setBorder(BorderFactory.createEmptyBorder());

    JButton closeButton = new JButton(app.getIcon("close-016.png"));
    closeButton.setFocusable(false);
    closeButton.setMargin(new Insets(5, 5, 5, 5));
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeMessageWindow();
      }
    });

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
      public void mousePressed(final MouseEvent e) {

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

  AppTrayMessageWindow(final App app, final String title, final String message, final MessageType messageType) {
    this(app, title, message, messageType, null);
  }

  private void init(final App app, final JComponent message, final MessageType messageType) {

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
      public void windowClosing(final WindowEvent e) {
        isClosed = true;
      }
    });

    //
    // close on escape

    UIUtils.setActionForEscapeKeyStroke(getRootPane(), new AbstractAction() {
      public void actionPerformed(final ActionEvent e) {
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

  private static Icon getIconFromOptionPane(final MessageType messageType) {

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

    private static final List<AppTrayMessageWindow> WINDOWS = new ArrayList<>();

    private static synchronized void add(final AppTrayMessageWindow window) {
      WINDOWS.add(window);
      updateLocations();
    }

    private static synchronized void remove(final AppTrayMessageWindow window) {
      WINDOWS.remove(window);
      updateLocations();
    }

    private static void updateLocations() {

      Rectangle maxWinBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

      int y = 0;

      for (AppTrayMessageWindow window : WINDOWS) {

        y += window.getHeight();

        window.setLocation(maxWinBounds.x + maxWinBounds.width - window.getWidth(), maxWinBounds.y + maxWinBounds.height - y);
      }
    }
  }

}
