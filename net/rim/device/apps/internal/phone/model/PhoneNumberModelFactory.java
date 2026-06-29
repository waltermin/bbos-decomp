package net.rim.device.apps.internal.phone.model;

import java.util.Vector;
import net.rim.device.api.ui.component.ActiveFieldContext;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.PhoneNumberTypes;

public class PhoneNumberModelFactory extends RIMModelFactory {
   private int _typeIndex;
   private int _ordering;
   public static final long PHONE_NUMBER_MODEL_FACTORY_GUID = 4493747836632402768L;

   public PhoneNumberModelFactory(long objectType) {
      this._typeIndex = this.mapObjectTypeToIndex(objectType);
      this._ordering = this.getMenuOrdering(this._typeIndex);
   }

   private int getMenuOrdering(int typeIndex) {
      switch (typeIndex) {
         case 0:
            return 16864000;
         case 1:
         default:
            return 16864016;
         case 2:
            return 16864017;
         case 3:
            return 16864032;
         case 4:
            return 16864033;
         case 5:
            return 16864048;
         case 6:
            return 16864064;
         case 7:
            return 16864080;
         case 8:
            return 16864085;
      }
   }

   private int mapObjectTypeToIndex(long objectType) {
      int type = 0;
      if (objectType == 8414046446004092553L) {
         return 1;
      }

      if (objectType == 476826571898366139L) {
         return 2;
      }

      if (objectType == 7064935308737611579L) {
         return 3;
      }

      if (objectType == 7076766837289517896L) {
         return 4;
      }

      if (objectType == -442687637293762776L) {
         return 5;
      }

      if (objectType == 6627402073208639065L) {
         return 6;
      }

      if (objectType == 2862138288634470671L) {
         return 7;
      }

      if (objectType == -1843891697376347796L) {
         type = 8;
      }

      return type;
   }

   @Override
   public Object createInstance(Object initialData) {
      PhoneNumberModel model = null;
      if (initialData instanceof ActiveFieldContext) {
         ActiveFieldContext afc = (ActiveFieldContext)initialData;
         initialData = new ContextObject();
         ContextObject.put(initialData, 253, afc.getData());
      }

      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            int type = syncBuffer.getFieldType(true);
            int phoneType = 0;
            switch (type) {
               case 2:
                  phoneType = 0;
                  break;
               case 3:
                  phoneType = 7;
                  break;
               case 6:
                  phoneType = 1;
                  break;
               case 7:
                  phoneType = 3;
                  break;
               case 8:
                  phoneType = 5;
                  break;
               case 9:
                  phoneType = 6;
                  break;
               case 16:
                  phoneType = 2;
                  break;
               case 17:
                  phoneType = 4;
                  break;
               case 18:
                  phoneType = 8;
            }

            String value = syncBuffer.getString(type, true);
            if (value != null && value.length() > 0) {
               model = this.createModel((Object)null);
               model._type = phoneType;
               model.setValue(value);
               return model;
            }
         } finally {
            ;
         }
      } else {
         if (ContextObject.getFlag(initialData, 34)) {
            return this.createModel(initialData);
         }

         if (ContextObject.getFlag(initialData, 55) && ContextObject.getFlag(initialData, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
            if (syncBuffer == null) {
               return null;
            }

            DataBuffer tmpBuf = syncBuffer.getDataBuffer();
            int pos = tmpBuf.getPosition();

            try {
               int len = tmpBuf.readShort();
               tmpBuf.readByte();
               model = this.createModel((Object)null);
               model._type = PhoneNumberTypes.mapType(tmpBuf.readInt(), false);
               len -= 4;
               byte[] data = tmpBuf.getArray();
               int offset = tmpBuf.getArrayPosition();

               for (int i = 0; i < len - 1; i++) {
                  if (data[i + offset] == 13) {
                     data[i + offset] = 10;
                  }
               }

               model.setValue(new String(data, offset, len - 1));
               return model;
            } finally {
               tmpBuf.setPosition(pos);
               return model;
            }
         }

         if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
            Vector data = (Vector)ContextObject.get(initialData, 249);
            String label = (String)data.elementAt(0);
            String value = (String)data.elementAt(1);
            int phoneType;
            if (label.equals("Work #")) {
               phoneType = 1;
            } else if (label.equals("Work2 #")) {
               phoneType = 2;
            } else if (label.equals("Home #")) {
               phoneType = 3;
            } else if (label.equals("Home2 #")) {
               phoneType = 4;
            } else if (label.equals("Mobile #")) {
               phoneType = 5;
            } else if (label.equals("Fax #")) {
               phoneType = 7;
            } else if (label.equals("Pager")) {
               phoneType = 6;
            } else {
               if (!label.equals("Other #")) {
                  return null;
               }

               phoneType = 8;
            }

            model = this.createModel((Object)null);
            model._type = phoneType;
            model.setValue(value);
            return model;
         }

         if (ContextObject.getFlag(initialData, 20) && ContextObject.getFlag(initialData, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
            if (syncBuffer == null) {
               return null;
            }

            try {
               model = this.createModel((Object)null);
               model._type = PhoneNumberTypes.mapType(syncBuffer.getInt(11, true), false);
               model.setValue(syncBuffer.getString(12, true));
               return model;
            } finally {
               ;
            }
         }

         model = this.createModel(initialData);
         if (model._type == 0) {
            model._type = this._typeIndex;
         }
      }

      return model;
   }

   protected PhoneNumberModel createModel(Object initialData) {
      return new PhoneNumberModel(initialData);
   }

   @Override
   public int getMinimumCount(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         switch (this._typeIndex) {
            case 0:
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            default:
               return 1;
         }
      }

      return Integer.MIN_VALUE;
   }

   @Override
   public int getMaximumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MAX_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (!(object instanceof PhoneNumberModel)) {
         if (!(object instanceof GroupAddressCardModel)) {
            if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
               SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
               if (syncBuffer != null) {
                  switch (syncBuffer.getFieldType(true)) {
                     case 2:
                     case 3:
                     case 6:
                     case 7:
                     case 8:
                     case 9:
                     case 16:
                     case 17:
                     case 18:
                        return true;
                  }
               }

               return false;
            } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
               Vector data = (Vector)ContextObject.get(object, 249);
               String label = (String)data.elementAt(0);
               return label == null
                  ? false
                  : label.equals("Work #")
                     || label.equals("Work2 #")
                     || label.equals("Home #")
                     || label.equals("Home2 #")
                     || label.equals("Mobile #")
                     || label.equals("Fax #")
                     || label.equals("Pager")
                     || label.equals("Other #");
            } else if (ContextObject.getFlag(object, 55) && ContextObject.getFlag(object, 19)) {
               SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
               return syncBuffer != null && syncBuffer.getFieldType() == 2;
            } else {
               return false;
            }
         } else {
            GroupAddressCardModel gacm = (GroupAddressCardModel)object;
            int size = gacm.size();

            for (int i = 0; i < size; i++) {
               if (gacm.getAddressModelTypeAt(i) == 2) {
                  return true;
               }
            }

            return false;
         }
      } else {
         PhoneNumberModel pnm = (PhoneNumberModel)object;
         return this._typeIndex == 0 || pnm._type == this._typeIndex;
      }
   }

   @Override
   public Verb[] getVerbs(Object context) {
      return new Verb[]{new AddPhoneNumberVerb(this, this._ordering, this._typeIndex)};
   }
}
