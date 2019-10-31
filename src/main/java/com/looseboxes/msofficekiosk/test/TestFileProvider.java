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

import java.io.File;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 4:42:11 PM
 */
public interface TestFileProvider {
    
    TestDocKey getKey(TestDoc testDoc);
    
    File getFile(TestDoc testDoc, String docName);
    
    default File getFile(TestDoc testDoc){
        return getFile(getKey(testDoc), testDoc.getExtension());
    }

    File getFile(TestDocKey testKey, String extension);
}
