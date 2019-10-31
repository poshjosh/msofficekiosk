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

import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.document.TestDocumentBuilder;
import com.looseboxes.msofficekiosk.security.CredentialsSupplier;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import okhttp3.OkHttpClient;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 9:17:15 PM
 */
public class TestDocSenderFactoryImpl implements TestDocSenderFactory {
    
    private final AppContext app;
    
    private final AppUiContext uiContext;

    private final OkHttpClient httpClient; 
            
    private final TestDocumentBuilder documentBuilder; 
    
    private final CredentialsSupplier credentialsSupplier;
    
    private final Mapper mapper;
    
    public TestDocSenderFactoryImpl(AppContext app, AppUiContext uiContext,
            OkHttpClient httpClient, TestDocumentBuilder documentBuilder, 
            CredentialsSupplier credentialsSupplier, Mapper mapper) {
        this.app = Objects.requireNonNull(app);
        this.httpClient = Objects.requireNonNull(httpClient);
        this.documentBuilder = Objects.requireNonNull(documentBuilder);
        this.credentialsSupplier = Objects.requireNonNull(credentialsSupplier);
        this.mapper = Objects.requireNonNull(mapper);
        this.uiContext = Objects.requireNonNull(uiContext);
    }
            
    @Override
    public Collection<String> getSupported() {
        return Arrays.asList(SEND_TO_SERVER, SEND_TO_EMAIL);
    }

    @Override
    public boolean isSupported(String id) {
        return getSupported().contains(id);
    }
    
    @Override
    public SendTestDocs get(String id) {
        final SendTestDocs output;
        switch(id) {
            case SEND_TO_SERVER:
                output = new SendTestDocsToServer(
                        httpClient, app, documentBuilder, 
                        credentialsSupplier, mapper, uiContext);
                break;
//            case SEND_TO_SOCKET:
//                output = new SendTestDocsOverWebSocket(app, uiContext);
//                break;
            case SEND_TO_EMAIL:
                output = new SendTestDocsByEmail(app, uiContext);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Found: " + id + ", expected any of: " + getSupported()
                );
        }        
        return output;
    }

    @Override
    public AppUiContext getUiContext() {
        return uiContext;
    }
}
