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

package com.looseboxes.msofficekiosk.ui.admin;

import com.looseboxes.msofficekiosk.functions.MakeReadOnly;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 26, 2018 4:18:38 PM
 */
public class FileUi {

    private static final Logger LOG = Logger.getLogger(FileUi.class.getName());
    
    private final Shell shell;

    private final File file;
    
    private final Boolean readOnly;

    private final OleFrame oleFrame;
    
    private final OleClientSite oleClientSite;
    
    private final boolean internalDisplay;

    public FileUi(File file, Boolean readOnly) {
        this(new Shell(new Display()), file, readOnly, true);
    }

    public FileUi(Shell shell, File file, Boolean readOnly) {
        this(shell, file, readOnly, false);
    }
    
    protected FileUi(Shell shell, File file, Boolean readOnly, boolean internalDiplay) {
        this.shell = Objects.requireNonNull(shell);
        this.file = Objects.requireNonNull(file);
        this.readOnly = Objects.requireNonNull(readOnly);
        this.internalDisplay = internalDiplay;

        shell.setText(" Viewing -> " + file.getName());
        shell.setLayout(new FillLayout());

        this.oleFrame = new OleFrame(shell, SWT.NONE);

        this.oleClientSite = new OleClientSite(oleFrame, SWT.NULL, file);
        oleClientSite.setLayout(new FillLayout());
        oleClientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
        new MakeReadOnly().accept(oleClientSite, readOnly);
        LOG.log(Level.FINE, "Done creating {0}", this);
    }

    public void show() {
        
        final Display display = shell.getDisplay();
        
        try{

            shell.open();

            while (!shell.isDisposed ()) {
                if (!display.readAndDispatch()) {
                    display.sleep ();
                }
            }
        }finally{
            if(internalDisplay) {
                display.close();
                display.dispose();
            }
        }
    }

    public Shell getShell() {
        return shell;
    }

    public File getFile() {
        return file;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public OleFrame getOleFrame() {
        return oleFrame;
    }

    public OleClientSite getOleClientSite() {
        return oleClientSite;
    }
}
