package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

public final class AutoTextUnitModel implements RIMModel, VerbProvider {
   private Object _entry;

   public AutoTextUnitModel(Object entry) {
      this._entry = entry;
   }

   public final Object getEntry() {
      return this._entry;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      } else if (ContextObject.getFlag(context, 3) && ContextObject.getFlag(context, 5)) {
         Array.resize(verbs, 2);
         verbs[0] = new EditAutoTextUnitVerb(this);
         verbs[1] = new DeleteAutoTextUnitVerb(this);
         return ContextObject.getFlag(context, 2) ? verbs[0] : null;
      } else {
         Array.resize(verbs, 0);
         return null;
      }
   }
}
