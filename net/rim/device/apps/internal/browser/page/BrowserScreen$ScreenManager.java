package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;
import net.rim.device.apps.internal.browser.ui.VerticalIndentFieldManager;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.component.Scrollbar;
import net.rim.vm.Memory;

final class BrowserScreen$ScreenManager extends Manager {
   private boolean _eatNavigationKeys;
   private PageHeaderField _header;
   private PageFooterField _footer;
   private Manager _fields = new VerticalIndentFieldManager(3460171888704094208L);
   private Scrollbar _verticalScrollbar;
   private Scrollbar _horizontalScrollbar;
   private boolean _showInFullScreen;
   private boolean _fullScreenModeRequested;
   private int _availableHeight;
   private boolean _overlayFooter;
   private boolean _scrollbarOnLeft;
   private BrowserScreen$ScreenManager$ScrollHelper _scrollHelper;

   BrowserScreen$ScreenManager() {
      super(3458764513820540928L);
      this._header = new PageHeaderField();
      this._verticalScrollbar = new Scrollbar();
      this._horizontalScrollbar = new Scrollbar(false, true);
      this._scrollHelper = new BrowserScreen$ScreenManager$ScrollHelper(this);
      this._footer = new PageFooterField();
      this._availableHeight = Integer.MAX_VALUE;
      this._fullScreenModeRequested = GeneralProperty.getCurrentPropertyAsBoolean(29);
      this._showInFullScreen = this._fullScreenModeRequested;
      super.add(this._header);
      super.add(this._fields);
      super.add(this._verticalScrollbar);
      super.add(this._horizontalScrollbar);
      super.add(this._footer);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int headerHeight = 0;
      int footerHeight = 0;
      if (this._showInFullScreen) {
         this.setPositionChild(this._header, 0, 0);
         this.layoutChild(this._header, width, 0);
      } else {
         this.setPositionChild(this._header, 0, 0);
         this.layoutChild(this._header, width, height);
         headerHeight = this._header.getHeight();
      }

      this._footer.setFullScreenMode(this._showInFullScreen);
      footerHeight = this._footer.getPreferredHeight();
      this.setPositionChild(this._footer, 0, height - footerHeight);
      this.layoutChild(this._footer, width, footerHeight);
      int horizontalScrollbarHeight = this._horizontalScrollbar.getPreferredHeight();
      this.setPositionChild(this._horizontalScrollbar, 0, height - footerHeight - horizontalScrollbarHeight);
      this.layoutChild(this._horizontalScrollbar, width, horizontalScrollbarHeight);
      this._availableHeight = height - headerHeight - footerHeight - horizontalScrollbarHeight;
      boolean scrollbarOnScreen = this._verticalScrollbar.getManager() != null;
      if (scrollbarOnScreen) {
         this.layoutChild(this._verticalScrollbar, width, this._availableHeight);
      }

      int remainingWidth = width - this._verticalScrollbar.getWidth();
      int fieldXPos = 0;
      int scrollXPos = remainingWidth;
      if (this._scrollbarOnLeft) {
         fieldXPos = this._verticalScrollbar.getWidth();
         scrollXPos = 0;
      }

      if (scrollbarOnScreen) {
         this.setPositionChild(this._verticalScrollbar, scrollXPos, headerHeight);
      }

      if (this._fields != null) {
         if (this._overlayFooter) {
            this._availableHeight += footerHeight;
         }

         this.layoutChild(this._fields, remainingWidth, this._availableHeight);
         this.setPositionChild(this._fields, fieldXPos, headerHeight);
      }

      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
   }

   final void setOverlayFooter(boolean overlayFooter) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setScrollbarOnLeft(boolean lhs) {
      if (this._scrollbarOnLeft != lhs) {
         this._scrollbarOnLeft = lhs;
         super.delete(this._verticalScrollbar);
         super.insert(this._verticalScrollbar, this._scrollbarOnLeft ? 1 : 2);
      }
   }

