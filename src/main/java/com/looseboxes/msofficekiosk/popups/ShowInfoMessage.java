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

import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 2:30:00 PM
 */
public class ShowInfoMessage extends ShowMessage {

    public ShowInfoMessage(Shell shell) {
        this(shell, "Information");
    }
    
    public ShowInfoMessage(Shell shell, String title) {
        super(shell, title);
    }

    public ShowInfoMessage(Shell shell, String title, int style) {
        super(shell, title, style);
    }
}
