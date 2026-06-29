package net.rim.device.apps.internal.supl;

final class EncodingType {
   byte type;
   static final byte UCS2;
   static final byte GSM_DEFAULT;
   static final byte UTF8;
   static final byte ENCODING_TYPE_BIT_SIZE;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.type = (byte)nib.getBitsLarge(2);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("Encoding Type: "))).append(this.type).toString());
   }
}
