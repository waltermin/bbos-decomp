package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public class ShowMessageAppVerb extends Verb {
   protected ShowMessageAppVerb(int ordering) {
   }

   protected ShowMessageAppVerb(int ordering, ResourceBundleFamily rb, int rbKey) {
   }

   public Object doInvoke(Object _1) {
      throw null;
   }

   @Override
   public Object invoke(Object parameter) {
      if (!ContextObject.getFlag(parameter, 20) && !ContextObject.getFlag(parameter, 61)) {
         return this.doInvoke(parameter);
      }

      ShowMessageApp.invokeVerb(this, parameter);
      return null;
   }
}
