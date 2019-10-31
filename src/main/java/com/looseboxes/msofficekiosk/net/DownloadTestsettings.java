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
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.test.Tests;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2019 11:17:45 AM
 */
public class DownloadTestsettings implements Runnable{

    private static final Logger LOG = Logger.getLogger(DownloadTestsettings.class.getName());

    private final Supplier<Tests> tests;
    
    private final Supplier<DocumentStore> documentStore;
    
    private final ConfigFactory configFactory;

    public DownloadTestsettings(Supplier<Tests> tests, 
            Supplier<DocumentStore> documentStore, ConfigFactory configFactory) {
        this.tests = Objects.requireNonNull(tests);
        this.documentStore = Objects.requireNonNull(documentStore);
        this.configFactory = Objects.requireNonNull(configFactory);
    }

    @Override
    public void run() {

        try{
            
            final int activationLeadtime = configFactory.getConfig(ConfigService.APP_INTERNAL)
                    .getInt(ConfigNames.TESTS_ACTIVATION_LEADTIME_MINUTES);

            final TimeUnit activationLeadTimeUnit = TimeUnit.MINUTES;

            final Optional<Test> testOptional = tests.get().getCurrentOrPendingTest(
                    activationLeadtime, activationLeadTimeUnit);

            Test current = null;

            if(testOptional.isPresent()) {

                current = testOptional.get();

                documentStore.get().fetchTestsettings(current);
            }

            final List<Test> testList = tests.get().get();
            
            final Test tgt = current;
            final Predicate<Test> filter = (test) -> ! Objects.equals(test, tgt);
            
            accept(testList.stream().filter(filter).collect(Collectors.toList()));

        }catch(RuntimeException e) {
        
            LOG.log(Level.WARNING, null, e);
        }
    }

    public void accept(List<Test> testList) {

        try{
            
            for(Test test : testList) {

                documentStore.get().fetchTestsettings(test);
            }
        }catch(RuntimeException e) {
        
            LOG.log(Level.WARNING, null, e);
        }
    }
}
