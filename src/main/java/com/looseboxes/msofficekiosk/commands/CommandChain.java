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

package com.looseboxes.msofficekiosk.commands;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2018 12:55:41 PM
 */
public class CommandChain implements Callable<Integer> {

    private final BiConsumer<String, Exception> exceptionHandler;
    
    private final Callable<Boolean> [] commands;

    public CommandChain(BiConsumer<String, Exception> exceptionHandler, List<Callable<Boolean>> commandList) {
        this(exceptionHandler, commandList.toArray(new Callable[0]));
    }
    
    public CommandChain(BiConsumer<String, Exception> exceptionHandler, Callable<Boolean>... commands) {
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
        this.commands = Objects.requireNonNull(commands);
    }

    @Override
    public Integer call() {
        int count = 0;
        for(Callable<Boolean> command : commands) {
            try{
                if(command.call()) {
                    ++count;
                }else{
                    break;
                }
            }catch(Exception e) {
                final String msg = "Encountered problem executing command: " + command.getClass().getSimpleName().toUpperCase();
                this.exceptionHandler.accept(msg, e);
            }
        }
        return count;
    }
}
