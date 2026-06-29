package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.DocumentType;

final class ESDocumentType extends ESNode {
   ESDocumentType(DocumentType docType) {
      super(docType, Names.DocumentType);
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(((DocumentType)this.getNode()).getName());
      } else if (name == Names.publicId) {
         return JavaScriptEngine.makeStringValue(((DocumentType)this.getNode()).getPublicId());
      } else if (name == Names.systemId) {
         return JavaScriptEngine.makeStringValue(((DocumentType)this.getNode()).getSystemId());
      } else if (name == Names.internalSubset) {
         return JavaScriptEngine.makeStringValue(((DocumentType)this.getNode()).getInternalSubset());
      } else if (name == Names.entities) {
         DocumentType docType = (DocumentType)this.getNode();
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(docType.getEntities());
      } else if (name == Names.notations) {
         DocumentType docType = (DocumentType)this.getNode();
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(docType.getNotations());
      } else {
         return super.requestFieldValue(name);
      }
   }
}
