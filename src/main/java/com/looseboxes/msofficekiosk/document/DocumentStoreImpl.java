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

package com.looseboxes.msofficekiosk.document;

import com.bc.config.Config;
import com.bc.diskcache.DiskLruCacheIx;
import com.bc.diskcache.DiskLruCacheIx.SnapshotEntry;
import com.bc.elmi.pu.entities.Document;
import com.bc.socket.io.CopyStream;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.net.UrlBuilderImpl;
import com.looseboxes.msofficekiosk.net.InvalidConfigurationException;
import com.looseboxes.msofficekiosk.net.RequestClient;
import com.looseboxes.msofficekiosk.net.Rest;
import com.looseboxes.msofficekiosk.security.CredentialsSupplier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.ResponseBody;

/**
 * @author Chinomso Bassey Ikwuagwu on May 15, 2019 4:19:07 PM
 */
public class DocumentStoreImpl implements DocumentStore {

    private static final Logger LOG = Logger.getLogger(DocumentStoreImpl.class.getName());
    
    private final ConfigFactory configFactory;
    
    private final RequestClient<okhttp3.Response> requestClient;
    
    private final CredentialsSupplier credentialsSupplier;
    
    private final DiskLruCacheIx cache;
    
    private final Path tempDir;
    
    public DocumentStoreImpl(Path tempDir, ConfigFactory configFactory, 
            RequestClient<okhttp3.Response> requestClient, 
            CredentialsSupplier credentialsSupplier, DiskLruCacheIx cache) {
        this.tempDir = Objects.requireNonNull(tempDir);
        if( ! Files.exists(tempDir)){
            try{
                Files.createDirectories(tempDir);
            }catch(IOException e) {
                LOG.log(Level.WARNING, "Failed to create dir: " + tempDir, e);
            }
        }
        this.configFactory = Objects.requireNonNull(configFactory);
        this.requestClient = Objects.requireNonNull(requestClient);
//        final Config config = configFactory.getConfig(ConfigService.APP_INTERNAL);
//        final Map map = new DefaultHeaders(config);
//        map.put("Accept", MimetypeEnum.application_msword_docx.getValue());
//        requestClient.headers(map);
        this.credentialsSupplier = Objects.requireNonNull(credentialsSupplier);
        this.cache = Objects.requireNonNull(cache);
    }
    
    @Override
    public Map<Document, Optional<File>> fetch(List<Document> docs) {

        LOG.log(Level.FINE, "Documents to fetch: {0}", docs);
        
        final Map<Document, Optional<File>> output = new HashMap<>(fetchLocal(docs));
        
        final List<Document> remaining = new ArrayList<>();
        
        output.forEach((doc, fileOptional) -> {
            if( ! fileOptional.isPresent()){
                remaining.add(doc);
            }
        });
        
        if( ! remaining.isEmpty()) {
            
            final Map<Document, Optional<File>> fromRemote = this.fetchRemote(remaining);
            
            output.putAll(fromRemote);
        }
            
        return Collections.unmodifiableMap(output);
    }
    
    @Override
    public Map<Document, Optional<File>> fetchLocal(List<Document> docs) {
        
        LOG.log(Level.FINE, "Documents to fetch from local: {0}", docs);
        
        final Map<Document, Optional<File>> output = new HashMap<>(docs.size(), 1.0f);

        for(Document doc : docs) {
            
            final File file = this.getFile(doc);
            
            if(file.exists()) {
            
                output.put(doc, Optional.of(file));
                
                continue;
            }
            
            final byte[] fromCache = this.getFromCache(doc);

            if(fromCache != null && fromCache.length > 0) {

                final Optional<File> fileOptional = write(fromCache, file.toPath());
                
                output.put(doc, fileOptional);
                
            }else{
            
                output.put(doc, Optional.empty());
            }
        }
        
        return Collections.unmodifiableMap(output);
    }

    @Override
    public Map<Document, Optional<File>> fetchRemote(List<Document> docs) {
        
        final Map<Document, Optional<File>> output = new HashMap<>(docs.size(), 1.0f);
        
        final Map<Document, Optional<byte[]>> fromRemote = fetchRemoteContent(docs);

        fromRemote.forEach((doc, contentOptional) -> {

            try{
                
                if( ! contentOptional.isPresent()) {

                    output.put(doc, Optional.empty());

                    return;
                }

                final byte[] content = contentOptional.get();

                addToCache(doc, content);

                final Optional<File> fileOptional = write(content, getFile(doc).toPath());

                output.put(doc, fileOptional);
            
            }catch(RuntimeException e) {
                
                output.put(doc, Optional.empty());
                
                LOG.log(Level.WARNING, null, e);
            }
        });
        
        return Collections.unmodifiableMap(output);
    }
    
