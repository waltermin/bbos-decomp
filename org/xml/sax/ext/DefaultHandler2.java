package org.xml.sax.ext;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultHandler2 extends DefaultHandler implements LexicalHandler, DeclHandler, EntityResolver2 {
   @Override
   public void startCDATA() {
   }

   @Override
   public void endCDATA() {
   }

   @Override
   public void startDTD(String name, String publicId, String systemId) {
   }

   @Override
   public void endDTD() {
   }

   @Override
   public void startEntity(String name) {
   }

   @Override
   public void endEntity(String name) {
   }

   @Override
   public void comment(char[] ch, int start, int length) {
   }

   @Override
   public void attributeDecl(String eName, String aName, String type, String mode, String value) {
   }

   @Override
   public void elementDecl(String name, String model) {
   }

   @Override
   public void externalEntityDecl(String name, String publicId, String systemId) {
   }

   @Override
   public void internalEntityDecl(String name, String value) {
   }

   @Override
   public InputSource getExternalSubset(String name, String baseURI) {
      return null;
   }

   @Override
   public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) {
      return null;
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return this.resolveEntity(null, publicId, null, systemId);
   }
}
