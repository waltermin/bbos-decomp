package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class CommonEditTextAreaElement extends CommonControlElement {
   protected boolean _mandatory;
   protected String _mapping;
   protected OnFocusOutElement _onFocusOut;
   protected boolean _readOnly;

   @Override
   public void addChild(AbstractElement parameter) {
      if (parameter instanceof OnFocusOutElement) {
         this._onFocusOut = (OnFocusOutElement)parameter;
      } else {
         super.addChild(parameter);
      }
   }

   public String getMapping() {
      return this._mapping;
   }

   public OnFocusOutElement getOnFocusOut() {
      return this._onFocusOut;
   }

   public boolean isMandatory() {
      return this._mandatory;
   }

   public boolean isReadOnly() {
      return this._readOnly;
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
            } else if (attName.equals("mandatory")) {
               this._mandatory = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("mapping")) {
               this._mapping = attValue;
            }
         }
      }
   }
}
