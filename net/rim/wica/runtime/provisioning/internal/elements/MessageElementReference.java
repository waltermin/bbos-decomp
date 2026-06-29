package net.rim.wica.runtime.provisioning.internal.elements;

public final class MessageElementReference extends ElementReference {
   public MessageElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public MessageElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         WicletElement wiclet = this.resolveWicletElement();
         this.setReference(wiclet.getMessageElement(this.getTargetName()));
      }

      return this.getReference();
   }
}
