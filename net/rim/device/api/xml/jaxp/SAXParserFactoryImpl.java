package net.rim.device.api.xml.jaxp;

import net.rim.device.api.xml.parsers.ParserConfigurationException;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXNotSupportedException;

public class SAXParserFactoryImpl extends SAXParserFactory {
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

      SAXParserImpl sax = new SAXParserImpl();
      sax.setAllowUndefinedNamespaces(this.getAllowUndefinedNamespaces());
      sax.setNamespaceAware(this.isNamespaceAware());
      return sax;
   }

   public void setFeature(String name, boolean value) throws SAXNotSupportedException {
      throw new SAXNotSupportedException("not supported");
   }

   public boolean getFeature(String name) throws SAXNotSupportedException {
      if (name.startsWith("http://xml.org/sax/features/")) {
         String feature = name.substring(28);
         if (feature.equals("namespaces")) {
            return true;
         }

         if (feature.equals("namespace-prefixes")) {
            return false;
         }
      }

      throw new SAXNotSupportedException("not supported");
   }
}
