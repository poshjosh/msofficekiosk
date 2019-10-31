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

package com.looseboxes.msofficekiosk.functions;

import com.looseboxes.msofficekiosk.AddJarToClassLoaders;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 24, 2018 3:04:55 AM
 */
public class LoadExternalJar implements Consumer<String>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(LoadExternalJar.class.getName());

    private final List<ClassLoader> classLoaders;

    public LoadExternalJar() {
        this(Arrays.asList(
                ClassLoader.getSystemClassLoader(), 
                Thread.currentThread().getContextClassLoader()
            )    
        );
    }
    
    public LoadExternalJar(List<ClassLoader> classLoaders) {
        this.classLoaders = Objects.requireNonNull(classLoaders);
    }

    @Override
    public void accept(String jarFileName) {

        LOG.info(() -> "SWT Jar file name: " + jarFileName);

        final AddJarToClassLoaders addJarToClassLoaders = new AddJarToClassLoaders(classLoaders);
        
        addJarToClassLoaders.apply(jarFileName);

        final String javaClassPath = System.getProperty("java.class.path");

        if(javaClassPath != null) {

            final Set<String> classPaths = Arrays.asList(javaClassPath.split(File.pathSeparator))
                    .stream().filter((classPath) -> classPath != null && !classPath.isEmpty()).collect(Collectors.toSet());

            LOG.info(() -> "Class paths: " + classPaths);

            if(classPaths.size() == 1) {

                final File parent = new File(classPaths.iterator().next()).getParentFile();
                final File jarFile = new File(parent, jarFileName);
                LOG.info(() -> "Jar file from app jar: " + jarFile);

                addJarToClassLoaders.apply(jarFile);

            }else{

                final Predicate<String> hasUserDefinedSuffix = (classPath) -> classPath.endsWith(jarFileName);
                
                final Predicate<String> addedToClassLoader = (classPath) -> 
                        ! addJarToClassLoaders.apply(new File(classPath)).isEmpty();
                
                boolean isPresent = classPaths.stream().filter(hasUserDefinedSuffix.and(addedToClassLoader)).findFirst().isPresent();

                if(!isPresent) {
                    
                    final Predicate<String> isSwtJar = (classPath) ->  classPath.contains("swt") && classPath.endsWith(".jar");
                    
                    final boolean is64bit = System.getProperty("os.arch").contains("64");
                    
                    final Predicate<String> matchesOsArch = (classPath) -> is64bit ? classPath.contains("64") : !classPath.contains("64");
                    
                    classPaths.stream().filter(isSwtJar.and(matchesOsArch).and(addedToClassLoader)).findFirst().isPresent();
                }
            }
        }
    }
}
