package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class AlertElement extends AbstractElement {
   private String _dialogText;
   private boolean _ribbon;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.visitAlertElement(this);
   }

   public final String getDialogText() {
      return this._dialogText;
   }

   @Override
   public final String getElementName() {
      return "alert";
   }

   public final boolean isRibbon() {
      return this._ribbon;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attName.equals("ribbon")) {
            this._ribbon = ProvisioningHelper.parseBoolean(attValue);
         } else if (attName.equals("dialogText")) {
            this._dialogText = attValue;
         }
      }
   }

   @Override
   public final String toString() {
      String className = ProvisioningHelper.getClassName(this);
      StringBuffer buf = (StringBuffer)(new Object(64));
      buf.append(className);
      buf.append("[dialogText=");
      buf.append(this._dialogText);
      buf.append(']');
      return buf.toString();
   }
}
