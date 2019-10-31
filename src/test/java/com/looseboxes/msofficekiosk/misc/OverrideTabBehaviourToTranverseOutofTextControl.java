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

package com.looseboxes.msofficekiosk.misc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2019 10:20:20 AM
 */
public class OverrideTabBehaviourToTranverseOutofTextControl {

    public static void main(String [] args) {
            Display display = new Display();
            Shell shell = new Shell(display);
            shell.setBounds(10,10,200,200);
            Text text1 = new Text(shell, SWT.MULTI | SWT.WRAP);
            Rectangle clientArea = shell.getClientArea();
            text1.setBounds(clientArea.x+10,clientArea.y+10,150,50);
            text1.setText("Tab will traverse out from here.");
            text1.addTraverseListener(e -> {
                    if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                            e.doit = true;
                    }
            });
            Text text2 = new Text(shell, SWT.MULTI | SWT.WRAP);
            text2.setBounds(10,100,150,50);
            text2.setText("But Tab will NOT traverse out from here (Ctrl+Tab will).");
            shell.open();
            while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) display.sleep();
            }
            display.dispose();
    }
}
