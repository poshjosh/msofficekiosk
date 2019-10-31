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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Chinomso Bassey Ikwuagwu on May 12, 2018 5:28:34 PM
 */
public class InputDialog extends Dialog {
  
  private String value;

  /**
   * @param parent
   */
  public InputDialog(Shell parent) {
    super(parent);
  }

  /**
   * @param parent
   * @param style
   */
  public InputDialog(Shell parent, int style) {
    super(parent, style);
  }

  /**
   * Makes the dialog visible.
   * 
   * @return
   */
  public String open() {
      return this.open(SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
  }
  
  public String open(int style) {
    final Shell parent = getParent();
    final Shell shell = new Shell(parent, style);
    shell.setText("Name Dialog");

    shell.setLayout(new GridLayout(2, true));

    Label label = new Label(shell, SWT.NULL);
    label.setText("Enter a name: ");

    final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);

    final Button buttonOK = new Button(shell, SWT.PUSH);
    buttonOK.setText("Ok");
    buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    Button buttonCancel = new Button(shell, SWT.PUSH);
    buttonCancel.setText("Cancel");

    text.addListener(SWT.Modify, new Listener() {
      @Override
      public void handleEvent(Event event) {
        try {
          value = text.getText();
          buttonOK.setEnabled(true);
        } catch (Exception e) {
          buttonOK.setEnabled(false);
        }
      }
    });

    buttonOK.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        shell.dispose();
      }
    });

    buttonCancel.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        value = null;
        shell.dispose();
      }
    });
    
    shell.addListener(SWT.Traverse, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if(event.detail == SWT.TRAVERSE_ESCAPE)
          event.doit = false;
      }
    });

    text.setText("");
    shell.pack();
    shell.open();

    final Display display = parent.getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }  
    }

    return value;
  }
}