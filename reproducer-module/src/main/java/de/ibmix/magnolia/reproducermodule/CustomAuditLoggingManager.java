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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.audit.AuditLoggingManager;
import info.magnolia.audit.LogConfiguration;
import info.magnolia.audit.LoggingLevel;
import info.magnolia.objectfactory.Components;

public class CustomAuditLoggingManager extends AuditLoggingManager {
	
	private List<LogConfiguration> logConfigurations = new ArrayList<LogConfiguration>();

    private Set<String> excludeWorkspaces = new HashSet<>(Arrays.asList("userranking", "messages", "tasks"));

    private String defaultSeparator = ", ";

    private static Logger applog = LoggerFactory.getLogger(AuditLoggingManager.class);
    
    public static CustomAuditLoggingManager getInstance() {
        try {
            return Components.getComponent(CustomAuditLoggingManager.class);
        } catch (Exception e) {
            // if not defined skip and return null
            applog.info("Class CustomAuditLoggingManager not defined");
            return null;
        }
    }
    
    @Override
    public void addLogConfigurations(LogConfiguration action) {
        this.logConfigurations.add(action);
    }

    @Override
    public void setLogConfigurations(List<LogConfiguration> logConfigurations) {
        this.logConfigurations = logConfigurations;
    }

    @Override
    public LogConfiguration getLogConfiguration(String action) {
    	CustomAuditLoggingManager.applog.debug("USING CustomAuditLoggingManager!!!!!");
    	Iterator<LogConfiguration> iterator = this.logConfigurations.iterator();
        while (iterator.hasNext()) {
            final LogConfiguration trail = iterator.next();
            if (StringUtils.equals(trail.getName(), action)) {
                return trail;
            }
        }
        return null;
    }

    /**
     * AuditLogging is active when at least one of log configurations is active.
     */
    @Override
    public boolean isAuditLoggingActive() {
        Iterator<LogConfiguration> iterator = this.logConfigurations.iterator();
        while (iterator.hasNext()) {
            final LogConfiguration trail = iterator.next();
            if (trail.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void log(String action, String[] data) {
        StringBuilder message = new StringBuilder();
        LogConfiguration trail = this.getLogConfiguration(action);
        if (trail == null) {
            applog.trace("Can't get log configuration");
        } else {
            String separator = defaultSeparator;
            if (!StringUtils.isEmpty(trail.getSeparator())) {
                separator = trail.getSeparator();
            }
            message.append("[CUSTOM AUDIT]");
            message.append(separator).append(action);
            if (trail.isActive()) {
                for (int i = 0; i < data.length; i++) {
                    if (StringUtils.isNotEmpty(data[i])) {
                        message.append(separator).append(data[i]);
                    }

                }
                
                LogManager.getLogger(trail.getLogName()).log(LoggingLevel.AUDIT_TRAIL, message.toString());
            }
        }
    }

    @Override
    public String getDefaultSeparator() {
        return defaultSeparator;
    }

    @Override
    public void setDefaultSeparator(String defaultSeparator) {
        this.defaultSeparator = defaultSeparator;
    }

    @Override
    public Set<String> getExcludeWorkspaces() {
        return excludeWorkspaces;
    }

    @Override
    public void setExcludeWorkspaces(Set<String> excludeWorkspaces) {
        this.excludeWorkspaces = excludeWorkspaces;
    }

    public void postModuleStart() {
        applog.debug("postModuleStart(): logConfigurations = {}", getLogConfigurations());
        for (LogConfiguration logConfiguration : getLogConfigurations()) {
            applog.debug(logConfiguration.getLogName() + ": logName = " + logConfiguration.getLogName());
        }
    }

}