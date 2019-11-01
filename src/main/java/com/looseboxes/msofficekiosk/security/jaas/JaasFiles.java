package com.looseboxes.msofficekiosk.security.jaas;

import com.looseboxes.msofficekiosk.FilePaths;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author USER
 */
public interface JaasFiles {
    
    String FILENAME_JAAS_CONFIG = "msofficekiosk_jaas_config.config";
    
    String RESOURCE_JAAS_CONFIG = Paths.get("META-INF", FILENAME_JAAS_CONFIG).toString();

    String FILENAME_KEY_VALUE_STORE = "kv.store";
    
    Path PATH_DIR = FilePaths.getDir(".jaas");
    
    Path PATH_KEY_VALUE_STORE = PATH_DIR.resolve(FILENAME_KEY_VALUE_STORE);
}
