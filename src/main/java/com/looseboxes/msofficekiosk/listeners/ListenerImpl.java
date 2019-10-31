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

package com.looseboxes.msofficekiosk.listeners;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 6:20:29 PM
 */
public class ListenerImpl implements Listener {

    private final Callable command;
    
    private final BiConsumer<String, Exception> exceptionHandler;

    public ListenerImpl(Callable command, BiConsumer<String, Exception> exceptionHandler) {
        this.command = Objects.requireNonNull(command);
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
    }

    @Override
    public void handleEvent(Event event) {
        this.onEvent(event);
    }

    public void onEvent(Object event) {
        try{
            command.call();
        }catch(Exception e) {
            final String msg = "Encountered problem executing command: " + this.command.getClass().getSimpleName().toUpperCase();
            this.exceptionHandler.accept(msg, e);
        }
    }
}
