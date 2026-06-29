package net.rim.device.apps.internal.blackberryemail.address;

import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.component.ActiveFieldContext;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class AddressModelFactory extends RIMModelFactory {
   private int _type;

   AddressModelFactory(int type) {
      this._type = type;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object createInstance(Object initialData) {
      RIMModel model = null;
      String value = null;
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

         label117:
         try {
            value = syncBuffer.getString(this._type, true);
         } finally {
            break label117;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         value = (String)data.elementAt(1);
      } else if (ContextObject.getFlag(initialData, 55) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         DataBuffer dataBuffer = syncBuffer.getDataBuffer();
         int pos = dataBuffer.getPosition();
         boolean var10 = false /* VF: Semaphore variable */;

         label103:
         try {
            var10 = true;
            dataBuffer.readShort();
            dataBuffer.readByte();
            value = ConverterUtilities.readString(dataBuffer);
            var10 = false;
         } finally {
            if (var10) {
               dataBuffer.setPosition(pos);
               break label103;
            }
         }
      } else {
         model = this.localCreateInstance(initialData);
      }

      if (value != null) {
         model = this.localCreateInstance(value);
      }

      return model;
   }

   private final RIMModel localCreateInstance(Object data) {
      switch (this._type) {
         case 1:
            return new EmailAddressModelImpl(data);
         case 10:
            return new PINAddressModelImpl(data);
         default:
            return null;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MIN_VALUE;
   }

   @Override
   public final int getMaximumCount(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         switch (this._type) {
            case 1:
               return 3;
            case 10:
               return 1;
         }
      }

      return Integer.MAX_VALUE;
   }

   @Override
   public final boolean recognize(Object object) {
      if (this._type == 1 && object instanceof EmailAddressModel) {
         return true;
      }

      if (this._type == 10 && object instanceof PINAddressModel) {
         return true;
      }

      if (!(object instanceof GroupAddressCardModel)) {
         if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
            return syncBuffer != null && syncBuffer.getFieldType(true) == this._type;
         } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
            Vector data = (Vector)ContextObject.get(object, 249);
            String name = this._type == 1 ? "Email" : "PIN";
            return data != null && data.size() > 0 && data.elementAt(0).equals(name);
         } else {
            return false;
         }
      } else {
         GroupAddressCardModel gacm = (GroupAddressCardModel)object;
         int size = gacm.size();

         for (int i = 0; i < size; i++) {
            byte modelType = gacm.getAddressModelTypeAt(i);
            if (this._type == 1 && modelType == 0) {
               return true;
            }

            if (this._type == 10 && modelType == 1) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      switch (this._type) {
         case 1:
            return new Verb[]{NewAddressVerb.newEmailAddressVerb(this)};
         case 10:
            return new Verb[]{NewAddressVerb.newPINAddressVerb(this)};
         default:
            return null;
      }
   }
}
