package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLObjectElement;

final class HTMLObject extends HTMLGenericElement implements HTMLObjectElement {
   private long _fieldStyle;

   public final void setFieldStyle(long style) {
      this._fieldStyle = style;
   }

   public final long getFieldStyle() {
      return this._fieldStyle;
   }

   @Override
   public final void setCode(String code) {
      this.setAttributeValue(103, code);
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
   public final String getArchive() {
      return this.getAttributeValue(88);
   }

   @Override
   public final void setArchive(String archive) {
      this.setAttributeValue(88, archive);
   }

   @Override
   public final String getBorder() {
      return this.getAttributeValue(92);
   }

   @Override
   public final void setBorder(String border) {
      this.setAttributeValue(92, border);
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
   public final String getCodeType() {
      return this.getAttributeValue(105);
   }

   @Override
   public final void setCodeType(String codeType) {
      this.setAttributeValue(105, codeType);
   }

   @Override
   public final String getData() {
      return this.getAttributeValue(112);
   }

   @Override
   public final void setData(String data) {
      this.setAttributeValue(112, data);
   }

   @Override
   public final boolean getDeclare() {
      return this.getAttributeValueAsBoolean(114, false);
   }

   @Override
   public final void setDeclare(boolean declare) {
      this.setAttributeValue(114, declare ? 1 : 0);
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
   public final String getStandby() {
      return this.getAttributeValue(182);
   }

   @Override
   public final void setStandby(String standby) {
      this.setAttributeValue(182, standby);
   }

   @Override
   public final int getTabIndex() {
      return 0;
   }

   @Override
   public final void setTabIndex(int tabIndex) {
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
   public final String getUseMap() {
      return this.getAttributeValue(191);
   }

   @Override
   public final void setUseMap(String useMap) {
      this.setAttributeValue(191, useMap);
   }

   @Override
   public final int getVspace() {
      return this.getAttributeValueAsInt(197);
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

   @Override
   public final Document getContentDocument() {
      return this.getOwnerDocument();
   }

   @Override
   public final String getCode() {
      return this.getAttributeValue(103);
   }

   @Override
   public final HTMLFormElement getForm() {
      return null;
   }

   @Override
   public final int getTagNameInt() {
      return 64;
   }

   public HTMLObject(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }
}
