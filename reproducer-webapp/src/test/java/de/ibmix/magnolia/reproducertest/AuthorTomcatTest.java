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
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.ibmix.magkit.test.server.MagnoliaConfigSelector;
import de.ibmix.magkit.test.server.MagnoliaConfigurer;
import de.ibmix.magkit.test.server.MagnoliaTomcatExtension;
import info.magnolia.audit.AuditLoggingManager;
import info.magnolia.audit.LogConfiguration;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.SiteManager;
import info.magnolia.module.site.theme.ThemeReference;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
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

    private static final String MAGNOLIA_RESOURCES_DIR_CONFIG_PROP = "magnolia.resources.dir";

    @Override
    public Map<String, String> getSystemPropsToSet() {
        Path lightmodulesDir = Paths.get("").toAbsolutePath().resolve("./modules");
        return Map.of(
            // same as in
            // /reproducer-webapp/src/main/webapp/WEB-INF/config/shared/magnolia.properties
            "log4j.config",
            "WEB-INF/config/reproducer-log4j2.xml",
            // tell Magnolia where to find the hello-magnolia lightmodule
            MAGNOLIA_RESOURCES_DIR_CONFIG_PROP,
            lightmodulesDir.toAbsolutePath().toString());
    }

    /**
     * Example of e.g. how to obtain a Magnolia component and test it:
     * In our module, we are bootstrapping reproducer-module/src/main/resources/mgnl-bootstrap/reproducer-module/config.server.auditLogging.logConfigurations.yaml
     * verify that all logConfigurations in the AuditLoggingManager object are now configured to send events.
     * @throws InterruptedException
     * @throws RepositoryException
     * @throws LoginException
     */
    @Test
    public void testAuditLoggingConfig() throws LoginException, RepositoryException {
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

        // obtain object from registry
        AuditLoggingManager auditLoggingManager = Components.getComponent(AuditLoggingManager.class);
        // verify bootstrapped configuration was properly set using node2bean in
        // the actual Java object
        List<LogConfiguration> logConfigurations = auditLoggingManager.getLogConfigurations();
        assertEquals(12, logConfigurations.size());
        for (LogConfiguration logConfiguration : logConfigurations) {
            assertTrue(logConfiguration.isSendEvents());
        }
    }

    @Test
    public void testSiteConfiguration() {
        // verify config in
        // /reproducer-module/src/main/resources/reproducer-module/decorations/multisite/config.sites.yaml
        // works
        SiteManager siteManager = Components.getComponent(SiteManager.class);
        assertNotNull(siteManager);
        // the default site is configured with name "fallback"
        Site defaultSite = siteManager.getDefaultSite();
        assertNotNull(defaultSite);

        I18nContentSupport i18n = defaultSite.getI18n();
        assertNotNull(i18n);
        Map<String, Locale> locales =
            i18n.getLocales().stream().collect(
                Collectors.toMap(
                    Locale::toLanguageTag,
                    l -> l));
        String[] expectedLanguages =
            {
                "en",
                "de",
                "it",
                "no"
            };
        Arrays.stream(expectedLanguages).forEach(
            l -> assertNotNull(locales.get(l)));

        assertNotNull(defaultSite.getDomains());
        assertNotNull(defaultSite.getMappings());
        assertNotNull(defaultSite.getTemplates());
        ThemeReference theme = defaultSite.getTheme();
        assertNotNull(theme);
    }

    /**
     * Verify our sample page was bootstrapped.
     */
    @Test
    public void testSamplePageBootstrap() throws LoginException, RepositoryException {
        // make sure
        // /reproducer-module/src/main/resources/mgnl-bootstrap-samples/reproducer-module/website.Hello-Magnolia.yaml
        // was bootstrapped
        SystemContext systemContext = Components.getComponent(SystemContext.class);
        MgnlContext.setInstance(systemContext);
        assertTrue(systemContext.getJCRSession("website").nodeExists("/Hello-Magnolia"));
    }

}
