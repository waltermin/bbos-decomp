package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class GranularPolicy$XMLToGranularPolicyHandler extends DefaultHandler {
   private GranularPolicyElement _currentElement;
   StringBuffer _characterAccumulator;

   private GranularPolicy$XMLToGranularPolicyHandler(GranularPolicy granularPolicy) {
      this._currentElement = granularPolicy;
      this._characterAccumulator = new StringBuffer();
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      this._currentElement = this._currentElement.handleStartElement(localName, attributes);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      this._characterAccumulator.append(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      this._currentElement = this._currentElement.handleEndElement(localName, this._characterAccumulator);
      this._characterAccumulator.setLength(0);
   }

   GranularPolicy$XMLToGranularPolicyHandler(GranularPolicy x0, GranularPolicy$1 x1) {
      this(x0);
   }
}
