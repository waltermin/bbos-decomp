package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class StatusBatteryLevel extends StatusListItem {
   public StatusBatteryLevel() {
      super(401);
   }

   @Override
   public final String getDisplayValue() {
      if ((DeviceInfo.getBatteryStatus() & 2097152) != 0) {
         return CommonResources.getString(104);
      }

      int level = DeviceInfo.getBatteryLevel();
      level = 5 * ((level + 2) / 5);
      if (level > 100) {
         level = 100;
      } else if (level < 0) {
         level = 0;
      }

      return level + OptionsResources.getString(413);
   }
}
