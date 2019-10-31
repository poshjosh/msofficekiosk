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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2019 10:25:54 AM
 */
public class ProgrammaticKeyEvent {

    public static void main(String [] args) {
        
            Display display = new Display();
            Shell shell = new Shell(display);
            shell.setBounds(10,10,500,500);
            Text text1 = new Text(shell, SWT.MULTI | SWT.WRAP);
            Rectangle clientArea = shell.getClientArea();
            text1.setBounds(clientArea.x+30,clientArea.y+30,450,450);
            text1.setText("Tab will traverse out from here.\nTab will traverse out from here.\nTab will traverse out from here.\nTab will traverse out from here.\nTab will traverse out from here.");
            shell.pack();

            System.out.println("Displaying");
            shell.open();
            
            new Thread() {
                public void run() {
                    final Event event = new Event();

            //        event.keyCode=SWT.ARROW_DOWN;
//                    event.type=SWT.ARROW;

                    event.display=display;

                    System.out.println("Eventing");

                    for(int i=0; i<10; i++) {
                        event.type=SWT.ARROW_DOWN;
                        event.keyCode=SWT.ARROW_DOWN;
                        display.post(event);
                        event.type=SWT.ARROW_UP;
                        event.keyCode=SWT.ARROW_UP;
                        display.post(event);
                    }

                    System.out.println("Done eventing");
                }
            }.start();

            while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) display.sleep();
            }
            display.dispose();
    }
}
