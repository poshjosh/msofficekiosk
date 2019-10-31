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

package com.looseboxes.msofficekiosk.functions.admin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on May 19, 2019 12:29:47 AM
 */
public class DirsFiles implements Supplier<List<File>>, Predicate<Path>{

    private static final Logger LOG = Logger.getLogger(DirsFiles.class.getName());

    private final File [] arr;
    
    private final FilenameFilter filter;
    
    private final boolean uniqueFilenames;

    public DirsFiles(FilenameFilter filter, boolean uniqueFilenames, File... arr) {
        this.filter = Objects.requireNonNull(filter);
        this.arr = Objects.requireNonNull(arr);
        this.uniqueFilenames = uniqueFilenames;
    }

    @Override
    public List<File> get() {
        final List<File> output = new ArrayList<>();
        for(File file : arr) {
            if(file.isFile()) {
                if(filter.accept(file.getParentFile(), file.getName())) {
                    output.add(file);
                }
            }else{
                Collection<File> found = Collections.EMPTY_SET;
                try{
                    found = Files.walk(file.toPath())
                            .filter(this)
                            .map((path) -> path.toFile())
                            .filter((f) -> ! uniqueFilenames || ! contains(output, f.getName()))
                            .collect(Collectors.toSet());
                }catch(IOException e) {
                    LOG.log(Level.WARNING, null, e);
                }

                output.addAll(found);
            }
        }
        return Collections.unmodifiableList(output);
    }
    
    public boolean contains(List<File> arr, String name) {
        return arr.stream().filter((file) -> file.getName().equals(name)).findFirst().isPresent();
    }
    
    @Override
    public boolean test(Path path) {
        final boolean output;
        if(Files.isDirectory(path)) {
            output = false;
        }else{
            final File file = path.toFile();
            output = filter.accept(file.getParentFile(), file.getName());
        }
        return output;
    }
}
