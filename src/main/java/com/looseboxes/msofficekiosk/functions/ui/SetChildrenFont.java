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

package com.looseboxes.msofficekiosk.functions.ui;

import java.util.function.BiFunction;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 3:10:27 PM
 */
public class SetChildrenFont implements BiFunction<Composite, Font, Integer> {
    
    @Override
    public Integer apply(Composite parent, Font font) {

        return this.apply(parent, font, 0);
    }
    
    public int apply(Composite parent, Font font, int count) {
        
        final Control [] children = parent.getChildren();
        
        if(children != null) {
            
            for(Control child : children) {
                
                try{
                    
                    apply((Composite)child, font, count);
                    
                }catch(ClassCastException ignored) {
                    
                    child.setFont(font);
                    
                    ++count;
                }
            }
        }
        
        return count;
    }
}
