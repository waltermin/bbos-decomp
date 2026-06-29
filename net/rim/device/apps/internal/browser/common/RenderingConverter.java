package net.rim.device.apps.internal.browser.common;

import java.io.DataInput;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

public final class RenderingConverter extends BaseConverter {
   private BrowserContentProvider _browserFieldProvider;

   public RenderingConverter(BrowserContentProvider browserFieldProvider) {
      this._browserFieldProvider = browserFieldProvider;
   }

   public final BrowserContentProvider getBrowserContentConverter() {
      return this._browserFieldProvider;
   }

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      return !(contextObject instanceof BrowserContentProviderContext) ? null : this.convert((BrowserContentProviderContext)contextObject);
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) {
      return !(contextObject instanceof BrowserContentProviderContext) ? null : this.convert((BrowserContentProviderContext)contextObject);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final Object convert(BrowserContentProviderContext providerContext) throws SerializationException {
      try {
         return this._browserFieldProvider.getBrowserContent(providerContext);
      } catch (Throwable var4) {
         throw new SerializationException(e.getMessage());
      }
   }
}
