package net.rim.device.api.xml.parsers;

import java.io.InputStream;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public class DocumentBuilder {
   protected DocumentBuilder() {
   }

   public Document parse(InputStream is) {
      if (is == null) {
         throw new Object("null InputStream");
      }

      InputSource in = (InputSource)(new Object(is));
      return this.parse(in);
   }

   public Document parse(InputStream is, String systemId) {
      if (is == null) {
         throw new Object("null InputStream");
      }

      InputSource in = (InputSource)(new Object(is));
      in.setSystemId(systemId);
      return this.parse(in);
   }

   public Document parse(InputSource _1) {
      throw null;
   }

   public boolean isNamespaceAware() {
      throw null;
   }

   public boolean isValidating() {
      throw null;
   }

   public void setEntityResolver(EntityResolver _1) {
      throw null;
   }

   public void setErrorHandler(ErrorHandler _1) {
      throw null;
   }

   public Document newDocument() {
      throw null;
   }

   public DOMImplementation getDOMImplementation() {
      throw null;
   }

   public boolean getAllowUndefinedNamespaces() {
      throw null;
   }

   public void setAllowUndefinedNamespaces(boolean _1) {
      throw null;
   }
}
