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

import java.util.Objects;
import java.util.function.Function;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 2:26:50 PM
 */
public class ShowMessage implements Function<String, Integer> {
    
    private final MessageBox messageBox;
    
    public ShowMessage(Shell shell, String title) {
        this(shell, title, SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    }
    
    public ShowMessage(Shell shell, String title, int style) {
        Objects.requireNonNull(shell);
        messageBox = new MessageBox (shell, style); 
        shell.getDisplay().syncExec(() -> {
            if(title != null) {
                messageBox.setText(title); 
            }
        });
    }

    @Override
    public Integer apply(String message) {
        this.setMessage(message);
        return messageBox.open(); 
    }
    
    public void setMessage(String message) {
        messageBox.getParent().getDisplay().syncExec(() -> {
            this.messageBox.setMessage(message);
        });
    }
}
