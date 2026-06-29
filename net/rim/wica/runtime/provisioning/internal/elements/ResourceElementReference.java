package net.rim.wica.runtime.provisioning.internal.elements;

public final class ResourceElementReference extends ElementReference {
   public ResourceElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public ResourceElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         WicletElement wiclet = this.resolveWicletElement();
         this.setReference(wiclet.getResourceElement(this.getTargetName()));
      }

      return this.getReference();
   }
}
