package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;

public final class VarElement extends ParamElement {
   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitVarElement(this);
   }

   @Override
   public final String getElementName() {
      return "var";
   }
}
