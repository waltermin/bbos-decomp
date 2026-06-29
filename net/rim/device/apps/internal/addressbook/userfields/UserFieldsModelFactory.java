package net.rim.device.apps.internal.addressbook.userfields;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.RIMModelFactoryCreateVerb;
import net.rim.device.apps.api.framework.verb.Verb;

public class UserFieldsModelFactory extends RIMModelFactory {
   static final int[] _addressBookSyncFieldIds = new int[]{
      65, 66, 67, 68, 1870004480, 16803179, 100713909, 12584539, 1694657542, 134219776, 119610152, 205588480, 1091043425, -1026723728, 765355936, -1572730880
   };

   UserFieldsModelFactory() {
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof UserFieldsModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer != null) {
            switch (syncBuffer.getFieldType(true)) {
               case 64:
                  break;
               case 65:
               case 66:
               case 67:
               case 68:
               default:
                  return true;
            }
         }
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         if (data != null && data.size() > 0) {
            String label = (String)data.elementAt(0);
            return label.startsWith("User");
         }
      }

      return false;
   }

   @Override
   public Object createInstance(Object initialData) {
      UserFieldsModel model = null;
      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            String[] fields = new String[4];
            boolean isData = false;
            int fieldCount = fields.length;

            for (int i = 0; i < fieldCount; i++) {
               syncBuffer.setPosition(0);
               if (syncBuffer.containsType(_addressBookSyncFieldIds[i], true)) {
                  fields[i] = syncBuffer.getString(_addressBookSyncFieldIds[i], true);
                  if (fields[i] != null && fields[i].length() > 0) {
                     isData = true;
                  }
               }
            }

            if (isData) {
               return new UserFieldsModel(fields);
            }
         } finally {
            ;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         int size = data.size();
         String[] fields = new String[4];
         boolean isData = false;

         for (int i = 0; i < size; i += 2) {
            String label = (String)data.elementAt(i);
            String value = (String)data.elementAt(i + 1);
            if (label.startsWith("User")) {
               label170:
               try {
                  int index = Integer.parseInt(label.substring(4));
                  if (index >= 1 && index <= 4 && value != null && value.length() > 0) {
                     fields[--index] = value;
                     isData = true;
                  }
               } finally {
                  break label170;
               }

               data.removeElementAt(i);
               data.removeElementAt(i);
               i -= 2;
               size -= 2;
            }
         }

         if (isData) {
            return new UserFieldsModel(fields);
         }
      } else {
         model = new UserFieldsModel(initialData);
      }

      return model;
   }

   @Override
   public int getMinimumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MIN_VALUE;
   }

   @Override
   public int getMaximumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MAX_VALUE;
   }

   @Override
   public Verb[] getVerbs(Object context) {
      return new Verb[]{new RIMModelFactoryCreateVerb(this, 16865360, 5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook", 1203)};
   }
}
