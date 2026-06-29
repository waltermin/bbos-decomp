package net.rim.device.api.xml.jaxp;

class DOMInternalRepresentation$FragmentHandler extends DOMInternalRepresentation$Handler {
   private final DOMInternalRepresentation this$0;

   DOMInternalRepresentation$FragmentHandler(DOMInternalRepresentation _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   public void endDocument() {
      int docFragment = this.this$0.addDocumentFragment();
      this.addChildren(docFragment);
      this.fini(docFragment);
      this.this$0.pushChildren();
      this.this$0.pushChild(docFragment);
   }
}
