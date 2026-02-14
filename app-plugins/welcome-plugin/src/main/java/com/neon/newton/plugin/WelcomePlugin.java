package com.neon.newton.plugin;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class WelcomePlugin extends Plugin {
    public WelcomePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("Welcome Plugin started!");
    }

    @Override
    public void stop() {
        System.out.println("Welcome Plugin stopped!");
    }
}
