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

package com.looseboxes.msofficekiosk.ui;

import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2019 6:46:14 PM
 */
public interface MessageDialog {

    default void showWarningMessage(String message) {
        this.showMessage(JOptionPane.WARNING_MESSAGE, message);
    }
    
    default void showInformationMessage(String message) {
        this.showMessage(JOptionPane.INFORMATION_MESSAGE, message);
    }
    
    void showMessage(int messageType, String message);
}
