package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.RadioInfo;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;

final class ESBlackberry extends ESObject {
   private ESBlackberryLocation _location;

   public ESBlackberry(JavaScriptEngine engine) {
      this.setGrowthIncrement(2);
      this.addField(Names.network, 5, this.getNetworkType());
      this._location = new ESBlackberryLocation(engine);
      this.addField(Names.location, 5, Value.makeObjectValue(this._location));
   }

   private final long getNetworkType() {
      StringBuffer buffer = new StringBuffer();
      int wafs = RadioInfo.getSupportedWAFs();
      if ((wafs & 1) != 0) {
         buffer.append("3GPP");
      }

      if ((wafs & 2) != 0) {
         if (buffer.length() > 0) {
            buffer.append(',');
         }

         buffer.append("CDMA");
      }

      if ((wafs & 8) != 0) {
         if (buffer.length() > 0) {
            buffer.append(',');
         }

         buffer.append("iDEN");
      }

      if ((wafs & 4) != 0) {
         if (buffer.length() > 0) {
            buffer.append(',');
         }

         buffer.append("Wi-Fi");
      }

      return buffer.length() > 0 ? Value.makeStringValue(buffer.toString()) : Value.NULL;
   }

   final void documentClosed() {
      this._location.documentClosed();
   }
}
