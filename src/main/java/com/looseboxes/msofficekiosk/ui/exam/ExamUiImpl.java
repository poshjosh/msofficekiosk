/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance withDocumentName the License.
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

import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.commands.CommandChain;
import com.looseboxes.msofficekiosk.commands.RunTimerCommand;
import com.looseboxes.msofficekiosk.commands.TickListenerImpl;
import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import com.bc.config.Config;
import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.OpenedFileManager;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.commands.CommandContext;
import com.looseboxes.msofficekiosk.commands.CommandNames;
import com.looseboxes.msofficekiosk.commands.CommandParams;
import com.looseboxes.msofficekiosk.functions.GetTimeDisplay;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleAdapter;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Properties;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.functions.GetDateTime;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.functions.UserProfileMessage;
import org.eclipse.swt.ole.win32.OleFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2018 11:05:36 AM
 */
public class ExamUiImpl implements ExamUi {

    private transient static final Logger LOG = Logger.getLogger(ExamUiImpl.class.getName());
    
    private class CommandChainExceptionHandler implements Serializable, BiConsumer<String, Exception> {
        @Override
        public void accept(String msg, Exception e) {
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }else{
                LOG.log(Level.WARNING, msg, e);
            }
        }
    }

    private boolean disposed;
    private boolean showing;
    
    private ZonedDateTime displayTime;
    private RunTimerCommand updateTimerLabelCommand;
    private RunTimerCommand periodTaskCommand;
    
    private final AppContext context;
    private final TestDoc testDoc;
    
    private final LifecycleEventHandler lifecycleEventHandler;

    private final AppUiContext uiContext;

    private final Display display;

    private final MessageDialog messageDialog;

    private final ExamUiConfigurer uiConfigurer;
    
    private final Tests tests;

    private final UserProfileMessage userProfileMessage;
    
    private final LoginManager loginManager;
    
    private final ExamShellUi shell; 

    public ExamUiImpl(AppContext context, AppUiContext uiContext, 
            Tests tests, TestDoc testDoc,
            LifecycleEventHandler lifecycleEventHandler, Display display, 
            MessageDialog messageDialog, ExamUiConfigurer uiConfigurer, 
            UserProfileMessage userProfileMessage, LoginManager loginManager) {
        this.context = Objects.requireNonNull(context);
        this.uiContext = Objects.requireNonNull(uiContext);
        this.tests = Objects.requireNonNull(tests);
        this.testDoc = Objects.requireNonNull(testDoc);
        this.lifecycleEventHandler = Objects.requireNonNull(lifecycleEventHandler);
        this.display = Objects.requireNonNull(display);
        this.messageDialog = Objects.requireNonNull(messageDialog);
        this.uiConfigurer = Objects.requireNonNull(uiConfigurer);
        this.userProfileMessage = Objects.requireNonNull(userProfileMessage);
        this.loginManager = Objects.requireNonNull(loginManager);
        this.shell = createShell();
        
        uiConfigurer.accept(this);
    }
    
    private ExamShellUiImpl createShell() {
        final String appName = getAppName();
        final Config config = context.getConfig(ConfigService.APP_UI);
        final File outputFile = getOutputFile();
        final Listener openDocListener = createOpenDocumentListener();
        final String userStatus  = this.userProfileMessage.get();
        return new ExamShellUiImpl(display, appName, config, outputFile, openDocListener, userStatus);
    } 
    
    private Listener createOpenDocumentListener() {
        final Listener listener = (Event event) -> {
            final String documentName = shell.getOpenNewForm().getText().getText();
            if(documentName == null || documentName.isEmpty()) {
                final String msg = "Please enter name of document to create"; 
                getMessageDialog().showInformationMessage(msg);
            }else{
                final Map<String, Object> params = new HashMap(
                        uiConfigurer.getCommandParameters(this, CommandNames.CREATE_AND_OPEN_DOCUMENT)
                );
                params.put(CommandParams.DOCUMENT_NAME, documentName);
                final Callable<Boolean> openNewCommand = uiConfigurer.getCommandContext()
                        .getCommand(CommandNames.CREATE_AND_OPEN_DOCUMENT, params);
                try{
                    openNewCommand.call();
                }catch(Exception e) {
                    final String msg = "Unexpected problem trying to open: " + documentName;
                    getMessageDialog().showWarningMessage(msg);
                    LOG.log(Level.WARNING, msg, e);
                }
            }
        };
        return listener;
    }
    
    @Override
    public boolean addLifeCycleListener(LifeCycleListener listener) {
        return this.lifecycleEventHandler.addLifeCycleListener(listener);
    }

    @Override
    public boolean removeLifeCycleListener(LifeCycleListener listener) {
        return lifecycleEventHandler.removeLifeCycleListener(listener);
    }
    
    @Override
    public ExamUi openDocument(String documentName) {
        
        LOG.log(Level.FINER, "Opening document: {0}", documentName);
        
        final ExamUi next;

        if(documentName != null && !documentName.isEmpty()) {

            final TestDoc nextDoc = tests.createTestDoc(testDoc.getTest(), documentName);
            
            next = new ExamUiImpl(context, uiContext, tests, nextDoc, lifecycleEventHandler, 
                display, messageDialog, uiConfigurer, userProfileMessage, loginManager);

            LOG.log(Level.FINER, "Created UI for document: {0}", documentName);
            
            final ExamUiImpl toBeClosed = this;

            next.addLifeCycleListener(new LifeCycleAdapter(){
                @Override
                public void onShown(LifeCycleListener.Event event) {
                    try{
                        LOG.fine(() -> "Opened document: " + documentName);

                        LOG.fine(() -> "Closing: " + toBeClosed.getTestDoc().getDocumentname());
                        
                        final ExamUiConfigurer uiConfigurer = toBeClosed.getConfigurer();
                        
                        final String[] commandNames = {CommandNames.SAVE, CommandNames.DISPOSE};
                        final Map params = uiConfigurer.getCommandParameters(toBeClosed, commandNames);
                        final CommandContext commandContext = uiConfigurer.getCommandContext();
                        final List<Callable<Boolean>> commands = commandContext.getCommands(params, commandNames);

                        new CommandChain(new CommandChainExceptionHandler(), commands).call();

                        LOG.fine(() -> "Closed: " + toBeClosed.getTestDoc().getDocumentname());
                        
                    }catch(RuntimeException e) {
                        LOG.log(Level.WARNING, null, e);
                    }
                }
            });
            
//            LOG.finer(() -> "Preparing to show: "+documentName+", diplayTime: " + this.displayTime);
            
            next.show(this.displayTime);
            
        }else{

            LOG.warning(() -> "Document name not valid, name: " + documentName);

            next = this;
        }

        return next;
    }
    
    @Override
    public void show() {
        this.show(this.getDisplayDurationMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void show(ZonedDateTime timeOffset) {
        this.show(timeOffset, this.getDisplayDurationMillis(), TimeUnit.MILLISECONDS);
    }    

    @Override
    public void show(long timeout, TimeUnit timeoutUnit) {
        this.show(getStarttime(testDoc.getTest()), timeout, timeoutUnit);
    }
    
    private ZonedDateTime getStarttime(Test test) {
        final Config config = context.getConfig(ConfigService.APP_PROTECTED);
        return new GetDateTime().apply(test.getStarttime().getTime(), config);
    }
    
    @Override
    public void show(ZonedDateTime timeOffset, long timeout, TimeUnit timeoutUnit) {
        
        LOG.fine(() -> "Display duration: " + timeoutUnit.toSeconds(timeout) + " secs for: " + testDoc);
        
        try{
           
            this.lifecycleEventHandler.onWillShow(this);

            this.updateOpenStatus();

            final Font font = getFont();
            this.shell.setFont(font);
            
            this.shell.getTimerLabel().setText("  " + new GetTimeDisplay().apply(0L, ZoneId.systemDefault()) + "  ");

            this.shell.getShell().open();
            
            this.showing = true;
            
            final String documentName = testDoc.getDocumentname();
            this.setMessageText("Currently Viewing: " + documentName);
            LOG.fine(() -> "Opened shell for: " + documentName);

            this.displayTime = timeOffset;
        
            this.scheduleTimerLabelUpdate();

            this.schedulePeriodicAutoSave();

            final long expendedMillis = ZonedDateTime.now().toInstant().toEpochMilli() - timeOffset.toInstant().toEpochMilli();

            final long remainingMillis = timeoutUnit.toMillis(timeout) - (expendedMillis < 0 ? 0 : expendedMillis);

            LOG.fine(() -> "Expended: " + TimeUnit.MILLISECONDS.toSeconds(expendedMillis) + 
                    " seconds. Remaining: " + TimeUnit.MILLISECONDS.toSeconds(remainingMillis) + " seconds.");

            this.scheduleSaveAndDisposeShell(remainingMillis);
            
        }catch(Throwable e){
            
            LOG.log(Level.WARNING, null, e);
            
            throw e;

        }finally{

            try{
                this.lifecycleEventHandler.onShown(this);
            }finally{
                
                LOG.fine(() -> "Looping shell for: " + testDoc);

                while ( ! shell.isDisposed()) {
                    if ( ! display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                
                LOG.fine(() -> "Exiting shell for: " + testDoc + 
                        ", still open: " + getFileManager().getCurrentlyOpenFileNames());
            }
        }
    }
    
    private void updateOpenStatus() {
        
        final String currentFileName = testDoc.getDocumentname();

        final OpenedFileManager fileManager = this.getFileManager();
        
        fileManager.addOpened(currentFileName);
        
        final String commandName = CommandNames.CREATE_AND_OPEN_DOCUMENT;
        
        final Map params = new HashMap(uiConfigurer.getCommandParameters(this, commandName));
        
        for(String openedFileName : fileManager.getOpenedFileNames()) {
            
            params.put(CommandParams.DOCUMENT_NAME, openedFileName);
            
            final Callable<Boolean> openCommand = uiConfigurer.getCommandContext().getCommand(commandName, params);
            
            final MenuItem openedMenuItem = this.shell.createCascadeMenuItem(this.shell.getOpenMenu(), openedFileName);
            openedMenuItem.addSelectionListener(uiConfigurer.createSelectionListener(openCommand));
            
            if(openedFileName.equals(currentFileName)) {
                
                openedMenuItem.setEnabled(false);
            }
        }
    }
    
    private void scheduleTimerLabelUpdate() {

        final long durationMillis = this.getDisplayDurationMillis();
        final long startTimeMillis = this.displayTime.toInstant().toEpochMilli();
        final long endTimeMillis = startTimeMillis + durationMillis; 
        final long _valueInSeconds = this.getConfig(ConfigService.APP_PROTECTED).getLong(ConfigNames.TIMER_INTERVAL_SECONDS, 1);
        final int timerIntervalMillis = (int)TimeUnit.SECONDS.toMillis(_valueInSeconds);
        final Label timerLabel = shell.getTimerLabel();
        final RunTimerCommand.TickListener tickListener = new TickListenerImpl(
                context.getConfigFactory(), display,
                (timer) -> !timerLabel.isDisposed() && timerLabel.isVisible(), 
                (timer) -> timer.getMillisTill(endTimeMillis), 
                (text) -> timerLabel.setText(text)
        );
        this.updateTimerLabelCommand = new RunTimerCommand(this.display, 
                startTimeMillis,  timerIntervalMillis, tickListener);
        this.updateTimerLabelCommand.start();
    }
    
    private void schedulePeriodicAutoSave() {
        final long _valueInSeconds = this.getConfig(ConfigService.APP_INTERNAL).getLong(ConfigNames.AUTOSAVE_INTERVAL_SECONDS, 10);
        final int autoSaveIntervalMillis = (int)TimeUnit.SECONDS.toMillis(_valueInSeconds);
        final RunTimerCommand.TickListener tickListener = new TickListenerImpl(
                context.getConfigFactory(), display,
                (timer) -> true, 
                (timer) -> timer.getTimeElapsedMillis(), 
                (text) -> {
                    try{
                        
                        LOG.finest(() -> "Saving after: " + text);
                        
                        final Map<String, Object> params = uiConfigurer
                                .getCommandParameters(this, CommandNames.SAVE);
                        final Callable command = uiConfigurer.getCommandContext()
                                .getCommand(CommandNames.SAVE, params, (displayArg) -> true);
                        
                        command.call();
                    
                    }catch(Exception e) {
                        LOG.log(Level.WARNING, "Exception executing PERIODIC_SAVE Command", e);
                    }
                }
        );
        this.periodTaskCommand = new RunTimerCommand(this.display, autoSaveIntervalMillis, tickListener);
        this.periodTaskCommand.start();
    }
    
    private void scheduleSaveAndDisposeShell(long timeoutMillis) {
        
        if(timeoutMillis > 0) {
            
            this.display.timerExec((int)timeoutMillis, () -> {
                try{

                    final Callable command = uiConfigurer.getCommand(
                            this, CommandNames.SAVE_THEN_DISPOSE_THEN_SUBMIT, (display) -> !display.isDisposed());

                    command.call();

                }catch(Exception e) {
                    LOG.log(Level.WARNING, "Exception executing SAVE_AND_DISPOSE_SHELL Command", e);
                }
            });
        }
    }
    
    @Override
    public void dispose() {
        if( ! disposed) {
            
            LOG.finer(() -> "Disposing App UI: " + testDoc);
            
            this.lifecycleEventHandler.onWillDispose(this);
            
            try{
                
                this.shell.dispose();

                final String documentName = this.testDoc.getDocumentname();
                
                LOG.fine(() -> "Disposed shell for: " + testDoc);
                
                final boolean lastOpen = this.getFileManager().removeFromCurrentlyOpen(documentName) == 0;
    
                if(lastOpen) {
                    uiContext.showProgressBarPercent("..Exiting", -1);
                }
                
                this.updateTimerLabelCommand.stop();
                this.periodTaskCommand.stop();

                if(lastOpen) {
                    uiContext.showProgressBarPercent("..Exiting", -1);
                }

            }finally{

                this.lifecycleEventHandler.onDisposed(this);
                
                disposed = true;
                if(this.getFileManager().isNoneOpen()) {
                    uiContext.showProgressBarPercent(100);
                }

                LOG.fine(() -> "Done disposing App UI: " + testDoc);
            }
        }
    }

    @Override
    public MessageDialog getMessageDialog() {
        return this.messageDialog;
    }
    
    @Override
    public void setMessageText(String text) {
        shell.setMessageText(text);
    }
    
    @Override
    public final long getDisplayDurationMillis() {
        final Test test = this.testDoc.getTest();
        LOG.finer(() -> "Test: " + test);
        final Integer durationInMinutes = test.getDurationinminutes();
        LOG.finer(() -> "Duration in minutes: " + durationInMinutes);
        final long durationMillis = TimeUnit.MINUTES.toMillis(durationInMinutes);
        return durationMillis;
    }
    
    private Config<Properties> getConfig(String id) {
        return context.getConfig(id);
    }

    private String getAppName() {
        final Config<Properties> cfg = context.getConfig(ConfigService.APP_INTERNAL);
        return Objects.requireNonNull(cfg.getString(ConfigNames.APP_NAME));
    }
    
    private OpenedFileManager getFileManager() {
        return context.getOpenedFileManager();
    }
    
    public ExamUiConfigurer getConfigurer() {
        return this.uiConfigurer;
    }

    @Override
    public File getOutputFile() {
        return getFileManager().getFile(testDoc);
    }

    @Override
    public final boolean isDisposed() {
        return disposed;
    }

    @Override
    public final boolean isShowing() {
        return showing;
    }

    @Override
    public Display getDisplay() {
        return display;
    }
    
    @Override
    public final ZonedDateTime getDisplayTime() {
        return displayTime;
    }

    @Override
    public final TestDoc getTestDoc() {
        return testDoc;
    }

    @Override
    public final AppContext getContext() {
        return context;
    }

    @Override
    public final Shell getShell() {
        return shell.getShell();
    }

    @Override
    public OleFrame getOleFrame() {
        return shell.getOleFrame();
    }

    @Override
    public ExamShellUi getShellUi() {
        return shell;
    }

    @Override
    public final OleClientSite getOleClientSite() {
        return shell.getOleClientSite();
    }

    @Override
    public Font getFont() {
        final Font font = uiContext.getSwtFont(display);
        return font;
    }
}
/**
 * 
    private void createShellAync() {
        display.asyncExec(() -> {
            try{
                shell = createShell();
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, null, e);
            }
        });
    }
    private synchronized void waitForShell(long milli) {
        final Thread waitFor = new Thread(() -> {
            try{
                while(shell == null) {
                    Thread.sleep(1000);
                }
            }catch(Exception e) {
                LOG.log(Level.WARNING, null, e);
            }
        }, this.getClass().getSimpleName()+"_WaitForShellCreation_Thread");
        waitFor.start();
        try{
            waitFor.join(milli);
        }catch(InterruptedException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }
 * 
 */