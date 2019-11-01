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

package com.looseboxes.msofficekiosk;

import com.looseboxes.msofficekiosk.test.OpenedFileManager;
import java.util.Objects;
import com.bc.config.Config;
import com.bc.diskcache.DiskLruCacheContext;
import com.bc.socket.io.BcSocketClientImpl;
import com.bc.socket.io.BcSocketServerImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.socket.io.BcSocketServer;
import com.bc.socket.io.BcSocketClient;
import com.bc.socket.io.messaging.SaveFileMessages;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.bc.util.Util;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.net.ComboEndpointTask;
import com.looseboxes.msofficekiosk.net.GetDevicedetails;
import com.looseboxes.msofficekiosk.net.InvalidConfigurationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.looseboxes.msofficekiosk.security.LoginManager;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 5:03:42 AM
 */
public class AppContextImpl implements AppContext {

    private transient static final Logger LOG = Logger.getLogger(AppContextImpl.class.getName());
    
    @Lazy @Autowired private SaveFileMessages incomingFileMessageConsumer;
    
    @Lazy @Autowired private LoginManager loginManager;
    
    @Lazy @Autowired private DiskLruCacheContext diskLruCacheContext;
    
    @Lazy @Autowired private GetDevicedetails getDevicedetails;
    
    @Lazy @Autowired private OpenedFileManager openedFileManager;
    
    @Lazy @Autowired private ComboEndpointTask dataUpdateTask;

    @Lazy @Autowired private MsKioskSetup setup;

    //@todo make a property
    private final int stopTimeoutMillis = 3_000;

    private ScheduledExecutorService executor;

    private BcSocketServer server;
    
    private boolean shutdown;
    
    public AppContextImpl() { }

    @Override
    public Devicedetails getDevicedetails() {
        return getDevicedetails.apply(this.getConfig(ConfigService.APP_PROTECTED));
    }
    
    @Override
    public void scheduleDataUpdate() {

        if(executor != null) {
            throw new IllegalStateException();
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        
        //@todo make these properties
        executor.scheduleAtFixedRate(dataUpdateTask, 10, 10, TimeUnit.MINUTES);
    } 
    
    @Override
    public boolean isServer() {
        if(this.setup == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return setup.isServer();
    }
    
    @Override
    public boolean isServerHost(String host) {
        if(this.setup == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return setup.isServerHost(host);
    }

    @Override
    public boolean isAdmin() {
        if(this.setup == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return this.setup.isAdmin();
    }

    @Override
    public OpenedFileManager getOpenedFileManager() {
        return openedFileManager;
    }
    
    @Override
    public BcSocketServer startSocketServerAsync() {
        if(this.server == null) {
            this.server = this.createAndStartServerAsync(this.loginManager.getLoggedInUserNameOrDefault());
        }else{
            if(!server.isStarting() && !server.isStarted()) {
                this.startServerAsync(server);
            }
        }
        return this.server;
    }
    
    @Override
    public void refresh() {
        this.refreshServer();
    }
    
    public boolean refreshServer() {
        
        if(this.server == null || this.server.isStarting() || this.server.isStarted()) {
            return false;
        }
        
        final String prev = this.server.getServiceDescriptor().getName();
        final String curr = this.loginManager.getLoggedInUserNameOrDefault();

        if( ! Objects.equals(prev, curr)) {

            try{
                this.server.stop(stopTimeoutMillis);
            }catch(InterruptedException e) {
                LOG.log(Level.WARNING, "Exception stopping Server named: " + prev, e);
            }

            this.server = this.createAndStartServerAsync(curr);
            
            return true;
            
        }else{
            
            return false;
        }
    }
    
    public BcSocketServer createAndStartServerAsync(String name) {
        this.server = new BcSocketServerImpl(name, this.getSocketPort());
        this.startServerAsync(this.server);
        return this.server;
    }
    
    private void startServerAsync(BcSocketServer srv) {
        Objects.requireNonNull(srv);
        srv.startAsync(this.incomingFileMessageConsumer);
    }
    
    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }

    @Override
    public void shutdown() {
        if(this.shutdown) {
            return;
        }
        LOG.info("Shutting down AppContext");
        this.shutdown = true;
        try{
            
            if(executor != null) {
                Util.shutdownAndAwaitTermination(executor, stopTimeoutMillis, TimeUnit.MILLISECONDS);
            }
            
            if(server != null) {
                server.stop(stopTimeoutMillis);
            }
            
            if(diskLruCacheContext != null) {
                diskLruCacheContext.closeAndRemoveAll();
            }
                    
            LOG.info("Done shutting down AppContext");

        }catch(Exception e) {
            LOG.log(Level.WARNING, "Exception stopping SocketServer", e);
        }
    }

    @Override
    public BcSocketServer getSocketServer() {
        return this.server;
    }

    @Override
    public BcSocketClient getSocketClient() {
        return new BcSocketClientImpl(this.getSocketPort());
    }
    
    private int getSocketPort() {
        final Config<Properties> config = this.getConfig(ConfigService.APP_PROTECTED);
        final int portNumber = config.getInt(ConfigNames.SOCKET_PORT_NUMBER, -1);
        if(portNumber == -1) {
            throw new InvalidConfigurationException("Port number for socket server not configured");
        }
        return portNumber;
    }

    @Override
    public Config<Properties> getConfig(String id) {
        return getConfigFactory().getConfig(id);
    }
    
    @Override
    public ConfigFactory getConfigFactory() {
        return getSetup().getConfigFactory();
    }

    @Override
    public MsKioskSetup getSetup() {
        return setup;
    }
}
/**
 * 

    @Override
    public void clear() {
        getSetup().getConfigFactory().clear();
    }

    @Override
    public void clear(String id) {
        getSetup().getConfigFactory().clear(id);
    }

    @Override
    public void saveConfig(Config config, String id) throws IOException {
        getSetup().getConfigFactory().saveConfig(config, id);
    }

    @Override
    public ConfigService createConfigService(String id) {
        return getSetup().getConfigFactory().createConfigService(id);
    }

    @Override
    public void loadConfigs() throws IOException {
        getSetup().getConfigFactory().loadConfigs();
    }

    @Override
    public Config<Properties> loadConfig(String id) throws IOException {
        return getSetup().getConfigFactory().loadConfig(id);
    }

 * 
 */