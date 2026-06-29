package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class TextAreaElement extends CommonEditTextAreaElement {
   private String _visibleRows;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitTextareaElement(this);
   }

   @Override
   public final String getElementName() {
      return "textarea";
   }

   public final String getVisibleRows() {
      return this._visibleRows;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      this._visibleRows = attributes.getValue("visibleRows");
   }
}
