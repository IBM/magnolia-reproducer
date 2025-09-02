package de.ibmix.magnolia.reproducermodule.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapSingleModuleResource;
import info.magnolia.module.delta.Task;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is optional and lets you manage the versions of your module,
 * by registering "deltas" to maintain the module's configuration, or other type of content.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 *
 * @see info.magnolia.module.DefaultModuleVersionHandler
 * @see info.magnolia.module.ModuleVersionHandler
 * @see info.magnolia.module.delta.Task
 */
public class ReproducerModuleVersionHandler extends DefaultModuleVersionHandler {

    @Override
    protected List<Task> getStartupTasks(InstallContext installContext) {
        List<Task> startupTasks = new LinkedList<>(super.getStartupTasks(installContext));
        startupTasks.add(new BootstrapSingleModuleResource("Bootstrap audit logging", "Configure logConfigurations' sendeEvent to active", "config.server.auditLogging.logConfigurations.yaml"));
        return startupTasks;
    }
}
