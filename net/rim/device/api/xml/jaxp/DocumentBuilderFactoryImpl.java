package net.rim.device.api.xml.jaxp;

import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.device.api.xml.parsers.ParserConfigurationException;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
   @Override
   public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
      if (this.isValidating()) {
         throw new ParserConfigurationException("not supported");
      }

      DocumentBuilderImpl db = new DocumentBuilderImpl(this);
      db.setAllowUndefinedNamespaces(this.getAllowUndefinedNamespaces());
      db.setNamespaceAware(this.isNamespaceAware());
      return db;
   }

   public void setAttribute(String name, Object value) {
      throw new IllegalArgumentException("not supported");
   }

   public Object getAttribute(String name) {
      throw new IllegalArgumentException("not supported");
   }
}
