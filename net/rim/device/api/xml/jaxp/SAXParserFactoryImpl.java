package net.rim.device.api.xml.jaxp;

import net.rim.device.api.xml.parsers.ParserConfigurationException;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

public class SAXParserFactoryImpl extends SAXParserFactory {
   private static final String FEATURE_PREFIX;
   private static final int FEATURE_PREFIX_STRLEN;
   private static final String FEATURE_NAMESPACES;
   private static final String FEATURE_NAMESPACE_PREFIXES;
   private static final String ERROR_NOT_SUPPORTED;

   @Override
   public SAXParser newSAXParser() {
      if (this.isValidating()) {
         throw new ParserConfigurationException("not supported");
      }

      SAXParserImpl sax = new SAXParserImpl();
      sax.setAllowUndefinedNamespaces(this.getAllowUndefinedNamespaces());
      sax.setNamespaceAware(this.isNamespaceAware());
      return sax;
   }

   public void setFeature(String name, boolean value) {
      throw new Object("not supported");
   }

   public boolean getFeature(String name) {
      if (name.startsWith("http://xml.org/sax/features/")) {
         String feature = name.substring(28);
         if (feature.equals("namespaces")) {
            return true;
         }

         if (feature.equals("namespace-prefixes")) {
            return false;
         }
      }

      throw new Object("not supported");
   }
}
