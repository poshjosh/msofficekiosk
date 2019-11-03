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

import com.bc.jpa.spring.JdbcPropertiesProvider;
import com.bc.jpa.spring.JpaConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on May 25, 2019 6:39:12 PM
 */
public class JpaConfigurationTestCase extends JpaConfiguration{
    
    public static class JdbcPropertiesProviderImpl implements JdbcPropertiesProvider{
        @Override
        public Properties apply(String persistenceUnitName) {
            try(final InputStream in = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("META-INF/test_jdbc.properties")) {
                final Properties output = new Properties();
                output.load(in);
                return output;
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public JpaConfigurationTestCase() {
        super(com.bc.elmi.pu.PersistenceUnit.NAME, new JdbcPropertiesProviderImpl());
    }
}
