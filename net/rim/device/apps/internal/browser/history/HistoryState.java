package net.rim.device.apps.internal.browser.history;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.common.BrowserLockScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.core.RibbonManagerThread;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.internal.ui.IconCollection;

public final class HistoryState extends MainScreen implements ListFieldCallback, BrowserLockScreen {
   private Vector _options = (Vector)(new Object());
   private int _currUrl = -1;
   private History _history;

   public HistoryState() {
      this.setTitle((Field)(new Object(BrowserResources.getString(796))));
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         this._history = session.getHistory();
         this._currUrl = this._history.lookupCurrentNodeId();
         HistoryNode node = this._history.lookupNodeAt(0);

         for (int i = 1; node != null; i++) {
            String title = node.getTitle();
            title = title != null && title.length() != 0 ? title : node.getUrl();
            this._options.addElement(title);
            node = this._history.lookupNodeAt(i);
         }

         ListField fields = (ListField)(new Object(this._options.size()));
         fields.setCallback(this);
         this.add(fields);
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int x = 0;
      IconCollection icons = BrowserIcons.getIcons();
      if (icons != null) {
         int height = listField.getRowHeight();
         int iconWidth = icons.getWidth(width, height);
         icons.paint(graphics, x, y, iconWidth, height, 2);
         x += iconWidth + 2;
      }

      if (index == this._currUrl) {
         graphics.setFont(Font.getDefault().derive(3));
      }

      graphics.drawText(this.get(index), x, y, 70, width - x);
      if (index == this._currUrl) {
         graphics.setFont(Font.getDefault());
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.get(index);
   }

   private final String get(int index) {
      return (String)(index < this._options.size() && index >= 0 ? this._options.elementAt(index) : null);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.handleSelection();
            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         synchronized (Application.getEventLock()) {
            UiApplication.getUiApplication().popScreen(this);
            this.cleanupScreen();
            return true;
         }
      } else if (key == '\n') {
         this.handleSelection();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if ((key == 19 || key == 21) && RibbonManagerThread.browserOwnsConvenienceKey(key)) {
         this.handleSelection();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   private final void handleSelection() {
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      Field fieldWithFocus = s.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof Object) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         if (this._history != null) {
            HistoryNode node = this._history.lookupNodeAt(index);
            if (node != null) {
               String prompt = BrowserResources.getString(223);
               String url = node.getUrl();
               String title = node.getTitle();
               String[] choices = new Object[]{CommonResources.getString(117), CommonResources.getString(9042)};
               String name = title != null && title.length() != 0 && !title.equals(url)
                  ? ((StringBuffer)(new Object())).append(title).append(" (").append(url).append(')').toString()
                  : url;
               if (Dialog.ask(((StringBuffer)(new Object())).append(prompt).append(name).toString(), choices, 0) != 0) {
                  return;
               }

               synchronized (Application.getEventLock()) {
                  UiApplication.getUiApplication().popScreen(this);
               }

               this._history.getNode(index);
               BrowserImpl browser = BrowserDaemonRegistry.getInstance();
               browser.releaseBrowserLock();
               ModelResult modelResult = new ModelResult(url, 1, node.getRequestHeaders());
               modelResult.setContext(node.getContext());
               modelResult.setHomePage(node.isHomePage());
               modelResult.setPostData(node.getPostData());
               FetchRequest fetchRequest = new FetchRequest(modelResult);
               fetchRequest.setTarget(node.getFrameset());
               fetchRequest.setHistoryRequest(true);
               String currentConfigUID = null;
               BrowserSession currentSession = BrowserSession.getCurrentSession();
               if (currentSession != null) {
                  currentConfigUID = currentSession.getConfig().getUid();
               }

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
         }
      }
   }

   @Override
   public final void close() {
      super.close();
      this.cleanupScreen();
   }

   @Override
   public final void cleanupScreen() {
      BrowserDaemonRegistry.getInstance().releaseBrowserLock();
   }
}
