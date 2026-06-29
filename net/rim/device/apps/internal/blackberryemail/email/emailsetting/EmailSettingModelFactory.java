package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class EmailSettingModelFactory extends RIMModelFactory {
   private static EmailSettingModelFactory _singletonInstance = new EmailSettingModelFactory();

   private EmailSettingModelFactory() {
   }

   public static final synchronized EmailSettingModelFactory getInstance() {
      return _singletonInstance;
   }

   @Override
   public final Object createInstance(Object initialData) {
      if (ContextObject.getFlag(initialData, 50) && ContextObject.getFlag(initialData, 19)) {
         EmailSettingModelImpl emailSettingModel = new EmailSettingModelImpl();
         emailSettingModel._fields.ensureCapacity(16);
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         int uid = syncBuffer.getUID();
         if (uid != 0) {
            emailSettingModel._uid = uid;
         }

         try {
            while (true) {
               switch (syncBuffer.getFieldType(true)) {
                  case 0:
                     syncBuffer.skipField();
                     break;
                  case 1:
                  default:
                     String sigText = syncBuffer.getString();
                     emailSettingModel.setAutoSignature(sigText);
                     emailSettingModel._autoSignature = sigText != null && sigText.length() > 0 ? 1 : 0;
                     break;
                  case 2:
                     emailSettingModel._emailRedirection = syncBuffer.getInt();
                     break;
                  case 3:
                     emailSettingModel._saveInSentItem = syncBuffer.getInt();
                     break;
                  case 4:
                     emailSettingModel._filterDefaultAction = syncBuffer.getInt();
                     break;
                  case 5:
                     emailSettingModel._outOfOffice = syncBuffer.getInt();
                     break;
                  case 6:
                     emailSettingModel.setOutOfOfficeText(syncBuffer.getString());
                     break;
                  case 7:
                     emailSettingModel._dueDate = (int)(DateTimeUtilities.convertEpochToMilliseconds(syncBuffer.getInt()) / 1000);
                     break;
                  case 8:
                     emailSettingModel._key = syncBuffer.getInt();
               }
            }
         } finally {
            emailSettingModel._fields.trimToSize();
            return emailSettingModel;
         }
      } else {
         return new EmailSettingModelImpl(initialData);
      }
   }

   @Override
   public final boolean recognize(Object object) {
      return object instanceof EmailSettingModelImpl ? true : ContextObject.getFlag(object, 50) && ContextObject.getFlag(object, 19);
   }
}
