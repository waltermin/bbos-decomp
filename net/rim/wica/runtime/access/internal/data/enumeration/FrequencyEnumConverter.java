package net.rim.wica.runtime.access.internal.data.enumeration;

import net.rim.device.api.util.IntIntHashtable;

public class FrequencyEnumConverter {
   protected static IntIntHashtable _commonToDevice = (IntIntHashtable)(new Object(7));
   protected static IntIntHashtable _deviceToCommon = (IntIntHashtable)(new Object(7));

   public static int deviceToCommon(int value) {
      return _deviceToCommon.get(value);
   }

   public static int commonToDevice(int value) {
      return _commonToDevice.get(value);
   }

   static {
      _commonToDevice.put(0, 0);
      _commonToDevice.put(1, 1);
      _commonToDevice.put(2, 2);
      _commonToDevice.put(3, 3);
      _commonToDevice.put(4, 4);
      _deviceToCommon.put(0, 0);
      _deviceToCommon.put(1, 1);
      _deviceToCommon.put(2, 2);
      _deviceToCommon.put(3, 3);
      _deviceToCommon.put(4, 4);
   }
}
