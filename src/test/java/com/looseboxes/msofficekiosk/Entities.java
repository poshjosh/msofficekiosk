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

package com.looseboxes.msofficekiosk;

import com.bc.elmi.pu.entities.Appointment;
import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2019 7:41:46 PM
 */
public class Entities {


    public Document getDocument() {
        final Document object = new Document();
        final User user = new User(3);
        user.setUsername("STUDENT");
        object.setAuthor(user);
        object.setDatesigned(new Date());
        object.setDocumentid(1);
        object.setSubject("SAMPLE SUBJECT");
        return object;
    }

    public User getUser() {
        final User u = new User(3);
        u.setUsername("STUDENT");
        final Appointment a = new Appointment((short)1);
        a.setAppointmentname("Appointment for all");
        u.setAppointment(a);
//        u.setGender(???);
        u.setPassword("1234567");
        u.setTimecreated(new Date());
        u.setTimemodified(new Date());
        return u;
    }

    public List<com.bc.elmi.pu.entities.Test> getTestList() {
        return getTestList(getUser());
    }
    
    public List<com.bc.elmi.pu.entities.Test> getTestList(User u) {
        final List<com.bc.elmi.pu.entities.Test> l = new ArrayList<>();
        for(int i=0; i<3; i++) {
            final com.bc.elmi.pu.entities.Test t = new com.bc.elmi.pu.entities.Test(i + 1);
            t.setDurationinminutes((i + 1) * 60);
            t.setStarttime(new Date());
            t.setTestname("RIC Demo Test " + i);
            t.setTimecreated(new Date());
            t.setTimemodified(new Date());
            if(u != null) {
                t.setUserList(Arrays.asList(u));
                List<Test> tl = u.getTestList();
                if(tl == null) {
                    tl = new ArrayList<>();
                    u.setTestList(tl);
                }
                tl.add(t);
            }
            l.add(t);
        }
        return l;
    }
}
