package de.ibmix.magnolia.reproducermodule;

import info.magnolia.audit.AuditLoggingManager;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import info.magnolia.objectfactory.Components;

/**
 * This class is optional and represents the configuration for the reproducer-module module.
 * By exposing simple getter/setter/adder methods, this bean can be configured via content2bean
 * using the properties and node from <tt>config:/modules/reproducer-module</tt>.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 * See https://documentation.magnolia-cms.com/display/DOCS/Module+configuration for information about module configuration.
 */
public class ReproducerModule implements ModuleLifecycle{

    @Override
    public void start(final ModuleLifecycleContext moduleLifecycleContext) {
        AuditLoggingManager customAuditLoggingManager = Components.getComponent(AuditLoggingManager.class);
        if (!"test".equals(customAuditLoggingManager.getLogConfiguration("deactivate").getLogName())) {
            throw new RuntimeException("customAuditLoggingManager was not configured from repo");
        }
    }

    @Override
    public void stop(final ModuleLifecycleContext moduleLifecycleContext) {
        // ignore
    }

}
