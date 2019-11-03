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

import com.looseboxes.msofficekiosk.mapper.MapperJackson;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.test.OpenedFileManagerImpl;
import com.looseboxes.msofficekiosk.test.OpenedFileManager;
import com.looseboxes.msofficekiosk.test.TestsImpl;
import com.looseboxes.msofficekiosk.test.Tests;
import com.bc.config.Config;
import com.bc.diskcache.DiskLruCacheContext;
import com.bc.diskcache.DiskLruCacheContextImpl;
import com.bc.diskcache.DiskLruCacheIx;
import com.bc.socket.io.messaging.SaveFileMessages;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.functions.SaveFileMessagesNotifyUser;
import org.springframework.context.annotation.Bean;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.CacheProvider;
import com.looseboxes.msofficekiosk.net.DownloadTestsettings;
import com.looseboxes.msofficekiosk.functions.admin.GetDevicedetailsForUser;
import com.looseboxes.msofficekiosk.messaging.MessageSender;
import com.looseboxes.msofficekiosk.messaging.MessageSenderImpl;
import com.looseboxes.msofficekiosk.net.ComboEndpointTask;
import com.looseboxes.msofficekiosk.net.CookieJarInMemoryStaticCache;
import com.looseboxes.msofficekiosk.net.GetDevicedetails;
import com.looseboxes.msofficekiosk.net.OkHttpClientProvider;
import com.looseboxes.msofficekiosk.net.SyncDataWithServerImpl;
import com.looseboxes.msofficekiosk.security.LoginListenerRefreshAppContext;
import java.io.IOException;
import java.util.Properties;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import com.looseboxes.msofficekiosk.net.SyncDataWithServer;
import com.looseboxes.msofficekiosk.security.CredentialsSupplierFromUserPrompt;
import com.looseboxes.msofficekiosk.security.Login;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.LoginManagerImpl;
import com.looseboxes.msofficekiosk.security.LoginToServer;
import com.looseboxes.msofficekiosk.security.PreconditionLogin;
import com.looseboxes.msofficekiosk.document.TestDocumentBuilder;
import com.looseboxes.msofficekiosk.document.TestDocumentBuilderImpl;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.io.File;
import java.nio.charset.Charset;
import com.looseboxes.msofficekiosk.test.TestFileProvider;
import com.looseboxes.msofficekiosk.net.OnResponseDownloadTestsettings;
import com.looseboxes.msofficekiosk.net.RequestClient;
import com.looseboxes.msofficekiosk.net.RequestClientProvider;
import com.looseboxes.msofficekiosk.net.Response;
import com.looseboxes.msofficekiosk.net.ResponseHandler;
import com.looseboxes.msofficekiosk.net.SimpleResponseHandler;
import com.looseboxes.msofficekiosk.security.CredentialsSupplier;
import com.looseboxes.msofficekiosk.security.CredentialsSupplierFromLoggedInUser;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.document.DocumentStoreImpl;
import com.looseboxes.msofficekiosk.functions.IsServerIp;
import com.looseboxes.msofficekiosk.functions.StudentGroupListSupplier;
import com.looseboxes.msofficekiosk.net.UrlBuilder;
import com.looseboxes.msofficekiosk.net.UrlBuilderImpl;
import com.looseboxes.msofficekiosk.net.TestDocSenderFactory;
import com.looseboxes.msofficekiosk.net.TestDocSenderFactoryImpl;
import com.looseboxes.msofficekiosk.security.LoginListenerUpdateUi;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2019 5:08:51 PM
 */
@Lazy
@Configuration
public class MsKioskConfiguration {

    private static final Logger LOG = Logger.getLogger(MsKioskConfiguration.class.getName());
    
    public static final String DEFAULT_CACHE_NAME = "mskiosk.cache.default";
    
    @Bean public StudentGroupListSupplier studentGroupListSupplier() {
        return () -> Collections.EMPTY_LIST;
    } 
    
    @Bean public AppContext appContext() {

        final  AppContext appContext = new AppContextImpl();

        return appContext;
    }
    
    @Bean @Scope("prototype") public TestDocumentBuilder testDocumentBuilder(
            AppContext app, TestFileProvider testFileProvider, LoginManager loginManager) {
        return new TestDocumentBuilderImpl(app, testFileProvider, loginManager);
    }
    
    @Bean public TestFileProvider testFileProvider(OpenedFileManager openedFileManager) {
        return openedFileManager;
    }
    
    @Bean public OpenedFileManager openedFileManager(
            AppContext appContext, Tests tests, LoginManager loginManager) {
        return new OpenedFileManagerImpl(appContext, tests, loginManager);
    }
    
    @Bean Tests tests(@Qualifier(MsKioskConfiguration.DEFAULT_CACHE_NAME) Cache cache) {
        return new TestsImpl(cache);
    }

