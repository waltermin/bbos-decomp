package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class ScriptElement extends AbstractElement {
   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitScriptElement(this);
   }

   @Override
   public final String getElementName() {
      return "script";
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attName.equals("name")) {
            super._name = attValue;
         }
      }
   }

   @Override
   public final String toString() {
      String className = ProvisioningHelper.getClassName(this);
      StringBuffer buf = (StringBuffer)(new Object(64));
      buf.append(className);
      buf.append("[name=");
      buf.append(super._name);
      buf.append(']');
      return buf.toString();
   }
}
