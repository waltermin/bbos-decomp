package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class ButtonElement extends ImageElement {
   private OnClickElement _onClick;
   private String _imageValue;

   @Override
   public final void addChild(AbstractElement parameter) {
      super.addChild(parameter);
      if (parameter instanceof OnClickElement) {
         this._onClick = (OnClickElement)parameter;
      }
   }

   @Override
   public final String getElementName() {
      return "button";
   }

   public final String getImageValue() {
      return this._imageValue;
   }

   public final OnClickElement getOnClick() {
      return this._onClick;
   }

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitButtonElement(this);
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      this._imageValue = attributes.getValue("imageValue");
   }
}
