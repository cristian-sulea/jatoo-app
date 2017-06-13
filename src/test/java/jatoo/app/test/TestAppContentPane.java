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

package jatoo.app.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;

import jatoo.app.App;

/**
 * Test App Content Pane.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.2, September 13, 2016
 */
@SuppressWarnings("serial")
public class TestAppContentPane extends JPanel {

  public TestAppContentPane(final App app) {

    app.getLogger().debug("debug");
    app.getLogger().info("info");
    app.getLogger().warn("warn");
    app.getLogger().error("error");
    app.getLogger().fatal("fatal");

    JButton showTrayMessage = new JButton("Show Tray Message");
    showTrayMessage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        app.showMessage("xxx");
        app.showMessages(Arrays.asList("xxx", "yyy"));
        
//        app.showTrayMessage("1 ncerc sa configurez un system de test pentru Migros. POS", "1 mesPOS-ul face restart la linia din system.log:" + System.getProperty("line.separator") + "xxx �lisysld: System Shutdown requested by 00000 Loc 011�sage");
//        app.showTrayMessage("1 mesPOS-ul face restart la linia din system.log:" + System.getProperty("line.separator") + "xxx �lisysld: System Shutdown requested by 00000 Loc 011�sage");
      }
    });

    JButton showTrayInformationMessage = new JButton("Show Information Tray Message");
    showTrayInformationMessage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        app.showTrayInformationMessage("22 ncerc sa configurez un system de test pentru Migros.", "22 mesPOS-ul face restart la .log:\n�lisysld: requested by 00000 Loc 011�sage");
      }
    });

    JButton showTrayWarningMessage = new JButton("Show Warning Tray Message");
    showTrayWarningMessage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        app.showTrayWarningMessage("333 ncerc sa configurez un system de test pentru Migros. POS", "333 mesPOS-ul face restart la linia din system.log:\n�lisysld: System Shutdown requested by 00000 Loc 011�sage");
      }
    });

    JButton showTrayErrorMessage = new JButton("Show Error Tray Message");
    showTrayErrorMessage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        app.showTrayErrorMessage("4444 ncerc sa configurez un system de test pentru Migros.", "4444 mesPOS-ul face restart la .log:\n�lisysld: requested by 00000 Loc 011�sage");
      }
    });

    add(showTrayMessage);
    add(showTrayInformationMessage);
    add(showTrayWarningMessage);
    add(showTrayErrorMessage);

    JButton pack = new JButton("pack()");
    pack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        app.getWindow().pack();
      }
    });

    add(pack);
  }

}