    @Bean DocumentStore documentStore(
            ConfigFactory configFactory, 
            @Qualifier(MsKioskConfiguration.DEFAULT_CACHE_NAME) Cache cache,
            RequestClientProvider requestClientProvider,
            CredentialsSupplier credentialsSupplier) {
        
        final RequestClient<okhttp3.Response> requestClient = requestClientProvider.get((response) -> (okhttp3.Response)response);
        
        final DiskLruCacheIx diskLruCache = (DiskLruCacheIx)cache.getDelegate();
        
        return new DocumentStoreImpl(FilePaths.getDir(FileNames.DIR_TEMP), 
                configFactory, requestClient, credentialsSupplier, diskLruCache);
    }
    
    @Bean @Scope("prototype") public CredentialsSupplierFromLoggedInUser credentialsSupplier(LoginManager loginManager) {
        return new CredentialsSupplierFromLoggedInUser(loginManager);
    }

    @Bean @Scope("prototype") public Login login(ApplicationContext spring) {
        final ConfigFactory configFactory = spring.getBean(ConfigFactory.class);
        final SyncDataWithServer syncDataWithServer = spring.getBean(SyncDataWithServer.class);
        final GetDevicedetails devicedetailsProvider = spring.getBean(GetDevicedetails.class);
        final Mapper mapper = spring.getBean(Mapper.class);
                    
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        final Login login = new LoginToServer(
                syncDataWithServer,
                configFactory,
                () -> devicedetailsProvider.apply(config),
                mapper
        );
        return login;
    }

    @Bean public LoginManager loginManager(ApplicationContext spring) {
       
        final AppContext app = spring.getBean(AppContext.class);
        final Config uiConfig = app.getConfigFactory().getConfig(ConfigService.APP_UI);
        final Login login = spring.getBean(Login.class);
        final MessageDialog messageDialog = spring.getBean(MessageDialog.class);
        final LoginManager loginManager = new LoginManagerImpl(
                login,
                new CredentialsSupplierFromUserPrompt(uiConfig, false),
                messageDialog);
        
        loginManager.addListener(new LoginListenerRefreshAppContext(app));
        
        loginManager.addListener(new LoginListenerUpdateUi(spring));
        
//        loginManager.addListener(new LoginListenerDownloadTestSettings(
//                () -> spring.getBean(Tests.class), () -> spring.getBean(DocumentStore.class), app));
        
        return loginManager;
    }
    
    @Bean @Scope("prototype") public PreconditionLogin preconditionLogin(LoginManager loginManager) {
        return new PreconditionLogin(loginManager);
    }
//    @Bean(DEFAULT_CACHE_NAME) public Cache cache(DiskLruCacheContext dcc, Mapper mapper) {
    @Bean(DEFAULT_CACHE_NAME) public Cache cache(ApplicationContext spring) {
        final DiskLruCacheContext dcc = spring.getBean(DiskLruCacheContext.class);     
        final Mapper mapper = spring.getBean(Mapper.class);    
        return new CacheProvider(dcc, FilePaths.getDir(FileNames.DIR_CACHE), mapper).apply(DEFAULT_CACHE_NAME);
    }
    
    @Bean public DiskLruCacheContext diskLruCacheContext(ConfigFactory configFactory) {
        final Config config = configFactory.getConfig(ConfigService.APP_INTERNAL);
        final int defaultCacheSizeBytes = config.getInt(ConfigNames.CACHE_EACH_DEFAULT_SIZE_BYTES);
        return new DiskLruCacheContextImpl(defaultCacheSizeBytes);
    }
    
    @Bean public CookieJar cookieJar() {
        return new CookieJarInMemoryStaticCache();
    }
    
    @Bean @Scope("prototype") public UrlBuilder urlBuilder(ConfigFactory configFactory) {
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        return new UrlBuilderImpl(config);
    }
    
    @Bean @Scope("prototype") public IsServerIp isServerIp(ConfigFactory configFactory) {
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        return new IsServerIp(config);
    }

    @Bean public OkHttpClient okhttpClient(ConfigFactory configFactory, CookieJar cookieJar) {
        final Config config = configFactory.getConfig(ConfigService.APP_INTERNAL);
        return new OkHttpClientProvider().apply(config, cookieJar);
    }

    @Bean @Scope("prototype") public ComboEndpointTask comboEndpointTask(
            ConfigFactory configFactory, 
            CredentialsSupplier credentialsSupplier, GetDevicedetails getDeviceDetails,
            RequestClient<Response<Object>> requestClient, 
            @Qualifier(MsKioskConfiguration.DEFAULT_CACHE_NAME) Cache cache, 
            Mapper mapper, OnResponseDownloadTestsettings onResponseDownloadTestsettings) {
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        return new ComboEndpointTask(configFactory, 
                credentialsSupplier, () -> getDeviceDetails.apply(config), 
                requestClient, cache, mapper, onResponseDownloadTestsettings);
    }

