package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingCollectionImpl;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.filters.EmailFilterBodyModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.filters.EmailFilterCollectionImpl;
import net.rim.device.apps.internal.blackberryemail.email.filters.EmailFilterModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.filters.FilterNameSearchModelFactory;
import net.rim.device.apps.internal.commonmodels.title.TitleModelFactory;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

public class EmailOptionsManager implements GlobalEventListener {
   private String[] _registeredUserIdArray = new Object[0];
   private static final long GUID;

   private EmailOptionsManager() {
   }

   public static EmailOptionsManager getInstance() {
      boolean initializeInstance = false;
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      EmailOptionsManager EmailOptionsManagerInstance = (EmailOptionsManager)registry.get(-3920253466163615500L);
      if (EmailOptionsManagerInstance == null) {
         synchronized (registry) {
            EmailOptionsManagerInstance = (EmailOptionsManager)registry.get(-3920253466163615500L);
            if (EmailOptionsManagerInstance == null) {
               EmailOptionsManagerInstance = new EmailOptionsManager();
               initializeInstance = true;
               registry.put(-3920253466163615500L, EmailOptionsManagerInstance);
            }
         }
      }

      if (initializeInstance) {
         EmailOptionsManagerInstance.init();
      }

      return EmailOptionsManagerInstance;
   }

   public static void registerOnceOnSystemStart() {
      VerbFactoryRepository.addFactory(2729258854446987021L, new MessageListVerbFactory());
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-4993125662867338663L, new FilterNameSearchModelFactory());
      ar.put(-1388842558271364146L, EmailFilterModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(-7388907038055180696L, TitleModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(-7388907038055180696L, EmailFilterBodyModelFactory.getInstance());
      ar.put(1683565110580900081L, EmailSettingModelFactory.getInstance());
      Proxy.getInstance().addGlobalEventListener(getInstance());
   }

   boolean displaySyncOptions() {
      return this._registeredUserIdArray.length > 0;
   }

   boolean displayCmimeOptions() {
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByCid("CMIME");
      return srs.length > 0;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      try {
         if (guid == -4220058463650496006L) {
            if (object0 instanceof Object) {
               this.addSB((ServiceRecord)object0);
            }
         } else if (guid == 2522898683889177438L) {
            if (object0 instanceof Object) {
               this.removeSB((ServiceRecord)object0);
            }
         } else if (guid == 8288627527798139133L) {
            if (object0 instanceof Object) {
               ServiceRecord sr = (ServiceRecord)object0;
               if (sr.getType() != 0) {
                  this.removeSB(sr);
               } else {
                  this.addSB(sr);
               }
            }
         } else if (guid == 8508406279413621091L || guid == -594020114676189989L) {
            String autoSignature = ITPolicy.getString(94);
            ServiceRecord[] srs = ServiceBook.getSB().findRecordsByCid("CMIME");
            if (autoSignature != null && srs.length > 0) {
               for (int i = 0; i < srs.length; i++) {
                  ServiceRecord serviceRecord = ServiceBook.getSB().getRecordByUidAndCid(srs[i].getUid(), "SYNC");
                  if (serviceRecord != null) {
                     String userId = String.valueOf(serviceRecord.getUserId());
                     EmailSettingModelImpl model = (EmailSettingModelImpl)EmailSettingCollectionImpl.getInstance(userId).getAt(0);
                     if (model != null) {
                        model.setAutoSignature(autoSignature);
                        model.updateConfig();
                     }
                  }
               }
            }
         }
      } finally {
         return;
      }
   }

   private void init() {
      SyncManager sm = SyncManager.getInstance();
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByCid("SYNC");

      for (int i = srs.length - 1; i >= 0; i--) {
         String id = String.valueOf(srs[i].getUserId());
         if (!this.contains(id)) {
            synchronized (this._registeredUserIdArray) {
               Arrays.add(this._registeredUserIdArray, id);
            }

            sm.enableSynchronization(EmailFilterCollectionImpl.getInstance(id));
            sm.enableSynchronization(EmailSettingCollectionImpl.getInstance(id));
         }
      }
   }

   private boolean contains(String userId) {
      synchronized (this._registeredUserIdArray) {
         for (int i = this._registeredUserIdArray.length - 1; i >= 0; i--) {
            if (StringUtilities.strEqualIgnoreCase(userId, this._registeredUserIdArray[i], 1701707776)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean removeUserIdFromRegisteredArray(String userId) {
      synchronized (this._registeredUserIdArray) {
         for (int i = this._registeredUserIdArray.length - 1; i >= 0; i--) {
            if (StringUtilities.strEqualIgnoreCase(userId, this._registeredUserIdArray[i], 1701707776)) {
               System.arraycopy(this._registeredUserIdArray, i + 1, this._registeredUserIdArray, i, this._registeredUserIdArray.length - i - 1);
               Array.resize(this._registeredUserIdArray, this._registeredUserIdArray.length - 1);
               return true;
            }
         }

         return false;
      }
   }

   private void addSB(ServiceRecord sr) {
      if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "SYNC", 1701707776)) {
         String userId = String.valueOf(sr.getUserId());
         if (!this.contains(userId)) {
            synchronized (this._registeredUserIdArray) {
               Arrays.add(this._registeredUserIdArray, userId);
            }

            SyncManager.getInstance().enableSynchronization(EmailFilterCollectionImpl.getInstance(userId));
            SyncManager.getInstance().enableSynchronization(EmailSettingCollectionImpl.getInstance(userId));
         }
      }
   }

   private void removeSB(ServiceRecord sr) {
      if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "SYNC", 1701707776)) {
         String userId = String.valueOf(sr.getUserId());
         if (this.removeUserIdFromRegisteredArray(userId)) {
            SyncManager.getInstance().disableSynchronization(EmailFilterCollectionImpl.getInstance(userId));
            SyncManager.getInstance().disableSynchronization(EmailSettingCollectionImpl.getInstance(userId));
         }
      }
   }
}
