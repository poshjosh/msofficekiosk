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

package com.looseboxes.msofficekiosk.ui;

import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 28, 2018 12:03:09 PM
 */
public interface UI {

    boolean addLifeCycleListener(LifeCycleListener listener);

    boolean removeLifeCycleListener(LifeCycleListener listener);

    void dispose();

    boolean isDisposed();

    boolean isShowing();
    
    void show();
    
    MessageDialog getMessageDialog();

    void setMessageText(String text);
}
