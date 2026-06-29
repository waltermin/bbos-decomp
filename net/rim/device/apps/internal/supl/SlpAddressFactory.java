package net.rim.device.apps.internal.supl;

final class SlpAddressFactory {
   static final SlpAddress DecodeChoiceIndex(Nibbler nib) {
      nib.getBit();
      SlpAddress slpAddress = null;
      switch (nib.getBitsLarge(1)) {
         case 0:
         default:
            return IpAddressFactory.DecodeChoiceIndex(nib);
         case 1:
            slpAddress = new Fqdn();
         case -1:
            return slpAddress;
      }
   }
}
