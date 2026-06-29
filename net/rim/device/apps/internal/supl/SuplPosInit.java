package net.rim.device.apps.internal.supl;

final class SuplPosInit extends UlpMessage {
   private SetCapabilities setCapabilities;
   private RequestedAssistData reqAssistData;
   private LocationId locationId;
   private Position position;
   private char[] ver = new char[8];
   private byte optionals;
   static final byte POS_INIT_OPT_REQ_ASSIST_DATA = 8;
   static final byte POS_INIT_OPT_POSITION = 4;
   static final byte POS_INIT_OPT_VER = 1;
   static final byte NUM_OPT_ELEMENTS = 4;
   static final byte OCTET_BIT_SIZE = 8;
   static final byte MAX_VER_SIZE = 8;

   SuplPosInit() {
      this.optionals = 0;
      this.setCapabilities = new SetCapabilities();
      this.locationId = new LocationId();
   }

   SuplPosInit(PosMethod posMethod, Position storedPosition, byte[] ver, SetCapabilities setCap) {
      this.reqAssistData = new RequestedAssistData();
      if (this.reqAssistData.isReqAssistElementsPresent()) {
         this.optionals = 8;
      }

      if (storedPosition != null) {
         this.optionals = (byte)(this.optionals | 4);
         this.position = storedPosition;
      }

      this.setCapabilities = setCap;
      this.locationId = new LocationId();
      if (ver != null) {
         this.optionals = (byte)(this.optionals | 1);

         for (int i = 0; i < 8; i++) {
            this.ver[i] = (char)(0xFF & (char)ver[i]);
         }
      }
   }

   @Override
   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(3, 3);
      stuff.putBit(false);
      stuff.putBits(this.optionals, 4);
      this.setCapabilities.encode(stuff);
      if ((this.optionals & 8) == 8) {
         this.reqAssistData.encode(stuff);
      }

      this.locationId.encode(stuff);
      if ((this.optionals & 4) == 4) {
         this.position.encode(stuff);
      }

      if ((this.optionals & 1) == 1) {
         for (byte i = 0; i < 8; i++) {
            stuff.putBits(0xFF & this.ver[i], 8);
         }
      }
   }

   @Override
   final void decode(Nibbler nib) {
   }

   @Override
   final void print() {
      System.out.println("SUPL POS INIT: ");
      System.out.println("Optionals: " + this.optionals);
      this.setCapabilities.print();
      if ((this.optionals & 8) == 8) {
         this.reqAssistData.print();
      }

      this.locationId.print();
      if ((this.optionals & 4) == 4) {
         this.position.print();
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Ver: ");

         for (int i = 0; i < 8; i++) {
            System.out.print(Integer.toHexString(255 & this.ver[i]) + " ");
         }

         System.out.print("\n");
      }
   }
}
