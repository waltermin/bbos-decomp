package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.i18n.ResourceBundle;

public final class DeviceSelfTestResources {
   private static ResourceBundle _strings = ResourceBundle.getBundle(2284076378893657675L, "net.rim.device.apps.internal.deviceselftest.DeviceSelfTest");

   private DeviceSelfTestResources() {
   }

   public static final String getString(int id) {
      return _strings.getString(id);
   }
}
