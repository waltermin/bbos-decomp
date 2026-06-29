package net.rim.wica.runtime.provisioning.internal.digester;

import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;
import org.xml.sax.Attributes;

public class SetAttributesRule extends Rule {
   public SetAttributesRule(Digester digester) {
      super(digester);
   }

   @Override
   public void begin(Attributes attributes) {
      AbstractElement top = (AbstractElement)super._digester.peek();
      top.setAttributes(attributes);
   }
}
