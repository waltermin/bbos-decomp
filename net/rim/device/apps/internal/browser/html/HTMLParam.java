package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLParamElement;

final class HTMLParam extends HTMLGenericElement implements HTMLParamElement {
   public HTMLParam(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 69;
   }

   @Override
   public final String getName() {
      return this.getAttributeValue(142);
   }

   @Override
   public final void setName(String name) {
      this.setAttributeValue(142, name);
   }

   @Override
   public final String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public final void setType(String type) {
      this.setAttributeValue(190, type);
   }

   @Override
   public final String getValue() {
      return this.getAttributeValue(193);
   }

   @Override
   public final void setValue(String value) {
      this.setAttributeValue(193, value);
   }

   @Override
   public final String getValueType() {
      return this.getAttributeValue(194);
   }

   @Override
   public final void setValueType(String valueType) {
      this.setAttributeValue(194, valueType);
   }
}
