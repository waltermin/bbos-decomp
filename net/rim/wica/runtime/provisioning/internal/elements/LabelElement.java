package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;

public final class LabelElement extends CommonControlElement {
   @Override
   public final String getElementName() {
      return "label";
   }

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitLabelElement(this);
   }
}
