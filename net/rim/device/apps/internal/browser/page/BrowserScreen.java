package net.rim.device.apps.internal.browser.page;

import java.util.Calendar;
import java.util.TimeZone;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.apps.api.framework.file.FileDialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.StackManager;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;
import net.rim.device.apps.internal.browser.util.PNGImageWriter;
import net.rim.device.apps.internal.browser.verbs.MoreImagesVerb;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

public final class BrowserScreen extends Screen {
   private Page[] _displayedPages;
   private int _currentPageIndex;
   private BrowserScreen$ScreenManager _screenManager = (BrowserScreen$ScreenManager)this.getDelegate();
   private ContextObject _menuContext = new ContextObject(61);
   private boolean _dispatchKeyEventHandled;
   private boolean _navigationClicked;
   private int _layoutDepth;
   private static final boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   private static IntIntHashtable _subMenuMapping = new IntIntHashtable();
   private static final int MENU_MAIN = 0;
   private static final int MENU_TOOLS = 1;
   private static final int MENU_VIEW = 2;
   private static final int MENU_ACTIONS = 3;

   public BrowserScreen() {
      super(new BrowserScreen$ScreenManager(), 65536);
      this._displayedPages = new Page[0];
   }

   public final PageFooterField getFooterField() {
      return this._screenManager._footer;
   }

   public final PageHeaderField getHeaderField() {
      return this._screenManager._header;
   }

