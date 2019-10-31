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

package com.looseboxes.msofficekiosk;

import com.looseboxes.msofficekiosk.commands.ExitCommand;
import com.looseboxes.msofficekiosk.functions.ui.DisplayException;
import com.looseboxes.msofficekiosk.functions.ui.LaunchTypeFromUserSelectionSupplier;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TimeZone;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2018 12:44:36 PM
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    
    public static final Path DIR_HOME = Paths.get(System.getProperty("user.home"), "msofficekiosk");

    public static void main(String [] args) {

        try{
            
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandlerImpl());
            
            LOG.log(Level.INFO, "Command line args: {0}", Arrays.toString(args));
            
            LOG.log(Level.INFO, "Default TimeZone: {0}", TimeZone.getDefault());
            
            final MsKioskSetup setup = new MsKioskSetupDev(args, DIR_HOME);

            final Class<?> [] configClasses = setup.getConfigClasses();

            final ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(configClasses);
            
            final String [] activeProfiles = ctx.getEnvironment().getActiveProfiles();

            LOG.log(Level.INFO, "Spring Context initialized. Active profiles: {0}", 
                    (activeProfiles==null?null:Arrays.toString(activeProfiles)));

            setup.initialize(ctx);
            
//            LauncherFactory.Type launchType = new LaunchTypeFromUserDetailsSupplier(Application.workingDir).get();
//            if(launchType == LauncherFactory.Type.None) {
//                launchType = new LaunchTypeFromUserSelectionSupplier().get();
//            }
            final LauncherFactory.Type launchType = new LaunchTypeFromUserSelectionSupplier().get();

            if(launchType == LauncherFactory.Type.None) {
                new ExitCommand(ctx.getBean(AppContext.class)).call();
            }

            setup.launchApp(launchType);
            
            final AppContext appContext = ctx.getBean(AppContext.class);
            
            appContext.scheduleDataUpdate();

            if(appContext.isAdmin()) {
                appContext.startSocketServerAsync();
            }
        }catch(Throwable e){
            try{
                new DisplayException().accept(e);
            }finally{
                System.exit(1);
            }
        }    
    }

    private static class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler{
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.log(Level.WARNING, "Uncaught exception by: " + t, e);
        }
    }
}
