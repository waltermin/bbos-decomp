package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.util.ImageMap;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLLIElement;

final class HTMLLi extends HTMLGenericElement implements HTMLLIElement, SecondaryURLNode {
   private Field _uiPeer;

   public final void setAlt(String alt) {
      this.setAttributeValue(87, alt);
   }

   public final void setSrc(String src) {
      this.setAttributeValue(181, src);
   }

   public final Field getPeer() {
      return this._uiPeer;
   }

   public final void setPeer(Field field) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Field getUIField() {
      return this.getPeer();
   }

   @Override
   public final void setUIField(Field field) {
      this.setPeer(field);
   }

   @Override
   public final void setValue(int value) {
      this.setAttributeValue(193, value);
   }

   @Override
   public final int getValue() {
      return this.getAttributeValueAsInt(193);
   }

   @Override
   public final int getVspace() {
      return 0;
   }

   @Override
   public final int getHspace() {
      return 0;
   }

   @Override
   public final int getWidth() {
      return -1;
   }

   @Override
   public final int getHeight() {
      return -1;
   }

   @Override
   public final void setWidth(int width) {
   }

   @Override
   public final void setHeight(int height) {
   }

   @Override
   public final String getSrc() {
      return this.getAttributeValue(181);
   }

   @Override
   public final void setType(String type) {
      this.setAttributeValue(190, type);
   }

   @Override
   public final int getReplaceTag() {
      return -1;
   }

   @Override
   public final void setReplaceTag(int value) {
      throw new Object();
   }

   @Override
   public final Object getCookie() {
      return null;
   }

   @Override
   public final long getStyle() {
      return 0;
   }

   @Override
   public final HTMLAnchorElement getLink() {
      return null;
   }

   @Override
   public final String getAlt() {
      return this.getAttributeValue(87);
   }

   @Override
   public final String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public final ImageMap getImageMap() {
      return null;
   }

   public HTMLLi(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 57;
   }
}
