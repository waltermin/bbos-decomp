package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Vector;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class CompanyInfoModelFactory extends RIMModelFactory {
   @Override
   public final boolean recognize(Object object) {
      if (object instanceof CompanyInfoModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer != null) {
            if (syncBuffer.getFieldType(true) == 33) {
               return true;
            }

            return false;
         }
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         if (data != null && data.size() > 0) {
            Object label = data.elementAt(0);
            return label.equals("Company");
         }
      }

      return false;
   }

   @Override
   public final Object createInstance(Object initialData) {
      CompanyInfoModel model = null;
      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            int position = syncBuffer.getPosition();
            String companyName = syncBuffer.getString(33, true);
            if (companyName != null) {
               model = new CompanyInfoModelImpl(companyName);
               String companyNameYOMI = syncBuffer.getString(78, true);
               if (companyNameYOMI != null) {
                  model.setCompanyNameYOMI(companyNameYOMI);
                  return model;
               }
            }
         } finally {
            ;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         int size = data.size();
         model = new CompanyInfoModelImpl((Object)null);

         for (int i = 0; i < size; i += 2) {
            String label = (String)data.elementAt(i);
            String value = (String)data.elementAt(i + 1);
            if (label.equals("Company")) {
               model.setCompanyName(value);
               if (i != 0) {
                  data.removeElementAt(i);
                  data.removeElementAt(i);
                  break;
               }
            }
         }

         if (model.getCompanyName() == null) {
            return null;
         }
      } else {
         model = new CompanyInfoModelImpl(initialData);
      }

      return model;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : 0;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }
}
