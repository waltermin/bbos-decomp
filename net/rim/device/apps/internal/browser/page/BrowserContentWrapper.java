package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Manager;

public final class BrowserContentWrapper extends BrowserContentImpl {
   private BrowserContent _browserContent;

   public BrowserContentWrapper(BrowserContent browserContent) {
      super(
         null,
         browserContent.getURL(),
         (Manager)(browserContent.getDisplayableContent() instanceof Object ? browserContent.getDisplayableContent() : null),
         browserContent.getRenderingApplication(),
         browserContent.getRenderingOptions(),
         browserContent.getRenderingFlags()
      );
      this._browserContent = browserContent;
      if (!(browserContent.getDisplayableContent() instanceof Object)) {
         this.getContentManager().add(browserContent.getDisplayableContent());
      }
   }

   @Override
   public final BrowserPageContext getBrowserPageContext() {
      return this._browserContent.getBrowserPageContext();
   }

   @Override
   public final String getError() {
      return this._browserContent.getError();
   }

   @Override
   public final EncodedImage getIcon() {
      return this._browserContent.getIcon();
   }

   @Override
   public final String getTitle() {
      return this._browserContent.getTitle();
   }

   @Override
   public final void finishLoading() {
      super.finishLoading();

      try {
         this._browserContent.finishLoading();
      } finally {
         return;
      }
   }

   @Override
   public final void resourceReady(RequestedResource resource) {
      this._browserContent.resourceReady(resource);
   }

   @Override
   public final boolean isBrowserContentEqual(Object bc) {
      return bc == this || bc == this._browserContent;
   }

   @Override
   public final boolean includeInPageCache() {
      return false;
   }
}
