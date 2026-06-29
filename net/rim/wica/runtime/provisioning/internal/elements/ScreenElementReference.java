package net.rim.wica.runtime.provisioning.internal.elements;

public final class ScreenElementReference extends ElementReference {
   public ScreenElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public ScreenElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         WicletElement wiclet = this.resolveWicletElement();
         this.setReference(wiclet.getScreenElement(this.getTargetName()));
      }

      return this.getReference();
   }
}
