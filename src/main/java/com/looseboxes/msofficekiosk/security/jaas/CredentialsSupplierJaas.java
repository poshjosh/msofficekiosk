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

package com.looseboxes.msofficekiosk.security.jaas;

import com.bc.jaas.callbacks.CredentialsFromUserPrompt;
import java.awt.Component;
import java.awt.Container;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2018 10:41:28 PM
 */
public class CredentialsSupplierJaas extends CredentialsFromUserPrompt {

    private transient static final Logger LOG = Logger.getLogger(CredentialsSupplierJaas.class.getName());

    public CredentialsSupplierJaas(Component parentComponent, String title) {
        super(parentComponent, title);
    }

    public CredentialsSupplierJaas(Component parentComponent, String title, 
            ComponentModel<Component, String> userCompModel, ComponentModel<JPasswordField, char[]> passCompModel) {
        super(parentComponent, title, userCompModel, passCompModel);
    }

    @Override
    public void show(Component parentComponent, Container ui, String title, int messageType) {

        LOG.fine(() -> "Displaying logging prompt with title: " + title);

        JOptionPane.showMessageDialog(parentComponent, ui, title, messageType);
    }
}
