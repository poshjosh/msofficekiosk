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

import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 7:44:42 PM
 */
@FunctionalInterface
public interface SelectionAction<V>{
    
    default String getName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * @param candidates The list of candidates to be acted upon
     * @return The list of candidates that were unsuccessful
     */
    List<V> run(List<V> candidates);
}
