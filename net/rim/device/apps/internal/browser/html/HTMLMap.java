package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLMapElement;

final class HTMLMap extends HTMLGenericElement implements HTMLMapElement {
   public HTMLMap(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 59;
   }

   @Override
   public final HTMLCollection getAreas() {
      return null;
   }

   @Override
   public final String getName() {
      return this.getAttributeValue(142);
   }

   @Override
   public final void setName(String name) {
      this.setAttributeValue(142, name);
   }
}
