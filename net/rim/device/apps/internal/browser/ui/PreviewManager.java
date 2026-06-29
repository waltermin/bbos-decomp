package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.internal.browser.api.PreviewViewable;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class PreviewManager extends Field {
   private Application _app;
   private PreviewManager$UpdateScroll _updateScroll;
   private int _invokeId = -1;
   private PreviewViewable _client;
   private int _focusX;
   private int _focusY;
   private int _focusWidth;
   private int _focusHeight;
   private int _currentScale;
   private boolean _scaleSet;
   private Bitmap _backbuffer;
   private Graphics _backbufferGraphics;
   private XYRect _validBufferRect;
   private int _preZoomX;
   private int _preZoomY;
   private static final int UPDATE_DELAY = 500;
   private static final int POINTER_OVERLAY_INDEX = 4;
   private static final int MIN_SCALE = Fixed32.toFP(2);
   private static final int MAX_SCALE = Fixed32.toFP(8);

   public PreviewManager(Field client) {
      super(18014462933991424L);
      this._currentScale = MIN_SCALE;
      this._preZoomX = -1;
      this._preZoomY = -1;
      if (!Graphics.isColor()) {
         throw new IllegalArgumentException();
      }

      if (!(client instanceof PreviewViewable)) {
         throw new IllegalArgumentException();
      }

      this._client = (PreviewViewable)client;
      this._app = Application.getApplication();
      this._updateScroll = new PreviewManager$UpdateScroll(this, null);
      this._client.activatePreviewMode();
      this._backbuffer = new Bitmap(Display.getWidth(), Display.getHeight());
      this._backbufferGraphics = new Graphics(this._backbuffer);
      this._validBufferRect = new XYRect();
   }

   final void setInitialScale(int width) {
      if (!this._scaleSet) {
         this._currentScale = Fixed32.toFP(4);
         this._scaleSet = true;
         this._focusY = Fixed32.divtoInt(Fixed32.toFP(this._client.getClientVerticalScroll()), this._currentScale);
         this._focusX = Fixed32.divtoInt(Fixed32.toFP(this._client.getClientHorizontalScroll()), this._currentScale);
      }
   }

   @Override
   protected final void layout(int width, int height) {
      height = RendererControl.fixed32DivToInt(this._client.getClientVirtualHeight(), this._currentScale);
      width = RendererControl.fixed32DivToInt(this._client.getClientVirtualWidth(), this._currentScale);
      this.setExtent(width, height);
      this._focusWidth = MathUtilities.clamp(0, RendererControl.fixed32DivToInt(this._client.getClientWidth(), this._currentScale), width);
      this._focusHeight = MathUtilities.clamp(0, RendererControl.fixed32DivToInt(this._client.getClientHeight(), this._currentScale), height);
      Manager manager = this.getManager();
      if (manager != null && this._preZoomX >= 0 && this._preZoomY >= 0) {
         int newVisibleWidth = MathUtilities.clamp(0, width, Display.getWidth());
         int newVisibleHeight = MathUtilities.clamp(0, height, Display.getHeight());
         int hscroll = MathUtilities.clamp(
            0, RendererControl.fixed32DivToInt(this._preZoomX, this._currentScale) - (newVisibleWidth >> 1), width - newVisibleWidth
         );
         int vscroll = MathUtilities.clamp(
            0, RendererControl.fixed32DivToInt(this._preZoomY, this._currentScale) - (newVisibleHeight >> 1), height - newVisibleHeight
         );
         manager.setHorizontalScroll(hscroll);
         manager.setVerticalScroll(vscroll);
         this._focusX = MathUtilities.clamp(hscroll, this._focusX, hscroll + newVisibleWidth - this._focusWidth);
         this._focusY = MathUtilities.clamp(vscroll, this._focusY, vscroll + newVisibleHeight - this._focusHeight);
         this.updateClientScroll();
      }

      this._preZoomX = -1;
      this._preZoomY = -1;
   }

   private final void adjustZoom(int delta) {
      Manager manager = this.getManager();
      if (manager != null) {
         this._preZoomX = RendererControl.fixed32MultToInt(manager.getHorizontalScroll() + (manager.getVisibleWidth() >> 1), this._currentScale);
         this._preZoomY = RendererControl.fixed32MultToInt(manager.getVerticalScroll() + (manager.getVisibleHeight() >> 1), this._currentScale);
      }

      this._focusX = RendererControl.fixed32MultToInt(this._focusX, this._currentScale);
      this._focusY = RendererControl.fixed32MultToInt(this._focusY, this._currentScale);
      this._currentScale = this._currentScale + Fixed32.toFP(delta);
      this._focusX = RendererControl.fixed32DivToInt(this._focusX, this._currentScale);
      this._focusY = RendererControl.fixed32DivToInt(this._focusY, this._currentScale);
      this._validBufferRect.set(0, 0, 0, 0);
   }

   public final void zoomIn() {
      if (this.canZoomIn()) {
         this.adjustZoom(-1);
      }
   }

   public final void zoomOut() {
      if (this.canZoomOut()) {
         this.adjustZoom(1);
      }
   }

   public final boolean canZoomIn() {
      return this._currentScale > MIN_SCALE;
   }

   public final boolean canZoomOut() {
      return this._currentScale < MAX_SCALE;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void drawClientThumbnail(int x, int y, int width, int height, int xoff, int yoff) {
      if (this._backbufferGraphics.pushContext(x, y, width, height, xoff, yoff)) {
         this._backbufferGraphics.clear();

         label24:
         try {
            this._client.drawThumbnail(this._backbufferGraphics, this._currentScale);
         } catch (Throwable var9) {
            QuincyUtil.sendQuincy(t, false);
            break label24;
         }

         this._backbufferGraphics.popContext();
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      graphics.setGlobalAlpha(255);
      graphics.setColor(16777215);
      graphics.fillRect(clip.x, clip.y, clip.width, clip.height);
      if (!this._validBufferRect.contains(clip)) {
         XYRect reusable = new XYRect(this._validBufferRect);
         reusable.intersect(clip);
         if (reusable.width > 0 && reusable.height > 0) {
            this._backbufferGraphics
               .copyArea(
                  reusable.x - this._validBufferRect.x,
                  reusable.y - this._validBufferRect.y,
                  reusable.width,
                  reusable.height,
                  this._validBufferRect.x - clip.x,
                  this._validBufferRect.y - clip.y
               );
            if (clip.x < reusable.x) {
               this.drawClientThumbnail(0, 0, reusable.x - clip.x, clip.height, -clip.x, -clip.y);
               reusable.width = reusable.width + (reusable.x - clip.x);
               reusable.x = clip.x;
            }

            if (clip.X2() > reusable.X2()) {
               this.drawClientThumbnail(reusable.width, 0, clip.X2() - reusable.X2(), clip.height, -clip.x, -clip.y);
               reusable.width = clip.width;
            }

            if (clip.y < reusable.y) {
               this.drawClientThumbnail(0, 0, clip.width, reusable.y - clip.y, -clip.x, -clip.y);
               reusable.height = reusable.height + (reusable.y - clip.y);
               reusable.y = clip.y;
            }

            if (clip.Y2() > reusable.Y2()) {
               this.drawClientThumbnail(0, reusable.height, clip.width, clip.Y2() - reusable.Y2(), -clip.x, -clip.y);
               reusable.height = clip.height;
            }
         } else {
            this.drawClientThumbnail(0, 0, clip.width, clip.height, -clip.x, -clip.y);
         }

         this._validBufferRect.set(clip);
      }

      graphics.drawBitmap(clip.x, clip.y, clip.width, clip.height, this._backbuffer, clip.x - this._validBufferRect.x, clip.y - this._validBufferRect.y);
      graphics.setColor(0);
      graphics.drawRoundRect(this._focusX, this._focusY, this._focusWidth - 1, this._focusHeight - 1, 5, 5);
      graphics.setColor(16777215);
      graphics.drawRoundRect(this._focusX + 1, this._focusY + 1, this._focusWidth - 3, this._focusHeight - 3, 5, 5);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      this._focusX = MathUtilities.clamp(0, this._focusX + dx, this.getContentWidth() - this._focusWidth);
      this._focusY = MathUtilities.clamp(0, this._focusY + dy, this.getContentHeight() - this._focusHeight);
      Screen screen = this.getScreen();
      if (screen != null) {
         screen.ensureRegionVisible(this, this._focusX, this._focusY, this._focusWidth, this._focusHeight);
      }

      this.invalidate();
      if (this._invokeId != -1) {
         this._app.cancelInvokeLater(this._invokeId);
         this._invokeId = -1;
      }

      this._invokeId = this._app.invokeLater(this._updateScroll, 500, false);
      return true;
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      int leftAdd = this.getBorderLeft() + this.getPaddingLeft();
      int rightAdd = this.getBorderRight() + this.getPaddingRight();
      int topAdd = this.getBorderTop() + this.getPaddingTop();
      int bottomAdd = this.getBorderBottom() + this.getPaddingBottom();
      rect.set(this._focusX - leftAdd, this._focusY - topAdd, this._focusWidth + leftAdd + rightAdd, this._focusHeight + topAdd + bottomAdd);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
   }

   final int getScaledTop() {
      int verticalScroll = this._client.getClientVerticalScroll();
      int myHeight = this.getHeight();
      int myVerticalSpace = this.getManager().getHeight();
      return myVerticalSpace > myHeight
         ? 0
         : MathUtilities.clamp(0, RendererControl.fixed32DivToInt(verticalScroll, this._currentScale), myHeight - myVerticalSpace);
   }

   final int getScaledLeft() {
      int horizontalScroll = this._client.getClientHorizontalScroll();
      int width = this._client.getClientVirtualWidth();
      int clientWidth = this._client.getClientWidth();
      return horizontalScroll + 3 * clientWidth >= width
         ? Math.max(RendererControl.fixed32DivToInt(width - 3 * clientWidth, this._currentScale), 0)
         : RendererControl.fixed32DivToInt(horizontalScroll, this._currentScale);
   }

   private final void updateClientScroll() {
      int clientX = RendererControl.fixed32MultToInt(this._focusX, this._currentScale);
      int clientY = RendererControl.fixed32MultToInt(this._focusY, this._currentScale);
      this._client
         .setClientScroll(
            MathUtilities.clamp(0, clientX, this._client.getClientVirtualWidth() - this._client.getClientWidth()),
            MathUtilities.clamp(0, clientY, this._client.getClientVirtualHeight() - this._client.getClientHeight())
         );
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      Screen screen = this.getScreen();
      screen.setTrackballSensitivityXOffset(100);
      screen.setTrackballSensitivityYOffset(100);
      screen.setTrackballFilter(6);
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      Screen screen = this.getScreen();
      screen.setTrackballSensitivityXOffset(Integer.MAX_VALUE);
      screen.setTrackballSensitivityYOffset(Integer.MAX_VALUE);
      screen.setTrackballFilter(-1);
      this.updateClientScroll();
   }
}
