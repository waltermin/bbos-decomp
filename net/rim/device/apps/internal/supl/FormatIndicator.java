package net.rim.device.apps.internal.supl;

final class FormatIndicator {
   private byte indicator;
   static final byte LOGICAL_NAME = 0;
   static final byte EMAIL_ADDRESS = 1;
   static final byte MSISDN = 2;
   static final byte URL = 3;
   static final byte SIP_URL = 4;
   static final byte MIN = 5;
   static final byte MDN = 6;
   static final byte IMS_PUBLIC_IDENTITY = 7;
   static final byte FORMAT_INDICATOR_BIT_SIZE = 3;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.indicator = (byte)nib.getBitsLarge(3);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("FormatInd: "))).append(this.indicator).toString());
   }
}
