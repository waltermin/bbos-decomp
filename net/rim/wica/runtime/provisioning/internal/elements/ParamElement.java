package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class ParamElement extends AbstractElement {
   protected ElementReference _component;

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitParamElement(this);
   }

   public DataElement getComponent() {
      return this.hasComponent() ? (DataElement)this._component.resolve() : null;
   }

   @Override
   public String getElementName() {
      return "param";
   }

   public boolean hasComponent() {
      return this._component != null;
   }

   @Override
   public void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("component")) {
               this._component = new DataElementReference(this, attValue);
            }
         }
      }
   }

   @Override
   public String toString() {
      String className = ProvisioningHelper.getClassName(this);
      StringBuffer buf = (StringBuffer)(new Object(64));
      buf.append(className);
      buf.append("[name=");
      buf.append(super._name);
      buf.append(']');
      return buf.toString();
   }
}
