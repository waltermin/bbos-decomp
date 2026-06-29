package net.rim.device.internal.synchronization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

class OTAUpgradeControl$CUSafe {
   private OTAUpgradeControl$CUSafe() {
   }

   static boolean skipField(DataBuffer buff) {
      try {
         ConverterUtilities.skipField(buff);
         return true;
      } finally {
         ;
      }
   }

   static int getType(DataBuffer buff) {
      try {
         return ConverterUtilities.getType(buff);
      } finally {
         ;
      }
   }

   static int readInt(DataBuffer buff) {
      try {
         return ConverterUtilities.readInt(buff);
      } finally {
         ;
      }
   }

   static long readLong(DataBuffer buff) {
      try {
         return ConverterUtilities.readLong(buff);
      } finally {
         return 0;
      }
   }

   static String readString(DataBuffer buff) {
      try {
         return ConverterUtilities.readString(buff);
      } finally {
         ;
      }
   }

   static byte[] readByteArray(DataBuffer buff) {
      try {
         return ConverterUtilities.readByteArray(buff);
      } finally {
         ;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static byte[] readByteStream(DataBuffer buff) {
      ByteArrayOutputStream out = new OTAUpgradeControl$CUSafe$ReservableByteArrayOutputStream(null);
      boolean var7 = false /* VF: Semaphore variable */;
      boolean var10 = false /* VF: Semaphore variable */;

      label42: {
         Object var3;
         try {
            label40:
            try {
               var10 = true;
               var7 = true;
               ConverterUtilities.readByteStream(buff, false, out);
               var7 = false;
               var10 = false;
               break label42;
            } finally {
               if (var10) {
                  var3 = null;
                  var7 = false;
                  break label40;
               }
            }
         } finally {
            if (var7) {
               OTAUpgradeControl.safeClose(out);
            }
         }

         OTAUpgradeControl.safeClose(out);
         return (byte[])var3;
      }

      OTAUpgradeControl.safeClose(out);
      return out.toByteArray();
   }

   static boolean writeInt(DataBuffer buff, int type, int value) {
      try {
         ConverterUtilities.writeInt(buff, type, value);
         return true;
      } finally {
         ;
      }
   }

   static boolean writeLong(DataBuffer buff, int type, long value) {
      try {
         ConverterUtilities.writeLong(buff, type, value);
         return true;
      } finally {
         ;
      }
   }

   static boolean writeString(DataBuffer buff, int type, String value) {
      try {
         ConverterUtilities.writeString(buff, type, value);
         return true;
      } finally {
         ;
      }
   }

   static boolean writeByteArray(DataBuffer buff, int type, byte[] value) {
      try {
         ConverterUtilities.writeByteArray(buff, type, value);
         return true;
      } finally {
         ;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static boolean writeByteStream(DataBuffer buff, int type, byte[] value) {
      ByteArrayInputStream bais = new ByteArrayInputStream(value);
      boolean var9 = false /* VF: Semaphore variable */;
      boolean var12 = false /* VF: Semaphore variable */;

      label41: {
         boolean var5;
         try {
            label39:
            try {
               var12 = true;
               var9 = true;
               ConverterUtilities.writeByteStream(buff, type, bais, value.length);
               var9 = false;
               var12 = false;
               break label41;
            } finally {
               if (var12) {
                  var5 = false;
                  var9 = false;
                  break label39;
               }
            }
         } finally {
            if (var9) {
               OTAUpgradeControl.safeClose(bais);
            }
         }

         OTAUpgradeControl.safeClose(bais);
         return var5;
      }

      OTAUpgradeControl.safeClose(bais);
      return true;
   }
}
