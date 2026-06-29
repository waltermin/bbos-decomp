package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;

public final class EmailFilterBodyModelFactory extends RIMModelFactory {
   private static EmailFilterBodyModelFactory _singletonInstance = new EmailFilterBodyModelFactory();

   private EmailFilterBodyModelFactory() {
   }

   public static final synchronized EmailFilterBodyModelFactory getInstance() {
      return _singletonInstance;
   }

   @Override
   public final Object createInstance(Object initialData) {
      EmailFilterBodyModelImpl filterBodyModel = null;
      if (ContextObject.getFlag(initialData, 33) && ContextObject.getFlag(initialData, 19)) {
         try {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
            if (syncBuffer == null) {
               return null;
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(4)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setAction(syncBuffer.getInt(4, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(5, true)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setFromList(syncBuffer.getString(5, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(6, true)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setSentToList(syncBuffer.getString(6, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(7, true)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setSubject(syncBuffer.getString(7, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(8, true)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setBody(syncBuffer.getString(8, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(9)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setRecipientTypes(syncBuffer.getInt(9, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(10)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setSensitivity((byte)syncBuffer.getInt(10, true));
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(11)) {
               if (filterBodyModel == null) {
                  filterBodyModel = new EmailMessageModelImpl();
               }

               filterBodyModel.setImportance((byte)syncBuffer.getInt(11, true));
               return filterBodyModel;
            }
         } finally {
            ;
         }
      } else {
         filterBodyModel = new EmailMessageModelImpl(initialData, 0);
      }

      return filterBodyModel;
   }

   @Override
   public final boolean recognize(Object object) {
      return object instanceof EmailMessageModelImpl ? true : ContextObject.getFlag(object, 33) && ContextObject.getFlag(object, 19);
   }
}
