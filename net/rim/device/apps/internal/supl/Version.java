package net.rim.device.apps.internal.supl;

final class Version {
   byte major = 1;
   private byte minor = 0;
   private byte servind = 0;
   static final byte VERSION_ELEMENT_BIT_SIZE = 8;
   static final byte VER_SUPPORTED_MAJ = 1;
   static final byte VER_SUPPORTED_MIN = 0;
   static final byte VER_SUPPORTED_SERVIND = 0;

   final void decode(Nibbler nib) {
      this.major = nib.getByte();
      this.minor = nib.getByte();
      this.servind = nib.getByte();
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.major, 8);
      stuff.putBits(this.minor, 8);
      stuff.putBits(this.servind, 8);
   }

   final void print() {
      System.out.println("Version: " + this.major + "." + this.minor + "." + this.servind);
   }
}
