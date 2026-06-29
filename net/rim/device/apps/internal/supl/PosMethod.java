package net.rim.device.apps.internal.supl;

final class PosMethod {
   byte method;
   static final byte AGPS_SET_ASSISTED;
   static final byte AGPS_SET_BASED;
   static final byte AGPS_SET_ASSISTED_PREF;
   static final byte AGPS_SET_BASED_PREF;
   static final byte AUTONOMOUS_GPS;
   static final byte AFLT;
   static final byte ECID;
   static final byte EOTD;
   static final byte ETDOA;
   static final byte NO_POSITION;
   static final byte POS_METHOD_BIT_SIZE;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.method = (byte)nib.getBitsLarge(4);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("PosMethod: "))).append(this.method).toString());
   }
}
