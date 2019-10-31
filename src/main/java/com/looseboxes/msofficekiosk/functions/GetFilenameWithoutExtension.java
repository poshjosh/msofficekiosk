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

package com.looseboxes.msofficekiosk.functions;

import java.io.File;
import java.util.function.UnaryOperator;

/**
 * @author Chinomso Bassey Ikwuagwu on May 16, 2019 9:33:34 AM
 */
public class GetFilenameWithoutExtension implements UnaryOperator<String> {

    public String apply(File file) {
        
        return apply(file.getName());
    }
    
    @Override
    public String apply(String fname) {
        
        return splitToNameAndExtension(fname)[0];
    }

    public String [] splitToNameAndExtension(String fname) {
        
        final int n = fname.lastIndexOf('.');
        
        final String name = n == -1 ? fname : fname.substring(0, n);
        final String ext = n == -1 ? "" : fname.substring(n + 1);
        
        return new String []{name, ext};
    }
}
