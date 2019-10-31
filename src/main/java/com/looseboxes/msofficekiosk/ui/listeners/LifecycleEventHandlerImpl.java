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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2019 5:52:21 PM
 */
public class LifecycleEventHandlerImpl<T> implements LifecycleEventHandler<T> {

    private final List<LifeCycleListener> listeners;
    
    public LifecycleEventHandlerImpl() {
        this.listeners = new ArrayList<>();
    }
    
    @Override
    public boolean addLifeCycleListener(LifeCycleListener listener) {
        return this.listeners.add(listener);
    }

    @Override
    public boolean removeLifeCycleListener(LifeCycleListener listener) {
        return this.listeners.remove(listener);
    }
    
    @Override
    public void onWillShow(T eventSource) {
        final LifeCycleListener.Event willShowEvent = this.createEvent(eventSource);
        this.listeners.forEach((listener) -> listener.onWillShow(willShowEvent));
    }

    @Override
    public void onShown(T eventSource) {
        final LifeCycleListener.Event shownEvent = this.createEvent(eventSource);
        this.listeners.forEach((listener) -> listener.onShown(shownEvent));
    }

    @Override
    public void onWillDispose(T eventSource) {
        final LifeCycleListener.Event willDisposeEvent = this.createEvent(eventSource);
        this.listeners.forEach((listener) -> listener.onWillDispose(willDisposeEvent));
    }

    @Override
    public void onDisposed(T eventSource) {
        final LifeCycleListener.Event disposedEvent = this.createEvent(eventSource);
        this.listeners.forEach((listener) -> listener.onDisposed(disposedEvent));
    }

    private LifeCycleListener.Event createEvent(final T eventSource) {
        final LifeCycleListener.Event event = new LifeCycleListener.Event() {
            @Override
            public Object getSource() { return eventSource; }
        };
        return event;
    }
}