    public Map<Document, Optional<byte[]>> fetchRemoteContent(List<Document> docs) {
      
        LOG.log(Level.FINER, "Fetching from remote server: {0}", docs);
        
        final Map<Document, Optional<byte[]>> output = new HashMap<>(docs.size(), 1.0f);

        try{
            
            final Map ud = credentialsSupplier.get();
            
            for(Document doc : docs) {
            
                final URL url = getDownloadUrl(doc.getDocumentid().toString());

                requestClient.params(ud).bodyParts(Collections.singletonMap(
                        "k", "DummyBodyPart_required for POST to work"))
                        .execute("POST", url, (r) -> {

                    try(final ResponseBody body = r.body()) {

                        output.put(doc, Optional.ofNullable(body.bytes()));

                    }catch(Exception e) {
                        
                        output.put(doc, Optional.empty());
                        LOG.log(Level.WARNING, null, e);
                    }
                });
            }
        }catch(MalformedURLException | IndexOutOfBoundsException e) {
            
            throw new InvalidConfigurationException("Server ip address not properly configured", e);
            
        }catch(IOException | ParseException e) {
            
            LOG.log(Level.WARNING, null, e);
        }
        
        return Collections.unmodifiableMap(output);
    }
    
    public URL getDownloadUrl(String sub) throws MalformedURLException{
        
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        
        final URL url = new UrlBuilderImpl(config).build(Rest.ENDPOINT_DOWNLOAD_DOCUMENT, sub).get(0);

        LOG.log(Level.FINER, "URL: {0}",  url);
        
        return url;
    }

    public byte [] getFromCache(Document doc) {
    
        final String key = getCacheKey(doc);
        
        LOG.log(Level.FINER, "Fetching from cache: {0}", key);
        
        byte [] output = new byte[0];

        try{
            
            final SnapshotEntry<InputStream> entry = cache.getStreamEntry(key, null);
            
            final InputStream in = entry == null ? null : entry.getData();
            
            if(in != null) {
                
                final int available = in.available();

                final int buff = available < 1024 ? 1024 : available;
                
                try(final InputStream from = in;
                    final ByteArrayOutputStream to = new ByteArrayOutputStream(buff)) {
             
                    new CopyStream().execute(from, to, buff);
                    
                    output = to.toByteArray();
                }
            }
            
            LOG.log(Level.FINE, "For: {0}, retrieved {1} bytes from cache", 
                    new Object[]{key, output.length});
            
        }catch(IOException e) {
        
            LOG.log(Level.WARNING, null, e);
        }
        
        return output;
    }
    
    public boolean addToCache(Document doc, byte[] content) {
    
        final String key = getCacheKey(doc);
        
        LOG.log(Level.FINER, "Adding to cache: {0}", key);
        
        try{
            
//            final Map annotations = mapper.toMap(doc);
            
            cache.put(key, new ByteArrayInputStream(content)); 
            
            LOG.log(Level.FINE, "Added to cache: {0}", key);
            
            return true;
            
        }catch(IOException e) {
        
            LOG.log(Level.WARNING, null, e);
            
            return false;
        }
    }

    public String getCacheKey(Document doc) {
        return getCacheKey(doc.getDocumentid());
    }
    
    public String getCacheKey(Integer id) {
        return "Document_" + id;
    }

    public File getFile(Document doc) {
    
        final String location = doc.getLocation();
        
        final Path path = Paths.get(tempDir.toString(), location);
        
        final Path parent = path.getParent();
        
        if( ! Files.exists(parent)) {
            try{
                Files.createDirectories(parent);
            }catch(IOException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        
        final File file = path.toFile();
        
        LOG.log(Level.FINE, "Document location: {0} file: {1}", new Object[]{location, file});
        
        return file;
    }
    
    public Optional<File> write(byte[] content, Path path) {
    
        final Path target = write(content, path, null);

        return target == null ? Optional.empty() : Optional.of(target.toFile());
    }
    
    public Path write(byte[] content, Path path, Path outputIfNone) {

        Path output = null;
        try{

            output = Files.write(path, content, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

        }catch(IOException e) {

            LOG.log(Level.WARNING, "Failed to write content from cache to: " + path, e);
        }
        
        return output == null ? outputIfNone : output;
    }
}
