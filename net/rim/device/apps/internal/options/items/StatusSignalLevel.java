package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class StatusSignalLevel extends StatusListItem {
   public StatusSignalLevel() {
      super(409);
   }

   @Override
   public final String getDisplayValue() {
      String displayValue = null;
      if (RadioInfo.getState() == 0) {
         return OptionsResources.getString(411);
      }

      int signalLevel = RadioInfo.getSignalLevel();
      return signalLevel == -256
         ? OptionsResources.getString(412)
         : ((StringBuffer)(new Object())).append(String.valueOf(signalLevel)).append(OptionsResources.getString(410)).toString();
   }
}
