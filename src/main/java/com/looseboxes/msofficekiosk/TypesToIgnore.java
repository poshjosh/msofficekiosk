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

import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.Documentaccess;
import com.bc.elmi.pu.entities.Gender;
import com.bc.elmi.pu.entities.Message;
import com.bc.elmi.pu.entities.Messageaccess;
import com.bc.elmi.pu.entities.Messagestatus;
import com.bc.elmi.pu.entities.Messagetype;
import com.bc.elmi.pu.entities.Mimetype;
import com.bc.elmi.pu.entities.Role;
import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.Testdocument;
import com.bc.elmi.pu.entities.Testsetting;
import com.bc.elmi.pu.entities.Unit;
import com.bc.elmi.pu.entities.User;
import com.bc.elmi.pu.entities.Userstatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on May 23, 2019 11:13:17 PM
 */
public class TypesToIgnore {

    public List<Class> apply(Object objectOrClass) {

        final Set<Class> output = new HashSet<>();

        final Class objType = objectOrClass instanceof Class ? (Class)objectOrClass : objectOrClass.getClass();

        output.addAll(Arrays.asList(objType, User.class, Test.class, Documentaccess.class, Messageaccess.class));
       
        if(User.class.isAssignableFrom(objType)) {
            output.addAll(Arrays.asList(User.class, Document.class, Message.class, Test.class));
        }else if(Test.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Test.class, Testdocument.class, User.class));
        }else if(Role.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Role.class, User.class));
        }else if(Message.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Message.class, User.class));
        }else if(Unit.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Test.class));
        }else if(Document.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Testdocument.class, Testsetting.class));
        }else if(Mimetype.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Document.class, Message.class));
        }else if(Gender.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(User.class));
        }else if(Messagestatus.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Message.class));
        }else if(Messagetype.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(Message.class));
        }else if(Userstatus.class.isAssignableFrom(objType)){
            output.addAll(Arrays.asList(User.class));
        }

        return Collections.unmodifiableList(new ArrayList(output));
    }
}
