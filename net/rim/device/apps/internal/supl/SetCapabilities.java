package net.rim.device.apps.internal.supl;

final class SetCapabilities {
   private byte posTechnology = 112;
   private PrefMethod prefMethod = new PrefMethod();
   private byte posProtocol = 2;
   static final byte POS_TECH_SET_ASSISTED;
   static final byte POS_TECH_SET_BASED;
   static final byte POS_TECH_AUTONOMOUS;
   static final byte POS_TECH_NUM_BITS;
   static final byte POS_PROTOCOL_RRLP;
   static final byte POS_PROTOCOL_RRC;
   static final byte POS_PROTOCOL_NUM_BITS;

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
      System.out.println(((StringBuffer)(new Object("Pos Technology: "))).append(this.posTechnology).toString());
      this.prefMethod.print();
      System.out.println(((StringBuffer)(new Object("Pos Protocol: "))).append(this.posProtocol).toString());
   }
}
