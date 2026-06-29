package net.rim.device.apps.internal.supl;

final class PrefMethod {
   private byte method = 1;
   static final byte AGPS_SET_ASSISTED_PREF = 0;
   static final byte AGPS_SET_BASED_PREF = 1;
   static final byte NO_PREF = 2;
   static final byte PREF_METHOD_NUM_BITS = 2;

   final void encode(Stuffer stuff) {
      stuff.putBits(this.method, 2);
   }

   final void print() {
      System.out.println("Pref Method: " + this.method);
   }
}
