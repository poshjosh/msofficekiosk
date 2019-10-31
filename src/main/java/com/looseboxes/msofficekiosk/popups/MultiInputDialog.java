/*
 * Copyright 2018 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.popups;

import com.bc.config.Config;
import java.awt.Component;
import java.awt.Container;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 21, 2018 7:24:14 PM
 */
public class MultiInputDialog extends MultiInputWindow {

//    private transient static final Logger LOG = Logger.getLogger(MultiInputDialog.class.getName());

    private final Component parentComponent;
    
    public MultiInputDialog(Config<Properties> config) {
        this(config, null);
    }

    public MultiInputDialog(Config<Properties> config, Component parentComponent) {
        super(config, false);
        this.parentComponent = parentComponent;
    }

    @Override
    public void show(Container message, String title) {
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.PLAIN_MESSAGE);
    }
}
