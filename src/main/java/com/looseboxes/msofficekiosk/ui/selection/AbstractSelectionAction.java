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

package com.looseboxes.msofficekiosk.ui.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2019 9:34:45 PM
 */
public abstract class AbstractSelectionAction<V> 
        implements SelectionAction<V>, Function<V, Boolean>{
    
    private final String name;

    public AbstractSelectionAction(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public List<V> run(List<V> candidates) {

        final List<V> errors = new ArrayList<>();

        for(V v : candidates) {
        
            if( ! apply(v)) {
                
                errors.add(v);
            }
        }
        
        return errors;
    }
}
