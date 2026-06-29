package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.browser.api.PreviewViewable;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Renderer;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;

public final class HTMLBrowserField extends BrowserTextFlowManager implements PreviewViewable, Destroyable {
   private HTMLContext _htmlContext;
   private HTMLBrowserContent _browserContent;
   private int _destroyMethod = 0;
   private boolean _destroyed;
   public static boolean _singleLayoutMode = false;

   final boolean isDestroyed() {
      return this._destroyed;
   }

   public final IBrowserContext getContext() {
      if (this._htmlContext == null) {
         this._htmlContext = new HTMLContext();
      }

      return this._htmlContext;
   }

   public final BrowserContentImpl getBrowserContent() {
      return this._browserContent;
   }

   @Override
   public final void destroy() {
      this._browserContent.shutDownScriptEngine();
      this._browserContent.destroyTimer();

      for (int i = this.getFieldCount() - 1; i >= 0; i--) {
         Field f = this.getField(i);
         if (f instanceof Destroyable) {
            ((Destroyable)f).destroy();
         }
      }

      this._destroyed = true;
   }

   @Override
   public final void setDestroyMethod(int method) {
      this._destroyMethod = method;

      for (int i = this.getFieldCount() - 1; i >= 0; i--) {
         Field f = this.getField(i);
         if (f instanceof Destroyable) {
            ((Destroyable)f).setDestroyMethod(method);
         }
      }
   }

   @Override
   public final int getClientVirtualHeight() {
      return this.getVirtualHeight();
   }

   @Override
   public final int getClientHeight() {
      return this.getHeight();
   }

   @Override
   public final int getClientVerticalScroll() {
      return this.getVerticalScroll();
   }

   @Override
   public final void drawThumbnail(Graphics graphics, int scale) {
      this.paintThumbnail(graphics, scale);
   }

   @Override
   public final void setClientScroll(int horizontalPos, int verticalPos) {
      if (this.getScreen() != null) {
         this.scrollViewTo(horizontalPos, verticalPos, true);
      }
   }

   @Override
   public final void activatePreviewMode() {
      this.cancelCurrentSelection();
   }

   @Override
   public final int getClientVirtualWidth() {
      return this.getVirtualWidth();
   }

   @Override
   public final int getClientWidth() {
      return this.getWidth();
   }

   @Override
   public final int getClientHorizontalScroll() {
      return this.getHorizontalScroll();
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this._browserContent.startTimer();
   }

   HTMLBrowserField(HTMLBrowserContent browserContent, Renderer renderer, HTMLContext context, long style) {
      super(style);
      if (browserContent == null) {
         throw new RuntimeException("Browser Content cannot be null");
      }

      this._htmlContext = context;
      this._browserContent = browserContent;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      Field field = this.getLeafFieldWithFocus();
      Manager manager = field.getManager();

      while (!(manager instanceof BrowserTextFlowManager)) {
         manager = manager.getManager();
      }

      if (manager == this) {
         super.makeMenu(menu, instance);
         this._browserContent.addBrowserContentMenuItems(menu);
      }
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      this._browserContent.stopTimer();
      if (this._destroyMethod == 0) {
         this.destroy();
      } else {
         this._browserContent.pauseScriptEngine();
      }

      this._browserContent.submit(false, this._browserContent.getContext());
   }
}
