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

package com.looseboxes.msofficekiosk.functions.admin;

import com.looseboxes.msofficekiosk.ui.admin.FileUi;
import com.looseboxes.msofficekiosk.ui.selection.AbstractSelectionAction;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 26, 2018 2:48:13 PM
 */
public class OpenFilesOleFrame extends AbstractSelectionAction<File> {

    private transient static final Logger LOG = Logger.getLogger(OpenFilesOleFrame.class.getName());
    
    private final Display display;
    
    private final Boolean readOnly;
    
    private final boolean internalDisplay;

    public OpenFilesOleFrame() {
        this(new Display(), Boolean.TRUE, true);
    }
    
    public OpenFilesOleFrame(Display display) {
        this(display, Boolean.TRUE, false);
    }
    
    public OpenFilesOleFrame(Boolean readOnly) {
        this(new Display(), readOnly, true);
    }
    
    public OpenFilesOleFrame(Display display, Boolean readOnly) {
        this(display, readOnly, false);
    }
    
    protected OpenFilesOleFrame(Display display, Boolean readOnly, boolean internalDisplay) {
        super(readOnly ? "Open File(s) - Read Only" : "Open File(s)");
        this.display = Objects.requireNonNull(display);
        this.readOnly = Objects.requireNonNull(readOnly);
        this.internalDisplay = internalDisplay;
    }

    @Override
    public Boolean apply(File file) {
        
        LOG.log(Level.FINER, "Preparing to display file: {0}", file);
        
        try{

            LOG.log(Level.FINE, "Displaying file: {0}", file);

            display.syncExec(() -> {
                try{
                    final Shell shell = new Shell(display);
                    createFileUi(shell, file, readOnly).show();
                    LOG.log(Level.FINER, "Done displaying file: {0}", file);
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            });

            return Boolean.TRUE;

        }catch(RuntimeException e) {

            LOG.log(Level.WARNING, "Failed to display file: " + file, e);
            
            return Boolean.FALSE;
        }
    }
    
    public FileUi createFileUi(Shell shell, File file, Boolean readOnly) {
        return new FileUi(shell, file, readOnly);
    }
}
