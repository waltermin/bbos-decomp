package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class DialVerbCombiner implements VerbCombiner {
   private static DialVerbCombiner _instance;

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof Object) {
         Verb verb = (Verb)object;
         if (verb.getVerbGroupId() != 1187214) {
            return false;
         }

         while (verb instanceof Object) {
            verb = ((WrapperVerb)verb).getInnerVerb();
         }

         if (verb instanceof DialVerb) {
            return ((DialVerb)verb).canCoalesce();
         }
      }

      return false;
   }

   public static final DialVerbCombiner getInstance() {
      if (_instance == null) {
         _instance = new DialVerbCombiner();
      }

      return _instance;
   }

   @Override
   public final Verb createWrapperVerb(Verb[] verbs, Verb defaultVerb) {
      Arrays.sort(verbs, 0, verbs.length, new DialVerbTypeComparator());
      ContextObject contextObject = null;
      String[] descriptions = new Object[verbs.length];

      for (int i = verbs.length - 1; i >= 0; i--) {
         Verb verb = verbs[i];

         while (verb instanceof Object) {
            verb = ((WrapperVerb)verb).getInnerVerb();
         }

         DialVerb dialVerb = (DialVerb)verb;
         contextObject = PhoneContexts.GET_VERBS_CONTEXT_WR.getContextObject();
         contextObject.reset();
         contextObject.setFlag(51, 42, 34);
         dialVerb.setContext(contextObject);
         descriptions[i] = dialVerb.toString();
      }

      String prompt = PhoneResources.getString(116);
      String verbDescription = null;
      if (verbs[0] != null) {
         verbDescription = verbs[0].toString(contextObject);
      }

      if (verbDescription == null) {
         verbDescription = PhoneResources.getString(115);
      }

      return new DialVerbChooser(verbDescription, prompt, verbs[0].getOrdering(), verbs, descriptions, defaultVerb);
   }
}
