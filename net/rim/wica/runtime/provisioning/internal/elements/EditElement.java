package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class EditElement extends CommonEditTextAreaElement {
   private String _format;
   private String _type = "text";

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitEditElement(this);
   }

   @Override
   public final String getElementName() {
      return "edit";
   }

   public final String getFormat() {
      return this._format;
   }

   public final String getType() {
      return this._type;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("type")) {
               this._type = attValue;
            } else if (attName.equals("format")) {
               this._format = attValue;
            }
         }
      }
   }
}
