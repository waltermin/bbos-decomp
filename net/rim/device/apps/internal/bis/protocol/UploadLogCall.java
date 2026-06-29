package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class UploadLogCall implements XMLCall {
   private String[] _loglines;
   private String[] _timestamps;

   UploadLogCall(String[] loglines, String[] timestamps) {
      this._loglines = loglines;
      this._timestamps = timestamps;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      int numLogLines = this._loglines.length;
      XMLUtils.startElement(ostream, "log");

      for (int i = 0; i < numLogLines; i++) {
         XMLUtils.startElement(ostream, "logline");
         XMLUtils.writeSimpleElement(ostream, "timestamp", this._timestamps[i]);
         XMLUtils.writeSimpleElement(ostream, "text", this._loglines[i]);
         XMLUtils.endElement(ostream, "logline");
      }

      XMLUtils.endElement(ostream, "log");
   }
}
