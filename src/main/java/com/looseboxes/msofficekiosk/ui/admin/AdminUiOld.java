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

package com.looseboxes.msofficekiosk.ui.admin;

import com.looseboxes.msofficekiosk.ui.MessageDialog;
import com.looseboxes.msofficekiosk.ui.UI;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import java.awt.EventQueue;
import java.util.Objects;
import javax.swing.JTextField;

/**
 * @author Chinomso Bassey Ikwuagwu on May 17, 2019 10:13:42 PM
 */
public class AdminUiOld extends AdminFrame implements UI {

    private boolean disposed;
    
    private final LifecycleEventHandler lifecycleEventHandler;
    
    private final MessageDialog messageDialog;

    public AdminUiOld(UiBeans.UiConfigurer<AdminUiOld> uiConfigurer, 
            LifecycleEventHandler lifecycleEventHandler, MessageDialog messageDialog) {
        this.lifecycleEventHandler = Objects.requireNonNull(lifecycleEventHandler);
        this.messageDialog = Objects.requireNonNull(messageDialog);
        
        uiConfigurer.accept(this);
    }
    
    @Override
    public boolean addLifeCycleListener(LifeCycleListener listener) {
        return this.lifecycleEventHandler.addLifeCycleListener(listener);
    }

    @Override
    public boolean removeLifeCycleListener(LifeCycleListener listener) {
        return lifecycleEventHandler.removeLifeCycleListener(listener);
    }
    
    @Override
    public void dispose() {
        try{
            this.lifecycleEventHandler.onWillDispose(this);
            super.dispose();
        }finally{
            this.disposed = true;
            this.lifecycleEventHandler.onDisposed(this);
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void show() {
        this.pack();
        this.lifecycleEventHandler.onWillShow(this);
        super.show();
        this.lifecycleEventHandler.onShown(this);
    }

    @Override
    public MessageDialog getMessageDialog() {
        return messageDialog;
    }

    @Override
    public void setMessageText(String text) {
        final JTextField ui = this.getMessageTextField();
        if(EventQueue.isDispatchThread()) {
            ui.setText(text);
        }else{
            EventQueue.invokeLater(() -> ui.setText(text));
        }
    }
}
