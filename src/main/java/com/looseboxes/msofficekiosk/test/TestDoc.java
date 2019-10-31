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

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 3:41:39 PM
 */
public interface TestDoc {
    
    String EXTENSION = "docx";
    String SCORE_EXTENSION = "txt";
    String MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    String CONTENTTYPE = "application/octet-stream";
    String SCORE_SUFFIX = "_score";
    
    default String getExtension() {
        return EXTENSION;
    }

    Test getTest();

    String getDocumentname();

    long getTimecreated();
}
