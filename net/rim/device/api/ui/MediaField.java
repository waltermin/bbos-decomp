package net.rim.device.api.ui;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.service.BasicService;
import net.rim.plazmic.internal.mediaengine.service.EventSubscription;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.MediaViewport;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.Pannable;
import net.rim.plazmic.internal.mediaengine.ui.ViewportManager;
import net.rim.plazmic.internal.mediaengine.ui.Zoomable;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaPlayer;

public class MediaField extends Manager implements BasicService, MediaListener, Pannable, Zoomable {
   protected MediaServices _services;
   protected MediaViewport _viewport;
   private ViewportManager _viewportManager;
   protected MediaController _controller;
   protected EventSubscription _subscription;
   protected FocusInteractor _focusInteractor;
   protected ModelInteractor _modelInteractor;
   protected MediaPlayer _player;
   protected boolean _playerStop;
   private boolean _inScrollMode;
   private XYRect _rect = (XYRect)(new Object());
   private XYRect _virtExtent = (XYRect)(new Object(0, 0, -1, -1));
   private Hashtable _mapFieldToFO;
   private int _preferredWidth = -1;
   private int _preferredHeight = -1;
   private Pannable _pannable;
   private Zoomable _zoomable;
   private static final int MEDIA_SHIFT = 18;
   public static final long WRAP_ALLOWED = 262144L;
   public static final long TRANSPARENT = 524288L;
   public static final long ACTIVATE_ON_CLICK = 1048576L;
   public static final long NO_CONTEXT_MENU = 2097152L;
   public static final long PREFERRED_WIDTH = 8388608L;
   public static final long PREFERRED_HEIGHT = 16777216L;
   public static final long CONTROLLER_STYLE_MASK = 18014398512889856L;
   private static final int CONTROLLER_INDEX = 0;
   private static final int DIMENSION_NOT_SET = -1;
   private static final int SCROLL_DELTA = 6;

   void killFocus(Field field) {
      if (this.isForeignObject(field) && this.getFieldWithFocus() == field) {
         setFocus(this._controller, 1);
      }
   }

   public void setPreferredWidth(int width) {
      if ((this.getStyle() & 8388608) != 8388608) {
         throw new Object();
      }

      this._preferredWidth = width;
      this.updateLayout();
   }

   public void setPreferredHeight(int height) {
      if ((this.getStyle() & 16777216) != 16777216) {
         throw new Object();
      }

      this._preferredHeight = height;
      this.updateLayout();
   }

   public void setPreferredExtent(int width, int height) {
      long bothPreferred = 25165824;
      if ((this.getStyle() & bothPreferred) != bothPreferred) {
         throw new Object();
      }

      this._preferredWidth = width;
      this._preferredHeight = height;
      this.updateLayout();
   }

   public boolean isScrollMode() {
      return this._inScrollMode;
   }

   public void setWrap(boolean wrapFocus) {
      if (this._focusInteractor != null) {
         this._focusInteractor.setWrap(wrapFocus);
      }
   }

   public void setScrollMode(boolean value) {
      if (this.isScrollable()) {
         this._inScrollMode = value;
         super.invalidate();
      } else {
         this._inScrollMode = false;
      }
   }

   protected void updateForeignObjects(ForeignObject[] foreignObjects) {
      if (foreignObjects == null) {
         Enumeration e = this._mapFieldToFO.keys();

         while (e.hasMoreElements()) {
            this.delete((Field)e.nextElement());
         }

         this._mapFieldToFO.clear();
      } else {
         Enumeration e = this._mapFieldToFO.keys();

         while (e.hasMoreElements()) {
            Field f = (Field)e.nextElement();
            Object fo = this._mapFieldToFO.get(f);
            int i = 0;

            while (i < foreignObjects.length && foreignObjects[i] != fo) {
               i++;
            }

            if (i == foreignObjects.length) {
               this._mapFieldToFO.remove(f);
               this.delete(f);
            }
         }

         for (int i = 0; i < foreignObjects.length; i++) {
            if (foreignObjects[i] != null && foreignObjects[i].getInstance() != null) {
               Object f = foreignObjects[i].getInstance();
               if (f instanceof Object && !this._mapFieldToFO.containsKey(f)) {
                  Field fi = (Field)f;
                  this._mapFieldToFO.put(fi, foreignObjects[i]);
                  if (fi.getManager() != this) {
                     this.add(fi);
                  }
               }
            }
         }
      }
   }

