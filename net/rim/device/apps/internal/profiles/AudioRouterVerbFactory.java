package net.rim.device.apps.internal.profiles;

import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;

final class AudioRouterVerbFactory implements VerbFactory {
   static final void registerOnceOnSystemStart() {
      AudioRouterVerbFactory factory = new AudioRouterVerbFactory();
      VerbFactoryRepository.addFactory(-5280468186386428176L, factory);
   }

   private AudioRouterVerbFactory() {
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      Verb[] verbs = new Object[0];
      if (context instanceof Object) {
         AudioPathControl control = (AudioPathControl)context;
         if (control.canSwitchToPath(3)) {
            Arrays.add(verbs, new SendToHeadsetVerb(control));
         }

         if (control.canSwitchToPath(1)) {
            Arrays.add(verbs, new SendToSpeakerphoneVerb(control));
         }

         if (control.canSwitchToPath(0)) {
            Arrays.add(verbs, new SendToHandsetVerb(control));
         }
      }

      return verbs;
   }
}
