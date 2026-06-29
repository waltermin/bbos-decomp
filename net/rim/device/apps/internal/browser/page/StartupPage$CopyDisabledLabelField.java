package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;
import net.rim.device.apps.internal.browser.bookmark.BookmarksVerb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.history.LongTermHistory;
import net.rim.device.apps.internal.browser.history.LongTermHistoryNode;
import net.rim.device.apps.internal.browser.history.LongTermHistoryScreen;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.verbs.FollowLinkVerb;

class StartupPage$CopyDisabledLabelField extends LabelField {
   private Object _cookie;
   private final StartupPage this$0;

   public StartupPage$CopyDisabledLabelField(StartupPage _1, String label, Object cookie) {
      super(label, 1170935903116329024L);
      this.this$0 = _1;
      this._cookie = cookie;
   }

   @Override
   public boolean isSelectionCopyable() {
      return false;
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      this.doAction();
      return true;
   }

   @Override
   protected boolean keyChar(char character, int status, int time) {
      if (character == '\n') {
         this.doAction();
         return true;
      } else if (character == '\b' && this._cookie instanceof LongTermHistoryNode) {
         LongTermHistory.getInstance().delete(((LongTermHistoryNode)this._cookie).getUrl(), 0, Long.MAX_VALUE, true);
         this.this$0.repopulate();
         return true;
      } else {
         return super.keyChar(character, status, time);
      }
   }

   private void doAction() {
      if (!(this._cookie instanceof PageModel)) {
         if (!(this._cookie instanceof LongTermHistoryNode)) {
            if (this._cookie instanceof Object) {
               switch (this._cookie) {
                  case -1:
                     break;
                  case 0:
                  default: {
                     BookmarksVerb verb = new BookmarksVerb();
                     verb.invoke(null);
                     return;
                  }
                  case 1: {
                     Verb verb = BrowserDaemonRegistry.getInstance().getBrowserVerbRepository().getVerbNoCheck(17);
                     if (verb != null) {
                        verb.invoke(null);
                     }
                  }
               }
            }
         } else {
            LongTermHistoryNode node = (LongTermHistoryNode)this._cookie;
            LongTermHistoryScreen.setBrowserConfig(node);
            FollowLinkVerb verb = new FollowLinkVerb(node.getUrl(), false, null);
            verb.invoke(null);
         }
      } else {
         PageModel model = (PageModel)this._cookie;
         boolean configWasSet = false;
         if (model instanceof BrowserPageModel) {
            BrowserPageModel browserPageModel = (BrowserPageModel)model;
            configWasSet = BookmarksScreen.setConfigFromModelOrFolder(
               browserPageModel, FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, browserPageModel.getFolderId())
            );
         }

         if (!configWasSet) {
            this.this$0.setBrowserConfig(null);
         }

         FollowLinkVerb verb = new FollowLinkVerb(model.getUrl(), model.isHomePage(), model.getTitle());
         verb.invoke(null);
         model.setLastAccessedTime(System.currentTimeMillis());
         BrowserDaemonRegistry.broadCastEvent(102, model);
      }
   }
}
