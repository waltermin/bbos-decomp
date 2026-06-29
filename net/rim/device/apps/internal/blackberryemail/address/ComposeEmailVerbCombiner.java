package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class ComposeEmailVerbCombiner implements VerbCombiner {
   @Override
   public final boolean recognize(Object object) {
      return !(object instanceof Verb) ? false : ((Verb)object).getVerbGroupId() == 15556151;
   }

   @Override
   public final Verb createWrapperVerb(Verb[] verbs, Verb defaultVerb) {
      String[] descriptions = new String[verbs.length];
      ContextObject contextObject = new ContextObject(63);

      for (int i = verbs.length - 1; i >= 0; i--) {
         Verb verb = verbs[i];
         descriptions[i] = verb.toString(contextObject);
      }

      String verbDescription = null;
      if (verbs[0] != null) {
         contextObject.reset();
         verbDescription = verbs[0].toString(contextObject);
      }

      if (verbDescription == null) {
         verbDescription = EmailResources.getString(43);
      }

      return new PopupVerbWrapper(verbDescription, EmailResources.getString(94), verbs[0].getOrdering(), verbs, descriptions, defaultVerb, true);
   }
}
