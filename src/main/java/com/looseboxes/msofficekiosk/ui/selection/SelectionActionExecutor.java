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

package com.looseboxes.msofficekiosk.ui.selection;

import com.looseboxes.msofficekiosk.functions.GetUserMessage;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 8:58:16 AM
 */
public class SelectionActionExecutor {

    private static final Logger LOG = Logger.getLogger(SelectionActionExecutor.class.getName());
    
    private final AppUiContext uiContext;
    
    private final HasDisplayValue hdv;

    public SelectionActionExecutor(AppUiContext uiContext) {
        this(uiContext, new HasDisplayValueImpl());
    }
    
    public SelectionActionExecutor(AppUiContext uiContext, HasDisplayValue hdv) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.hdv = Objects.requireNonNull(hdv);
    }

    public <SELECTED> List<SELECTED> run(
            SelectionAction<SELECTED> action, 
            Precondition<SELECTED> preCondition,
            SELECTED... selected) {
        
        return run(action, preCondition, Arrays.asList(selected));
    }
    
    public <SELECTED> List<SELECTED> run(
            SelectionAction<SELECTED> action, 
            Precondition<SELECTED> preCondition,
            Collection<SELECTED> selected) {

        LOG.finer(() -> "Selected: " + selected);
        
        if(selected == null || selected.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        
        final List<SELECTED> ok = new ArrayList<>(selected.size());
        final List<SELECTED> no = new ArrayList<>(selected.size());
        for(SELECTED value : selected) {
            if( ! preCondition.test(value)) {
                no.add(value);
            }else{
                ok.add(value);
            }
        }
        
        final Object actionName = action.getName();
        
        if( ! no.isEmpty()) {
            
            final StringBuilder builder = new StringBuilder();
            final char separator = File.separatorChar;
            builder.append(actionName).append(" failed for the following files.")
                    .append(separator).append("Reason: ")
                    .append(Precondition.class.getSimpleName())
                    .append(" failed").append(separator).append(separator);
            
            final int maxLen = 100;
            
            for(SELECTED v : no) {
                
                builder.append(hdv.getDisplayValue(v, maxLen)).append(separator);
            }
            
            uiContext.getUi().getMessageDialog().showWarningMessage(builder.toString());
        }
        
        List<SELECTED> errors = Collections.EMPTY_LIST;
        
        try{
            
            try{
                uiContext.showProgressBarPercent(-1);
//                final int delay = 2_000;
//                final ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
//                svc.schedule(() -> {
//                    uiContext.showProgressBarPercent(-1);
//                }, delay, TimeUnit.MILLISECONDS);
//                svc.shutdown();
//                Util.shutdownAndAwaitTermination(svc, delay + 1_000, TimeUnit.MILLISECONDS);
            }catch(RuntimeException e) {
                final String m = "Unexpected Exception";
                LOG.log(Level.WARNING, m, e);
            }

            LOG.log(Level.FINER, "Executing {0} on {0} selected", 
                    new Object[]{actionName, selected.size()});

            errors = action.run(ok);
            
        }catch(RuntimeException e) {
            final String m = "Unexpected Exception";
            LOG.log(Level.WARNING, m, e);
            uiContext.getUi().getMessageDialog().showWarningMessage(new GetUserMessage().apply(e));
        }finally{
            uiContext.showProgressBarPercent(100);
        }

        if( ! errors.isEmpty()) {
            final String message = "Failed to "+actionName+". " + selected.size() + " selected";
            uiContext.getUi().getMessageDialog().showWarningMessage(message);
        }
        
        return errors;
    }
}
