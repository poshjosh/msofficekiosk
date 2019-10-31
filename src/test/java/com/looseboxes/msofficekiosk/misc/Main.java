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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2018 9:50:19 AM
 */
public class Main {
   static Display display;
   static Shell shell;
   static Color color;
     
   public static void main(String[] args) throws Exception {

       if(true) {
           
           final String userHome = System.getProperty("user.home");
           final Path appHome = Paths.get(userHome, "mswordbox");
           System.out.println("User home: " + userHome + "\nApp home: " + appHome);
           System.out.println("AppHome.relativize(userHome)" + appHome.relativize(Paths.get(userHome)));
           System.out.println("AppHome.resolve('config')" + appHome.resolve("config"));
           System.out.println("AppHome.resolveSibling('config')" + appHome.resolveSibling("config"));
           System.out.println("AppHome.resolve('mswordbox')" + appHome.resolve("mswordbox"));
           System.out.println("AppHome.resolveSibling('mswordbox')" + appHome.resolveSibling("mswordbox"));
           
           return;
       }
     
      display = new Display();
      shell = new Shell(display);
  
      // pos x, pos y, width, height
      shell.setBounds(200, 200, 400, 200);
      shell.setText("SWT Shell Demonstration");
      shell.setLayout(new GridLayout());
  
      final Group buttonGroup = new Group(shell, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      buttonGroup.setLayout(gridLayout);
      buttonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
      final Button createShellButton = new Button(buttonGroup, SWT.PUSH);
      createShellButton.setText("Open Shell");
  
      SelectionListener selectionListener = new SelectionAdapter () {
         @Override
         public void widgetSelected(SelectionEvent event) {
            final Shell childShell = new Shell(SWT.RESIZE | SWT.CLOSE | SWT.ON_TOP);
            Button closeButton = new Button(childShell, SWT.PUSH);
            closeButton.setBounds(10, 10, 100, 30);
            closeButton.setText("Close Shell");
            closeButton.addListener(SWT.Selection, new Listener() {
               @Override
               public void handleEvent(Event event) {
                  childShell.dispose();
               }
            });
           
            childShell.setSize (300, 100);
            childShell.setText ("Title of Shell");
            childShell.open ();                 
         };
      };
        
      createShellButton.addSelectionListener(selectionListener);
        
      shell.open();
  
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      if (color != null && !color.isDisposed()) {
         color.dispose();
      }
      display.dispose();
   }
}
