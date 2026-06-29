package net.rim.wica.runtime.provisioning.internal.elements;

import org.xml.sax.Attributes;

public final class OnClickElement extends CommonScriptBindingElement {
   private String _transaction = "none";
   private ElementReference _transition;

   @Override
   public final String getElementName() {
      return "onClick";
   }

   public final String getTransaction() {
      return this._transaction;
   }

   public final AbstractElement getTransition() {
      AbstractElement me = null;
      if (this.hasTransition()) {
         me = this._transition.resolve();
      }

      return me;
   }

   public final boolean hasTransition() {
      return this._transition != null;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("transaction")) {
               this._transaction = attValue;
            } else if (attName.equals("transition")) {
               ElementReference er = new ScriptElementReference(this, attValue);
               this._transition = new ScreenElementReference(er, this, attValue);
            }
         }
      }
   }
}
