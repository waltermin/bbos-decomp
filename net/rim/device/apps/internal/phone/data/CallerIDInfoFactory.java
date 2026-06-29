package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

class CallerIDInfoFactory extends RIMModelFactory {
   @Override
   public Object createInstance(Object initialData) {
      CallerIDInfo model = null;
      if (ContextObject.getFlag(initialData, 20) && ContextObject.getFlag(initialData, 19)) {
         long addressCardUid = 0;
         int flags = 0;
         int clipDisplayMode = 0;
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);

         label246:
         try {
            addressCardUid = syncBuffer.getLong(10, true);
            flags = syncBuffer.getInt(13, true);
            clipDisplayMode = syncBuffer.getInt(14, true);
         } finally {
            break label246;
         }

         Object modelData = ContextObject.get(initialData, 254);
         AbstractPhoneNumberModel phoneNumberModel;
         if (!(modelData instanceof Object)) {
            if (syncBuffer.getFieldType() == 15) {
               if (DirectConnect.isSupported()) {
                  phoneNumberModel = (AbstractPhoneNumberModel)FactoryUtil.createInstance(532879436795165891L, initialData);
               } else {
                  phoneNumberModel = null;
               }
            } else {
               phoneNumberModel = (AbstractPhoneNumberModel)FactoryUtil.createInstance(3797587162219887872L, initialData);
            }
         } else {
            phoneNumberModel = (AbstractPhoneNumberModel)modelData;
         }

         model = new CallerIDInfo();
         model._phoneNumber = phoneNumberModel;
         model._flags = flags;
         model._addressCardUid = addressCardUid;
         if (clipDisplayMode != 0) {
            model.setClipDisplayMode(clipDisplayMode);
         }

         label240:
         try {
            String fullName = null;
            String firstName = syncBuffer.getString(31, true);
            String lastName = syncBuffer.getString(31, true);
            if (firstName != null) {
               fullName = firstName;
               if (lastName != null) {
                  fullName = ((StringBuffer)(new Object())).append(fullName).append(' ').toString();
                  fullName = ((StringBuffer)(new Object())).append(fullName).append(lastName).toString();
               }

               model.setFriendlyName(fullName);
            }
         } finally {
            break label240;
         }

         label237:
         try {
            String companyName = syncBuffer.getString(33, true);
            if (companyName != null) {
               model.setFriendlyName(companyName, -2467076596918202204L);
            }
         } finally {
            break label237;
         }

         model.resolveAddressCardInformation();
         return model;
      } else if (!PhoneUtilities.getPrivateFlag(initialData, 24)) {
         if (!PhoneUtilities.getPrivateFlag(initialData, 22) && !PhoneUtilities.getPrivateFlag(initialData, 23)) {
            model = new CallerIDInfo();
         } else {
            if (PhoneUtilities.getPrivateFlag(initialData, 26)) {
               model = new CallerIDInfo();
               model.setClipDisplayMode(1);
            } else if (PhoneUtilities.getPrivateFlag(initialData, 25)) {
               model = new CallerIDInfo();
               model.setClipDisplayMode(2);
            } else {
               Object phoneNumber = ContextObject.get(initialData, 247);
               Object addressCard = ContextObject.get(initialData, 252);
               boolean useSmartDialing = ContextObject.getFlag(initialData, 117);
               model = new CallerIDInfo((PersistableRIMModel)phoneNumber, (PersistableRIMModel)addressCard, true, useSmartDialing);
            }

            Object callID = ContextObject.get(initialData, 253);
            if (callID instanceof Object) {
               String str = (String)callID;
               if (str.length() > 0) {
                  model.setFriendlyName(str);
                  return model;
               }
            }
         }

         return model;
      } else {
         Object phoneNumber = ContextObject.get(initialData, 247);
         Object addressCard = ContextObject.get(initialData, 252);
         boolean useSmartDialing = ContextObject.getFlag(initialData, 117);
         boolean voicemailRedirection = PhoneUtilities.getPrivateFlag(initialData, 50);
         if (addressCard == null || voicemailRedirection) {
            if (PhoneUtilities.getPrivateFlag(initialData, 7)) {
               addressCard = new SpecialAddressCard(-7117173429217454741L);
            } else if (PhoneUtilities.getPrivateFlag(initialData, 33)) {
               addressCard = new SpecialAddressCard(2280195576896513113L);
            } else if (PhoneUtilities.getPrivateFlag(initialData, 62)) {
               addressCard = new SpecialAddressCard(-2948267102114848593L);
            }
         }

         return new CallerIDInfo((PersistableRIMModel)phoneNumber, (PersistableRIMModel)addressCard, false, useSmartDialing, initialData);
      }
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (object == null) {
         return false;
      } else if (object instanceof CallerIDInfo) {
         return true;
      } else if (ContextObject.getFlag(object, 20) && ContextObject.getFlag(object, 19)) {
         ContextObject context = (ContextObject)object;
         SyncBuffer syncBuffer = (SyncBuffer)context.get(255);
         int type = syncBuffer.getFieldType();
         return type == 10 || type == 13 || type == 9 || type == 15 || type == 11;
      } else {
         return false;
      }
   }
}
