package net.rim.device.apps.internal.browser.history;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.browser.common.BrowserLockScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.core.BrowserVerbRepository;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.IconCollection;

public class LongTermHistoryScreen extends MainScreen implements BrowserLockScreen, TreeFieldCallback {
   private TreeField _treeField;
   private LongTermHistory _longTermHistory;
   private int _currentSortOrder;
   private String NO_HOST_STRING = BrowserResources.getString(799);
   private Screen _screenToRemove;
   private Date _today;
   private static DateFormat DATE_FORMAT = DateFormat.getInstance(54);
   private static final int NUMBER_OF_DATE_FOLDERS = 8;
   private static final int SORT_TYPE_DATE = 0;
   private static final int SORT_TYPE_SITE = 1;
   private static final int SORT_TYPE_TODAYS_ORDER = 2;

   public LongTermHistoryScreen() {
      this(null);
   }

   public LongTermHistoryScreen(Screen screen) {
      this._screenToRemove = screen;
      this.setTitle((Field)(new Object(BrowserResources.getString(368))));
      this._treeField = (TreeField)(new Object(this, 0));
      this._treeField.setDefaultExpanded(false);
      this._longTermHistory = LongTermHistory.getInstance();
      this._longTermHistory.cleanup(0);
      this._currentSortOrder = 0;
      this.displayHistory(this._currentSortOrder);
      this.add(this._treeField);
   }

