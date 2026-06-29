package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class RegionElement extends CommonControlElement {
   private String _layout;
   private Vector _nestedElements = new Vector();

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      if (v.visitRegionElement(this)) {
         ProvisioningHelper.visitDefinitionElementsHelper(this._nestedElements, v);
      }
   }

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof CommonControlElement) {
         this._nestedElements.addElement(parameter);
      } else {
         super.addChild(parameter);
      }
   }

   @Override
   public final String getElementName() {
      return "region";
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      this._layout = attributes.getValue("layout");
   }

   public final String getLayout() {
      return this._layout;
   }

   public final Vector getNestedElements() {
      return this._nestedElements;
   }
}
