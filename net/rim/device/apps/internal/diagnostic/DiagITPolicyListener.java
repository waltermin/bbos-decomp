package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;

final class DiagITPolicyListener implements GlobalEventListener {
   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object obj0, Object obj1) {
      if (guid == 8508406279413621091L) {
         boolean newDisabled = ITPolicy.getBoolean(46, 1, false);
         if (newDisabled != Diag.appDisabled) {
            if (newDisabled) {
               VerbRepository repository1 = VerbRepository.getVerbRepository(1479696779947759213L);
               Verb[] v = repository1.getVerbs(1);

               for (int i = 0; i < v.length; i++) {
                  if (v[i] instanceof DiagnosticDisplayVerb) {
                     repository1.deregister(v[i], 4738722199580714034L);
                     break;
                  }
               }

               Diag.appDisabled = true;
               return;
            }

            boolean isVerbRegistered = false;
            VerbRepository repository1 = VerbRepository.getVerbRepository(1479696779947759213L);
            Verb[] v = repository1.getVerbs(1);

            for (int i = 0; i < v.length; i++) {
               if (v[i] instanceof DiagnosticDisplayVerb) {
                  isVerbRegistered = true;
                  break;
               }
            }

            if (!isVerbRegistered) {
               Diag.registerVerb();
               Diag.appDisabled = false;
            }
         }
      }
   }
}
