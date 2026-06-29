package net.rim.device.apps.internal.options;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.vm.Array;

public final class CustomDictUnitModel implements RIMModel, VerbProvider {
   private Object _entry;

   public CustomDictUnitModel(Object entry, int screenType, CustomDictionary customDictionary) {
      this._entry = entry;
   }

   public final Object getEntry() {
      return this._entry;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (ContextObject.getFlag(context, 3) && ContextObject.getFlag(context, 5)) {
         Array.resize(verbs, 2);
         if (ContextObject.getFlag(context, 85)) {
            verbs[0] = new DeleteCustomDictUnitVerb(this);
            return verbs[0];
         } else {
            verbs[0] = new EditCustomDictUnitVerb(this);
            verbs[1] = new DeleteCustomDictUnitVerb(this);
            return ContextObject.getFlag(context, 2) ? verbs[0] : null;
         }
      } else {
         Array.resize(verbs, 0);
         return null;
      }
   }
}
