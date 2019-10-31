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

package com.looseboxes.msofficekiosk.net;

import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.test.Tests;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2019 11:15:50 AM
 */
public class OnResponseDownloadTestsettings implements Consumer<Response<Object>>{

    private static final Logger LOG = Logger.getLogger(OnResponseDownloadTestsettings.class.getName());

    private final DownloadTestsettings downloadTestSettings;
    
    private final Mapper mapper;

    public OnResponseDownloadTestsettings(Supplier<Tests> tests, 
            Supplier<DocumentStore> documentStore, ConfigFactory configFactory,
            Mapper mapper) {
        this(new DownloadTestsettings(tests, documentStore, configFactory), mapper);
    }

    public OnResponseDownloadTestsettings(DownloadTestsettings downloadTestSettings, Mapper mapper) {
        this.downloadTestSettings = Objects.requireNonNull(downloadTestSettings);
        this.mapper = Objects.requireNonNull(mapper);
    }
    
    @Override
    public void accept(Response<Object> response) {
        
        final Object bodyObj = response.getBody();
        
        if(response.isError()) {
        
            LOG.log(Level.WARNING, "Error: {0}", response);
            
        }else{
            
            if(bodyObj instanceof Map) {
            
                final Object obj = ((Map)bodyObj).get(Rest.RESULTNAME_TESTS);

                final Runnable task;

                if(obj instanceof List) {

                    final List<Test> tgt = new ArrayList<>();

                    final List src = (List)obj;

                    for(Object o : src) {

                        if(o instanceof Map) {
                            tgt.add(mapper.toObject((Map)o, Test.class));
                        }else if(o instanceof String) {
                            try{
                                tgt.add(mapper.toObject((String)o, Test.class));
                            }catch(ParseException e) {
                                LOG.log(Level.WARNING, null, e);
                            }
                        }else if(o instanceof Test) {
                            tgt.add((Test)o);
                        }else{
                            throw new UnsupportedOperationException("Unexpected type: " + (o == null ? null : o.getClass()));
                        }
                    }

                    task = () -> {
                        try{
                            downloadTestSettings.accept(tgt);
                        }catch(RuntimeException e) {
                            LOG.log(Level.WARNING, null, e);
                        }
                    };

                }else{

                    task = () -> {
                        try{
                            downloadTestSettings.run();
                        }catch(RuntimeException e) {
                            LOG.log(Level.WARNING, null, e);
                        }
                    };
                }

                new Thread(task, this.getClass().getSimpleName() + "_Thread").start();
                
            }else{
            
                LOG.log(Level.WARNING, "Unexpected: {0}", response);
            }
        }
    }
}
