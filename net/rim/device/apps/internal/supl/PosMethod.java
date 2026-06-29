package net.rim.device.apps.internal.supl;

final class PosMethod {
   byte method;
   static final byte AGPS_SET_ASSISTED = 0;
   static final byte AGPS_SET_BASED = 1;
   static final byte AGPS_SET_ASSISTED_PREF = 2;
   static final byte AGPS_SET_BASED_PREF = 3;
   static final byte AUTONOMOUS_GPS = 4;
   static final byte AFLT = 5;
   static final byte ECID = 6;
   static final byte EOTD = 7;
   static final byte ETDOA = 8;
   static final byte NO_POSITION = 9;
   static final byte POS_METHOD_BIT_SIZE = 4;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.method = (byte)nib.getBitsLarge(4);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("PosMethod: "))).append(this.method).toString());
   }
}
