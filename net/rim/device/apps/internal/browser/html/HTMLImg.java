package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.ui.BrowserBitmapField;
import net.rim.device.apps.internal.browser.util.ImageMap;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLImageElement;

public final class HTMLImg extends HTMLGenericElement implements HTMLImageElement, SecondaryURLNode {
   private ImageMap _imgMap;
   private long _style;
   private HTMLAnchorElement _link;
   private Field _uiPeer;
   private int _replaceTag = -1;

   public final void onLoad() {
      String onLoad = this.getAttributeValue(156);
      if (onLoad != null && onLoad.length() > 0) {
         HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
         if (parent == null) {
            return;
         }

         RenderingApplication renderingApplication = parent.getRenderingApplication();
         if (renderingApplication != null) {
            renderingApplication.invokeRunnable(new HTMLImg$1(this, parent, onLoad));
         }
      }
   }

   public final void setImageMap(ImageMap map) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final Field getPeer() {
      return this._uiPeer;
   }

   public final void setLink(HTMLAnchorElement link) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setStyle(long style) {
      this._style = style;
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
   public final long getStyle() {
      return this._style;
   }

   @Override
   public final HTMLAnchorElement getLink() {
      return this._link;
   }

   @Override
   public final void setReplaceTag(int value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final int getReplaceTag() {
      return this._replaceTag;
   }

   @Override
   public final Object getCookie() {
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
   public final String getBorder() {
      return this.getAttributeValue(92);
   }

   @Override
   public final void setBorder(String border) {
      this.setAttributeValue(92, border);
   }

   @Override
   public final int getHeight() {
      return this.getAttributeValueAsPixels(124);
   }

   @Override
   public final void setHeight(int height) {
      this.setAttributeValue(124, height);
   }

   @Override
   public final int getHspace() {
      return this.getAttributeValueAsPixels(127);
   }

   @Override
   public final void setHspace(int hspace) {
      this.setAttributeValue(127, hspace);
   }

   @Override
   public final boolean getIsMap() {
      return this.getAttributeValueAsBoolean(130, false);
   }

   @Override
   public final void setIsMap(boolean isMap) {
      this.setAttributeValue(130, isMap ? 1 : 0);
   }

   @Override
   public final String getLongDesc() {
      return this.getAttributeValue(135);
   }

   @Override
   public final void setLongDesc(String longDesc) {
      this.setAttributeValue(135, longDesc);
   }

   @Override
   public final String getSrc() {
      return this.getAttributeValue(181);
   }

   @Override
   public final void setSrc(String src) {
      this.setAttributeValue(181, src);
      if (this._uiPeer instanceof BrowserBitmapField) {
         ((BrowserBitmapField)this._uiPeer).setImageUrl(src);
      }

      HTMLDocumentImpl docImpl = (HTMLDocumentImpl)this.getOwnerDocument();
      if (docImpl != null) {
         HTMLBrowserContent browserContent = docImpl.getUiPeer();
         if (browserContent != null) {
            browserContent.addSecondaryURL(src, this, false);
         }
      }
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
      return this.getAttributeValueAsPixels(197);
   }

   @Override
   public final void setVspace(int vspace) {
      this.setAttributeValue(197, vspace);
   }

   @Override
   public final int getWidth() {
      return this.getAttributeValueAsPixels(198);
   }

   @Override
   public final void setWidth(int width) {
      this.setAttributeValue(198, width);
   }

   @Override
   public final ImageMap getImageMap() {
      return this._imgMap;
   }

   @Override
   public final int getTagNameInt() {
      return 50;
   }

   public HTMLImg(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }
}