    @Bean @Scope("prototype") public SyncDataWithServer syncDataWithServer(
                        RequestClient<Response<Object>> requestClient,
                        @Qualifier(MsKioskConfiguration.DEFAULT_CACHE_NAME) Cache cache,
                        Mapper mapper,
                        OnResponseDownloadTestsettings onResponseDownloadTestsettings) {
    
        return new SyncDataWithServerImpl(requestClient, cache, mapper, onResponseDownloadTestsettings);
    }
    
    @Bean @Scope("prototype") OnResponseDownloadTestsettings onResponseDownloadTestsettings(
            DownloadTestsettings downloadTestsettings, Mapper mapper) {
        
        return new OnResponseDownloadTestsettings(downloadTestsettings, mapper);
    }
    
    @Bean @Scope("prototype") DownloadTestsettings downloadTestsettings(
            ApplicationContext spring, ConfigFactory configFactory) {
        
        return new DownloadTestsettings(
                () -> spring.getBean(Tests.class),
                () -> spring.getBean(DocumentStore.class),
                configFactory
        );
    }
            
    @Bean @Scope("prototype") public RequestClient<Response<Object>> requestClient(
            RequestClientProvider requestClientProvider, Mapper mapper) {
        return requestClientProvider.forJson(mapper);
    }
    
    @Bean @Scope("prototype") public RequestClientProvider requestClientProvider(
            ConfigFactory configFactory, OkHttpClient httpClient) {
        return new RequestClientProvider(configFactory, httpClient);
    }

    @Bean @Scope("prototype") Mapper mapConverter() {
        return new MapperJackson();
    }
    
    @Bean @Scope("prototype") public MessageSender messageSender(Mapper mapper, 
            ConfigFactory configFactory, RequestClient<Response<Object>> requestClient, ResponseHandler responseHandler) {
        return new MessageSenderImpl(mapper, configFactory, requestClient, responseHandler);
    }
    
    @Bean @Scope("prototype") public ResponseHandler simpleResponseHandler(MessageDialog messageDialog) {
        return new SimpleResponseHandler(messageDialog);
    }

    @Bean public SaveFileMessages saveFileMessages(ApplicationContext spring) {
        final ConfigFactory configFactory = spring.getBean(ConfigFactory.class);
        final Charset charset = getCharset(configFactory);
        final File file = FilePaths.getDir(FileNames.DIR_INBOX).toFile();
        return new SaveFileMessagesNotifyUser(file, 8192, charset);
    }
    
    public Charset getCharset(ConfigFactory configFactory) {
        final Config config = configFactory.getConfig(ConfigService.APP_INTERNAL);
        final Charset charset = Charset.forName(config.get(ConfigNames.CHARACTER_SET).trim());
        return charset;
    }

    @Bean @Scope("prototype") public GetDevicedetailsForUser getDevicedetailsForUser(
            @Qualifier(MsKioskConfiguration.DEFAULT_CACHE_NAME) Cache cache) {
        return new GetDevicedetailsForUser(cache);
    }
    @Bean @Scope("prototype") public GetDevicedetails getDevicedetails(LoginManager loginManager) {
        return new GetDevicedetails(loginManager);
    }
    
    @Bean @Scope("prototype") public TestDocSenderFactory testDocSenderFactory(AppContext app,
            AppUiContext uiContext, TestDocumentBuilder testDocumentBuilder,
            CredentialsSupplier credentialsSupplier,
            OkHttpClient httpClient, Mapper mapper) {
        
        return new TestDocSenderFactoryImpl(
                app, uiContext, httpClient, testDocumentBuilder, credentialsSupplier, mapper
        );
    }
    
    @Bean public ConfigFactory configFactory() {
        return new ConfigFactoryImpl(FilePaths.getHomeDir());
    }

    @Bean(ConfigService.APP_PROTECTED) @Scope("prototype") public Config<Properties> getConfig(
            ConfigFactory configFactory) {
        return this.loadConfig(configFactory, ConfigService.APP_PROTECTED);
    }
    
    @Bean(ConfigService.APP_UI) @Scope("prototype") public Config<Properties> getUiConfig(
            ConfigFactory configFactory) {
        return this.loadConfig(configFactory, ConfigService.APP_UI);
    }
    
    @Bean(ConfigService.APP_INTERNAL) @Scope("prototype") public Config<Properties> getInternalConfig(
            ConfigFactory configFactory) {
        
        return this.loadConfig(configFactory, ConfigService.APP_INTERNAL);
    }
    
    public Config<Properties> loadConfig(ConfigFactory configFactory, String id) {
        try{
            return configFactory.loadConfig(id);
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
