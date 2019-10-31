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

package com.looseboxes.msofficekiosk.examples;

import com.bc.elmi.pu.entities.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.looseboxes.msofficekiosk.Entities;
import java.util.Arrays;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2019 7:51:53 PM
 */
public class ObjectMapperExample {

    public static void main(String... args) {
        try{
            final Entities e = new Entities();
            final ObjectMapper mapper = new ObjectMapper();
            final List<Test> list = e.getTestList();
            System.out.println(list);

            final String json = mapper.writeValueAsString(list);
            System.out.println(json);
            
            final Test [] a = mapper.readValue(json, Test[].class);
            System.out.println(Arrays.asList(a));
            
            final List b = mapper.readValue(json, List.class);
            System.out.println(b);
            
            final List<Test> c = mapper.readValue(json, new TypeReference<List<Test>>(){});
            System.out.println(c);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
