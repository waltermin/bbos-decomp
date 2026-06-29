package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.sms.resources.SMSResources;

final class SMSComposeVerbCombiner implements VerbCombiner {
   @Override
   public final boolean recognize(Object object) {
      return !(object instanceof Verb) ? false : ((Verb)object).getVerbGroupId() == 15307058;
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
         verbDescription = SMSResources.getString(190);
      }

      return new PopupVerbWrapper(verbDescription, SMSResources.getString(180), verbs[0].getOrdering(), verbs, descriptions, defaultVerb, true);
   }
}
