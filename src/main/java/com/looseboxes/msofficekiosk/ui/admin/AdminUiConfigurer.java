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

import com.bc.config.Config;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.commands.ExitCommand;
import com.looseboxes.msofficekiosk.commands.UpdatePropertiesCommand;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.functions.ui.DisplayAbout;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.springframework.beans.factory.annotation.Autowired;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.admin.BuildAdminUiTitle;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.PreconditionLogin;
import com.looseboxes.msofficekiosk.functions.admin.DecryptTestDocNames;
import com.looseboxes.msofficekiosk.functions.admin.DirsFiles;
import com.looseboxes.msofficekiosk.functions.admin.GetDevicedetailsList;
import com.looseboxes.msofficekiosk.functions.admin.MsWordDocFilenameFilter;
import com.looseboxes.msofficekiosk.functions.admin.OpenFilesOleFrame;
import com.looseboxes.msofficekiosk.functions.admin.PromptUserSelect;
import com.looseboxes.msofficekiosk.functions.admin.ShowMarkingUi;
import com.looseboxes.msofficekiosk.functions.admin.SubmitScoreCards;
import com.looseboxes.msofficekiosk.functions.admin.ViewScoreCards;
import com.looseboxes.msofficekiosk.listeners.SelectionListenerImpl;
import com.looseboxes.msofficekiosk.messaging.MessageBuilder;
import com.looseboxes.msofficekiosk.messaging.MessageBuilderForDocLinks;
import com.looseboxes.msofficekiosk.messaging.MessageSender;
import com.looseboxes.msofficekiosk.net.SendFilesToNetworkDevices;
import com.looseboxes.msofficekiosk.net.SendMessageWithLinkToFiles;
import com.looseboxes.msofficekiosk.net.SendTestDocs;
import com.looseboxes.msofficekiosk.net.TestDocSenderFactory;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.ui.UI;
import com.looseboxes.msofficekiosk.ui.selection.ActionListenerForSelection;
import com.looseboxes.msofficekiosk.ui.selection.ListSelectionManagerForDevicedetails;
import com.looseboxes.msofficekiosk.ui.selection.SelectionAction;
import com.looseboxes.msofficekiosk.ui.selection.SelectionActionExecutor;
import com.looseboxes.msofficekiosk.ui.selection.TreeSelectionManagerForDirFiles;
import com.looseboxes.msofficekiosk.ui.selection.UiSelectionManager;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.awt.Window;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTree;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 11:17:52 AM
 */
