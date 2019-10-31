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

import com.looseboxes.msofficekiosk.AddJarToClassLoaders;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 10:18:14 AM
 */
public class LoadInternalJar implements Consumer<String>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(LoadInternalJar.class.getName());
    
    private final List<ClassLoader> classLoaders;

    public LoadInternalJar() {
        this(Arrays.asList(
                ClassLoader.getSystemClassLoader(), 
                Thread.currentThread().getContextClassLoader()
            )    
        );
    }
    
    public LoadInternalJar(List<ClassLoader> classLoaders) {
        this.classLoaders = Objects.requireNonNull(classLoaders);
    }

    @Override
    public void accept(String jarFileName) {

        LOG.info(() -> "SWT Jar file name: " + jarFileName);

        new AddJarToClassLoaders(classLoaders).apply(new File("lib", jarFileName));
    }
}
