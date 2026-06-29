package net.rim.device.apps.api.memo;

import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;

public class MemoVerbs {
   public static final int VERB_NEW_MEMO = 0;
   public static final int VERB_EDIT_MEMO = 1;
   public static final int VERB_VIEW_MEMO = 2;

   MemoVerbs() {
   }

   public static Verb getMemoVerb(int type) {
      VerbRepository memoVerbs;
      switch (type) {
         case -1:
            return null;
         case 0:
         default:
            memoVerbs = VerbRepository.getVerbRepository(1725319375636856359L);
            break;
         case 1:
            memoVerbs = VerbRepository.getVerbRepository(725314104275387162L);
            break;
         case 2:
            memoVerbs = VerbRepository.getVerbRepository(5385087948863077831L);
      }

      if (memoVerbs != null) {
         Verb[] verbs = memoVerbs.getVerbs(null);
         if (verbs != null && verbs.length > 0) {
            Verb var10000 = verbs[0];
            if (!(verbs[0] instanceof Copyable)) {
               return verbs[0];
            }

            return (Verb)((Copyable)var10000).copy();
         }
      }

      return null;
   }
}
