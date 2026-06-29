package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class WebPageAddressModel$FollowLinkVerb extends Verb {
   private String _address;

   public WebPageAddressModel$FollowLinkVerb(String address) {
      super(341248);
      this._address = address;
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1727);
   }

   @Override
   public final Object invoke(Object param) {
      BrowserServices.loadUrl(this._address);
      return new ContextObject(39);
   }
}
