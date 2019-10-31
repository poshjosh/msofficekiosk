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

package com.looseboxes.msofficekiosk.config;

import com.bc.config.PropertiesConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;
import com.bc.config.Config;
import com.looseboxes.msofficekiosk.functions.io.CreateNewFile;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2018 9:42:36 AM
 */
public class ConfigServiceImpl implements Serializable, ConfigService {

    private transient static final Logger LOG = Logger.getLogger(ConfigServiceImpl.class.getName());
    
    private final String defaultPath;
    
    private final String path;

    public ConfigServiceImpl(Path homeDir, String id) { 
        this(
                "META-INF/properties/"+id+".properties",
                homeDir.resolve(Paths.get("config", id+".properties")).toString()
        );
    }

    public ConfigServiceImpl(String defaultPath, String path) {
        this.defaultPath = defaultPath;
        this.path = Objects.requireNonNull(path);
        LOG.info(() -> "Default path: " + defaultPath + "\nPath: " + path);
        final File file = new File(path);
        try{
            new CreateNewFile().execute(file, Boolean.FALSE);
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Map<String, Object> loadMap() throws IOException {
        
        final Config<Properties> config = this.loadConfig();
        
        final Map<String, Object> map = config.toMap();
        
        return map;
    }

    @Override
    public Config<Properties> loadConfig() throws IOException {
        
        final Config<Properties> config = new PropertiesConfig(load());
        
        return config;
    }

    @Override
    public Properties load() throws IOException {
        
        final Properties defaults = defaultPath == null ? null : this.loadResource(new Properties(), defaultPath);
        
        return this.loadFile(new Properties(defaults), path);
    }

    private Properties loadFile(Properties properties, String path) throws IOException {
        
        return this.load(properties, new FileInputStream(path));
    }
    
    private Properties loadResource(Properties properties, String path) throws IOException {
        
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        final InputStream in = classLoader.getResourceAsStream(path);
        
        return this.load(properties, in);
    }

    private Properties load(Properties properties, InputStream in) throws IOException {
        
        try(final Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8.name())) {
        
            properties.load(reader);
            
        }catch(UnsupportedEncodingException e) {
            
            throw new RuntimeException(e);
        }
        
        LOG.fine(() -> "Loaded: " + properties.stringPropertyNames());
        
        return properties;
    }

    @Override
    public Map<String, Object> saveMap(Map map) throws IOException {
        
        final Properties properties = new Properties();
        
        map.forEach((key, val) -> {
            if(key != null && val != null) {
                properties.setProperty(key.toString(), val.toString());
            }
        });
        
        this.save(properties);
        
        return map;
    }

    @Override
    public Config<Properties> saveConfig(Config<Properties> config) throws IOException {

        this.save(config.getSourceData());
        
        return config;
    }

    @Override
    public Properties save(Properties properties) throws IOException {
        
        LOG.fine(() -> "Saving: " + properties.stringPropertyNames());

        final OutputStream out = new FileOutputStream(path, false);
        
        try(final Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8.name())) {

            properties.store(writer, "Saved by " + System.getProperty("user.name") + " on " + ZonedDateTime.now());
            
        }catch(UnsupportedEncodingException e) {
            
            throw new RuntimeException(e);
        }
        
        return properties;
    }
    
    public String toString(Properties props) {
        final StringBuilder b = new StringBuilder();
        final Set<String> names = props.stringPropertyNames();
        for(String name : names) {
            b.append('\n').append(name).append('=').append(props.getProperty(name));
        }
        return b.toString();
    }

    @Override
    public String getDefaultPath() {
        return defaultPath;
    }

    @Override
    public String getPath() {
        return path;
    }
}
