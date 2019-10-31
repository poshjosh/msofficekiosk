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

import com.looseboxes.msofficekiosk.FileNames;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.Main;
import com.looseboxes.msofficekiosk.MsKioskConfiguration;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.mapper.MapperJackson;
import com.bc.config.Config;
import com.bc.diskcache.DiskLruCacheContext;
import com.bc.diskcache.DiskLruCacheContextImpl;
import com.bc.diskcache.DiskLruCacheIx;
import com.bc.elmi.pu.entities.User;
import com.bc.jpa.dao.JpaObjectFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.CacheProvider;
import com.looseboxes.msofficekiosk.messaging.MessageSender;
import com.looseboxes.msofficekiosk.messaging.MessageSenderImpl;
import com.looseboxes.msofficekiosk.net.CookieJarInMemoryStaticCache;
import com.looseboxes.msofficekiosk.net.RequestClient;
import com.looseboxes.msofficekiosk.net.RequestClientProvider;
import com.looseboxes.msofficekiosk.net.Response;
import com.looseboxes.msofficekiosk.net.ResponseHandler;
import com.looseboxes.msofficekiosk.net.SimpleResponseHandler;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.LoginManagerImpl;
import com.looseboxes.msofficekiosk.security.OneOffLoginManagerProvider;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.document.DocumentStoreImpl;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.test.TestDocImpl;
import com.looseboxes.msofficekiosk.document.TestDocumentBuilder;
import com.looseboxes.msofficekiosk.document.TestDocumentBuilderImpl;
import com.looseboxes.msofficekiosk.security.CredentialsSupplier;
import com.looseboxes.msofficekiosk.security.CredentialsSupplierFromLoggedInUser;
import com.looseboxes.msofficekiosk.test.TestFileProvider;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.test.TestsImpl;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.AppUiContextImpl;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import com.looseboxes.msofficekiosk.ui.SwingMessageDialog;
import com.looseboxes.msofficekiosk.ui.UI;
import com.looseboxes.msofficekiosk.ui.UiImpl;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.LogManager;
import javax.security.auth.login.LoginException;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import org.junit.BeforeClass;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2019 9:39:54 PM
 */
public class TestBase {

    public static final String extension = "docx";
//    public static final String extensionName = "MS Word Documents";
    public static final String USR = "STUDENT";
    public static final String PWD = "1234567";
           
