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

package com.looseboxes.msofficekiosk.net;

import com.bc.config.Config;
import com.bc.socket.io.messaging.FileMessageBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import org.json.simple.JSONObject;
import com.bc.socket.io.BcSocketClient;
import com.bc.socket.io.messaging.MessageBuilder;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.GetServerHostsFromConfig;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2018 8:16:25 AM
 */
public class SendTestDocsOverWebSocket implements SendTestDocs{

    private transient static final Logger LOG = Logger.getLogger(SendTestDocsOverWebSocket.class.getName());
    
    private final AppContext context;
    
    private final AppUiContext uiContext;

    public SendTestDocsOverWebSocket(AppContext context, AppUiContext uiContext) {
        this.context = Objects.requireNonNull(context);
        this.uiContext = Objects.requireNonNull(uiContext);
    }

    @Override
    public Boolean apply(final Map<TestDoc, File> attachments) {
        try{
            
            if(attachments.isEmpty()) {
                return Boolean.FALSE;
            }
            
            send(attachments);
            
            final String msg = "Done sending "+attachments.size()+" documents";
            LOG.fine(msg);
            uiContext.getMessageDialog().showInformationMessage(msg);

            return Boolean.TRUE;
            
        }catch(IOException | InvalidConfigurationException e) {
            final String msg = "Failed to send document(s) via local network. Reason: " + e.getLocalizedMessage();
            LOG.log(Level.WARNING, msg, e);
            uiContext.getMessageDialog().showWarningMessage(msg);
            return Boolean.FALSE;
        }
    }
    
    @Override
    public void send(final Map<TestDoc, File> attachments) 
            throws IOException, InvalidConfigurationException {
        
        try{
        
            uiContext.showProgressBarPercent("..Sending", -1);

            final Config<Properties> config = context.getConfig(ConfigService.APP_PROTECTED);

            final String [] ipAddresses = new GetServerHostsFromConfig().apply(config);

            this.send(ipAddresses, attachments);
            
        }finally{
        
            uiContext.showProgressBarPercent(100);
        }
    }

    private void send(String [] ipAddresses, Map<TestDoc, File> attachments) throws IOException {
        
        final MessageBuilder<JSONObject, Path> msgBuilder = new FileMessageBuilder();
        
        final String groupId = Long.toHexString(System.currentTimeMillis());

        final BcSocketClient client = context.getSocketClient();
        
        for(String ipAddress : ipAddresses) {

            for(File file : attachments.values()) {
                
                try(final InputStream in = msgBuilder.buildStream(groupId, file.getName(), file.toPath())) {

                    final int sent = client.send(in, ipAddress);
                    
                    LOG.finer(() -> "Sent " + sent + " bytes of " + file + " to ip: " + ipAddress);
                }
            }
        }
    }
}
