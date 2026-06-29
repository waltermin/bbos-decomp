package net.rim.wica.runtime.provisioning.internal.elements;

public final class StyleElementReference extends ElementReference {
   public StyleElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public StyleElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         WicletElement wiclet = this.resolveWicletElement();
         this.setReference(wiclet.getStyleElement(this.getTargetName()));
      }

      return this.getReference();
   }
}
