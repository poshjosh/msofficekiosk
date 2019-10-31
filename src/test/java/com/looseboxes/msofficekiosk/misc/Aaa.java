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

import com.bc.ui.UIContext;
import com.bc.ui.UIContextImpl;
import java.awt.Container;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 26, 2018 12:44:28 PM
 */
public class Aaa {

    public static void main(String [] args) {
        
        final Display display = Display.getDefault();
        final Shell h = new Shell(display);
        
        final Event event = new Event();

//        event.keyCode=SWT.ARROW_DOWN;
        event.type=SWT.ARROW;

        event.display=Display.getDefault();

        for(int i=0; i<10; i++) {
            event.keyCode=SWT.ARROW_DOWN;
            Display.getDefault().post(event);
            event.keyCode=SWT.ARROW_UP;
            Display.getDefault().post(event);
        }
        
        if(true) {
            return;
        }
                
        final TableCursor cursor = new TableCursor(null, SWT.NONE);
//        org.eclipse.swt.widgets
        Display d; 
//        d.getCursorControl();
        
        Control c;
//        c.addKeyListener(???);
        Shell k; 
        
        if(true) {
            return;
        }

        Aaa aaa = new Aaa();
        
        aaa.createFrame(new JTextArea(), true);

        aaa.createFrame(new JEditorPane(), false);
    }
    
    private JFrame createFrame(Container child, boolean left) {
        
        final JFrame frame = new JFrame(child.getClass().getName());
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        final JScrollPane scrolls = new JScrollPane(child);

        frame.getContentPane().add(scrolls);
        
        final UIContext context = new UIContextImpl();
        
        if(left) {
            context.positionHalfScreenLeft(child);
            context.positionHalfScreenLeft(scrolls);
            context.positionHalfScreenLeft(frame);
        }else{
            context.positionHalfScreenRight(child);
            context.positionHalfScreenRight(scrolls);
            context.positionHalfScreenRight(frame);
        }
        
        frame.pack();
        
        frame.setVisible(true);
        
        return frame;
    }
}
