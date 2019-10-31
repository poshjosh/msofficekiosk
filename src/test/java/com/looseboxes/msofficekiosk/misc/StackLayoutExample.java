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

package com.looseboxes.msofficekiosk.misc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 12:18:22 PM
 */
public class StackLayoutExample {

        public static void main(String[] args) {
            
                Display display = new Display();
                Shell shell = new Shell(display);
                shell.setLayout(new GridLayout());
        
                final Composite parent = new Composite(shell, SWT.NONE);
                parent.setLayoutData(new GridData(GridData.FILL_BOTH));
                final StackLayout layout = new StackLayout();
                parent.setLayout(layout);
                final Button[] bArray = new Button[10];
                for (int i = 0; i < 10; i++) {
                        bArray[i] = new Button(parent, SWT.PUSH);
                        bArray[i].setText("Button "+i);
                }
                layout.topControl = bArray[0];
        
                Button b = new Button(shell, SWT.PUSH);
                b.setText("Show Next Button");
                final int[] index = new int[1];
                b.addListener(SWT.Selection, new Listener(){
                        public void handleEvent(Event e) {
                                index[0] = (index[0] + 1) % 10;
                                layout.topControl = bArray[index[0]];
                                parent.layout();
                        }
                });
        
                shell.open();
                while (shell != null && !shell.isDisposed()) {
                        if (!display.readAndDispatch())
                                display.sleep(); 
                }       
        }
}
