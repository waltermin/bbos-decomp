package net.rim.device.apps.internal.browser.common;

import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.core.AcceptValueProviderRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class RenderingConverterDescriptor implements ConverterDescriptor, AcceptValueProvider {
   private RenderingConverter _converter;
   public static final long ACCEPT_VALUE_PROVIDERS_REGISTRY_KEY = 8967752585940069864L;
   public static final String RENDERING_CONTEXT = "net.rim.device.apps.internal.rendering";

   public RenderingConverterDescriptor(RenderingConverter converter) {
      this._converter = converter;
   }

   public final void register() {
      if (this._converter != null) {
         BrowserContentProvider provider = this._converter.getBrowserContentConverter();
         String[] acceptData = provider.getSupportedMimeTypes();
         if (acceptData != null) {
            int length = acceptData.length;
            String[] typesToRegister = new Object[length];

            for (int index = 0; index < length; index++) {
               String item = acceptData[index];
               int indexOfSemiColon = item.indexOf(59);
               item = StringUtilities.toLowerCase(indexOfSemiColon == -1 ? item : item.substring(0, indexOfSemiColon), 1701707776);
               Converter converter = SerializationManager.getConverter(item, "net.rim.device.apps.internal.rendering");
               if (converter != null) {
                  String[] str = new Object[]{item};
                  String message = MessageFormat.format(BrowserResources.getString(806), str);
                  throw new Object(message);
               }

               typesToRegister[index] = item;
            }

            for (int index = 0; index < length; index++) {
               try {
                  SerializationManager.registerConverter(typesToRegister[index], this);
               } finally {
                  continue;
               }
            }

            AcceptValueProviderRegistry.getInstance(8967752585940069864L).registerAcceptValueProvider(this);
         }

         if (provider instanceof ProtocolContentProvider) {
            String[] protocols = ((ProtocolContentProvider)provider).getAcceptProtocols();
            int length = protocols != null ? protocols.length : 0;

            for (int index = 0; index < length; index++) {
               try {
                  SerializationManager.registerConverter(protocols[index], this);
               } finally {
                  continue;
               }
            }
         }
      }
   }

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      return true;
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return true;
   }

   @Override
   public final Converter createConverterInstance(String typeString) {
      return this._converter;
   }

   @Override
   public final Object getContext() {
      return "net.rim.device.apps.internal.rendering";
   }

   @Override
   public final String[] getAccept(Object context) {
      return context instanceof Object
         ? this._converter.getBrowserContentConverter().getAccept((RenderingOptions)context)
         : this._converter.getBrowserContentConverter().getAccept(null);
   }
}
