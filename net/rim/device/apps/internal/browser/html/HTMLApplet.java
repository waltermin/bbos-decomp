package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLAppletElement;

final class HTMLApplet extends HTMLGenericElement implements HTMLAppletElement {
   public HTMLApplet(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 9;
   }

   @Override
   public final String getAlign() {
      return this.getAttributeValue(85);
   }

   @Override
   public final void setAlign(String align) {
      this.setAttributeValue(85, align);
   }

   @Override
   public final String getAlt() {
      return this.getAttributeValue(87);
   }

   @Override
   public final void setAlt(String alt) {
      this.setAttributeValue(87, alt);
   }

   @Override
   public final String getArchive() {
      return this.getAttributeValue(88);
   }

   @Override
   public final void setArchive(String archive) {
      this.setAttributeValue(88, archive);
   }

   @Override
   public final String getCode() {
      return this.getAttributeValue(103);
   }

   @Override
   public final void setCode(String code) {
      this.setAttributeValue(103, code);
   }

   @Override
   public final String getCodeBase() {
      return this.getAttributeValue(104);
   }

   @Override
   public final void setCodeBase(String codeBase) {
      this.setAttributeValue(104, codeBase);
   }

   @Override
   public final String getHeight() {
      return this.getAttributeValue(124);
   }

   @Override
   public final void setHeight(String height) {
      this.setAttributeValue(124, height);
   }

   @Override
   public final int getHspace() {
      return this.getAttributeValueAsInt(127);
   }

   @Override
   public final void setHspace(int hspace) {
      this.setAttributeValue(127, hspace);
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
   public final String getObject() {
      return this.getAttributeValue(147);
   }

   @Override
   public final void setObject(String object) {
      this.setAttributeValue(147, object);
   }

   @Override
   public final int getVspace() {
      return this.getAttributeValueAsInt(85);
   }

   @Override
   public final void setVspace(int vspace) {
      this.setAttributeValue(197, vspace);
   }

   @Override
   public final String getWidth() {
      return this.getAttributeValue(198);
   }

   @Override
   public final void setWidth(String width) {
      this.setAttributeValue(198, width);
   }
}
