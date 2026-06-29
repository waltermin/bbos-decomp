package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.util.ImageMap;
import org.w3c.dom.html2.HTMLAnchorElement;

public final class WMLSecondaryURLNode implements SecondaryURLNode {
   private Field _field;
   private int _hspace;
   private int _vspace;
   private long _style;
   private String _src;
   private Object _cookie;
   private int _width = -1;
   private int _height = -1;

   final void setCookie(Object cookie) {
      this._cookie = cookie;
   }

   final void setStyle(long style) {
      this._style = style;
   }

   final void setHspace(int value) {
      this._hspace = value;
   }

   final void setVspace(int value) {
      this._vspace = value;
   }

   final void setSrc(String src) {
      this._src = src;
   }

   @Override
   public final long getStyle() {
      return this._style;
   }

   @Override
   public final Object getCookie() {
      return this._cookie;
   }

   @Override
   public final int getWidth() {
      return this._width;
   }

   @Override
   public final int getHeight() {
      return this._height;
   }

   @Override
   public final String getSrc() {
      return this._src;
   }

   @Override
   public final int getVspace() {
      return this._vspace;
   }

   @Override
   public final int getHspace() {
      return this._hspace;
   }

   @Override
   public final void setUIField(Field field) {
      this._field = field;
   }

   @Override
   public final Field getUIField() {
      return this._field;
   }

   @Override
   public final void setWidth(int imageWidth) {
      this._width = imageWidth;
   }

   @Override
   public final void setHeight(int imageHeight) {
      this._height = imageHeight;
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

   @Override
   public final void setReplaceTag(int value) {
   }

   WMLSecondaryURLNode() {
   }
}
