package net.rim.device.apps.internal.commonmodels.title;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class TitleModelFactory extends RIMModelFactory {
   private static TitleModelFactory _singleInstance = new TitleModelFactory();

   private TitleModelFactory() {
   }

   public static TitleModelFactory getInstance() {
      return _singleInstance;
   }

   @Override
   public Object createInstance(Object initialData) {
      TitleModel model = null;
      if (ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);

         try {
            int fieldId = getSyncFieldId(initialData);
            if (fieldId != -1) {
               return new TitleModelImpl(syncBuffer.getString(fieldId, true));
            }
         } finally {
            ;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         int size = data.size();
         model = new TitleModelImpl(null);

         for (int i = 0; i < size; i += 2) {
            String label = (String)data.elementAt(i);
            String value = (String)data.elementAt(i + 1);
            if (label.equals("Title")) {
               model.setTitle(value);
               if (i != 0) {
                  data.removeElementAt(i);
                  data.removeElementAt(i);
                  if (model.getTitle() == null || model.getTitle().length() == 0) {
                     return null;
                  }
                  break;
               }
            }
         }
      } else {
         if (initialData instanceof ContextObject) {
            return new TitleModelImpl(null);
         }

         model = new TitleModelImpl(initialData);
      }

      return model;
   }

   static final int getSyncFieldId(Object context) {
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(28)) {
            return 2;
         }

         if (contextObject.getFlag(35) || contextObject.getFlag(8)) {
            return 1;
         }

         if (contextObject.getFlag(22)) {
            return 1;
         }

         if (contextObject.getFlag(11)) {
            return 42;
         }

         if (contextObject.getFlag(33)) {
            return 1;
         }
      }

      return -1;
   }

   @Override
   public int getMinimumCount(Object context) {
      return getSyncFieldId(context) != -1 ? 1 : Integer.MIN_VALUE;
   }

   @Override
   public int getMaximumCount(Object context) {
      return getSyncFieldId(context) != -1 ? 1 : Integer.MAX_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (object instanceof TitleModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         int fieldId = getSyncFieldId(object);
         if (fieldId != -1) {
            if (fieldId == syncBuffer.getFieldType(true)) {
               return true;
            }

            return false;
         }
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         if (data != null && data.size() > 0) {
            Object label = data.elementAt(0);
            return label.equals("Title");
         }
      }

      return false;
   }
}
