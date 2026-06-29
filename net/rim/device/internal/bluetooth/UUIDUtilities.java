package net.rim.device.internal.bluetooth;

import net.rim.device.api.util.NumberUtilities;

public final class UUIDUtilities {
   private static final byte[] BASE_UUID = new byte[]{0, 0, 0, 0, 0, 0, 16, 0, -128, 0, 0, -128, 95, -101, 52, -5};

   private UUIDUtilities() {
   }

   public static final String toString(byte[] uuidData) {
      StringBuffer sb = new StringBuffer();
      int length = uuidData.length;

      for (int i = 0; i < length; i++) {
         String s = Integer.toHexString(uuidData[i] & 255);
         if (s.length() == 1) {
            sb.append('0');
         }

         sb.append(s);
      }

      return sb.toString();
   }

   public static final byte[] toBytes(String uuid) {
      int length = uuid.length();
      if (length > 32) {
         throw new IllegalArgumentException();
      }

      byte[] data;
      if (length == 32) {
         data = new byte[16];
      } else {
         if (length == 8 && uuid.startsWith("0000")) {
            uuid = uuid.substring(4);
            length -= 4;
         }

         if (length > 4) {
            data = new byte[4];
         } else {
            data = new byte[2];
         }
      }

      int i = data.length - 1;
      if (length <= 2) {
         data[i] = (byte)(NumberUtilities.parseInt(uuid, 0, length, 16) & 0xFF);
         return data;
      }

      for (int stringIndex = length - 2; stringIndex >= 0; stringIndex -= 2) {
         data[i--] = (byte)(NumberUtilities.parseInt(uuid, stringIndex, stringIndex + 2, 16) & 0xFF);
      }

      return data;
   }

   public static final byte[] promoteTo128Bits(String uuid) {
      byte[] data = toBytes(uuid);
      int length = data.length;
      if (length == 16) {
         return data;
      }

      byte[] newData = new byte[16];
      System.arraycopy(BASE_UUID, 0, newData, 0, BASE_UUID.length);
      System.arraycopy(data, 0, newData, 4 - length, length);
      return newData;
   }

   public static final byte[] serialize(String uuid) {
      return serialize(toBytes(uuid));
   }

   private static final byte[] serialize(byte[] data) {
      byte[] serializedData = new byte[data.length + 1];
      System.arraycopy(data, 0, serializedData, 1, data.length);
      serializedData[0] = 24;
      switch (data.length) {
         case 2:
            serializedData[0] = (byte)(serializedData[0] | 1);
            return serializedData;
         case 4:
            serializedData[0] = (byte)(serializedData[0] | 2);
            return serializedData;
         case 16:
            serializedData[0] = (byte)(serializedData[0] | 4);
         default:
            return serializedData;
      }
   }
}
