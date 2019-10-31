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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
    
    public static final Path DIR_HOME = Paths.get(System.getProperty("user.home"), "mswordbox");

    public static void main(String [] args) {

        try{
            
            LOG.log(Level.INFO, "Command line args: {0}", Arrays.toString(args));
            
            LOG.log(Level.INFO, "Default TimeZone: {0}", TimeZone.getDefault());
            
            final MsKioskSetup setup = new MsKioskSetupDev(DIR_HOME);

            final Class<?> [] configClasses = setup.init(args);

            if( ! setup.isSetup()) {

                throw new RuntimeException("You did not enter/select valid details for the app");
            }    

            final ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(configClasses);
            
            final String [] activeProfiles = ctx.getEnvironment().getActiveProfiles();

            LOG.log(Level.INFO, "Spring Context initialized. Active profiles: {0}", 
                    (activeProfiles==null?null:Arrays.toString(activeProfiles)));

            setup.register(ctx);
            
            setup.initLaunchType();

            final AppContext appContext = setup.launchApp();
            
            appContext.scheduleDataUpdate();

            if(appContext.isAdmin()) {
                appContext.startSocketServerAsync();
            }
        }catch(Throwable e){
            
            e.printStackTrace();
            
            final String msg = "Encountered an unexpected problem while starting the application";
            
            LOG.log(Level.WARNING, msg, e);
            
            Object errMsg = e.getLocalizedMessage() == null ? "" : 
                    e.getLocalizedMessage().length() <= 100 ? e.getLocalizedMessage() :
                    e.getLocalizedMessage().substring(0, 100) + "...";
            
            JOptionPane.showMessageDialog(null, msg + "\n" + errMsg, 
                    "Startup Error", JOptionPane.WARNING_MESSAGE);
        }    
    }
}
