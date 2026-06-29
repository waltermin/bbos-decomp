package net.rim.device.apps.internal.supl;

import net.rim.vm.Array;

public final class Ulp {
   private short length;
   Version version;
   SessionId sessionId;
   UlpMessage ulpMessage;
   static final byte SUPL_ULP_LENGTH_BIT_SIZE = 16;
   static final int ULP_MAX_PDU_SIZE = 65535;

   Ulp() {
   }

   Ulp(UlpMessage msg, SessionId sessionId) {
      this.version = new Version();
      this.sessionId = sessionId;
      this.ulpMessage = msg;
   }

   final byte[] encode() {
      byte[] pdu = new byte[65535];
      Stuffer stuff = new Stuffer(pdu, 65535);
      stuff.putBits(this.length, 16);
      this.version.encode(stuff);
      this.sessionId.encode(stuff);
      this.ulpMessage.encode(stuff);
      this.length = (short)(stuff.bitsWrittenCount() + 7 >> 3);
      pdu[0] = (byte)(this.length >> 8);
      pdu[1] = (byte)(this.length & 0xFF);
      Array.resize(pdu, this.length);
      return pdu;
   }

   final void decode(byte[] pdu) {
      Nibbler nib = new Nibbler();
      nib.fillBitString(pdu, pdu.length);
      this.length = (short)nib.getBitsLarge(16);
      this.version = new Version();
      this.version.decode(nib);
      this.sessionId = new SessionId();
      this.sessionId.decode(nib);
      this.ulpMessage = UlpMessageFactory.DecodeChoiceIndex(nib);
      if (this.ulpMessage != null) {
         this.ulpMessage.decode(nib);
      }
   }

   final void print() {
      System.out.println("ULP Message");
      System.out.println("Length: " + this.length);
      this.version.print();
      this.sessionId.print();
      this.ulpMessage.print();
   }
}
