package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.ScreenStyleHack;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.BorderSimple;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

public final class DialogPreviewPage extends PopupScreen {
   private Field _client;
   private PreviewManager _previewMgr;
   private static Tag TAG = Tag.create("browser-preview");
   private static final boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   private static final int BORDER_WIDTH = 2;
   private static final int BORDER_HEIGHT = 2;

   public static final void showDialog() {
      try {
         UiApplication.getUiApplication().pushScreen(new DialogPreviewPage());
      } finally {
         Status.show(BrowserResources.getString(763));
         return;
      }
   }

   private DialogPreviewPage() {
      super(new VerticalFieldManager(1407374883553280L), 68719673344L);
      this.setTag(TAG);
      Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
      if (page == null) {
         throw new IllegalArgumentException();
      }

      BrowserContent content = page.getBrowserContent();
      if (content == null) {
         throw new IllegalArgumentException();
      }

      this._client = content.getDisplayableContent();
      this._previewMgr = new PreviewManager(this._client);
      this.getDelegate().add(this._previewMgr);
      this.getDelegate().setBorder(new BorderSimple(2, 2, 2, 2));
      ScreenStyleHack.setStyleSystem(this, 68719476736L, 0);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      SLKeyLayout keyLayout = Keypad.getLayout();
      if (keyLayout != null) {
         key = UiInternal.map(keyLayout.getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
      }

      if (key != 'i' && (!_isReducedKeyboard || key != 'u')) {
         if (key == 'o') {
            this._previewMgr.zoomOut();
            this.reLayout();
            return true;
         }

         if (key == 149 && Trackball.isSupported()) {
            return false;
         }

         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else {
         this._previewMgr.zoomIn();
         this.reLayout();
         return true;
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._previewMgr.canZoomIn()) {
         menu.add(new DialogPreviewPage$1(this, BrowserResources.getString(888), 0, 0));
      }

      if (this._previewMgr.canZoomOut()) {
         menu.add(new DialogPreviewPage$2(this, BrowserResources.getString(889), 0, 0));
      }
   }

   private final void reLayout() {
      this.invalidateLayout0();
      this.doLayout();
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (super.trackwheelClick(status, time)) {
         return true;
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setMargin(0, 0, 0, 0);
      this.setBorder(0, 0, 0, 0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      XYRect extent = this._client.getExtent();
      width = Math.min(width, extent.width);
      height = Math.min(height, extent.height);
      this._previewMgr.setInitialScale(width - 4);
      this.layoutDelegate(width, height);
      XYRect fmExtent = this.getDelegate().getExtent();
      this.setPositionDelegate(width - fmExtent.width, 0);
      this.setExtent(width, height);
      this.setPosition(extent.x, extent.y);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.getDelegate().setVerticalScroll(this._previewMgr.getScaledTop());
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect extent = this.getExtent();
      graphics.setGlobalAlpha(159);
      graphics.setColor(16777215);
      graphics.rop(-95, 0, 0, extent.width, extent.height, null, 0, 0);
      super.paint(graphics);
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
   }
}
