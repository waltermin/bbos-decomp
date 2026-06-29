package net.rim.device.api.ui;

import java.io.IOException;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.MediaPlayerState;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.Events;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.Background;
import net.rim.device.internal.ui.BackingStore;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.UiOptionsRegistry;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.TraceBack;

public class Screen extends Manager {
   private Object[] _focusChangeListeners;
   private Object[] _keyListeners;
   private Object[] _paintabilityListeners;
   private Object[] _screenUiEngineAttachedListeners;
   private Object[] _stylusListeners;
   private Object[] _trackwheelListeners;
   private Manager _delegate;
   private XYRect _invalid = new XYRect();
   int _layoutGeneration;
   private int _themeGeneration;
   private int _inPaint;
   private int _inLayout;
   private UiEngineImpl _uiEngine;
   private boolean _globalScreen;
   private boolean _acceptsInput = true;
   private boolean _inputStartReceived;
   private boolean _transparentBorder;
   private boolean _transparentBackground;
   private int _backdoorCode;
   private int _backdoorAltStatus = 1;
   private int _screenState;
   private Graphics _graphicsInUse;
   private Menu _menu;
   private BackingStore _backingStore;
   private boolean _scrollBehaviourView;
   private boolean _scrollBehaviourSelect;
   private int _trackballSensitivityXOffset = Integer.MAX_VALUE;
   private int _trackballSensitivityYOffset = Integer.MAX_VALUE;
   private int _trackballFilter = -1;
   private boolean _noSynchOnInvalidate;
   private XYRect _lastInvalid;
   private boolean _saveLastInvalid;
   private boolean _clickHandled;
   private int _pushMethod;
   private boolean _dismissing = false;
   private boolean _clearBackingStore;
   private boolean _paintController;
   private boolean _backingStoreUpdated;
   private int _superCalled;
   private static Tag TAG = Tag.create("window");
   private static Tag TAG_ROOT = Tag.create("");
   public static final long DEFAULT_MENU;
   public static final long TRANSPARENT;
   public static final long DEFAULT_CLOSE;
   private static final long SCREEN_STYLES;
   private static final int STATE_UI_ENGINE_ATTACHED;
   private static final int STATE_NO_GATE_INPUT;
   private static final int STATE_IS_OBSCURED;
   private static final int STATE_IS_VISIBLE;
   private static final int STATE_IS_PAINTABLE;
   private static final int STATE_CATCH_PAINT_EXCEPTIONS;
   private static final int STATE_HAS_FOCUS;
   private static final int STATE_STATUS_TOGGLE;
   private static final int STATUS_LEGACY_MASK;
   private static final int STATUS_TOGGLE_BIT;
   private static final boolean DEBUG_INVALID_REGION;
   private static final int SUPER_UNUSED;
   private static final int SUPER_ON_UI_ENGINE_ATTACHED;
   private static final int FIELD_VISITOR_TYPE_STATE_IS_PAINTABLE;

   public void addFocusChangeListener(FocusChangeListener listener) {
      this._focusChangeListeners = ListenerUtilities.addListener(this._focusChangeListeners, listener);
   }

   public final synchronized void addKeyListener(KeyListener listener) {
      if (listener instanceof Field) {
         throw new IllegalArgumentException("A key listener cannot be a Field or a Screen.");
      }

      if (!ControlledAccess.verifyRRISignatures(true) && !CodeStore.isPartOfCurrentApp(TraceBack.getCallingModule(0))) {
         throw new ControlledAccessException("Unauthorized attempt to monitor key presses");
      }

      this._keyListeners = ListenerUtilities.addListener(this._keyListeners, listener);
   }

   public void addPaintabilityListener(PaintabilityListener listener) {
      this._paintabilityListeners = ListenerUtilities.addListener(this._paintabilityListeners, listener);
   }

   public void addScreenUiEngineAttachedListener(ScreenUiEngineAttachedListener listener) {
      this._screenUiEngineAttachedListeners = ListenerUtilities.addListener(this._screenUiEngineAttachedListeners, listener);
   }

   public final synchronized void addStylusListener(StylusListener listener) {
      if (listener instanceof Field) {
         throw new IllegalArgumentException("A stylus listener cannot be a Field or a Screen.");
      }

      this._stylusListeners = ListenerUtilities.addListener(this._stylusListeners, listener);
   }

   public final synchronized void addTrackwheelListener(TrackwheelListener listener) {
      if (listener instanceof Field) {
         throw new IllegalArgumentException("A trackwheel listener cannot be a Field or a Screen.");
      }

      this._trackwheelListeners = ListenerUtilities.addListener(this._trackwheelListeners, listener);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void callOnUiEngineAttached(boolean attached) {
      if (attached) {
         if (this.isScreenState(1)) {
            throw new IllegalStateException("onUiEngineAttached(true) of screen already attached");
         }

         this.setScreenState(1, true);
         if (this.isScreenState(4)) {
            throw new IllegalStateException("onUiEngineAttached(true) of an obscured screen");
         }

         this.onDisplay();
      } else {
         this.callOnUiEngineDettachedWithoutNotify();
         this.onUndisplay();
      }

      this.beginSuperCalled(1);
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         this.onUiEngineAttached(attached);
         var7 = false;
      } finally {
         if (var7) {
            this.assertSuperCalled(1);
         }
      }

      this.assertSuperCalled(1);
      Object[] listeners = this._screenUiEngineAttachedListeners;
      if (listeners != null) {
         for (int index = 0; index < listeners.length; index++) {
            try {
               ((ScreenUiEngineAttachedListener)listeners[index]).onScreenUiEngineAttached(this, attached);
            } catch (Throwable var8) {
            }
         }
      }

      this._delegate.callOnDisplayOrUndisplay(attached);
   }

   final void callOnUiEngineDettachedWithoutNotify() {
      if (!this.isScreenState(1)) {
         throw new IllegalStateException("onUiEngineAttached(false) of screen not attached");
      }

      this.setScreenState(1, false);
      this.setScreenState(4, false);
      Graphics.releaseGraphics(this);
      Screen$LocationFocusSelector.getSelector(null, 0, 0, 0, 0);
      Screen$PagingFocusSelector.getSelector(null, 0);
      Screen$NavigationMovementFocusSelector.getSelector(null, 0, 0, 0, 0);
      Screen$TrackwheelRollFocusSelector.getSelector(null, 0, 0, 0);
      Screen$FindNewFocusSelector.getSelector(null, false);
      Screen$ViewFocusSelector.getSelector(null, 0, 0, 0);
      Screen$SetFocusSelector.getSelector(null, null);
   }

   final void clearBackingStore() {
      this._backingStore = null;
   }

   final void cleanBackingStore() {
      if (this._backingStore != null) {
         this._backingStore.cleanBackingStore();
      }
   }

   final void setClearBackingStore(boolean val) {
      this._clearBackingStore = val;
   }

   public void close() {
      UiEngineImpl uiEngine = this._uiEngine;
      if (uiEngine == null) {
         throw new IllegalStateException("close() called when not displayed.");
      }

      boolean global = this.isGlobal();
      uiEngine.popScreen(this);
      if (!global && uiEngine.getScreenCount() == 0 && (uiEngine.getUiApplicationStyle() & 1) == 0) {
         System.exit(0);
      }
   }

   public boolean dispatchKeyEvent(int event, char key, int status, int time) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      boolean result = false;
      switch (event) {
         case 513:
            result = this.keyDown(status, time);
            break;
         case 514:
            result = this.keyRepeat(status, time);
            break;
         case 515:
            result = this.keyUp(status, time);
            break;
         case 520:
            result = this.keyStatus(status, time);
            break;
         case 32768:
            if (128 <= key && key <= 159) {
               result = this.keyControl(key, status & 65535, time);
            } else {
               result = this.keyChar(key, status & 65535, time);
            }
      }

