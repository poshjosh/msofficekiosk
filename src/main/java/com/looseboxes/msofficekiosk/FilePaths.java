package com.looseboxes.msofficekiosk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author USER
 */
public interface FilePaths {
    
    Path DIR_HOME = Paths.get(System.getProperty("user.home"), "msofficekiosk");
    
    public static Path getHomeDir() {
        return DIR_HOME.toAbsolutePath().normalize();
    }
    
    public static Path getDir(String name) {
        final Path dir = DIR_HOME.resolve(name);
        if( ! Files.exists(dir)) {
            try{
                Files.createDirectories(dir);
//                LOG.log(Level.INFO, "Created dir: {0}", dir);
            }catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return dir;
    }
}