    static{
        try(final InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("META-INF/properties/logging_devmode.properties")) {
            LogManager.getLogManager().readConfiguration(in);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
    private static class MyTestFileProvider implements TestFileProvider{
        private final ConfigFactory configFactory;
        private final LoginManager loginManager;
        public MyTestFileProvider(ConfigFactory configFactory, LoginManager loginManager) {
            this.configFactory = Objects.requireNonNull(configFactory);
            this.loginManager = Objects.requireNonNull(loginManager);
        }
        @Override
        public TestDocKey getKey(TestDoc testDoc) {
            return new TestDocKey(null, configFactory, loginManager, testDoc);
        }
        @Override
        public File getFile(TestDoc testDoc, String docName) {
            return getFile(getKey(new TestDocImpl(testDoc.getTest(), docName)), testDoc.getExtension());
        }
        @Override
        public File getFile(TestDocKey testKey, String extension) {
            return Main.DIR_HOME.resolve(FileNames.DIR_OUTPUT).resolve(testKey + "." + extension).toFile();
        }
    }
    
    private static CookieJar cookieJar;
    
    private static OkHttpClient httpClient;
    
    private static ConfigFactory configFactory;
    
    private static TestFileProvider testFileProvider;
    
    private static LoginManager loginManager;
    
    private static TestDocumentBuilder documentBuilder;
    
    private static Mapper mapper;

    private static BiConsumer<String, Exception> messageHandler;
    
    private static BiConsumer<String, Integer> progressHandler;

    private static DiskLruCacheContext cacheContext;
    
    private static CacheProvider cacheProvider;
    
    private static Cache cache;
    
    private static Tests tests;
    
    private static RequestClientProvider requestClientProvider;
    
    private static DocumentStore documentStore;
    
    private static CredentialsSupplier credentialsSupplier;
    
    private static UI ui;
    
    private static AppUiContext uiContext;
    
    private static JpaConfigurationTestCase jpaConfig;
    
    private static JpaObjectFactory jpa;
    
    @BeforeClass
    public static void setUpClass(){ 
        cookieJar = new CookieJarInMemoryStaticCache();
        httpClient = new OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true) 
        .cookieJar(cookieJar).build(); 

        configFactory = new ConfigFactoryImpl(Main.DIR_HOME);
        
////////////////////////////////////////        
//        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
//        config.setString(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS, "192.168.43.142");
//        try{
//            configFactory.saveConfig(config, ConfigService.APP_PROTECTED);
//        }catch(Throwable t) {
//            t.printStackTrace();
//        }
/////////////////////////////////////////

        loginManager = new OneOffLoginManagerProvider(cookieJar).get(Main.DIR_HOME);
        credentialsSupplier = new CredentialsSupplierFromLoggedInUser(loginManager);
        testFileProvider = new MyTestFileProvider(configFactory, loginManager);
        final Config config = configFactory.getConfig(ConfigService.APP_UI);
        ui = new UiImpl();
        uiContext = new AppUiContextImpl(config, () -> ui, () ->  null);        
        documentBuilder = null; //new TestDocumentBuilderImpl(null, testFileProvider, loginManager);
        mapper = new MapperJackson();

        cacheContext = new DiskLruCacheContextImpl(10_000_000);
        cacheProvider = new CacheProvider(cacheContext, Main.DIR_HOME.resolve(FileNames.DIR_CACHE), mapper);
        cache = cacheProvider.apply(MsKioskConfiguration.DEFAULT_CACHE_NAME);
        
        tests = new TestsImpl(cache);
        
        requestClientProvider = new RequestClientProvider(configFactory, httpClient);
        
        final Map credentials = new HashMap();
        credentials.put("username", USR);
        credentials.put("password", PWD);
        
        final RequestClient requestClient = requestClientProvider.get((response) -> response);
        documentStore = new DocumentStoreImpl(Main.DIR_HOME.resolve(FileNames.DIR_TEMP), 
                configFactory, 
                requestClient, 
                () -> credentials, 
                (DiskLruCacheIx)cache.getDelegate());
        
        messageHandler = (m, e) -> {
            if(e != null) {
                System.err.println(m);
                e.printStackTrace();
            }else{
                System.out.println(m);
            }
        };
        progressHandler = (m, i) -> { };
        
        System.out.println("Setup completed. Logging in");

        final Object o = configFactory.getConfig(ConfigService.APP_PROTECTED).getString(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS);
        System.out.println(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS + ": " + o);
        
        jpaConfig = new JpaConfigurationTestCase();
        
        jpa = jpaConfig.jpaObjectFactory(
                jpaConfig.entityManagerFactoryCreator(jpaConfig.jdbcPropertiesProvider(null)), 
                jpaConfig.sqlDateTimePatterns(jpaConfig.jdbcPropertiesProvider(null)));
        
//        loginManager.promptUserLogin(2);
        final LoginManagerImpl impl = ((LoginManagerImpl)loginManager);
        try{
            final User u = impl.login(USR, PWD);
            System.out.println("Logged in: " + u);
        }catch(LoginException e) {
            e.printStackTrace();
        }
    }

    public static String getExtension() {
        return extension;
    }

//    public static String getExtensionName() {
//        return extensionName;
//    }

    public static CookieJar getCookieJar() {
        return cookieJar;
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static ConfigFactory getConfigFactory() {
        return configFactory;
    }

    public static TestFileProvider getTestFileProvider() {
        return testFileProvider;
    }

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    public static TestDocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    public static CredentialsSupplier getCredentialsSupplier() {
        return credentialsSupplier;
    }

    public static Mapper getMapper() {
        return mapper;
    }

    public static BiConsumer<String, Exception> getMessageHandler() {
        return messageHandler;
    }

    public static BiConsumer<String, Integer> getProgressHandler() {
        return progressHandler;
    }
    
    public static RequestClient<Response<Object>> getJsonRequestClient() {
        return requestClientProvider().forJson(mapper);
    }

    public static RequestClientProvider requestClientProvider() {
        return new RequestClientProvider(configFactory, httpClient);
    }

    public static MessageSender getMessageSender() {
        return new MessageSenderImpl(mapper,
                configFactory,
                getJsonRequestClient(),
                getResponseHandler()
        );
    }
    
    public static ResponseHandler getResponseHandler() {
        return new SimpleResponseHandler(getMessageDialog());
    }

    public static MessageDialog getMessageDialog() {
        return new SwingMessageDialog(null);
    }

    public static DiskLruCacheContext getCacheContext() {
        return cacheContext;
    }

    public static CacheProvider getCacheProvider() {
        return cacheProvider;
    }

    public static Cache getCache() {
        return cache;
    }

    public static Tests getTests() {
        return tests;
    }

    public static RequestClientProvider getRequestClientProvider() {
        return requestClientProvider;
    }

    public static DocumentStore getDocumentStore() {
        return documentStore;
    }

    public static AppUiContext getUiContext() {
        return uiContext;
    }

    public static JpaConfigurationTestCase getJpaConfig() {
        return jpaConfig;
    }

    public static JpaObjectFactory getJpa() {
        return jpa;
    }
}
