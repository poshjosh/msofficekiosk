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

package com.looseboxes.msofficekiosk;

import com.bc.config.Config;
import com.bc.socket.io.functions.GetLocalIpAddress;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.config.ConfigServiceImpl;
import com.looseboxes.msofficekiosk.functions.InitLoggingConfig;
import com.looseboxes.msofficekiosk.functions.io.CreateNewFile;
import com.looseboxes.msofficekiosk.functions.swtjar.GetSwtJarFileNameForOsAch;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import com.looseboxes.msofficekiosk.exceptions.StartupException;
import com.looseboxes.msofficekiosk.functions.IsServerIp;
import com.looseboxes.msofficekiosk.launchers.UiLauncher;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 5:32:25 AM
 */
public class MsKioskSetupImpl implements MsKioskSetup {

    private static final Logger LOG = Logger.getLogger(MsKioskSetupImpl.class.getName());
    
    private final String loggingConfigFile;
    
    private final ConfigFactory configFactory;
    
    private LauncherFactory.Type launchType;
    
    private ConfigurableApplicationContext applicationContext;

    public MsKioskSetupImpl(String [] args, Path homeDir,
            Consumer<String> addNamedJar, String loggingConfigFile) {

        homeDir = homeDir.toAbsolutePath().normalize();

        this.loggingConfigFile = loggingConfigFile;
        this.configFactory = new ConfigFactoryImpl(homeDir);
        try{
            
            final AtomicBoolean swtJarAdded = new AtomicBoolean(false);
            final List<String> argList = Arrays.asList(args);
            argList.stream()
                    .filter((jarFile) -> jarFile.startsWith("org.eclipse.swt.win32"))
                    .findFirst().ifPresent((jarFile) -> {
            
                LOG.log(Level.INFO, "Adding swt jar file from command line args: {0}", jarFile);
                addNamedJar.accept(jarFile);

                swtJarAdded.set(true);
            });
            
            new CreateNewFile().execute(homeDir.toFile(), Boolean.TRUE);

            if(loggingConfigFile != null) {
                new InitLoggingConfig(homeDir.resolve(FileNames.DIR_LOGS), loggingConfigFile).run();
            }

            if( ! swtJarAdded.get()) {
                
                final ConfigService configSvc = new ConfigServiceImpl(homeDir, ConfigService.APP_INTERNAL);
                final Config config = configSvc.loadConfig();
                final String jarFileName;
                if(argList.contains("64")) {
                    jarFileName = config.getString(ConfigNames.SWT_JARFILE_64BIT).trim();
                    LOG.info(() -> "swt jar file from command line os.arch args: " + jarFileName);
                }else if(argList.contains("32")) {
                    jarFileName = config.getString(ConfigNames.SWT_JARFILE_32BIT).trim();
                    LOG.info(() -> "swt jar file from command line os.arch args: " + jarFileName);
                }else{
                    jarFileName = new GetSwtJarFileNameForOsAch().apply(config);
                }

                addNamedJar.accept(jarFileName);
            }
            
        }catch(IOException e) {
            
            LOG.log(Level.WARNING, null, e);
            
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final String beanName = getClass().getSimpleName();
        LOG.log(Level.INFO, "Registering singleton bean. Name: {0}, type: {1}",
                new Object[]{beanName, getClass()});
        applicationContext.getBeanFactory().registerSingleton(beanName, this);
        this.applicationContext = applicationContext;
    }
    
    @Override
    public void launchApp(LauncherFactory.Type launchType) {
        
        requireRegistered();
        
        LOG.log(Level.INFO, "Launching App with launch type: {0}", launchType);
        
        this.launchType = Objects.requireNonNull(launchType);
        
        final Precondition precondition = this.applicationContext.getBean(UiBeans.PRECONDITION_FOR_UI_LAUNCH, Precondition.class);
        
        precondition.validate("App Launch", this);

        final Display display = this.applicationContext.getBean(Display.class);
        
        display.syncExec(() -> {

            applicationContext.getBean(UiLauncher.class).launch();
        });
    }
    
    @Override
    public boolean isServer() {
        final String ownIp = new GetLocalIpAddress().apply(null);
        return isServerHost(ownIp);
    }
    
    @Override
    public boolean isServerHost(String host) {
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        return new IsServerIp(config).test(host);
    }
    
    @Override
    public boolean isAdmin() {
        return isAdmin(launchType);
    }

    @Override
    public Path getDir(String name) {
        return FilePaths.getDir(name);
    }

    @Override
    public boolean isAdmin(LauncherFactory.Type launchType) {
        return launchType == LauncherFactory.Type.Academic_Staff || 
                launchType == LauncherFactory.Type.IT_Admin ||
                launchType == LauncherFactory.Type.Background;
    }

    private void requireRegistered() {
        if(this.applicationContext == null) {
            throw new StartupException("Setup not registered");
        }
    }

    @Override
    public LauncherFactory.Type getLaunchType() {
        return launchType;
    }

    @Override
    public String getLoggingConfigFile() {
        return loggingConfigFile;
    }
    
    @Override
    public ConfigFactory getConfigFactory() {
        return configFactory;
    }
}
