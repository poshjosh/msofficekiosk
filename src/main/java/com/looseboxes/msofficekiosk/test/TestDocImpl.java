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
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 4:44:59 PM
 */
public class TestDocImpl implements TestDoc, Serializable{

    private Test test;

    private String documentname;
    
    private long timecreated;

    public TestDocImpl() { }

    public TestDocImpl(Test test, String documentname) {
        this.test = Objects.requireNonNull(test);
        this.documentname = Objects.requireNonNull(documentname);
        this.timecreated = System.currentTimeMillis();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.test);
        hash = 89 * hash + Objects.hashCode(this.documentname);
        hash = 89 * hash + (int) (this.timecreated ^ (this.timecreated >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestDocImpl other = (TestDocImpl) obj;
        if (this.timecreated != other.timecreated) {
            return false;
        }
        if (!Objects.equals(this.documentname, other.documentname)) {
            return false;
        }
        if (!Objects.equals(this.test, other.test)) {
            return false;
        }
        return true;
    }

    @Override
    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    @Override
    public String getDocumentname() {
        return documentname;
    }

    public void setDocumentname(String documentname) {
        this.documentname = documentname;
    }

    @Override
    public long getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(long timecreated) {
        this.timecreated = timecreated;
    }

    @Override
    public String toString() {
        return "TestDocImpl{" + "test=" + test + ", documentname=" + documentname + ", timecreated=" + timecreated + '}';
    }
}
