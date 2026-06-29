package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;

public final class BrowserPushConverterDescriptor implements ConverterDescriptor {
   static final String PUSH_TYPE_MESSAGE_VALUE = "browser-message";
   static final String PUSH_TYPE_CHANNEL_VALUE = "browser-channel";
   static final String PUSH_TYPE_CHANNEL_DELETE_VALUE = "browser-channel-delete";
   static final String PUSH_TYPE_CONTENT_VALUE = "browser-content";
   static final String PUSH_TYPE_STORE_VALUE = "store";

   public final void register() {
      try {
         SerializationManager.registerConverter("browser-message", this);
         SerializationManager.registerConverter("browser-channel", this);
         SerializationManager.registerConverter("browser-channel-delete", this);
         SerializationManager.registerConverter("browser-content", this);
         SerializationManager.registerConverter("store", this);
      } finally {
         return;
      }
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return false;
   }

   @Override
   public final Converter createConverterInstance(String pushType) {
      if (StringUtilities.strEqualIgnoreCase(pushType, "browser-message", 1701707776)) {
         return new BrowserMessageConverter();
      } else if (StringUtilities.strEqualIgnoreCase(pushType, "browser-channel", 1701707776)) {
         return new BrowserChannelConverter();
      } else if (StringUtilities.strEqualIgnoreCase(pushType, "browser-channel-delete", 1701707776)) {
         return new BrowserChannelDeleteConverter();
      } else if (StringUtilities.strEqualIgnoreCase(pushType, "browser-content", 1701707776)) {
         return new BrowserContentConverter();
      } else {
         return StringUtilities.strEqualIgnoreCase(pushType, "store", 1701707776) ? new StoreConverter() : null;
      }
   }

   @Override
   public final Object getContext() {
      return "net.rim.device.apps.internal.browser.wappush";
   }

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      return true;
   }
}
