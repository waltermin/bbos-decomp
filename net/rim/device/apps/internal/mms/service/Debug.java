package net.rim.device.apps.internal.mms.service;

final class Debug {
   private static final int MAX_DUMP_LENGTH;
   private static final int BYTES_PER_ROW;

   static final void dumpBytes(byte[] data) {
      StringBuffer bufA = (StringBuffer)(new Object());
      StringBuffer bufB = (StringBuffer)(new Object());
      String SPACES = "   ";
      String COLON_SPACE = ": ";
      int length = Math.min(data.length, 2048);
      int rows = (length + 12 - 1) / 12;

      for (int row = 0; row < rows; row++) {
         bufA.setLength(0);
         bufB.setLength(0);
         int start = row * 12;

         for (int idx = 0; idx < 12; idx++) {
            int index = start + idx;
            if (index < length) {
               bufA.append(hex(data[index]));
               bufA.append(' ');
               char ch = (char)data[index];
               if (ch < ' ') {
                  bufB.append('.');
               } else {
                  bufB.append(ch);
               }
            } else {
               bufA.append(SPACES);
            }
         }

         print(
            ((StringBuffer)(new Object())).append(hex(start, 6)).append(COLON_SPACE).append(bufA.toString()).append(SPACES).append(bufB.toString()).toString()
         );
      }
   }

   private static final String hex(byte b) {
      return hex(b & 255, 2);
   }

   private static final String hex(int value, int digitCount) {
      String str = Integer.toHexString(value);

      while (str.length() < digitCount) {
         str = ((StringBuffer)(new Object())).append('0').append(str).toString();
      }

      return str;
   }

   private static final void print(String msg) {
      System.out.println(msg);
   }
}
