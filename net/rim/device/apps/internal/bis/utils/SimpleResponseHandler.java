package net.rim.device.apps.internal.bis.utils;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class SimpleResponseHandler extends DefaultHandler {
   private String _response;
   private boolean _foundResponseTag;
   private static final String TAG_RESPONSE;

   @Override
   public final void characters(char[] ch, int start, int length) {
      if (this._foundResponseTag) {
         this._response = (String)(ch != null && ch.length > 0 ? new Object(ch, start, length) : null);
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      if ("response".equals(qName)) {
         this._foundResponseTag = true;
      }
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
   }
}
