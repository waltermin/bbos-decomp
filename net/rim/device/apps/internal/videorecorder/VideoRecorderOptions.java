package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;

public final class VideoRecorderOptions extends OptionsBase {
   private VideoRecorderOptions$PersistedVideoOptions _persistedVideoOptions;
   private static final long VIDEORECORD_OPTIONS_SYNC_ITEM;
   private static final long PERSISTED_VIDEORECORD_OPTIONS;
   public static final int MEMORY_INTERNAL;
   public static final int MEMORY_MEDIACARD;
   public static final int VIDEO_WIDTH;
   public static final int VIDEO_HEIGHT;
   public static final int VIDEO_CODEC;
   public static final int VIDEO_FORMAT_STANDARD;
   public static final int VIDEO_FORMAT_MMS_MODE;
   public static final int VIDEO_FORMAT_DEFAULT_INDEX;
   private static int[][][] _videoFormatTable = new int[2][][];
   private static final int[] VIDEO_FORMAT_240x180 = new int[]{
      240, 180, 2, 527827200, 16810638, 1701539702, 1870004480, 1849779563, 56711012, 1870004480, 290219371, 262155
   };
   private static final int[] VIDEO_FORMAT_240x176 = new int[]{240, 176, 2, -804651005, 240, 180, 2, 527827200, 16810638, 1701539702, 1870004480, 1849779563};
   private static final int[] VIDEO_FORMAT_176x144 = new int[]{176, 144, 5, -804651005, 240, 176, 2, -804651005, 240, 180, 2, 527827200};
   public static final int FLASH_MODE_OFF_INDEX;
   public static final int FLASH_MODE_ON_INDEX;
   public static final int FLASH_MODE_AUTO_INDEX;
   private static final int FLASH_MODE_DEFAULT_INDEX;
   public static final int[] FLASH_MODE_TABLE = new int[]{0, 1, 2, -804651005, 0, 5, 6, -805044216, 33554432, 0, -805044216, 67108864};
   private static final int SUPPORTED_COLOUR_EFFECTS = Camera.getColourEffects();
   private static final int COLOUR_EFFECTS_DEFAULT_INDEX;
   private static final int[] COLOUR_EFFECTS_TABLE = new int[]{
      0, 5, 6, -805044216, 33554432, 0, -805044216, 67108864, 1953066601, -805044213, 775162112, 774909491
   };
   public static final int VIDEO_OPTION_FLASHMODE;
   public static final int VIDEO_OPTION_WHITEBALANCE;
   public static final int VIDEO_OPTION_FORMAT;
   public static final int VIDEO_MEMORY_TYPE;
   public static final int VIDEO_DESTINATION_FOLDER;
   public static final int VIDEO_COLOUR_EFFECTS;
   private static VideoRecorderOptions _options;

   private VideoRecorderOptions() {
      String device = DeviceInfo.getDeviceName();
      if (!device.equals("8130") && !device.equals("8330")) {
         _videoFormatTable[0] = (int[][])VIDEO_FORMAT_240x180;
         _videoFormatTable[1] = (int[][])VIDEO_FORMAT_176x144;
      } else {
         _videoFormatTable[0] = (int[][])VIDEO_FORMAT_240x176;
         _videoFormatTable[1] = (int[][])VIDEO_FORMAT_176x144;
      }
   }

   public static final VideoRecorderOptions getOptions() {
      if (_options == null) {
         _options = new VideoRecorderOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(825712288313247278L);
      synchronized (persistentObject) {
         this._persistedVideoOptions = (VideoRecorderOptions$PersistedVideoOptions)persistentObject.getContents();
         if (this._persistedVideoOptions == null) {
            this._persistedVideoOptions = new VideoRecorderOptions$PersistedVideoOptions();
            persistentObject.setContents(this._persistedVideoOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(9066959603476445837L);
         if (syncItem == null) {
            syncItem = new VideoRecorderOptions$VideoRecordOptionsSyncItem();
            ar.put(9066959603476445837L, syncItem);
         }

         return syncItem;
      }
   }

   private static final String getDefaultPath(int type) {
      return FileIndexService.getDefaultMediaFolderForFileSystem(3, type == 1 ? 1 : 3, -1);
   }

   public static final String getDefaultPath() {
      return getDefaultPath(1);
   }

   public final String getDestinationFolder() {
      return this._persistedVideoOptions._destinationFolder;
   }

   public final void setDestinationFolder(String folder) {
      boolean folderValid = FileUtilities.ensureDirectoryExists(FileUtilities.makeFileURL(folder));
      if (!folderValid) {
         folder = getDefaultPath();
      }

      if (!folder.equals(this._persistedVideoOptions._destinationFolder)) {
         this._persistedVideoOptions._destinationFolder = folder;
         this.fireOptionsChanged(5);
      }
   }

   public final int getVideoFormatIndex() {
      return this._persistedVideoOptions._videoFormatIndex;
   }

   public final int[] getVideoFormat() {
      return this.getVideoFormat(this.getVideoFormatIndex());
   }

   public final int[] getVideoFormat(int index) {
      return (int[])_videoFormatTable[index];
   }

   public final void setVideoFormatIndex(int index) {
      if (index < 0 || index >= _videoFormatTable.length) {
         index = 0;
      }

      if (index != this._persistedVideoOptions._videoFormatIndex) {
         this._persistedVideoOptions._videoFormatIndex = index;
         this.fireOptionsChanged(3);
      }
   }

   public final int getFlashModeIndex() {
      return this._persistedVideoOptions._flashModeIndex;
   }

   public final void setFlashModeIndex(int flashModeIndex) {
      if (flashModeIndex < 0 || flashModeIndex >= FLASH_MODE_TABLE.length) {
         flashModeIndex = 0;
      }

      if (flashModeIndex != this._persistedVideoOptions._flashModeIndex) {
         this._persistedVideoOptions._flashModeIndex = flashModeIndex;
         this.fireOptionsChanged(1);
      }
   }

   public final int getColourEffectIndex() {
      return this._persistedVideoOptions._colourEffectIndex;
   }

   public final int getColourEffect() {
      return this.getColourEffect(this.getColourEffectIndex());
   }

   public final int getColourEffect(int index) {
      return COLOUR_EFFECTS_TABLE[index];
   }

   public final void setColourEffectIndex(int index) {
      if (index < 0 || index >= COLOUR_EFFECTS_TABLE.length || (SUPPORTED_COLOUR_EFFECTS & 1 << COLOUR_EFFECTS_TABLE[index]) == 0) {
         index = 0;
      }

      if (index != this._persistedVideoOptions._colourEffectIndex) {
         this._persistedVideoOptions._colourEffectIndex = index;
         this.fireOptionsChanged(6);
      }
   }
}
