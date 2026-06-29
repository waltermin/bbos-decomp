package net.rim.wica.runtime.access.internal.data.enumeration;

import net.rim.device.api.util.IntIntHashtable;

public class EmailFolderEnumConverter {
   protected static IntIntHashtable _commonToDevice = (IntIntHashtable)(new Object(3));
   protected static IntIntHashtable _deviceToCommon = (IntIntHashtable)(new Object(3));

   public static int deviceToCommon(int value) {
      return _deviceToCommon.get(value);
   }

   public static int commonToDevice(int value) {
      return _commonToDevice.get(value);
   }

   static {
      _commonToDevice.put(0, 2);
      _commonToDevice.put(1, 4);
      _deviceToCommon.put(2, 0);
      _deviceToCommon.put(4, 1);
   }
}
