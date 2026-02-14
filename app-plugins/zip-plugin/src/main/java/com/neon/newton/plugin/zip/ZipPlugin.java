package com.neon.newton.plugin.zip;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class ZipPlugin extends Plugin {
    public ZipPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("ZIP-packaged Plugin started!");
    }

    @Override
    public void stop() {
        System.out.println("ZIP-packaged Plugin stopped!");
    }
}
