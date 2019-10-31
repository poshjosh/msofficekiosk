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

package com.looseboxes.msofficekiosk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Chinomso Bassey Ikwuagwu on May 15, 2019 8:47:34 AM
 */
public class TestUtil {

    public static File [] selectFiles(String extension) {
        
        return selectFiles(extension, extension + " files");
    }
    
    public static File [] selectFiles(String extension, String extensionName) {
        
        final Predicate<String> extensionTest = (e) -> e.endsWith("."+extension) || 
                        e.endsWith("."+extension.toUpperCase());
        
        final String userHome = System.getProperty("user.home");
        
        Set<File> found;
        try{
            found = Files.walk(Paths.get(userHome, "Desktop", "elmi_temp"))
                    .filter((path) -> extensionTest.test(path.getFileName().toString()))
                    .limit(2)
                    .map((path) -> path.toFile())
                    .collect(Collectors.toSet());
            System.out.println("FileVisit selected " + (found == null ? null : found.size()) + " files");
        }catch(IOException e) {
            e.printStackTrace();
            found = Collections.EMPTY_SET;
        }
        
        final File [] output;
        if(found != null && !found.isEmpty()) {
            output = found.toArray(new File[0]);
        }else{
        
            JFileChooser c = new JFileChooser();
    //        c.addChoosableFileFilter(FileFilter);
    //        c.setApproveButtonText("Select");
            c.setDialogTitle("Select " + extensionName);
            c.setFileFilter(new FileFilter(){
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || extensionTest.test(f.getName());
                }
                @Override
                public String getDescription() {
                    return extensionName;
                }

            });
            c.setFileHidingEnabled(false);
            c.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            c.setMultiSelectionEnabled(true);
            c.showDialog(null, "Select");
            output = c.getSelectedFiles();
            System.out.println("User selected " + (output == null ? null : output.length) + " files");
        }
        
        return output;
    }
}
