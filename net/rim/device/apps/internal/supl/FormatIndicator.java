package net.rim.device.apps.internal.supl;

final class FormatIndicator {
   private byte indicator;
   static final byte LOGICAL_NAME;
   static final byte EMAIL_ADDRESS;
   static final byte MSISDN;
   static final byte URL;
   static final byte SIP_URL;
   static final byte MIN;
   static final byte MDN;
   static final byte IMS_PUBLIC_IDENTITY;
   static final byte FORMAT_INDICATOR_BIT_SIZE;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.indicator = (byte)nib.getBitsLarge(3);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("FormatInd: "))).append(this.indicator).toString());
   }
}
