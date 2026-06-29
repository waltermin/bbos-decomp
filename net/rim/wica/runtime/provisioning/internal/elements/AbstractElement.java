package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class AbstractElement {
   protected AbstractElement _parent;
   protected String _name;

   public void accept(DefinitionVisitor v) {
   }

   public void addChild(AbstractElement parameter) {
   }

   public void addElementBody(String elementName, String elementBodyText) {
   }

   public String getElementName() {
      throw null;
   }

   public AbstractElement getParent() {
      return this._parent;
   }

   public AbstractElement resolveAncestor(String parentElementName) {
      return ProvisioningHelper.findAncestor(this, parentElementName);
   }

   public void setAttributes(Attributes attributes) {
   }

   public void setParent(AbstractElement element) {
      this._parent = element;
   }

   public String getName() {
      return this._name;
   }

   @Override
   public String toString() {
      return this.getElementName();
   }
}
