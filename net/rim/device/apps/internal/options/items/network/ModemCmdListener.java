package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.ModemListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.RadioInternal;

public final class ModemCmdListener implements ModemListener {
   @Override
   public final void networkSelectionModeChanged(int mode) {
      if ((1 << mode & RadioInternal.getAvailableNetworkSelectionModes()) == 0) {
         System.out.println("Cannot set PLMN selection mode. Mode is unavailable.");
      } else {
         RadioInternal.setNetworkSelectionMode(mode);
      }
   }

   @Override
   public final void queryNetworkDisplayName(int networkId) {
      String name = NetworkOptionsUtils.getPredefinedNetworkName(networkId);
      if (name == null || name.length() == 0) {
         switch (RadioInfo.getNetworkType()) {
            case 3:
            case 7:
               try {
                  StringBuffer strBuf = new StringBuffer(32);
                  NumberUtilities.appendNumber(strBuf, networkId & 65535, 16);
                  strBuf.append('-');
                  NumberUtilities.appendNumber(strBuf, networkId >> 16 & 65535, 16);
                  name = strBuf.toString();
                  break;
               } finally {
                  break;
               }
            default:
               name = Integer.toHexString(networkId);
         }
      }

      if (name == null || name.length() == 0) {
         name = OptionsResources.getResourceBundle().getString(907);
      }

      try {
         RadioInternal.reportNetworkDisplayName(name, name.length());
      } finally {
         System.out.println("reportNetworkDisplayName failed.");
         return;
      }
   }

   @Override
   public final void networkChangeResult(int parameter, int mode) {
   }
}
