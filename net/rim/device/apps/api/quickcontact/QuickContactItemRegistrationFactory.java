package net.rim.device.apps.api.quickcontact;

import java.util.Enumeration;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class QuickContactItemRegistrationFactory extends RIMModelFactory {
   private static final long QUICK_CONTACT_ITEM_FACTORIES_GUID = 931849679150063665L;
   private static LongHashtable _factories = ApplicationRegistry.getApplicationRegistry().getLongHashtable(931849679150063665L);

   QuickContactItemRegistrationFactory() {
      _factories = (LongHashtable)(new Object());
   }

   public static void registerQuickContactItemFactory(long factoryId, Object factory) {
      if (factory instanceof QuickContactItemFactory) {
         synchronized (_factories) {
            _factories.put(factoryId, factory);
         }
      }
   }

   public static void deregisterQuickContactItemFactory(long factoryId) {
      synchronized (_factories) {
         _factories.remove(factoryId);
      }
   }

   public static QuickContactItemFactory[] getFactories() {
      QuickContactItemFactory[] factories = new QuickContactItemFactory[_factories.size()];
      Enumeration enumeration;
      synchronized (_factories) {
         enumeration = _factories.elements();
      }

      for (int index = 0; enumeration.hasMoreElements(); index++) {
         factories[index] = (QuickContactItemFactory)enumeration.nextElement();
      }

      return factories;
   }

   private static RIMModelFactory findRecognizingFactory(Object o) {
      synchronized (_factories) {
         Enumeration elems = _factories.elements();

         while (elems.hasMoreElements()) {
            Object elem = elems.nextElement();
            if (elem instanceof Object && ((Recognizer)elem).recognize(o)) {
               return (RIMModelFactory)elem;
            }
         }

         return null;
      }
   }

   @Override
   public boolean recognize(Object o) {
      if (findRecognizingFactory(o) != null) {
         return true;
      } else if (ContextObject.getFlag(o, 79) && ContextObject.getFlag(o, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(o, 255);
         return syncBuffer != null && syncBuffer.getFieldType() == 1;
      } else {
         return false;
      }
   }

   @Override
   public Object createInstance(Object data) {
      if (ContextObject.getFlag(data, 79) && ContextObject.getFlag(data, 19)) {
         SyncBuffer buf = (SyncBuffer)ContextObject.get(data, 255);
         if (buf == null) {
            return null;
         }

         Factory f = findRecognizingFactory(data);
         if (f != null) {
            return f.createInstance(data);
         }

         try {
            int type = ConverterUtilities.getType(buf.getDataBuffer());
            if (type == 1) {
               DataBuffer dataBuffer = buf.getDataBuffer();
               ConverterUtilities.skipField(dataBuffer);
               return QuickContactItem.getGhostItem();
            }
         } finally {
            return null;
         }
      }

      return null;
   }
}
