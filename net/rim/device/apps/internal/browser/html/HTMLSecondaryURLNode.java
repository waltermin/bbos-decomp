package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.util.ImageMap;
import org.w3c.dom.html2.HTMLAnchorElement;

public final class HTMLSecondaryURLNode implements SecondaryURLNode {
   private Object _cookie;
   private Field _field;

   HTMLSecondaryURLNode(Field field, Object cookie) {
      this._field = field;
      this._cookie = cookie;
   }

   @Override
   public final Object getCookie() {
      return this._cookie;
   }

   @Override
   public final Field getUIField() {
      return this._field;
   }

   @Override
   public final void setUIField(Field f) {
      this._field = f;
   }

   @Override
   public final void setReplaceTag(int value) {
   }

   @Override
   public final int getHspace() {
      return 0;
   }

   @Override
   public final int getVspace() {
      return 0;
   }

   @Override
   public final String getSrc() {
      return null;
   }

   @Override
   public final long getStyle() {
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
   public final void setWidth(int imageWidth) {
   }

   @Override
   public final void setHeight(int imageHeight) {
   }

   @Override
   public final int getReplaceTag() {
      return -1;
   }

   @Override
   public final HTMLAnchorElement getLink() {
      return null;
   }

   @Override
   public final String getAlt() {
      return null;
   }

   @Override
   public final ImageMap getImageMap() {
      return null;
   }
}
