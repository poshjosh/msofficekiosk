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

package com.looseboxes.msofficekiosk.messaging;

import com.bc.config.Config;
import com.bc.elmi.pu.entities.Message;
import com.bc.elmi.pu.entities.User;
import com.bc.elmi.pu.enums.MessagestatusEnum;
import com.bc.elmi.pu.enums.MessagetypeEnum;
import com.bc.elmi.pu.enums.MimetypeEnum;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.net.UrlBuilderImpl;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2019 10:58:06 AM
 */
public class MessageBuilderForDocLinks implements MessageBuilder<List<File>>{

    private static final Logger LOG = Logger.getLogger(MessageBuilderForDocLinks.class.getName());
    
    private final AppContext app;
    
    private final LoginManager loginManager;

    private final boolean addLinkForMarking;
    
    public MessageBuilderForDocLinks(AppContext app, LoginManager loginManager, boolean addLinkForMarking) {
        this.app = Objects.requireNonNull(app);
        this.loginManager = Objects.requireNonNull(loginManager);
        this.addLinkForMarking = addLinkForMarking;
    }
    
    @Override
    public Message buildMessage(String subject, List<File> files, Devicedetails dd) {
    
        final Object id = loginManager.validateUserId();
        final User sender = new User((Integer)id);
        LOG.log(Level.FINE, "Adding message sender: {}", sender);
        final String content = buildMessageContent(files, dd);
        
        final Message m = new Message();
        m.setContent(content);
        m.setType(MessagetypeEnum.Generic.getEntity());
        m.setMimetype(MimetypeEnum.text_html.getEntity());
        m.setStatus(MessagestatusEnum.Unsent.getEntity());
        m.setSender(sender);
        m.setSubject(subject);
        
        return m;
    }
    
    public String buildMessageContent(List<File> files, Devicedetails dd) {
        
        final StringBuilder builder = new StringBuilder();
        
        for(File file : files) {
            builder.append("<p>").append(buildDocumentLink(file, dd)).append("</p>");
        }
        
        builder.append("<p><small>Message created on: ").append(new Date()).append("</small></p>");
        
        return builder.toString();
    }
    
    public String buildDocumentLink(File file, Devicedetails dd) {
        
        final Map params = new HashMap();
//        final TestDocKey key = TestDocKey.decodeFilename(app, file.getName());
//        params.put("testid", key.getTestid());
//        final Object id = loginManager.validateUserId();
//        params.put("userid", id);
//        params.put("documentname", key.getDocumentname() + "." + TestDoc.EXTENSION);
        params.put("filename", file.getName());
        
        final Config config = app.getConfig(ConfigService.APP_PROTECTED);
        
        String link;

        try{
            
            final String ip = dd == null ? "localhost" : dd.getIpaddress();
            
            final List<URL> openUrls = new UrlBuilderImpl(config).build(
                    new String[]{ip}, params, true, "open/File");

            link = truncate(file.getName(), 50) + 
                "... <a href=\"" + openUrls.get(0).toExternalForm() + "\">Open file</a>";
            
            if(addLinkForMarking) {
                final List<URL> markUrls = new UrlBuilderImpl(config).build(
                        new String[]{ip}, params, true, "mark/File");
                link += " <a href=\"" + markUrls.get(0).toExternalForm() + "\">Mark file</a>";
            }   
            
        }catch(IOException e) {
        
            throw new RuntimeException(e);
        }
        
        return link;
    }
    
    private String truncate(String val, int max) {
        return val == null ? null : val.length() <= max ? val : val.substring(0, max);
    }
}
