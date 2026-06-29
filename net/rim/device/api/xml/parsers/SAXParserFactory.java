package net.rim.device.api.xml.parsers;

import net.rim.device.api.xml.jaxp.SAXParserFactoryImpl;

public class SAXParserFactory {
   private boolean _allowUndefinedNamespaces = false;
   private boolean _isValidating = false;
   private boolean _isNamespaceAware = true;

   protected SAXParserFactory() {
   }

   public static SAXParserFactory newInstance() {
      return new SAXParserFactoryImpl();
   }

   public SAXParser newSAXParser() {
      throw null;
   }

   public void setNamespaceAware(boolean aware) {
      this._isNamespaceAware = aware;
   }

   public boolean isNamespaceAware() {
      return this._isNamespaceAware;
   }

   public void setValidating(boolean validate) {
      this._isValidating = validate;
   }

   public boolean isValidating() {
      return this._isValidating;
   }

   public void setAllowUndefinedNamespaces(boolean allow) {
      this._allowUndefinedNamespaces = allow;
   }

   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }
}
