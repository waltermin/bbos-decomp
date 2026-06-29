package net.rim.device.api.xml.jaxp;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

class DOMImplementationImpl implements DOMImplementation {
   static DOMImplementationImpl _instance;

   private DOMImplementationImpl() {
   }

   static DOMImplementationImpl getInstance() {
      if (_instance == null) {
         _instance = new DOMImplementationImpl();
      }

      return _instance;
   }

   @Override
   public boolean hasFeature(String feature, String version) {
      return DOMInternalRepresentation.hasFeature(feature, version);
   }

   @Override
   public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
      DOMInternalRepresentation.isQName(qualifiedName);
      DOMInternalRepresentation ir = new DOMInternalRepresentation();
      DocumentType rc = (DocumentType)ir.getNode(ir.addDocumentType(qualifiedName, publicId, systemId, ""));
      return rc;
   }

   @Override
   public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) {
      DOMInternalRepresentation.isQName(qualifiedName);
      DOMInternalRepresentation ir;
      if (doctype != null) {
         if (!(doctype instanceof DOMDocumentTypeImpl)) {
            throw new Object((short)4, "");
         }

         DOMNodeImpl node = (DOMNodeImpl)doctype;
         ir = node._ir;
         if (ir.getDocument() != 0) {
            throw new Object((short)4, "");
         }
      } else {
         ir = new DOMInternalRepresentation();
      }

      ir.addDocument();
      Document rc = (Document)ir.getNode(ir.getDocument());
      return rc;
   }
}
