package net.rim.device.apps.internal.supl;

final class VelocityFactory {
   static final byte VELOCITY_ALT_HORVEL;
   static final byte VELOCITY_ALT_HORANDVERVEL;
   static final byte VELOCITY_ALT_HORVELUNCERT;
   static final byte VELOCITY_ALT_HORANDVERUNCERT;
   static final byte VELOCITY_CHOICE_BIT_SIZE;

   static final Velocity DecodeChoiceIndex(Nibbler nib) {
      Velocity velocity = null;
      nib.getBit();
      switch (nib.getBitsLarge(2)) {
         case 0:
         default:
            return new HorVel();
         case 1:
            return new HorAndVerVel();
         case 2:
            return new HorVelUncert();
         case 3:
            velocity = new HorAndVerUncert();
         case -1:
            return velocity;
      }
   }
}