   private void calculateTodaysDate() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime((Date)(new Object(System.currentTimeMillis())));
      calendar.set(11, 0);
      calendar.set(12, 0);
      calendar.set(13, 0);
      this._today = calendar.getTime();
   }

   private void displayHistory(int sort) {
      this._treeField.deleteAll();
      this._currentSortOrder = sort;
      switch (sort) {
         case -1:
            break;
         case 0:
            this.calculateTodaysDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this._today);
            int dayOfWeek = calendar.get(7);
            String[] dateFolderLabels = new Object[8];
            dateFolderLabels[0] = BrowserResources.getString(791);
            dateFolderLabels[7] = BrowserResources.getString(795);
            dayOfWeek--;

            for (int i = 1; i < 7; i++) {
               switch (dayOfWeek) {
                  case -1:
                     break;
                  case 0:
                  case 7:
                     dateFolderLabels[i] = BrowserResources.getString(809);
                     dayOfWeek = 6;
                     break;
                  case 1:
                     dateFolderLabels[i] = BrowserResources.getString(810);
                     dayOfWeek = 7;
                     break;
                  case 2:
                  default:
                     dateFolderLabels[i] = BrowserResources.getString(792);
                     dayOfWeek = 1;
                     break;
                  case 3:
                     dateFolderLabels[i] = BrowserResources.getString(793);
                     dayOfWeek = 2;
                     break;
                  case 4:
                     dateFolderLabels[i] = BrowserResources.getString(794);
                     dayOfWeek = 3;
                     break;
                  case 5:
                     dateFolderLabels[i] = BrowserResources.getString(811);
                     dayOfWeek = 4;
                     break;
                  case 6:
                     dateFolderLabels[i] = BrowserResources.getString(808);
                     dayOfWeek = 5;
               }
            }

            long endTime = System.currentTimeMillis();
            long startTime = this._today.getTime();
            int dateFolderId = 0;

            for (int currentFolder = 0; currentFolder < dateFolderLabels.length; currentFolder++) {
               if (currentFolder != 0) {
                  endTime = startTime;
                  startTime = currentFolder <= 7 ? startTime - 86400000 : 0;
               }

               LongTermHistoryNode[] nodes = this._longTermHistory.getUrlsInRange(startTime, endTime, false);
               if (nodes != null && nodes.length != 0) {
                  if (dateFolderId == 0) {
                     this._treeField.setDefaultExpanded(true);
                     dateFolderId = this._treeField.addChildNode(dateFolderId, new Folder(dateFolderLabels[currentFolder], startTime, endTime));
                     this._treeField.setDefaultExpanded(false);
                  } else {
                     dateFolderId = this._treeField.addSiblingNode(dateFolderId, new Folder(dateFolderLabels[currentFolder], startTime, endTime));
                  }

                  this.addSiteSubtree(nodes, dateFolderId);
               }
            }
            break;
         case 1:
         default:
            LongTermHistoryNode[] nodes = this._longTermHistory.getUrlsInRange(0, Long.MAX_VALUE, false);
            this.addSiteSubtree(nodes, 0);
            return;
         case 2:
            LongTermHistoryNode[] nodes = this._longTermHistory.getUrlsInRange(this._today.getTime(), System.currentTimeMillis(), true);
            if (nodes == null || nodes.length == 0) {
               return;
            }

            int id = this._treeField.addChildNode(0, nodes[0]);

            for (int i = 1; i < nodes.length; i++) {
               id = this._treeField.addSiblingNode(id, nodes[i]);
            }
      }
   }

   private void addSiteSubtree(LongTermHistoryNode[] nodes, int parentId) {
      if (nodes != null && nodes.length != 0) {
         String currentDomain = nodes[0].getDomain();
         int parent = this._treeField.addChildNode(parentId, new Folder(currentDomain));
         int sibling = this._treeField.addChildNode(parent, nodes[0]);

         for (int i = 1; i < nodes.length; i++) {
            LongTermHistoryNode node = nodes[i];
            if (currentDomain.equalsIgnoreCase(node.getDomain())) {
               sibling = this._treeField.addSiblingNode(sibling, node);
            } else {
               currentDomain = node.getDomain();
               parent = this._treeField.addSiblingNode(parent, new Folder(currentDomain));
               sibling = this._treeField.addChildNode(parent, node);
            }
         }
      }
   }

   @Override
   public void cleanupScreen() {
      BrowserDaemonRegistry.getInstance().releaseBrowserLock();
   }

   @Override
   public void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) {
      int x = 0;
      Object item = treeField.getCookie(node);
      String label = null;
      if (!(item instanceof LongTermHistoryNode)) {
         if (item instanceof Folder) {
            label = ((Folder)item)._label;
         }
      } else {
         label = ((LongTermHistoryNode)item).getTitle();
         if (this._currentSortOrder == 2) {
            indent = 0;
         }

         IconCollection icons = BrowserIcons.getIcons();
         int height = treeField.getRowHeight();
         int iconWidth = icons.getWidth(width, height);
         icons.paint(graphics, x + indent, y, iconWidth, height, 2);
         x += iconWidth + 2;
      }

      if (label != null) {
         y = treeField.getAdjustedY(this.getFont(), label, y);
      }

      if (label == null || label.length() == 0) {
         label = this.NO_HOST_STRING;
      }

      graphics.drawText(label, x + indent, y, 70, Display.getWidth() - indent - x);
   }

   private void delete(boolean confirm) {
      int id = this._treeField.getCurrentNode();
      Object node = this._treeField.getCookie(id);
      if (confirm) {
         String title = null;
         String altTitle = null;
         if (!(node instanceof LongTermHistoryNode)) {
            if (node instanceof Folder) {
               title = ((Folder)node)._label;
               altTitle = ((Folder)node)._label;
            }
         } else {
            title = ((LongTermHistoryNode)node).getTitle();
            altTitle = ((LongTermHistoryNode)node).getUrl();
         }

         String[] name = new Object[]{title != null && title.length() != 0 ? title : altTitle};
         String message = MessageFormat.format(BrowserResources.getString(282), name);
         if (Dialog.ask(2, message) != 3) {
            return;
         }
      }

      switch (this._currentSortOrder) {
         case -1:
            break;
         case 0:
         default:
            if (!(node instanceof LongTermHistoryNode)) {
               if (node instanceof Folder) {
                  Folder folder = (Folder)node;
                  if (folder._isDateFolder) {
                     this._longTermHistory.delete(null, folder._start, folder._end, true);
                  } else {
                     long start = 0;
                     long end = Long.MAX_VALUE;
                     int parentId = this._treeField.getParent(id);
                     Object dateNode = this._treeField.getCookie(parentId);
                     if (dateNode instanceof Folder) {
                        Folder dateFolder = (Folder)dateNode;
                        if (dateFolder._isDateFolder) {
                           start = dateFolder._start;
                           end = dateFolder._end;
                        }
                     }

                     this._longTermHistory.delete(folder._label, start, end, false);
                  }
               }
            } else {
               LongTermHistoryNode lthNode = (LongTermHistoryNode)node;
               long start = 0;
               long end = Long.MAX_VALUE;
               int parentId = this._treeField.getParent(id);
               parentId = this._treeField.getParent(parentId);
               Object dateNode = this._treeField.getCookie(parentId);
               if (dateNode instanceof Folder) {
                  Folder dateFolder = (Folder)dateNode;
                  if (dateFolder._isDateFolder) {
                     start = dateFolder._start;
                     end = dateFolder._end;
                  }
               }

               this._longTermHistory.delete(lthNode.getUrl(), start, end, true);
            }
            break;
         case 1:
            if (node instanceof LongTermHistoryNode) {
               this._longTermHistory.delete(((LongTermHistoryNode)node).getUrl(), 0, Long.MAX_VALUE, true);
            } else if (node instanceof Folder) {
               this._longTermHistory.delete(((Folder)node)._label, 0, Long.MAX_VALUE, false);
            }
            break;
         case 2:
            if (node instanceof LongTermHistoryNode) {
               this._longTermHistory.delete(((LongTermHistoryNode)node).getUrl(), this._today.getTime(), System.currentTimeMillis(), true);
            }
      }

      int parentId = this._treeField.getParent(id);
      this._treeField.deleteSubtree(id);

      while (parentId != 0 && this._treeField.getFirstChild(parentId) < 0) {
         id = this._treeField.getParent(parentId);
         this._treeField.deleteSubtree(parentId);
         parentId = id;
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      return action == 1 && this.keyChar('\n', 0, 0) ? true : super.invokeAction(action);
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         int currentNode = this._treeField.getCurrentNode();
         if (currentNode > 0) {
            if (!(this._treeField.getCookie(currentNode) instanceof Folder)) {
               this.loadUrl();
               return true;
            }

            key = ' ';
         }
      } else if (key == 127) {
         this.delete(true);
         return true;
      }

      return super.keyChar(key, status, time);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void close() {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         super.close();
         if (this.isDisplayed()) {
            synchronized (Application.getEventLock()) {
               UiApplication.getUiApplication().popScreen(this);
               var6 = false;
            }
         } else {
            var6 = false;
         }
      } finally {
         if (var6) {
            if (this._screenToRemove == null) {
               this.cleanupScreen();
            }
         }
      }

      if (this._screenToRemove == null) {
         this.cleanupScreen();
      }
   }

   private void loadUrl() {
      Object item = this._treeField.getCookie(this._treeField.getCurrentNode());
      if (item instanceof LongTermHistoryNode) {
         LongTermHistoryNode node = (LongTermHistoryNode)item;
         this.close();
         if (this._screenToRemove != null) {
            this._screenToRemove.close();
         }

         this._screenToRemove = null;
         ModelResult modelResult = new ModelResult(node.getUrl(), 8193, null);
         FetchRequest fetchRequest = new FetchRequest(modelResult);
         setBrowserConfig(node);
         BrowserDaemonRegistry.getInstance().initiateFetchRequest(fetchRequest);
      }
   }

   public static void setBrowserConfig(LongTermHistoryNode node) {
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
         BrowserDaemonRegistry.getInstance().activateConfig(historyConfigUID, true);
      }
   }

   @Override
   protected void makeMenu(Menu menu, int arg1) {
      super.makeMenu(menu, arg1);
      int currentNode = this._treeField.getCurrentNode();
      if (currentNode >= 0) {
         if (this._currentSortOrder != 0) {
            menu.add(new LongTermHistoryScreen$1(this, BrowserResources.getString(797), 100, 1));
         }

         if (this._currentSortOrder != 1) {
            menu.add(new LongTermHistoryScreen$2(this, BrowserResources.getString(798), 100, 1));
         }

         if (this._currentSortOrder != 2) {
            menu.add(new LongTermHistoryScreen$3(this, BrowserResources.getString(790), 100, 1));
         }

         Object item = this._treeField.getCookie(currentNode);
         if (item instanceof LongTermHistoryNode) {
            menu.add(new LongTermHistoryScreen$4(this, BrowserResources.getString(100), 10, 1));
            menu.add(new LongTermHistoryScreen$5(this, BrowserResources.getString(802), 100, 1));
            BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
            BrowserVerbRepository verbRepository = browserImpl.getBrowserVerbRepository();
            BrowserVerb verb = verbRepository.getVerb(0, browserImpl.getVerbMask());
            if (verb != null) {
               LongTermHistoryNode node = (LongTermHistoryNode)item;
               ContextObject object = (ContextObject)(new Object());
               object.put(253, node.getUrl());
               object.put(-7261227923983886841L, node.getTitle());
               object.put(867508017068302662L, node.getConfigUID());
               menu.add((MenuItem)(new Object(null, 100, 1, verb, object)));
            }
         }

         menu.add(new LongTermHistoryScreen$6(this, CommonResource.getString(17), 100, 1));
      }
   }
}
