package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class MultiChoiceElement extends SingleChoiceElement {
   private boolean _mandatory;

   public MultiChoiceElement() {
      super._type = "checkbox";
   }

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitMultiChoiceElement(this);
   }

   @Override
   public final String getElementName() {
      return "multiChoice";
   }

   public final boolean isMandatory() {
      return this._mandatory;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      String attValue = attributes.getValue("mandatory");
      if (attValue != null) {
         this._mandatory = ProvisioningHelper.parseBoolean(attValue);
      }
   }
}
