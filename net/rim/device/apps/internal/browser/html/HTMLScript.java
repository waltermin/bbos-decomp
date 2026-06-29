package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLScriptElement;

final class HTMLScript extends HTMLGenericElement implements HTMLScriptElement {
   public HTMLScript(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 75;
   }

   @Override
   public final String getText() {
      return null;
   }

   @Override
   public final void setText(String text) {
   }

   @Override
   public final String getHtmlFor() {
      return this.getAttributeValue(120);
   }

   @Override
   public final void setHtmlFor(String htmlFor) {
      this.setAttributeValue(120, htmlFor);
   }

   @Override
   public final String getEvent() {
      return null;
   }

   @Override
   public final void setEvent(String event) {
   }

   @Override
   public final String getCharset() {
      return this.getAttributeValue(97);
   }

   @Override
   public final void setCharset(String charset) {
      this.setAttributeValue(97, charset);
   }

   @Override
   public final boolean getDefer() {
      return this.getAttributeValueAsBoolean(115, false);
   }

   @Override
   public final void setDefer(boolean defer) {
      this.setAttributeValue(115, defer ? 1 : 0);
   }

   @Override
   public final String getSrc() {
      return this.getAttributeValue(181);
   }

   @Override
   public final void setSrc(String src) {
      this.setAttributeValue(181, src);
   }

   @Override
   public final String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public final void setType(String type) {
      this.setAttributeValue(190, type);
   }
}
