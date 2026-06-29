package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.bookmark.AddBookmarkVerb;
import net.rim.device.apps.internal.browser.bookmark.BookmarksVerb;
import net.rim.device.apps.internal.browser.options.OptionsVerb;
import net.rim.device.apps.internal.browser.page.PageVerb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.apps.internal.browser.verbs.GoToVerb;
import net.rim.device.apps.internal.browser.verbs.HistoryBackVerb;
import net.rim.device.apps.internal.browser.verbs.HistoryForwardVerb;
import net.rim.device.apps.internal.browser.verbs.SavePageToMessageListVerb;
import net.rim.device.apps.internal.browser.verbs.SaveRequestToMessageListVerb;
import net.rim.device.internal.i18n.CommonResource;

public final class BrowserVerbRepository {
   private BrowserVerb[] _verbs = new BrowserVerb[22];
   public static final int ADD_BOOKMARK_VERB = 0;
   public static final int BOOKMARKS_VERB = 1;
   public static final int BROWSER_HIDE_VERB = 2;
   public static final int BROWSER_QUIT_VERB = 3;
   public static final int HISTORY_BACK_VERB = 4;
   public static final int HISTORY_FORWARD_VERB = 5;
   public static final int HISTORY_VERB = 6;
   public static final int HOME_PAGE_VERB = 7;
   public static final int OPTIONS_VERB = 8;
   public static final int RELOAD_VERB = 9;
   public static final int SAVE_PAGE_TO_MESSAGE_LIST_VERB = 10;
   public static final int SAVE_REQUEST_TO_MESSAGE_LIST_VERB = 11;
   public static final int CHARSET_VERB = 12;
   public static final int OFFLINE_QUEUES_VERB = 13;
   public static final int SEND_ADDRESS_VERB = 14;
   public static final int GO_TO_VERB = 15;
   public static final int STOP_LOADING_VERB = 16;
   public static final int LONG_TERM_HISTORY = 17;
   public static final int PAGE_OVERVIEW = 18;
   public static final int NEW_TAB = 19;
   public static final int CHANGE_TAB = 20;
   public static final int ZOOM_TO_FIT = 21;
   private static final int TOTAL_COUNT = 22;
   private static final int ALL_VERBS = 4194303;
   private static final int RESTRICTED_VERBS = 66056;
   public static final int DEFAULT_VERB_CONFIGURATION = 4194303;
   public static final int RESTRICTED_VERB_CONFIGURATION = 66056;
   public static final int UNRESTRICTED_VERB_CONFIGURATION = 4194303;

   public BrowserVerbRepository() {
      this.initialize();
   }

   private final synchronized void initialize() {
      ResourceBundleFamily browser = BrowserResources.getResourceBundle();
      ResourceBundleFamily runtimeCommon = CommonResource.getBundle();
      this._verbs[0] = new AddBookmarkVerb();
      this._verbs[1] = new BookmarksVerb();
      this._verbs[2] = new PageVerb(268501008, browser, 428);
      this._verbs[3] = new PageVerb(268501008, runtimeCommon, 9);
      this._verbs[15] = new GoToVerb();
      this._verbs[4] = new HistoryBackVerb();
      this._verbs[5] = new HistoryForwardVerb();
      this._verbs[6] = new PageVerb(1180533, browser, 796);
      this._verbs[7] = new PageVerb(1180160, browser, 116);
      this._verbs[8] = new OptionsVerb(true);
      this._verbs[9] = new PageVerb(1180672, browser, 113);
      this._verbs[10] = new SavePageToMessageListVerb();
      this._verbs[11] = new SaveRequestToMessageListVerb();
      this._verbs[16] = new PageVerb(1179984, browser, 163);
      this._verbs[12] = new PageVerb(1180709, browser, 617);
      this._verbs[13] = new PageVerb(1180688, browser, 661);
      this._verbs[14] = new PageVerb(16987168, browser, 676);
      this._verbs[17] = new PageVerb(1180533, browser, 368);
      this._verbs[18] = new PageVerb(139265, browser, 807);
      this._verbs[19] = new PageVerb(1180256, browser, 896);
      this._verbs[20] = new PageVerb(1180257, browser, 897);
      this._verbs[21] = new PageVerb(139265, browser, 905);
   }

   public final void cleanup() {
      BrowserVerb[] verbs;
      synchronized (this) {
         verbs = new BrowserVerb[22];
         System.arraycopy(this._verbs, 0, verbs, 0, 22);
         this._verbs = new BrowserVerb[22];
      }

      for (int i = 0; i < 22; i++) {
         if (verbs[i] != null) {
            verbs[i].cleanup();
         }
      }
   }

   public final synchronized void reinitialize() {
      this.cleanup();
      this.initialize();
   }

   public final BrowserVerb getVerb(int index, int mask) {
      if (index >= 0 && index < 22) {
         BrowserVerb verb;
         synchronized (this) {
            verb = this._verbs[index];
         }

         if (verb != null && verb.isEnabled() && (mask & 1 << index) != 0) {
            return verb;
         }
      }

      return null;
   }

   public final synchronized BrowserVerb getVerbNoCheck(int index) {
      return index >= 0 && index < 22 ? this._verbs[index] : null;
   }

   public final void addVerbsToMenu(Menu menu, int mask) {
      BrowserVerb[] verbs;
      synchronized (this) {
         verbs = new BrowserVerb[22];
         System.arraycopy(this._verbs, 0, verbs, 0, 22);
      }

      for (int i = 0; i < 22; i++) {
         BrowserVerb verb = verbs[i];
         if (verb != null && verb.isEnabled() && (mask & 1 << i) != 0) {
            int priority = Integer.MAX_VALUE;
            if (i == 16) {
               priority = 140;
            } else if (i == 15) {
               priority = 141;
            } else if (i == 2) {
               continue;
            }

            MenuItem item = new VerbMenuItem(verb, priority);
            menu.add(item);
            if (menu.getDefault() == null) {
               menu.setDefault(item);
            }
         }
      }
   }

   public final synchronized BrowserVerb getVerbByHotkey(char key, int mask) {
      Verb match = BrowserHotkeys.getVerb(key);

      for (int i = 0; i < 22; i++) {
         BrowserVerb verb = this._verbs[i];
         if (verb == match && (mask & 1 << i) != 0) {
            return verb;
         }
      }

      return null;
   }
}
