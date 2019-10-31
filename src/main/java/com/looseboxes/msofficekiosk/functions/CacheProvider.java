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

import com.bc.diskcache.DiskLruCacheContext;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.CacheImpl;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 12:44:32 PM
 */
public class CacheProvider {

    private static final Logger LOG = Logger.getLogger(CacheProvider.class.getName());
    
    private final DiskLruCacheContext cacheContext;
    
    private final Path cacheDir;
    
    private final Mapper mapper;

    public CacheProvider(DiskLruCacheContext cacheContext, Path cacheDir, Mapper mapper) {
        this.cacheContext = Objects.requireNonNull(cacheContext);
        this.cacheDir = cacheDir.toAbsolutePath().normalize();
        this.mapper = Objects.requireNonNull(mapper);
    }

    public Cache apply(String cacheName) {
    
        return apply(cacheContext, cacheName);
    }
    
    public Cache apply(DiskLruCacheContext cacheContext, String cacheName) {
        
        if( ! Files.exists(cacheDir)) {
            try{
                Files.createDirectories(cacheDir);
                LOG.log(Level.INFO, "Created cache dir: {}", cacheDir);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return new CacheImpl(
                cacheContext.getInstance(cacheDir.resolve(cacheName).toString()),
                mapper
        );
    }
}
