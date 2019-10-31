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

import java.awt.Component;
import java.io.File;
import java.util.Objects;
import java.util.function.Supplier;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2018 12:18:10 PM
 */
public class FileSupplierFromUserSelection implements Supplier<File> {
    
    private static File lastChosen;
    
    private final Component parent;
    
    private final FileFilter fileFilter;

    public FileSupplierFromUserSelection(FileFilter fileFilter) {
        this(null, fileFilter);
    }
    
    public FileSupplierFromUserSelection(Component parent, FileFilter fileFilter) {
        this.parent = parent;
        this.fileFilter = Objects.requireNonNull(fileFilter);
    }
    
    @Override
    public File get() {
        final JFileChooser chooser = new JFileChooser();
        final String description = "Select " + fileFilter.getDescription() + " to Open";
        chooser.setApproveButtonText("Open");
        chooser.setApproveButtonToolTipText(description);
        chooser.setCurrentDirectory(this.getDefaultDirectory());
        chooser.setDialogTitle(description);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileFilter(fileFilter);
        chooser.setFileHidingEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(false);
        final int state = chooser.showOpenDialog(parent);  
        switch(state) {
            case JFileChooser.APPROVE_OPTION:
            lastChosen = chooser.getSelectedFile();
            break;
            default:    
        }
        return lastChosen;
    }
    
    public File getDefaultDirectory() {
        final File output;
        if(lastChosen != null) {
            if(lastChosen.isDirectory()) {
                output = lastChosen;
            }else{
                output = lastChosen.getParentFile();
            }
        }else{
            output = new File(System.getProperty("user.home"));
        }
        return output;
    }
}
