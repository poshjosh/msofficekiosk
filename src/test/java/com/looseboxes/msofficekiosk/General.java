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

import com.bc.elmi.pu.entities.Test_;
import com.bc.jpa.dao.JpaObjectFactory;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.Update;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2019 12:17:56 PM
 */
public class General extends TestBase{

//    @Test
    public void main() {
    
        final JpaObjectFactory jpa = TestBase.getJpa();
        
        final Select<com.bc.elmi.pu.entities.Test> dao0 = jpa.getDaoForSelect(com.bc.elmi.pu.entities.Test.class);
        
        final List<com.bc.elmi.pu.entities.Test> arr0 = dao0.where(Test_.durationinminutes, (Object)null).getResultsAndClose();
        
        System.out.println("Result count: " + arr0.size());

        for(com.bc.elmi.pu.entities.Test t : arr0) {
            System.out.println(t);
        }

        final Update<com.bc.elmi.pu.entities.Test> update = jpa.getDaoForUpdate(com.bc.elmi.pu.entities.Test.class);
        
        final int updateCount = update.set(Test_.durationinminutes, 10)
                .where(Test_.durationinminutes, (Object)null)
                .executeUpdateCommitAndClose();
        
        System.out.println("Update count: " + updateCount);
        
        final Select<com.bc.elmi.pu.entities.Test> dao = jpa.getDaoForSelect(com.bc.elmi.pu.entities.Test.class);
        
        final List<com.bc.elmi.pu.entities.Test> arr = dao.where(Test_.durationinminutes, (Object)null).getResultsAndClose();
        
        System.out.println("Result count: " + arr.size());

        for(com.bc.elmi.pu.entities.Test t : arr) {
            System.out.println(t);
        }
    }
}
