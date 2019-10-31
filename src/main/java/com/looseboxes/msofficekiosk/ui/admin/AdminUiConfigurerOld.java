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

package com.looseboxes.msofficekiosk.ui.admin;

import com.bc.config.Config;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.commands.UpdatePropertiesCommand;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigNamesInternal;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.admin.BuildAdminUiTitle;
import com.looseboxes.msofficekiosk.functions.admin.DecryptTestDocNames;
import com.looseboxes.msofficekiosk.functions.admin.DirsFiles;
import com.looseboxes.msofficekiosk.functions.admin.GetDevicedetailsList;
import com.looseboxes.msofficekiosk.functions.admin.MsWordDocFilenameFilter;
import com.looseboxes.msofficekiosk.functions.admin.OpenFilesOleFrame;
import com.looseboxes.msofficekiosk.functions.admin.PromptUserSelect;
import com.looseboxes.msofficekiosk.functions.admin.ShowMarkingUi;
import com.looseboxes.msofficekiosk.functions.admin.SubmitScoreCards;
import com.looseboxes.msofficekiosk.functions.admin.ViewScoreCards;
import com.looseboxes.msofficekiosk.functions.ui.DisplayAbout;
import com.looseboxes.msofficekiosk.messaging.MessageBuilder;
import com.looseboxes.msofficekiosk.messaging.MessageBuilderForDocLinks;
import com.looseboxes.msofficekiosk.messaging.MessageSender;
import com.looseboxes.msofficekiosk.net.SendFilesToNetworkDevices;
import com.looseboxes.msofficekiosk.net.SendMessageWithLinkToFiles;
import com.looseboxes.msofficekiosk.net.SendTestDocs;
import com.looseboxes.msofficekiosk.net.TestDocSenderFactory;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.PreconditionLogin;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.UI;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import com.looseboxes.msofficekiosk.ui.admin.listeners.CommandActionListener;
import com.looseboxes.msofficekiosk.ui.selection.ActionListenerForSelection;
import com.looseboxes.msofficekiosk.ui.selection.ListSelectionManagerForDevicedetails;
import com.looseboxes.msofficekiosk.ui.selection.SelectionAction;
import com.looseboxes.msofficekiosk.ui.selection.TreeSelectionManagerForDirFiles;
import com.looseboxes.msofficekiosk.ui.selection.UiSelectionManager;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chinomso Bassey Ikwuagwu on May 17, 2019 10:16:13 PM
 */
public class AdminUiConfigurerOld implements UiBeans.UiConfigurer<AdminUiOld>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(AdminUiConfigurerOld.class.getName());
    
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
    
    public AdminUiConfigurerOld(File... dirs) { 
        this.dirs = Objects.requireNonNull(dirs);
    }
    
    @Override
    public void accept(AdminUiOld adminUi) {
        
        LOG.fine("Configuring Admin Frame");
        
        final Config config = app.getConfig(ConfigService.APP_INTERNAL);
        
        final String title = titleBuilder.with(config);
        
        adminUi.setTitle(title);
        
        //@todo global UI configurer for icon, font etc
        uiContext.getImageIconOptional().ifPresent((imageIcon) -> {
            adminUi.setIconImage(imageIcon.getImage());
        });
        
        uiContext.positionFullScreen(adminUi);
        
        adminUi.getLoginMenuItem().setText(loginManager.isLoggedIn() ? "Logout" : "Login");
        
        adminUi.getLoginMenuItem().addActionListener((ae) -> {
            try{
                if(loginManager.isLoggedIn()) {
                    loginManager.logout();
                }else{
                    loginManager.promptUserLogin(1);
                }
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, null, e);
            }
        });
        
        final UpdatePropertiesCommand updatePropsCmd = new UpdatePropertiesCommand(
                configFactory, ConfigService.APP_PROTECTED
        );
        adminUi.getSettingsMenuItem().addActionListener(new CommandActionListener(adminUi, updatePropsCmd));
        
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
        final List<SelectionAction<File>> testDocActions =
                Arrays.asList(new OpenFilesOleFrame(display), sendFilesAction, 
                        new ShowMarkingUi(app, display, submitAction), new ViewScoreCards(app, display), 
                        submitAction, new DecryptTestDocNames(app));
        
        final Supplier<List<File>> filesSupplier = new DirsFiles(filenameFilter, true, dirs);
        
        final UiSelectionManager<JTree, File> fileSelectionManager = new TreeSelectionManagerForDirFiles(
                uiContext, display, filesSupplier, allow, testDocActions);

        adminUi.getViewSubmittedDocsMenuItem().addActionListener(
                new ActionListenerForSelection(
                        uiContext,
                        "Inbox -> View Files", 
                        preconditionLogin,
                        fileSelectionManager
                )
        );
        
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

        adminUi.getViewConnectedDevicesMenuItem().addActionListener(
                new ActionListenerForSelection(
                        uiContext,
                        "View Connected Devices", 
                        preconditionLogin,
                        deviceSelectionManager
                )
        );

        final Callable displayAboutCmd = () -> displayAbout.apply(uiContext, Collections.EMPTY_MAP);
        adminUi.getAboutMenuItem().addActionListener(new CommandActionListener(adminUi, displayAboutCmd));
        
        final Predicate<ActionEvent> preconditionForExit = (ae) -> {
            final int selection = JOptionPane.showConfirmDialog(adminUi, 
                    "Are you sure you want to exit?", "Exit", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
            return selection == JOptionPane.YES_OPTION;
        };
        
        final Callable exitCmd = getExitCommand(app, adminUi);
        
        adminUi.getExitMenuItem().addActionListener(new CommandActionListener(adminUi, preconditionForExit, exitCmd));

        adminUi.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                LOG.entering(this.getClass().getName(), "windowClosing(WindowEvent)");
                try{
                    exitCmd.call();
                }catch(Exception e) {
                    LOG.log(Level.WARNING, null, e);
                }
            }
        });

        LOG.fine("Done configuring Admin Frame");
    }
    
    public Callable getExitCommand(AppContext ctx, UI ui) {
        final boolean exitSystemOnExit = configFactory.getConfig(ConfigService.APP_INTERNAL)
                .getBoolean(ConfigNamesInternal.EXIT_SYSTEM_ON_UI_EXIT);
        final Callable exitCmd = () -> {
            try{
                if(!app.isShutdown()) {
                    app.shutdown();
                }
                if(!ui.isDisposed()) {
                    ui.dispose();
                }
            }finally{
                if(exitSystemOnExit) {
                    System.exit(0);
                }
            }
            return Boolean.TRUE;
        };
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