package net.rim.device.apps.internal.supl;

final class SuplStart extends UlpMessage {
   private SetCapabilities setCapabilities;
   private LocationId locationId;
   private QOP qop;
   private byte optionals;
   static final byte SUPL_START_OPT_QOP = 1;
   static final byte NUM_OPT_ELEMENTS = 1;

   SuplStart() {
      this.setCapabilities = new SetCapabilities();
      this.locationId = new LocationId();
      this.optionals = 0;
   }

   SuplStart(SetCapabilities capabilities) {
      this.setCapabilities = capabilities;
      this.locationId = new LocationId();
      this.optionals = 0;
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(1, 3);
      stuff.putBit(false);
      stuff.putBits(this.optionals, 1);
      this.setCapabilities.encode(stuff);
      this.locationId.encode(stuff);
      if ((this.optionals & 1) == 1) {
         this.qop.encode(stuff);
      }
   }

   @Override
   final void decode(Nibbler nib) {
   }

   @Override
   final void print() {
      System.out.println("SUPL START:");
      System.out.println("Optionals: " + this.optionals);
      this.setCapabilities.print();
      this.locationId.print();
      if ((this.optionals & 1) == 1) {
         this.qop.print();
      }
   }
}
