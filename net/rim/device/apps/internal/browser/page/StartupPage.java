package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.bookmark.Bookmarks;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.history.LongTermHistory;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.DomainOverrides;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.stack.StackManager;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.apps.internal.browser.ui.UrlEntryField;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.container.FrameLayout;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class StartupPage extends Page implements FieldChangeListener {
   private Image _defaultPageImage;
   private String _initialConfigUID;
   private int _variableItemIndex;
   public static String STARTUP_URL = "about:start";
   private static final int ICON_WIDTH;
   private static final int ICON_HEIGHT;
   private static final int ID_BOOKMARKS;
   private static final int ID_HISTORY;
   private static final Tag BG_TAG = Tag.create("browser-startup-background");
   private static final Tag URL_ENTRY_TAG = Tag.create("browser-startup-urlentry");
   private static final Tag URL_ENTRY_BG = Tag.create("browser-startup-urlbg");
   private static final Tag HEADER_TAG = Tag.create("browser-startup-header");
   private static final Tag ENTRY_TAG = Tag.create("browser-startup-entry");

   public StartupPage(RenderingOptions renderingOptions) {
      super(new FetchRequest(new ModelResult(STARTUP_URL, 0, null), null, 0), null, 0);
      this.setBrowserContent((BrowserContentImpl)(new Object(null, STARTUP_URL, (Manager)(new Object(3459045988797251584L)), this, renderingOptions, 0)));
      String configUID = BrowserDaemonRegistry.getInstance().getInitialConfigUID();
      BrowserConfigRecord currentConfig = BrowserConfigRecord.getDecodedConfig(configUID, BrowserConfigRecord.INVALID_VALUE, null);
      if (currentConfig != null) {
         this._initialConfigUID = currentConfig.getUid();
         this.setTitle(currentConfig.getLocalizedString(11));
      }

      this.setVerbMask(196874);
      this._defaultPageImage = BrowserIcons.getIcons().getImage(3);
      this.populate();
   }

   @Override
   public final Verb getPrevVerb() {
      BrowserSession session = BrowserSession.getCurrentSession();
      return session != null && session.getHistory().getSize() > 0 ? new StartupPage$StartupBackVerb() : null;
   }

   @Override
   public final void addPageSpecificMenuItems(Menu menu) {
      super.addPageSpecificMenuItems(menu);
      Verb backVerb = this.getPrevVerb();
      if (backVerb != null) {
         menu.add((MenuItem)(new Object(backVerb, 0)));
      }
   }

   private final Field getLabelField(String text, boolean showImage, EncodedImage img, Tag attrTag, Object cookie) {
      LabelField lf = new StartupPage$CopyDisabledLabelField(this, text, cookie);
      lf.setTag(attrTag);
      if (showImage) {
         HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
         if (img != null) {
            BitmapField bf = (BitmapField)(new Object());
            bf.setImage(img);
            hfm.add(bf);
         } else {
            ImageField imgField = (ImageField)(new Object());
            imgField.setImage(this._defaultPageImage);
            imgField.setPreferredSize(16, 16);
            hfm.add(imgField);
         }

         hfm.add(lf);
         return hfm;
      } else {
         return lf;
      }
   }

   final void repopulate() {
      BrowserContent browserContent = this.getBrowserContent();
      VerticalIndentFieldManager mgr = (VerticalIndentFieldManager)browserContent.getDisplayableContent();
      int focus = mgr.getFieldWithFocusIndex();
      if (this._variableItemIndex == 0) {
         mgr.deleteAll();
         this.populate();
      } else {
         mgr.deleteRange(this._variableItemIndex, mgr.getFieldCount() - this._variableItemIndex);
         this.populateVariableItems(mgr);
      }

      focus = Math.min(focus, mgr.getFieldCount() - 1);
      if (focus >= 0) {
         mgr.getField(focus).setFocus();
      }

      String configUID = BrowserDaemonRegistry.getInstance().getInitialConfigUID();
      BrowserConfigRecord currentConfig = BrowserConfigRecord.getDecodedConfig(configUID, BrowserConfigRecord.INVALID_VALUE, null);
      if (currentConfig != null) {
         this._initialConfigUID = currentConfig.getUid();
         this.setTitle(currentConfig.getLocalizedString(11));
         this.eventOccurred((Event)(new Object(this.getBrowserContent())));
      }
   }

   private final void populate() {
      BrowserContent browserContent = this.getBrowserContent();
      VerticalIndentFieldManager mgr = (VerticalIndentFieldManager)browserContent.getDisplayableContent();
      mgr.setTag(BG_TAG);
      Font urlFont = Font.getDefault();
      Theme t = ThemeManager.getActiveTheme();
      if (t != null) {
         ThemeAttributeSet ts = t.getAttributeSet(URL_ENTRY_TAG);
         if (ts != null) {
            Font f = ts.getFont();
            if (f != null) {
               urlFont = f;
            }
         }
      }

      UrlEntryField urlText = new UrlEntryField(urlFont);
      urlText.setChangeListener(this);
      int numChars = Display.getWidth() * 8 / 10 / urlFont.getAdvance('x');
      Manager subMgr = (Manager)(new Object(numChars, 1, 1125899906842624L, urlFont));
      subMgr.add(urlText);
      FrameLayout urlTextManager = (FrameLayout)(new Object(12884901889L));
      urlTextManager.setTag(URL_ENTRY_TAG);
      urlTextManager.add(subMgr);
      VerticalFieldManager vmgr = (VerticalFieldManager)(new Object(1152921504606846976L));
      vmgr.setTag(URL_ENTRY_BG);
      vmgr.add(urlTextManager);
      mgr.add(vmgr);
      this.populateVariableItems(mgr);
   }

   private final void populateVariableItems(VerticalIndentFieldManager mgr) {
      this._variableItemIndex = mgr.getFieldCount();
      mgr.add(this.getLabelField(BrowserResources.getString(849), false, null, HEADER_TAG, new Object(0)));
      PageModel[] bookmarkModels = Bookmarks.getLastAccessedBookmarks(5);

      for (int i = 0; i < bookmarkModels.length; i++) {
         String title = bookmarkModels[i].getTitle();
         if (bookmarkModels[i].isHomePage()) {
            PageModel var10000 = bookmarkModels[i];
            if (bookmarkModels[i] instanceof BrowserPageModel) {
               BrowserPageModel bmark = (BrowserPageModel)var10000;
               Folder folder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, bmark.getFolderId());
               if (folder != null && folder.getFriendlyName().length() > 0) {
                  title = ((StringBuffer)(new Object())).append(title).append(" (").append(folder.getFriendlyName()).append(')').toString();
               }
            }
         }

         mgr.add(this.getLabelField(title, true, this.getIconImage(bookmarkModels[i].getIconUrl()), ENTRY_TAG, bookmarkModels[i]), 5);
      }

      mgr.add(this.getLabelField(BrowserResources.getString(891), false, null, HEADER_TAG, new Object(1)));
      bookmarkModels = LongTermHistory.getInstance().getMostRecentUrls(5);

      for (int i = 0; i < bookmarkModels.length; i++) {
         String iconUrl = ((StringBuffer)(new Object("http://"))).append(bookmarkModels[i].getDomain()).append("/favicon.ico").toString();
         mgr.add(this.getLabelField(bookmarkModels[i].getTitle(), true, this.getIconImage(iconUrl), ENTRY_TAG, bookmarkModels[i]), 5);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context == 132388 && field instanceof UrlEntryField) {
         this.loadUrl((UrlEntryField)field);
      }
   }

   private final void loadUrl(UrlEntryField urlText) {
      String urlToFetch = urlText.getUrlToLoad();
      if (urlToFetch.length() > 0) {
         ModelResult mresult = new ModelResult(urlToFetch, 8449, null);
         this.setBrowserConfig(urlToFetch);
         FetchRequest fetchRequest = new FetchRequest(mresult);
         BrowserDaemonRegistry.getInstance().initiateFetchRequest(fetchRequest);
      }
   }

   private final void setBrowserConfig(String urlToFetch) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      BrowserSession currentSession = BrowserSession.getCurrentSession();
      String configUID = null;
      if (urlToFetch != null) {
         int currentConfigType = BrowserConfigRecord.INVALID_VALUE;
         if (currentSession != null) {
            currentConfigType = currentSession.getConfig().getPropertyAsInt(12);
         }

         String overrideConfigUID = DomainOverrides.getInstance().getOverride(urlToFetch, currentConfigType);
         if (overrideConfigUID != null) {
            configUID = overrideConfigUID;
         }
      }

      if (configUID == null) {
         configUID = this._initialConfigUID;
         if (!"S TCP-WBC".equalsIgnoreCase(this._initialConfigUID)) {
            configUID = StackManager.getInstance().getRoutableService(configUID, null);
         }
      }

      if (configUID != null) {
         String currentConfigUID = null;
         if (currentSession != null) {
            currentConfigUID = currentSession.getConfig().getUid();
         }

         if (!StringUtilities.strEqualIgnoreCase(configUID, currentConfigUID, 1701707776)) {
            browser.activateConfig(configUID, true);
         }
      }
   }

   private final EncodedImage getIconImage(String url) {
      EncodedImage icon = null;
      if (url != null) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         RawDataCache cache = browser.getRawDataCache();
         CacheResult result = cache.get(url);
         if (result != null) {
            byte[] data = result.getDataAsArray();
            icon = ImageConverter.convertAndScale(data, 0, data.length, null, 16, 16, Display.getWidth());
         }
      }

      return icon;
   }
}
