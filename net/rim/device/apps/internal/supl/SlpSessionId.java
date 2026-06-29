package net.rim.device.apps.internal.supl;

final class SlpSessionId {
   int sessionId;
   SlpAddress slpAddress;
   static final byte SLP_SESSION_ID_BIT_SIZE = 32;

   final void decode(Nibbler nib) {
      this.sessionId = nib.getBitsLarge(32);
      this.slpAddress = SlpAddressFactory.DecodeChoiceIndex(nib);
      if (this.slpAddress != null) {
         this.slpAddress.decode(nib);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.sessionId, 32);
      stuff.putBit(false);
      if (this.slpAddress instanceof IpAddress) {
         stuff.putBits(0, 1);
      }

      this.slpAddress.encode(stuff);
   }

   final boolean equals(SlpSessionId slpSessionId) {
      boolean retVal = true;
      if (this.sessionId == slpSessionId.sessionId) {
         retVal = true;
      } else {
         retVal = false;
      }

      if (retVal) {
         if (this.slpAddress instanceof Fqdn) {
            Fqdn fqdn = (Fqdn)this.slpAddress;
            return fqdn.equals(slpSessionId.slpAddress);
         }

         if (this.slpAddress instanceof Ipv4Address) {
            Ipv4Address addr = (Ipv4Address)this.slpAddress;
            return addr.equals(slpSessionId.slpAddress);
         }

         if (this.slpAddress instanceof Ipv6Address) {
            Ipv6Address addr = (Ipv6Address)this.slpAddress;
            return addr.equals(slpSessionId.slpAddress);
         }

         System.out.println("Unknown SlpAddress");
         retVal = false;
      }

      return retVal;
   }

   final void print() {
      System.out.println("SLP Session ID: ");
      System.out.println("SessionId: " + this.sessionId);
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
}
