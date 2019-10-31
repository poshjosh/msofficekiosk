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

package com.looseboxes.msofficekiosk.test;

import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 8:33:14 PM
 */
public interface Tests {
    
    String MAIN_DOCUS_DEFAULT_NAME = "MAIN_DOCUS";
    int ADVANCE_TIME_MILLIS = 10_000;

    void delete(Test test);
    
    default TestDoc createTestDoc(Test test, String documentName) {
        final TestDoc testDoc = new TestDocImpl(test, documentName);
        return testDoc;
    }

    Test activateTest(LoginManager loginManager, int period, TimeUnit timeUnit);

    void activateTest(Test test, LoginManager loginManager);
    
    /**
     * @return A copy 
     */
    List<Test> get();
    
    Optional<Test> get(Object id);

    Optional<Test> getCurrentTest();

    Optional<Test> getCurrentOrPendingTest(int period, TimeUnit timeUnit);
    
    default Test validateActivatedTest() {
        return getActivatedTest().orElseThrow(() -> new ValidationException("No activated Test available"));
    }

    Optional<Test> getActivatedTest();
        
    Comparator<Test> getComparator();

    Optional<Test> getPendingTest(int period, TimeUnit timeUnit);

    Test getUserCreatedTest();

    void setUserCreatedTest(Test userCreatedTest);

}
