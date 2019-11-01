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

package com.looseboxes.msofficekiosk.ui;

import com.bc.config.Config;
import com.bc.ui.functions.SetLookAndFeel;
import com.looseboxes.msofficekiosk.FileNames;
import com.looseboxes.msofficekiosk.MsKioskSetup;
import com.looseboxes.msofficekiosk.commands.CommandContext;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.ui.DisplayAbout;
import com.looseboxes.msofficekiosk.functions.ui.DisplayURL;
import com.looseboxes.msofficekiosk.launchers.AdminUiLauncher;
import com.looseboxes.msofficekiosk.launchers.ExamUiLauncher;
import com.looseboxes.msofficekiosk.launchers.PreconditionForExamUiLaunch;
import com.looseboxes.msofficekiosk.launchers.UiLauncher;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.functions.UserProfileMessage;
import com.looseboxes.msofficekiosk.launchers.PreconditionForAdminUiLaunch;
import com.looseboxes.msofficekiosk.test.EditTest;
import com.looseboxes.msofficekiosk.test.PromptUserSelectTest;
import com.looseboxes.msofficekiosk.ui.admin.AdminUi;
import com.looseboxes.msofficekiosk.ui.admin.AdminUiConfigurer;
import com.looseboxes.msofficekiosk.ui.exam.ExamUi;
import com.looseboxes.msofficekiosk.ui.exam.ExamUiConfigurerImpl;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandlerImpl;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.awt.Window;
import java.io.File;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2019 1:10:30 PM
 */
@Lazy
@Configuration
public class UiConfiguration implements UiBeans {

    private static final Logger LOG = Logger.getLogger(UiConfiguration.class.getName());
    
    @Autowired private MsKioskSetup setup;
    
    @Bean public AppUiContext appUIContext(ApplicationContext springContext, ConfigFactory configFactory) {
        final Config config = configFactory.getConfig(ConfigService.APP_UI);        
        return new AppUiContextImpl(config, getUiSupplier(springContext), getMainWindowSupplier(springContext));
    }
    
    public Supplier<UI> getUiSupplier(ApplicationContext applicationContext) {
        return setup.isAdmin() ? () -> applicationContext.getBean(AdminUi.class) : 
                () -> applicationContext.getBean(ExamUi.class);
    }

//    public Supplier<Window> getMainWindowSupplier(ApplicationContext applicationContext) {
//        return setup.isAdmin() ? () -> applicationContext.getBean(AdminUi.class) : () -> null;
//    }
    public Supplier<Window> getMainWindowSupplier(ApplicationContext applicationContext) {
        return () -> null;
    }

    @Bean @Scope("prototype") public UiLauncher uiLauncher(ApplicationContext applicationContext) {
        
        return setup.isAdmin() ? new AdminUiLauncher(applicationContext.getBean(AdminUi.class)) : 
                new ExamUiLauncher(applicationContext.getBean(ExamUi.class));
    }
    
    @Bean public UiBeans.UiConfigurer uiConfigurer(ApplicationContext applicationContext) {
        return setup.isAdmin() ? 
                new AdminUiConfigurer(getAdminUiConfigurerDir(applicationContext)) : 
                new ExamUiConfigurerImpl(applicationContext.getBean(CommandContext.class));
    }
    
    public File getAdminUiConfigurerDir(ApplicationContext applicationContext) {
        return setup.getDir(FileNames.DIR_INBOX).toFile();
    }
    
    @Bean(PRECONDITION_FOR_UI_LAUNCH) @Scope("prototype") public Precondition preconditionForUiLaunch(
            ApplicationContext applicationContext, AppUiContext uiContext, ConfigFactory configFactory) {

        final Precondition precondition = setup.isAdmin() ? 
                new PreconditionForAdminUiLaunch() : 
                new PreconditionForExamUiLaunch(
                        uiContext, 
                        configFactory,
                        applicationContext.getBean(LoginManager.class),
                        applicationContext.getBean(Tests.class), 
                        applicationContext.getBean(DocumentStore.class), 
                        applicationContext.getBean(PromptUserSelectTest.class),
                        applicationContext.getBean(EditTest.class));

        LOG.log(Level.FINE, "Precondition for UI launch, type: {0}", precondition.getClass().getName());

        return precondition;
    }

    @Bean public Display display() {
        return Display.getDefault();
    }

    @Bean @Scope("prototype") public UserProfileMessage userProfileMessage(LoginManager loginManager) {
        return new UserProfileMessage(loginManager);
    }

    @Bean @Scope("prototype") public LifecycleEventHandler lifecycleEventHandler() {
        return new LifecycleEventHandlerImpl();
    }

    @Bean @Scope("prototype") public DisplayURL displayURL() {
        return new DisplayURL();
    }
    
    @Bean @Scope("prototype") public DisplayAbout displayAbout() {
        return new DisplayAbout();
    }
    
    @Bean @Scope("prototype") public Function<String, Optional<javax.swing.UIManager.LookAndFeelInfo>> lookAndFeelConfigurer() {
        return new SetLookAndFeel();
    }

    @Bean @Scope("prototype") public UiBeans.ServerSocketIncomingMessageHandler incomingMessageHandler(UI ui) {
        return (level, msg) -> {
            if(LOG.isLoggable(Level.FINE)) {
                ui.getMessageDialog().showInformationMessage(msg);
            }
            ui.setMessageText(msg);
            LOG.log(level, msg);
        };
    }
    
    @Bean @Scope("prototype") public MessageDialog messageDialog(ApplicationContext app) {
//        if(app.getBean(MsKioskSetup.class).isAdmin()) {
            return new SwingMessageDialog(app.getBean(AppUiContext.class).getMainWindowOptional().orElse(null));
//        }else{
//            return new SwtMessageDialog(app.getBean(Display.class));
//        }
    }
}
/**
 * 
    private Display display;
    @Bean public Display display() {
        final Thread uiThread = new Thread("SWT_UIThread") {
            @Override
            public void run() {
                display = new Display();
                LOG.info(() -> "T-"+Thread.currentThread().getName() + " Done creating SWT display: " + display);
            }
        };
        uiThread.setDaemon(false);
        uiThread.start();
        try{
            uiThread.join();
            LOG.info(() -> "T-"+Thread.currentThread().getName() + " Returned from creating SWT display: " + display);
        }catch(InterruptedException e) {
            LOG.log(Level.WARNING, "Thread creating SWT Display was interrupted. Thread: " + uiThread.getName(), e);
            Thread.currentThread().interrupt();
        }
        return display;
    }
 * 
 */