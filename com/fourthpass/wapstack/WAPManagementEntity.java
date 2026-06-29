package com.fourthpass.wapstack;

public final class WAPManagementEntity {
   private static byte[][] _timerSettings = new byte[][]{{2, 2}, {0, 1}, {4, 4}, {5, 5}, {3, 4}, {7, 7}, {3, 3}, {40, 40}, {120, 120}};
   private static byte[][] _counterSettings = new byte[][]{{8, 8}, {6, 6}};
   private static short _WTP_PDUBufferSize = 64;
   private static short _WSP_PDUBufferSize = 64;

   public static final short getWSP_PDUBufferSize() {
      return _WSP_PDUBufferSize;
   }

   public static final short getWTP_PDUBufferSize() {
      return _WTP_PDUBufferSize;
   }

   public static final byte getTimerSetting(byte type, boolean userAck) {
      byte withUserAck = 0;
      if (userAck) {
         withUserAck = 1;
      }

      byte value = 0;

      try {
         return _timerSettings[type][withUserAck];
      } finally {
         ;
      }
   }

   public static final byte getCounterSetting(byte type, boolean userAck) {
      byte withUserAck = 0;
      if (userAck) {
         withUserAck = 1;
      }

      byte value = 0;

      try {
         return _counterSettings[type][withUserAck];
      } finally {
         ;
      }
   }
}