public class AdminUiConfigurer implements UiBeans.UiConfigurer<AdminUi>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(AdminUiConfigurer.class.getName());
    
    public static boolean JTREE_ROOT_INCLUSIVE = false;
    
    @Autowired private AppContext app;
    
    @Autowired private ConfigFactory configFactory;
        
    @Autowired private AppUiContext uiContext;
    
    @Autowired private Display display;

    @Autowired private LoginManager loginManager;

    @Autowired private PreconditionLogin preconditionLogin;
    
    @Autowired private DisplayAbout displayAbout;
    
    @Autowired private BuildAdminUiTitle titleBuilder;
    
    @Autowired private GetDevicedetailsList getDevicedetailsList;
    
    @Autowired private MessageSender messageSender;
    
    @Autowired private Tests tests;
    
    @Autowired private TestDocSenderFactory testDocSenderFactory;

    private final File[] dirs;
    
    public AdminUiConfigurer(File... dirs) { 
        this.dirs = Objects.requireNonNull(dirs);
    }
    
    public Callable getUpdatePropertiesCommand(String propertiesId) {
        final UpdatePropertiesCommand updatePropsCmd = new UpdatePropertiesCommand(
                configFactory, propertiesId, (obj) -> true, getPropertiesUpdateListener()
        );
        return updatePropsCmd;
    }

    public Consumer<Map<String, String>> getPropertiesUpdateListener() {
        return (update) -> {};
    }
    
    @Override
    public void accept(AdminUi adminUi) {
        
        LOG.fine("Configuring Admin Frame");
        
        final Config config = app.getConfig(ConfigService.APP_INTERNAL);
        
        final String title = titleBuilder.with(config);
        
        adminUi.getTitleMenuItem().setText(title);
        
        adminUi.getLoginMenuItem().setText(loginManager.isLoggedIn() ? "Logout" : "Login");
        
        adminUi.getLoginMenuItem().addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent se) {
                try{
                    if(loginManager.isLoggedIn()) {
                        loginManager.logout();
                    }else{
                        loginManager.promptUserLogin(1);
                    }
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            }
        });
        
        final BiConsumer<String, Exception> exceptionHandler = (m, e) -> {
            LOG.log(Level.WARNING, m, e);
        };
        
        final Callable updatePropsCmd = getUpdatePropertiesCommand(ConfigService.APP_PROTECTED);
        adminUi.getSettingsMenuItem().addSelectionListener(new SelectionListenerImpl(updatePropsCmd, exceptionHandler));

        final Callable updateUiPropsCmd = getUpdatePropertiesCommand(ConfigService.APP_UI);
        adminUi.getUiSettingsMenuItem().addSelectionListener(new SelectionListenerImpl(updateUiPropsCmd, exceptionHandler));

        final Callable displayAboutCmd = () -> displayAbout.apply(uiContext, Collections.EMPTY_MAP);
        adminUi.getAboutMenuItem().addSelectionListener(new SelectionListenerImpl(displayAboutCmd, exceptionHandler));
        
        final Predicate preconditionForExit = (any) -> {
            final Window mainWindow = uiContext.getMainWindowOptional().orElse(null);
            final Icon icon = uiContext.getImageIconOptional().orElse(null);
            final int selection = JOptionPane.showConfirmDialog(mainWindow, 
                    "Are you sure you want to exit?", "Exit", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);
            return selection == JOptionPane.YES_OPTION;
        };
        
        final Callable exitCmd = getExitCommand(adminUi, preconditionForExit);
        
        adminUi.getExitMenuItem().addSelectionListener(new SelectionListenerImpl(exitCmd, exceptionHandler));

        addMoreListeners(adminUi, exceptionHandler);
        
        adminUi.getShell().addShellListener(new ShellAdapter(){
            @Override
            public void shellClosed(ShellEvent se) {
                LOG.entering(this.getClass().getName(), "shellClosed(ShellEvent)");
                try{
                    exitCmd.call();
                }catch(Exception e) {
                    LOG.log(Level.WARNING, null, e);
                }
            }
        });
        
        LOG.fine("Done configuring Admin Frame");
    }
    
    private void addMoreListeners(AdminUi adminUi, BiConsumer<String, Exception> exceptionHandler) {
        
        final FilenameFilter filenameFilter = new MsWordDocFilenameFilter();
        
        final PromptUserSelect<JList<Devicedetails>, Devicedetails> promptSelectDevices = 
                new PromptUserSelect<>(uiContext, new ListSelectionManagerForDevicedetails(
                        uiContext, display, getDevicedetailsList));
        
        final Precondition allow = (selected) -> true;
        
        final MessageBuilder messageBuilder = new MessageBuilderForDocLinks(app, loginManager, true);
        
        final SendFilesToNetworkDevices sendMessageWithLinkToFiles = 
                new SendMessageWithLinkToFiles(uiContext, messageBuilder, messageSender);
        
        final SelectionAction<File> sendFilesAction = new SelectionAction<File>(){
            @Override
            public String getName() {
                return "Send File(s)";
            }
            @Override
            public List<File> run(List<File> files) {
                return sendMessageWithLinkToFiles.apply(files, promptSelectDevices.get()) ? 
                        Collections.EMPTY_LIST : files;
            }
        };
        
        final SendTestDocs sendTestDocs = testDocSenderFactory.get(TestDocSenderFactory.SEND_TO_SERVER);
        
        final SelectionAction<File> submitAction = new SubmitScoreCards(app, tests, sendTestDocs);

        final ShowMarkingUi showMarkingUi = new ShowMarkingUi(app, display, submitAction);
        
        final List<SelectionAction<File>> testDocActions =
                Arrays.asList(new OpenFilesOleFrame(display), sendFilesAction, 
                        showMarkingUi, new ViewScoreCards(app, display), 
                        submitAction, new DecryptTestDocNames(app));
        
        final Supplier<List<File>> filesSupplier = new DirsFiles(filenameFilter, true, dirs);
        
        final UiSelectionManager<JTree, File> fileSelectionManager = new TreeSelectionManagerForDirFiles(
                uiContext, display, filesSupplier, allow, testDocActions);

        adminUi.getViewInboxMenuItem().addSelectionListener(
                new ActionListenerForSelection(
                        uiContext,
                        "View Test Documents", 
                        preconditionLogin,
                        fileSelectionManager
                ));
        
        final PromptUserSelect<JTree, File> promptSelectTestDocs = 
                new PromptUserSelect<>(uiContext, 
                        new TreeSelectionManagerForDirFiles(uiContext, display, filesSupplier));
        
        final SelectionAction<Devicedetails> sendFilesAction2 = new SelectionAction<Devicedetails>(){
            @Override
            public String getName() {
                return "Send File(s)";
            }
            @Override
            public List<Devicedetails> run(List<Devicedetails> ddList) {
                return sendMessageWithLinkToFiles.apply(promptSelectTestDocs.get(), ddList) ? Collections.EMPTY_LIST : ddList;
            }
        };
        final List<SelectionAction<Devicedetails>> ddActions =
                Arrays.asList(sendFilesAction2);
        final UiSelectionManager<JList<Devicedetails>, Devicedetails> deviceSelectionManager = 
                new ListSelectionManagerForDevicedetails(uiContext, display, getDevicedetailsList, allow, ddActions);

        adminUi.getViewOnlineDevicesMenuItem().addSelectionListener(
                new ActionListenerForSelection(
                        uiContext,
                        "View Online Devices", 
                        preconditionLogin,
                        deviceSelectionManager
                ));

        final Callable markFileCmd = () -> {
            
            final Shell shell = new Shell(display);
            final FileDialog fileDialog = new FileDialog(shell);
            fileDialog.setText("Select File to Mark");
            fileDialog.setFilterExtensions(new String[] { "*.docx" });
            // Put in a readable name for the filter
            fileDialog.setFilterNames(new String[] { "*.docx (MS Word Documents)" });
            // Open Dialog and save result of selection
            final String selected = fileDialog.open();

            LOG.log(Level.FINE, "Selected: {0}", selected);
            
            if(selected == null || selected.isEmpty()) {
                
                return Boolean.FALSE;
            }
            
            new SelectionActionExecutor(uiContext).run(
                    showMarkingUi, preconditionLogin, new File(selected));
            
            return Boolean.TRUE;
        };
        
        adminUi.getOpenFileForMarkingMenuItem()
                .addSelectionListener(new SelectionListenerImpl(markFileCmd, exceptionHandler));
    }
    
    public Callable getExitCommand(UI ui, Predicate test) {

        final Callable exitCmd = new ExitCommand(test, app, ui);
        
        return exitCmd;
    }
}
/**
 * 
        adminUi.getDecryptDocsMenuItem().addActionListener(
                new ActionListenerForSelection(
                        uiContext,
                        "Inbox -> Decrypt File Name(s)", 
                        preconditionLogin,
                        fileSelectionManager
                )
        );

 * 
 */