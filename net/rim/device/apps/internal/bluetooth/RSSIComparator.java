package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.util.Comparator;

final class RSSIComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int rssi1 = ((BluetoothDevice)o1).getRSSI();
      int rssi2 = ((BluetoothDevice)o2).getRSSI();
      return rssi2 - rssi1;
   }
}
