/*
 * Copyright 2019 NUROX Ltd.
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

import com.looseboxes.msofficekiosk.ui.selection.AbstractSelectionAction;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on May 13, 2019 9:44:38 PM
 */
public class OpenFilesNative extends AbstractSelectionAction<File> {

    private static final Logger LOG = Logger.getLogger(OpenFilesNative.class.getName());

    private final boolean displayLinkIfActionNotSupported;
    
    private Desktop desktop;
    
    public OpenFilesNative() {
        this(false);
    }
    public OpenFilesNative(boolean displayLinkIfActionNotSupported) {
        super("Open File(s)");
        this.displayLinkIfActionNotSupported = displayLinkIfActionNotSupported;
        desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    }

    @Override
    public List<File> run(List<File> files) {
        
        List<File> errors = super.run(files);
        
        if(this.displayLinkIfActionNotSupported) {
            
            this.displayLinks(errors);
            
            errors = Collections.EMPTY_LIST;
        }

        return errors;
    }

    @Override
    public Boolean apply(File file) {
        
        Boolean opened = open(file);
        
        if( ! opened && this.displayLinkIfActionNotSupported) {
            
            this.displayLinks(Collections.singletonList(file));
            
            opened = Boolean.TRUE;
        }
        
        return opened;
    }

    public Boolean open(File file) {
        Boolean opened = Boolean.FALSE;
        if(desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
            try{
                desktop.open(file);
                opened = Boolean.TRUE;
            }catch(IOException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        return opened;
    }
    
    public void displayLinks(List<File> files){
    
        if(files.isEmpty()) {
            return;
        }
        
        final String title = files.size() + " files could not be opened!";
        
        final JFrame frame = new JFrame(title);
        
        final JTextArea textArea = new JTextArea();
        
        final StringBuilder builder = new StringBuilder();
        
        builder.append(title).append('\n').append('\n')
                .append("Please use the File Eplorer to open them manually");
        
        for(File file : files) {
        
            builder.append('\n').append('\n').append(file);
        }
        
        textArea.setText(builder.toString());
        
        textArea.setSize(500, 300);
        
        final JScrollPane pane = new JScrollPane(textArea);
        
        pane.setSize(500, 300);
        
        frame.getContentPane().add(pane);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        frame.pack();
        
        frame.setVisible(true);
    }
}
