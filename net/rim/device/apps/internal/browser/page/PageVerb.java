package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.history.HistoryState;
import net.rim.device.apps.internal.browser.history.LongTermHistoryScreen;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;
import net.rim.device.apps.internal.browser.ui.DialogChangeTab;
import net.rim.device.apps.internal.browser.ui.DialogSelectCharset;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;
import net.rim.device.internal.system.InternalServices;

public final class PageVerb extends BrowserVerb {
   public PageVerb(int ordering, ResourceBundleFamily rb, int resourceId) {
      super(ordering, rb, resourceId);
      int id = this.mapHotkey(resourceId);
      if (id != -1) {
         BrowserHotkeys.registerBrowserHotKey(id, this);
      }
   }

   @Override
   public final Object invoke(Object context) {
      switch (super._rbKey) {
         case 9:
            boolean confirm = false;
            if (context instanceof Object) {
               confirm = context;
            }

            BrowserDaemonRegistry.getInstance().closeBrowser(confirm);
            return null;
         case 113:
            reload();
            return null;
         case 116:
            String url = null;
            BrowserSession session = BrowserSession.getCurrentSession();
            if (session != null) {
               url = session.getConfig().getHomePageWithOverride();
            }

            if (url != null && url.length() > 0) {
               ModelResult mresultx = new ModelResult(url, 8449, null);
               mresultx.setHomePage(true);
               BrowserDaemonRegistry.getInstance().initiateFetchRequest(new FetchRequest(mresultx));
               return null;
            }
            break;
         case 163:
            BrowserDaemonRegistry.getInstance().abortCurrentRequest();
            return null;
         case 368:
            BrowserDaemonRegistry.getInstance().setReleaseLock(false);
            synchronized (Application.getEventLock()) {
               UiApplication.getUiApplication().pushScreen(new LongTermHistoryScreen());
               return null;
            }
         case 428:
            BrowserDaemonRegistry.getInstance().requestBackground();
            return null;
         case 617: {
            DialogSelectCharset dialog = new DialogSelectCharset();
            if (dialog.promptForCharset()) {
               reload();
               return null;
            }
            break;
         }
         case 661:
            ModelResult mresult = new ModelResult("queue:", 8449, null);
            BrowserDaemonRegistry.getInstance().initiateFetchRequest(new FetchRequest(mresult));
            return null;
         case 676:
            this.sendAddress();
            return null;
         case 796:
            BrowserDaemonRegistry.getInstance().setReleaseLock(false);
            synchronized (Application.getEventLock()) {
               UiApplication.getUiApplication().pushScreen(new HistoryState());
               return null;
            }
         case 807:
         case 905:
            Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
            if (page != null) {
               BrowserContent content = page.getBrowserContent();
               if (content != null) {
                  Field field = content.getDisplayableContent();
                  if (field instanceof Object) {
                     BrowserTextFlowManager tfm = (BrowserTextFlowManager)field;
                     if (tfm.getWideViewMode()) {
                        tfm.adjustZoom(Integer.MAX_VALUE);
                     }
                  }
               }
            }
            break;
         case 896:
            BrowserImpl browser = BrowserDaemonRegistry.getInstance();
            BrowserScreen browserScreen = browser.getBrowserScreen();
            if (browserScreen != null) {
               browser.showStartupPage(true);
               return null;
            }
            break;
         case 897: {
            DialogChangeTab dialog = new DialogChangeTab();
            dialog.show();
            return null;
         }
      }

      return null;
   }

   @Override
   public final boolean isModal() {
      switch (super._rbKey) {
         case 368:
         case 796:
            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean isEnabled() {
      BrowserSession session = BrowserSession.getCurrentSession();
      switch (super._rbKey) {
         case 9:
            if (session != null && session.getConfig().getPropertyAsInt(20) == 1) {
               return false;
            }

            return true;
         case 113:
         case 676:
            try {
               return BrowserDaemonRegistry.getInstance().getCurrentPage().getURL() != null;
            } finally {
               ;
            }
         case 163:
            int state = BrowserDaemonRegistry.getInstance().getBrowserExecutionState();
            if (state != 5 && state != 0) {
               return true;
            }

            return false;
         case 428:
            if (session != null && session.getConfig().getPropertyAsInt(20) == 1) {
               return false;
            }

            return true;
         case 617:
            return true;
         case 661:
            if (BrowserDaemonRegistry.getInstance().getOfflineQueue().numQueues() > 0) {
               return true;
            }

            return false;
         case 807:
            return false;
         case 896:
            return false;
         case 897:
            BrowserImpl browser = BrowserDaemonRegistry.getInstance();
            BrowserScreen browserScreen = browser.getBrowserScreen();
            if (browserScreen != null) {
               if (browserScreen.getNumberOfTabs() > 1) {
                  return true;
               }

               return false;
            }

            return false;
         case 905:
            try {
               Object content = BrowserDaemonRegistry.getInstance().getCurrentPage().getContentManager();
               if (content instanceof Object) {
                  if (!(content instanceof Object)) {
                     return true;
                  }

                  BrowserTextFlowManager btfm = (BrowserTextFlowManager)content;
                  if (super._rbKey == 807) {
                     return !btfm.getWideViewMode();
                  }

                  return btfm.getWideViewMode() && btfm.canZoomOut();
               }
            } finally {
               return false;
            }

            return false;
         default:
            return super.isEnabled();
      }
   }

   @Override
   public final void cleanup() {
      int id = this.mapHotkey(super._rbKey);
      if (id != -1) {
         BrowserHotkeys.deregisterBrowserHotKey(id);
      }
   }

   private final int mapHotkey(int id) {
      boolean isReducedKeyboard = InternalServices.isReducedFormFactor();
      switch (id) {
         case 113:
            if (isReducedKeyboard) {
               return -1;
            }

            return 336;
         case 116:
            if (isReducedKeyboard) {
               return -1;
            }

            return 340;
         case 368:
            if (isReducedKeyboard) {
               return -1;
            }

            return 515;
         case 428:
            if (isReducedKeyboard) {
               return -1;
            }

            return 663;
         default:
            return -1;
      }
   }

   static final void reload() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      Page page = browser.getCurrentPage();
      if (page != null) {
         String url = page.getURL();
         if (url != null) {
            boolean isHomePage = false;
            byte[] postData = null;
            HttpHeaders requestHeaders = null;
            ModelResult oldModelResult = (ModelResult)page.getModelResult();
            if (oldModelResult != null) {
               isHomePage = oldModelResult.isHomePage();
               postData = oldModelResult.getPostData();
               if (postData != null) {
                  HttpHeaders oldRequestHeaders = oldModelResult.getRequestHeaders();
                  if (oldRequestHeaders != null) {
                     String contentType = oldRequestHeaders.getPropertyValue("Content-Type");
                     if (contentType != null) {
                        requestHeaders = (HttpHeaders)(new Object());
                        requestHeaders.setProperty("Content-Type", contentType);
                     }
                  }
               }
            }

            ModelResult modelResult = new ModelResult(url, 3, requestHeaders);
            modelResult.setHomePage(isHomePage);
            modelResult.setPostData(postData);
            FetchRequest fr = new FetchRequest(modelResult);
            fr.setTarget(page.getFrameset());
            browser.initiateFetchRequest(fr);
         }
      }
   }

   private final void sendAddress() {
      Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
      String url = page.getURL();
      if (url != null) {
         ShowUrlVerb.sendAddress(url, page.getFriendlyTitle(), 676);
      }
   }
}
