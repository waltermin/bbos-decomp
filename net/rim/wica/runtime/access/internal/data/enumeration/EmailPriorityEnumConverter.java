package net.rim.wica.runtime.access.internal.data.enumeration;

import net.rim.device.api.util.IntIntHashtable;

public final class EmailPriorityEnumConverter {
   protected static IntIntHashtable _commonToDevice = new IntIntHashtable(4);
   protected static IntIntHashtable _deviceToCommon = new IntIntHashtable(4);

   public static final int deviceToCommon(int value) {
      return _deviceToCommon.get(value);
   }

   public static final int commonToDevice(int value) {
      return _commonToDevice.get(value);
   }

   static {
      _commonToDevice.put(0, 2);
      _commonToDevice.put(2, 3);
      _commonToDevice.put(1, 1);
      _deviceToCommon.put(2, 0);
      _deviceToCommon.put(3, 2);
      _deviceToCommon.put(1, 1);
   }
}
