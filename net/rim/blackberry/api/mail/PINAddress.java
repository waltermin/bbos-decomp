package net.rim.blackberry.api.mail;

import net.rim.device.api.ui.text.HexadecimalTextFilter;
import net.rim.device.api.util.AbstractStringWrapper;

public class PINAddress extends Address {
   private static HexadecimalTextFilter _filter = new HexadecimalTextFilter();

   public PINAddress(String addr, String name) throws AddressException {
      addr = addr.trim().toUpperCase();
      if (addr.length() > 8) {
         throw new AddressException(addr + " is an invalid PIN address");
      }

      AbstractStringWrapper asw = AbstractStringWrapper.createInstance(addr);
      if (!_filter.validate(asw)) {
         throw new AddressException(addr + " is an invalid PIN address");
      }

      super._type = Address.EMAIL_ADDR;
      super._addr = addr;
      super._name = name;
   }
}
