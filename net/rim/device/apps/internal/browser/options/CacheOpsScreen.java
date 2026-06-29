package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.browser.cookie.CookieCache;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.history.HistoryNode;
import net.rim.device.apps.internal.browser.history.LongTermHistory;
import net.rim.device.apps.internal.browser.page.PageCache;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.stack.StackManager;
import net.rim.device.apps.internal.browser.util.VisitedURLStore;
import net.rim.vm.Memory;

final class CacheOpsScreen extends AppsMainScreen implements IBrowserProperty, FieldChangeListener {
   private ButtonField _clearContentCacheButton;
   private ButtonField _clearCookiesButton;
   private ButtonField _clearPersistentCacheButton;
   private ButtonField _clearPasswordsButton;
   private ButtonField _clearHistoryButton;
   private ButtonField _revalidateButton;
   private LabelField _shortTermSizeLabel;
   private LabelField _cookieSizeLabel;
   private LabelField _longTermSizeLabel;
   private PageCache _pageCache;
   private boolean _dirty;

   CacheOpsScreen() {
      super(3458764513820540928L);
      this.setTitle(BrowserResources.getString(653));
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      this.add(new CacheOpsScreen$HackField());
      if (StackManager.getInstance().authExists() || !GeneralProperty.getAuthenticationCredentials().isEmpty()) {
         this._clearPasswordsButton = new ButtonField(BrowserResources.getString(623), 8590000128L);
         this._clearPasswordsButton.setChangeListener(this);
         this.add(this._clearPasswordsButton);
      }

      BrowserSession session = BrowserSession.getCurrentSession();
      History history = null;
      if (session != null) {
         history = session.getHistory();
         this._pageCache = session.getPageCache();
      }

      VisitedURLStore urlStore = VisitedURLStore.getInstance();
      LongTermHistory longTermHistory = LongTermHistory.getInstance();
      if (history != null && history.getSize() > 1 || urlStore.size() > 0 || longTermHistory.getNumberOfElements() > 0) {
         this._clearHistoryButton = new ButtonField(BrowserResources.getString(669), 8590000128L);
         this._clearHistoryButton.setChangeListener(this);
         this.add(this._clearHistoryButton);
      }

      this.add(new LabelField(BrowserResources.getString(194)));
      this._shortTermSizeLabel = new LabelField("", 8589934592L);
      this.add(this._shortTermSizeLabel);
      if (rawDataCache.getShortTermCacheCount() > 0 || this._pageCache != null && this._pageCache.getCount() > 0) {
         this._clearContentCacheButton = new ButtonField(BrowserResources.getString(713), 8590000128L);
         this._clearContentCacheButton.setChangeListener(this);
         this.add(this._clearContentCacheButton);
      }

      this.add(new LabelField(BrowserResources.getString(454)));
      this._longTermSizeLabel = new LabelField("", 8589934592L);
      this.add(this._longTermSizeLabel);
      if (rawDataCache.containsPushedContent()) {
         this._clearPersistentCacheButton = new ButtonField(BrowserResources.getString(713), 8590000128L);
         this._clearPersistentCacheButton.setChangeListener(this);
         this.add(this._clearPersistentCacheButton);
      }

      int numCookies = CookieCache.getInstance().getNumUniqueHosts();
      this.add(new LabelField(BrowserResources.getString(210)));
      this._cookieSizeLabel = new LabelField("", 8589934592L);
      this.add(this._cookieSizeLabel);
      if (numCookies > 0) {
         this._clearCookiesButton = new ButtonField(BrowserResources.getString(713), 8590000128L);
         this._clearCookiesButton.setChangeListener(this);
         this.add(this._clearCookiesButton);
      }

      if (this._clearContentCacheButton != null || this._clearPersistentCacheButton != null) {
         this._revalidateButton = new ButtonField(BrowserResources.getString(746), 8590000128L);
         this._revalidateButton.setChangeListener(this);
         this.add(this._revalidateButton);
      }

      this.updateSizeLabels();
   }

   private final void updateSizeLabels() {
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      String size = BrowserResources.getString(513) + ' ';
      this._shortTermSizeLabel.setText(size + formatK(rawDataCache.getShortTermCacheSize(true)) + ' ');
      int cookieCount = CookieCache.getInstance().getNumCookies();
      if (cookieCount == 1) {
         this._cookieSizeLabel.setText(size + BrowserResources.getString(716));
      } else {
         this._cookieSizeLabel.setText(size + MessageFormat.format(BrowserResources.getString(714), new String[]{Integer.toString(cookieCount)}));
      }

      this._longTermSizeLabel.setText(size + formatK(rawDataCache.getLongTermCacheSize(false)) + ' ');
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      RawDataCache rawDataCache = browser.getRawDataCache();
      if (field == this._clearContentCacheButton) {
         rawDataCache.clearShortTermCache();
         if (this._pageCache != null) {
            this._pageCache.destroy(false);
         }
      } else if (field == this._clearCookiesButton) {
         CookieCache.getInstance().clearCookies();
      } else if (field == this._clearPersistentCacheButton) {
         if (Dialog.ask(2, BrowserResources.getString(672)) != 3) {
            field.setDirty(false);
            return;
         }

         rawDataCache.clearLongTermCache(true);
      } else if (field == this._clearPasswordsButton) {
         StackManager.getInstance().clearAuth();
         GeneralProperty.clearAuthenticationCredentials();
      } else {
         if (field != this._clearHistoryButton) {
            if (field == this._revalidateButton) {
               browser.doCacheRevalidation(null);
               this.delete(field);
               return;
            }

            return;
         }

         BrowserSession session = BrowserSession.getCurrentSession();
         History history = null;
         if (session != null) {
            history = session.getHistory();
            if (history.getSize() > 0) {
               HistoryNode node = history.currentNode();
               history.clear();
               if (node != null) {
                  history.addNewNode(node);
               }
            }
         }

         LongTermHistory longTermHistory = LongTermHistory.getInstance();
         longTermHistory.clear();
         VisitedURLStore urlStore = VisitedURLStore.getInstance();
         if (urlStore.size() > 0) {
            urlStore.clear();
         }
      }

      this._dirty = true;
      this.delete(field);
      this.updateSizeLabels();
   }

   @Override
   public final boolean onClose() {
      if (this._dirty) {
         Status.show(BrowserResources.getString(743));
         Memory.thoroughGC();
         this._dirty = false;
      }

      return super.onClose();
   }

   @Override
   public final void saveProperty() {
   }

   @Override
   public final Verb getOpenVerb(boolean restrictedAccess) {
      return new OpenOptionVerb(this, restrictedAccess);
   }

   @Override
   public final String getLabel() {
      return BrowserResources.getString(653);
   }

   @Override
   public final Screen getScreen(boolean restrictedAccess) {
      if (restrictedAccess && this._revalidateButton != null && this._revalidateButton.getManager() != null) {
         this.delete(this._revalidateButton);
      }

      return this;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.onClose();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private static final String formatK(int number) {
      int k = number / 1024;
      int r = (number - k * 1024) / 100;
      return Integer.toString(k) + '.' + Integer.toString(r) + 'K';
   }

   @Override
   public final String getAccessibleName() {
      return this.getLabel();
   }
}
