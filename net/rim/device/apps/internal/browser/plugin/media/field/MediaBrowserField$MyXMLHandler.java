package net.rim.device.apps.internal.browser.plugin.media.field;

import org.xml.sax.helpers.DefaultHandler;

final class MediaBrowserField$MyXMLHandler extends DefaultHandler {
   private String _parsedString;

   @Override
   public final void characters(char[] ch, int start, int length) {
      this._parsedString = (String)(new Object(ch, start, length));
   }

   public final String getParsedString() {
      return this._parsedString;
   }
}