   public final Page getPage() {
      return this._currentPageIndex >= 0 && this._currentPageIndex < this._displayedPages.length ? this._displayedPages[this._currentPageIndex] : null;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached) {
         this.updateScroll();
      }
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         Page curPage = this.getPage();
         if (curPage instanceof StartupPage) {
            ((StartupPage)curPage).repopulate();
         }
      }

      super.onVisibilityChange(visible);
   }

   @Override
   public final Menu getMenu(int instance) {
      SystemEnabledMenu menu = new SystemEnabledMenu(this._menuContext, null, instance);
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      if (instance != 65536) {
         this.makeMenuWithContext(menu, instance);
         return menu;
      } else {
         this.makeCustomContextMenu(menu, instance);
         return menu;
      }
   }

   private final void makeCustomContextMenu(SystemEnabledMenu menu, int instance) {
      this.makeRssCustomContextMenu(menu, instance);
   }

   private final void makeRssCustomContextMenu(SystemEnabledMenu menu, int instance) {
      for (Field field = this.getLeafFieldWithFocus(); field != null; field = field.getManager()) {
         if (field.getClass().getName().equalsIgnoreCase("net.rim.device.apps.internal.browser.webfeed.WebFeedItemManager")) {
            menu.add(field.getContextMenu(instance));
         }

         if (field == this) {
            return;
         }
      }
   }

   private final void updateScroll() {
      Page currentPage = this.getPage();
      if (currentPage != null) {
         currentPage.updateScroll();
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      Page currentPage = this.getPage();
      if (currentPage != null) {
         VerbRepository vr = VerbRepository.getVerbRepository(-7149655706782283261L);
         if (vr != null) {
            Verb[] verbs = vr.getVerbs(null);
            if (verbs != null) {
               for (int i = 0; i < verbs.length; i++) {
                  VerbMenuItem vbm = new VerbMenuItem(verbs[i], verbs[i].getOrdering());
                  ContextObject co = ContextObject.castOrCreate(null);
                  String url = currentPage.getURL();
                  if (url != null) {
                     ContextObject.put(co, 3696141428889703675L, url);
                  }

                  vbm.setContext(co);
                  menu.add(vbm);
               }
            }
         }

         currentPage.addPageSpecificMenuItems(menu);
         if (WLAN.isSupported()) {
            BrowserDaemonRegistry.getInstance().addWLANActivationMenuItems(menu);
         }

         if (menu instanceof SystemEnabledMenu) {
            ((SystemEnabledMenu)menu).promoteVerbs();
         }
      }
   }

   @Override
   protected final void applyTheme(Graphics graphics, boolean drawBackground) {
      super.applyTheme(graphics, drawBackground);
      graphics.setColor(0);
      graphics.setBackgroundColor(16777215);
   }

   public final void setPage(Page page, boolean doFullGC, boolean newTab) {
      this.updateScroll();
      Page currentPage = this.getPage();
      if (currentPage != null) {
         currentPage.getContentManager().setScrollListener(null);
      } else {
         doFullGC = false;
      }

      if (!newTab && currentPage != null) {
         this._displayedPages[this._currentPageIndex] = page;
      } else {
         Arrays.add(this._displayedPages, page);
         this._currentPageIndex = this._displayedPages.length - 1;
      }

      this.setActivePage(this._currentPageIndex, doFullGC);
   }

   private final void setActivePage(int index, boolean doFullGC) {
      this._currentPageIndex = index;
      Page page = this.getPage();
      if (page != null) {
         page.getContentManager().setScrollListener(null);
         String title = page.getTitle();
         if (title == null) {
            title = page.getURL();
            if (title != null) {
               int lastSlash = title.lastIndexOf(47);
               if (lastSlash != -1 && lastSlash != title.length() - 1) {
                  title = URIDecoder.decode(title.substring(lastSlash + 1), "utf-8");
               }
            }
         }

         this._screenManager._header.setTitle(title, page.getIcon());
         this.enableVerticalScrollbar();
         this.disableHorizontalScrollbar();
         if ((page.getStyle() & 2) != 0) {
            this.disableVerticalScrollbar();
         }

         if ((page.getStyle() & 16) != 0) {
            this._screenManager._footer.showFooter();
         }

         this._screenManager._header.setSecurityInfo(page.getSecurityInfo());
         this._screenManager._showInFullScreen = (page.getStyle() & 4) != 0;
         if (!this._screenManager._showInFullScreen) {
            this._screenManager._showInFullScreen = this._screenManager._fullScreenModeRequested;
         }

         this._screenManager._footer.clearVerbs();
         page.addVerbsToBrandingField(this._screenManager._footer);
         this._screenManager.setOverlayFooter((page.getStyle() & 8) != 0);
         Manager mgr = page.getBrowserContent().getContentManager();
         this._screenManager._verticalScrollbar.setClient(null);
         this._screenManager._horizontalScrollbar.setClient(null);
         this._screenManager.replaceContentField(mgr, doFullGC);
         this._screenManager.setScrollbarOnLeft((page.getStyle() & 32) != 0);
         mgr.setFocus();
         this._screenManager._verticalScrollbar.setClient(mgr, false);
         this._screenManager._horizontalScrollbar.setClient(mgr, false);
         mgr.setScrollListener(this._screenManager._scrollHelper);
         if (page.getBrowserContent().getRenderingOptions().getPropertyWithBooleanValue(4550690918222697397L, 39, false)) {
            this._menuContext.remove(244);
         } else {
            if (!this._menuContext.containsKey(244)) {
               this._menuContext.put(244, "browser");
            }
         }
      }
   }

   final void print() {
      if (this._screenManager._fields != null) {
         FileDialog fd = new FileDialog("/SDCard/", "page.png", 1, "Pick location to save");
         if (fd.doModal() != -1) {
            PNGImageWriter writer = new PNGImageWriter(this._screenManager._fields, fd.getURL());
            writer.start();
         }
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      this._screenManager._eatNavigationKeys = false;
      return super.navigationMovement(dx, dy, status, time);
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      boolean fromWheel = (status & 1073741824) != 0;
      if (!fromWheel) {
         return super.navigationClick(status, time);
      } else if (super.navigationClick(status, time)) {
         return true;
      } else {
         boolean altPressed = (status & 1) != 0;
         if (altPressed) {
            BrowserDaemonRegistry.getInstance().showMenu();
            return true;
         } else {
            this._navigationClicked = true;
            BrowserDaemonRegistry.getInstance().getMenuDelayThread().trackwheelClicked();
            return true;
         }
      }
   }

   @Override
   public final boolean navigationUnclick(int status, int time) {
      boolean fromWheel = (status & 1073741824) != 0;
      if (!fromWheel) {
         return super.navigationUnclick(status, time);
      }

      if (super.navigationUnclick(status, time)) {
         return true;
      }

      if (!this._navigationClicked) {
         return true;
      }

      this._navigationClicked = false;
      BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
      if (!browserImpl.getMenuDelayThread().trackwheelUnclicked()) {
         return true;
      }

      browserImpl.showMenu();
      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      char keyChar = key;
      SLKeyLayout keyLayout = Keypad.getLayout();
      if (keyLayout != null) {
         keyChar = UiInternal.map(keyLayout.getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
      }

      if (!this._screenManager._eatNavigationKeys || key != ' ' && BrowserHotkeys.map(CharacterUtilities.toUpperCase(keyChar, 1701707776)) != 664) {
         this._screenManager._eatNavigationKeys = false;
         if (super.keyChar(key, status, time)) {
            return true;
         }
      }

      return this._screenManager.handleHotkeys(32768, keyChar, status, time);
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (!this.keyChar('\n', 0, 0) && !BrowserDaemonRegistry.getInstance().keyChar('\n', 0, 0)) {
                  return false;
               }

               return true;
         }
      }

      return handled;
   }

   private final boolean handleSendKey() {
      Page currentPage = this.getPage();
      if (currentPage != null && currentPage.getBrowserContent() != null) {
         Field fieldWithFocus = currentPage.getBrowserContent().getDisplayableContent().getLeafFieldWithFocus();
         Manager fieldsMgr = currentPage.getContentManager();
         Object focusedModel = null;
         if (!(fieldWithFocus instanceof CookieProvider)) {
            if (fieldsMgr instanceof BrowserTextFlowManager) {
               focusedModel = CookieProviderUtilities.getDefaultCookie(((BrowserTextFlowManager)fieldsMgr).getCookieWithFocus());
            }
         } else {
            focusedModel = CookieProviderUtilities.getDefaultCookie(((CookieProvider)fieldWithFocus).getCookieWithFocus());
         }

         if (focusedModel != null) {
            ContextObject context = new ContextObject(2, 73, 96);
            context.setFlag(83);
            context.setFlag(61);
            context.put(8128293842573788963L, currentPage.getBrowserContent().getBrowserPhoneConfirmation());
            return ControllerUtilities.invokeSendKeyVerb(focusedModel, context);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      Page currentPage = this.getPage();
      if (currentPage != null) {
         currentPage.getBackdoorKeyProcessor().keyDown(keycode);
      }

      int key = Keypad.key(keycode);
      switch (key) {
         case 17:
            if (this.handleSendKey()) {
               return true;
            }
         default:
            return super.keyDown(keycode, time);
      }
   }

   final int getAvailableHeight() {
      return this._screenManager._availableHeight;
   }

   public final int getNumberOfTabs() {
      return this._displayedPages.length;
   }

   public final String getTabTitle(int index) {
      return this._displayedPages[index].getFriendlyTitle();
   }

   public final EncodedImage getTabIcon(int index) {
      return this._displayedPages[index].getIcon();
   }

   public final void setActiveTab(int index) {
      if (index != this._currentPageIndex) {
         this.updateScroll();
         this.setActivePage(index, false);
      }
   }

   private final void requestImages(boolean doMoreAll) {
      int state = BrowserDaemonRegistry.getInstance().getBrowserExecutionState();
      if (state == 0 || state == 3) {
         Page currentPage = this.getPage();
         if (currentPage != null) {
            BrowserContentImpl browserContent = currentPage.getBrowserContent();
            if (browserContent != null) {
               MoreImagesVerb.requestImages(browserContent, doMoreAll);
            }
         }
      }
   }

   private final String getLinkUnderCursor() {
      Page currentPage = this.getPage();
      if (currentPage != null && currentPage.getBrowserContent() != null) {
         Object model = null;
         Field fieldWithFocus = currentPage.getBrowserContent().getDisplayableContent().getLeafFieldWithFocus();
         Manager fieldsMgr = currentPage.getContentManager();
         if (!(fieldWithFocus instanceof CookieProvider)) {
            if (fieldsMgr instanceof BrowserTextFlowManager) {
               model = CookieProviderUtilities.getDefaultCookie(((BrowserTextFlowManager)fieldsMgr).getCookieWithFocus());
            }
         } else {
            model = CookieProviderUtilities.getDefaultCookie(((CookieProvider)fieldWithFocus).getCookieWithFocus());
         }

         String url = null;
         if (model != null) {
            if (!(model instanceof URLProvider)) {
               url = BrowserResources.getString(390);
            } else {
               URLProvider urlProvider = (URLProvider)model;
               BrowserContentImpl browserContent = BrowserDaemonRegistry.getInstance().getCurrentPage().getBrowserContent();
               url = urlProvider.getURL();
               if (url != null) {
                  url = browserContent.resolveUrl(url);
               }
            }
         }

         return url;
      } else {
         return null;
      }
   }

   private final void reLayout() {
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate();
   }

   private final void onShowInFullScreenChange() {
      boolean showInFullScreen = !GeneralProperty.getCurrentPropertyAsBoolean(29);
      GeneralProperty.setCurrentProperty(29, showInFullScreen);
      this._screenManager._fullScreenModeRequested = showInFullScreen;
      this._screenManager._showInFullScreen = showInFullScreen;
      Field fieldWithFocus = this.getFieldWithFocus();
      if (fieldWithFocus instanceof PageFooterField) {
         Page currentPage = this.getPage();
         if (currentPage != null) {
            currentPage.getContentManager().setFocus();
         }
      }

      this.reLayout();
   }

   public final void showInFullScreen() {
      this._screenManager._fullScreenModeRequested = true;
      this._screenManager._showInFullScreen = true;
   }

   public final void enableVerticalScrollbar() {
      this._screenManager._verticalScrollbar.enable();
   }

   public final void disableVerticalScrollbar() {
      this._screenManager._verticalScrollbar.disable();
   }

   public final void enableHorizontalScrollbar() {
      this._screenManager._horizontalScrollbar.enable();
   }

   public final void disableHorizontalScrollbar() {
      this._screenManager._horizontalScrollbar.disable();
   }

   public final void setMobileViewCursor(boolean value) {
      if (this._screenManager._fields instanceof BrowserTextFlowManager) {
         ((BrowserTextFlowManager)this._screenManager._fields).setMobileViewCursor(value);
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      int oldVirtualWidth = 0;
      this._layoutDepth++;
      if (this._screenManager._fields != null && this._layoutDepth == 1) {
         oldVirtualWidth = this._screenManager._fields.getVirtualWidth();
         if (oldVirtualWidth > width) {
            Page currentPage = this.getPage();
            if (currentPage != null && (currentPage.getStyle() & 64) == 0) {
               this.enableHorizontalScrollbar();
            }
         }
      }

      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      if (this._screenManager._fields != null && this._layoutDepth == 1) {
         int newVirtualWidth = this._screenManager._fields.getVirtualWidth();
         if (!this._screenManager._horizontalScrollbar.isEnabled() && oldVirtualWidth != newVirtualWidth && newVirtualWidth > width) {
            Page currentPage = this.getPage();
            if (currentPage != null && (currentPage.getStyle() & 64) == 0) {
               this.enableHorizontalScrollbar();
               this.reLayout();
            }
         } else if (this._screenManager._horizontalScrollbar.isEnabled() && oldVirtualWidth != newVirtualWidth && newVirtualWidth < width) {
            this.disableHorizontalScrollbar();
            this.reLayout();
         }
      }

      this._layoutDepth--;
   }

   public final void browserStateChanged() {
      RibbonComponent$RibbonComponentChangeListener listener = this._screenManager._header;
      if (listener != null) {
         listener.ribbonComponentChanged(null);
      }
   }

   public final void setScrollbarLocation(boolean lhs) {
      this._screenManager.setScrollbarOnLeft(lhs);
   }

   private final void showConnectionInfo() {
      StringBuffer buffer = new StringBuffer(BrowserResources.getString(539));
      buffer.append('\n');
      SessionStats stats = StackManager.getInstance().getSessionStats();
      if (stats == null) {
         buffer.append(BrowserResources.getString(550));
      } else {
         buffer.append(BrowserResources.getString(540));
         String host = stats.getConnectedHost();
         int port = stats.getConnectedPort();
         if (host != null && host.length() > 0) {
            buffer.append(host);
            if (port > 0) {
               buffer.append(':');
               buffer.append(port);
            }
         } else {
            buffer.append(CommonResources.getString(104));
         }

         buffer.append('\n');
         buffer.append(CommonResources.getString(2003));
         Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
         SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");
         ((CalendarExtensions)calendar).setTimeLong(stats.getDuration());
         durationFormat.format(calendar, buffer, null);
         buffer.append('\n');
         buffer.append(BrowserResources.getString(542));
         buffer.append(stats.getBytesSent());
         buffer.append('\n');
         buffer.append(BrowserResources.getString(549));
         buffer.append(stats.getBytesReceived());
      }

      buffer.append("\n\n");
      buffer.append(BrowserResources.getString(590));
      buffer.append('\n');
      SecurityInfo securityInfo = null;
      Page currentPage = this.getPage();
      if (currentPage != null) {
         securityInfo = currentPage.getSecurityInfo();
      }

      if (securityInfo != null) {
         buffer.append(BrowserResources.getString(421));
         buffer.append(securityInfo.getProtocolName());
         String version = securityInfo.getProtocolVersion();
         if (version != null) {
            buffer.append(' ');
            buffer.append('(');
            buffer.append(version);
            buffer.append(')');
         }

         buffer.append('\n');
         String suites = securityInfo.getCipherSuite();
         if (suites != null) {
            buffer.append(BrowserResources.getString(425));
            buffer.append(suites);
            buffer.append('\n');
         }
      } else {
         buffer.append(BrowserResources.getString(550));
      }

      UiApplication.getUiApplication().pushScreen(new DialogShowInfo(buffer.toString(), securityInfo));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      boolean var8 = false /* VF: Semaphore variable */;

      boolean var10;
      label32: {
         try {
            var8 = true;
            if (this._dispatchKeyEventHandled) {
               var10 = false;
               var8 = false;
               break label32;
            }

            var10 = super.dispatchKeyEvent(event, key, keycode, time);
            var8 = false;
         } finally {
            if (var8) {
               this._dispatchKeyEventHandled = false;
            }
         }

         this._dispatchKeyEventHandled = false;
         return var10;
      }

      this._dispatchKeyEventHandled = false;
      return var10;
   }

   @Override
   public final void setThemeAttributesSpecial(ThemeAttributeSet themeAttributesSpecial, Graphics graphics) {
      super.setThemeAttributesSpecial(themeAttributesSpecial, graphics);
      if (graphics != null) {
         graphics.setColor(0);
         graphics.setBackgroundColor(16777215);
      }
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
      graphics.setBackgroundColor(16777215);
      graphics.clear();
   }

   static {
      _subMenuMapping.put(1180709, 1);
      _subMenuMapping.put(1180533, 2);
      _subMenuMapping.put(1180160, 2);
      _subMenuMapping.put(16987173, 1);
      _subMenuMapping.put(1312144, 3);
      _subMenuMapping.put(16987136, 1);
      _subMenuMapping.put(16987157, 1);
      _subMenuMapping.put(30240, 2);
      _subMenuMapping.put(139266, 2);
      _subMenuMapping.put(4096, 1);
      _subMenuMapping.put(139265, 2);
      _subMenuMapping.put(16864293, 2);
      _subMenuMapping.put(268501008, 0);
      _subMenuMapping.put(268501000, 0);
      _subMenuMapping.put(1180240, 0);
      _subMenuMapping.put(341248, 0);
   }
}
