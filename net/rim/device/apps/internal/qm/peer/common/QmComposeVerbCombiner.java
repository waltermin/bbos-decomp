package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.qm.resource.QmResources;

public final class QmComposeVerbCombiner implements VerbCombiner {
   public static final int COMPOSE_QM_VERB_GROUP = -1937319827;
   public static final long QM_COMPOSE_VERB_COMBINER = 5126250407518351329L;
   private static QmComposeVerbCombiner _instance;

   public static final void registerOnceOnSystemStartUp() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         synchronized (applicationRegistry) {
            if (applicationRegistry.get(5126250407518351329L) == null) {
               _instance = new QmComposeVerbCombiner();
               applicationRegistry.put(5126250407518351329L, _instance);
               VerbCombinerRepository.addCombiner(5126250407518351329L, _instance);
            }
         }
      }
   }

   @Override
   public final boolean recognize(Object object) {
      return !(object instanceof Object) ? false : ((Verb)object).getVerbGroupId() == -1937319827;
   }

   @Override
   public final Verb createWrapperVerb(Verb[] verbs, Verb defaultVerb) {
      String[] descriptions = new Object[verbs.length];
      ContextObject contextObject = (ContextObject)(new Object(34));
      contextObject.setFlag(63);

      for (int i = verbs.length - 1; i >= 0; i--) {
         descriptions[i] = verbs[i].toString(contextObject);
      }

      String verbDescription = QmResources.getString(85);
      return (Verb)(new Object(verbDescription, QmResources.getString(84), verbs[0].getOrdering(), verbs, descriptions, defaultVerb));
   }
}
