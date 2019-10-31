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

package com.looseboxes.msofficekiosk.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 3:41:19 PM
 */
public interface CommandContext {
    
    default boolean hasSubCommands(String command) {
        return this.getSubCommandNames(command).size() > 0;
    }

    List<String> getSubCommandNames(String command);
        
    default Callable<Integer> composeCommands(Map<String, Object> params, String... ids) {
        return composeCommands(params, Collections.EMPTY_MAP, ids);
    }
    
    Callable<Integer> composeCommands(Map<String, Object> params, Map<String, Predicate<Display>> preConditions, String... ids);

    default Callable<Boolean> getCommand(String id, Map<String, Object> params) {
        final Predicate<Display> preCondition = this.getPreCondition(id, params);
        return getCommand(id, params, preCondition);
    }
    
    Predicate<Display> getPreCondition(String command, Map<String, Object> params);

    Callable<Boolean> getCommand(String id, Map<String, Object> params, Predicate<Display> preCondition);

    default List<Callable<Boolean>> getCommands(Map<String, Object> params, String... ids) {
        return getCommands(params, Collections.EMPTY_MAP, ids);
    }

    List<Callable<Boolean>> getCommands(Map<String, Object> params, Map<String, Predicate<Display>> preConditions, String... ids);
}
