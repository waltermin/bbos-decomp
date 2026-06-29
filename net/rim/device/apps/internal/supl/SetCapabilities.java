package net.rim.device.apps.internal.supl;

final class SetCapabilities {
   private byte posTechnology = 112;
   private PrefMethod prefMethod = new PrefMethod();
   private byte posProtocol = 2;
   static final byte POS_TECH_SET_ASSISTED = 64;
   static final byte POS_TECH_SET_BASED = 32;
   static final byte POS_TECH_AUTONOMOUS = 16;
   static final byte POS_TECH_NUM_BITS = 7;
   static final byte POS_PROTOCOL_RRLP = 2;
   static final byte POS_PROTOCOL_RRC = 1;
   static final byte POS_PROTOCOL_NUM_BITS = 3;

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBit(false);
      stuff.putBits(this.posTechnology, 7);
      this.prefMethod.encode(stuff);
      stuff.putBit(false);
      stuff.putBits(this.posProtocol, 3);
   }

   final void print() {
      System.out.println("Set Capabilities: ");
      System.out.println("Pos Technology: " + this.posTechnology);
      this.prefMethod.print();
      System.out.println("Pos Protocol: " + this.posProtocol);
   }
}
