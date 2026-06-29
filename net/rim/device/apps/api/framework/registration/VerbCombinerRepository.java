package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.verb.VerbCombiner;

public class VerbCombinerRepository {
   private static final long ID;
   public static final long ALL_VERB_COMBINERS;
   public static final long COMPOSE_VERB_COMBINER;
   public static final long PHONE_COMPOSE_VERB_COMBINER;
   public static final long SMS_COMPOSE_VERB_COMBINER;
   public static final long MMS_COMPOSE_VERB_COMBINER;
   public static final long EMAIL_COMPOSE_VERB_COMBINER;
   public static final long PIN_COMPOSE_VERB_COMBINER;
   public static final long IMPLUS_PHONE_COMPOSE_VERB_COMBINER;
   private static LongHashtable _combinerCollections = ApplicationRegistry.getApplicationRegistry().getLongHashtable(-6051637417518632414L);

   private VerbCombinerRepository() {
   }

   public static void addCombiner(long collectionId, VerbCombiner combiner) {
      if (combiner == null) {
         throw new Object();
      }

      VerbCombiner[] combiners;
      synchronized (_combinerCollections) {
         combiners = getCombiners(collectionId);
         if (combiners == null) {
            combiners = new VerbCombiner[0];
            _combinerCollections.put(collectionId, combiners);
         }
      }

      synchronized (combiners) {
         if (Arrays.getIndex(combiners, combiner) == -1) {
            Arrays.add(combiners, combiner);
         }
      }

      if (collectionId != -3072555018635390988L) {
         addCombiner(-3072555018635390988L, combiner);
      }
   }

   public static void removeCombiner(long collectionId, VerbCombiner combiner) {
      VerbCombiner[] combiners = getCombiners(collectionId);
      if (combiners != null) {
         synchronized (combiners) {
            Arrays.remove(combiners, combiner);
         }
      }

      if (collectionId != -3072555018635390988L) {
         removeCombiner(-3072555018635390988L, combiner);
      }
   }

   public static VerbCombiner[] getCombiners(long collectionId) {
      return (VerbCombiner[])_combinerCollections.get(collectionId);
   }
}
