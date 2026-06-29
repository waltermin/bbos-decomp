package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class MenuItemElement extends AbstractElement {
   private String _inValue;
   private OnClickElement _onClick;
   private boolean _visible = true;

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof OnClickElement) {
         this._onClick = (OnClickElement)parameter;
      }
   }

   @Override
   public final String getElementName() {
      return "menuItem";
   }

   public final String getInValue() {
      return this._inValue;
   }

   public final OnClickElement getOnClick() {
      return this._onClick;
   }

   public final boolean getVisible() {
      return this._visible;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("inValue")) {
               this._inValue = attValue;
            } else if (attName.equals("visible")) {
               this._visible = ProvisioningHelper.parseBoolean(attValue);
            }
         }
      }
   }

   @Override
   public final String toString() {
      String className = ProvisioningHelper.getClassName(this);
      StringBuffer buf = new StringBuffer(64);
      buf.append(className);
      buf.append("[name=");
      buf.append(super._name);
      buf.append(']');
      return buf.toString();
   }
}
