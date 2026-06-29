package net.rim.device.apps.api.utility.serialization;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Array;

public final class SerializationManager {
   private Hashtable _descriptors = (Hashtable)(new Object(60));
   private static SerializationManager _instance;
   private static final long APPLICATION_REGISTRY_NAME;

   private SerializationManager() {
   }

   public static final void deregisterConverter(String typeString, Object contextObject) {
      Hashtable descriptors = getSerializationManager()._descriptors;
      synchronized (descriptors) {
         ConverterDescriptor[] array = (ConverterDescriptor[])descriptors.get(typeString);
         if (array != null) {
            int descriptorIndex = -1;
            ConverterDescriptor descriptor = null;

            for (int index = 0; index < array.length; index++) {
               descriptor = array[index];
               if (descriptor.getContext().equals(contextObject)) {
                  descriptorIndex = index;
                  break;
               }
            }

            if (descriptorIndex != -1) {
               if (array.length == 1) {
                  descriptors.remove(typeString);
               } else {
                  int shiftedIndex = 0;
                  ConverterDescriptor[] oldArray = array;
                  array = new ConverterDescriptor[oldArray.length - 1];

                  for (int index = 0; index < oldArray.length; index++) {
                     if (index != descriptorIndex) {
                        array[shiftedIndex++] = oldArray[index];
                     }
                  }

                  descriptors.put(typeString, array);
               }
            }
         }
      }
   }

   public static final Converter findConverter(byte[] inputBytes) {
      ConverterDescriptor[] array = null;
      ConverterDescriptor descriptor = null;
      Hashtable descriptors = getSerializationManager()._descriptors;
      String key = null;
      synchronized (descriptors) {
         Enumeration keys = descriptors.keys();

         while (keys.hasMoreElements()) {
            key = (String)keys.nextElement();
            array = (ConverterDescriptor[])descriptors.get(key);

            for (int index = 0; index < array.length; index++) {
               descriptor = array[index];
               if (descriptor.canConvert(inputBytes, descriptor.getContext())) {
                  return getConverter(descriptor, key);
               }
            }
         }

         return null;
      }
   }

   public static final Converter findConverter(Object inputObject) {
      ConverterDescriptor[] array = null;
      ConverterDescriptor descriptor = null;
      Hashtable descriptors = getSerializationManager()._descriptors;
      String key = null;
      synchronized (descriptors) {
         Enumeration keys = descriptors.keys();

         while (keys.hasMoreElements()) {
            key = (String)keys.nextElement();
            array = (ConverterDescriptor[])descriptors.get(key);

            for (int index = 0; index < array.length; index++) {
               descriptor = array[index];
               if (descriptor.canConvert(inputObject, descriptor.getContext())) {
                  return getConverter(descriptor, key);
               }
            }
         }

         return null;
      }
   }

   public static final Converter getConverter(String typeString, Object contextObject) {
      Hashtable descriptors = getSerializationManager()._descriptors;
      ConverterDescriptor descriptor = null;
      synchronized (descriptors) {
         ConverterDescriptor[] array = (ConverterDescriptor[])descriptors.get(typeString);
         if (array != null) {
            for (int index = 0; index < array.length; index++) {
               descriptor = array[index];
               if (descriptor.getContext().equals(contextObject)) {
                  break;
               }

               descriptor = null;
            }
         }
      }

      return descriptor == null ? null : getConverter(descriptor, typeString);
   }

   private static final Converter getConverter(ConverterDescriptor aConverterDescriptor, String typeString) {
      return aConverterDescriptor.createConverterInstance(typeString);
   }

   private static final SerializationManager getSerializationManager() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (SerializationManager)applicationRegistry.getOrWaitFor(-531295951157606877L);
         if (_instance == null) {
            _instance = new SerializationManager();
            applicationRegistry.put(-531295951157606877L, _instance);
         }
      }

      return _instance;
   }

   public static final void registerConverter(String typeString, ConverterDescriptor aConverterDescriptor) {
      Hashtable descriptors = getSerializationManager()._descriptors;
      synchronized (descriptors) {
         ConverterDescriptor[] array = (ConverterDescriptor[])descriptors.get(typeString);
         if (array != null) {
            boolean updated = false;
            ConverterDescriptor descriptor = null;

            for (int index = 0; index < array.length; index++) {
               descriptor = array[index];
               if (descriptor.getContext().equals(aConverterDescriptor.getContext())) {
                  array[index] = aConverterDescriptor;
                  updated = true;
                  break;
               }
            }

            if (!updated) {
               int length = array.length;
               Array.resize(array, length + 1);
               array[length] = aConverterDescriptor;
            }
         } else {
            array = new ConverterDescriptor[]{aConverterDescriptor};
            descriptors.put(typeString, array);
         }
      }
   }
}
