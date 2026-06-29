package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.common.AcceptValueProvider;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.multipart.MultipartConverter;

public final class WAPPushConverterDescriptor implements ConverterDescriptor, AcceptValueProvider {
   private static final String[] ACCEPT = new String[]{
      "application/vnd.wap.coc",
      "application/vnd.wap.multipart.mixed",
      "application/vnd.wap.multipart.alternative",
      "application/vnd.wap.multipart.related",
      "multipart/mixed",
      "multipart/alternative",
      "multipart/related",
      "application/vnd.wap.slc",
      "application/vnd.wap.sic",
      "text/vnd.wap.co",
      "text/vnd.wap.sl",
      "text/vnd.wap.si"
   };

   public final void register() {
      label28:
      try {
         for (int i = 0; i < ACCEPT.length; i++) {
            SerializationManager.registerConverter(ACCEPT[i], this);
         }
      } finally {
         break label28;
      }

      BrowserDaemonRegistry.getInstance().registerAcceptValueProvider(this);
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return false;
   }

   @Override
   public final Converter createConverterInstance(String typeString) {
      switch (typeString.hashCode()) {
         case -2010642142:
         case -1741069880:
         case -407316790:
         case -261887696:
         case 1231688920:
         case 1745846304:
            return new MultipartConverter("net.rim.device.apps.internal.browser.wappush");
         case -1459306984:
            return new PushTextConverter(2);
         case -1459306494:
            return new PushTextConverter(0);
         case -1459306491:
            return new PushTextConverter(1);
         case 2034936046:
            return new COCConverter();
         case 2034951236:
            return new SICConverter();
         case 2034951329:
            return new SLCConverter();
         default:
            return null;
      }
   }

   @Override
   public final Object getContext() {
      return "net.rim.device.apps.internal.browser.wappush";
   }

   @Override
   public final String[] getAccept(Object context) {
      return ACCEPT;
   }

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      return true;
   }
}
