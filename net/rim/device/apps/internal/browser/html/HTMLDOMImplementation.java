package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

class HTMLDOMImplementation implements DOMImplementation {
   static HTMLDOMImplementation _instance;

   private HTMLDOMImplementation() {
   }

   static HTMLDOMImplementation getInstance() {
      if (_instance == null) {
         _instance = new HTMLDOMImplementation();
      }

      return _instance;
   }

   @Override
   public boolean hasFeature(String feature, String version) {
      return HTMLDOMInternalRepresentation.hasFeature(feature, version);
   }

   @Override
   public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
      HTMLDOMInternalRepresentation.isQName(qualifiedName);
      HTMLDOMInternalRepresentation ir = new HTMLDOMInternalRepresentation();
      return (DocumentType)ir.getNode(ir.addDocumentType(qualifiedName, publicId, systemId, ""));
   }

   @Override
   public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) {
      HTMLDOMInternalRepresentation.isQName(qualifiedName);
      HTMLDOMInternalRepresentation ir;
      if (doctype != null) {
         if (!(doctype instanceof HTMLDocumentType)) {
            throw new DOMException((short)4, "");
         }

         HTMLNode node = (HTMLNode)doctype;
         ir = node._ir;
         if (ir.getDocument() != 0) {
            throw new DOMException((short)4, "");
         }
      } else {
         ir = new HTMLDOMInternalRepresentation();
      }

      return (Document)ir.getNode(ir.getDocument());
   }
}
