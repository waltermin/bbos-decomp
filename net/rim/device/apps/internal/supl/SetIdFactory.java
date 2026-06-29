package net.rim.device.apps.internal.supl;

final class SetIdFactory {
   static final SetId DecodeChoiceIndex(Nibbler nib) {
      nib.getBit();
      byte choice = (byte)nib.getBitsLarge(3);
      switch (choice) {
         case -1:
         case 4:
            return null;
         case 0:
         default:
            return new Msisdn();
         case 1:
            return new Mdn();
         case 2:
            return new Min();
         case 3:
            return new Imsi();
         case 5:
            return IpAddressFactory.DecodeChoiceIndex(nib);
      }
   }
}
