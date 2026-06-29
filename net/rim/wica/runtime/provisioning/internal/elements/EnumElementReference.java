package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.StandardComponentResolver;

public final class EnumElementReference extends ElementReference {
   public EnumElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public EnumElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         WicletElement wiclet = this.resolveWicletElement();
         String componentToResolve = this.getTargetName();
         AbstractElement resolvedComponent = null;
         StandardComponentResolver standardComponentResolver = wiclet.getStandardComponentResolver();
         if (standardComponentResolver.isStandardComponent(componentToResolve)) {
            resolvedComponent = standardComponentResolver.resolveEnumerationElement(componentToResolve);
         } else {
            resolvedComponent = wiclet.getEnumerationElement(componentToResolve);
         }

         this.setReference(resolvedComponent);
      }

      return this.getReference();
   }
}
