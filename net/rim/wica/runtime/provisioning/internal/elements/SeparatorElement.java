package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class SeparatorElement extends CommonControlElement {
   private boolean _isWhitespace;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitSeparatorElement(this);
   }

   @Override
   public final String getElementName() {
      return "separator";
   }

   public final boolean isWhitespace() {
      return this._isWhitespace;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      String attValue = attributes.getValue("isWhitespace");
      if (attValue != null) {
         this._isWhitespace = ProvisioningHelper.parseBoolean(attValue);
      }
   }
}