      Object[] listeners = this._keyListeners;
      if (!result && listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               if (Events.dispatchKeyEvent(event, key, status, time, (KeyListener)listeners[i])) {
                  return true;
               }
            } catch (Throwable var9) {
            }
         }
      }

      if (!result && event == 32768) {
         result = this.keyCharUnhandled(key, status, time);
      }

      return result;
   }

   public boolean dispatchStylusEvent(int event, int x, int y, int status, int time) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (this.blockInputEvents(event, 0)) {
         return false;
      }

      boolean result = false;
      XYRect extent = this.getExtent();
      switch (event) {
         case 6655:
            break;
         case 6656:
         default:
            result = this.stylusDown(x - extent.x, y - extent.y, status, time);
            if (!result) {
               this.setFocus(this._delegate, x - extent.x, y - extent.y, status, time);
            }
            break;
         case 6657:
            result = this.stylusDrag(x - extent.x, y - extent.y, status, time);
            break;
         case 6658:
            result = this.stylusUp(x - extent.x, y - extent.y, status, time);
            break;
         case 6659:
            result = this.stylusTap(x - extent.x, y - extent.y, status, time);
            break;
         case 6660:
            result = this.stylusTapHold(x - extent.x, y - extent.y, status, time);
            break;
         case 6661:
            result = this.stylusDoubleTap(x - extent.x, y - extent.y, status, time);
      }

      Object[] listeners = this._stylusListeners;
      if (!result && listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               if (Events.dispatchStylusEvent(event, x - extent.x, y - extent.y, status, time, (StylusListener)listeners[i])) {
                  return true;
               }
            } catch (Throwable var11) {
            }
         }
      }

      if (!result && !DeviceInfo.isInHolster()) {
         switch (event) {
            case 6660:
               result = this.onMenu(0);
         }
      }

      return result;
   }

   boolean dispatchNavigationEvent(int event, int dx, int dy, int status, int time) {
      if (this.blockInputEvents(event, 0)) {
         return false;
      }

      int statusToggle = 0;
      if (this.isScreenState(128)) {
         this.setScreenState(128, false);
         statusToggle = Integer.MIN_VALUE;
      } else {
         this.setScreenState(128, true);
      }

      if (this.processNavigationEvent(event, dx, dy, status, time)) {
         this._clickHandled = event == 516 || event == 6914;
         return true;
      }

      boolean clickHandled = this._clickHandled;
      boolean result = false;
      switch (event) {
         case 516:
            result = this.trackwheelClick(status, time);
            if (!result) {
               result = this.navigationClick(status | statusToggle, time);
            }

            clickHandled = result;
            this._clickHandled = result;
            break;
         case 517:
            result = this.trackwheelUnclick(status, time);
            if (!result) {
               result = this.navigationUnclick(status | statusToggle, time);
            }

            this._clickHandled = false;
            break;
         case 519:
            if ((dx | dy) == 0) {
               return true;
            }

            result = this.trackwheelRoll(dy, status & -1610612737, time);
            if (!result) {
               result = this.navigationMovement(dx, dy, status | statusToggle, time);
            }
            break;
         case 6913:
            if ((dx | dy) == 0) {
               return true;
            }

            result = this.navigationMovement(dx, dy, status | statusToggle, time);
            if (!result) {
               result = this.trackwheelRoll(dy, status & -1610612737, time);
            }
            break;
         case 6914:
            result = this.navigationClick(status | statusToggle, time);
            if (!result) {
               result = this.trackwheelClick(status, time);
            }

            clickHandled = result;
            this._clickHandled = result;
            break;
         case 6915:
            result = this.navigationUnclick(status | statusToggle, time);
            if (!result) {
               result = this.trackwheelUnclick(status, time);
            }

            this._clickHandled = false;
      }

      Object[] listeners = this._trackwheelListeners;
      if (!result && listeners != null) {
         int wheelEvent = 0;
         switch (event) {
            case 516:
            case 517:
            case 519:
               wheelEvent = event;
               break;
            case 6913:
               if (dy != 0) {
                  wheelEvent = 519;
               }
               break;
            case 6914:
               wheelEvent = 516;
               break;
            case 6915:
               wheelEvent = 517;
         }

         if (wheelEvent != 0) {
            for (int i = listeners.length - 1; i >= 0; i--) {
               try {
                  if (Events.dispatchTrackwheelEvent(wheelEvent, dy, status & -1610612737, time, (TrackwheelListener)listeners[i])) {
                     this._clickHandled = clickHandled = wheelEvent == 516;
                     return true;
                  }
               } catch (Throwable var13) {
               }
            }
         }
      }

      if (!result && !clickHandled) {
         if (event == 6915) {
            result = this.navigationUnclickUnhandled(status | statusToggle, time);
         } else if (event == 516) {
            result = this.trackwheelClickUnhandled(status, time);
         }
      }

      return result;
   }

   public boolean dispatchTrackwheelEvent(int event, int magnitude, int status, int time) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this.dispatchNavigationEvent(event, 0, magnitude, status | 1073741824, time);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void doLayout() {
      if (this._layoutGeneration != UiEngineImpl._layoutGeneration || !this.isValidLayout()) {
         this.assertHaveEventLock();
         int themeGeneration = ThemeManager.getGeneration();
         if (this._themeGeneration != themeGeneration) {
            this.applyTheme();
            this._themeGeneration = themeGeneration;
         }

         this.applyFont();
         XYRect oldExtent = Ui.getTmpXYRect();
         this.getExtent(oldExtent);
         this._inLayout++;
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            int uiEngine = Display.getWidth();
            int height = Display.getHeight();
            uiEngine -= this.getBorderLeft() + this.getBorderRight() + this.getPaddingLeft() + this.getPaddingRight();
            height -= this.getBorderTop() + this.getBorderBottom() + this.getPaddingTop() + this.getPaddingBottom();
            this.layout(uiEngine, height);
            this.invalidate(0, 0, this.getContentWidth(), this.getContentHeight());
            this.setValidLayout(true);
            this._layoutGeneration = UiEngineImpl._layoutGeneration;
            this.ensureFocusVisible();
            var8 = false;
         } finally {
            if (var8) {
               this._inLayout--;
               if (this.isUiEngineAttached() && oldExtent.width > 0 && oldExtent.height > 0 && !oldExtent.equals(this.getExtent())) {
                  UiEngineImpl uiEnginex = this.getUiEngineImpl();
                  if (uiEnginex != null) {
                     uiEnginex.appInvalidate(oldExtent.x, oldExtent.y, oldExtent.width, oldExtent.height);
                  }
               }

               Ui.returnTmpXYRect(oldExtent);
            }
         }

         this._inLayout--;
         if (this.isUiEngineAttached() && oldExtent.width > 0 && oldExtent.height > 0 && !oldExtent.equals(this.getExtent())) {
            UiEngineImpl uiEngine = this.getUiEngineImpl();
            if (uiEngine != null) {
               uiEngine.appInvalidate(oldExtent.x, oldExtent.y, oldExtent.width, oldExtent.height);
            }
         }

         Ui.returnTmpXYRect(oldExtent);
      }

      this.assertLayoutCompleteOnScreen();
   }

   public final void doPaint() {
      UiEngineImpl engine = this._uiEngine;
      if (engine != null) {
         engine.doPainting();
      }
   }

   final void doPaintInternal(XYRect invalid) {
      if (invalid != null && !invalid.isEmpty()) {
         synchronized (this._invalid) {
            this._invalid.union(invalid);
         }
      }

      if (!this.isScreenState(32)) {
         this.doPaint0(false, false);
      } else {
         try {
            this.doPaint0(false, false);
         } catch (Throwable var4) {
         }
      }
   }

   final boolean isBackingStoreUpdated() {
      return this._backingStoreUpdated;
   }

   final void setBackingStoreUpdated(boolean val) {
      this._backingStoreUpdated = val;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void doPaint0(boolean forceAllowDrawing, boolean paintToBackingStore) {
      UiEngineImpl engine = this._uiEngine;
      if (engine != null) {
         if (this.isScreenState(16) || forceAllowDrawing) {
            engine.assertHaveEventLock();
            XYRect extent = this.getExtent();
            if (paintToBackingStore && this._backingStore == null) {
               if (extent.width != Display.getWidth() || extent.height != Display.getHeight()) {
                  this._backingStore = new BackingStore(extent.width, extent.height, this.isTransparent() || this.isTransparentBorder());
               } else if (!this.isTransparent() && !this.isTransparentBorder()) {
                  this._backingStore = GlobalScreenManager.getBackingStore();
               } else {
                  this._backingStore = GlobalScreenManager.getTransparentBackingStore();
               }
            }

            this._inPaint++;
            boolean var12 = false /* VF: Semaphore variable */;

            label131: {
               try {
                  var12 = true;
                  Graphics graphics = null;
                  synchronized (this._invalid) {
                     if (!this._invalid.isEmpty()) {
                        if (paintToBackingStore) {
                           XYRect invalid = Ui.getTmpXYRect();
                           invalid.set(this._invalid);
                           invalid.translate(-extent.x, -extent.y);
                           if (this._backingStore != null) {
                              graphics = this._backingStore.getGraphics(invalid);
                           }

                           Ui.returnTmpXYRect(invalid);
                        } else {
                           graphics = this.getGraphics(this._invalid, forceAllowDrawing);
                        }

                        if (this._saveLastInvalid) {
                           this._lastInvalid = Ui.getTmpXYRect();
                           this._lastInvalid.set(this._invalid);
                        }

                        this._invalid.set(0, 0, 0, 0);
                     }
                  }

                  this._graphicsInUse = graphics;
                  if (graphics == null) {
                     var12 = false;
                     break label131;
                  }

                  if (this._uiEngine == null) {
                     var12 = false;
                     break label131;
                  }

                  this.paintSelf(graphics, false, 0, 0);
                  if (paintToBackingStore) {
                     if (this._backingStore != null) {
                        this._backingStore.updateBuffers();
                        this._backingStoreUpdated = true;
                        var12 = false;
                     } else {
                        var12 = false;
                     }
                  } else {
                     var12 = false;
                  }
               } finally {
                  if (var12) {
                     this._graphicsInUse = null;
                     this._inPaint--;
                  }
               }

               this._graphicsInUse = null;
               this._inPaint--;
               return;
            }

            this._graphicsInUse = null;
            this._inPaint--;
         }
      }
   }

   public boolean doSaveInternal() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this.onSave();
   }

   void ensureFocusVisible() {
      Field focus = this.getLeafFieldWithFocus();
      if (focus != null) {
         XYRect rect = Ui.getTmpXYRect();
         focus.getFocusRectPhantom(rect);
         this.ensureRegionVisible(focus, rect.x, rect.y, rect.width, rect.height, false, true);
         Ui.returnTmpXYRect(rect);
      }
   }

   public void ensureRegionVisible(Field field, int x, int y, int width, int height) {
      boolean immediate = this._invalid.width == 0 || this._invalid.height == 0;
      this.ensureRegionVisible(field, x, y, width, height, immediate, true);
   }

   void focusChangeNotifyListeners(Field field, int eventType) {
      Object[] listeners = this._focusChangeListeners;
      if (listeners != null) {
         for (int index = 0; index < listeners.length; index++) {
            try {
               ((FocusChangeListener)listeners[index]).focusChanged(field, eventType);
            } catch (Throwable var6) {
            }
         }
      }
   }

   final boolean acceptsInput() {
      return this._acceptsInput;
   }

   public Application getApplication() {
      return this._uiEngine != null ? this._uiEngine.getApplication() : null;
   }

   final BackingStore getBackingStore() {
      return this._backingStore;
   }

   public Manager getDelegate() {
      return this._delegate;
   }

   public Graphics getGraphics() {
      if (this._inPaint != 0) {
         throw new IllegalStateException("Screen.getGraphics called during a paint operation.");
      }

      Graphics graphics = this.getGraphics0(null, false);
      graphics.pushRegion(0, 0, this.getContentWidth(), this.getContentHeight(), 0, 0);
      return graphics;
   }

   Graphics getGraphics(XYRect clip, boolean forceAllowDrawing) {
      this.assertHaveEventLock();
      if (!forceAllowDrawing) {
         UiEngine ui = this.getUiEngine();
         if (ui == null || !this.isScreenState(16)) {
            return Graphics.getNullGraphics();
         }
      }

      Graphics graphics = Graphics.getGraphics(this);
      XYRect extent = this.getExtent();
      if (clip != null) {
         graphics.pushContext(clip, extent.x, extent.y);
         return graphics;
      } else {
         graphics.pushRegion(extent);
         return graphics;
      }
   }

   final XYRect getInvalid() {
      return this._invalid;
   }

   XYRect getLastInvalid() {
      return this._lastInvalid;
   }

   public Menu getMenu(int instance) {
      Menu menu = new Menu(65536);
      Menu.setTargetScreen(this);
      menu.setTargetScreenVirtual(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      return menu;
   }

   public Screen getScreenAbove() {
      UiEngineImpl uie = this._uiEngine;
      if (uie == null) {
         throw new IllegalStateException();
      } else {
         return uie.getScreenAbove(this);
      }
   }

   public Screen getScreenBelow() {
      UiEngineImpl uie = this._uiEngine;
      if (uie == null) {
         throw new IllegalStateException();
      } else {
         return uie.getScreenBelow(this);
      }
   }

   public int getTrackballSensitivityXOffset() {
      return this._trackballSensitivityXOffset;
   }

   public int getTrackballSensitivityYOffset() {
      return this._trackballSensitivityYOffset;
   }

   public final UiEngine getUiEngine() {
      UiEngineImpl.assertIpcOrDependency();
      return this._uiEngine;
   }

   final UiEngineImpl getUiEngineImpl() {
      return this._uiEngine;
   }

   public void invalidateLayout() {
      if (this._uiEngine != null) {
         throw new IllegalStateException();
      }

      this.invalidateLayout0();
   }

   boolean isCatchPaintExceptions() {
      return this.isScreenState(32);
   }

   final boolean isClearBackingStore() {
      return this._clearBackingStore;
   }

   boolean isDismissing() {
      return this._dismissing;
   }

   public boolean isGlobal() {
      return this._globalScreen;
   }

   public boolean isGlobalStatus() {
      return this.isGlobal();
   }

   boolean isInLayout() {
      return this._inLayout != 0;
   }

   public boolean isObscured() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this.isScreenState(4);
   }

   boolean isPaintController() {
      return this._paintController;
   }

   public boolean isScrollBehaviourView() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this.isScrollBehaviourViewInternal();
   }

   public final boolean isTransparent() {
      return this.isStyle(68719476736L) || this.isTransparentBackground();
   }

   protected boolean isTransparentBackground() {
      return this._transparentBackground;
   }

   public final boolean isTransparentBorder() {
      return this._transparentBorder;
   }

   public boolean isUiEngineAttached() {
      return this.isScreenState(1);
   }

   final boolean isValid() {
      UiEngineImpl engine = this._uiEngine;
      return engine != null && !engine.isValid() ? false : this._invalid.width == 0 || this._invalid.height == 0;
   }

   void setSaveLastInvalid(boolean val) {
      this._saveLastInvalid = val;
   }

   boolean isScreenFocus() {
      return this.isScreenState(64);
   }

   public boolean invokeDefaultMenuItem(int instance) {
      boolean result = false;
      if (this.isStyle(65536) && !DeviceInfo.isInHolster()) {
         MenuItem item = this.getDefaultMenuItem(instance);
         if (item != null) {
            item.run();
            result = true;
         }

         ContextMenu.getInstance().setTarget(null);
         Menu.setTargetScreen(null);
      }

      return result;
   }

   public MenuItem getDefaultMenuItem(int instance) {
      MenuItem menuItem = null;
      if (this.isStyle(65536)) {
         Menu menu = this.getMenu(instance);
         menuItem = menu.getDefault();
      }

      return menuItem;
   }

   final void invalidateInternal() {
      XYRect extent = this.getExtent();
      this.invalidateCommon(0, 0, extent.width, extent.height, extent);
   }

   final void invalidateInternal(int x, int y, int width, int height) {
      XYRect content = Ui.getTmpXYRect();
      this.getContentRect(content);
      this.invalidateCommon(x, y, width, height, content);
      Ui.returnTmpXYRect(content);
   }

   protected boolean openDevelopmentBackdoor(int backdoorCode) {
      return false;
   }

   protected boolean openProductionBackdoor(int backdoorCode) {
      return false;
   }

   protected boolean keyCharUnhandled(char key, int status, int time) {
      boolean result = false;
      if (key == 27) {
         if (this._scrollBehaviourSelect) {
            this._scrollBehaviourSelect = false;
            this.invalidate();
         } else {
            result = this.onClose();
         }
      } else if (key == 149 && Trackball.isSupported()) {
         if (Ui.getTrackballClickAction() != 0) {
            result = this.onMenu(1073741824);
         } else {
            result = this.invokeAction(1);
         }
      }

      return result;
   }

   protected boolean blockInputEvents(int event, int keycode) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      switch (event) {
         case 513:
         case 520:
            this._inputStartReceived = true;
            return false;
         case 514:
         case 515:
         case 32768:
            if (event == 514 && Keypad.key(keycode) == 20 && InputContext.getInstance().isSureType()) {
               return false;
            }

            if (!this._inputStartReceived && !this.isScreenState(2)) {
               return true;
            }
            break;
         case 6914:
            this._inputStartReceived = true;
            return false;
         case 6915:
            if (!this._inputStartReceived) {
               return true;
            }
      }

      return false;
   }

   protected boolean ignoreBacklightOffKeyEvent(int event, char key, int keycode, int time) {
      return true;
   }

   protected final void layoutDelegate(int width, int height) {
      this.layoutChild(this._delegate, width, height);
   }

   protected final void makeMenuWithContext(Menu menu, int instance) {
      Field field = this.getLeafFieldWithFocus();
      if (field != null) {
         if (instance != 0) {
            ContextMenu cmenu = field.getContextMenu(instance);
            menu.add(cmenu, true);
         } else {
            ContextMenu cmenu = field.getContextMenu();
            menu.add(cmenu, true);
         }
      } else {
         field = this;
      }

      while (field != null) {
         field.makeMenu(menu, instance);
         if (field == this) {
            return;
         }

         field = field.getManager();
      }
   }

   public boolean onClose() {
      boolean closeable = true;
      if (this.isStyle(131072)) {
         if (this.isDirty()) {
            closeable = this.onSavePrompt();
         }

         if (closeable) {
            this.close();
            return true;
         }
      }

      return false;
   }

   protected void onFocusNotify(boolean focus) {
      this._inputStartReceived = false;
      this.setScreenState(64, focus);
      this.applyTrackballOffsets();
      if (Ui.isTTSEnabled()) {
         if (focus) {
            this.accessibleEventOccurred(1, new Integer(this.getAccessibleStateSet()), new Integer(2), this);
            this.addAccessibleState(2);
            return;
         }

         this.removeAccessibleState(2);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean onMenu(int instance) {
      if (this.isStyle(65536) && !DeviceInfo.isInHolster()) {
         boolean menuKey = (instance & 1073741824) != 0;
         instance &= -1073741825;
         int menuStyle = UiOptionsRegistry.getInstance().getInt(8794318953449332169L);
         if (menuStyle == 1) {
            instance = 0;
            menuKey = true;
         }

         this._menu = this.getMenu(instance);
         if (instance == 65537 || instance == 65536) {
            this._menu.setAlignment(12884901888L, 34359738368L);
         } else if (instance == 0 && menuKey) {
            this._menu.setAlignment(4294967296L, 34359738368L);
         }

         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            this._menu.show();
            this.onMenuDismissed(this._menu);
            ContextMenu.getInstance().setTarget(null);
            Menu.setTargetScreen(null);
            var6 = false;
         } finally {
            if (var6) {
               this._menu = null;
            }
         }

         this._menu = null;
         return true;
      } else {
         return false;
      }
   }

   protected boolean onSave() {
      if (this.isDataValid()) {
         try {
            this.save();
            return true;
         } catch (IOException e) {
            Dialog.inform(CommonResource.getString(10018));
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean onSavePrompt() {
      return true;
   }

   protected void onUiEngineAttached(boolean attached) {
   }

   public void save() {
   }

   void doRemoveFocus() {
      UiEngineImpl engine = this._uiEngine;
      if (engine != null) {
         engine.assertHaveEventLock();
         Field focus = this._delegate.getLeafFieldWithFocus();
         if (focus != null) {
            XYRect rect = Ui.getTmpXYRect();
            focus.getFocusRect(rect);
            if (this.isScreenState(16)
               && !this.isTransparent()
               && focus.getBorder() == null
               && !engine.isPaintingSuspended()
               && !this._globalScreen
               && engine.allowImmediate(this)) {
               boolean drawBackground = focus.isFocusDrawn();
               if (!this._delegate.drawLeafFocus(drawBackground, false)) {
                  Graphics graphics = focus.getGraphics0(rect, drawBackground);
                  graphics.popContext();
                  this._graphicsInUse = graphics;
                  if (this._uiEngine != null) {
                     graphics.setDrawingStyle(8, false);
                     boolean orgdraw = Ui.DRAW_FOCUS_IN_PAINT;
                     Ui.DRAW_FOCUS_IN_PAINT = false;
                     if (!this.isScreenState(32)) {
                        focus.paintSelf(graphics, true, 0, 0);
                        if (!drawBackground) {
                           focus.drawFocus(graphics, false);
                        }
                     } else {
                        try {
                           focus.paintSelf(graphics, true, 0, 0);
                           if (!drawBackground) {
                              focus.drawFocus(graphics, false);
                           }
                        } catch (Throwable var8) {
                        }
                     }

                     Ui.DRAW_FOCUS_IN_PAINT = orgdraw;
                     graphics.setDrawingStyle(8, false);
                     engine.invalidateTransparentScreens(this);
                  }

                  this._graphicsInUse = null;
               }
            } else {
               focus.invalidate(rect.x, rect.y, rect.width, rect.height);
            }

            Ui.returnTmpXYRect(rect);
         }
      }
   }

   public void removeFocus() {
      this.doRemoveFocus();
      this.onUnfocus();
   }

   void doAddFocus(boolean draw) {
      Field focus = this.getLeafFieldWithFocus();
      UiEngineImpl engine = this._uiEngine;
      if (engine != null && focus != null) {
         if (draw) {
            engine.assertHaveEventLock();
         }

         boolean immediate = engine.allowImmediate(this);
         boolean paintable = this.isScreenState(16) && !engine.isPaintingSuspended();
         if (!paintable) {
            immediate = false;
            draw = false;
         }

         if (!this.isScrollBehaviourViewInternal()) {
            XYRect focusRect = Ui.getTmpXYRect();
            focus.getFocusRectPhantom(focusRect);
            boolean orgdraw = Ui.DRAW_FOCUS_IN_PAINT;
            Ui.DRAW_FOCUS_IN_PAINT = false;
            Field step = focus;

            for (Manager manager = focus.getManager(); step != this; manager = step.getManager()) {
               focusRect.translate(step.getContentLeft(), step.getContentTop());
               manager.makeFocusVisible(immediate, focusRect, true, false);
               focusRect.translate(-manager.getHorizontalScroll(), -manager.getVerticalScroll());
               step = manager;
            }

            Ui.DRAW_FOCUS_IN_PAINT = orgdraw;
            Ui.returnTmpXYRect(focusRect);
         }

         if (draw) {
            if (paintable && !this.isTransparent() && focus.getBorder() == null && !this._globalScreen && engine.allowImmediate(this)) {
               XYRect rect = Ui.getTmpXYRect();
               focus.getFocusRect(rect);
               if (!this._delegate.drawLeafFocus(false, true)) {
                  Graphics graphics = focus.getGraphics0(rect, false);
                  Ui.returnTmpXYRect(rect);
                  XYRect var13 = null;
                  this._graphicsInUse = graphics;
                  if (this._uiEngine != null) {
                     graphics.setDrawingStyle(8, true);
                     if (!this.isScreenState(32)) {
                        focus.drawFocus(graphics, true);
                     } else {
                        try {
                           focus.drawFocus(graphics, true);
                        } catch (Throwable var10) {
                        }
                     }

                     graphics.setDrawingStyle(8, true);
                     engine.invalidateTransparentScreens(this);
                  }

                  this._graphicsInUse = null;
                  return;
               }
            } else {
               XYRect rect = Ui.getTmpXYRect();
               this.getFocusRect(rect);
               this.invalidate(rect.x, rect.y, rect.width, rect.height);
               Ui.returnTmpXYRect(rect);
            }
         }
      }
   }

   protected final void setAcceptsInput(boolean acceptsInput) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._acceptsInput = acceptsInput;
      this.setPaintController(false);
   }

   protected void setBackdoorAltStatus(boolean altStatus) {
      this._backdoorAltStatus = altStatus ? 1 : 0;
   }

   public void setCatchPaintExceptions(boolean catchPaintExceptions) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.setScreenState(32, catchPaintExceptions);
   }

   protected void setDefaultClose(boolean provideDefaultClose) {
      if (provideDefaultClose) {
         this.setStyleSystem(131072, 0);
      } else {
         this.setStyleSystem(0, 131072);
      }
   }

   public void setScrollBehaviourView(boolean scrollBehaviourView) {
      this._scrollBehaviourView = scrollBehaviourView && UiOptionsRegistry.getInstance().getBoolean(9099188860628284837L);
   }

   public void setScrollBehaviourSelect(boolean scrollBehaviourSelect) {
      if (this._scrollBehaviourSelect != scrollBehaviourSelect) {
         this._scrollBehaviourSelect = scrollBehaviourSelect;
         this.invalidate();
      }
   }

   void findNewFocus(boolean down) {
      if (this._delegate.getFieldWithFocus() != null) {
         this.doFocusMove(true, true, Screen$FindNewFocusSelector.getSelector(this, down));
      }
   }

   void setFocus(Field field) {
      if (field instanceof Manager) {
         for (Field focusField = this.getFieldWithFocus(); focusField instanceof Manager; focusField = ((Manager)focusField).getFieldWithFocus()) {
            if (focusField == field) {
               return;
            }
         }
      } else if (this.getLeafFieldWithFocus() == field) {
         return;
      }

      this.doFocusMove(true, true, Screen$SetFocusSelector.getSelector(this, field));
   }

   protected boolean navigationUnclickUnhandled(int status, int time) {
      boolean result = false;
      if (!result && !DeviceInfo.isInHolster()) {
         if (Ui.getTrackballClickAction() == 0) {
            return this.onMenu(0);
         }

         Field leafWithFocus = this.getLeafFieldWithFocus();
         if (leafWithFocus != null && leafWithFocus.isSelecting()) {
            this.onMenu(65537);
            return true;
         }

         result = this.invokeAction(1);
         if (!result) {
            result = this.onMenu(65536);
         }
      }

      return result;
   }

   protected boolean trackwheelClickUnhandled(int status, int time) {
      boolean result = false;
      if (!result && !DeviceInfo.isInHolster()) {
         if (Ui.getTrackwheelClickAction() == 0) {
            return this.onMenu(0);
         }

         result = this.invokeAction(1);
      }

      return result;
   }

   public boolean defaultStylusAction(int context) {
      boolean result = false;
      if (this.isStyle(65536) && !DeviceInfo.isInHolster()) {
         MenuItem item = this.getDefaultMenuItem(context);
         if (item != null) {
            item.run();
            result = true;
         }

         ContextMenu.getInstance().setTarget(null);
         Menu.setTargetScreen(null);
      }

      return result;
   }

   public void removeFocusChangeListener(FocusChangeListener listener) {
      this._focusChangeListeners = ListenerUtilities.addListener(this._focusChangeListeners, listener);
   }

   public final synchronized void removeKeyListener(KeyListener listener) {
      this._keyListeners = ListenerUtilities.removeListener(this._keyListeners, listener);
   }

   public void removePaintabilityListener(PaintabilityListener listener) {
      this._paintabilityListeners = ListenerUtilities.addListener(this._paintabilityListeners, listener);
   }

   public final synchronized void removeScreenUiEngineAttachedListener(ScreenUiEngineAttachedListener listener) {
      this._screenUiEngineAttachedListeners = ListenerUtilities.removeListener(this._screenUiEngineAttachedListeners, listener);
   }

   public final synchronized void removeStylusListener(StylusListener listener) {
      this._stylusListeners = ListenerUtilities.removeListener(this._stylusListeners, listener);
   }

   public final synchronized void removeTrackwheelListener(TrackwheelListener listener) {
      this._trackwheelListeners = ListenerUtilities.removeListener(this._trackwheelListeners, listener);
   }

   public final boolean scroll(int direction) {
      if (!this.isDisplayed()) {
         throw new IllegalStateException("scroll called when screen is not on stack.");
      }

      Screen$PagingFocusSelector selector = Screen$PagingFocusSelector.getSelector(this, direction);
      this.doFocusMove(true, true, selector);
      return selector.getSuccess();
   }

   final void setUiEngine(UiEngineImpl uiEngine) {
      if (uiEngine != null && this._uiEngine != null) {
         throw new RuntimeException("Attempt to push Screen while already displayed!");
      }

      if (!this.isGlobal()) {
         if (this._uiEngine != null) {
            this._uiEngine.assertHaveEventLock();
         } else if (uiEngine != null) {
            uiEngine.assertHaveEventLock();
         }
      }

      this._uiEngine = uiEngine;
      if (this.isGlobal()) {
         Graphics graphics = this._graphicsInUse;
         if (graphics != null) {
            graphics.nullify();
         }
      }
   }

   public final boolean setFocus(Field field, int x, int y, int status, int time) {
      if (field.getScreen() != this) {
         throw new IllegalArgumentException();
      }

      Screen$LocationFocusSelector selector = Screen$LocationFocusSelector.getSelector(field, x, y, status, time);
      this.doFocusMove(true, true, selector);
      return selector.getSuccess();
   }

   public void setGateInput(boolean gated) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.setScreenState(2, !gated);
   }

   final void setGlobal(boolean on) {
      this._globalScreen = on;
   }

   void setPaintController(boolean on) {
      this._paintController = on;
   }

   protected final void setPositionDelegate(int x, int y) {
      this._delegate.setPosition(x, y);
   }

   public void setTrackballFilter(int filter) {
      if (filter != -1 && (filter & -8) != 0) {
         throw new IllegalArgumentException();
      }

      this._trackballFilter = filter;
      if (this.isScreenState(64)) {
         this.applyTrackballOffsets();
      }
   }

   public void setTrackballSensitivityXOffset(int trackballSensitivityXOffset) {
      if (trackballSensitivityXOffset >= -100 && (100 >= trackballSensitivityXOffset || trackballSensitivityXOffset == Integer.MAX_VALUE)) {
         this._trackballSensitivityXOffset = trackballSensitivityXOffset;
         if (this.isScreenState(64)) {
            this.applyTrackballOffsets();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setTrackballSensitivityYOffset(int trackballSensitivityYOffset) {
      if (trackballSensitivityYOffset >= -100 && (100 >= trackballSensitivityYOffset || trackballSensitivityYOffset == Integer.MAX_VALUE)) {
         this._trackballSensitivityYOffset = trackballSensitivityYOffset;
         if (this.isScreenState(64)) {
            this.applyTrackballOffsets();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void updateDisplay() {
      Graphics.updateDisplay();
   }

   void doPaintabilityWalk(boolean paintable) {
      if (this.isScreenState(16) != paintable) {
         this.setScreenState(16, paintable);
         Object[] listeners = this._paintabilityListeners;
         if (listeners != null) {
            for (int index = 0; index < listeners.length; index++) {
               try {
                  ((PaintabilityListener)listeners[index]).onPaintablityChange(paintable);
               } catch (Throwable var5) {
               }
            }
         }
      }
   }

   void doLayoutNoSynch() {
      this._noSynchOnInvalidate = true;
      this.doLayout();
      this._noSynchOnInvalidate = false;
   }

   void setPushMethod(int method) {
      this._pushMethod = method;
   }

   protected int getPushMethod() {
      return this._pushMethod;
   }

   void setDismissing(boolean val) {
      this._dismissing = val;
   }

   public final boolean isDisplayed() {
      return this._uiEngine != null;
   }

   @Override
   protected void onDisplay() {
      this._inputStartReceived = false;
      Field defaultFocus = this.getLeafFieldWithFocus();
      if (defaultFocus == null) {
         if (!this._delegate.isFocusable()) {
            return;
         }

         this.onFocus(1);
         defaultFocus = this.getLeafFieldWithFocus();
      }

      XYRect rect = Ui.getTmpXYRect();
      defaultFocus.getFocusRectPhantom(rect);
      this.ensureRegionVisible(defaultFocus, rect.x, rect.y, rect.width, rect.height, false, false);
      Ui.returnTmpXYRect(rect);
   }

   @Override
   protected void onExposed() {
   }

   @Override
   final void callOnExposed() {
      if (this.isScreenState(4)) {
         this.setScreenState(4, false);
         this.onExposed();
         this._delegate.callOnExposed();
      }
   }

   @Override
   protected void onFocus(int direction) {
      if (!this._delegate.isFocusable()) {
         throw new RuntimeException("Attempt to give focus to screen that doesn't accept focus.");
      }

      super.onFocus(direction);
      this._delegate.onFocus(direction);
      this._delegate.focusChangeNotify(1);
   }

   @Override
   public boolean isMuddy() {
      return this._delegate.isMuddy();
   }

   @Override
   public void delete(Field field) {
      this._delegate.delete(field);
   }

   @Override
   protected void onMenuDismissed(Menu menu) {
      this.onMenuDismissed();

      for (Field curField = this.getLeafFieldWithFocus(); curField != null && curField != this; curField = curField.getManager()) {
         curField.onMenuDismissed(menu);
      }
   }

   @Override
   protected void onMenuDismissed() {
   }

   @Override
   protected void onObscured() {
   }

   @Override
   final void callOnObscured() {
      if (!this.isScreenState(4)) {
         this.setScreenState(4, true);
         this.onObscured();
         this._delegate.callOnObscured();
      }
   }

   private void assertLayoutCompleteOnScreen() {
      this.acceptVisitor(new Screen$ScreenFieldVisitor(this, 0));
   }

   private boolean isScreenState(int state) {
      return (this._screenState & state) == state;
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
   }

   @Override
   protected void onUnfocus() {
      this._delegate.onUnfocus();
      this._delegate.focusChangeNotify(3);
   }

   private void assertSuperCalled(int method) {
      if (method != this._superCalled) {
         throw new IllegalStateException("Missing call to super");
      }

      this._superCalled = 0;
   }

   @Override
   protected void paint(Graphics graphics) {
      this.paintChild(graphics, this._delegate);
   }

   @Override
   void paintBorder(Graphics graphics) {
      Border border = this.getBorder();
      if (border != null) {
         int fgPrevious = graphics.getColor();
         int bgPrevious = graphics.getBackgroundColor();
         if (!Graphics.isColor()) {
            graphics.setColor(graphics.getBackgroundColor());
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

         graphics.setColor(ThemeAttributeSet.getColor(this, 1));
         graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 0));
         XYRect rect = Ui.getTmpXYRect();
         rect.set(this.getExtent());
         rect.x = rect.y = 0;
         border.paint(graphics, rect);
         Ui.returnTmpXYRect(rect);
         graphics.setColor(fgPrevious);
         graphics.setBackgroundColor(bgPrevious);
      }
   }

   @Override
   protected void paintBackground(Graphics graphics) {
      XYRect rect = Ui.getTmpXYRect();
      rect.set(0, 0, this.getWidth() - this.getBorderLeft() - this.getBorderRight(), this.getHeight() - this.getBorderTop() - this.getBorderBottom());
      Background background = ThemeAttributeSet.getBackground(this);
      if (background != null) {
         background.draw(graphics, rect);
      } else if (this.getThemeAttributeSet() == null) {
         Background.createSolidBackground(16777215).draw(graphics, rect);
      }

      Ui.returnTmpXYRect(rect);
   }

   @Override
   public void replace(Field oldField, Field newField) {
      this._delegate.replace(oldField, newField);
   }

   private boolean isScrollBehaviourViewInternal() {
      return this._scrollBehaviourView && !this._scrollBehaviourSelect;
   }

   @Override
   public boolean isSelecting() {
      return this._delegate.isSelecting();
   }

   private void beginSuperCalled(int method) {
      if (this._superCalled != 0) {
         throw new IllegalStateException("Call to super checker already in use.");
      }

      this._superCalled = method;
   }

   private void ensureRegionVisible(Field field, int x, int y, int width, int height, boolean immediate, boolean draw) {
      XYRect rect = Ui.getTmpXYRect();

      while (true) {
         Manager manager = field.getManager();
         if (manager == this || manager == null) {
            Ui.returnTmpXYRect(rect);
            return;
         }

         x += field.getContentLeft();
         y += field.getContentTop();
         rect.set(x, y, width, height);
         if (!this.isScrollBehaviourViewInternal()) {
            manager.makeFocusVisible(immediate, rect, draw, false);
         }

         int hscroll = manager.getHorizontalScroll();
         int vscroll = manager.getVerticalScroll();
         int mgrWidth = manager.getWidth();
         int mgrHeight = manager.getHeight();
         if (x < hscroll) {
            width -= hscroll - x;
            x = hscroll;
         }

         if (y < vscroll) {
            height -= vscroll - y;
            y = vscroll;
         }

         int xOverflow = x + width - (hscroll + mgrWidth);
         if (xOverflow > 0) {
            width -= xOverflow;
         }

         int yOverflow = y + height - (vscroll + mgrHeight);
         if (yOverflow > 0) {
            height -= yOverflow;
         }

         field = manager;
      }
   }

   private void doFocusMove(boolean removeFocus, boolean addFocus, Screen$FocusSelector focusSelector) {
      this.assertHaveEventLock();
      Ui.DRAW_FOCUS_IN_PAINT = false;
      if (removeFocus) {
         this.doRemoveFocus();
      }

      focusSelector.select();
      this.doAddFocus(addFocus);
      Ui.DRAW_FOCUS_IN_PAINT = true;
      if (addFocus) {
         UiEngineImpl uiEngine = this.getUiEngineImpl();
         if (uiEngine != null) {
            uiEngine.setSomethingInvalid();
         }
      }
   }

   public Screen(Manager delegate) {
      this(delegate, 0);
   }

   @Override
   public void deleteRange(int start, int count) {
      this._delegate.deleteRange(start, count);
   }

   public Screen(Manager delegate, long style) {
      super(style);
      this.setTag(TAG);
      this._delegate = delegate;
      this._delegate.setManager(this, 0);
      this.setPaintController(true);
   }

   @Override
   final boolean isVisible0() {
      return this.isScreenState(8);
   }

   private void setFocusChain(Field field) {
      int direction = 0;
      Manager manager = field.getManager();
      if (field instanceof Manager) {
         direction = 1;
      }

      if (field.isFocusable()) {
         field.onFocus(direction);
         field.focusChangeNotify(1);
         if (field != this._delegate) {
            while (true) {
               manager.onFocus(0);
               manager.setFieldWithFocus(field);
               manager.focusChangeNotify(1);
               if (manager == this._delegate) {
                  return;
               }

               field = manager;
               manager = field.getManager();
            }
         }
      }
   }

   private final void setScreenState(int state, boolean on) {
      if (on) {
         this._screenState |= state;
      } else {
         this._screenState &= ~state;
      }
   }

   @Override
   public void add(Field field) {
      this._delegate.add(field);
   }

   @Override
   int getVisibleHeight(int min, int y) {
      if (0 > y) {
         min += y;
         y = 0;
      }

      if (this.getHeight() < min + y) {
         min = this.getHeight() - y;
      }

      return min;
   }

   @Override
   public void insert(Field field, int index) {
      this._delegate.insert(field, index);
   }

   @Override
   protected boolean invokeAction(int action) {
      if (this.getDelegate().invokeAction(action)) {
         return true;
      }

      switch (action) {
         case 1:
            Field leafWithFocus = this.getLeafFieldWithFocus();
            if (leafWithFocus != null && leafWithFocus.isSelecting()) {
               this.onMenu(65537);
               return true;
            } else if (this._scrollBehaviourSelect) {
               if (leafWithFocus != null) {
                  leafWithFocus.select(true);
               }

               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return this._delegate.navigationClick(status, time);
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean result = false;
      boolean delegateNavMovement = this._delegate.navigationMovement(dx, dy, status, time);
      if (this._delegate.getFieldWithFocus() == null) {
         return false;
      }

      if (delegateNavMovement) {
         return true;
      }

      if (this.isScrollBehaviourViewInternal()) {
         int amount = dy;
         Screen$ViewFocusSelector selector = Screen$ViewFocusSelector.getSelector(this, amount, status, time);
         this.doFocusMove(true, true, selector);
         result = selector.getSuccess();
      } else {
         Screen$NavigationMovementFocusSelector selector = Screen$NavigationMovementFocusSelector.getSelector(this, dx, dy, status, time);
         this.doFocusMove(true, true, selector);
         result = selector.getSuccess();
      }

      if (!result && (status & 1) != 0 && (status & 536870912) != 0 && dy != 0) {
         result = this.scroll(dy > 0 ? 512 : 256);
      }

      if (!result) {
         result = super.navigationMovement(dx, dy, status, time);
      }

      return result;
   }

   @Override
   protected boolean navigationUnclick(int status, int time) {
      return this._delegate.navigationUnclick(status, time);
   }

   @Override
   int getVisibleWidth(int min, int x) {
      if (0 > x) {
         min += x;
         x = 0;
      }

      if (this.getWidth() < min + x) {
         min = this.getWidth() - x;
      }

      return min;
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this._delegate.trackwheelClick(status, time) ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected void applyFont() {
      super.applyFont();
      this._delegate.applyFont();
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (this._delegate.trackwheelRoll(amount, status, time)) {
         return true;
      } else if (this._delegate.getFieldWithFocus() == null) {
         return false;
      } else if (this.isScrollBehaviourViewInternal()) {
         Screen$ViewFocusSelector selector = Screen$ViewFocusSelector.getSelector(this, amount, status, time);
         this.doFocusMove(true, true, selector);
         return selector.getSuccess();
      } else {
         Screen$TrackwheelRollFocusSelector selector = Screen$TrackwheelRollFocusSelector.getSelector(this, amount, status, time);
         this.doFocusMove(true, true, selector);
         return selector.getSuccess();
      }
   }

   @Override
   protected boolean trackwheelUnclick(int status, int time) {
      return this._delegate.trackwheelUnclick(status, time);
   }

   @Override
   protected void invalidateLayout0() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.setValidLayout(false);
      this._delegate.invalidateLayout0();
   }

   private int mapStylusX(int x) {
      return x - this._delegate.getLeft();
   }

   private int mapStylusY(int y) {
      return y - this._delegate.getTop();
   }

   @Override
   protected boolean stylusDown(int x, int y, int status, int time) {
      return this._delegate.stylusDown(this.mapStylusX(x), this.mapStylusY(y), status, time);
   }

   @Override
   protected boolean stylusUp(int x, int y, int status, int time) {
      return this._delegate.stylusUp(this.mapStylusX(x), this.mapStylusY(y), status, time);
   }

   @Override
   protected boolean stylusDrag(int x, int y, int status, int time) {
      return this._delegate.stylusDrag(this.mapStylusX(x), this.mapStylusY(y), status, time);
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return this._delegate.stylusTap(this.mapStylusX(x), this.mapStylusY(y), status, time);
   }

   @Override
   protected boolean stylusDoubleTap(int x, int y, int status, int time) {
      return this._delegate.stylusDoubleTap(this.mapStylusX(x), this.mapStylusY(y), status, time);
   }

   @Override
   protected boolean stylusTapHold(int x, int y, int status, int time) {
      return this._delegate.stylusTapHold(this.mapStylusX(x), this.mapStylusY(y), status, time);
   }

   @Override
   public int getFieldCount() {
      return this._delegate.getFieldCount();
   }

   @Override
   public Field getField(int index) {
      return this._delegate.getField(index);
   }

   @Override
   public Field getFieldWithFocus() {
      return this._delegate.getFieldWithFocus();
   }

   @Override
   public int getFieldWithFocusIndex() {
      return this._delegate.getFieldWithFocusIndex();
   }

   @Override
   public Field getLeafFieldWithFocus() {
      return this._delegate.getLeafFieldWithFocus();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   void runLayoutUpdate0(int index, int added, int deleted) {
      this._inLayout++;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         UiEngineImpl engine = this._uiEngine;
         if (engine != null) {
            engine.appInvalidate(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight());
         }

         int width = Display.getWidth();
         int height = Display.getHeight();
         width -= this.getBorderLeft() + this.getBorderRight() + this.getPaddingLeft() + this.getPaddingRight();
         height -= this.getBorderTop() + this.getBorderBottom() + this.getPaddingTop() + this.getPaddingBottom();
         this.layout(width, height);
         this._invalid.set(0, 0, 0, 0);
         this.invalidate();
         this.ensureFocusVisible();
         var9 = false;
      } finally {
         if (var9) {
            this._inLayout--;
         }
      }

      this._inLayout--;
   }

   @Override
   public void invalidate() {
      this.invalidateInternal();
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      return this._delegate.getFieldAtLocation(x, y);
   }

   @Override
   public void invalidate(int x, int y, int width, int height) {
      this.invalidateInternal(x, y, width, height);
   }

   @Override
   public void invalidateAll(int x, int y, int width, int height) {
      this.invalidateCommon(x, y, width, height, this.getExtent());
   }

   private void invalidateCommon(int x, int y, int width, int height, XYRect extent) {
      UiEngineImpl engine = this._uiEngine;
      if (engine != null && this.isValidLayout()) {
         int clipx1 = extent.x;
         int clipx2 = extent.x + extent.width;
         int clipy1 = extent.y;
         int clipy2 = extent.y + extent.height;
         x += clipx1;
         y += clipy1;
         int newx1 = MathUtilities.clamp(clipx1, x, clipx2);
         int newx2 = MathUtilities.clamp(clipx1, x + width, clipx2);
         int newy1 = MathUtilities.clamp(clipy1, y, clipy2);
         int newy2 = MathUtilities.clamp(clipy1, y + height, clipy2);
         if (this._noSynchOnInvalidate) {
            this._invalid.union(newx1, newy1, newx2 - newx1, newy2 - newy1);
         } else {
            synchronized (this._invalid) {
               this._invalid.union(newx1, newy1, newx2 - newx1, newy2 - newy1);
            }
         }

         engine.forceRepaintIfNotOnEventThread();
         engine.setSomethingInvalid();
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      this._delegate.getFocusRect(rect);
      XYRect extent = this._delegate.getContentRect();
      rect.translate(extent.x, extent.y);
   }

   @Override
   public boolean isDataValid() {
      return this._delegate.isDataValid();
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      return this._delegate.keyChar(c, status, time);
   }

   @Override
   public void setDirty(boolean dirty) {
      this._delegate.setDirty(dirty);
   }

   @Override
   public void setFocus() {
      this._delegate.setFocus();
   }

   @Override
   public boolean isDirty() {
      return this._delegate.isDirty();
   }

   @Override
   protected boolean keyControl(char c, int status, int time) {
      boolean volumeKey = false;
      if (c == 150 || c == 151) {
         AudioRouter audioRouter = AudioRouter.getInstance();
         if (audioRouter.canChangeMasterVolume()) {
            audioRouter.incrementMasterVolume(c == 150 ? 10 : -10);
            return true;
         }

         volumeKey = true;
      }

      if (this._delegate.keyControl(c, status, time)) {
         return true;
      } else if (super.keyControl(c, status, time)) {
         return true;
      } else {
         return volumeKey ? this.adjustVolume(c == 150 ? 1 : -1) >= 0 : false;
      }
   }

   @Override
   public int adjustVolume(int volumeLevelChange) {
      return MediaPlayerState.areAnyPlayersRegistered() ? -1 : 0;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean result = false;
      if ((Keypad.status(keycode) & 1) == this._backdoorAltStatus) {
         this._backdoorCode = BackdoorKeyProcessor.appendKeyDetectingMultitap(this._backdoorCode, Keypad.key(keycode));
         result = this.openProductionBackdoor(this._backdoorCode);
         if (!result && BackdoorKeyProcessor.isDevelopmentDevice()) {
            result = this.openDevelopmentBackdoor(this._backdoorCode);
         }
      } else {
         this._backdoorCode = 0;
      }

      if (!result) {
         result = this._delegate.keyDown(keycode, time);
      }

      if (!result) {
         result = super.keyDown(keycode, time);
      }

      return result;
   }

   @Override
   public void setHorizontalQuantization(int horizontalQuanta) {
      this._delegate.setHorizontalQuantization(horizontalQuanta);
   }

   @Override
   void setManager(Manager manager, int index) {
      throw new IllegalStateException();
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      return this._delegate.keyUp(keycode, time);
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      return this._delegate.keyRepeat(keycode, time);
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      return this._delegate.keyStatus(keycode, time);
   }

   @Override
   Graphics getGraphics0(XYRect clip, boolean drawBackground) {
      Graphics graphics = this.getGraphics(clip, false);
      XYRect rect = Ui.getTmpXYRect();
      this.getContentRect(rect);
      rect.x = rect.x - this.getLeft();
      rect.y = rect.y - this.getTop();
      graphics.pushRegion(rect);
      Ui.returnTmpXYRect(rect);
      graphics.translate(-this.getPaddingLeft(), -this.getPaddingTop());
      this.applyTheme(graphics, drawBackground);
      graphics.translate(this.getPaddingLeft(), this.getPaddingTop());
      return graphics;
   }

   @Override
   public void setVerticalQuantization(int verticalQuanta) {
      this._delegate.setVerticalQuantization(verticalQuanta);
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (this.blockInputEvents(event, keycode)) {
         return 0;
      }

      if (Keypad.key(keycode) == 0) {
         keycode = 32768;
      }

      int result = this._delegate.processKeyEvent(event, key, keycode, time);
      boolean handled = (result & 65536) == 65536;
      if (!handled || event == 513) {
         handled |= this.dispatchKeyEvent(event, (char)(result & 65535), keycode, time);
      }

      if (!handled && (event == 513 || event == 514)) {
         char charkey = (char)(result & 65535);
         if (Ui.getTrackballClickAction() == 1 && Keypad.key(keycode) == 4098) {
            charkey = 149;
         }

         if (charkey == 130 && InternalServices.isReducedFormFactor()) {
            charkey = 132;
         }

         if (charkey != 0) {
            handled = this.dispatchKeyEvent(32768, charkey, keycode, time);
         }
      }

      if (handled) {
         result |= 65536;
      }

      return result;
   }

   @Override
   public int getAccessibleRole() {
      return 1;
   }

   @Override
   public String getAccessibleName() {
      return null;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return this.getField(index);
   }

   @Override
   public int getAccessibleChildCount() {
      return this._delegate.getAccessibleChildCount();
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return this._delegate.getAccessibleSelectionAt(index);
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 1;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this._delegate.isAccessibleChildSelected(index);
   }

   @Override
   protected void accessibleEventOccurred(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventDispatcher.dispatchAccessibleEvent(event, oldValue, newValue, context);
   }

   @Override
   boolean validateFieldStyle(long style) {
      return super.validateFieldStyle(style & -68719476737L);
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return this._delegate != null
         ? this._delegate.processNavigationEvent(event, dx, dy, status, time)
         : super.processNavigationEvent(event, dx, dy, status, time);
   }

   @Override
   void doVisibilityWalk(boolean visible) {
      if (this.isScreenState(8) != visible) {
         if (Trackball.isSupported()) {
            this._inputStartReceived = false;
         }

         this.setScreenState(8, visible);
         this.onVisibilityChange(visible);
         this._delegate.doVisibilityWalk(visible);
      }
   }

   @Override
   public boolean isFocusable() {
      return this._delegate.isFocusable();
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      if (this.getThemeAttributeSet() == null) {
         this.setThemeAttributeSet(ThemeManager.getActiveTheme().getAttributeSet(TAG_ROOT));
      }

      Border border = this.getBorder();
      this._transparentBorder = border != null ? border.isTransparent() : false;
      Background background = ThemeAttributeSet.getBackground(this);
      this._transparentBackground = background == null || background.isTransparent();
      this._delegate.applyTheme();
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance == 0) {
         if (this.isStyle(131072) && !this._scrollBehaviourSelect) {
            menu.add(MenuItem.getPrefab(14));
         }

         if (this.isScrollBehaviourViewInternal()) {
            menu.add(MenuItem.getPrefab(16));
         }

         if (this._scrollBehaviourSelect) {
            menu.add(MenuItem.getPrefab(17));
            return;
         }
      } else {
         menu.add(MenuItem.getPrefab(18));
      }
   }

   private void applyTrackballOffsets() {
      if (Trackball.isSupported()) {
         boolean hasFocus = this.isScreenState(64);
         if (hasFocus && this._trackballSensitivityXOffset != Integer.MAX_VALUE) {
            int sensitivity = MathUtilities.clamp(0, Trackball.getSensitivityXForSystem() + this._trackballSensitivityXOffset, 100);
            Trackball.setSensitivityXInternal(sensitivity);
         } else {
            Trackball.setSensitivityXInternal(Integer.MAX_VALUE);
         }

         if (hasFocus && this._trackballSensitivityYOffset != Integer.MAX_VALUE) {
            int sensitivity = MathUtilities.clamp(0, Trackball.getSensitivityYForSystem() + this._trackballSensitivityYOffset, 100);
            Trackball.setSensitivityYInternal(sensitivity);
         } else {
            Trackball.setSensitivityYInternal(Integer.MAX_VALUE);
         }

         if (hasFocus) {
            Trackball.setFilterInternal(this._trackballFilter);
         } else {
            Trackball.setFilterInternal(-1);
         }

         Trackball.updateDeviceWithAppSettings();
      }
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._delegate.getFocusRectPhantom(rect);
      XYRect extent = this._delegate.getContentRect();
      rect.translate(extent.x, extent.y);
   }
}
