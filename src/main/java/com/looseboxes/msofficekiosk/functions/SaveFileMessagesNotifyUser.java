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

package com.looseboxes.msofficekiosk.functions;

import com.bc.socket.io.messaging.SaveFileMessagesImpl;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.awt.Toolkit;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.bc.socket.io.messaging.Message;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import java.nio.charset.Charset;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 25, 2019 2:13:53 AM
 */
public class SaveFileMessagesNotifyUser extends SaveFileMessagesImpl {

    private transient static final Logger LOG = Logger.getLogger(SaveFileMessagesNotifyUser.class.getName());

    private @Lazy @Autowired AppContext appContext;

    private @Lazy @Autowired UiBeans.ServerSocketIncomingMessageHandler statusMessageHandler;
    
    public SaveFileMessagesNotifyUser(File dir) {
        super(dir);
    }

    public SaveFileMessagesNotifyUser(File dir, int defaultBufferSize, Charset charset) {
        super(dir, defaultBufferSize, charset);
    }

    @Override
    public void onSaveFailed(Message messagePart, File file, Exception e) {
        try{
            if(this.isToBeep()) {
                Toolkit.getDefaultToolkit().beep();
            }
            final String msg = "Incoming message failed";
            final Throwable cause;
            final String reason = (cause = e.getCause()) == null ? 
                    e.getLocalizedMessage() : cause.getLocalizedMessage();
            this.statusMessageHandler.accept(Level.FINE, msg + ". Reason: " + reason);
            LOG.log(Level.WARNING, msg, e);
        }catch(RuntimeException re) {
            LOG.log(Level.WARNING, null, re);
        }
    }

    @Override
    public void onSaved(Message messagePart, File file) {
        try{
            if(this.isToBeep()) {
                Toolkit.getDefaultToolkit().beep();
            }
            final String msg = "Received incoming file: " + file.getName();
            this.statusMessageHandler.accept(Level.WARNING, msg);
            LOG.fine(msg);
        }catch(RuntimeException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }
    
    public boolean isToBeep() {
        final boolean beep = appContext.getConfigFactory().getConfig(ConfigService.APP_PROTECTED)
                .getBoolean(ConfigNames.BEEP_ON_INCOMING_MESSAGE_RECEIVED, true);
        return beep;
    }
}
