package net.rim.device.apps.internal.browser.ui;

import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScrollChangeListener;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.browser.common.BrowserPhoneConfirmation;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import org.w3c.dom.html2.HTMLElement;

public class BrowserTextFlowManager extends TextFlowManager implements CookieProvider {
   private ScrollChangeListener _elevator;
   private BrowserContentImpl _currentBrowserContent;
   private boolean _layoutActive = true;
   private static ContextObject _browserContextObject = (ContextObject)(new Object(2, 96, 61));

   protected Object getContextMenuContext() {
      return _browserContextObject;
   }

   public void setBrowserContent(BrowserContentImpl browserContent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean isLayoutActive() {
      return this._layoutActive;
   }

   @Override
   public Object getCookieWithFocus() {
      long cookieID = this.getFocusScalarCookie();
      if (cookieID != 0) {
         label55:
         try {
            Object focusCookie = this.getFocusCookie();
            if (focusCookie instanceof Object) {
               String url = (String)focusCookie;
               ContextObject context = (ContextObject)(new Object());
               ContextObject.put(context, 253, url);
               ContextObject.put(context, -442409970680484936L, this._currentBrowserContent);
               return FactoryUtil.createInstance(cookieID, context);
            }

            if (this._currentBrowserContent != null && focusCookie instanceof HTMLElement) {
               String url = ((HTMLElement)focusCookie).getAttribute("href");
               ContextObject context = (ContextObject)(new Object());
               Vector vector = (Vector)(new Object(3));
               vector.addElement(url);
               vector.addElement(null);
               vector.addElement(focusCookie);
               ContextObject.put(context, 249, vector);
               ContextObject.put(context, -442409970680484936L, this._currentBrowserContent);
               ContextObject.put(context, 253, url);
               return FactoryUtil.createInstance(cookieID, context);
            }
         } finally {
            break label55;
         }
      }

      Field focusField = this.getFieldWithFocus();
      if (focusField instanceof FrameManager) {
         focusField = ((Manager)focusField).getFieldWithFocus();
      }

      return !(focusField instanceof BrowserTextFlowManager)
         ? null
         : CookieProviderUtilities.getDefaultCookie(((BrowserTextFlowManager)focusField).getCookieWithFocus());
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (!this.isPositioningOrSelecting() && (status & 1) != 0 && (this.getStyle() & 1125899906842624L) == 0) {
         Field aField = this.getLeafFieldWithFocus();
         if (aField == null || !(aField instanceof Object) && !aField.isSelecting()) {
            Screen screen = this.getScreen();
            if (screen != null) {
               if (amount > 0) {
                  screen.scroll(512);
                  return true;
               }

               screen.scroll(256);
               return true;
            }
         }
      }

      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      return Keypad.key(keycode) == 21 && ControllerUtilities.invokeApplicationKeyVerb(this.getCookieWithFocus()) ? true : super.keyDown(keycode, time);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      if (Graphics.isColor()) {
         ThemeAttributeSet tas = this.getThemeAttributeSet();
         ThemeAttributeSet$Writer ta;
         if (tas == null) {
            Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
            ta = themeWriter.createThemeAttributeSetWriter(null);
         } else {
            ta = tas.getWriterInternal();
         }

         ta.setFocusStyle(3);
         ta.setColor(2, 3100495);
         ta.setColor(3, 16772045);
         ta.setColor(4, 3100495);
         ta.setColor(5, 16772045);
         this.setThemeAttributeSet(ta.getThemeAttributeSet());
      }
   }

   @Override
   public void setScrollListener(ScrollChangeListener listener) {
      super.setScrollListener(listener);
      this._elevator = listener;
   }

   @Override
   public void setLayoutActive(boolean on) {
      super.setLayoutActive(on);
      if (on && this._elevator != null) {
         this._elevator.scrollChanged(this, 0, 0);
      }

      this._layoutActive = on;
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.addMenuItemsToContextMenu()) {
         BrowserBitmapField image = this.getSelectedImage();
         if (image != null) {
            ContextMenu cmenu = image.getContextMenu();
            menu.add(cmenu, true);
            menu.setDefaultIgnoreContextMenuDefault(cmenu.getDefaultItem());
         }

         if (this.getFocusCookie() != null) {
            Verb[] verbs = new Object[0];
            Verb defaultVerb = CookieProviderUtilities.getFocusVerbs(this, this.getContextMenuContext(), verbs);
            int count = verbs.length;
            ContextMenu cmenu = ContextMenu.getInstance();
            cmenu.clear();

            for (int index = 0; index < count; index++) {
               int priority = verbs[index] == defaultVerb ? 10 : Integer.MAX_VALUE;
               VerbMenuItem menuItem = (VerbMenuItem)(new Object(null, verbs[index].getOrdering(), priority, verbs[index], this.getContextMenuContext()));
               cmenu.addItem(menuItem);
               if (verbs[index] == defaultVerb) {
                  cmenu.setDefaultItem(menuItem);
               }
            }

            if (count > 0) {
               menu.add(cmenu, true);
               if (cmenu.getDefaultItem() != null) {
                  menu.setDefaultIgnoreContextMenuDefault(cmenu.getDefaultItem());
               }
            }
         }
      }
   }

   public BrowserTextFlowManager(long style) {
      super(style);
   }

   @Override
   public void viewModeChanged() {
      if (this._currentBrowserContent != null) {
         RenderingOptions options = this._currentBrowserContent.getRenderingOptions();
         if (options != null) {
            options.setProperty(4550690918222697397L, 44, this.getWideViewMode() ? 1 : 0);
            options.setProperty(4550690918222697397L, 45, true);
         }
      }
   }

   static {
      _browserContextObject.setFlag(83);
      _browserContextObject.setFlag(117);
      _browserContextObject.put(8128293842573788963L, new BrowserPhoneConfirmation());
   }
}
