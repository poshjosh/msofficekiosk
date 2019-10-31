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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2018 2:50:36 PM
 */
public class LayoutSample {
     
   public static void main(String[] args) {
      final Display display = new Display();
      final Shell shell = new Shell(display);
  
      // pos x, pos y, width, height
      shell.setBounds(200, 200, 400, 200);
      shell.setText("SWT Shell Demonstration");
      shell.setLayout(new GridLayout());
  
//      final Group group = new Group(shell, SWT.NONE);
//      GridLayout gridLayout = new GridLayout();
//      gridLayout.numColumns = 3;
//      group.setLayout(gridLayout);
//      group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
//      final Button createShellButton = new Button(group, SWT.PUSH);
//      createShellButton.setText("Exit");
      
      final Label top = new Label(shell, SWT.NONE);
      top.setText("Top");
      top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      
      final Label center = new Label(shell, SWT.BORDER_SOLID);
      center.setText("Center");
      center.setLayoutData(new GridData(GridData.FILL_BOTH));
        final Color colorWhite = display.getSystemColor(SWT.COLOR_WHITE);
        center.setBackground(colorWhite);
  
//      SelectionListener selectionListener = new SelectionAdapter () {
//         @Override
//         public void widgetSelected(SelectionEvent event) {
//             System.exit(0);
//         };
//      };
        
//      createShellButton.addSelectionListener(selectionListener);
        
      shell.open();
  
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
   }
}
