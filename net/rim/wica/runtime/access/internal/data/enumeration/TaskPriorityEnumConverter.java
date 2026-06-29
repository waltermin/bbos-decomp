package net.rim.wica.runtime.access.internal.data.enumeration;

import net.rim.device.api.util.IntIntHashtable;

public final class TaskPriorityEnumConverter {
   protected static IntIntHashtable _commonToDevice = (IntIntHashtable)(new Object(4));
   protected static IntIntHashtable _deviceToCommon = (IntIntHashtable)(new Object(4));

   public static final int deviceToCommon(int value) {
      return _deviceToCommon.get(value);
   }

   public static final int commonToDevice(int value) {
      return _commonToDevice.get(value);
   }

   static {
      _commonToDevice.put(0, 1);
      _commonToDevice.put(0, 2);
      _commonToDevice.put(0, 3);
      _commonToDevice.put(1, 0);
      _commonToDevice.put(1, 4);
      _commonToDevice.put(1, 5);
      _commonToDevice.put(1, 6);
      _commonToDevice.put(2, 7);
      _commonToDevice.put(2, 8);
      _commonToDevice.put(2, 9);
      _deviceToCommon.put(1, 0);
      _deviceToCommon.put(2, 0);
      _deviceToCommon.put(3, 0);
      _deviceToCommon.put(0, 1);
      _deviceToCommon.put(4, 1);
      _deviceToCommon.put(5, 1);
      _deviceToCommon.put(6, 1);
      _deviceToCommon.put(7, 2);
      _deviceToCommon.put(8, 2);
      _deviceToCommon.put(9, 2);
   }
}
