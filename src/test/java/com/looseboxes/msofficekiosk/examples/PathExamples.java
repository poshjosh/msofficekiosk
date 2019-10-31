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

package com.looseboxes.msofficekiosk.examples;

import static java.lang.System.out;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 9, 2019 6:34:31 PM
 */
public class PathExamples {

    public static void main(String... args) {
        
        final Path root = Paths.get("abc", "def");
    
        final Path path = Paths.get("abc", "def", "ghi", "jkl");
        
        final Path a = root.relativize(path);
        
        final Path b = Paths.get("ghi", "jkl");
        
        out.println(root.resolve(a));
        
        out.println(root.resolve(b));
        
        out.print(root.resolve(root.relativize(Paths.get("def", "ghi", "jkl"))));
    }
}
