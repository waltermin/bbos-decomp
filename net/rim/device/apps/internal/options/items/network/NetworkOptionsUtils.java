package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.SE13NetworkTable;
import net.rim.vm.Array;

final class NetworkOptionsUtils {
   static final int MCC_MNC_SIZE = 3;

   public static final int scanForNetworks() {
      SpinnerDialog d = new SpinnerDialog(1, -1, -1);
      int result = d.go();
      switch (result) {
         case -1001:
            Dialog.alert(OptionsResources.getString(1417));
         case -1002:
            return result;
         case -1000:
         default:
            Dialog.alert(OptionsResources.getString(919));
            return result;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final NetworkInfo[] getAvailableNetworks() {
      int size = RadioInfo.getNumberOfNetworks();
      NetworkInfo[] infos = new Object[size];
      int i = 0;

      while (true) {
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            if (i >= size) {
               var5 = false;
               break;
            }

            infos[i] = (NetworkInfo)(new Object());
            infos[i].setName(getAvailableNetworkName(i));
            infos[i].setNetworkId(RadioInfo.getNetworkId(i));
            infos[i].setCategory(RadioInternal.getNetworkCategory(i));
            i++;
         } finally {
            if (var5) {
               Array.resize(infos, i);
               break;
            }
         }
      }

      return infos;
   }

   public static final boolean is3GSupported() {
      if (RadioInfo.getNetworkType() == 7) {
         try {
            return SIMCard.isUSIMPresent();
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }

   public static final int clearFlag(int original, int flagToClear) {
      return original & ~flagToClear;
   }

   public static final String getAvailableNetworkName(int index) {
      ResourceBundle rb = OptionsResources.getResourceBundle();
      String name = null;
      String none = rb.getString(907);
      int activeWAFs = RadioInfo.getActiveWAFs();

      int netId;
      try {
         netId = RadioInfo.getNetworkId(index);
      } finally {
         ;
      }

      if (index >= 0) {
         if ((activeWAFs & 1) != 0) {
            RibbonNetworkInfo ni = RibbonNetworkInfo.getInstance();
            if (ni != null) {
               label209:
               try {
                  name = ni.getOperatorName(
                     netId & 65535,
                     netId >> 16 & 65535,
                     RadioInternal.getNetworkLAC(index),
                     RadioInfo.getNetworkCountryCode(index),
                     RadioInternal.getNetworkCategory(index)
                  );
               } finally {
                  break label209;
               }
            }
         }

         if (name == null || name.length() == 0) {
            label203:
            try {
               name = RadioInfo.getNetworkName(index);
            } finally {
               break label203;
            }
         }
      }

      if (name == null || name.length() == 0) {
         if ((activeWAFs & 1) != 0) {
            label195:
            try {
               StringBuffer strBuf = (StringBuffer)(new Object(32));
               NumberUtilities.appendNumber(strBuf, netId & 65535, 16);
               strBuf.append('-');
               NumberUtilities.appendNumber(strBuf, netId >> 16 & 65535, 16);
               name = strBuf.toString();
            } finally {
               break label195;
            }
         } else {
            name = Integer.toHexString(netId);
         }
      }

      if (name == null || name.length() == 0) {
         name = none;
      }

      return name;
   }

   public static final SimCardEfHandler getPreferredNetworks(SimCardEfHandlerCallback cb) {
      SimCardEfHandler handler = new SimCardEfHandler(cb);
      handler.read();
      return handler;
   }

   public static final String getPredefinedNetworkName(int netId) {
      SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
      return se13NetTable.getNetworkName(netId);
   }

   public static final NetworkInfo[] getPredefinedNetworks() {
      SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
      return se13NetTable.getPredefinedNetworkTable();
   }

   public static final String mobileCodeToString(int code) {
      return Integer.toHexString(code).toUpperCase();
   }

   public static final int stringToMobileCode(String str) {
      return Integer.valueOf(str, 16);
   }

   public static final String buildNetIdString(NetworkInfo netInfo) {
      if (netInfo != null) {
         StringBuffer idStr = (StringBuffer)(new Object());
         if (is3GSupported()) {
            int cat = netInfo.getCategory();
            if ((cat & 64) != 0) {
               idStr.append(OptionsResources.getStringArray(1970)[0]);
               idStr.append(' ');
            } else if ((cat & 16) != 0) {
               idStr.append(OptionsResources.getStringArray(1970)[1]);
               idStr.append(' ');
            }
         }

         idStr.append(mobileCodeToString(netInfo.getMcc()));
         idStr.append("/");
         idStr.append(mobileCodeToString(netInfo.getMnc()));
         return idStr.toString();
      } else {
         return "";
      }
   }
}
