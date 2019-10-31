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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2018 12:53:43 PM
 */
public class ZipFiles implements BiConsumer<Collection<File>, File>, 
        Function<Collection<File>, File>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(ZipFiles.class.getName());

    private final Charset charset;
    
    private final int bufferSize;
    
    public ZipFiles() {
        this(StandardCharsets.UTF_8, 8192);
    }
    
    public ZipFiles(Charset charset, int bufferSize) {
        this.charset = Objects.requireNonNull(charset);
        this.bufferSize = bufferSize;
    }
    
    @Override
    public File apply(Collection<File> input) {
        final String fileName = Long.toHexString(System.currentTimeMillis()) + ".zip";
        final File file = Paths.get(System.getProperty("java.io.tmpdir"), fileName).toFile();
        this.accept(input, file);
        return file;
    }

    @Override
    public void accept(Collection<File> input, File output) {
        try{
            this.apply(input, output, charset);
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
        }
    } 
    
    public void apply(Collection<File> input, File output, Charset charset) 
            throws FileNotFoundException, IOException {
        
        try(final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output))) {
            
            apply(input, zos);
        }
    }

    public void apply(Collection<File> inputFiles, ZipOutputStream outputStream) throws IOException {
        
        final byte[] buffer = new byte[bufferSize];

        for (File srcFile : inputFiles) {

            try(FileInputStream fis = new FileInputStream(srcFile)) {

                try{
                    outputStream.putNextEntry(new ZipEntry(srcFile.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }finally{
                    outputStream.closeEntry();
                }
            }
        }
    }
}
