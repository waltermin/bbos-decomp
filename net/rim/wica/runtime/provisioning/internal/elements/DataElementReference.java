package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.StandardComponentResolver;

public final class DataElementReference extends ElementReference {
   public DataElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public DataElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         AbstractElement resolvedComponent = null;
         WicletElement wiclet = this.resolveWicletElement();
         String componentToResolve = this.getTargetName();
         StandardComponentResolver standardComponentResolver = wiclet.getStandardComponentResolver();
         if (standardComponentResolver.isStandardComponent(componentToResolve)) {
            resolvedComponent = standardComponentResolver.resolveDataElement(componentToResolve);
         } else {
            resolvedComponent = wiclet.getDataElement(componentToResolve);
         }

         this.setReference(resolvedComponent);
      }

      return this.getReference();
   }
}
