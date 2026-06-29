package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;
import net.rim.vm.Array;

final class SystemMonitor$ServiceProviderDisplayInfo implements SIMCardEfTask {
   public int[] _spdiList;
   private byte[] _buffer;
   private final SystemMonitor this$0;
   private static final int STATE_GEN_FAILURE;
   private static final int STATE_SUCCESS;

   public SystemMonitor$ServiceProviderDisplayInfo(SystemMonitor _1) {
      this.this$0 = _1;
      this._spdiList = new int[0];
   }

   @Override
   public final void doWork(SIMCardEfHandler efHandler) {
      int result = efHandler.infoRequest(84);
      if (result == 0) {
         this._buffer = new byte[this.this$0._efHandler.getFileSize()];
         int code = efHandler.readRequest(0, this._buffer);
         if (code == 0) {
            DataBuffer db = (DataBuffer)(new Object(this._buffer, 0, this._buffer.length, true));
            int length = this._buffer.length;
            int plmnIndex = 0;

            label93:
            try {
               if (length < 4 || db.readUnsignedByte() != 163) {
                  return;
               }

               int tlvLen = (byte)db.readUnsignedByte();
               if (db.readUnsignedByte() != 128 || tlvLen > this._buffer.length - 2) {
                  return;
               }

               int var13 = (byte)db.readUnsignedByte();
               if (var13 == 0 || var13 > this._buffer.length - 4 || var13 % 3 != 0) {
                  return;
               }

               length = var13 / 3;
               Array.resize(this._spdiList, length);

               for (int i = 0; i < length; i++) {
                  int netId = this.parseNetworkId(db);
                  if (netId != -1) {
                     this._spdiList[plmnIndex++] = netId;
                  }
               }
            } finally {
               break label93;
            }

            Array.resize(this._spdiList, plmnIndex);
         }
      }
   }

   private final int parseNetworkId(DataBuffer db) {
      int byte1 = db.readUnsignedByte();
      int byte2 = db.readUnsignedByte();
      int byte3 = db.readUnsignedByte();
      if ((byte1 & byte2 & byte3) == 255) {
         return -1;
      }

      int mcc = (byte1 & 15) << 8;
      mcc |= byte1 & 240;
      mcc |= byte2 & 15;
      int mnc;
      if ((byte2 & 240) == 240) {
         mnc = (byte3 & 15) << 4;
         mnc |= (byte3 & 240) >> 4;
         if (SIMCard.is3DigitMNC(mcc, mnc)) {
            mnc <<= 4;
         }
      } else {
         mnc = (byte3 & 15) << 8;
         mnc |= byte3 & 240;
         mnc |= (byte2 & 240) >> 4;
      }

      return mnc << 16 | mcc;
   }
}
