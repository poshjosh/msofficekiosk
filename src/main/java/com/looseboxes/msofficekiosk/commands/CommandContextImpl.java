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

package com.looseboxes.msofficekiosk.commands;

import com.bc.config.ConfigData;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.functions.ui.DisplayAbout;
import com.looseboxes.msofficekiosk.popups.ShowInfoMessage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import org.eclipse.swt.ole.win32.OleClientSite;
import com.looseboxes.msofficekiosk.ui.exam.ExamUi;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.net.TestDocSenderFactory;
import com.looseboxes.msofficekiosk.ui.AppUiContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 1:51:30 PM
 */
public class CommandContextImpl implements CommandContext {

    private static final Logger LOG = Logger.getLogger(CommandContextImpl.class.getName());

    private final AppContext context;
    
    @Lazy @Autowired private AppUiContext uiContext;
    
    @Lazy @Autowired private Display display;
    
    @Lazy @Autowired private Tests tests;
    
    @Lazy @Autowired private DisplayAbout displayAbout;
    
    @Lazy @Autowired private TestDocSenderFactory testDocSenderFactory;

    public CommandContextImpl(AppContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public Callable<Integer> composeCommands(Map<String, Object> params, 
            Map<String, Predicate<Display>> preConditions, String...ids) {
        final List<Callable<Boolean>> commands = this.getCommands(params, preConditions, ids);
        final BiConsumer<String, Exception> exceptionHandler = (msg, ex) -> {
            LOG.log(Level.WARNING, msg, ex);
        };
        return new CommandChain(exceptionHandler, commands);
    }
    
    @Override
    public List<Callable<Boolean>> getCommands(Map<String, Object> params, 
            Map<String, Predicate<Display>> preConditions, String...ids) {
        
        final Callable<Boolean> [] result = new Callable[ids.length];
        
        final Predicate<Display> displayNotDisposed = (d) -> ! d.isDisposed();
        
        for(int i=0; i<result.length; i++) {
            
            final String id = ids[i];
            
            final Predicate<Display> preCondition = preConditions.get(id) == null ?
                    (diplay) -> true : preConditions.get(id);
        
            result[i] = getCommand(id, params, displayNotDisposed.and(preCondition));
        }
        return Arrays.asList(result);
    }
    
    @Override
    public Predicate<Display> getPreCondition(String command, Map<String, Object> params) {
        final Predicate<Display> result;
        switch(command) {
            case CommandNames.ABOUT:
                result = (d) -> ! d.isDisposed();
                break;
            case CommandNames.CREATE_AND_OPEN_DOCUMENT:    
                result = (d) -> ! d.isDisposed();
                break;
            case CommandNames.DISPOSE:
                result = this.getUserPromptPrecondition("Submit?");
                break;
            case CommandNames.SAVE_THEN_DISPOSE_THEN_SUBMIT:
                result = this.getUserPromptPrecondition("Submit and exit?");
               break;
            case CommandNames.SAVE_THEN_DISPOSE:
                result = this.getUserPromptPrecondition("Exit without submitting?");
                break;
            case CommandNames.SAVE:
                result = this.getUserPromptPrecondition("Save?");
                break;
            case CommandNames.SUBMIT:
                result = this.getUserPromptPrecondition("Open document will be submitted");
                break;
            default:
                throw new IllegalArgumentException("Unexpected command: " + command);
        }
        return result;
    }
    
    @Override
    public Callable<Boolean> getCommand(String id, Map<String, Object> params, Predicate<Display> preCondition) {
        final Callable result;
        switch(id) {
            case CommandNames.ABOUT:
                result = () -> displayAbout.apply(uiContext, Collections.EMPTY_MAP);
                break;
            case CommandNames.CREATE_AND_OPEN_DOCUMENT:    
                result = this.getOpenCommand(preCondition, params);
                break;
            case CommandNames.DISPOSE:
                result = this.getDisposeCommand(preCondition, params);
                break;
            case CommandNames.SAVE_THEN_DISPOSE_THEN_SUBMIT:
                result = composeCommands(params, 
                    Collections.singletonMap(CommandNames.SAVE, preCondition), 
                    getSubCommandNames(id).toArray(new String[0]));
               break;
            case CommandNames.SAVE_THEN_DISPOSE:
                result = composeCommands(params, 
                Collections.singletonMap(CommandNames.SAVE, preCondition), 
                getSubCommandNames(id).toArray(new String[0]));
                break;
            case CommandNames.SAVE:
                result = this.getSaveCommand(preCondition, params);
                break;
            case CommandNames.SUBMIT:
                result = this.getSubmitCommand(preCondition, params);
                break;
            default:
                throw new IllegalArgumentException("Unexpected command: " + id);
        }
        return result;
    }
    
    @Override
    public List<String> getSubCommandNames(String command) {
        final List<String> result;
        switch(command) {
            case CommandNames.SAVE_THEN_DISPOSE_THEN_SUBMIT:
                result = Arrays.asList(CommandNames.SAVE, CommandNames.DISPOSE, CommandNames.SUBMIT);
               break;
            case CommandNames.SAVE_THEN_DISPOSE:
                result = Arrays.asList(CommandNames.SAVE, CommandNames.DISPOSE);
                break;
            default:
                result = Arrays.asList(command);
        }
        return result;
    }

    public Callable<Boolean> getDisposeCommand(Predicate<Display> preCondition, Map<String, Object> params) {
        final ExamUi appUi = (ExamUi)getParamValue(
                CommandNames.DISPOSE, params, CommandParams.APP_UI);
        final Callable<Boolean> command = new DisposeCommand(appUi, preCondition);
        return command;
    }

    public Callable<Boolean> getSubmitCommand(Predicate<Display> preCondition, Map<String, Object> params) {
        
        final Callable<Boolean> command = new SubmitCommand(
                context, uiContext, display, tests, testDocSenderFactory, preCondition);
        
        return command;
    }

    public Callable<Boolean> getOpenCommand(Predicate<Display> preCondition, Map<String, Object> params) {
        final ExamUi appUi = (ExamUi)getParamValue(
                CommandNames.CREATE_AND_OPEN_DOCUMENT, params, CommandParams.APP_UI);
        final String documentName = (String)getParamValue(
                CommandNames.CREATE_AND_OPEN_DOCUMENT, params, CommandParams.DOCUMENT_NAME);
        final Callable<Boolean> command = new OpenDocumentCommand(appUi, documentName, preCondition); 
        return command;
    }

    public Callable<Boolean> getSaveCommand(Predicate<Display> preCondition, Map<String, Object> params) {
        
        final File file = (File)getParamValue(CommandNames.SAVE, params, CommandParams.FILE);
        final OleClientSite oleClientSite = (OleClientSite)getParamValue(CommandNames.SAVE, params, CommandParams.OLECLIENTSITE);
        
        return this.getSaveCommand(preCondition, oleClientSite, file);
    }
    
    public Object getParamValue(String commandName, Map<String, Object> params, String paramName) {
        return getParamValue(new String[]{commandName}, params, paramName);
    }
    
    public Object getParamValue(String [] commandNames, Map<String, Object> params, String paramName) {
        Object oval = null;
        for(String commandName : commandNames) {
            oval = params.get(commandName + '.' + paramName);
            if(oval != null) {
                break;
            }
        }
        if(oval == null) {
            oval = params.get(paramName);
        }
        Objects.requireNonNull(oval);
        return oval;
    }

    public Callable<Boolean> getSaveCommand(Predicate<Display> preCondition, 
            OleClientSite oleClientSite, File outputFile) {
        
        final ConfigData config = context.getConfigFactory().getConfig(ConfigService.APP_PROTECTED);
        
        return new SaveCommand(
                oleClientSite, outputFile, 
                config.getBoolean(ConfigNames.OUTPUTFILE_INCLUDEOLEINFO, true),
                preCondition
        );
    }

    public Predicate<Display> getUserPromptPrecondition(String buttonText) {
        Objects.requireNonNull(buttonText);
//        final MessageDialog messageDialog = this.getMessageDialog(new String[]{command}, params);
        return (display) -> {
            if(display.isDisposed()) {
                return false;
            }else{
                Shell shell = display.getActiveShell();
                final boolean newShell = shell == null || shell.isDisposed();
                try{
                    if(newShell) {
                        shell = new Shell(display);
                    }
                    return new ShowInfoMessage(shell).apply(buttonText) == SWT.YES;
                }finally{
                    if(newShell) {
                        shell.close();
                        shell.dispose();
                    }
                }
            }
        };    
    }

    public AppContext getContext() {
        return context;
    }

    public AppUiContext getUiContext() {
        return uiContext;
    }

    public Display getDisplay() {
        return display;
    }
}
/**
 * 
    public BiConsumer<String, Exception> getMessageHandler(String command, Map<String, Object> params) {
        return this.getMessageHandler(new String[]{command}, params);
    }
    
    public BiConsumer<String, Exception> getMessageHandler(String [] commands, Map<String, Object> params) {
        final MessageDialog messageDialog = this.getMessageDialog(commands, params);
        final BiConsumer<String, Exception> messageHandler = (msg, ex) -> {
            if(messageDialog != null) {
                final int msgType = ex == null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
                messageDialog.showMessage(msgType, msg);
            }
            if(ex == null) {
                LOG.log(Level.FINE, msg);
            }else{
                LOG.log(Level.WARNING, msg, ex);
            }
        };
        return messageHandler;
    }
    
    public MessageDialog getMessageDialog(String [] commands, Map<String, Object> params) {
        final UI appUi = (UI)getParamValue(commands, params, CommandParams.APP_UI);
        return this.getMessageDialog(appUi, null);
    }
    
    public MessageDialog getMessageDialog(UI appUi, MessageDialog resultIfNone) {
        final MessageDialog messageDialog;
        if(appUi != null) {
            messageDialog = appUi.getMessageDialog();
        }else{
            messageDialog = null;
        }
        return messageDialog == null ? resultIfNone : messageDialog;
    }

 * 
 */