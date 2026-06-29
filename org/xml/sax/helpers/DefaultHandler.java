package org.xml.sax.helpers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class DefaultHandler implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler {
   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return null;
   }

   @Override
   public void notationDecl(String name, String publicId, String systemId) {
   }

   @Override
   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
   }

   @Override
   public void setDocumentLocator(Locator locator) {
   }

   @Override
   public void startDocument() {
   }

   @Override
   public void endDocument() {
   }

   @Override
   public void startPrefixMapping(String prefix, String uri) {
   }

   @Override
   public void endPrefixMapping(String prefix) {
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
   }

   @Override
   public void characters(char[] ch, int start, int length) {
   }

   @Override
   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   @Override
   public void processingInstruction(String target, String data) {
   }

   @Override
   public void skippedEntity(String name) {
   }

   @Override
   public void warning(SAXParseException e) {
   }

   @Override
   public void error(SAXParseException e) {
   }

   @Override
   public void fatalError(SAXParseException e) {
      throw e;
   }
}
