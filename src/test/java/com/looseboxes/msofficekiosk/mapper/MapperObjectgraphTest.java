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

package com.looseboxes.msofficekiosk.mapper;

import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.mapper.MapperObjectgraph;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2019 6:54:24 PM
 */
public class MapperObjectgraphTest extends MapperJacksonTest{

    @Override
    public Mapper getInstance() {
        return new MapperObjectgraph();
    }
}
