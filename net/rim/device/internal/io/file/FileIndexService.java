package net.rim.device.internal.io.file;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class FileIndexService {
   private WeakReference[] _metaDatalisteners = new Object[0];
   private FilteredListenerRec[] _filteredFileListenerList = new FilteredListenerRec[0];
   private FilteredListenerRec[] _filteredFolderListenerList = new FilteredListenerRec[0];
   private MetaDataFileInfo _info = new MetaDataFileInfo();
   private int _fileMediaTypeFilterMask;
   private int _folderMediaTypeFilterMask;
   private static final long GUID = 4014511989342665574L;
   protected static final int METADATAFILE_UNAVAILABLE = 0;
   protected static final int METADATA_DELETED = 1;
   protected static final int METADATA_ADDED = 2;
   protected static final int NON_METADATA_FILE_DELETED = 4;
   protected static final int NON_METADATA_FILE_ADDED = 5;
   protected static final int FOLDER_DELETED = 6;
   protected static final int FOLDER_ADDED = 7;
   protected static final int FLASH = 0;
   protected static final int SDCARD = 1;
   private static final int TOTAL_FS_MAP_INDICIES = 2;
   private static final int MAX_MEDIA_TYPE = 7;
   private static final int[] _MediaTypeMap = new int[8];
   private static final String[][] _DefaultMediaFolders = new Object[2][];
   private static final int RINGTONE;
   private static String SDCARD_HOME_STR = "/SDCard/";
   private static String FLASH_HOME_STR = "/store/home/user/";
   private static final int RINGTONE_SIZE_THRESHOLD = 614400;

   private static int initMediaFolderMapping(int mapIndex, int mediaType, String flashFolder, String SDCardFolder) {
      _MediaTypeMap[mediaType] = mapIndex;
      addToArray(_DefaultMediaFolders[0], mapIndex, flashFolder);
      addToArray(_DefaultMediaFolders[1], mapIndex, SDCardFolder);
      return mapIndex + 1;
   }

   private static void addToArray(String[] array, int index, String value) {
      if (array.length == index) {
         Array.extend(array, 1);
      }

      array[index] = value;
   }

   protected FileIndexService() {
      EventLogger.register(4014511989342665574L, "FileIndexService", 2);
   }

   public static FileIndexService getService() {
      try {
         return (FileIndexService)ApplicationRegistry.getApplicationRegistry().waitFor(4014511989342665574L);
      } finally {
         ;
      }
   }

   public static boolean isPrecreatedMediaFolder(String folder) {
      FileIndexService service = getService();
      return service != null ? service.isPrecreatedFolder(folder) : false;
   }

   public static final String getCurrentMediaFolder(int mediaType, int fileSize) {
      if (0 > mediaType || mediaType > 7) {
         mediaType = 0;
      }

      int mediaIndex = getMediaIndexFromMediaType(mediaType, fileSize);
      if (FileUtilities.isSDCardMounted()) {
         String folder = _DefaultMediaFolders[1][mediaIndex];
         if (folder != null && mediaIndex != 0) {
            FileIndexService service = getService();
            if (service != null) {
               try {
                  if (service.mediaFolderExists(folder)) {
                     return folder;
                  }
               } finally {
                  ;
               }
            }
         }

         return SDCARD_HOME_STR;
      } else {
         String folder = _DefaultMediaFolders[0][mediaIndex];
         return folder != null ? folder : FLASH_HOME_STR;
      }
   }

   public static String getDefaultMediaFolderForFileSystem(int mediaType, int fileSystemType, int fileSize) {
      int fsIndex = fileSystemType == 1 ? 1 : 0;
      int mediaIndex = getMediaIndexFromMediaType(mediaType, fileSize);
      String folder = _DefaultMediaFolders[fsIndex][mediaIndex];
      if (folder == null) {
         folder = fsIndex == 1 ? SDCARD_HOME_STR : FLASH_HOME_STR;
      }

      return folder;
   }

   private static int getMediaIndexFromMediaType(int mediaType, int fileSize) {
      return mediaType == 2 && fileSize <= 614400 ? RINGTONE : _MediaTypeMap[mediaType];
   }

   protected boolean mediaFolderExists(String _1) {
      throw null;
   }

   protected boolean isPrecreatedFolder(String _1) {
      throw null;
   }

   protected static void setService(FileIndexService service) {
      ApplicationRegistry.getApplicationRegistry().put(4014511989342665574L, service);
   }

   public void addMetaDataListener(MetaDataListener listener) {
      if (listener == null) {
         throw new Object();
      }

      synchronized (this._metaDatalisteners) {
         int i = this._metaDatalisteners.length;

         while (--i >= 0) {
            Object item = this._metaDatalisteners[i].get();
            if (item == listener) {
               return;
            }

            if (item == null) {
               Arrays.removeAt(this._metaDatalisteners, i);
            }
         }

         Arrays.add(this._metaDatalisteners, new Object(listener));
      }
   }

   public void addFilteredFileListener(FilteredFileListener listener, int mediaType) {
      synchronized (this._filteredFileListenerList) {
         this._fileMediaTypeFilterMask = this.addFilteredListener(listener, mediaType, this._filteredFileListenerList, this._fileMediaTypeFilterMask);
      }
   }

   public void addFilteredFolderListener(FilteredFolderListener listener, int mediaType) {
      synchronized (this._filteredFolderListenerList) {
         this._folderMediaTypeFilterMask = this.addFilteredListener(listener, mediaType, this._filteredFolderListenerList, this._folderMediaTypeFilterMask);
      }
   }

   private int addFilteredListener(Object listener, int mediaType, FilteredListenerRec[] list, int listMediaTypeMask) {
      if (listener == null) {
         throw new Object();
      }

      int mediaTypeMask = mediaType == 0 ? -1 : 1 << mediaType;
      int i = list.length;

      while (--i >= 0) {
         FilteredListenerRec r = list[i];
         Object item = r.getListener();
         if (item == listener) {
            r.setMediaTypeMask(mediaTypeMask);
            return this.recalculateMediaTypeFilterMask(list);
         }
      }

      FilteredListenerRec r = new FilteredListenerRec(listener);
      r.setMediaTypeMask(mediaTypeMask);
      Arrays.add(list, r);
      return listMediaTypeMask | mediaTypeMask;
   }

   protected void onFileDeleted(String pathURL, String fileName) {
      MetaDataFile db = MetaDataFile.getIfOpen(pathURL);
      if (db != null) {
         db.removeEntry(fileName);
      }

      this.notifyMetaDataListeners(1, pathURL, fileName, null);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void notifyMetaDataListeners(int notificationType, String pathURL, String fileName, Object[] metaDataObjects) {
      synchronized (this._metaDatalisteners) {
         int i = this._metaDatalisteners.length;
         String fullPathURL = null;
         if (i > 0) {
            switch (notificationType) {
               case 2:
                  this._info.setPath(pathURL);
                  this._info.setFileName(fileName);
                  this._info.setMetaData(metaDataObjects);
            }
         }

         while (--i >= 0) {
            MetaDataListener listener = (MetaDataListener)this._metaDatalisteners[i].get();
            if (listener == null) {
               Arrays.removeAt(this._metaDatalisteners, i);
            } else {
               try {
                  switch (notificationType) {
                     case -1:
                        break;
                     case 0:
                     default:
                        listener.metaDataFileUnavailable(pathURL);
                        break;
                     case 1:
                        if (fullPathURL == null) {
                           fullPathURL = ((StringBuffer)(new Object())).append(pathURL).append(fileName).toString();
                        }

                        listener.metaDataDeleted(fullPathURL);
                        break;
                     case 2:
                        listener.metaDataAdded(this._info);
                  }
               } catch (Throwable var13) {
                  EventLogger.logEvent(4014511989342665574L, ((StringBuffer)(new Object("listener threw "))).append(t).toString().getBytes(), 2);
                  Arrays.removeAt(this._metaDatalisteners, i);
                  continue;
               }
            }
         }
      }
   }

   protected void notifyFilteredFileListeners(int notificationType, int mediaType, String pathURL) {
      synchronized (this._filteredFileListenerList) {
         this._fileMediaTypeFilterMask = this.notifyFilteredListeners(
            this._filteredFileListenerList, this._fileMediaTypeFilterMask, notificationType, mediaType, pathURL
         );
      }
   }

   protected void notifyFilteredFolderListeners(int notificationType, int mediaType, String pathURL) {
      synchronized (this._filteredFolderListenerList) {
         this._folderMediaTypeFilterMask = this.notifyFilteredListeners(
            this._filteredFolderListenerList, this._folderMediaTypeFilterMask, notificationType, mediaType, pathURL
         );
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int notifyFilteredListeners(FilteredListenerRec[] listeners, int listMediaTypeMask, int notificationType, int mediaType, String pathURL) {
      boolean recalcMaskNeeded = false;
      int i = listeners.length;

      while (--i >= 0) {
         FilteredListenerRec r = listeners[i];
         if (r.isListeningFor(mediaType)) {
            Object listener = r.getListener();
            if (listener == null) {
               Arrays.removeAt(listeners, i);
               recalcMaskNeeded = true;
            } else {
               try {
                  switch (notificationType) {
                     case 3:
                        break;
                     case 4:
                     default:
                        if (listener instanceof FilteredFileListener) {
                           ((FilteredFileListener)listener).fileDeleted(pathURL);
                        }
                        break;
                     case 5:
                        if (listener instanceof FilteredFileListener) {
                           ((FilteredFileListener)listener).fileAdded(pathURL);
                        }
                        break;
                     case 6:
                        if (listener instanceof FilteredFolderListener) {
                           ((FilteredFolderListener)listener).folderDeleted(pathURL);
                        }
                        break;
                     case 7:
                        if (listener instanceof FilteredFolderListener) {
                           ((FilteredFolderListener)listener).folderAdded(pathURL);
                        }
                  }
               } catch (Throwable var12) {
                  EventLogger.logEvent(4014511989342665574L, ((StringBuffer)(new Object("listener threw "))).append(t.toString()).toString().getBytes(), 2);
                  Arrays.removeAt(this._filteredFileListenerList, i);
                  recalcMaskNeeded = true;
                  continue;
               }
            }
         }
      }

      if (recalcMaskNeeded) {
         listMediaTypeMask = this.recalculateMediaTypeFilterMask(listeners);
      }

      return listMediaTypeMask;
   }

   public boolean haveFilteredFileListener(int mediaType) {
      return (1 << mediaType & this._fileMediaTypeFilterMask) != 0;
   }

   public boolean haveFilteredFolderListener(int mediaType) {
      return (1 << mediaType & this._folderMediaTypeFilterMask) != 0;
   }

   private int recalculateMediaTypeFilterMask(FilteredListenerRec[] list) {
      int newMask = 0;
      int i = list.length;

      while (--i >= 0) {
         newMask |= list[i].getMediaTypeMask();
      }

      return newMask;
   }

   public Enumeration metaDataFolders() {
      throw null;
   }

   public Enumeration metaDataFolders(int _1) {
      throw null;
   }

   public void requestScan() {
      throw null;
   }

   static {
      _DefaultMediaFolders[0] = new Object[6];
      _DefaultMediaFolders[1] = new Object[6];
      int i = 0;
      i = initMediaFolderMapping(i, 0, FLASH_HOME_STR, SDCARD_HOME_STR);
      int audio = i;
      i = initMediaFolderMapping(i, 2, "/store/home/user/music/", "/SDCard/BlackBerry/music/");
      i = initMediaFolderMapping(i, 1, "/store/home/user/pictures/", "/SDCard/BlackBerry/pictures/");
      i = initMediaFolderMapping(i, 3, "/store/home/user/videos/", "/SDCard/BlackBerry/videos/");
      i = initMediaFolderMapping(i, 7, _DefaultMediaFolders[0][audio], _DefaultMediaFolders[1][audio]);
      RINGTONE = i;
      i = initMediaFolderMapping(i, 7, "/store/home/user/ringtones/", "/SDCard/BlackBerry/ringtones/");
      Array.resize(_DefaultMediaFolders[0], i);
      Array.resize(_DefaultMediaFolders[1], i);
   }
}
