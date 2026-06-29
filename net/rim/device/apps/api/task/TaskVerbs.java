package net.rim.device.apps.api.task;

import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;

public final class TaskVerbs {
   public static final int VERB_OPEN_TASK = 1;
   public static final int VERB_NEW_TASK = 2;

   private TaskVerbs() {
   }

   public static final Verb getTaskVerb(int type) {
      VerbRepository taskVerbs;
      switch (type) {
         case 0:
            return null;
         case 1:
         default:
            taskVerbs = VerbRepository.getVerbRepository(-7621772147653206349L);
            break;
         case 2:
            taskVerbs = VerbRepository.getVerbRepository(-5900177594279140906L);
      }

      if (taskVerbs != null) {
         Verb[] verbs = taskVerbs.getVerbs(null);
         if (verbs != null && verbs.length > 0) {
            Verb var10000 = verbs[0];
            if (!(verbs[0] instanceof Object)) {
               return verbs[0];
            }

            return (Verb)((Copyable)var10000).copy();
         }
      }

      return null;
   }
}
