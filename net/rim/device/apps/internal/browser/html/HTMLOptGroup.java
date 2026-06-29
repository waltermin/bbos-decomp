package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLOptGroupElement;

final class HTMLOptGroup extends HTMLGenericElement implements HTMLOptGroupElement {
   public HTMLOptGroup(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 66;
   }

   @Override
   public final Node appendChild(Node newChild) {
      if (newChild instanceof HTMLOption) {
         HTMLOption option = (HTMLOption)newChild;
         Node parent = this.getParentNode();
         if (parent instanceof HTMLSelect) {
            HTMLSelect select = (HTMLSelect)parent;
            select.addOption(option, (byte)8);
         }
      }

      return super.appendChild(newChild);
   }

   @Override
   public final boolean getDisabled() {
      return this.getAttributeValueAsBoolean(117, false);
   }

   @Override
   public final void setDisabled(boolean disabled) {
      this.setAttributeValue(117, disabled ? 1 : 0);
   }

   @Override
   public final String getLabel() {
      return this.getAttributeValue(131);
   }

   @Override
   public final void setLabel(String label) {
      this.setAttributeValue(131, label);
   }
}
