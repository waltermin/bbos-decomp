package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class NotificationElement extends AbstractElement {
   private boolean _backgroundProcessing;
   private boolean _keepLast;

   @Override
   public final void accept(DefinitionVisitor v) {
   }

   @Override
   public final String getElementName() {
      return "notification";
   }

   public final boolean isBackgroundProcessing() {
      return this._backgroundProcessing;
   }

   public final boolean isKeepLast() {
      return this._keepLast;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attName.equals("backgroundProcessing")) {
            this._backgroundProcessing = ProvisioningHelper.parseBoolean(attValue);
         } else if (attName.equals("keepLast")) {
            this._keepLast = ProvisioningHelper.parseBoolean(attValue);
         }
      }
   }
}
