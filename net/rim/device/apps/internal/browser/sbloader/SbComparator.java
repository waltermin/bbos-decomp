package net.rim.device.apps.internal.browser.sbloader;

import net.rim.device.api.util.Comparator;

class SbComparator implements Comparator {
   private static String[] SERVICE_NAMES = new String[]{
      "BlackBerry Prosumer Service", "BlackBerry Enterprise & Prosumer Service", "BlackBerry Enterprise Service"
   };

   @Override
   public int compare(Object o1, Object o2) {
      if (o1 instanceof SbInfo && !(o2 instanceof SbInfo)) {
         return -1;
      } else if (o2 instanceof SbInfo && !(o1 instanceof SbInfo)) {
         return 1;
      } else {
         SbInfo info1 = (SbInfo)o1;
         SbInfo info2 = (SbInfo)o2;
         String deviceName = SBHelper.getDeviceName();
         boolean info1HasDevice = this.hasDevice(info1.getDevices(), deviceName);
         boolean info2HasDevice = this.hasDevice(info2.getDevices(), deviceName);
         if (info1HasDevice && !info2HasDevice) {
            return -1;
         } else if (info2HasDevice && !info1HasDevice) {
            return 1;
         } else {
            int index1 = this.indexOfElement(SERVICE_NAMES, info1.getService());
            int index2 = this.indexOfElement(SERVICE_NAMES, info2.getService());
            if (index1 < index2) {
               return -1;
            } else {
               return index2 < index1 ? 1 : 0;
            }
         }
      }
   }

   private boolean hasDevice(String devices, String device) {
      String[] dev = SBHelper.split(devices, ';');
      int len = dev.length;

      for (int i = 0; i < len; i++) {
         if (dev[i].trim().equals(device)) {
            return true;
         }
      }

      return false;
   }

   private int indexOfElement(String[] array, String elem) {
      int length = array.length;

      for (int i = 0; i < length; i++) {
         if (array[i].equals(elem)) {
            return i;
         }
      }

      return length;
   }
}
