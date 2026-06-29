package net.rim.device.apps.internal.commonmodels.body;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.RIMModelFactoryCreateVerb;
import net.rim.device.apps.api.framework.verb.Verb;

class BodyModelFactory extends RIMModelFactory {
   @Override
   public Object createInstance(Object initialData) {
      BodyModelImpl model = null;
      String text = null;
      if (ContextObject.getFlag(initialData, 19)) {
         label68:
         try {
            int id = getSyncFieldId(initialData);
            int maxLength = -1;
            if (id == 2) {
               maxLength = 8191;
            }

            if (id != -1) {
               text = ((SyncBuffer)ContextObject.get(initialData, 255)).getString(id, true);
               if (text != null && maxLength != -1 && text.length() > maxLength) {
                  text = text.substring(0, maxLength);
               }
            }
         } finally {
            break label68;
         }
      } else if (ContextObject.getFlag(initialData, 11)
         && ContextObject.getFlag(initialData, 43)
         && ContextObject.getFlag(initialData, 54)
         && ContextObject.get(initialData, 249) != null) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         text = (String)data.elementAt(1);
      } else {
         model = new BodyModelImpl(initialData);
      }

      if (text != null && text.length() > 0) {
         return new BodyModelImpl(text);
      }

      if (text == null && ContextObject.getFlag(initialData, 20)) {
         model = new BodyModelImpl(null);
      }

      return model;
   }

   static int getSyncFieldId(Object context) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(11)) {
            return 64;
         }

         if (contextObject.getFlag(35)) {
            return 2;
         }

         if (contextObject.getFlag(28)) {
            return 3;
         }

         if (contextObject.getFlag(20)) {
            return 8;
         }
      }

      return -1;
   }

   @Override
   public int getMaximumCount(Object context) {
      switch (getSyncFieldId(context)) {
         case 2:
         case 3:
         case 64:
            return 1;
         default:
            return Integer.MAX_VALUE;
      }
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof BodyModelImpl) {
         return true;
      }

      if (ContextObject.getFlag(object, 19)) {
         int fieldId = getSyncFieldId(object);
         if (fieldId != -1) {
            if (fieldId == ((SyncBuffer)ContextObject.get(object, 255)).getFieldType(true)) {
               return true;
            }

            return false;
         }
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         if (data != null && data.size() > 0 && data.elementAt(0).equals("Notes")) {
            return true;
         }

         return false;
      }

      return false;
   }

   @Override
   public Verb[] getVerbs(Object context) {
      return ContextObject.getFlag(context, 11)
         ? new Verb[]{new RIMModelFactoryCreateVerb(this, 16865280, -8414468493733347764L, "net.rim.device.apps.internal.resource.Common", 1790)}
         : null;
   }
}