   boolean isForeignObject(Field f) {
      return f != null && this._mapFieldToFO.containsKey(f);
   }

   public MediaViewport getMediaViewport() {
      return this._viewport;
   }

   public boolean isScrollable() {
      boolean temp = false;
      if ((this.getStyle() & 281474976710656L) != 0) {
         temp = this.getVirtualHeight() > this.getContentHeight();
      }

      if (!temp && (this.getStyle() & 1125899906842624L) != 0) {
         temp = this.getVirtualWidth() > this.getContentWidth();
      }

      return temp;
   }

   protected void scroll(int xAmount, int yAmount) {
      if ((this.getStyle() & 281474976710656L) != 0 && yAmount != 0) {
         this._rect
            .set(
               this.getHorizontalScroll(),
               MathUtilities.clamp(0, this.getVerticalScroll() + yAmount * 6, this.getVirtualHeight() - this.getContentHeight()),
               0,
               this.getContentHeight()
            );
         super.makeFocusVisible(true, this._rect, true, false);
      }

      if ((this.getStyle() & 1125899906842624L) != 0 && xAmount != 0) {
         this._rect
            .set(
               MathUtilities.clamp(0, this.getHorizontalScroll() + xAmount * 6, this.getVirtualWidth() - this.getContentWidth()),
               this.getVerticalScroll(),
               this.getContentWidth(),
               0
            );
         super.makeFocusVisible(true, this._rect, true, false);
      }
   }

