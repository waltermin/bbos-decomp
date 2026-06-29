package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;

public class RIMModelFactoryRepository {
   public static final long ADDRESSCARDREPOSITORY = -5785746452676094833L;
   public static final long REVERSELOOKUPADDRESSCARDREPOSITORY = -5785746452676094832L;
   public static final long GROUPADDRESSCARDREPOSITORY = 5109901783680962134L;
   public static final long EMAILMESSAGEREPOSITORY = 2497613418300956405L;
   public static final long ADDRESSBOOKREPOSITORY = -7921492803965144520L;
   public static final long MESSAGEFILTERREPOSITORY = 7820085525428081380L;
   public static final long TASKAPPREPOSITORY = -8250775496544885030L;
   public static final long TASKREPOSITORY = 7798410905730545828L;
   public static final long MEMOREPOSITORY = 8809206174646860213L;
   public static final long EMAIL_COMPOSE_OPTIONS_REPOSITORY = 3735013535338552331L;
   public static final long PINMESSAGEREPOSITORY = 3893959701496671961L;
   public static final long IMPLUSREPOSITORY = 617011471051544137L;
   public static final long MESSAGE_SERVICES_CONTENT_TYPE_OPTIONS_REPOSITORY = -1203249910507515082L;
   public static final long QUICK_CONTACT_REPOSITORY = 9021823141602707590L;
   public static final long PHONECALLREPOSITORY = -5829986326706945081L;
   public static final long PHONEHOTLISTREPOSITORY = -3466239368616563929L;
   public static final long EMAIL_FILTER_REPOSITORY = -7388907038055180696L;
   public static final long EMAIL_SETTING_REPOSITORY = 1212906989701596812L;
   public static final long DEVICE_AGENT_REPOSITORY = -587743679001488504L;
   private static final long RIM_MODEL_FACTORY_REPOSITORY_ID = 8278738986755225333L;
   private static LongHashtable _factoryCollections = ApplicationRegistry.getApplicationRegistry().getLongHashtable(8278738986755225333L);

   private RIMModelFactoryRepository() {
   }

   public static void addFactory(long collectionId, RIMModelFactory modelFactory) {
      if (modelFactory == null) {
         throw new IllegalArgumentException();
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
