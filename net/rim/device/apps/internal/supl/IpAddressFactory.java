package net.rim.device.apps.internal.supl;

final class IpAddressFactory {
   public static final IpAddress DecodeChoiceIndex(Nibbler nib) {
      switch (nib.getBitsLarge(1)) {
         case -1:
            return null;
         case 0:
         default:
            return new Ipv4Address();
         case 1:
            return new Ipv6Address();
      }
   }
}
