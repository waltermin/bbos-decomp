package net.rim.tid.im.ui;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.spellcheck.SpellCheckInputMethodVariant;
import net.rim.tid.itie.EventHandler;

public final class ChooseVariantDialog extends PopupScreen {
   private ChooseVariantManager _chooseVariantManager = (ChooseVariantManager)this.getDelegate();
   private XYRect _anchorRect;
   private boolean _changingVisibility;
   private boolean _isVisible;
   static final int MAX_VISIBLE = 4;

   public ChooseVariantDialog(SpellCheckInputMethodVariant spellChecker) {
      super(new ChooseVariantManager(spellChecker), 65536);
   }

   public final void setItems(Vector items) {
      this._chooseVariantManager.setItems(items);
   }

   public final int getSelectedIndex() {
      return this._chooseVariantManager.getSelectedIndex();
   }

   @Override
   protected final void onFocusNotify(boolean focus) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final synchronized void setVisible(boolean visible) {
      this._changingVisibility = true;
      if (visible && !this._isVisible) {
         Ui.getUiEngine().pushScreen(this);
         this._isVisible = true;
      } else if (!visible && this._isVisible) {
         Application app = this.getApplication();
         synchronized (app.getAppEventLock()) {
            UiEngine ui = this.getUiEngine();

            for (Screen screen = ui.getActiveScreen(); screen != null; screen = screen.getScreenBelow()) {
               if (screen == this) {
                  ui.popScreen(this);
                  break;
               }
            }
         }

         this._isVisible = false;
      }

      this._changingVisibility = false;
   }

   public final void setPositionRelativeTo(XYRect anchorRect) {
      this._chooseVariantManager._anchorRect = anchorRect;
      this._anchorRect = anchorRect;
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (!this._changingVisibility) {
         synchronized (this) {
            if (!this._changingVisibility) {
               if (!visible && this.isDisplayed()) {
                  this._changingVisibility = true;
                  boolean var9 = false /* VF: Semaphore variable */;

                  try {
                     var9 = true;
                     InputContext ic = InputContext.getInstance();
                     SLControlObject co = ic.getInputMethodControlObject();
                     ((SLControlObject)co).actionPerformed(42, null);
                     var9 = false;
                  } finally {
                     if (var9) {
                        this._changingVisibility = false;
                     }
                  }

                  this._changingVisibility = false;
               }
            }
         }
      }
   }

   @Override
   public final boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      int rc = 4;
      switch (event) {
         case 516:
         case 6914:
            if (this.shouldClickShowMenu()) {
               this._chooseVariantManager._spellChecker.showMenu();
               rc = 0;
            } else {
               InputContext ic = InputContext.getInstance();
               SLControlObject co = (SLControlObject)ic.getInputMethodControlObject();
               rc = co.actionPerformed(43, null);
            }
            break;
         case 519:
         case 6913:
            if (dy != 0) {
               return super.processNavigationEvent(event, dx, dy, status, time);
            }
      }

      return rc == 0 ? true : EventHandler.getInstance().processNavigationEvent(event, dx, dy, status, time);
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      switch (event) {
         case 513:
         case 514:
         case 515:
         case 520:
            if (Keypad.key(keycode) == 4098) {
               return super.processKeyEvent(event, key, keycode, time);
            } else {
               Screen underlyingScreen = this.getScreenBelow();
               if (underlyingScreen != null) {
                  return underlyingScreen.processKeyEvent(event, key, keycode, time);
               }
            }
         default:
            return 0;
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._chooseVariantManager._list.reinit();
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      int screenHeight = Display.getHeight();
      int screenWidth = Display.getWidth();
      int middle = screenHeight >> 1;
      int fontHeight = this._chooseVariantManager._list.getFont().getHeight();
      int halfFontHeight = fontHeight >> 1;
      int yPos;
      if (middle < this._anchorRect.y + halfFontHeight) {
         height = Math.min(height, this._anchorRect.y - halfFontHeight);
         int buttonHeight = this._chooseVariantManager.getMenuButtonHeight();
         yPos = this._anchorRect.y
            - this._chooseVariantManager.calcLineCount(height - this.getMarginTop() - this.getMarginBottom()) * fontHeight
            - buttonHeight
            - this.getMarginTop()
            - this.getMarginBottom();
         yPos = Math.max(yPos, 0);
      } else {
         yPos = this._anchorRect.y + fontHeight;
         height = Math.min(height, screenHeight - yPos);
      }

      int xPos = this._anchorRect.x;
      width = this._chooseVariantManager.getLongestWidth(this._chooseVariantManager._list.getFont()) + this.getMarginRight() + this.getMarginLeft();
      if (xPos + width > screenWidth) {
         xPos = screenWidth - width;
      }

      super.sublayout(width, height);
      this.setPosition(xPos, yPos);
   }

   private final boolean shouldClickShowMenu() {
      return this._chooseVariantManager._menuButton != null ? this._chooseVariantManager._menuButton.isFocus() : false;
   }
}
