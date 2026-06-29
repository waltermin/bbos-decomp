package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

final class BatteryComponentFactory implements SystemListener, Factory {
   private int _lastLevel = 0;
   private int _lastStatus = 0;
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();

   final void init() {
      Application app = Application.getApplication();
      app.addSystemListener(this);
      this._lastLevel = DeviceInfo.getBatteryLevel();
      this._lastStatus = DeviceInfo.getBatteryStatus();
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("BatteryLevel", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      BatteryLevelField bf = new BatteryLevelField();
      this._helper.addComponentForUpdate(bf);
      return bf;
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
      int level = DeviceInfo.getBatteryLevel();
      if (level != this._lastLevel || status != this._lastStatus) {
         this._lastLevel = level;
         this._lastStatus = status;
         this._helper.doUpdates();
      }
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }
}
