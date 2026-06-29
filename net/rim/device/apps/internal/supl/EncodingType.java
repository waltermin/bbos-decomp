package net.rim.device.apps.internal.supl;

final class EncodingType {
   byte type;
   static final byte UCS2 = 0;
   static final byte GSM_DEFAULT = 1;
   static final byte UTF8 = 2;
   static final byte ENCODING_TYPE_BIT_SIZE = 2;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.type = (byte)nib.getBitsLarge(2);
   }

   final void print() {
      System.out.println("Encoding Type: " + this.type);
   }
}
