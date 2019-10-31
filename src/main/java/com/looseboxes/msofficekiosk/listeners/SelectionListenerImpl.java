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

package com.looseboxes.msofficekiosk.listeners;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2018 12:33:17 PM
 */
public class SelectionListenerImpl extends SelectionAdapter {

    private final ListenerImpl listener;

    public SelectionListenerImpl(Callable command, BiConsumer<String, Exception> exceptionHandler) {
        this.listener = new ListenerImpl(command, exceptionHandler);
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        listener.onEvent(event);
    }
}
