package net.rim.device.cldc.io.gme;

public final class GMETarget {
   public String address;
   public String redirect;
   public int type;
   public static final int TYPE_UNKNOWN;
   public static final int TYPE_SERVICE;
   public static final int TYPE_PEER;

   public GMETarget() {
      this(null, 1, null);
   }

   public GMETarget(String a) {
      this(a, 1, null);
   }

   public GMETarget(String a, int t) {
      this(a, t, null);
   }

   public GMETarget(String a, int t, String r) {
      this.address = a;
      this.type = t;
      this.redirect = r;
   }
}
