package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class CommonControlElement extends AbstractElement {
   private String _inValue;
   private OnInitElement _onInit;
   private String _placement;
   private ElementReference _style;
   private boolean _visible = true;

   @Override
   public void addChild(AbstractElement parameter) {
      if (parameter instanceof OnInitElement) {
         this._onInit = (OnInitElement)parameter;
      }
   }

   public String getInValue() {
      return this._inValue;
   }

   public OnInitElement getOnInit() {
      return this._onInit;
   }

   public String getPlacement() {
      return this._placement;
   }

   public StyleElement getStyle() {
      return this._style != null ? (StyleElement)this._style.resolve() : null;
   }

   public boolean getVisible() {
      return this._visible;
   }

   public boolean hasStyle() {
      return this._style != null;
   }

   @Override
   public void setAttributes(Attributes attributes) {
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
            } else if (attName.equals("style")) {
               this._style = new StyleElementReference(this, attValue);
            } else if (attName.equals("placement")) {
               this._placement = attValue;
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer(64);
      String className = ProvisioningHelper.getClassName(this);
      buf.append(className);
      buf.append("[name=");
      buf.append(this.getName());
      buf.append(']');
      return buf.toString();
   }
}
