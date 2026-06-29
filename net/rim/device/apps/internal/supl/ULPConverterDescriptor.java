package net.rim.device.apps.internal.supl;

import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.internal.browser.common.AcceptValueProvider;

public final class ULPConverterDescriptor implements ConverterDescriptor, AcceptValueProvider {
   private static final String[] ACCEPT = new String[]{"application/vnd.omaloc-supl-init"};
   private static final String[] ACCEPT_CHARSET = new String[]{"UTF-8"};

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
      return new ULPConverter();
   }

   @Override
   public final Object getContext() {
      return "net.rim.device.apps.internal.browser.wappush";
   }

   @Override
   public final String[] getAccept(Object context) {
      return ACCEPT;
   }
}
