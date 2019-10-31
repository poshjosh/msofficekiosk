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

package com.looseboxes.msofficekiosk.functions.admin;

import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.net.Rest;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2019 1:44:20 PM
 */
public class GetDevicedetailsForUser implements Function<String, Optional<Devicedetails>>{

    private static final Logger LOG = Logger.getLogger(GetDevicedetailsForUser.class.getName());

    private final Cache cache;

    public GetDevicedetailsForUser(Cache cache) {
        this.cache = Objects.requireNonNull(cache);
    }

    @Override
    public Optional<Devicedetails> apply(String username) {
        
        final Collection<Devicedetails> devicedetailsList;
        try{
            devicedetailsList = cache.getListFromJson(Rest.RESULTNAME_DEVICEDETAILS, Devicedetails.class, Collections.EMPTY_LIST);
        }catch(ParseException | IOException e) {
            throw new RuntimeException(e);
        }

        LOG.log(Level.FINER, "Devicedetails: {0}", devicedetailsList);
        
        final Optional<Devicedetails> output = devicedetailsList.stream()
                .filter((dd) -> username.equals(dd.getUsername())).findFirst();
        
        LOG.fine(() -> "Username: " + username + ", device details: " + output);
        
        return output;
    }
}
