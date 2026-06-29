package net.rim.device.apps.internal.mms.plugin;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.mms.MMSPolicy;
import net.rim.device.apps.internal.mms.MMSPolicy$ChangeListener;

final class MMSPushConverterDescriptor implements ConverterDescriptor, MMSPolicy$ChangeListener {
   public static final String DEFAULT_CONTEXT;

   public final void startListening() {
      MMSPolicy.addListener(this);
   }

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      return true;
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return false;
   }

   @Override
   public final Converter createConverterInstance(String typeString) {
      if (typeString != null) {
         String normalizedTypeString = MIMETypeAssociations.getNormalizedType(typeString);
         if ("application/vnd.wap.mms-message".equals(normalizedTypeString)) {
            return new MMSPushConverter();
         }
      }

      return null;
   }

   @Override
   public final Object getContext() {
      return "net.rim.device.apps.internal.browser.wappush";
   }

   @Override
   public final void onPolicyEnabled() {
      try {
         SerializationManager.registerConverter("application/vnd.wap.mms-message", this);
      } finally {
         return;
      }
   }

   @Override
   public final void onPolicyDisabled() {
      try {
         SerializationManager.deregisterConverter("application/vnd.wap.mms-message", this);
      } finally {
         return;
      }
   }
}
