package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.history.HistoryNode;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public final class HistoryForwardVerb extends BrowserVerb {
   private static final int DESCRIPTION = 104;

   public HistoryForwardVerb() {
      super(1180496);
      BrowserHotkeys.registerBrowserHotKey(339, this);
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(104);
   }

   @Override
   public final Object invoke(Object context) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session == null) {
         return null;
      }

      History history = session.getHistory();
      if (history.canGoForward()) {
         HistoryNode node = history.nextNode();
         if (node == null) {
            return null;
         }

         ModelResult modelResult = new ModelResult(node.getUrl(), 1, node.getRequestHeaders());
         modelResult.setContext(node.getContext());
         modelResult.setHomePage(node.isHomePage());
         modelResult.setPostData(node.getPostData());
         FetchRequest fetchRequest = new FetchRequest(modelResult);
         fetchRequest.setTarget(node.getFrameset());
         fetchRequest.setHistoryRequest(true);
         String currentConfigUID = session.getConfig().getUid();
         String historyConfigUID = currentConfigUID;
         BrowserConfigRecord historyConfig = BrowserConfigRecord.getDecodedConfig(node.getConfigUID(), node.getConfigType(), node.getTransportCID());
         if (historyConfig != null) {
            historyConfigUID = historyConfig.getUid();
         }

         if (!StringUtilities.strEqualIgnoreCase(historyConfigUID, currentConfigUID, 1701707776)) {
            browser.activateConfig(historyConfigUID, true);
         }

         browser.initiateFetchRequest(fetchRequest);
      }

      return null;
   }

   @Override
   public final boolean isEnabled() {
      BrowserSession session = BrowserSession.getCurrentSession();
      return session != null ? session.getHistory().canGoForward() : false;
   }

   @Override
   public final void cleanup() {
      BrowserHotkeys.deregisterBrowserHotKey(339);
   }
}
