package net.rim.device.apps.internal.supl;

final class Uncertainty {
   private byte uncertSemiMajor;
   private byte uncertSemiMinor;
   private short orientationMajorAxis;
   private byte optionals;
   static final byte UNCERT_OPT_ORIENTATION_MAJ_AXIS = 1;
   static final byte NUM_OPT_ELEMENTS = 1;
   static final byte UNCERT_SEMI_MAJ_BIT_SIZE = 7;
   static final byte UNCERT_SEMI_MIN_BIT_SIZE = 7;
   static final byte ORIENTATION_MAJ_AXIS_BIT_SIZE = 8;

   final void decode(Nibbler nib) {
      this.optionals = (byte)nib.getBitsLarge(1);
      this.uncertSemiMajor = (byte)nib.getBitsLarge(7);
      this.uncertSemiMinor = (byte)nib.getBitsLarge(7);
      if ((this.optionals & 1) == 1) {
         this.orientationMajorAxis = (short)nib.getBitsLarge(8);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.optionals, 1);
      stuff.putBits(this.uncertSemiMajor, 7);
      stuff.putBits(this.uncertSemiMinor, 7);
      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.orientationMajorAxis, 8);
      }
   }

   final void print() {
      System.out.println("Uncertainty: ");
      System.out.println("Optionals: " + this.optionals);
      System.out.println("UncertSemiMajor: " + this.uncertSemiMajor);
      System.out.println("UncertSemiMinor: " + this.uncertSemiMinor);
      if ((this.optionals & 1) == 1) {
         System.out.println("Orientation Maj Axis: " + this.orientationMajorAxis);
      }
   }
}
