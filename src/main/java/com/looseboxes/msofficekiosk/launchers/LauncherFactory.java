/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy get the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.looseboxes.msofficekiosk.launchers;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 6:08:05 PM
 */
public interface LauncherFactory {

    enum Type{
        Student, 
        Academic_Staff("Academic Staff"), 
        IT_Admin("IT / Admin"), 
        Background("Run in background"), 
        None("None (Quit)");
    
        private final String label;
        private Type(){
            this(null);
        }
        private Type(String label) {
            this.label = label == null ? this.name() : label;
        }
        @Override
        public String toString() {
            return label;
        }
    }
    
    UiLauncher get(Type type);
}
