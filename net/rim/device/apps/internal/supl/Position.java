package net.rim.device.apps.internal.supl;

final class Position {
   private UtcTime utcTime;
   private PositionEstimate posEst;
   private Velocity velocity;
   private byte optionals;
   static final byte POS_OPT_VEL = 1;
   static final byte NUM_OPT_ELEMENTS = 1;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.optionals = (byte)nib.getBitsLarge(1);
      this.utcTime = new UtcTime();
      this.utcTime.decode(nib);
      this.posEst = new PositionEstimate();
      this.posEst.decode(nib);
      if ((this.optionals & 1) == 1) {
         this.velocity = VelocityFactory.DecodeChoiceIndex(nib);
         if (this.velocity == null) {
            return;
         }

         this.velocity.decode(nib);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.optionals, 1);
      this.utcTime.encode(stuff);
      this.posEst.encode(stuff);
      if ((this.optionals & 1) == 1) {
         this.velocity.encode(stuff);
      }
   }

   final void print() {
      System.out.println("Position: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      this.utcTime.print();
      this.posEst.print();
      if ((this.optionals & 1) == 1) {
         this.velocity.print();
      }
   }
}
