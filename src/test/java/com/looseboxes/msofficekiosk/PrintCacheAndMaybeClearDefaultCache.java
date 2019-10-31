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

import com.looseboxes.msofficekiosk.FileNames;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.MsKioskConfiguration;
import com.looseboxes.msofficekiosk.Main;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.mapper.MapperJackson;
import com.bc.diskcache.DiskLruCacheContext;
import com.bc.diskcache.DiskLruCacheContextImpl;
import com.bc.diskcache.DiskLruCacheIx;
import com.looseboxes.msofficekiosk.functions.CacheProvider;
import com.looseboxes.msofficekiosk.net.Rest;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 30, 2019 10:36:01 PM
 */
public class PrintCacheAndMaybeClearDefaultCache {

    public static void main(String [] args) {
        
        try{
            
            final DiskLruCacheContext dcc = new DiskLruCacheContextImpl(10_000_000);
            final Mapper mapper = new MapperJackson();
            final Cache cache = new CacheProvider(
                    dcc, Main.DIR_HOME.resolve(FileNames.DIR_CACHE), mapper)
                    .apply(MsKioskConfiguration.DEFAULT_CACHE_NAME);

            final String [] arr = {
                Rest.RESULTNAME_USER, Rest.RESULTNAME_DEVICEDETAILS, Rest.RESULTNAME_TESTS
            };

            for(String key : arr) {

                System.out.println("Printing value for key: " + key);

                Object val = ((DiskLruCacheIx)cache.getDelegate()).getString(key, null);

                System.out.println(val);
            }

            cache.clear();
            cache.close();
            dcc.closeAndRemoveAll();
        
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
