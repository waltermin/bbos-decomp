package net.rim.device.apps.internal.browser.common;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.PageConverter;

public class BrowserConverterDescriptor implements ConverterDescriptor, AcceptValueProvider {
   private Converter _converter;
   private String[] _acceptData;
   public static final String BROWSER_CONTEXT = "net.rim.device.apps.internal.browser";

   public BrowserConverterDescriptor(Converter converter, String[] acceptData) {
      this._converter = converter;
      this._acceptData = acceptData;
   }

   public void register() {
      if (this._acceptData != null) {
         label38:
         try {
            int length = this._acceptData.length;

            for (int index = 0; index < length; index++) {
               String item = this._acceptData[index];
               int indexOfSemiColon = item.indexOf(59);
               item = StringUtilities.toLowerCase(indexOfSemiColon == -1 ? item : item.substring(0, indexOfSemiColon), 1701707776);
               SerializationManager.registerConverter(item, this);
            }
         } finally {
            break label38;
         }
      }

      BrowserDaemonRegistry.getInstance().registerAcceptValueProvider(this);
   }

   public static Converter getConverter(String contentType) {
      Converter converter = null;
      if (contentType != null && contentType.length() > 0) {
         converter = SerializationManager.getConverter(StringUtilities.toLowerCase(contentType, 1701707776), "net.rim.device.apps.internal.browser");
      }

      if (converter == null) {
         converter = PageConverter.getInstance();
      }

      return converter;
   }

   @Override
   public String[] getAccept(Object context) {
      return this._acceptData;
   }

   @Override
   public boolean canConvert(byte[] inputBytes, Object contextObject) {
      return true;
   }

   @Override
   public boolean canConvert(Object inputObject, Object contextObject) {
      return true;
   }

   @Override
   public Converter createConverterInstance(String typeString) {
      return this._converter;
   }

   @Override
   public Object getContext() {
      return "net.rim.device.apps.internal.browser";
   }
}
