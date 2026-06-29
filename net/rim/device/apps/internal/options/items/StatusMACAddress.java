package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.general.SerialNumber;

public final class StatusMACAddress extends StatusListItem {
   private int _macType = -1;
   public static final int MAC_ADDRESS_WLAN = 0;
   public static final int MAC_ADDRESS_BLUETOOTH = 1;

   public StatusMACAddress(int macType) {
      switch (macType) {
         case 0:
         default:
            this._macType = 0;
            this.setResourceID(1926);
            return;
         case 1:
            this._macType = 1;
         case -1:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getDisplayValue() {
      String sn = SerialNumber.getMACAddress(this._macType);
      if (sn == null) {
         sn = CommonResources.getString(105);
      }

      return sn;
   }
}
