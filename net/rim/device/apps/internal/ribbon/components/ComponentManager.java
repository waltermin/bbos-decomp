package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.FontRegistry;

public final class ComponentManager {
   private static SignalComponentFactory _signalComponentFactory;
   private static WLANSignalComponentFactory _wlanSignalComponentFactory;

   private ComponentManager() {
   }

   public static final void init() {
      new BatteryComponentFactory().init();
      new CoverageComponentFactory().init();
      new RoamingComponentFactory().init();
      new GPSComponentFactory().init();
      new CurrentDateTimeComponentFactory().init();
      _signalComponentFactory = new SignalComponentFactory();
      _signalComponentFactory.init();
      if (WLAN.isSupported()) {
         _wlanSignalComponentFactory = new WLANSignalComponentFactory();
         _wlanSignalComponentFactory.init();
      }

      new SystemStatusComponentFactory().init();
      new TextFieldFactory().init();
      new TitleFieldFactory().init();
      new ImageFactory().init();
      new EntryIconComponentFactory().init();
      new EntryBitmapComponentFactory().init();
      new AnalogClockComponentFactory().init();
      new OwnerInfoFactory().init();
      new EntryDescriptionComponentFactory().init();
   }

   public static final void toggleDisplayMode() {
      if (WLAN.isSupported() && _wlanSignalComponentFactory != null) {
         _wlanSignalComponentFactory._numericDisplay = !_wlanSignalComponentFactory._numericDisplay;
         _wlanSignalComponentFactory.updateSignalLevel();
      }

      _signalComponentFactory._numericDisplay = !_signalComponentFactory._numericDisplay;
      _signalComponentFactory.updateSignalLevel();
   }

   static {
      FontRegistry.loadFont("LargeTimerFont_14B.cbtf", "net_rim_bb_framework_api", "LargeTimerFont");
   }
}
