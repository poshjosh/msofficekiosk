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

package com.looseboxes.msofficekiosk.ui.exam;

import com.looseboxes.msofficekiosk.commands.CommandContext;
import com.looseboxes.msofficekiosk.commands.CommandNames;
import com.looseboxes.msofficekiosk.commands.CommandParams;
import com.looseboxes.msofficekiosk.listeners.SelectionListenerImpl;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 12:55:04 PM
 */
public class ExamUiConfigurerImpl implements  Serializable, ExamUiConfigurer {

    private transient static final Logger LOG = Logger.getLogger(ExamUiConfigurerImpl.class.getName());

    public static class MessageHandler implements BiConsumer<String, Exception>{
        @Override
        public void accept(String m, Exception e) {
            LOG.log(Level.WARNING, m, e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }else{
                throw new RuntimeException(m, e);
            }
        }
    }
    
    private final BiConsumer<String, Exception> messageHandler;
    
    private final CommandContext commandContext;

    public ExamUiConfigurerImpl(CommandContext commandContext) {
        this(commandContext, new MessageHandler());
    }
    
    public ExamUiConfigurerImpl(CommandContext commandContext, BiConsumer<String, Exception> messageHandler) {
        this.messageHandler = Objects.requireNonNull(messageHandler);
        this.commandContext = Objects.requireNonNull(commandContext);
    }

    @Override
    public void accept(ExamUi examUi) {

        final ExamShellUi shellUi = examUi.getShellUi();
        
        this.addSelectionListener(examUi, shellUi.getSubmitAndCloseMenuItem(), CommandNames.SAVE_THEN_DISPOSE_THEN_SUBMIT);

        this.addSelectionListener(examUi, shellUi.getSaveMenuItem(), CommandNames.SAVE);

        this.addSelectionListener(examUi, shellUi.getAboutMenuItem(), CommandNames.ABOUT);

        this.addSelectionListener(examUi, shellUi.getExitMenuItem(), CommandNames.SAVE_THEN_DISPOSE_THEN_SUBMIT);

        this.addSelectionListener(examUi, shellUi.getExitWithoutSubmittingMenuItem(), CommandNames.SAVE_THEN_DISPOSE);
    }   
    
    @Override
    public SelectionListener addSelectionListener(ExamUi ui, MenuItem menuItem, String commandId) {
        final Map<String, Object> commandParameters = this.getCommandParameters(ui, commandId);
        final SelectionListener selectionListener = this.createSelectionListener(commandId, commandParameters);
        menuItem.addSelectionListener(selectionListener);
        return selectionListener;
    }
    
    @Override
    public Callable getCommand(ExamUi ui, String commandId) {
        final Map<String, Object> commandParameters = getCommandParameters(ui, commandId);
        return commandContext.getCommand(commandId, commandParameters);
    }
    
    @Override
    public Callable getCommand(ExamUi ui, String commandId, Predicate<Display> preCondition) {
        final Map<String, Object> commandParameters = getCommandParameters(ui, commandId);
        return commandContext.getCommand(commandId, commandParameters, preCondition);
    }
    
    @Override
    public Map<String, Object> getCommandParameters(ExamUi ui, String... commands) {
        final Map<String, Object> result = new HashMap<>();
        result.put(CommandParams.APP_UI, ui);
        result.put(CommandParams.FILE, ui.getOutputFile());
        result.put(CommandParams.OLECLIENTSITE, ui.getOleClientSite());
        return result;
    }
    
    @Override
    public SelectionListener createSelectionListener(String commandId, Map<String, Object> commandParameters) {
        final Callable command = commandContext.getCommand(commandId, commandParameters);
        return createSelectionListener(command);
    }
    
    @Override
    public SelectionListener createSelectionListener(Callable command) {
        return new SelectionListenerImpl(command, messageHandler);
    }

    @Override
    public CommandContext getCommandContext() {
        return commandContext;
    }
}
