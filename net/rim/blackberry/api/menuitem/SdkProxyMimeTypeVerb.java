package net.rim.blackberry.api.menuitem;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.MIMETypeProvider;
import net.rim.device.api.system.ApplicationDescriptor;

class SdkProxyMimeTypeVerb extends SdkProxyContextVerb implements MIMETypeProvider {
   SdkProxyMimeTypeVerb(ApplicationMenuItem ami, long mii, ApplicationDescriptor application, String context) {
      super(ami, mii, application, context);
      super._context = MIMETypeAssociations.getNormalizedType(context);
   }

   @Override
   public String getMIMEType() {
      return (String)super._context;
   }
}
