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

import com.looseboxes.msofficekiosk.popups.ShowInfoMessageViaShell;
import com.looseboxes.msofficekiosk.popups.ShowWarningMessageViaShell;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2019 6:55:36 PM
 */
public class SwtMessageDialog implements MessageDialog {

    private static final Logger LOG = Logger.getLogger(SwtMessageDialog.class.getName());

    private final Display display;

    public SwtMessageDialog(Display display) {
        this.display = Objects.requireNonNull(display);
    }
    
    @Override
    public void showMessage(int messageType, String message) {

        final Shell activeShell = display.getActiveShell();

        if(activeShell == null || activeShell.isDisposed()) {

            LOG.log(Level.WARNING, "Creating new shell to display message: {0}", message);
            final Shell shell = new Shell(display);
            try{
                getMessageDisplay(messageType).accept(shell, message);
            }finally{
                shell.close();
                shell.dispose();
            }
        }else{
            getMessageDisplay(messageType).accept(activeShell, message);
        }
    }
    
    private BiConsumer<Shell, String> getMessageDisplay(int messageType) {
        final BiConsumer<Shell, String> result;
        switch(messageType) {
            case JOptionPane.WARNING_MESSAGE:
                result = new ShowWarningMessageViaShell(display); break;
            default:
                result = new ShowInfoMessageViaShell(display);
        }
        return result;
    }
}
