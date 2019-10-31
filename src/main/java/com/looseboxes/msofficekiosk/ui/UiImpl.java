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

package com.looseboxes.msofficekiosk.ui;

import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 11:31:26 AM
 */
public class UiImpl implements UI{

    private boolean disposed;
    
    @Override
    public boolean addLifeCycleListener(LifeCycleListener listener) { 
        return false;
    }            
    
    @Override
    public boolean removeLifeCycleListener(LifeCycleListener listener) {
        return false;
    }

    @Override
    public void dispose() { 
        disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void show() { 
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageDialog getMessageDialog() {
        return new SwingMessageDialog(null);
    }

    @Override
    public void setMessageText(String text) { }
}
