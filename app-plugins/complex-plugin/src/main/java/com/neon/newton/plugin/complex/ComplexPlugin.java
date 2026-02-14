package com.neon.newton.plugin.complex;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class ComplexPlugin extends Plugin {
    public ComplexPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("Complex Plugin started!");
    }

    @Override
    public void stop() {
        System.out.println("Complex Plugin stopped!");
    }
}
