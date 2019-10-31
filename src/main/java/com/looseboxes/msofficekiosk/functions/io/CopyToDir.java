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

import com.bc.io.FileIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 24, 2018 10:53:11 AM
 */
public class CopyToDir implements BiFunction<File, Path, File>, Serializable {

    private final FileIO fileIO;
    
    private final CreateNewFile createNew;

    public CopyToDir() {
        this.fileIO = new FileIO();
        this.createNew = new CreateNewFile();
    }
    
    @Override
    public File apply(File src, Path dir) {
        try{
            return this.execute(src, dir);
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public File execute(File src, Path dir) throws IOException {
        
        final byte [] bytes = Files.readAllBytes(src.toPath());
        
        return execute(src.getName(), bytes, dir);
    }

    public File execute(String filename, byte [] filedata, Path dir) throws IOException {
        
        try(InputStream in = new ByteArrayInputStream(filedata)) {
        
            return execute(filename, in, dir);
        }
    }

    public File execute(String filename, InputStream filedata, Path dir) throws IOException {
        
        final File tgt = dir.resolve(filename).toFile();
        
        createNew.apply(tgt, Boolean.FALSE);
        
        try(OutputStream out = new FileOutputStream(tgt, false)) {
        
            fileIO.copyStream(filedata, out);
        }
        
        return tgt;
    }
}
