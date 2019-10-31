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

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.admin.MarkingUi;
import com.looseboxes.msofficekiosk.ui.selection.AbstractSelectionAction;
import com.looseboxes.msofficekiosk.ui.selection.SelectionAction;
import java.io.File;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 13, 2019 6:18:38 PM
 */
public class ShowMarkingUi extends AbstractSelectionAction<File> {

    private static final Logger LOG = Logger.getLogger(ShowMarkingUi.class.getName());

    private final AppContext app;
    
    private final Display display;
    
    private Shell shell;
    
    private final boolean internalDisplay;
    
    private final SelectionAction<File> submitAction;

    public ShowMarkingUi(AppContext context, SelectionAction<File> submitAction) {
        this(context, new Display(), true, submitAction);
    }
    
    public ShowMarkingUi(AppContext context, Display display, SelectionAction<File> submitAction) {
        this(context, display, false, submitAction);
    }
    
    protected ShowMarkingUi(AppContext context, Display display, 
            boolean internalDisplay, SelectionAction<File> submitAction) {
        super("Mark Test Document(s)");
        this.app = Objects.requireNonNull(context);
        this.display = Objects.requireNonNull(display);
        this.internalDisplay = internalDisplay;
        this.submitAction = Objects.requireNonNull(submitAction);
    }
    
    @Override
    public Boolean apply(File file) {
        
        LOG.log(Level.FINER, "Preparing to display Marking UI for file: {0}", file);
        
        try{

            LOG.log(Level.FINE, "Displaying Marking UI for file: {0}", file);

            final Config<Properties> uiConfig = app.getConfig(ConfigService.APP_UI);
            final int left = uiConfig.getInt(ConfigNames.DUAL_WINDOW_LEFT_WINDOW_RATIO, 60);
            final int right = uiConfig.getInt(ConfigNames.DUAL_WINDOW_RIGHT_WINDOW_RATIO, 40);

            display.syncExec(() -> {
                try{
                    
                    shell = new Shell(display);
                    
                    createMarkingUi(app, shell, new int[]{left, right}, submitAction, file).show();
                    
                    LOG.log(Level.FINER, "Done displaying Marking UI for file: {0}", file);                    
                    
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            });
            
            return Boolean.TRUE;

        }catch(RuntimeException e) {

            LOG.log(Level.WARNING, "Failed to display Marking UI for file: " + file, e);
            
            return Boolean.FALSE;
        }
    }
    
    public MarkingUi createMarkingUi(AppContext app, Shell shell, int [] splitFormRatios, 
            SelectionAction<File> submitAction, File file) {
        return new MarkingUi(app, shell, splitFormRatios, submitAction, file);
    }
}
