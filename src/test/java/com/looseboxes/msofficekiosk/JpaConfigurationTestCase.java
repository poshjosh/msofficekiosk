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

import com.bc.jpa.spring.JpaConfiguration;
import java.util.Properties;
import org.springframework.context.ApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on May 25, 2019 6:39:12 PM
 */
public class JpaConfigurationTestCase extends JpaConfiguration{
    
    @Override
    public String getPersistenceUnitName() {
        return com.bc.elmi.pu.PersistenceUnit.NAME;
    }

    @Override
    public com.bc.jpa.spring.JdbcPropertiesProvider jdbcPropertiesProvider(ApplicationContext spring) {
        final Properties output = new Properties();
        output.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/elmi?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC");
        output.setProperty("javax.persistence.jdbc.user", "root");
        output.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        output.setProperty("javax.persistence.jdbc.password", "Jesus4eva-");
        output.setProperty("eclipselink.logging.level.sql", "FINE");
        output.setProperty("eclipselink.logging.parameters", "true");
        return (puName) -> output;
    }

}
