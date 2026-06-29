package net.rim.device.api.xml.jaxp;

class DOMInternalRepresentation$DocumentHandler extends DOMInternalRepresentation$Handler {
   private final DOMInternalRepresentation this$0;

   DOMInternalRepresentation$DocumentHandler(DOMInternalRepresentation _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   public void endDocument() {
      int document = this.this$0.addDocument();
      this.addChildren(document);
      if (this.this$0._dtdBody != null) {
         this.this$0.addDocumentType();
      }

      this.fini(document);
      this.this$0.pushChildren();
      this.this$0.pushChild(document);
   }
}
