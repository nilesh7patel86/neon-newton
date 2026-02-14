package com.neon.newton.plugin;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class AnotherPlugin extends Plugin {
    public AnotherPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("Another Plugin started!");
    }

    @Override
    public void stop() {
        System.out.println("Another Plugin stopped!");
    }
}
