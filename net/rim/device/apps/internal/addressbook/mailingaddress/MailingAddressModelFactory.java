package net.rim.device.apps.internal.addressbook.mailingaddress;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;

class MailingAddressModelFactory extends RIMModelFactory {
   private int _type;

   MailingAddressModelFactory(int type) {
      this._type = type;
   }

   @Override
   public Object createInstance(Object initialData) {
      MailingAddressModelImpl model = null;
      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         label149:
         try {
            int position = syncBuffer.getPosition();
            model = new MailingAddressModelImpl(null);
            if (this._type == 0) {
               model.setAddressLine1(syncBuffer.getString(35, true));
               model.setAddressLine2(syncBuffer.getString(position, 36, true));
               model.setCity(syncBuffer.getString(position, 38, true));
               model.setArea(syncBuffer.getString(position, 39, true));
               model.setZipOrPostalCode(syncBuffer.getString(position, 40, true));
               model.setCountry(syncBuffer.getString(position, 41, true));
               syncBuffer.getString(position, 37, true);
            } else {
               model.setAddressLine1(syncBuffer.getString(61, true));
               model.setAddressLine2(syncBuffer.getString(position, 62, true));
               model.setCity(syncBuffer.getString(position, 69, true));
               model.setArea(syncBuffer.getString(position, 70, true));
               model.setZipOrPostalCode(syncBuffer.getString(position, 71, true));
               model.setCountry(syncBuffer.getString(position, 72, true));
               syncBuffer.getString(position, 63, true);
            }

            if (!model.hasData()) {
               model = null;
            }
         } finally {
            break label149;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         int size = data.size();
         model = new MailingAddressModelImpl(null);

         for (int i = 0; i < size; i += 2) {
            String label = (String)data.elementAt(i);
            String value = (String)data.elementAt(i + 1);
            if (this._type == 0) {
               if (label.equals("Address1")) {
                  model.setAddressLine1(value);
               } else if (label.equals("Address2")) {
                  model.setAddressLine2(value);
               } else if (label.equals("City")) {
                  model.setCity(value);
               } else if (label.equals("State/Prov")) {
                  model.setArea(value);
               } else if (label.equals("ZIP/Postal Code")) {
                  model.setZipOrPostalCode(value);
               } else {
                  if (!label.equals("Country")) {
                     continue;
                  }

                  model.setCountry(value);
               }
            } else if (label.equals("Home Address1")) {
               model.setAddressLine1(value);
            } else if (label.equals("Home Address2")) {
               model.setAddressLine2(value);
            } else if (label.equals("Home City")) {
               model.setCity(value);
            } else if (label.equals("Home State/Prov")) {
               model.setArea(value);
            } else if (label.equals("Home ZIP/Postal Code")) {
               model.setZipOrPostalCode(value);
            } else {
               if (!label.equals("Home Country")) {
                  continue;
               }

               model.setCountry(value);
            }

            if (i != 0) {
               data.removeElementAt(i);
               data.removeElementAt(i);
               i -= 2;
               size -= 2;
            }
         }

         if (!model.hasData()) {
            model = null;
         }
      } else {
         model = new MailingAddressModelImpl(initialData);
      }

      if (model != null) {
         model.setType(this._type);
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
   public boolean recognize(Object object) {
      if (object instanceof MailingAddressModelImpl) {
         return ((MailingAddressModelImpl)object).getType() == this._type;
      }

      if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer != null) {
            switch (syncBuffer.getFieldType(true)) {
               case 35:
               case 36:
               case 37:
               case 38:
               case 39:
               case 40:
               case 41:
                  if (this._type == 0) {
                     return true;
                  }

                  return false;
               case 61:
               case 62:
               case 63:
               case 69:
               case 70:
               case 71:
               case 72:
                  if (this._type == 1) {
                     return true;
                  }

                  return false;
            }
         }
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         if (data != null && data.size() > 0) {
            Object label = data.elementAt(0);
            if (this._type == 0) {
               if (!label.equals("Address1")
                  && !label.equals("Address2")
                  && !label.equals("City")
                  && !label.equals("State/Prov")
                  && !label.equals("ZIP/Postal Code")
                  && !label.equals("Country")) {
                  return false;
               }

               return true;
            }

            if (!label.equals("Home Address1")
               && !label.equals("Home Address2")
               && !label.equals("Home City")
               && !label.equals("Home State/Prov")
               && !label.equals("Home ZIP/Postal Code")
               && !label.equals("Home Country")) {
               return false;
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public Verb[] getVerbs(Object context) {
      return new Object[]{new Object(this, 16864848, 5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook", 503)};
   }
}
