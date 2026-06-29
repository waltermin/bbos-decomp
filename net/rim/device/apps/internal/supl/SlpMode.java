package net.rim.device.apps.internal.supl;

final class SlpMode {
   boolean mode;
   static final boolean PROXY;
   static final boolean NON_PROXY;

   final void decode(Nibbler nib) {
      this.mode = nib.getBit();
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("SlpMode: "))).append(this.mode ? "Non-Proxy" : "Proxy").toString());
   }
}
