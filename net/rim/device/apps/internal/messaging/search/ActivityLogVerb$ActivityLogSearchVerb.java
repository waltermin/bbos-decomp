package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageAppVerb;
import net.rim.device.apps.api.messaging.search.MessageSearch;

class ActivityLogVerb$ActivityLogSearchVerb extends ShowMessageAppVerb {
   public ActivityLogVerb$ActivityLogSearchVerb() {
      super(0);
   }

   @Override
   public Object doInvoke(Object context) {
      String name = (String)ContextObject.get(context, 253);
      boolean returnToBackground = ContextObject.getFlag(context, 96);
      MessageSearch.getInstance().nameSearch(name, false, context, returnToBackground);
      return null;
   }
}
