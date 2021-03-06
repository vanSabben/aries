/*
// * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIESOR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jpa.eclipselink.adapter;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.apache.aries.jpa.eclipselink.adapter.platform.OSGiTSServer;
import org.apache.aries.util.nls.MessageUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service factory for generating the Eclipselink OSGi compatible provider. It proxies the provider so that
 * we can go in at entity manager creation time and set the eclipselink target-server to be {@link OSGiTSServer}.
 */
public class EclipseLinkProviderService implements ServiceFactory {
  private static final Logger logger = LoggerFactory.getLogger(Activator.class);
  private static final MessageUtil MESSAGES = MessageUtil.createMessageUtil(EclipseLinkProviderService.class, "org.apache.aries.jpa.eclipselink.adapter.jpaEclipseLinkAdapter");
  
  private final Bundle eclipseLinkJpaBundle;
    
  public EclipseLinkProviderService(Bundle b) {
      eclipseLinkJpaBundle = b;
  }
  
  public Object getService(Bundle bundle, ServiceRegistration registration) {
    logger.debug("Requested EclipseLink Provider service");
    
    try {
      Class<? extends PersistenceProvider> providerClass = eclipseLinkJpaBundle.loadClass(Activator.ECLIPSELINK_JPA_PROVIDER_CLASS_NAME);
      Constructor<? extends PersistenceProvider> con = providerClass.getConstructor();
      final PersistenceProvider provider = con.newInstance();
      
      return new PersistenceProvider() {
        public ProviderUtil getProviderUtil() {
          return provider.getProviderUtil();
        }
        
        public EntityManagerFactory createEntityManagerFactory(String arg0, Map arg1) {
          return provider.createEntityManagerFactory(arg0, arg1);
        }
        
        public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo punit, Map props) {
          return provider.createContainerEntityManagerFactory(new PersistenceUnitProxyWithTargetServer(punit, 
                eclipseLinkJpaBundle), props);
        }
      };
        
    } catch (Exception e) {
        logger.error(MESSAGES.getMessage("error.creating.eclipselink.provider"), e);
        return null;                
    }
  }

  public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {}
}