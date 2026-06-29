package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class PosPayload {
   byte[] payload;
   static final boolean DEBUG_DUMP_RAW_PAYLOAD = true;
   static final byte POS_PAYLOAD_ALT_TIA802 = 0;
   static final byte POS_PAYLOAD_ALT_RRC = 1;
   static final byte POS_PAYLOAD_ALT_RRLP = 2;
   static final byte POS_PAYLOAD_CHOICE_BIT_SIZE = 2;
   static final byte POS_PAYLOAD_MIN_LENGTH = 1;
   static final int POS_PAYLOAD_MAX_LENGTH = 8192;
   static final byte POS_PAYLOAD_LENGTH_BIT_SIZE = 13;

   PosPayload() {
   }

   PosPayload(byte[] data) {
      this.payload = new byte[data.length];
      this.payload = Arrays.copy(data);
   }

   final void decode(Nibbler nib) {
      nib.getBit();
      byte payloadType = (byte)nib.getBitsLarge(2);
      switch (payloadType) {
         case 2:
            int length = nib.getBitsLarge(13) + 1;
            this.payload = new byte[length];

            for (int i = 0; i < length; i++) {
               this.payload[i] = nib.getByte();
            }
      }
   }

   final void encode(Stuffer stuff) {
      int length = this.payload.length;
      stuff.putBit(false);
      stuff.putBits(2, 2);
      stuff.putBits(length - 1, 13);

      for (int i = 0; i < length; i++) {
         stuff.putBits(0xFF & this.payload[i], 8);
      }
   }

   final void print() {
      int length = this.payload.length;
      System.out.println("Pos Payload: ");
      System.out.println(((StringBuffer)(new Object("Length: "))).append(length).toString());
      System.out.println("Payload: ");

      for (int i = 0; i < length; i++) {
         System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.payload[i])).append(" ").toString());
      }

      System.out.print("\n");
   }
}
