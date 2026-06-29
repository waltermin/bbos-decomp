package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.messaging.util.SortedCollection;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.channel.ChannelModel;
import net.rim.device.apps.internal.browser.channel.Channels;
import net.rim.device.apps.internal.browser.channel.DeleteChannelVerb;
import net.rim.device.apps.internal.browser.channel.OpenChannelVerb;
import net.rim.device.apps.internal.browser.common.BrowserLockScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.core.MenuDelayThread;
import net.rim.device.apps.internal.browser.core.RibbonManagerThread;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.history.LongTermHistory;
import net.rim.device.apps.internal.browser.options.BrowserConfigChangeListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.DomainOverrides;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.options.OptionsVerb;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.page.PageVerb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.stack.StackManager;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.store.FolderEventListener;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.apps.internal.browser.util.DomainUtilities;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.apps.internal.browser.verbs.FollowLinkVerb;
import net.rim.device.apps.internal.browser.verbs.GoToVerb;
import net.rim.vm.Array;

public final class BookmarksScreen extends FolderList implements BrowserConfigChangeListener, FolderEventListener, BrowserLockScreen {
   private boolean _isInitialScreen;
   private BookmarksScreenCloseVerb _bookmarksScreenCloseVerb;
   private Verb _browserHideVerb = new PageVerb(268501008, BrowserResources.getResourceBundle(), 428);
   private Verb _deleteFolderVerb;
   private Verb _createFolderVerb;
   private Verb _renameFolderVerb;
   private Verb _moveBookmarkVerb = new BookmarksScreen$MoveVerb(this, true);
   private boolean _navigationClicked;
   private MenuDelayThread _menuDelayThread;
   private RIMModel _bookmarkToMove;
   private Folder _folderBeforeMove;
   private int _positionBeforeMove = -1;
   private BookmarksScreen$BookmarksTreeField _bookmarksTreeField;
   private BookmarksScreen$MyIconCollection _customIcons = new BookmarksScreen$MyIconCollection();
   private ToIntHashtable _urlsToIcons = (ToIntHashtable)(new Object());
   private int _state = 100;
   private int _selectedNode;
   private Object _persistentObject;
   private static final long PROVISIONED_BOOKMARKS_HIERARCHY_ID;
   private static final long BOOKMARKS_TREE_STATE_STORE_ID;
   private static final int ICON_WIDTH;
   private static final int ICON_HEIGHT;
   private static final int GO_TO_PRIORITY;
   private static final int HISTORY_PRIORITY;
   private static final int ADD_BOOKMARK_PRIORITY;
   private static final int STARTUP;
   private static final int STARTUP_DONE;
   private static final int QUERY_START;
   private static final int QUERY_DONE;
   private static PersistentObject _store = PersistentStore.getPersistentObject(-3181414277122434333L);

   public final void cleanup() {
      Folder currentFolder = this.getCurrentFolder();
      if (currentFolder != null && currentFolder.getLUID() != BrowserFolders.BROWSER_CHANNELS_FOLDER_ID) {
         BookmarksFolderList.setDefaultFolderID(currentFolder.getLUID());
      }

      BrowserDaemonRegistry.removeBrowserConfigChangeListener(this);
      this._menuDelayThread.setBookmarksScreen(null);
   }

   public final Folder getCurrentFolder() {
      Folder folder = this.getFolderContainingFocusedItem();
      if (folder == null) {
         folder = BookmarksFolderList.BOOKMARKS_HIERARCHIES[0];
      }

      return folder;
   }

