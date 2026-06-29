package javax.xml.parsers;

import net.rim.device.api.xml.jaxp.RIMSAXParserFactory;

public class SAXParserFactory {
   private boolean _isValidating = false;
   private boolean _isNamespaceAware = false;

   protected SAXParserFactory() {
   }

   public static SAXParserFactory newInstance() {
      return new RIMSAXParserFactory();
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

   public void setFeature(String _1, boolean _2) {
      throw null;
   }

   public boolean getFeature(String _1) {
      throw null;
   }
}
