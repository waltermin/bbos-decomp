package net.rim.device.apps.internal.browser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

final class XMLRenderingConverter$HelperHandler extends DefaultHandler {
   String _defaultNamespace;
   String _contentType;

   private XMLRenderingConverter$HelperHandler() {
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      this._contentType = XMLConverterRegistry.getInstance().getContentType(localName);
      if (this._contentType == null && this._defaultNamespace != null) {
         this._contentType = XMLConverterRegistry.getInstance().getContentType(this._defaultNamespace);
      }

      throw new Object("found it");
   }

   @Override
   public final void startPrefixMapping(String prefix, String uri) {
      if (prefix != null && prefix.length() == 0) {
         this._defaultNamespace = uri;
      }
   }

   XMLRenderingConverter$HelperHandler(XMLRenderingConverter$1 x0) {
      this();
   }
}
