package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.general.SerialNumber;

public final class StatusSerialNumber extends StatusListItem {
   private boolean _hexDisplay;
   private int _waf;

   public StatusSerialNumber(boolean hexDisplay, int waf) {
      this._waf = waf;
      int labelIndex;
      switch (waf) {
         case 2:
            if (hexDisplay) {
               labelIndex = 416;
            } else {
               labelIndex = 415;
            }
            break;
         case 4:
            labelIndex = 1926;
            break;
         default:
            labelIndex = 414;
      }

      this.setResourceID(labelIndex);
      this._hexDisplay = hexDisplay;
   }

   @Override
   public final String getDisplayValue() {
      String sn = SerialNumber.getSerialNumber(this._waf);
      if (RadioInfo.areWAFsSupported(2) && !this._hexDisplay) {
         sn = SerialNumber.getDecimalSerialNumber(this._waf);
      }

      if (sn == null) {
         sn = CommonResources.getString(105);
      }

      return sn;
   }
}
