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

import com.bc.elmi.pu.entities.Message;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.messaging.MessageBuilder;
import com.looseboxes.msofficekiosk.messaging.MessageSender;
import java.io.File;
import java.util.List;
import java.util.Objects;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.awt.Window;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 4:20:42 AM
 */
public class SendMessageWithLinkToFiles implements SendFilesToNetworkDevices {

//    private static final Logger LOG = Logger.getLogger(SendMessageWithLinkToFiles.class.getName());

    private final AppUiContext appUiContext;
    
    private final MessageBuilder<List<File>> messageBuilder;
    
    private final MessageSender messageSender; 
    
    public SendMessageWithLinkToFiles(
            AppUiContext appUiContext,
            MessageBuilder<List<File>> messageBuilder,
            MessageSender messageSender) {
        this.appUiContext = Objects.requireNonNull(appUiContext);
        this.messageBuilder = Objects.requireNonNull(messageBuilder);
        this.messageSender = Objects.requireNonNull(messageSender);
    }
    
    @Override
    public Boolean apply(List<File> files, List<Devicedetails> ddList) {
        
        final Window window = appUiContext.getMainWindowOptional().orElse(null);
        
        final Icon icon = appUiContext.getImageIconOptional().orElse(null);
        
        final JTextArea ta = new JTextArea(3, 75);
        
        final JScrollPane pane = new JScrollPane(ta);
        
        final int option = JOptionPane.showConfirmDialog(window, 
                pane, "Type in a Subject for the message you are about to send", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
        
        if(option != JOptionPane.OK_OPTION) {
            return Boolean.FALSE;
        }
        
        final String subject = ta.getText();
        
        //@todo property allow empty subjects
        if(subject == null || subject.isEmpty()) {
        
            appUiContext.getUi().getMessageDialog()
                    .showWarningMessage("You did not enter a Subject");
            
            return Boolean.FALSE;
        }
        
        //@todo check len from database
        final int len = 500;
        
        if(subject.length() > len) {
            
            appUiContext.getUi().getMessageDialog()
                    .showWarningMessage("Subject too long. Enter < " + len + " characters.");
            
            return Boolean.FALSE;
        }
        
        try{
            
            //@todo get from datbase
            final int maxContentLen = 20_000;

            final Message m = messageBuilder.buildMessage(subject.trim(), files);
            
            final String content = m.getContent();

            if(content != null && content.length() > maxContentLen) {

                appUiContext.getUi().getMessageDialog().showWarningMessage(
                        "Message will not be sent, content too long.\n\nReduce the number of files being attached and try again.");

                return Boolean.FALSE;
            }

            final Set<String> recipients = ddList.stream()
                    .map((dd) -> dd.getUsername())
                    .filter((u) -> u != null && ! u.isEmpty())
                    .collect(Collectors.toSet());
            
            messageSender.send(m, recipients, Rest.ENDPOINT_TRANSFER_MESSAGE);
            
            return Boolean.TRUE;
            
        }catch(IOException | ParseException e) {
        
            return Boolean.FALSE;
        }
    }
}
