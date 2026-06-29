package net.rim.blackberry.api.menuitem;

import net.rim.device.api.system.ApplicationDescriptor;

class SdkProxyContextVerb extends SdkProxyVerb {
   protected Object _context;

   SdkProxyContextVerb(ApplicationMenuItem ami, long mii, ApplicationDescriptor application, Object context) {
      super(ami, mii, application);
      this._context = context;
   }
}
