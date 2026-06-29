package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class MMSComposeVerbCombiner implements VerbCombiner {
   @Override
   public final boolean recognize(Object object) {
      return !(object instanceof Verb) ? false : ((Verb)object).getVerbGroupId() == 12759082;
   }

   @Override
   public final Verb createWrapperVerb(Verb[] verbs, Verb defaultVerb) {
      String[] descriptions = new String[verbs.length];
      ContextObject contextObject = new ContextObject(51, 42, 34);
      contextObject.setFlag(63);

      for (int i = verbs.length - 1; i >= 0; i--) {
         descriptions[i] = verbs[i].toString(contextObject);
      }

      String verbDescription = null;
      if (verbs[0] != null) {
         contextObject.reset();
         verbDescription = verbs[0].toString(contextObject);
      }

      if (verbDescription == null) {
         verbDescription = MMSResources.getString(12);
      }

      return new PopupVerbWrapper(verbDescription, MMSResources.getString(10), this.getPopupOrdering(verbs), verbs, descriptions, defaultVerb);
   }

   private final int getPopupOrdering(Verb[] verbs) {
      int verbsSize = verbs.length;

      for (int i = 0; i < verbsSize; i++) {
         int verbOrdering = verbs[i].getOrdering();
         if (verbOrdering == 328000 || verbOrdering == 1267024 || verbOrdering == 1267040) {
            return verbOrdering;
         }
      }

      return verbs[0].getOrdering();
   }
}
