package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;

final class IMPlusPhoneComposeVerbCombiner implements VerbCombiner {
   @Override
   public final boolean recognize(Object object) {
      return !(object instanceof Object) ? false : ((Verb)object).getVerbGroupId() == 1187214;
   }

   @Override
   public final Verb createWrapperVerb(Verb[] verbs, Verb defaultVerb) {
      String[] descriptions = new Object[verbs.length];
      ContextObject contextObject = (ContextObject)(new Object(51, 42, 34));

      for (int i = verbs.length - 1; i >= 0; i--) {
         descriptions[i] = verbs[i].toString(contextObject);
      }

      String verbDescription = null;
      if (verbs[0] != null) {
         contextObject.reset();
         verbDescription = verbs[0].toString(contextObject);
      }

      if (verbDescription == null) {
         verbDescription = IMPlusResources.getString(19);
      }

      return (Verb)(new Object(verbDescription, IMPlusResources.getString(20), verbs[0].getOrdering(), verbs, descriptions, defaultVerb));
   }
}
