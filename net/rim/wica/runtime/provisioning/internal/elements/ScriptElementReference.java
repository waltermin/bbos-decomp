package net.rim.wica.runtime.provisioning.internal.elements;

public final class ScriptElementReference extends ElementReference {
   public ScriptElementReference(AbstractElement element, String name) {
      super(element, name);
   }

   public ScriptElementReference(ElementReference ref, AbstractElement element, String name) {
      super(ref, element, name);
   }

   @Override
   public final AbstractElement resolve() {
      if (!this.isResolved()) {
         WicletElement wiclet = this.resolveWicletElement();
         this.setReference(wiclet.getScriptElement(this.getTargetName()));
      }

      return this.getReference();
   }
}