   protected void updateViewportManager(long style) {
      int viewportStyle = 0;
      if ((style & 12884901888L) == 12884901888L) {
         viewportStyle |= 12;
      } else if ((style & 4294967296L) == 4294967296L) {
         viewportStyle |= 4;
      } else if ((style & 8589934592L) == 8589934592L) {
         viewportStyle |= 8;
      }

      if ((style & 51539607552L) == 51539607552L) {
         viewportStyle |= 3;
      } else if ((style & 17179869184L) == 17179869184L) {
         viewportStyle |= 1;
      } else if ((style & 34359738368L) == 34359738368L) {
         viewportStyle |= 2;
      }

      if (this._viewportManager == null) {
         this._viewportManager = new ViewportManager(viewportStyle);
      } else {
         this._viewportManager.setStyle(viewportStyle);
      }
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 20:
            this.repaint(false);
            if (this._player != null && this._playerStop && this._player.getState() == 2) {
               this._playerStop = false;
            }
      }
   }

   @Override
   public void setServices(MediaServices services) {
      this.assertHaveEventLock();
      if (this._services != services) {
         if (this._subscription != null) {
            this._subscription.removeListener(this);
         }

         if (this._focusInteractor != null) {
            this._focusInteractor.setFocusToItem(-1);
            this._focusInteractor = null;
         }

         this._services = services;
         if (services == null) {
            this._subscription = null;
            this._viewport = null;
            this._player = null;
         } else {
            this._subscription = (EventSubscription)services.getService("EventSubscription");
            if (this._subscription != null) {
               this._subscription.addListener(this);
            }

            if (this._focusInteractor == null) {
               this._focusInteractor = (FocusInteractor)services.getService("FocusInteractor");
            }

            if (this._focusInteractor != null) {
               this._focusInteractor.moveFocus(1, 0, false);
            }

            this._modelInteractor = (ModelInteractor)this._services.getService("ModelInteractor");
            if (PMEGraphics.isDisabled()) {
               this._viewport = (MediaViewport)this._services.getService("Viewport");
            } else {
               this._viewport = (MediaViewport)this._services.getService("PMEViewport");
               if (this._viewport == null) {
                  this._viewport = (MediaViewport)this._services.getService("Viewport");
               }
            }

            if (this._viewport != null && (this.getStyle() & 524288) != 0) {
               this._viewport.setStyle(this._viewport.getStyle() | 1);
            }

            this._player = (MediaPlayer)this._services.getService("MediaPlayer");
         }

         if (this._viewport != null) {
            if (this._viewport instanceof Pannable) {
               this._pannable = (Pannable)this._viewport;
            } else {
               this._pannable = null;
            }

            if (this._viewport instanceof Zoomable) {
               this._zoomable = (Zoomable)this._viewport;
            } else {
               this._zoomable = null;
            }
         }

         this._viewportManager.setViewport(this._viewport);
      }

      Object media = this._viewport != null ? this._viewport.getMedia() : null;
      this.updateForeignObjects(!(media instanceof MediaModel) ? null : ((MediaModel)media).getForeignObjects());
      this.updateLayout();
      this.updateFocus();
      super.invalidate();
   }

   @Override
   public void setPanX(int amount) {
      if (this._pannable != null) {
         this._pannable.setPanX(amount);
      }
   }

   @Override
   public void setPanY(int amount) {
      if (this._pannable != null) {
         this._pannable.setPanY(amount);
      }
   }

   @Override
   public int getPanX() {
      return this._pannable == null ? 0 : this._pannable.getPanX();
   }

   @Override
   public int getPanY() {
      return this._pannable == null ? 0 : this._pannable.getPanY();
   }

   @Override
   public boolean isPannable() {
      return this._pannable != null && this._pannable.isPannable();
   }

   @Override
   public void setZoomAmount(int amount) {
      if (this._zoomable != null) {
         this._zoomable.setZoomAmount(amount);
      }
   }

   @Override
   public int getZoomAmount() {
      return this._zoomable == null ? 65536 : this._zoomable.getZoomAmount();
   }

   @Override
   public void setZoomOriginX(int xOrigin) {
      if (this._zoomable != null) {
         this._zoomable.setZoomOriginX(xOrigin);
      }
   }

   @Override
   public void setZoomOriginY(int yOrigin) {
      if (this._zoomable != null) {
         this._zoomable.setZoomOriginY(yOrigin);
      }
   }

   @Override
   public int getZoomOriginX() {
      return this._zoomable == null ? 0 : this._zoomable.getZoomOriginX();
   }

   @Override
   public int getZoomOriginY() {
      return this._zoomable == null ? 0 : this._zoomable.getZoomOriginY();
   }

   @Override
   public boolean isZoomable() {
      return this._zoomable != null && this._zoomable.isZoomable();
   }

   @Override
   public void getPanBounds(XYEdges theEdge) {
      if (this._viewport != null) {
         int panBoundRight = (this.getContentWidth() >> 1) - this._viewport.getAlignX() << 16;
         int panBoundLeft = panBoundRight - (this._viewport.getVirtualWidth() << 16);
         int panBoundBottom = (this.getContentHeight() >> 1) - this._viewport.getAlignY() << 16;
         int panBoundTop = panBoundBottom - (this._viewport.getVirtualHeight() << 16);
         theEdge.set(panBoundTop, panBoundRight, panBoundBottom, panBoundLeft);
      }
   }

   @Override
   public Field getLeafFieldWithFocus() {
      return this._inScrollMode ? null : super.getLeafFieldWithFocus();
   }

   private void updateFocus() {
      Manager m = this.getManager();
      if (m != null && m.getFieldWithFocus() == this && this.getFieldWithFocus() == null) {
         this.onFocus(1);
      }
   }

   @Override
   public int getPreferredHeight() {
      if ((this.getStyle() & 16777216) == 16777216 && this._preferredHeight != -1) {
         return this._preferredHeight;
      }

      if (this._virtExtent.height == -1) {
         this.calculateVirtualExtent();
      }

      return this._virtExtent.height;
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if ((status & 1) != 0) {
         this.setScrollMode(!this._inScrollMode);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      boolean alt = (status & 1) > 0;
      Field focusField = this.getFieldWithFocus();
      if (this._inScrollMode) {
         if (alt) {
            this.scroll(amount, 0);
            return 0;
         } else {
            this.scroll(0, amount);
            return 0;
         }
      } else {
         int remaining = amount;
         if (focusField != null) {
            if (!this.isForeignObject(focusField)) {
               remaining = super.moveFocus(amount, status, time);
            } else {
               remaining = focusField.moveFocus(amount, status, time);
               if ((status & 8) != 0) {
                  remaining = 0;
               }

               if (remaining != amount && !(focusField instanceof Object)) {
                  focusField.focusChangeNotify(2);
               }

               if (remaining != 0) {
                  int direction = MathUtilities.clamp(-1, amount, 1);
                  FocusInteractor cia = this._focusInteractor;
                  if (cia != null) {
                     int hotspot = cia.getItemInFocus();
                     int next = cia.getNextFocusableItem(direction > 0 ? 1 : -1, hotspot, false);
                     if (next != hotspot) {
                        setFocus(this._controller, direction);
                     } else {
                        boolean wrap = (this.getFieldStyle() & 262144) != 0 && cia.getWrap();
                        int nextField = this.getNextFocusableField(direction, 0, wrap);
                        nextField = !wrap && 0 == nextField ? -1 : nextField;
                        if (nextField == 0) {
                           if (cia.getNextFocusableItem(direction > 0 ? 1 : -1, hotspot, true) == hotspot) {
                              return remaining;
                           }

                           setFocus(this._controller, direction);
                           cia.moveFocus(remaining >= 0 ? 1 : -1, Math.abs(remaining), true);
                           remaining = 0;
                        } else {
                           if (nextField == -1) {
                              return remaining;
                           }

                           setFocus(this.getField(nextField), direction);
                           remaining -= direction;
                        }
                     }
                  }

                  if (remaining != 0) {
                     remaining = super.moveFocus(remaining, status, time);
                  }
               }
            }
         }

         if (this._services != null && this._services.getEngine().isRunning()) {
            this._services.getEngine().dispatchEvents();
         }

         return remaining;
      }
   }

   public MediaField() {
      this(18014398509481984L);
   }

   static void setFocus(Field field, int direction) {
      Manager m = field.getManager();
      if (m != null) {
         Field focus = m.getFieldWithFocus();
         if (focus != field && field.isFocusable()) {
            Screen scr = field.getScreen();
            if (scr != null) {
               scr.doRemoveFocus();
            }

            m.setFieldWithFocus(field);
            if (focus != null) {
               focus.onUnfocus();
               focus.focusChangeNotify(3);
            }

            field.onFocus(direction);
            field.focusChangeNotify(1);
            m.focusChangeNotify(2);
            if (scr != null) {
               scr.doAddFocus(true);
            }
         }
      }
   }

   @Override
   protected int nextFocus(int direction, boolean alt) {
      FocusInteractor cia = this._focusInteractor;
      if (cia == null) {
         return super.nextFocus(direction, alt);
      }

      Field inFocus = this.getFieldWithFocus();
      if (this.isForeignObject(inFocus)) {
         return 0;
      }

      if (inFocus == this._controller) {
         int currentHotspot = cia.getItemInFocus();
         int nextHs = cia.getNextFocusableItem(direction > 0 ? 1 : -1, currentHotspot, false);
         if (nextHs != currentHotspot) {
            return 0;
         }
      }

      boolean wrap = (this.getFieldStyle() & 262144) != 0 && cia.getWrap();
      int inFocusIndex = this.getFieldWithFocusIndex();
      int next = this.getNextFocusableField(direction, inFocusIndex, wrap);
      return !wrap && inFocusIndex == next ? -1 : next;
   }

   private int getNextFocusableField(int direction, int curItem, boolean wrap) {
      int fieldCount = this.getFieldCount();
      if (fieldCount == 0) {
         return -1;
      }

      boolean next = direction > 0;
      int i;
      int curr;
      if (curItem < 0) {
         if (next) {
            curr = fieldCount - 1;
            i = -1;
         } else {
            i = fieldCount;
            curr = 0;
         }
      } else {
         i = curItem;
         curr = curItem;
      }

      do {
         i += next ? 1 : -1;
         if (wrap) {
            i = (i + fieldCount) % fieldCount;
         } else if (i < 0 || i >= fieldCount) {
            break;
         }

         Field f = this.getField(i);
         if (f.isFocusable() && !this.isForeignObject(f)) {
            return i;
         }
      } while (i != curr);

      if (curItem != -1) {
         Field focus = this.getField(curItem);
         if (focus == null || !focus.isFocusable()) {
            curItem = -1;
         }
      }

      return curItem;
   }

   @Override
   protected int firstFocus(int direction) {
      int result = -1;
      FocusInteractor cia = this._focusInteractor;
      if (cia != null) {
         int hotspot = cia.getFirstFocusableItem(direction >= 0 ? 1 : -1);
         if (hotspot != -1) {
            Object f = cia.getWrappedObject(hotspot);
            if (f != null) {
               for (int i = 0; i < this.getFieldCount(); i++) {
                  if (f == this.getField(i)) {
                     result = i;
                     break;
                  }
               }
            }
         }
      }

      if (result == -1) {
         result = this.getNextFocusableField(direction, -1, false);
      }

      return result;
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      if (added == 1) {
         Field f = this.getField(index);
         if (f != null) {
            ForeignObject fo = (ForeignObject)this._mapFieldToFO.get(this.getField(index));
            if (fo != null) {
               this.layoutChild(f, fo.getWidth(), fo.getHeight());
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected void sublayout(int availWidth, int availHeight) {
      this.assertHaveEventLock();
      long style = this.getStyle();
      this._virtExtent.set(0, 0, 0, 0);
      int fieldWidth;
      int fieldHeight;
      if (this._viewport != null && this._viewport.getMedia() != null) {
         int minPreferredAvailWidth = (style & 8388608) == 8388608 && this._preferredWidth < availWidth ? this._preferredWidth : availWidth;
         int minPreferredAvailHeight = (style & 16777216) == 16777216 && this._preferredHeight < availHeight ? this._preferredHeight : availHeight;
         this._viewport.setExtent(minPreferredAvailWidth, minPreferredAvailHeight);
         this._virtExtent.width = this._viewport.getVirtualWidth();
         this._virtExtent.height = this._viewport.getVirtualHeight();
         this.layoutFields(this._virtExtent);
         if ((style & 1152921504606846976L) == 1152921504606846976L) {
            fieldWidth = availWidth;
            this._virtExtent.width = Math.max(availWidth, this._virtExtent.width);
         } else if (this._viewport.isContentWidthAbsolute()) {
            fieldWidth = Math.min(availWidth, this._virtExtent.width);
         } else {
            fieldWidth = minPreferredAvailWidth;
            this._virtExtent.width = minPreferredAvailWidth;
         }

         if ((style & 2305843009213693952L) == 2305843009213693952L) {
            fieldHeight = availHeight;
            this._virtExtent.height = Math.max(availHeight, this._virtExtent.height);
         } else if (this._viewport.isContentHeightAbsolute()) {
            fieldHeight = Math.min(availHeight, this._virtExtent.height);
         } else {
            fieldHeight = minPreferredAvailHeight;
            this._virtExtent.height = minPreferredAvailHeight;
         }

         this._viewportManager.layout(fieldWidth, fieldHeight);
      } else {
         this.layoutFields(this._virtExtent);
         if ((style & 1152921504606846976L) == 1152921504606846976L) {
            fieldWidth = availWidth;
            this._virtExtent.width = Math.max(availWidth, this._virtExtent.width);
         } else {
            fieldWidth = Math.min(availWidth, this._virtExtent.width);
         }

         if ((style & 2305843009213693952L) == 2305843009213693952L) {
            fieldHeight = availHeight;
            this._virtExtent.height = Math.max(availHeight, this._virtExtent.height);
         } else {
            fieldHeight = Math.min(availHeight, this._virtExtent.height);
         }
      }

      this.setExtent(fieldWidth, fieldHeight);
      this.setVirtualExtent(this._virtExtent.width, this._virtExtent.height);
   }

   private void layoutFields(XYRect virtExtent) {
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         this.layoutChild(field, field.getWidth(), field.getHeight());
         if (!this.isForeignObject(field)) {
            field.getExtent(this._rect);
            virtExtent.union(this._rect);
         }
      }
   }

   public MediaField(long style, long contentStyle) {
      super(style | 144115188075855872L);
      if (!this.validateStyle(style)) {
         throw new Object();
      }

      this.updateViewportManager(contentStyle);
      this._controller = new MediaController(style & 18014398512889856L);
      this.add(this._controller);
      this._inScrollMode = false;
      this._mapFieldToFO = (Hashtable)(new Object());
      this.setFocusListener(new MediaField$MFFocusChangeListener(this));
   }

   private boolean validateStyle(long style) {
      return true;
   }

   @Override
   protected void subpaint(Graphics graphics) {
      if (this._viewport != null && this._viewport.getMedia() != null) {
         XYRect clip = graphics.getClippingRect();
         this._viewport.paint(graphics, clip.x, clip.y, clip.width, clip.height);
      }

      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         if (!this.isForeignObject(field)) {
            this.paintChild(graphics, field);
         }
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
   }

   @Override
   public void invalidate() {
      MediaViewport vp = this._viewport;
      if (vp != null && vp.getMedia() != null) {
         vp.invalidate();
      }

      super.invalidate();
   }

   private final void repaint(boolean full) {
      this.assertHaveEventLock();
      int repaintX = 0;
      int repaintY = 0;
      int repaintW = this.getWidth();
      int repaintH = this.getHeight();
      if (!full) {
         MediaViewport vp = this._viewport;
         if (vp != null && vp.getMedia() != null) {
            vp.invalidate();
            repaintX = vp.getDirtyX();
            repaintY = vp.getDirtyY();
            repaintW = vp.getDirtyWidth();
            repaintH = vp.getDirtyHeight();
         }
      }

      if (this.isVisible()) {
         this.invalidate(repaintX, repaintY, repaintW, repaintH);
      }
   }

   public MediaField(long style) {
      this(style, 21474836480L);
   }

   @Override
   public int getPreferredWidth() {
      if ((this.getStyle() & 8388608) == 8388608 && this._preferredWidth != -1) {
         return this._preferredWidth;
      }

      if (this._virtExtent.width == -1) {
         this.calculateVirtualExtent();
      }

      return this._virtExtent.width;
   }

   @Override
   protected void makeFocusVisible(boolean immediate, XYRect region, boolean draw, boolean reset) {
   }

   private void calculateVirtualExtent() {
      int w = this._viewport != null ? this._viewport.getVirtualWidth() : 0;
      int h = this._viewport != null ? this._viewport.getVirtualHeight() : 0;
      this.sublayout(w, h);
   }

   public static void updateScreen(Screen scrn) {
      if (scrn != null) {
         UiEngineImpl display = scrn.getUiEngineImpl();
         if (display != null) {
            display.invalidateTransparentScreens(scrn);
         }
      }
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      if (this._player != null) {
         if (visible) {
            this.startPlayer();
            if (this._modelInteractor != null) {
               int onVisibleEvent = this._modelInteractor.getHandle("onVisible");
               if (onVisibleEvent != -1) {
                  this._modelInteractor.trigger(107, onVisibleEvent, null);
               }
            }

            super.invalidate();
         } else {
            if (this._modelInteractor != null) {
               int onInvisibleEvent = this._modelInteractor.getHandle("onInvisible");
               if (onInvisibleEvent != -1) {
                  this._modelInteractor.trigger(107, onInvisibleEvent, null);
               }
            }

            this.stopPlayer();
         }

         super.onVisibilityChange(visible);
      }
   }

   @Override
   protected void onObscured() {
      if (this._modelInteractor != null) {
         int onObscuredEventHandle = this._modelInteractor.getHandle("onObscured");
         if (onObscuredEventHandle != -1) {
            this._modelInteractor.trigger(107, onObscuredEventHandle, null);
         }
      }

      super.onObscured();
   }

   @Override
   protected void onExposed() {
      if (this._modelInteractor != null) {
         int onExposedEventHandle = this._modelInteractor.getHandle("onExposed");
         if (onExposedEventHandle != -1) {
            this._modelInteractor.trigger(107, onExposedEventHandle, null);
         }
      }

      MediaViewport vp = this._viewport;
      if (vp != null && vp.getMedia() != null) {
         vp.invalidate(vp.getOriginX(), vp.getOriginY(), vp.getVirtualWidth(), vp.getVirtualHeight());
      }

      super.onExposed();
   }

   private void stopPlayer() {
      if (this._player != null) {
         if (this._player.getState() == 2) {
            this._player.stop();
            this._playerStop = true;
         }
      }
   }

   private void startPlayer() {
      if (this._player != null) {
         if (this._playerStop) {
            this._playerStop = false;

            try {
               if (this._player.getState() == 1) {
                  this._player.start();
                  return;
               }
            } catch (MediaException var2) {
            }
         }
      }
   }
}
