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

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2018 12:55:39 PM
 */
public class MSWordInJFrame {
    
    static OleClientSite clientSite;
    static OleFrame frame;

    public static void main(final String[] args) {
        
        final Display display = new Display();
        
        final Shell shell = new Shell(display, SWT.NONE);
        shell.setMaximized(true);
//        shell.setFullScreen(true);
        
//        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//        System.out.println("Screen size: " + screenSize);
//        shell.setSize(1366, 768);
        shell.setText("Word Example");
        shell.setLayout(new FillLayout());

        try {
            frame = new OleFrame(shell, SWT.NONE);
            
            // esto abre un documento existente
            // clientSite = new OleClientSite(frame, SWT.NONE, new File("Doc1.doc"));
            // esto abre un documento en blanco
            clientSite = new OleClientSite(frame, SWT.NONE, "Word.Document");

            addFileMenu(frame);
            System.out.println(" I am in run method ");

        } catch (final SWTError e) {
            System.out.println("Unable to open activeX control");
            display.dispose();
            return;
        }

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    static void addFileMenu(OleFrame frame) {
        final Shell shell = frame.getShell();
        Menu menuBar = shell.getMenuBar();
        if (menuBar == null) {
            menuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(menuBar);
        }
        MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
        fileMenu.setText("&File");
        Menu menuFile = new Menu(fileMenu);
        fileMenu.setMenu(menuFile);
        frame.setFileMenus(new MenuItem[] { fileMenu });

        MenuItem menuFileOpen = new MenuItem(menuFile, SWT.CASCADE);
        menuFileOpen.setText("Open...");
        menuFileOpen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fileOpen();
            }
        });
        MenuItem menuFileExit = new MenuItem(menuFile, SWT.CASCADE);
        menuFileExit.setText("Exit");
        menuFileExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });
    }

    static void fileOpen() {
        FileDialog dialog = new FileDialog(clientSite.getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*.doc" });
        String fileName = dialog.open();
        if (fileName != null) {
            clientSite.dispose();
            clientSite = new OleClientSite(frame, SWT.NONE, "Word.Document", new File(fileName));
            clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
        }
    }
    
    static void printMenuItems(Object prefix, MenuItem [] menuItems, Object suffix) {
        System.out.println(prefix);
        if(menuItems != null) {
            for(MenuItem menuItem : menuItems) {
                System.out.println(menuItem.toString());
            }
        }
        System.out.println(suffix);
    }
}
