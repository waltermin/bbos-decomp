package net.rim.device.apps.internal.bis.utils.system;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager$Instance;
import net.rim.device.apps.api.utility.general.SerialNumber;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAFMConfigurationManagerImpl;

public final class DeviceUtils {
   private static final char ESN_SEPARATOR;
   private static final int ESN_SEPARATOR_INDEX;

   public static final boolean isDeleteSyncEnabled(String serviceRecordName) {
      boolean available = false;
      OTAFMConfigurationManagerImpl manager = (OTAFMConfigurationManagerImpl)OTAFMConfigurationManager$Instance.getInstance();
      ServiceBook serviceBook = ServiceBook.getSB();
      ServiceRecord[] serviceRecords = serviceBook.findRecordsByName(serviceRecordName);
      if (serviceRecords != null && serviceRecords.length > 0 && "CMIME".equals(serviceRecords[0].getCid())) {
         OTAFMConfiguration config = manager.getConfiguration(serviceRecords[0]);
         if (config != null) {
            available = config.getWirelessDeletesEnabled();
         }
      }

      return available;
   }

   static final String getIMEIESN() {
      String imeiESN = null;
      if (RadioInfo.getNetworkType() == 4) {
         StringBuffer esn = (StringBuffer)(new Object(SerialNumber.getDecimalSerialNumber()));
         esn.insert(3, '/');
         return esn.toString();
      } else {
         return SerialNumber.getSerialNumber();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final String getIMSI() {
      if (RadioInfo.getNetworkType() == 3) {
         try {
            byte[] bytes = SIMCard.getIMSI();
            return SIMCard.imsiToString(bytes);
         } catch (Throwable var4) {
            BISEventLogger.logEvent(sce.toString(), 0);
            return null;
         }
      } else if (RadioInfo.getNetworkType() != 4) {
         if (RadioInfo.getNetworkType() == 5) {
         }

         return null;
      } else {
         byte[] imsi = CDMAInfo.getIMSI();
         if (imsi == null) {
            return null;
         }

         StringBuffer buff = (StringBuffer)(new Object());

         for (int i = 0; i < imsi.length; i++) {
            NumberUtilities.appendNumber(buff, imsi[i], 16);
         }

         return buff.toString();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final String getICCID() {
      if (RadioInfo.getNetworkType() == 3) {
         try {
            byte[] bytes = SIMCard.getICCID();
            return SIMCard.iccidToString(bytes);
         } catch (Throwable var2) {
            BISEventLogger.logEvent(sce.toString(), 0);
            return null;
         }
      } else {
         if (RadioInfo.getNetworkType() == 4) {
            return null;
         }

         if (RadioInfo.getNetworkType() == 5) {
         }

         return null;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final String getMSISDN() {
      String msisdn = null;

      try {
         return Phone.getInstance().getNumber(0);
      } catch (Throwable var3) {
         BISEventLogger.logEvent(e.toString(), 0);
         return msisdn;
      }
   }
}
