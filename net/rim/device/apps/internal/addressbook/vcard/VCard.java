package net.rim.device.apps.internal.addressbook.vcard;

import net.rim.device.api.system.ApplicationRegistry;

public final class VCard {
   public static final void libMain(String[] args) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(9048770516632928843L, new VCardToAddressCardConverter());
      ar.put(-5888220356524146836L, new AddressCardToVCardConverter());
   }
}
