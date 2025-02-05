package de.ibmix.magnolia.reproducermodule;

/*-
 * #%L
 * magkit-test-server Maven Module
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.audit.AuditLoggingManager;
import info.magnolia.audit.LogConfiguration;

/**
 * Custom class to override AuditLoggingManager.
 *
 * @author jfrantzius
 */
public class CustomAuditLoggingManager extends AuditLoggingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuditLoggingManager.class);

    public CustomAuditLoggingManager() {
        LOGGER.debug("CustomAuditLoggingManager constructor");
    }

    public void postModuleStart() {
        LOGGER.debug("postModuleStart(): logConfigurations = {}", getLogConfigurations());
        for (LogConfiguration logConfiguration : getLogConfigurations()) {
            LOGGER.debug(logConfiguration.getLogName() + ": logName = " + logConfiguration.getLogName());
        }
    }

}
