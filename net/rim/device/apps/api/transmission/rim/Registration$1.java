package net.rim.device.apps.api.transmission.rim;

import java.util.Vector;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.service.DefaultServicesOptionsItem;
import net.rim.device.apps.api.transmission.rim.options.HostRoutingTableOptionsItem;
import net.rim.device.apps.api.transmission.rim.options.ServiceBookOptionsItem;

class Registration$1 implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public Vector getOptionsItems() {
      Vector v = (Vector)(new Object(3));
      if (RadioInfo.getNetworkType() != 6) {
         v.addElement(new HostRoutingTableOptionsItem());
      }

      v.addElement(new DefaultServicesOptionsItem());
      v.addElement(new ServiceBookOptionsItem());
      return v;
   }
}
