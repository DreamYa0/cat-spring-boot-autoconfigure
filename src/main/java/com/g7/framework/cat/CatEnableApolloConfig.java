package com.g7.framework.cat;

import com.g7.framework.cat.dubbo.DubboCat;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author dreamyao
 * @title
 * @date 2019/3/7 10:58 AM
 * @since 1.0.0
 */
class CatEnableApolloConfig {

    private static final Logger logger = LoggerFactory.getLogger(CatEnableApolloConfig.class);
    /**
     * 描述当前cat监控的状态 true表示已开启、false表示已关闭
     */
    private volatile boolean currentCatState = true;
    private static final String CAT_MONITOR_ENABLED_KEY = "cat.monitor.enabled";
    private static volatile CatEnableApolloConfig instance;

    protected static CatEnableApolloConfig getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (CatEnableApolloConfig.class) {
                if (Objects.isNull(instance)) {
                    instance = new CatEnableApolloConfig();
                }
            }
        }
        return instance;
    }

    private CatEnableApolloConfig() {
        init();
    }

    private void init() {

        Config config = ConfigService.getAppConfig();

        Set<String> propertyNames = config.getPropertyNames();
        if (propertyNames.contains(CAT_MONITOR_ENABLED_KEY)) {

            String enable = config.getProperty(CAT_MONITOR_ENABLED_KEY, "true");
            currentCatState = Boolean.parseBoolean(enable);
        }

        config.addChangeListener(changeEvent -> {

            logger.info("apollo config changes for namespace {}", changeEvent.getNamespace());

            // 获取配置中心cat开关的key是否发生变化
            boolean isChanged = changeEvent.isChanged(CAT_MONITOR_ENABLED_KEY);
            if (isChanged) {

                ConfigChange configChange = changeEvent.getChange(CAT_MONITOR_ENABLED_KEY);
                PropertyChangeType changeType = configChange.getChangeType();

                changeCatMonitor(configChange, changeType);

                aFreshCatMonitor(changeType);
            }
        });
    }

    protected boolean isCurrentCatState() {
        return currentCatState;
    }

    private void aFreshCatMonitor(PropertyChangeType changeType) {
        // 如果配置中心删除了cat的配置，如果cat之前是处于关闭状态，则从新开启cat
        boolean isDelete = Objects.equals(changeType, PropertyChangeType.DELETED);
        if (isDelete && Boolean.FALSE.equals(currentCatState)) {
            Cat.enable();
            DubboCat.enable();
            logger.info("------------------------------------------ open cat monitor ------------------------------------------");
            currentCatState = true;
        }
    }

    private void changeCatMonitor(ConfigChange configChange, PropertyChangeType changeType) {

        // 如果是新增cat开关配置或更新配置
        boolean isAddOrModify = Objects.equals(changeType, PropertyChangeType.ADDED) || Objects.equals(changeType, PropertyChangeType.MODIFIED);
        if (isAddOrModify) {

            String newConfigValue = configChange.getNewValue();
            openCatMonitor(newConfigValue);
            closeCatMonitor(newConfigValue);
        }
    }

    private void openCatMonitor(String configValue) {

        boolean isOpen = Boolean.FALSE.equals(StringUtils.isEmpty(configValue)) && "true".equalsIgnoreCase(configValue) && Boolean.FALSE.equals(currentCatState);
        if (isOpen) {
            Cat.enable();
            DubboCat.enable();
            logger.info("------------------------------------------ open cat monitor ------------------------------------------");
            currentCatState = true;
        }
    }

    private void closeCatMonitor(String configValue) {

        boolean isClose = Boolean.FALSE.equals(StringUtils.isEmpty(configValue)) && "false".equalsIgnoreCase(configValue) && currentCatState;
        if (isClose) {
            Cat.disable();
            DubboCat.disable();
            logger.info("------------------------------------------ close cat monitor ------------------------------------------");
            currentCatState = false;
        }
    }
}
