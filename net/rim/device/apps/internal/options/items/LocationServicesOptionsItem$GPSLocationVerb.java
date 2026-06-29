package net.rim.device.apps.internal.options.items;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class LocationServicesOptionsItem$GPSLocationVerb extends Verb {
   private int _menuKey;
   private final LocationServicesOptionsItem this$0;

   public LocationServicesOptionsItem$GPSLocationVerb(LocationServicesOptionsItem _1, int key) {
      super(16986368, OptionsResources.getResourceBundle(), key);
      this.this$0 = _1;
      this._menuKey = key;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._menuKey) {
         case 1902:
            if (GPS.isSupportedOnCurrentNetwork() || BluetoothSerialPort.isSupported() && this.this$0.isPuckPaired()) {
               if (RadioInfo.getState() != 1 && RadioInfo.getNetworkType() == 5) {
                  UiApplication.getUiApplication().invokeLater(new LocationServicesOptionsItem$GPSLocationVerb$1(this));
                  this.this$0.beep(false);
                  return null;
               } else {
                  new LocationServicesOptionsItem$GPSThread(this.this$0).start();
               }
            }
         default:
            return null;
      }
   }
}
