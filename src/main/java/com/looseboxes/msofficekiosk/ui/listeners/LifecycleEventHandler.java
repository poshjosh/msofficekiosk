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

package com.looseboxes.msofficekiosk.ui.listeners;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2019 5:58:34 PM
 */
public interface LifecycleEventHandler<T> {
    boolean addLifeCycleListener(LifeCycleListener listener);
    boolean removeLifeCycleListener(LifeCycleListener listener);
    void onWillShow(T eventSource);
    void onShown(T eventSource);
    void onWillDispose(T eventSource);
    void onDisposed(T eventSource);
}
