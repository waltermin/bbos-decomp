package net.rim.device.apps.internal.supl;

final class Uncertainty {
   private byte uncertSemiMajor;
   private byte uncertSemiMinor;
   private short orientationMajorAxis;
   private byte optionals;
   static final byte UNCERT_OPT_ORIENTATION_MAJ_AXIS;
   static final byte NUM_OPT_ELEMENTS;
   static final byte UNCERT_SEMI_MAJ_BIT_SIZE;
   static final byte UNCERT_SEMI_MIN_BIT_SIZE;
   static final byte ORIENTATION_MAJ_AXIS_BIT_SIZE;

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
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      System.out.println(((StringBuffer)(new Object("UncertSemiMajor: "))).append(this.uncertSemiMajor).toString());
      System.out.println(((StringBuffer)(new Object("UncertSemiMinor: "))).append(this.uncertSemiMinor).toString());
      if ((this.optionals & 1) == 1) {
         System.out.println(((StringBuffer)(new Object("Orientation Maj Axis: "))).append(this.orientationMajorAxis).toString());
      }
   }
}
