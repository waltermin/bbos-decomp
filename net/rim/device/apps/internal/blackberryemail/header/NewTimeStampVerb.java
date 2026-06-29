package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class NewTimeStampVerb extends Verb {
   public NewTimeStampVerb() {
      super(16860160, EmailResources.getResourceBundle(), 27);
   }

   @Override
   public Object invoke(Object context) {
      return new TimeStampModel(null);
   }
}
