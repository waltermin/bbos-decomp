package net.rim.device.apps.internal.supl;

class Velocity {
   short bearing;
   int horSpeed;
   static final byte VELOCITY_ALT_HORVEL = 0;
   static final byte VELOCITY_ALT_HORANDVERVEL = 1;
   static final byte VELOCITY_ALT_HORVELUNCERT = 2;
   static final byte VELOCITY_ALT_HORANDVERUNCERT = 3;
   static final byte VELOCITY_CHOICE_BIT_SIZE = 2;
   static final byte BEARING_BIT_SIZE = 9;
   static final byte HOR_SPEED_BIT_SIZE = 16;

   void decode(Nibbler _1) {
      throw null;
   }

   void encode(Stuffer _1) {
      throw null;
   }

   void print() {
      throw null;
   }
}
