package net.rim.device.apps.internal.supl;

final class UtcTime {
   private String utcTimeString;
   static final byte UTC_LENGTH = 13;
   static final byte LENGTH_BIT_SIZE = 8;
   static final byte UTCTIME_CHAR_BIT_SIZE = 7;

   final void encode(Stuffer stuff) {
   }

   final void decode(Nibbler nib) {
      int length = nib.getBitsLarge(8);
      StringBuffer strBuf = (StringBuffer)(new Object());

      for (int i = 0; i < length; i++) {
         strBuf.append((char)(0xFF & nib.getBitsLarge(7)));
      }

      this.utcTimeString = strBuf.toString();
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("Utc Time: "))).append(this.utcTimeString).toString());
   }
}