   public final void setIsInitialScreen(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean getIsInitialScreen() {
      return this._isInitialScreen;
   }

   public final void trackwheelHold() {
      RIMModel bookmarkOrChannel = this.getFocusedBookmarkOrChannel();
      if (bookmarkOrChannel != null) {
         Verb linkVerb = this.getFollowLinkVerb(bookmarkOrChannel);
         if (linkVerb != null) {
            BrowserDaemonRegistry.getInstance().invokeLater((Runnable)(new Object(null, linkVerb, null, false)));
         }
      }
   }

   @Override
   public final void browserEventOccured(int eventId, Object param) {
      switch (eventId) {
         case 199:
            break;
         case 200:
         default:
            Folder folderx = FolderHierarchies.getFolder(
               BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.makeLUID(((SyncObject)param).getUID())
            );
            if (folderx != null) {
               Folder parent = folderx.getParentFolder();
               if (parent != null) {
                  this.elementAdded(parent.getContainedItems(), folderx);
                  return;
               }
            }
            break;
         case 201:
            this.removeFolder(BrowserFolders.makeLUID(((SyncObject)param).getUID()));
            return;
         case 202:
            Folder folder = FolderHierarchies.getFolder(
               BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.makeLUID(((SyncObject)param).getUID())
            );
            if (folder != null) {
               this.elementUpdated(null, folder, folder);
            }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void cleanupScreen() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.cleanup();
         var3 = false;
      } finally {
         if (var3) {
            BrowserDaemonRegistry.getInstance().releaseBrowserLock();
         }
      }

      BrowserDaemonRegistry.getInstance().releaseBrowserLock();
   }

   @Override
   public final void browserConfigChanged() {
      UiApplication browserDaemon = BrowserDaemonRegistry.getInstance();
      browserDaemon.invokeLater(new BookmarksScreen$UpdateFolder());
   }

   @Override
   public final void browserConfigInvalid() {
   }

   private static final void updateHomePageBookmark(long folderID, BrowserConfigRecord config) {
      String homePageURL = config != null ? config.getHomePageWithOverride() : null;
      String configUID = config != null ? config.getUid() : null;
      int configType = config != null ? config.getPropertyAsInt(12) : BrowserConfigRecord.INVALID_VALUE;
      String transportCID = config != null ? config.getPropertyAsString(3) : null;
      Folder bookmarksFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, folderID);
      BookmarksFolderList.setFolderName(folderID, bookmarksFolder, config);
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList bookmarksContents = (ReadableList)bookmarksFolder.getContainedItems();
         int i = -1;
         PageModel homePage = null;
         boolean updateHomePage = true;
         if (bookmarksContents != null) {
            for (int var19 = bookmarksContents.size() - 1; var19 >= 0; var19--) {
               PageModel model = (PageModel)bookmarksContents.getAt(var19);
               if (model != null && model.isHomePage()) {
                  if (updateHomePage) {
                     ModelResult mr = model.getModelResult();
                     if (mr != null
                        && mr.getURL() != null
                        && mr.getURL().equals(homePageURL)
                        && StringUtilities.strEqualIgnoreCase(mr.getConfigUID(), configUID, 1701707776)
                        && StringUtilities.strEqualIgnoreCase(mr.getTransportCID(), transportCID, 1701707776)
                        && mr.getConfigType() == configType) {
                        updateHomePage = false;
                        continue;
                     }
                  }

                  homePage = model;
                  Bookmarks.deleteBookmark(model, bookmarksFolder);
               }
            }
         }

         if (homePageURL != null && updateHomePage) {
            int position = 0;
            int updateStart = 0;
            int updatePeriod = 0;
            byte updateFlags = 0;
            if (homePage != null) {
               position = (int)homePage.getTimeStamp();
               updateFlags = homePage.getUpdateFlags();
               updateStart = homePage.getUpdateStart();
               updatePeriod = homePage.getUpdatePeriod();
            }

            Bookmarks.addBookmarkToFolder(
               BrowserResources.getString(355), homePageURL, bookmarksFolder, true, position, null, updateFlags, updateStart, updatePeriod, config
            );
         }
      }
   }

   @Override
   protected final TreeField makeTreeField() {
      BookmarksScreen$BookmarksTreeField treeField = new BookmarksScreen$BookmarksTreeField(this, this, 0, super._field);
      treeField.setEmptyString(BrowserResources.getString(146), 4);
      treeField.setDefaultExpanded(false);
      this._bookmarksTreeField = treeField;
      return treeField;
   }

   @Override
   public final void clearTree() {
      String text = this.getSearchField().getText();
      if (text != null && text.length() == 0 && this._state == 102) {
         this.onQueryEnd();
      } else if (text != null && text.length() == 1 && this._state != 102) {
         this.onQueryStart();
      }

      super.clearTree();
   }

   private final void onQueryStart() {
      this.saveBookmarksTreeState();
      this._state = 102;
   }

   private final RIMModel getFocusedBookmarkOrChannel() {
      Object cookie = this.getFocusedItem();
      return (RIMModel)(!(cookie instanceof Object) ? null : cookie);
   }

   private final synchronized void enableMoveMode(boolean enabled) {
      this._bookmarkToMove = enabled ? this.getFocusedBookmarkOrChannel() : null;
      this._folderBeforeMove = enabled ? this.getCurrentFolder() : null;
      this._positionBeforeMove = enabled ? this._bookmarksTreeField.getCurrentBookmarkPositionInFolder() : -1;
      this._bookmarksTreeField.invalidateNode(this._bookmarksTreeField.getCurrentNode());
   }

   private final synchronized void completeBookmarkMove() {
      if (this._bookmarkToMove != null) {
         int newPosition = this._bookmarksTreeField.getCurrentBookmarkPositionInFolder();
         if (newPosition >= 0) {
            Folder currentFolder = this.getCurrentFolder();
            if (currentFolder == this._folderBeforeMove) {
               if (!(this._bookmarkToMove instanceof PageModel)) {
                  if (this._bookmarkToMove instanceof ChannelModel) {
                     ChannelModel channelToMove = (ChannelModel)this._bookmarkToMove;
                     int moveAmount = newPosition - this._positionBeforeMove;
                     if (moveAmount != 0) {
                        this.putMovedBookmarkBack();
                        Channels.moveChannel(channelToMove, moveAmount);
                     }
                  }
               } else {
                  PageModel bookmarkToMove = (PageModel)this._bookmarkToMove;
                  int moveAmount = newPosition - this._positionBeforeMove;
                  if (moveAmount != 0) {
                     this.putMovedBookmarkBack();
                     Bookmarks.moveBookmarkUpOrDown(bookmarkToMove, currentFolder, moveAmount);
                  }
               }
            } else {
               PageModel bookmarkToMove = (PageModel)this._bookmarkToMove;
               this.putMovedBookmarkBack();
               Bookmarks.deleteBookmark(bookmarkToMove, this._folderBeforeMove);
               Bookmarks.addBookmarkToFolder(bookmarkToMove, currentFolder, newPosition);
            }
         }
      }

      this.enableMoveMode(false);
   }

   private final int putMovedBookmarkBack() {
      this._bookmarksTreeField.deleteSubtree(this._bookmarksTreeField.getCurrentNode());
      return this.addNode(this._bookmarksTreeField, this._folderBeforeMove.getContainedItems(), this._bookmarkToMove);
   }

   private final synchronized void cancelBookmarkMove() {
      if (this._bookmarkToMove != null) {
         if (this.getCurrentFolder() == this._folderBeforeMove && this._bookmarksTreeField.getCurrentBookmarkPositionInFolder() == this._positionBeforeMove) {
            this.enableMoveMode(false);
            return;
         }

         int node = this.putMovedBookmarkBack();
         if (node > 0) {
            this._bookmarksTreeField.setCurrentNode(node);
         }
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      if ((status & 1) != 0
         && this._bookmarkToMove == null
         && this.getFocusedBookmarkOrChannel() != null
         && !(this.getCurrentFolder() instanceof ProvisionedBookmarksFolder)) {
         this.enableMoveMode(true);
      }

      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      label28: {
         boolean fromWheel = (status & 1073741824) != 0;
         if (fromWheel) {
            if (this._bookmarkToMove != null) {
               this.completeBookmarkMove();
               return true;
            }

            if ((status & 1) == 0) {
               RIMModel bookmarkOrChannel = this.getFocusedBookmarkOrChannel();
               if (bookmarkOrChannel != null) {
                  if (!(bookmarkOrChannel instanceof PageModel)) {
                     break label28;
                  }

                  if (((PageModel)bookmarkOrChannel).getModelResult() != null) {
                     break label28;
                  }
               }
            }
         }

         return super.navigationClick(status, time);
      }

      this._navigationClicked = true;
      this._menuDelayThread.trackwheelClicked();
      return true;
   }

   @Override
   public final boolean navigationUnclick(int status, int time) {
      boolean fromWheel = (status & 1073741824) != 0;
      if (fromWheel && this._navigationClicked) {
         this._navigationClicked = false;
         if (this._menuDelayThread.trackwheelUnclicked()) {
            this.onMenu(0);
         }

         return true;
      } else {
         return super.navigationUnclick(status, time);
      }
   }

   private final void onQueryEnd() {
      this._state = 103;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (!this._bookmarksTreeField.keyChar('\n', 0, 0) && !this.keyChar('\n', 0, 0)) {
               return false;
            }

            return true;
         default:
            return false;
      }
   }

   private final Verb getFollowLinkVerb(RIMModel bookmarkOrChannel) {
      if (!(bookmarkOrChannel instanceof PageModel)) {
         if (!(bookmarkOrChannel instanceof ChannelModel)) {
            return null;
         }

         ChannelModel channel = (ChannelModel)bookmarkOrChannel;
         return new BookmarksScreen$DoAndCloseVerb(this, new OpenChannelVerb(channel, 309, 1315152), this, bookmarkOrChannel);
      } else {
         PageModel bookmark = (PageModel)bookmarkOrChannel;
         ModelResult mr = bookmark.getModelResult();
         if (mr != null) {
            CacheResult cacheResult = mr.getCacheResult();
            if (cacheResult != null) {
               RawDataCache cache = BrowserDaemonRegistry.getInstance().getRawDataCache();
               CacheNode cacheNode = cache.getCacheNode(cacheResult.getURLWithoutFragment());
               if (cacheNode == null || !cacheNode.getAvailableOffline()) {
                  cache.put(cacheResult.getURLWithoutFragment(), cacheNode != null ? cacheNode.getContents() : cacheResult, true, true);
               }
            }
         }

         return new BookmarksScreen$DoAndCloseVerb(
            this, new FollowLinkVerb(bookmark.getUrl(), bookmark.isHomePage(), bookmark.getTitle()), this, bookmarkOrChannel
         );
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      menu.add(new GoToVerb(this), 10000);
      Folder currentFolder = this.getCurrentFolder();
      if (!(currentFolder instanceof ProvisionedBookmarksFolder) && currentFolder.getLUID() != BrowserFolders.BROWSER_CHANNELS_FOLDER_ID) {
         BrowserVerb verb = BrowserDaemonRegistry.getInstance().getBrowserVerbRepository().getVerbNoCheck(0);
         if (verb != null) {
            ContextObject context = (ContextObject)(new Object());
            context.put(-1219344331000926502L, this.getCurrentFolder());
            menu.add((MenuItem)(new Object(null, verb.getOrdering(), 10033, verb, context)));
         }
      }

      Object focusedItem = this.getFocusedItem();
      if (!(focusedItem instanceof PageModel)) {
         if (!(focusedItem instanceof Object)) {
            if (focusedItem instanceof ChannelModel) {
               ChannelModel focusedChannel = (ChannelModel)focusedItem;
               Verb followLink = this.getFollowLinkVerb(focusedChannel);
               if (followLink != null) {
                  menu.add(followLink, 0);
               }

               menu.add(new OpenChannelVerb(focusedChannel, 313, 1315584));
               menu.add(new DeleteChannelVerb(focusedChannel));
               if (this._bookmarksTreeField.currentBookmarkHasSibling()) {
                  menu.add(new BookmarksScreen$MoveVerb(this, false));
               }
            }
         } else {
            Folder focusedFolder = (Folder)focusedItem;
            this.getContext().put(-1219344331000926502L, focusedFolder);
            long folderId = focusedFolder.getLUID();
            if (folderId != BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID
               && folderId != BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
               && !(focusedFolder instanceof ProvisionedBookmarksFolder)) {
               if (folderId == BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID) {
                  menu.add(this._createFolderVerb);
               } else if (folderId != BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID && folderId != BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID) {
                  menu.add(this._deleteFolderVerb);
                  menu.add(this._createFolderVerb);
                  menu.add(this._renameFolderVerb);
               } else {
                  menu.add(this._createFolderVerb);
               }
            }
         }
      } else {
         PageModel focusedBookmark = (PageModel)focusedItem;
         Verb followLinkVerb = this.getFollowLinkVerb(focusedBookmark);
         if (followLinkVerb != null) {
            menu.add(followLinkVerb, 0);
            if (!(currentFolder instanceof ProvisionedBookmarksFolder)) {
               menu.add(new EditBookmarkVerb(focusedBookmark, currentFolder, 149, 1312000));
            }

            menu.add(new CopyBookmarkURLVerb(focusedBookmark.getUrl()));
         }

         if (!(currentFolder instanceof ProvisionedBookmarksFolder)) {
            if (focusedBookmark.isHomePage()) {
               if (this._bookmarksTreeField.currentBookmarkHasSibling()) {
                  menu.add(this._moveBookmarkVerb);
               }
            } else {
               menu.add(new EditBookmarkVerb(focusedBookmark, currentFolder, 145, 1312080));
               menu.add(this._moveBookmarkVerb);
            }
         }

         menu.add(new SendBookmarkVerb(focusedBookmark));
      }

      if (BrowserDaemonRegistry.getInstance().getOfflineQueue().numQueues() > 0) {
         menu.add(new BookmarksScreen$DoAndCloseVerb(this, new PageVerb(1180688, BrowserResources.getResourceBundle(), 661), this, null));
      }

      menu.add(new OptionsVerb(false));
      BrowserSession session = BrowserSession.getCurrentSession();
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (session != null) {
         History history = session.getHistory();
         if (history.getSize() != 0 && history.lookupCurrentNodeId() == -1) {
            BrowserVerb verb = browser.getBrowserVerbRepository().getVerb(5, browser.getVerbMask());
            if (verb != null) {
               menu.add(new BookmarksScreen$DoAndCloseVerb(this, verb, this, null));
            }
         }
      }

      LongTermHistory longTermHistory = LongTermHistory.getInstance();
      if (longTermHistory.getNumberOfElements() != 0) {
         BrowserVerb verb = browser.getBrowserVerbRepository().getVerb(17, browser.getVerbMask());
         if (verb != null) {
            menu.add(new BookmarksScreen$1(this, verb.toString(), verb.getOrdering(), 10001));
         }
      }

      menu.add(this._browserHideVerb);
      menu.add(this._bookmarksScreenCloseVerb);
   }

   private static final boolean switchConfigs(String newConfigUID) {
      if (newConfigUID != null && newConfigUID.length() > 0) {
         ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(newConfigUID, BrowserConfigRecord.SERVICE_CID);
         if (sr != null && BrowserConfigRecord.getDecodedConfig(sr).getPropertyAsInt(7) == 0) {
            BrowserDaemonRegistry.getInstance().activateConfig(newConfigUID, true);
            return true;
         }
      }

      return false;
   }

   private final void setConfigFromModelOrCurrentFolder(RIMModel model) {
      setConfigFromModelOrFolder(model, this.getFolderContainingFocusedItem());
   }

   public static final boolean setConfigFromModelOrFolder(RIMModel model, Folder modelFolder) {
      String currentConfigUID = null;
      BrowserSession currentSession = BrowserSession.getCurrentSession();
      if (currentSession != null) {
         currentConfigUID = currentSession.getConfig().getUid();
      }

      Folder folder = modelFolder;

      while (folder != null) {
         long folderLUID = folder.getLUID();
         if (folderLUID != BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID && folderLUID != BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID) {
            if (folderLUID == BrowserFolders.BROWSER_CHANNELS_FOLDER_ID) {
               if (model instanceof ChannelModel) {
                  ChannelModel channel = (ChannelModel)model;
                  BrowserConfigRecord channelConfig = BrowserConfigRecord.getDecodedConfig(
                     channel.getConfigUID(), channel.getConfigType(), channel.getTransportCID()
                  );
                  if (channelConfig != null) {
                     String channelConfigUID = StackManager.getInstance().getRoutableService(channelConfig.getUid(), channelConfig);
                     if (!StringUtilities.strEqualIgnoreCase(channelConfigUID, currentConfigUID, 1701707776)) {
                        BrowserDaemonRegistry.getInstance().activateConfig(channelConfigUID, true);
                     }

                     return true;
                  }
               }
            } else {
               if (folderLUID == BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID) {
                  return false;
               }

               if (!(folder instanceof ProvisionedBookmarksFolder)) {
                  folder = folder.getParentFolder();
                  continue;
               }

               ProvisionedBookmarksFolder provisionedFolder = (ProvisionedBookmarksFolder)folder;
               String configUID = provisionedFolder.getConfigUID();
               if (configUID != null && configUID.length() > 0) {
                  if (!"S TCP-WBC".equalsIgnoreCase(configUID)) {
                     configUID = StackManager.getInstance().getRoutableService(configUID, null);
                  }

                  if (!StringUtilities.strEqualIgnoreCase(configUID, currentConfigUID, 1701707776)) {
                     BrowserDaemonRegistry.getInstance().activateConfig(configUID, true);
                  }

                  return true;
               }
            }
            break;
         }

         String initialConfigUID = BrowserDaemonRegistry.getInstance().getInitialConfigUID();
         String bookmarkConfigUID = initialConfigUID;
         BrowserConfigRecord bookmarkConfig = null;
         if (model instanceof PageModel) {
            PageModel bookmark = (PageModel)model;
            ModelResult modelResult = bookmark.getModelResult();
            if (modelResult.getConfigUID() != null) {
               bookmarkConfig = BrowserConfigRecord.getDecodedConfig(modelResult.getConfigUID(), modelResult.getConfigType(), modelResult.getTransportCID());
            }

            if (bookmarkConfig != null) {
               bookmarkConfigUID = bookmarkConfig.getUid();
            } else {
               BrowserConfigRecord initialConfig = BrowserConfigRecord.getDecodedConfig(initialConfigUID, BrowserConfigRecord.INVALID_VALUE, null);
               if (initialConfig != null) {
                  bookmarkConfigUID = BookmarksFolderList.getConfigUIDForFolder(initialConfig, folder);
                  bookmarkConfig = BrowserConfigRecord.getDecodedConfig(bookmarkConfigUID, BrowserConfigRecord.INVALID_VALUE, null);
                  if (bookmarkConfig != null) {
                     modelResult.setConfigUID(bookmarkConfig.getUid());
                     modelResult.setConfigType(bookmarkConfig.getPropertyAsInt(12));
                     modelResult.setTransportCID(bookmarkConfig.getPropertyAsString(3));
                     synchronized (FolderHierarchies.getLockObject()) {
                        WritableSet bookmarkItems = (WritableSet)modelFolder.getContainedItems();
                        bookmarkItems.remove(model);
                        bookmarkItems.add(model);
                        BrowserDaemonRegistry.broadCastEvent(102, model);
                     }
                  }
               }
            }

            if (bookmark.isHomePage()) {
               label141:
               try {
                  String url = bookmark.getUrl();
                  String host = DomainUtilities.parseAuthority((URI)(new Object(url)));
                  if (DomainUtilities.isHostInDomain(host, "mobile.blackberry.com")) {
                     String overrideUid = DomainOverrides.getInstance()
                        .getOverride(url, bookmarkConfig != null ? bookmarkConfig.getPropertyAsInt(12) : BrowserConfigRecord.INVALID_VALUE);
                     if (overrideUid != null) {
                        bookmarkConfigUID = overrideUid;
                        bookmarkConfig = null;
                     }
                  }
               } finally {
                  break label141;
               }
            }
         }

         bookmarkConfigUID = StackManager.getInstance().getRoutableService(bookmarkConfigUID, bookmarkConfig);
         if (!StringUtilities.strEqualIgnoreCase(bookmarkConfigUID, currentConfigUID, 1701707776)) {
            return switchConfigs(bookmarkConfigUID);
         }

         return true;
      }

      return false;
   }

   public BookmarksScreen(long startFolderID) {
      super(null, BrowserResources.getString(102), false, true, BookmarksOrderHelper.getInstance(), 0);
      this.setDefaultClose(false);
      this.setHelp("browser");
      Folder[] hierarchies = null;
      int numHierarchies = 1;
      int hierarchyIndex = 0;
      if (Channels.getChannelCount() > 0) {
         numHierarchies++;
      }

      Folder startFolder = null;
      ProvisionedBookmarksFolder provisionedBookmarksHierarchy = null;
      ProvisionedBookmarksFolder[] provisionedBookmarksFolders = null;
      BrowserConfigRecord[] records = BrowserConfigRecord.getValidBrowserConfigRecords();
      boolean firstProvisionedBookmarksAlwaysShown = true;

      for (int i = records.length - 1; i >= 0; i--) {
         BrowserConfigRecord config = records[i];
         SortedCollection provisionedBookmarks = config.getProvisionedBookmarks();
         if (provisionedBookmarks != null && (config.getPropertyAsInt(32) != 2 || config.getUid().equals(getCurrentConfigUID()))) {
            if (provisionedBookmarksFolders == null) {
               provisionedBookmarksHierarchy = new ProvisionedBookmarksFolder(-7037877772142231911L, null);
               provisionedBookmarksFolders = new ProvisionedBookmarksFolder[1];
               provisionedBookmarksHierarchy.setSubfolders(provisionedBookmarksFolders);
               firstProvisionedBookmarksAlwaysShown = config.getPropertyAsInt(32) != 1;
            } else {
               if (config.getPropertyAsInt(32) == 1) {
                  continue;
               }

               if (firstProvisionedBookmarksAlwaysShown) {
                  Array.resize(provisionedBookmarksFolders, provisionedBookmarksFolders.length + 1);
               } else {
                  firstProvisionedBookmarksAlwaysShown = true;
               }
            }

            String configUID = config.getUid();
            long folderLUID = BookmarksFolderList.getProvisionedFolderLUID(configUID);
            ProvisionedBookmarksFolder provisionedBookmarksFolder = new ProvisionedBookmarksFolder(folderLUID, provisionedBookmarksHierarchy);
            provisionedBookmarksFolder.setBookmarks(provisionedBookmarks);
            provisionedBookmarksFolder.setConfigUID(configUID);
            int expandByDefault = config.getPropertyAsInt(57);
            provisionedBookmarksFolder.setExpandByDefault(expandByDefault == 0 || expandByDefault == 2 && configUID.equals(getCurrentConfigUID()));
            if (folderLUID == startFolderID) {
               startFolder = provisionedBookmarksFolder;
            }

            String provisionedBookmarksFolderName = config.getLocalizedString(24);
            if (provisionedBookmarksFolderName == null) {
               provisionedBookmarksFolderName = BrowserResources.getString(652);
            }

            provisionedBookmarksFolder.setFriendlyName(provisionedBookmarksFolderName);
            provisionedBookmarksFolders[provisionedBookmarksFolders.length - 1] = provisionedBookmarksFolder;
         }
      }

      if (provisionedBookmarksHierarchy != null) {
         hierarchies = new Object[++numHierarchies];
         hierarchies[hierarchyIndex++] = provisionedBookmarksHierarchy;
      } else {
         hierarchies = new Object[numHierarchies];
      }

      hierarchies[hierarchyIndex++] = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID);
      if (hierarchyIndex < hierarchies.length) {
         Folder channelsFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID);
         if (channelsFolder != null) {
            hierarchies[hierarchyIndex] = channelsFolder.getParentFolder();
         }
      }

      if (startFolder == null) {
         startFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, startFolderID);
         if (startFolder == null) {
            startFolder = BookmarksFolderList.getDefaultFolderForSessionConfig();
         }
      }

      this.setStartFolder(startFolder);
      this.setHierarchies(hierarchies, true);
      this._deleteFolderVerb = new FolderVerb(169, 16864272);
      this._createFolderVerb = new FolderVerb(168, 16864261);
      this._renameFolderVerb = new FolderVerb(170, 16864277);
      updateHomePageBookmarks();
      this._bookmarksScreenCloseVerb = new BookmarksScreenCloseVerb(this);
      BrowserDaemonRegistry.addBrowserConfigChangeListener(this);
      this._menuDelayThread = BrowserDaemonRegistry.getInstance().getMenuDelayThread();
      this.setItemIcon(BrowserIcons.getIcons(), 3);
   }

   @Override
   public final void run() {
      this._menuDelayThread.setBookmarksScreen(this);
      BrowserDaemonRegistry.addFolderEventListener(this);
      synchronized (Application.getEventLock()) {
         super.run();
      }

      BrowserDaemonRegistry.removeFolderEventListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this.cleanup();
         synchronized (Application.getEventLock()) {
            if (this.isDisplayed()) {
               UiApplication.getUiApplication().popScreen(this);
            }

            BrowserDaemonRegistry.getInstance().pushSplashScreenIfNeeded();
            var6 = false;
         }
      } finally {
         if (var6) {
            BrowserDaemonRegistry.getInstance().releaseBrowserLock();
         }
      }

      BrowserDaemonRegistry.getInstance().releaseBrowserLock();
   }

   @Override
   protected final void cancel() {
      BrowserDaemonRegistry.getInstance().invokeLater(new BookmarksScreen$2(this));
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) == 0 && this._bookmarkToMove != null) {
         this.completeBookmarkMove();
         return true;
      } else {
         return super.keyStatus(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (this._bookmarkToMove != null) {
         if (key == '\n') {
            this.completeBookmarkMove();
            return true;
         } else if (key == 27) {
            this.cancelBookmarkMove();
            return true;
         } else {
            return false;
         }
      } else {
         if (key == '\n') {
            Object focusedItem = this.getFocusedItem();
            if (focusedItem instanceof Object) {
               Verb linkVerb = this.getFollowLinkVerb((RIMModel)focusedItem);
               if (linkVerb != null) {
                  linkVerb.invoke(null);
                  return true;
               }
            } else if (focusedItem instanceof Object) {
               return this._bookmarksTreeField.keyChar(' ', status, time);
            }
         } else if (key == 127 || key == '\b' && this.getQuery().length() == 0) {
            Object focusedItem = this.getFocusedItem();
            if (focusedItem instanceof PageModel) {
               PageModel focusedBookmark = (PageModel)focusedItem;
               Folder currentFolder = this.getCurrentFolder();
               if (!focusedBookmark.isHomePage() && !(currentFolder instanceof ProvisionedBookmarksFolder)) {
                  new EditBookmarkVerb(focusedBookmark, currentFolder, 145, 1312080).invoke(null);
               }

               return true;
            }

            if (focusedItem instanceof ChannelModel) {
               ChannelModel focusedChannel = (ChannelModel)focusedItem;
               new DeleteChannelVerb(focusedChannel).invoke(null);
               return true;
            }

            if (focusedItem instanceof Object) {
               Folder focusedFolder = (Folder)focusedItem;
               long folderId = focusedFolder.getLUID();
               if (folderId != BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID
                  && folderId != BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID
                  && folderId != BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID
                  && folderId != BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID
                  && folderId != BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
                  && !(focusedFolder instanceof ProvisionedBookmarksFolder)) {
                  this._deleteFolderVerb.invoke(focusedFolder);
               }

               return true;
            }
         }

         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if ((key == 19 || key == 21) && RibbonManagerThread.browserOwnsConvenienceKey(key)) {
         this.keyChar('\n', 0, time);
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   public final void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) {
      Object cookie = treeField.getCookie(node);
      if (cookie instanceof ChannelModel && ((ChannelModel)cookie).getStatus() == 1) {
         Font oldFont = graphics.getFont();
         graphics.setFont(oldFont.derive(3));
         this.setItemIcon(BrowserIcons.getIcons(), 2);
         super.drawTreeItem(treeField, graphics, node, y, width, indent);
         this.setItemIcon(BrowserIcons.getIcons(), 3);
         graphics.setFont(oldFont);
      } else {
         if (!(cookie instanceof BrowserPageModel)) {
            super.drawTreeItem(treeField, graphics, node, y, width, indent);
         } else {
            BrowserPageModel model = (BrowserPageModel)cookie;
            int index = this.getIconIndex(model.getIconUrl());
            if (index != -1) {
               this._customIcons.setWidth(BrowserIcons.getIcons().getWidth(0, treeField.getRowHeight()));
               this.setItemIcon(this._customIcons, index);
               super.drawTreeItem(treeField, graphics, node, y, width, indent);
               this.setItemIcon(BrowserIcons.getIcons(), 3);
               return;
            }

            boolean isUnread = model.getStatus() == 3;
            Font oldFont = graphics.getFont();
            if (isUnread) {
               graphics.setFont(oldFont.derive(3));
               this.setItemIcon(BrowserIcons.getIcons(), 2);
            }

            super.drawTreeItem(treeField, graphics, node, y, width, indent);
            if (isUnread) {
               this.setItemIcon(BrowserIcons.getIcons(), 3);
               graphics.setFont(oldFont);
               return;
            }
         }
      }
   }

   private final int getIconIndex(String url) {
      if (url == null) {
         return -1;
      }

      int index = this._urlsToIcons.get(url);
      if (index == -1) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         RawDataCache cache = browser.getRawDataCache();
         CacheNode cacheNode = cache.getLongTermCacheNode(url);
         if (cacheNode == null) {
            cacheNode = cache.getShortTermCacheNode(url);
            if (cacheNode != null) {
               cache.moveNodeToPeristentCache(cacheNode);
            }
         }

         if (cacheNode != null) {
            CacheResult result = cacheNode.getContents();
            if (result != null) {
               byte[] data = result.getDataAsArray();
               EncodedImage icon = ImageConverter.convertAndScale(data, 0, data.length, null, 16, 16, Display.getWidth());
               if (icon != null) {
                  this._customIcons.addImage(icon);
                  index = this._urlsToIcons.size();
                  this._urlsToIcons.put(url, index);
               }
            }
         }
      }

      return index;
   }

   @Override
   public final String getLabel(Object cookie) {
      try {
         return super.getLabel(cookie);
      } finally {
         ;
      }
   }

   @Override
   public final int getWidth(TreeField treeField, int node) {
      Object cookie = treeField.getCookie(node);
      if (cookie == this._bookmarkToMove) {
         return Display.getWidth();
      }

      if ((!(cookie instanceof ChannelModel) || ((ChannelModel)cookie).getStatus() != 1)
         && (!(cookie instanceof PageModel) || ((PageModel)cookie).getStatus() != 3)) {
         return super.getWidth(treeField, node);
      }

      Font font = treeField.getFont();
      String label = cookie.toString();
      this.setItemIcon(BrowserIcons.getIcons(), 2);
      int width = super.getWidth(treeField, node) + (font.derive(3).getBounds(label) - font.getBounds(label));
      this.setItemIcon(BrowserIcons.getIcons(), 3);
      return width;
   }

   private static final String getCurrentConfigUID() {
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         BrowserConfigRecord currentConfig = session.getConfig();
         if (currentConfig != null) {
            return currentConfig.getUid();
         }
      }

      return null;
   }

   static final void updateHomePageBookmarks() {
      BrowserConfigRecord defaultConfig = null;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         defaultConfig = session.getConfig();
      }

      long defaultFolderID = BookmarksFolderList.getDefaultFolderIDForConfig(defaultConfig);
      updateHomePageBookmark(defaultFolderID, defaultConfig);
      long otherFolderID = defaultFolderID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID
         ? BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID
         : BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID;
      String configUid = otherFolderID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID
         ? GeneralProperty.getDefaultWapBrowserConfigServiceUID()
         : GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
      boolean otherFolderUpdated = false;
      if (configUid != null && configUid.length() > 0) {
         ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(configUid, BrowserConfigRecord.SERVICE_CID);
         if (sr != null) {
            otherFolderUpdated = true;
            updateHomePageBookmark(otherFolderID, BrowserConfigRecord.getDecodedConfig(sr));
         }
      }

      if (!otherFolderUpdated) {
         BookmarksFolderList.setFolderName(otherFolderID, null);
      }
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached) {
         if (this._state == 102) {
            return;
         }

         this.saveBookmarksTreeState();
      }
   }

   private final void saveBookmarksTreeState() {
      BookmarksTreeState state = this.getBookmarksTreeState();
      if (state != null) {
         this.savePersistentObject(state);
      }
   }

   private final void savePersistentObject(Object o) {
      synchronized (_store) {
         _store.setContents(o, 51);
         _store.commit();
      }

      this._persistentObject = o;
   }

   private final Object retrievePersistentObject() {
      if (this._persistentObject != null) {
         return this._persistentObject;
      }

      synchronized (_store) {
         this._persistentObject = _store.getContents();
      }

      return this._persistentObject;
   }

   private final BookmarksTreeState getBookmarksTreeState() {
      TreeField tree = this._bookmarksTreeField;
      long[] collapsedFolders = this.getCollapsedFolders(tree);
      int node = tree.getCurrentNode();
      if (node == -1) {
         return null;
      }

      Object o = tree.getCookie(node);
      if (!(o instanceof Object)) {
         if (!(o instanceof ProvisionedBookmarksFolder)) {
            return null;
         }

         DummyPersistable sf = new DummyPersistable(((Folder)o).getLUID());
         o = sf;
      }

      return new BookmarksTreeState(collapsedFolders, (Persistable)o);
   }

   private final long[] getCollapsedFolders(TreeField tree) {
      int lGROW_BY = 10;
      long[] collapsedFolders = new long[10];
      int i = 0;

      for (int current = tree.getFirstRoot(); current > 0; current = tree.nextNode(current, 0, true)) {
         if (i == collapsedFolders.length - 1) {
            Array.resize(collapsedFolders, collapsedFolders.length + lGROW_BY);
         }

         Object nodeCookie = tree.getCookie(current);
         if (nodeCookie instanceof Object && !tree.getExpanded(current)) {
            Folder f = (Folder)nodeCookie;
            collapsedFolders[i] = f.getLUID();
         }

         i++;
      }

      if (i < collapsedFolders.length) {
         Array.resize(collapsedFolders, i);
      }

      return collapsedFolders;
   }

   @Override
   protected final void updateFocusWithoutQuery() {
      if (this._state == 100) {
         this.onStartup();
      } else if (this._selectedNode > 0) {
         this._bookmarksTreeField.setCurrentNode(this._selectedNode);
      }
   }

   private final void onStartup() {
      Object obj = this.retrievePersistentObject();
      if (!(obj instanceof BookmarksTreeState)) {
         super.updateFocusWithoutQuery();
      }

      if (this._selectedNode > 0) {
         TreeField tree = this._bookmarksTreeField;
         if (tree.getCookie(this._selectedNode) instanceof Object) {
            if (!tree.getExpanded(this._selectedNode)) {
               tree.setCurrentNode(this._selectedNode);
               this._state = 101;
               return;
            }

            int nextNode = tree.getFirstChild(this._selectedNode);
            if (nextNode <= 0) {
               tree.setCurrentNode(this._selectedNode);
               this._state = 101;
               return;
            }

            do {
               if (!(tree.getCookie(nextNode) instanceof Object)) {
                  tree.setCurrentNode(nextNode);
                  this._state = 101;
                  return;
               }

               nextNode = tree.getNextSibling(nextNode);
            } while (nextNode > 0);
         }

         this._bookmarksTreeField.setCurrentNode(this._selectedNode);
      }

      this._state = 101;
   }

   static final String access$1000(BookmarksScreen x0) {
      return x0.getQuery();
   }
}
