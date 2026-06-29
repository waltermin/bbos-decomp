package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public class ImageElement extends CommonControlElement {
   protected ElementReference _resource;

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitImageElement(this);
   }

   @Override
   public String getElementName() {
      return "image";
   }

   public ResourceElement getResource() {
      return this.hasResource() ? (ResourceElement)this._resource.resolve() : null;
   }

   public boolean hasResource() {
      return this._resource != null;
   }

   @Override
   public void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      String attValue = attributes.getValue("resource");
      if (attValue != null) {
         this._resource = new ResourceElementReference(this, attValue);
      }
   }
}
