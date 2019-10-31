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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 9:18:13 PM
 */
public class SwtInSwing {

    private static final Logger LOG = Logger.getLogger(SwtInSwing.class.getName());

    private final Display display;
    
    private final Dimension dimension;
    
    private Shell shell;
            
    private JFrame frame;
    
    private Canvas canvas;

    public static void main(String [] args) {
        
        final File file = Paths.get(System.getProperty("user.home"), "Desktop", "MESSAGES.docx").toFile();
        
        final Display display = new Display();
        
        final SwtInSwing sis = new SwtInSwing(display, new Dimension(850, 650));
        
        java.awt.EventQueue.invokeLater(() -> {
            sis.show(file);
        });
        
        sis.waitTillDisposed();
        
        display.dispose();
//        System.exit(0);
    }
    
    public SwtInSwing() {
        this(new Display(), new Dimension(850, 650));
    }
    
    public SwtInSwing(Display display, Dimension dimension) {
        this.display = Objects.requireNonNull(display);
        this.dimension = Objects.requireNonNull(dimension);
        this.display.addListener(SWT.CLOSE, new Listener() {
            public void handleEvent(Event event) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try{
                            if(frame != null) {
                                frame.dispose();
                            }
                            if(shell != null && !shell.isDisposed()) {
                                if(display.isDisposed()) {
                                    display.asyncExec(() -> shell.dispose());
                                }
                            }
                        }catch(RuntimeException e) {
                            LOG.log(Level.WARNING, null, e);
                        }
                    }
                });
            }
        });
    }

    public void show(File file) {
        
        show(file, "Displaying: " + file.getName(), Boolean.TRUE);
    }
    
    public void show(File file, String frameTitle, Boolean readOnly) {
        
        Objects.requireNonNull(readOnly);
        
        frame = new JFrame(frameTitle);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(shell != null && !shell.isDisposed()) {
                    if(display.isDisposed()) {
                        display.asyncExec(() -> shell.dispose());
                    }
                }
            }
        });

        frame.setSize(dimension);

        canvas = new Canvas();
        
        canvas.setSize(dimension);

        frame.getContentPane().add(canvas);

        frame.pack();
        frame.setVisible(true);

        display.asyncExec(() -> {
            
            try {
                
                final FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.ALL);
                shell = SWT_AWT.new_Shell(display, canvas);
                shell.setLayout(thisLayout);
                shell.setSize(dimension.width-50, dimension.height-50);

                final OleFrame oleFrame = new OleFrame(shell, SWT.NONE);
                
                OleClientSite clientSite = new OleClientSite(oleFrame, SWT.NULL, file);
//                clientSite.setBounds(0, 0, 104, 54);
                clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
                
//                new MakeReadOnly().accept(clientSite, readOnly);
                
                LOG.fine("Complete process OLE Client Site");
                
                shell.layout();

                shell.open();
                
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Create OleClientSite Error", e);
            }
        });
    }
    
    public void waitTillDisposed() {
        while (!display.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    public Display getDisplay() {
        return display;
    }

    public Optional<Shell> getShellOptional() {
        return Optional.ofNullable(shell);
    }
    
    public Optional<JFrame> getFrameOptional() {
        return Optional.ofNullable(frame);
    }

    public Optional<Canvas> getCanvasOptional() {
        return Optional.ofNullable(canvas);
    }

    public Dimension getDimension() {
        return dimension;
    }
}
