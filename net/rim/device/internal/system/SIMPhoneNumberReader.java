package net.rim.device.internal.system;

import net.rim.device.api.system.SIMCard;

public final class SIMPhoneNumberReader implements SIMCardEfTask {
   SIMPhoneNumberReader$PhoneNumberList _list;
   int _recNum;
   int _efID;
   static final byte INTERNATIONAL_CONST = -112;
   static final byte UNKNOWN_CONST = -128;
   static StringBuffer _tempNumber = new StringBuffer();

   public SIMPhoneNumberReader(SIMPhoneNumberReader$PhoneNumberList list, int efID, int recNum) {
      if (list == null) {
         throw new NullPointerException();
      }

      this._list = list;
      this._efID = efID;
      this._recNum = recNum;
   }

   @Override
   public final void doWork(SIMCardEfHandler efHandler) {
      int res = efHandler.infoRequest(this._efID);
      if (res == 0) {
         int lastRecNum = efHandler.getNumRecords();
         if (lastRecNum > 0 && this._recNum <= lastRecNum) {
            int recLen = efHandler.getRecordLength();
            byte[] buffer = new byte[recLen];
            int startRecNum = 1;
            if (this._recNum > 0) {
               startRecNum = this._recNum;
               lastRecNum = this._recNum;
            }

            for (int recNum = startRecNum; recNum <= lastRecNum; recNum++) {
               res = efHandler.readRequest(recNum, buffer);
               if (res != 0) {
                  return;
               }

               this.readFromBuffer(recNum, recLen, buffer);
            }
         }
      }
   }

   private final void readFromBuffer(int recNum, int size, byte[] buffer) {
      String name = null;
      String number = null;
      if (size > 14) {
         name = SIMCard.decodeAlphaId(buffer, 0, size - 14);
      }

      if (name != null) {
         name = name.trim();
      }

      _tempNumber.setLength(0);
      int len = buffer[size - 14];
      if (len > 1) {
         len--;
         byte tonAndNpi = buffer[size - 14 + 1];
         int start = size - 14 + 2;
         readBCDNumber(_tempNumber, buffer, start, start + len);
         number = _tempNumber.toString();
         byte extRecord = buffer[size - 14 + 13];
         this._list.addPhoneNumber(recNum, name, number, extRecord, tonAndNpi);
      }
   }

   public static final void readBCDNumber(StringBuffer number, byte[] buffer, int start, int endplus1) {
      for (int i = start; i < endplus1; i++) {
         byte b = buffer[i];
         if (b == -1) {
            return;
         }

         int dig2 = (b & 240) >> 4;
         int dig1 = b & 15;
         handleDigit(number, dig1);
         handleDigit(number, dig2);
      }
   }

   private static final void handleDigit(StringBuffer number, int dig) {
      if (dig < 10) {
         char c = (char)(48 + dig);
         number.append(c);
      } else if (dig == 10) {
         number.append('*');
      } else if (dig == 11) {
         number.append('#');
      } else if (dig == 12) {
         number.append(',');
      } else {
         if (dig == 13) {
            number.append('?');
         }
      }
   }
}
