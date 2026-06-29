package net.rim.device.apps.api.framework.model;

import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class RIMModelFactoryCache {
   public static RIMModelFactory[] allocate() {
      return new RIMModelFactory[256];
   }

   public static Object checkAndCreateSubModel(
      RIMModelFactory[] cache, RIMModelFactory[] factories, RIMModelFactory default_factory, SyncBuffer sync_buffer, Object initial_data
   ) {
      int position = sync_buffer.getPosition();
      int field_type = sync_buffer.getFieldType();
      RIMModelFactory factory = cache[field_type];
      if (default_factory == null) {
         default_factory = new EmptyRIMModelFactory();
      }

      if (factory == null || !factory.recognize(initial_data)) {
         int n = factories.length;
         int i = 0;

         while (true) {
            if (i >= n) {
               if (default_factory == null) {
                  return null;
               }

               sync_buffer.setPosition(position);
               if (!default_factory.recognize(initial_data)) {
                  return null;
               }

               factory = default_factory;
               break;
            }

            factory = factories[i];
            sync_buffer.setPosition(position);
            if (factory.recognize(initial_data)) {
               cache[field_type] = factory;
               break;
            }

            i++;
         }
      }

      return factory.createInstance(initial_data);
   }

   public static void addToModelWithCache(
      RIMModelFactory[] cache,
      RIMModelFactory[] factories,
      RIMModelFactory default_factory,
      SyncBuffer sync_buffer,
      WritableSet model,
      ContextObject initial_data
   ) {
      while (!sync_buffer.isEmpty()) {
         int position = sync_buffer.getPosition();
         int fieldType = sync_buffer.getFieldType();
         if (fieldType != 255) {
            Object m = checkAndCreateSubModel(cache, factories, default_factory, sync_buffer, initial_data);
            if (m != null) {
               model.add(m);
            }
         }

         sync_buffer.setPosition(position);
         sync_buffer.skipField();
      }
   }
}
