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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2018 1:27:09 PM
 */
public class ShowWarningMessage extends ShowMessage {

    public ShowWarningMessage(Shell shell) {
        this(shell, "Warning");
    }
    
    public ShowWarningMessage(Shell shell, String title) {
        this(shell, title, SWT.APPLICATION_MODAL | SWT.OK);
    }

    public ShowWarningMessage(Shell shell, String title, int style) {
        super(shell, title, style);
    }
}