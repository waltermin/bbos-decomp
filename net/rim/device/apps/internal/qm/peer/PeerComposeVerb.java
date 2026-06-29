package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.internal.qm.peer.common.QmComposeVerb;

public final class PeerComposeVerb extends QmComposeVerb {
   private static final long PEER_COMPOSE_VERB = 2085988894834128019L;
   private static PeerComposeVerb _instance;

   PeerComposeVerb(int ordering) {
      super(ordering);
   }

   static final void registerOnceOnSystemStartUp() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         synchronized (applicationRegistry) {
            Object obj = applicationRegistry.get(2085988894834128019L);
            if (obj == null) {
               _instance = new PeerComposeVerb(1267040);
               applicationRegistry.put(2085988894834128019L, _instance);
               VerbRepository.getVerbRepository(-7881764549058890736L).register(_instance, -537018776823173138L);
            }
         }
      }
   }

   @Override
   public final Object invoke(Object context) {
      PeerApplication.dismissConversationScreen();
      PeerEntry.getInstance().run();
      return null;
   }
}
