package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class CheckboxElement extends CommonControlElement {
   protected String _mapping;
   protected OnChangeElement _onChange;
   protected boolean _readOnly;

   @Override
   public void addChild(AbstractElement parameter) {
      if (parameter instanceof OnChangeElement) {
         this._onChange = (OnChangeElement)parameter;
      } else {
         super.addChild(parameter);
      }
   }

   public String getMapping() {
      return this._mapping;
   }

   public OnChangeElement getOnChange() {
      return this._onChange;
   }

   public boolean isReadOnly() {
      return this._readOnly;
   }

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitCheckboxElement(this);
   }

   @Override
   public String getElementName() {
      return "checkbox";
   }

   @Override
   public void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("readOnly")) {
               this._readOnly = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("mapping")) {
               this._mapping = attValue;
            }
         }
      }
   }
}
