package net.rim.device.apps.internal.supl;

final class SuplPos extends UlpMessage {
   private boolean velocityPresent = false;
   PosPayload posPayload;
   private Velocity velocity;

   SuplPos() {
      this.posPayload = new PosPayload();
   }

   SuplPos(byte[] payload) {
      this.posPayload = new PosPayload(payload);
   }

   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      this.velocityPresent = nib.getBit();
      this.posPayload = new PosPayload();
      this.posPayload.decode(nib);
      if (this.velocityPresent) {
         this.velocity = VelocityFactory.DecodeChoiceIndex(nib);
         if (this.velocity == null) {
            return;
         }

         this.velocity.decode(nib);
      }
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(4, 3);
      stuff.putBit(false);
      stuff.putBit(this.velocityPresent);
      this.posPayload.encode(stuff);
      if (this.velocityPresent) {
         this.velocity.encode(stuff);
      }
   }

   @Override
   final void print() {
      System.out.println("SUPL POS: ");
      this.posPayload.print();
      if (this.velocityPresent) {
         this.velocity.print();
      }
   }
}
