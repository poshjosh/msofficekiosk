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
import com.bc.ui.UIContext;
import com.bc.ui.UIContextImpl;
import com.looseboxes.msofficekiosk.commands.CommandConfiguration;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.config.ConfigServiceImpl;
import com.looseboxes.msofficekiosk.functions.InitLoggingConfig;
import com.looseboxes.msofficekiosk.functions.io.CreateNewFile;
import com.looseboxes.msofficekiosk.functions.swtjar.GetSwtJarFileNameForOsAch;
import com.looseboxes.msofficekiosk.functions.ui.LaunchTypeFromUserDetailsSupplier;
import com.looseboxes.msofficekiosk.functions.ui.LaunchTypeFromUserSelectionSupplier;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import com.looseboxes.msofficekiosk.exceptions.StartupException;
import com.looseboxes.msofficekiosk.functions.IsServerIp;
import com.looseboxes.msofficekiosk.launchers.UiLauncher;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import com.looseboxes.msofficekiosk.ui.UiConfiguration;
import com.looseboxes.msofficekiosk.ui.admin.AdminUiConfiguration;
import com.looseboxes.msofficekiosk.ui.exam.ExamUiConfiguration;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 5:32:25 AM
 */
public class MsKioskSetupImpl implements MsKioskSetup {

    private static final Logger LOG = Logger.getLogger(MsKioskSetupImpl.class.getName());
    
    private final Path homeDir;
    
    private final Consumer<String> addNamedJar; 
    
    private final String loggingConfigFile;
    
    private final Supplier<LauncherFactory.Type> launchTypeSupplier;
    
    private final ConfigFactory configFactory;
    
    private final UIContext startupUiContext;
    
    private LauncherFactory.Type launchType;
    
    private Class<?> [] configClasses;
    
    private boolean initialized;
    
    private ConfigurableApplicationContext applicationContext;

    public MsKioskSetupImpl(Path homeDir, Consumer<String> addNamedJar, String loggingConfigFile) {
        this(homeDir, addNamedJar, loggingConfigFile, new LaunchTypeFromUserDetailsSupplier(homeDir));
    }
    
    public MsKioskSetupImpl(Path homeDir,
            Consumer<String> addNamedJar, String loggingConfigFile,
            Supplier<LauncherFactory.Type> launchTypeSupplier) {
        this.homeDir = homeDir.toAbsolutePath().normalize();
        this.launchTypeSupplier = Objects.requireNonNull(launchTypeSupplier);
        this.addNamedJar = Objects.requireNonNull(addNamedJar);
        this.loggingConfigFile = loggingConfigFile;
        this.configFactory = new ConfigFactoryImpl(homeDir);
        this.startupUiContext = new UIContextImpl();
    }

    @Override
    public UIContext getStartupUiContext() {
        return startupUiContext;
    }
    
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        register(applicationContext);
    }
    
    @Override
    public void register(ConfigurableApplicationContext applicationContext) {
        final String beanName = getClass().getSimpleName();
        LOG.log(Level.INFO, "Registering singleton bean. Name: {0}, type: {1}",
                new Object[]{beanName, getClass()});
        applicationContext.getBeanFactory().registerSingleton(beanName, this);
        this.applicationContext = applicationContext;
    }
    
    @Override
    public AppContext launchApp() {
        
        requireRegistered();
        
        final Precondition precondition = this.applicationContext.getBean(UiBeans.PRECONDITION_FOR_UI_LAUNCH, Precondition.class);
        
        precondition.validate("App Launch", this);

        return this.applicationContext.getBean(UiLauncher.class).launch();
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
        if( ! isSetup()) {
            throw new IllegalStateException("Not yet Setup");
        }
        return launchType == LauncherFactory.Type.Academic_Staff || 
                launchType == LauncherFactory.Type.IT_Admin ||
                launchType == LauncherFactory.Type.Background;
    }

    @Override
    public Class<?> [] init(String [] args) {
        try{
            
            if(initialized) {
                throw new IllegalStateException("Already initialized");
            }
            
            initialized = true;
            
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
            
            configClasses = new Class[]{MsKioskConfiguration.class, CommandConfiguration.class, 
                    UiConfiguration.class, AdminUiConfiguration.class, ExamUiConfiguration.class};
            
//            configClasses = promptUserForSpringConfigurationClasses();
            
            return configClasses;
            
        }catch(IOException e) {
            
            LOG.log(Level.WARNING, null, e);
            
            throw new RuntimeException(e);
        }
    }

    protected Class<?> [] promptUserForSpringConfigurationClasses() {
        
        this.launchType = initLaunchType();
        
        return launchType == LauncherFactory.Type.None ? new Class[0] : 
                getSpringConfigClassesProvider().apply(launchType);
    }
    
    @Override
    public LauncherFactory.Type initLaunchType() {
        
        LauncherFactory.Type output = launchTypeSupplier.get();
        
        if(output == LauncherFactory.Type.None) {
            output = new LaunchTypeFromUserSelectionSupplier().get();
        }
        
        this.launchType = output;
        
        return output;
    }

    @Override
    public SpringConfigClassesProvider getSpringConfigClassesProvider() {
        return new SpringConfigClassesProviderImpl();
    }

    @Override
    public boolean isSetup() {
        return configClasses != null && configClasses.length > 0;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public LauncherFactory.Type getLaunchType() {
        return launchType;
    }

    @Override
    public Class<?>[] getConfigClasses() {
        return configClasses;
    }

    @Override
    public String getLoggingConfigFile() {
        return loggingConfigFile;
    }
    
    @Override
    public Path getDir(String name) {
        final Path dir = homeDir.resolve(name);
        if( ! Files.exists(dir)) {
            try{
                Files.createDirectories(dir);
                LOG.log(Level.INFO, "Created dir: {0}", dir);
            }catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return dir;
    }

    @Override
    public Path getHomeDir() {
        return homeDir;
    }
    
    public void requireRegistered() {
        if(this.applicationContext == null) {
            throw new StartupException("Setup not registered");
        }
    }

    @Override
    public ConfigFactory getConfigFactory() {
        return configFactory;
    }
}
