package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.apps.internal.ribbon.banners.RibbonBannerImpl;
import net.rim.device.apps.internal.ribbon.components.ComponentManager;
import net.rim.device.apps.internal.ribbon.indicators.IndicatorManagerImpl;

public final class SystemManagementApp {
   public static final void init() {
      RadioOffWarningManagerImpl.init();
      SystemMonitor.init();
      ComponentManager.init();
      IndicatorManagerImpl.init();
      RibbonBannerImpl.init();
      StandbyManagerImpl.init();
      SystemOnOffManager.init();
   }
}
