package net.rim.device.apps.internal.implus;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class IMPlusAddressModelFactory extends RIMModelFactory {
   private int _type;

   public IMPlusAddressModelFactory(int type) {
      this._type = type;
   }

   @Override
   public final Object createInstance(Object initialData) {
      RIMModel model = null;
      String value = null;
      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         label50:
         try {
            value = syncBuffer.getString(this._type, true);
         } finally {
            break label50;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         value = (String)data.elementAt(1);
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
         case 3:
            return null;
         case 4:
            return new OneWayPagerAddressModelImpl(data);
         case 5:
         default:
            return new InteractiveHHAddressModelImpl(data);
      }
   }

   @Override
   public final boolean recognize(Object object) {
      if ((this._type != 5 || !(object instanceof Object)) && (this._type != 4 || !(object instanceof Object))) {
         if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
            return syncBuffer != null && syncBuffer.getFieldType() == this._type;
         }

         if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
            Vector data = (Vector)ContextObject.get(object, 249);
            String name = "";
            switch (this._type) {
               case 3:
                  break;
               case 4:
                  name = "1-way Pager";
                  break;
               case 5:
               default:
                  name = "Interactive Handheld";
            }

            return data != null && data.size() > 0 && data.elementAt(0).equals(name);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MIN_VALUE;
   }

   @Override
   public final int getMaximumCount(Object context) {
      int returnVal = Integer.MAX_VALUE;
      if (ContextObject.getFlag(context, 11)) {
         switch (this._type) {
            case 3:
               break;
            case 4:
               returnVal = 1;
               break;
            case 5:
            default:
               return 1;
         }
      }

      return returnVal;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      switch (this._type) {
         default:
            return null;
      }
   }
}
