package net.rim.device.apps.internal.supl;

final class PrefMethod {
   private byte method = 1;
   static final byte AGPS_SET_ASSISTED_PREF;
   static final byte AGPS_SET_BASED_PREF;
   static final byte NO_PREF;
   static final byte PREF_METHOD_NUM_BITS;

   final void encode(Stuffer stuff) {
      stuff.putBits(this.method, 2);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("Pref Method: "))).append(this.method).toString());
   }
}