   final void replaceContentField(Manager manager, boolean doFullGC) {
      Manager oldField = this._fields;
      this._fields = null;
      super.delete(oldField);
      Manager var4 = null;
      if (doFullGC && Memory.getRAMReady() < 1048576) {
         Memory.fullGC();
      }

      this._fields = manager;
      super.insert(manager, 1);
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int top = clip.y;
      int bottom = clip.y + clip.height;
      int numFields = this.getFieldCount();
      int i = this.getFieldAtLocation(clip.x, top);
      if (i >= 0) {
         while (i < numFields) {
            Field field = this.getField(i);
            if (field.getTop() >= bottom) {
               return;
            }

            this.paintChild(graphics, field);
            i++;
         }
      }
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      if (event != 513) {
         return super.processKeyEvent(event, key, keycode, time);
      }

      int result = 0;
      if (!this._eatNavigationKeys || !this.isNavigationKey(keycode)) {
         result = super.processKeyEvent(event, key, keycode, time);
         BrowserScreen screen = (BrowserScreen)this.getScreen();
         boolean handled = (result & 65536) != 0;
         if (!handled) {
            handled |= screen.dispatchKeyEvent(event, (char)(result & 65535), keycode, time);
         }

         if (!handled) {
            char charkey = (char)(result & 65535);
            if (charkey != 0) {
               handled = screen.dispatchKeyEvent(32768, charkey, keycode, time);
            }
         }

         screen._dispatchKeyEventHandled = true;
         if (handled) {
            result |= 65536;
         }
      }

      if ((result & 65536) == 0 && this.handleHotkeys(event, UiInternal.map(keycode), Keypad.status(keycode), time)) {
         result |= 65536;
      }

      return result;
   }

   private final boolean isNavigationKey(int keycode) {
      switch (Keypad.key(keycode)) {
         case 20:
         case 32:
         case 66:
         case 77:
         case 84:
         case 85:
            return BrowserScreen._isReducedKeyboard;
         default:
            return false;
      }
   }

   private final boolean handleHotkeys(int event, char key, int status, int time) {
      if ((status & 1) != 0) {
         this._eatNavigationKeys = false;
      } else {
         BrowserScreen screen = (BrowserScreen)this.getScreen();
         if (screen != null) {
            if (BrowserScreen._isReducedKeyboard && event == 513) {
               this._eatNavigationKeys = false;
               switch (CharacterUtilities.toUpperCase(key, 1701707776)) {
                  case ' ':
                     int direction = (status & 2) == 0 ? 512 : 256;
                     screen.scroll(direction);
                     this._eatNavigationKeys = true;
                     return true;
                  case 'B':
                     if (screen.dispatchTrackwheelEvent(519, 1, 0, time)) {
                        this._eatNavigationKeys = true;
                        return true;
                     }

                     return false;
                  case 'M':
                     screen.scroll(512);
                     this._eatNavigationKeys = true;
                     return true;
                  case 'T':
                     if (screen.dispatchTrackwheelEvent(519, -1, 0, time)) {
                        this._eatNavigationKeys = true;
                        return true;
                     }

                     return false;
                  case 'U':
                     screen.scroll(256);
                     this._eatNavigationKeys = true;
                     return true;
               }
            }

            if (BrowserScreen._isReducedKeyboard && event == 513 || !BrowserScreen._isReducedKeyboard && event == 32768) {
               this._eatNavigationKeys = false;
               if (key == ' ') {
                  int direction = (status & 2) == 0 ? 512 : 256;
                  screen.scroll(direction);
                  this._eatNavigationKeys = true;
                  return true;
               }

               Page currentPage = screen.getPage();
               switch (BrowserHotkeys.map(CharacterUtilities.toUpperCase(key, 1701707776))) {
                  case 337:
                     if (currentPage != null) {
                        ShowUrlVerb.showUrl(currentPage.getTitle(), currentPage.getURL());
                     }

                     return true;
                  case 353:
                     String url = screen.getLinkUnderCursor();
                     ShowUrlVerb.showUrl(null, url);
                     return true;
                  case 366:
                     screen.scroll(1);
                     return true;
                  case 367:
                     screen.scroll(2);
                     return true;
                  case 448:
                     if (currentPage != null) {
                        currentPage.invokeFind(false, null);
                     }

                     return true;
                  case 449:
                     if (currentPage != null) {
                        currentPage.invokeFind(true, null);
                     }

                     return true;
                  case 458:
                     screen.requestImages(false);
                     return true;
                  case 459:
                     screen.requestImages(true);
                     return true;
                  case 475:
                     screen.onShowInFullScreenChange();
                     return true;
                  case 650:
                     screen.showConnectionInfo();
                     return true;
                  case 664:
                     if (currentPage != null && currentPage.jumpToNextHeading()) {
                        this._eatNavigationKeys = true;
                     }

                     return true;
                  case 671:
                     if (this._fields instanceof BrowserTextFlowManager) {
                        BrowserTextFlowManager tfm = (BrowserTextFlowManager)this._fields;
                        if (tfm.getWideViewMode()) {
                           tfm.adjustZoom(Integer.MAX_VALUE);
                           return true;
                        }
                     }

                     return false;
                  case 903:
                     if (this._fields instanceof BrowserTextFlowManager) {
                        ((BrowserTextFlowManager)this._fields).toggleViewMode();
                        return true;
                     }
               }
            }
         }
      }

      return false;
   }
}
