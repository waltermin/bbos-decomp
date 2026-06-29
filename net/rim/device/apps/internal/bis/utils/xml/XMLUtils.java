package net.rim.device.apps.internal.bis.utils.xml;

import java.io.OutputStream;

public final class XMLUtils {
   private static final String UTF_ENCODING = "UTF-8";

   public static final void startElement(OutputStream ostream, String tag) {
      ostream.write(("<" + tag + ">").getBytes("UTF-8"));
   }

   public static final void endElement(OutputStream ostream, String tag) {
      ostream.write(("</" + tag + ">").getBytes("UTF-8"));
   }

   public static final void writeSimpleElement(OutputStream ostream, String tag, Object value) {
      startElement(ostream, tag);
      ostream.write(value.toString().getBytes("UTF-8"));
      endElement(ostream, tag);
   }
}
