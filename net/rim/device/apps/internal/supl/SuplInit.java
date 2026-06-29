package net.rim.device.apps.internal.supl;

final class SuplInit extends UlpMessage {
   PosMethod posMethod;
   Notification notification;
   SlpAddress slpAddress;
   QOP qop;
   SlpMode slpMode;
   Mac mac;
   KeyIdentity keyIdentity;
   byte optionals;
   static final byte SUPL_MASK_INIT_NOTIFICATION = 16;
   static final byte SUPL_MASK_INIT_SLP_ADDRESS = 8;
   static final byte SUPL_MASK_INIT_QOP = 4;
   static final byte SUPL_MASK_INIT_MAC = 2;
   static final byte SUPL_MASK_INIT_KEY_IDENTITY = 1;
   static final byte SUPL_INIT_OPTIONALS_BIT_SIZE = 5;

   @Override
   final void decode(Nibbler nib) {
      nib.getBit();
      this.optionals = (byte)nib.getBitsLarge(5);
      this.posMethod = new PosMethod();
      this.posMethod.decode(nib);
      if (Nibbler.IsBitSet(this.optionals, (byte)16)) {
         this.notification = new Notification();
         this.notification.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)8)) {
         this.slpAddress = SlpAddressFactory.DecodeChoiceIndex(nib);
         if (this.slpAddress != null) {
            this.slpAddress.decode(nib);
         }
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)4)) {
         this.qop = new QOP();
         this.qop.decode(nib);
      }

      this.slpMode = new SlpMode();
      this.slpMode.decode(nib);
      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         this.mac = new Mac();
         this.mac.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         this.keyIdentity = new KeyIdentity();
         this.keyIdentity.decode(nib);
      }
   }

   @Override
   final void encode(Stuffer stuff) {
   }

   @Override
   final void print() {
      System.out.println("SUPL INIT: ");
      System.out.println("Optionals: " + this.optionals);
      this.posMethod.print();
      if ((this.optionals & 16) == 16) {
         this.notification.print();
      }

      if ((this.optionals & 8) == 8) {
         if (!(this.slpAddress instanceof Fqdn)) {
            if (!(this.slpAddress instanceof Ipv4Address)) {
               if (!(this.slpAddress instanceof Ipv6Address)) {
                  System.out.println("Unknown SLP Address type");
               } else {
                  Ipv6Address addr = (Ipv6Address)this.slpAddress;
                  addr.print();
               }
            } else {
               Ipv4Address addr = (Ipv4Address)this.slpAddress;
               addr.print();
            }
         } else {
            Fqdn fqdn = (Fqdn)this.slpAddress;
            fqdn.print();
         }
      }

      if ((this.optionals & 4) == 4) {
         this.qop.print();
      }

      this.slpMode.print();
      if ((this.optionals & 2) == 2) {
         this.mac.print();
      }

      if ((this.optionals & 1) == 1) {
         this.keyIdentity.print();
      }
   }
}
