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

import com.bc.elmi.pu.entities.Unit;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2019 9:30:35 PM
 */
public class UnitOrChildHasIdOrName implements Predicate<Unit>{

    private final String idOrUnitName;

    public UnitOrChildHasIdOrName(Object idOrUnitName) {
        this.idOrUnitName = idOrUnitName.toString();
    }

    @Override
    public boolean test(Unit unit) {
        if(idOrUnitName.equals(unit.getUnitid().toString()) || idOrUnitName.equals(unit.getUnitname())) {
            return true;
        }else{
            final List<Unit> childUnits = unit.getUnitList();
            if(childUnits == null || childUnits.isEmpty()){
                return false;
            }else{
                for(Unit childUnit : childUnits) {
                    if(test(childUnit)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
