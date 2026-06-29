package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;

public class VerbFactoryRepository {
   private static final long VERB_FACTORIES_REPOSITORY_ID = -4466860719023034319L;
   private static LongHashtable _verbFactories = ApplicationRegistry.getApplicationRegistry().getLongHashtable(-4466860719023034319L);

   private VerbFactoryRepository() {
   }

   public static void addFactory(long id, VerbFactory verbFactory) {
      if (verbFactory == null) {
         throw new IllegalArgumentException();
      }

      VerbFactory[] factories = (VerbFactory[])_verbFactories.get(id);
      if (factories == null) {
         synchronized (_verbFactories) {
            factories = getVerbFactories(id);
            if (factories == null) {
               factories = new VerbFactory[0];
               _verbFactories.put(id, factories);
            }
         }
      }

      synchronized (factories) {
         if (Arrays.getIndex(factories, verbFactory) == -1) {
            Arrays.add(factories, verbFactory);
         }
      }
   }

   public static void removeFactory(long id, VerbFactory verbFactory) {
      VerbFactory[] factories = getVerbFactories(id);
      if (factories != null) {
         synchronized (factories) {
            Arrays.remove(factories, verbFactory);
         }
      }
   }

   public static VerbFactory[] getVerbFactories(long id) {
      return (VerbFactory[])_verbFactories.get(id);
   }
}
