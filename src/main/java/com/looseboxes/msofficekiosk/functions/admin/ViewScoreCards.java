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

import com.looseboxes.msofficekiosk.test.ScoreFile;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.ui.SwtMessageDialog;
import com.looseboxes.msofficekiosk.ui.selection.AbstractSelectionAction;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 16, 2019 10:32:16 AM
 */
public class ViewScoreCards extends AbstractSelectionAction<File> {

    private static final Logger LOG = Logger.getLogger(ShowMarkingUi.class.getName());

    private final AppContext app;
    
    private final Display display;
    
    public ViewScoreCards(AppContext context, Display display) {
        super("View Score Card(s)");
        this.app = Objects.requireNonNull(context);
        this.display = Objects.requireNonNull(display);
    }
    
    @Override
    public Boolean apply(File file) {
        
        LOG.log(Level.FINER, "Preparing to display Marking UI for file: {0}", file);
        
        try{

            LOG.log(Level.FINE, "Displaying Marking UI for file: {0}", file);

            final File scoreFile = new ScoreFile(app).toScoreFile(file);
            
            if( ! scoreFile.exists()) {
                
                new SwtMessageDialog(display).showWarningMessage("Score Card does not exist");
            
                return Boolean.FALSE;
                
            }else {
                
                if( ! new OpenFilesNative().apply(scoreFile)) {
                
                    new OpenFilesOleFrame(display, false).apply(scoreFile);
                }
            
                LOG.log(Level.FINER, "Done displaying Marking UI for file: {0}", file);

                return Boolean.TRUE;
            }

        }catch(RuntimeException e) {

            LOG.log(Level.WARNING, "Failed to display Marking UI for file: " + file, e);
            
            return Boolean.FALSE;
        }
    }
}
