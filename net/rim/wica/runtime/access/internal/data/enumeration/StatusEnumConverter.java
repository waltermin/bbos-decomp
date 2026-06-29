package net.rim.wica.runtime.access.internal.data.enumeration;

import net.rim.device.api.util.IntIntHashtable;

public final class StatusEnumConverter {
   protected static IntIntHashtable _commonToDevice = new IntIntHashtable(7);
   protected static IntIntHashtable _deviceToCommon = new IntIntHashtable(7);

   public static final int deviceToCommon(int value) {
      return _deviceToCommon.get(value);
   }

   public static final int commonToDevice(int value) {
      return _commonToDevice.get(value);
   }

   static {
      _commonToDevice.put(2, 2);
      _commonToDevice.put(4, 4);
      _commonToDevice.put(1, 1);
      _commonToDevice.put(0, 0);
      _commonToDevice.put(3, 3);
      _deviceToCommon.put(2, 2);
      _deviceToCommon.put(4, 4);
      _deviceToCommon.put(1, 1);
      _deviceToCommon.put(0, 0);
      _deviceToCommon.put(3, 3);
   }
}
