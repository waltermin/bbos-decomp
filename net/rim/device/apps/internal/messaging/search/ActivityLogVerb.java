package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class ActivityLogVerb extends Verb implements SetParameter {
   private String _nameString;

   public ActivityLogVerb() {
      super(611616);
   }

   @Override
   public final String toString() {
      return SearchResources.getString(70);
   }

   @Override
   public final void setParameter(Object object) {
      if (object instanceof String) {
         this._nameString = (String)object;
      }
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject contextObj = ContextObject.clone(context);
      if (this._nameString != null) {
         ContextObject.put(contextObj, 253, this._nameString);
         boolean isForeground = ShowMessageApp.isMessagingAppForeground();
         if (!isForeground) {
            contextObj.setFlag(96);
         } else {
            contextObj.clearFlag(96);
         }

         ShowMessageApp.invokeVerb(new ActivityLogVerb$ActivityLogSearchVerb(), contextObj);
      }

      return null;
   }
}
