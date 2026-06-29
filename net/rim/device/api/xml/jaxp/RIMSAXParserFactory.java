package net.rim.device.api.xml.jaxp;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXNotRecognizedException;

public class RIMSAXParserFactory extends SAXParserFactory {
   private boolean _namespaces = false;
   private boolean _prefixes = true;
   private boolean _allowUndefinedNamespaces = false;
   private static final String FEATURE_PREFIX = "http://xml.org/sax/features/";
   private static final int FEATURE_PREFIX_STRLEN = 28;
   private static final String FEATURE_NAMESPACES = "namespaces";
   private static final String FEATURE_NAMESPACE_PREFIXES = "namespace-prefixes";
   private static final String ERROR_NOT_SUPPORTED = "not supported";

   @Override
   public SAXParser newSAXParser() throws ParserConfigurationException {
      if (this.isValidating()) {
         throw new ParserConfigurationException("not supported");
      }

      RIMSAXParser sax = new RIMSAXParser();
      sax.setAllowUndefinedNamespaces(this.getAllowUndefinedNamespaces());
      sax.setNamespaceAware(this.isNamespaceAware());
      return sax;
   }

   @Override
   public boolean isNamespaceAware() {
      return this._namespaces && !this._prefixes;
   }

   @Override
   public void setNamespaceAware(boolean aware) {
      super.setNamespaceAware(aware);
      if (aware) {
         this._namespaces = true;
         this._prefixes = false;
      } else {
         this._namespaces = false;
         this._prefixes = true;
      }
   }

   public void setAllowUndefinedNamespaces(boolean allow) {
      this._allowUndefinedNamespaces = allow;
   }

   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }

   @Override
   public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
      if (name.startsWith("http://xml.org/sax/features/")) {
         String feature = name.substring(28);
         if (feature.equals("namespaces")) {
            this._namespaces = value;
         }

         if (feature.equals("namespace-prefixes")) {
            this._prefixes = value;
         }
      } else {
         throw new SAXNotRecognizedException(name);
      }
   }

   @Override
   public boolean getFeature(String name) throws SAXNotRecognizedException {
      if (name.startsWith("http://xml.org/sax/features/")) {
         String feature = name.substring(28);
         if (feature.equals("namespaces")) {
            return this._namespaces;
         }

         if (feature.equals("namespace-prefixes")) {
            return this._prefixes;
         }
      }

      throw new SAXNotRecognizedException(name);
   }
}
