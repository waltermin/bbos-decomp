package net.rim.wica.runtime.provisioning.internal.digester;

import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;

public class InvokeParentChildRule extends Rule {
   public InvokeParentChildRule(Digester digester) {
      super(digester);
   }

   @Override
   public void end() {
      AbstractElement child = (AbstractElement)super._digester.peek(0);
      AbstractElement parent = (AbstractElement)super._digester.peek(1);
      child.setParent(parent);
      parent.addChild(child);
   }
}
