package io.github.vikindor.configs;

import org.aeonbits.owner.ConfigFactory;

public final class ConfigProvider {

    private ConfigProvider() {}

    private static final ProjectConfig CONFIG = ConfigFactory.create(ProjectConfig.class);

    public static ProjectConfig config() {
        return CONFIG;
    }
}
