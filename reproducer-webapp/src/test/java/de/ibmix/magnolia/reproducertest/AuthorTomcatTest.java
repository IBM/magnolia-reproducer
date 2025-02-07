package de.ibmix.magnolia.reproducertest;

/*-
 * #%L
 * magkit-test-webapp Magnolia Webapp that runs a TomcatTest
 * %%
 * Copyright (C) 2023 - 2025 IBM iX
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.ibmix.magkit.test.server.MagnoliaConfigSelector;
import de.ibmix.magkit.test.server.MagnoliaConfigurer;
import de.ibmix.magkit.test.server.MagnoliaTomcatExtension;
import de.ibmix.magnolia.reproducermodule.CustomAuditLoggingManager;
import info.magnolia.audit.AuditLoggingManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import java.util.Map;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

@TestInstance(Lifecycle.PER_CLASS)
@MagnoliaConfigSelector(magnoliaInstanceType = "author")
@ExtendWith(MagnoliaTomcatExtension.class)
public class AuthorTomcatTest implements MagnoliaConfigurer {

    @Override
    public Map<String, String> getSystemPropsToSet() {
        // same as in /reproducer-webapp/src/main/webapp/WEB-INF/config/shared/magnolia.properties
        return Map.of("log4j.config", "WEB-INF/config/reproducer-log4j2.xml");
    }

    /**
     * Verify our config in magkit-test-server/src/main/resources/magkit-test-server/config.server.auditLogging.yaml
     * was bootstrapped and our custom class has replaced Magnolia's AuditLoggingManager as a component.
     * @throws InterruptedException
     * @throws RepositoryException
     * @throws LoginException
     */
    @Test
    public void testAuditLoggingConfig() throws InterruptedException, LoginException, RepositoryException {
        // verify our bootstrap config was really bootstrapped
        SystemContext systemContext = Components.getComponent(SystemContext.class);
        MgnlContext.setInstance(systemContext);
        Session jcrSession = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        Node node = jcrSession.getNode("/server/auditLogging/logConfigurations/deactivate");
        // self-test our assumption
        assertNotNull(node);

        Property propertyOrNull = PropertyUtil.getPropertyOrNull(node, "logName");
        // self-test our assumption
        assertNotNull(propertyOrNull);

        assertEquals("test", propertyOrNull.getString());

        // obtain AuditLoggingManager and trying to cast it to our class
        CustomAuditLoggingManager customAuditLoggingManager = (CustomAuditLoggingManager) verifyComponentClass(AuditLoggingManager.class, CustomAuditLoggingManager.class);
        // verify bootstrapped configuration was properly set using node2bean in the actual Java object
        assertEquals("test", customAuditLoggingManager.getLogConfiguration("deactivate").getLogName());

    }

    public <C> C verifyComponentClass(Class<C> componentClass, Class<?> expectedClass) {
        // what we get from Components.getComponent() is a ByteBuddy generated object that contains our object, which luckily has the expected name in its toString()
        C foundComponent = Components.getComponent(componentClass);
        String foundClassName = foundComponent.toString().split("@")[0];
        String expectedClassName = expectedClass.getName();
        assertEquals(expectedClassName, foundClassName, "Expected " + componentClass + "component to be " + expectedClass);
        return foundComponent;
    }


}
