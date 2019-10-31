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

package com.looseboxes.msofficekiosk.launchers;

import com.looseboxes.msofficekiosk.exceptions.StartupException;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.MsKioskSetup;
import com.looseboxes.msofficekiosk.popups.ShowWarningMessage;
import com.looseboxes.msofficekiosk.ui.UI;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager.LookAndFeelInfo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.config.ConfigNamesUI;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 4:44:44 PM
 */
public abstract class AbstractUiLauncher<U> 
        implements UiLauncher, UiInitializationCallback, Runnable {

    private static final Logger LOG = Logger.getLogger(AbstractUiLauncher.class.getName());
    
    @Autowired private AppContext context;

    @Autowired private MsKioskSetup setup;
        
    @Autowired private Function<String, Optional<LookAndFeelInfo>> lookAndFeelConfigurer;

    private UI ui;
        
    private int exitStatus;
        
    public AbstractUiLauncher() { }

    @Override
    public AppContext launch(){
        
        run();
        
        return context;
    }
    
    public abstract void initUi(UiInitializationCallback callback);
    
    public void showUi(UI ui) {
        if(!ui.isShowing()) {
            ui.show();
        }
    }
    
    public void setLookAndFeel() {
        final String lookAndFeel = context.getConfig(ConfigService.APP_UI).getString(ConfigNamesUI.LOOK_AND_FEEL);
        if(lookAndFeel != null) {
            this.lookAndFeelConfigurer.apply(lookAndFeel);
        }
    }
    
    @Override
    public void run() {
        
        try{
            
            LOG.info(() -> "Started UI Launcher");

            this.setLookAndFeel();
            
            this.setup.getStartupUiContext().showProgressBarPercent("..Loading", -1);
            
            this.initUi(this);

        }catch(RuntimeException e) {
            
            this.onLaunchFailed(context, e);
            
            this.setup.getStartupUiContext().showProgressBarPercent(100);
        }
    }
    
    @Override
    public void onUiInitialized(UI ui) {
        try{
            
            this.ui = ui;
            
            this.onLaunchCompleted(context, ui);
            
        }catch(RuntimeException e) {
            
            this.onLaunchFailed(context, e);
        }
    }
    
    public void onLaunchCompleted(AppContext context, UI ui) { 
        exitStatus = 0;
        LOG.info("Done launching app");
        this.setup.getStartupUiContext().showProgressBarPercent(100);
    }

    public void onLaunchFailed(Exception e) {
        exitStatus = 1;
        this.onLaunchFailed(context, e);
    }
    
    public void onLaunchFailed(AppContext context, Exception e) {
        exitStatus = 1;
        LOG.log(Level.WARNING, "Exception launching app", e);
        this.onLaunchFailed(context);
    }
    
    public void onLaunchFailed(AppContext context) {
        try{
            exitStatus = 1;
            this.setup.getStartupUiContext().showProgressBarPercent(100);
            this.shutdownAndExit(context);
        }catch(RuntimeException e){
            LOG.log(Level.WARNING, "Exception shutting down and exiting", e);
            System.exit(exitStatus);
        }
    }

    public void shutdownAndExit() {
        this.shutdownAndExit(context);
    }
    
    public void shutdownAndExit(AppContext context) {
        try{
            this.shutdown(context);
        }finally{
            LOG.info("Exiting");
            System.exit(exitStatus);
        }
    }
    
    public void shutdown(AppContext context) {
        LOG.info("Shutting down");
        try{
            if(context != null && !context.isShutdown()) {
                context.shutdown();
            }    
        }finally{
            if(ui != null && !ui.isDisposed()) {
                ui.dispose();
            }
        }
        LOG.info("Shutdown completed successfully");
    }
    
    public void complainAndTerminate(Display display, String warningMessage) 
            throws StartupException {
        
        final Shell shell = new Shell(display);
        
        try{
            new ShowWarningMessage(shell).apply(warningMessage);
        }finally{
            shell.dispose();
        }
        
        throw new StartupException(warningMessage);
    }

    public int getExitStatus() {
        return exitStatus;
    }
}
