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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.JFrame;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 8:29:18 PM
 */
public class AsimpleWordInJFrame {
    
    public static void main(String [] args) {
        
        final File file = Paths.get(System.getProperty("user.home"), "Desktop", "MESSAGES.docx").toFile();
        
        new AsimpleWordInJFrame().init(file);
    }

    private JFrame init(File file) {
        
        final Dimension size = new Dimension(800, 600);

        final Display display = new Display();
        final Shell shell = new Shell(display);
        
        final JFrame jframe = new JFrame(file.getName());
        final Canvas canvas = new Canvas();
        jframe.getContentPane().add(canvas);
        jframe.setSize(size);
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jframe.setVisible(true);
        
        display.asyncExec(new Runnable(){
            @Override
            public void run() {
                
                final Shell shell = SWT_AWT.new_Shell(display, canvas);
                shell.setSize(size.width, size.height);
                shell.setLayout(new FillLayout());

                final OleFrame oleFrame = new OleFrame(shell, SWT.NONE);
                final OleClientSite clientSite;
                if(file.exists()) {
//                    clientSite = new OleClientSite(oleFrame, SWT.NONE, "Word.Document", file);
                    clientSite = new OleClientSite(oleFrame, SWT.NULL, file);
                }else{
                    clientSite = new OleClientSite(oleFrame, SWT.NONE, "Word.Document");
                }

//                clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);

                final boolean withoutSettingFocusClientSiteDidNotDisplay = true;

                if(withoutSettingFocusClientSiteDidNotDisplay) {
//                    clientSite.setFocus();
                }

                shell.open();
            }
        });

        display.addListener(SWT.CLOSE, new Listener() {
            public void handleEvent(Event event) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        jframe.dispose();
                    }
                });
            }
        });
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }    
        }
        
        display.dispose();
        
        return jframe;
    }
}
