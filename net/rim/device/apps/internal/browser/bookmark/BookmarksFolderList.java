package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.store.FolderEventListener;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class BookmarksFolderList extends FolderList implements FolderEventListener {
   private Folder _selectedFolder;
   private BookmarksFolderList$SelectVerb _selectVerb = new BookmarksFolderList$SelectVerb(this);
   private Verb _deleteVerb;
   private Verb _createVerb;
   private Verb _renameVerb;
   private static final int PROVISIONED_BOOKMARKS_FOLDER_ID_SCOPE = -1578139142;
   static final Folder[] BOOKMARKS_HIERARCHIES = new Folder[]{
      FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID)
   };

   public final Folder getSelectedFolder() {
      return this._selectVerb.selectionMade() ? this._selectedFolder : null;
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

   @Override
   public final void run() {
      BrowserDaemonRegistry.addFolderEventListener(this);
      super.run();
      BrowserDaemonRegistry.removeFolderEventListener(this);
   }

   @Override
   public final String getLabel(Object cookie) {
      try {
         return super.getLabel(cookie);
      } finally {
         ;
      }
   }

   public BookmarksFolderList(Folder startFolder, String title) {
      super(startFolder, title, false, false, BookmarksOrderHelper.getInstance(), 0);
      this.setHierarchies(BOOKMARKS_HIERARCHIES, true);
      setFolderNames();
      this._deleteVerb = new FolderVerb(169, 16864272);
      this._createVerb = new FolderVerb(168, 16864261);
      this._renameVerb = new FolderVerb(170, 16864277);
   }

   public static final Folder getDefaultFolderForSessionConfig() {
      BrowserSession session = BrowserSession.getCurrentSession();
      Folder defaultFolder = FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, getDefaultFolderIDForConfig(session != null ? session.getConfig() : null)
      );
      if (defaultFolder == null) {
         defaultFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID);
      }

      return defaultFolder;
   }

   public static final long getDefaultFolderIDForConfig(BrowserConfigRecord config) {
      return config == null
            || config.getPropertyAsInt(12) != 7 && !StringUtilities.strEqualIgnoreCase(config.getPropertyAsString(3), WAPServiceRecord.SERVICE_CID, 1701707776)
         ? BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID
         : BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID;
   }

   public static final Folder getDefaultFolder() {
      Folder defaultFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, getDefaultFolderID());
      if (defaultFolder == null) {
         defaultFolder = getDefaultFolderForSessionConfig();
      }

      return defaultFolder;
   }

   public static final long getDefaultFolderID() {
      BrowserSession session = BrowserSession.getCurrentSession();
      return session != null ? session.getDefaultBookmarksFolderID() : BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID;
   }

   public static final void setDefaultFolderID(long defaultFolderID) {
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         session.setDefaultBookmarksFolderID(defaultFolderID);
      }
   }

   private static final boolean configIsUnconstrained(String configUID) {
      if (configUID != null) {
         ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(configUID, BrowserConfigRecord.SERVICE_CID);
         if (sr != null) {
            if (BrowserConfigRecord.getDecodedConfig(sr).getPropertyAsInt(7) == 0) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public static final String getConfigUIDForFolder(BrowserConfigRecord browserConfig, Folder folder) {
      String configUID = null;
      int configType = BrowserConfigRecord.INVALID_VALUE;
      if (browserConfig != null) {
         configUID = browserConfig.getUid();
         configType = browserConfig.getPropertyAsInt(12);
      }

      while (folder != null) {
         long folderLUID = folder.getLUID();
         if (folderLUID == BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID) {
            if (configType == 0 || configType == 7) {
               String mdsConfigUID = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
               if (configIsUnconstrained(mdsConfigUID)) {
                  return mdsConfigUID;
               }
            }
            break;
         }

         if (folderLUID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID) {
            if (configType != 0 && configType != 7) {
               String wapConfigUID = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
               if (configIsUnconstrained(wapConfigUID)) {
                  return wapConfigUID;
               }
            }
            break;
         }

         folder = folder.getParentFolder();
      }

      return configUID;
   }

   public static final void setFolderNames() {
      BrowserConfigRecord defaultConfig = null;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         defaultConfig = session.getConfig();
      }

      long defaultFolderID = BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID;
      if (defaultConfig == null) {
         String configUid = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
         if (configUid != null && configUid.length() > 0) {
            ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(configUid, BrowserConfigRecord.SERVICE_CID);
            if (sr != null) {
               defaultConfig = BrowserConfigRecord.getDecodedConfig(sr);
            }
         }
      } else {
         defaultFolderID = getDefaultFolderIDForConfig(defaultConfig);
      }

      setFolderName(defaultFolderID, defaultConfig);
      long otherFolderID = defaultFolderID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID
         ? BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID
         : BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID;
      String configUid = otherFolderID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID
         ? GeneralProperty.getDefaultWapBrowserConfigServiceUID()
         : GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
      BrowserConfigRecord otherConfig = null;
      if (configUid != null && configUid.length() > 0) {
         ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(configUid, BrowserConfigRecord.SERVICE_CID);
         if (sr != null) {
            otherConfig = BrowserConfigRecord.getDecodedConfig(sr);
         }
      }

      setFolderName(otherFolderID, otherConfig);
   }

   static final void setFolderName(long folderID, BrowserConfigRecord config) {
      setFolderName(folderID, FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, folderID), config);
   }

   static final void setFolderName(long folderID, Folder folder, BrowserConfigRecord config) {
      if (folder instanceof SimpleFolder) {
         String customFolderName = null;
         if (config != null) {
            customFolderName = config.getLocalizedString(42);
            if (folderID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID && customFolderName == null) {
               customFolderName = config.getLocalizedString(11);
            }
         }

         ((SimpleFolder)folder)
            .setFriendlyName(
               customFolderName != null ? customFolderName : BrowserResources.getString(folderID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID ? 570 : 568)
            );
      }
   }

   public static final long getProvisionedFolderLUID(String configUID) {
      return UIDGenerator.makeLUID(-1578139142, HashCodeCalculator.getCRC32(configUID.getBytes()));
   }

   @Override
   public final boolean onMenu(int instance) {
      this.updateActions(this.getFocusedFolder());
      return super.onMenu(instance);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      this.updateActions(this.getFocusedFolder());
      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.updateActions(this.getFocusedFolder());
      return super.navigationClick(status, time);
   }

   private final void updateActions(Folder focusedFolder) {
      this.setSelectVerb(null);
      this.setDeleteVerb(null);
      this.setCreateVerb(null);
      this.setRenameVerb(null);
      if (focusedFolder != null) {
         long folderId = focusedFolder.getLUID();
         if (folderId == BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID) {
            return;
         }

         if (folderId == BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID) {
            this.setSelectVerb(this._selectVerb);
            this.setCreateVerb(this._createVerb);
            return;
         }

         if (folderId == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID || folderId == BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID) {
            this.setSelectVerb(this._selectVerb);
            this.setCreateVerb(this._createVerb);
            return;
         }

         this.setSelectVerb(this._selectVerb);
         this.setDeleteVerb(this._deleteVerb);
         this.setCreateVerb(this._createVerb);
         this.setRenameVerb(this._renameVerb);
      }
   }
}
