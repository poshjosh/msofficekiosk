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

package com.looseboxes.msofficekiosk.security;

import com.bc.config.Config;
import com.bc.diskcache.DiskLruCacheContext;
import com.bc.diskcache.DiskLruCacheContextImpl;
import com.bc.diskcache.DiskLruCacheIx;
import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.FileNames;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.mapper.MapperJackson;
import com.looseboxes.msofficekiosk.MsKioskConfiguration;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.CacheProvider;
import com.looseboxes.msofficekiosk.net.CookieJarInMemoryStaticCache;
import com.looseboxes.msofficekiosk.net.GetDevicedetails;
import com.looseboxes.msofficekiosk.net.OkHttpClientProvider;
import com.looseboxes.msofficekiosk.net.RequestClient;
import com.looseboxes.msofficekiosk.net.SyncDataWithServer;
import com.looseboxes.msofficekiosk.net.SyncDataWithServerImpl;
import com.looseboxes.msofficekiosk.ui.SwingMessageDialog;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import com.looseboxes.msofficekiosk.net.RequestClientProvider;
import com.looseboxes.msofficekiosk.net.Response;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.document.DocumentStoreImpl;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.test.TestsImpl;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2019 2:12:10 AM
 */
public final class OneOffLoginManagerProvider {

    private static final Logger LOG = Logger.getLogger(OneOffLoginManagerProvider.class.getName());
    
    private final CookieJar cookieJar;
    
    private static class Credentials implements CredentialsSupplier{
        private LoginManager loginManager;
        private Credentials() {
            this(null);
        }
        private Credentials(LoginManager loginManager) {
            this.loginManager = loginManager;
        }
        @Override
        public Map get() {
            return new CredentialsSupplierFromLoggedInUser(loginManager).get();
        }
    } 

    public OneOffLoginManagerProvider() {
        this(new CookieJarInMemoryStaticCache());
    }
    
    public OneOffLoginManagerProvider(CookieJar cookieJar) {
        this.cookieJar = Objects.requireNonNull(cookieJar);
    }

    public LoginManager get(Path homeDir) {
        return get(homeDir, MsKioskConfiguration.DEFAULT_CACHE_NAME);
    }
    
    public LoginManager get(Path homeDir, String cacheName) {
        
        final ConfigFactory configFactory = new ConfigFactoryImpl(homeDir);
        final Config internal = configFactory.getConfig(ConfigService.APP_INTERNAL);
        final OkHttpClient httpClient = new OkHttpClientProvider().apply(internal, cookieJar);
        final RequestClientProvider rcp = new RequestClientProvider(configFactory, httpClient);
        
        final Mapper mapper = new MapperJackson();
        final int defaultCacheSizeBytes = internal.getInt(ConfigNames.CACHE_EACH_DEFAULT_SIZE_BYTES);
        final DiskLruCacheContext cacheContext = new DiskLruCacheContextImpl(defaultCacheSizeBytes);
        final Cache cache = new CacheProvider(cacheContext, homeDir.resolve(FileNames.DIR_CACHE), mapper).apply(cacheName);
        
        final Tests tests = new TestsImpl(cache);
        final Path tempDir = getTempDir(homeDir);

        final Credentials credentials = new Credentials();
        final RequestClient rc = rcp.get((response) -> response);
        final DocumentStore documentStore = new DocumentStoreImpl(
                tempDir, configFactory, 
                rc, 
                credentials, (DiskLruCacheIx)cache.getDelegate());
        
        final Consumer<Response<Object>> c = (r) -> {
            new Thread() {
                @Override
                public void run() {
                    final List<Test> tt = tests.get();
                    for(Test t : tt) {
                        final List<Document> docs = t.getTestsettingList().stream().map((e) -> e.getTestsetting()).collect(Collectors.toList());
                        documentStore.fetch(docs);
                    }
                }
            }.start();
        };
        
//        final OnResponseDownloadTestsettings downloadTestsettings = 
//                new OnResponseDownloadTestsettings(() -> tests, () -> testsettings, configFactory, mapper);
        
        final SyncDataWithServer syncDataWithServer = new SyncDataWithServerImpl(
                rcp.forJson(mapper), cache, mapper, c);

        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        final Config uiConfig = configFactory.getConfig(ConfigService.APP_UI);

        final Login login = new LoginToServer(
            syncDataWithServer,
            configFactory,
            () -> new GetDevicedetails(null).apply(config),
            mapper
        );
        
        final LoginManager loginManager = new LoginManagerImpl(
                login,
                new CredentialsSupplierFromUserPrompt(uiConfig, false),
                new SwingMessageDialog(null)
        ){
            @Override
            public boolean promptUserLogin(int n) {
                try{
                    return super.promptUserLogin(n); 
                }finally{
                    try{
                        final boolean closed = cache.isClosed();
                        LOG.log(Level.FINE, "Cache is closed: {0}", closed);
                        if( ! closed) {
                            cache.close();
                            LOG.log(Level.FINE, "Closed Cache named: {0}", cacheName);
                        }
                    }catch(IOException e) {  
                        LOG.log(Level.WARNING, null, e);
                    }finally{
                        LOG.fine("Closing CacheContext");
                        cacheContext.closeAndRemoveAll();
                        LOG.info("Closed CacheContext");
                    }
                }
            }
        };
        
        credentials.loginManager = loginManager;
        
//        final LoginListener loginListener = new LoginListenerDownloadTestSettings(
//                () -> tests, () -> tet, configFactory);

//        loginManager.addListener(loginListener);
        
        return loginManager;
    }
    
    private Path getTempDir(Path homeDir) {
        final Path tempDir = homeDir.resolve(FileNames.DIR_TEMP);
        if( ! Files.exists(tempDir)){
            try{
                Files.createDirectories(tempDir);
            }catch(IOException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        return tempDir;
    }
}
