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

package com.looseboxes.msofficekiosk.net;

import com.bc.config.Config;
import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.document.TestDocumentBuilder;
import com.looseboxes.msofficekiosk.security.CredentialsSupplier;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 8:14:52 PM
 */
public class SendTestDocsToServer implements SendTestDocs{

    private static final Logger LOG = Logger.getLogger(SendTestDocsToServer.class.getName());
    
    private static final MediaType CONTENT_TYPE = MultipartBody.FORM;
    private final Charset charset;
    private final String userAgent;
    
    private final OkHttpClient httpClient; 
        
    private final ConfigFactory configFactory;
    
    private final TestDocumentBuilder documentBuilder;
    
    private final CredentialsSupplier credentialsSupplier;
    
    private final Mapper mapper;

    private final AppUiContext uiContext;
    
    public SendTestDocsToServer(OkHttpClient httpClient, ConfigFactory configFactory,
            TestDocumentBuilder documentBuilder, CredentialsSupplier credentialsSupplier,
            Mapper mapper, AppUiContext uiContext) {
        this(httpClient, 
                Charset.forName(configFactory.getConfig(ConfigService.APP_INTERNAL).get(ConfigNames.CHARACTER_SET).trim()), 
                configFactory.getConfig(ConfigService.APP_INTERNAL).get(ConfigNames.HTTP_USER_AGENT),
                configFactory, documentBuilder, credentialsSupplier, 
                mapper, uiContext);
    }

    public SendTestDocsToServer(OkHttpClient httpClient, Charset charset, String userAgent, 
            ConfigFactory configFactory, TestDocumentBuilder documentBuilder, 
            CredentialsSupplier credentialsSupplier, Mapper mapper,
            AppUiContext uiContext) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.charset = Objects.requireNonNull(charset);
        this.userAgent = Objects.requireNonNull(userAgent);
        this.configFactory = Objects.requireNonNull(configFactory);
        this.documentBuilder = Objects.requireNonNull(documentBuilder);
        this.credentialsSupplier = Objects.requireNonNull(credentialsSupplier);
        this.mapper = Objects.requireNonNull(mapper);
        this.uiContext = Objects.requireNonNull(uiContext);
    }

    @Override
    public Boolean apply(Map<TestDoc, File> attachments) {
        try{

            if(attachments == null || attachments.isEmpty()) {
                LOG.log(Level.FINE, "Attachments: {0}", (attachments == null ? null : attachments.size()));
                return Boolean.FALSE;
            }
            
            send(attachments);
            
            final String msg = "Done sending "+attachments.size()+" documents";
            LOG.fine(msg);
            uiContext.getMessageDialog().showInformationMessage(msg);

            return Boolean.TRUE;
            
        }catch(IOException | ParseException | RuntimeException e) {

            final String message = "Failed to send document(s) via local network.\nReason: " + 
                    (e.getLocalizedMessage() == null ? "Unexpected Exception" : e.getLocalizedMessage().length() <= 100 ?
                    e.getLocalizedMessage() : e.getLocalizedMessage().substring(0, 100));
            
            LOG.log(Level.WARNING, message, e);
            uiContext.getMessageDialog().showWarningMessage(message);
            
            return Boolean.FALSE;
        }
    }
    
    @Override
    public void send(Map<TestDoc, File> attachments) 
            throws IOException, ParseException, InvalidConfigurationException {
    
        try{
            
            uiContext.showProgressBarPercent("..Sending", -1);

            final Config<Properties> config = configFactory.getConfig(ConfigService.APP_PROTECTED);

            final List<URL> urls;
            try{
                urls = new UrlBuilderImpl(config).build(Rest.ENDPOINT_UPLOAD_DOCUMENT + "s");
            }catch(Exception e) {
                final String msg = "Not configured properly";
                throw new InvalidConfigurationException(msg, e);
            }

            for(URL url : urls) {

                this.send(url, attachments);
            }
        }finally{
        
            uiContext.showProgressBarPercent(100);
        }
    }
    
    public void send(URL url, Map<TestDoc, File> attachments) 
            throws IOException, ParseException {
        
        LOG.fine(() -> "sending to: "+url+ "\nAttachments: " + attachments.values());

        final MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
       
        multipartBodyBuilder.setType(CONTENT_TYPE);
        
        final Set<Test> tests = attachments.keySet().stream().map((td) -> td.getTest()).collect(Collectors.toSet());
        if(tests.size() != 1) {
            throw new IllegalStateException("May not submit > One Test at a time");
        }
        
        final Test test = tests.iterator().next();
//        multipartBodyBuilder.addFormDataPart(Test_.testid.getName(), test.getTestid().toString());
        LOG.log(Level.FINE, "Adding to form document testid: {0}", test.getTestid());
        multipartBodyBuilder.addFormDataPart("testid", test.getTestid().toString());
        
        final Map credentials = credentialsSupplier.get();
        LOG.log(Level.FINE, "Credentials to add: {0}", credentials.keySet());
        for(Object key : credentials.keySet()) {
            multipartBodyBuilder.addFormDataPart(key.toString(), credentials.get(key).toString());
        }
        
        Document main = null;
        for(TestDoc testDoc : attachments.keySet()) {
            
            final Document doc = documentBuilder.build(main, testDoc);
            final String docStr = mapper.toJsonString(doc);
            LOG.log(Level.FINE, "Adding to form document json: {0}", docStr);
            
            multipartBodyBuilder.addFormDataPart(
                    Document.class.getSimpleName() + "s", testDoc.getDocumentname(), 
                    okhttp3.RequestBody.create(MediaType.parse("application/json"), docStr));
            
//            if(testDoc.getDocumentname().equals(Tests.MAIN_DOCUS_DEFAULT_NAME)) {
//                main = doc;
//            }
        }
        
        for(File file : attachments.values()) {
            LOG.log(Level.FINE, "Adding to form file part: {0}", file);
            multipartBodyBuilder.addFormDataPart(
                    "files", file.getName(), 
                    okhttp3.RequestBody.create(MediaType.parse(TestDoc.CONTENTTYPE), file));
        }
        
        final MultipartBody partBody = multipartBodyBuilder.build();
        
        Request.Builder builder = new Request.Builder();
        
        final Request request = builder.url(url)
                .header("Accept-Charset", this.charset.name())
                .header("Accept", "application/json")
                .header("User-Agent", this.userAgent)
                .method("POST", partBody)
                .build();
        
        final Response response = this.httpClient.newCall(request).execute();
        
        this.onResponse(url, response);
    }

    public boolean onResponse(URL url, Response response) {

        if(response == null) {
            final String msg = "Unexpected exception sending files. No Response from server";
            LOG.warning(() -> msg + ". Target: " + url);
            uiContext.getMessageDialog().showWarningMessage(msg);
            return false;
        }
        
        if(LOG.isLoggable(Level.FINE)) {
            try{
                final Map map = mapper.toObject(response.body().string(), Map.class);
                LOG.log(Level.FINE, "Response body: {0}", map);
            }catch(Exception e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        
        if( ! response.isSuccessful()) {
            final String msg = "Failed to send files. Reason: " + response.message();
            LOG.warning(() -> msg + ". Target: " + url);
            uiContext.getMessageDialog().showWarningMessage(msg);
        }else{
            if(LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Successfully sent files to: {0}\nResponse code: {1}, message: {2}", 
                        new Object[]{url, response.code(), response.message()});
            }
        }

        return response.isSuccessful();
    }
}
