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

import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.Testdocument;
import com.bc.elmi.pu.entities.Testsetting;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on May 15, 2019 5:25:28 PM
 */
public interface DocumentStore {

    default Map<Document, Optional<File>> fetchTestdocuments(Test test) {
        final List<Testdocument> tdl = test.getTestdocumentList();
        return tdl == null || tdl.isEmpty() ? Collections.EMPTY_MAP : fetchTestdocuments(tdl);
    }

    default Map<Document, Optional<File>> fetchTestdocuments(List<Testdocument> testdocs) {
        return fetch(testdocs.stream().map((td) -> td.getTestdocument()).collect(Collectors.toList()));
    }

    default Map<Document, Optional<File>> fetchTestsettings(Test test) {
        final List<Testsetting> ttl = test.getTestsettingList();
        return ttl == null || ttl.isEmpty() ? Collections.EMPTY_MAP : fetchTestsettings(ttl);
    }

    default Map<Document, Optional<File>> fetchTestsettings(List<Testsetting> testsettings) {
        return fetch(testsettings.stream().map((ts) -> ts.getTestsetting()).collect(Collectors.toList()));
    }

    Map<Document, Optional<File>> fetch(List<Document> docs);

    Map<Document, Optional<File>> fetchLocal(List<Document> docs);

    Map<Document, Optional<File>> fetchRemote(List<Document> docs);

}
