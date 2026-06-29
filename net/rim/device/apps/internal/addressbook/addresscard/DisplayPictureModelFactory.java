package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Vector;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

final class DisplayPictureModelFactory extends RIMModelFactory implements VerbFactory {
   @Override
   public final Object createInstance(Object initialData) {
      if (!ContextObject.getFlag(initialData, 11)) {
         return initialData != null ? new DisplayPictureModelImpl(initialData) : null;
      }

      label76:
      if (ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            initialData = syncBuffer.getBytes(77, true);
            return initialData != null ? new DisplayPictureModelImpl(initialData) : null;
         } finally {
            return initialData != null ? new DisplayPictureModelImpl(initialData) : null;
         }
      } else {
         if (ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
            Vector data = (Vector)ContextObject.get(initialData, 249);
            if (data != null && data.size() >= 1) {
               Object element = data.elementAt(1);
               if (element instanceof String) {
                  try {
                     initialData = Base64InputStream.decode((String)element);
                  } finally {
                     ;
                  }
               }
            }
         }
         break label76;
      }
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof DisplayPictureModelImpl) {
         return true;
      }

      if (ContextObject.getFlag(object, 11)) {
         if (ContextObject.getFlag(object, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
            if (syncBuffer != null && syncBuffer.getFieldType(true) == 77) {
               return true;
            }

            return false;
         }

         if (ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
            Vector data = (Vector)ContextObject.get(object, 249);
            if (data.elementAt(0).equals("Photo")) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      Verb[] verbs = new Verb[0];
      Object selectedElement = ContextObject.get(context, 252);
      if (selectedElement instanceof AddressCardModel) {
         Array.resize(verbs, 1);
         verbs[0] = new DisplayPictureVerb(3, 16864581);
         return verbs;
      }

      if (ContextObject.getFlag(context, 0)) {
         Array.resize(verbs, 1);
         verbs[0] = new DisplayPictureVerb(0, 16864581);
      }

      return verbs;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MAX_VALUE;
   }
}
