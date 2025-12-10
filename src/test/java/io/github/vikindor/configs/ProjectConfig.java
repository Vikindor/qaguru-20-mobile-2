package io.github.vikindor.configs;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:${platform}.properties"
})
public interface ProjectConfig extends Config {

    @Key("userName")
    String browserstackUser();

    @Key("accessKey")
    String browserstackKey();

    @Key("browserstackApp")
    String browserstackApp();

    @Key("browserstackUrl")
    String browserstackUrl();

    @Key("deviceName")
    String deviceName();

    @Key("platformVersion")
    String platformVersion();

    @Key("timeout")
    Integer timeout();

    @Key("appiumUrl")
    String appiumUrl();

    @Key("app")
    String app();

    @Key("appPackage")
    String appPackage();

    @Key("appActivity")
    String appActivity();

    @Key("projectName")
    String projectName();

    @Key("buildName")
    String buildName();

    @Key("sessionName")
    String sessionName();
}
