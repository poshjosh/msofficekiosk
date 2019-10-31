/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.looseboxes.msofficekiosk.examples;

import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2019 9:46:26 PM
 */
public class JListExample {

    public static void main(String... args) {
    
        final Object [] arr = {"Option A", "Option B", "Option C"};

        final JList jList = new JList(arr);
        
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final Font font = Font.decode("Arial-PLAIN-28");
        jList.setFont(font);
        final JScrollPane pane = new JScrollPane(jList);
//        pane.setFont(font);
//        JOptionPane.getRootFrame().setFont(font);
        final ImageIcon icon = null;
        JOptionPane.showMessageDialog(null, pane, "Select Test to Edit", JOptionPane.PLAIN_MESSAGE, icon);
    }
}
