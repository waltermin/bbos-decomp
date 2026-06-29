package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;

public class RIMModelFactoryRepository {
   public static final long ADDRESSCARDREPOSITORY;
   public static final long REVERSELOOKUPADDRESSCARDREPOSITORY;
   public static final long GROUPADDRESSCARDREPOSITORY;
   public static final long EMAILMESSAGEREPOSITORY;
   public static final long ADDRESSBOOKREPOSITORY;
   public static final long MESSAGEFILTERREPOSITORY;
   public static final long TASKAPPREPOSITORY;
   public static final long TASKREPOSITORY;
   public static final long MEMOREPOSITORY;
   public static final long EMAIL_COMPOSE_OPTIONS_REPOSITORY;
   public static final long PINMESSAGEREPOSITORY;
   public static final long IMPLUSREPOSITORY;
   public static final long MESSAGE_SERVICES_CONTENT_TYPE_OPTIONS_REPOSITORY;
   public static final long QUICK_CONTACT_REPOSITORY;
   public static final long PHONECALLREPOSITORY;
   public static final long PHONEHOTLISTREPOSITORY;
   public static final long EMAIL_FILTER_REPOSITORY;
   public static final long EMAIL_SETTING_REPOSITORY;
   public static final long DEVICE_AGENT_REPOSITORY;
   private static final long RIM_MODEL_FACTORY_REPOSITORY_ID;
   private static LongHashtable _factoryCollections = ApplicationRegistry.getApplicationRegistry().getLongHashtable(8278738986755225333L);

   private RIMModelFactoryRepository() {
   }

   public static void addFactory(long collectionId, RIMModelFactory modelFactory) {
      if (modelFactory == null) {
         throw new Object();
      }

      RIMModelFactory[] factories;
      synchronized (_factoryCollections) {
         factories = getModelFactories(collectionId);
         if (factories == null) {
            factories = new RIMModelFactory[0];
            _factoryCollections.put(collectionId, factories);
         }
      }

      synchronized (factories) {
         if (Arrays.getIndex(factories, modelFactory) == -1) {
            Arrays.add(factories, modelFactory);
         }
      }
   }

   public static void removeFactory(long collectionId, RIMModelFactory modelFactory) {
      RIMModelFactory[] factories = getModelFactories(collectionId);
      if (factories != null) {
         synchronized (factories) {
            Arrays.remove(factories, modelFactory);
         }
      }
   }

   public static RIMModelFactory[] getModelFactories(long collectionId) {
      return (RIMModelFactory[])_factoryCollections.get(collectionId);
   }
}
