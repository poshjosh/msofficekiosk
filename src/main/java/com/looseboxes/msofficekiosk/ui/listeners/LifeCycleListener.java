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

package com.looseboxes.msofficekiosk.ui.listeners;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 18, 2018 4:08:47 AM
 */
public interface LifeCycleListener<T> {
    
    interface Event<T> {
        T getSource();
    }

    void onWillShow(Event<T> event);
    
    void onShown(Event<T> event);
    
    void onWillDispose(Event<T> event);
    
    void onDisposed(Event<T> event);
}
