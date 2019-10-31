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

package com.looseboxes.msofficekiosk.functions.io;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 23, 2018 11:36:02 AM
 */
public class CreateNewFile implements BiFunction<File, Boolean, Boolean>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(CreateNewFile.class.getName());

    @Override
    public Boolean apply(File file, Boolean isDir) {
        try{
            this.execute(file, isDir);
            return Boolean.TRUE;
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
            return Boolean.FALSE;
        }
    }

    public void execute(File file, Boolean isDir) throws IOException {
        if(!file.exists()) {
            final File dir = !isDir ? file.getParentFile() : file;
            if(dir != null) {
                final boolean created = dir.mkdirs(); 
                LOG.fine(() ->  "Created: " + created + ", dir: " + file);
            }
            if(!isDir) {
                final boolean created = file.createNewFile();
                LOG.fine(() ->  "Created: " + created + ", file: " + file);
            }
        }
    }
}
