package net.rim.device.apps.internal.browser.dd;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

final class DownloadDescriptorXMLHandler extends DefaultHandler {
   private DownloadDescriptorAttributes _ddAttributes;
   private Stack _elementStack = new Stack();

   public DownloadDescriptorXMLHandler(DownloadDescriptorAttributes ddAttributes) {
      this._ddAttributes = ddAttributes;
   }

   @Override
   public final void characters(char[] ch, int start, int length) {
      if (!this._elementStack.empty()) {
         this._ddAttributes.setAttribute((String)this._elementStack.peek(), ch, start, length);
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      this._elementStack.push(localName);
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
      this._elementStack.pop();
   }
}
