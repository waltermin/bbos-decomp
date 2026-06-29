package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.ui.BrowserLinkBitmapField;

public final class WMLAnchoredBitmapField extends BrowserLinkBitmapField {
   private WMLAnchorVerb _anchorVerb;

   WMLAnchoredBitmapField(BrowserContentImpl browserField, Bitmap bitmap, String imageUrl, WMLAnchorVerb anchorVerb) {
      this(browserField, bitmap, imageUrl, 0, anchorVerb);
   }

   public WMLAnchoredBitmapField(BrowserContentImpl browserField, Bitmap bitmap, String imageUrl, long style, WMLAnchorVerb anchorVerb) {
      super(browserField, bitmap, imageUrl, style, (String)((Object)null), null);
      this._anchorVerb = anchorVerb;
   }

   @Override
   public final Object getCookieWithFocus() {
      return new WMLAnchorModel(this._anchorVerb);
   }
}
