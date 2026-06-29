package net.rim.device.apps.internal.camera;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;

public final class CameraOptions extends OptionsBase {
   private CameraOptions$PersistedCameraOptions _persistedCameraOptions;
   private static final long CAMERA_OPTIONS_SYNC_ITEM = -2704008073494369964L;
   private static final long PERSISTED_CAMERA_OPTIONS = -1621984282985383160L;
   public static final int MEMORY_INTERNAL = 0;
   public static final int MEMORY_MEDIACARD = 1;
   public static final int IMAGE_WIDTH = 0;
   public static final int IMAGE_HEIGHT = 1;
   private static final int IMAGE_LARGE = 0;
   private static final int IMAGE_MEDIUM = 1;
   private static final int IMAGE_SMALL = 2;
   public static final int IMAGE_SIZE_DEFAULT_INDEX = 0;
   private static int[][][] _imageSizeTable = new int[3][][];
   private static final int[] IMAGE_SIZE_1600x1200 = new int[]{1600, 1200, -805042284, 100692579, 11408, 1935999488, 1811939328, 2141993162};
   private static final int[] IMAGE_SIZE_1280x1024 = new int[]{1280, 1024, 51, -804651006, 320, 240, -804651006, 1600};
   private static final int[] IMAGE_SIZE_1024x768 = new int[]{1024, 768, -804651006, 1280, 1024, 51, -804651006, 320};
   private static final int[] IMAGE_SIZE_640x480 = new int[]{640, 480, 527827200, 16810638, 1701539702, 1870004480, 290219371, -1258225653};
   private static final int[] IMAGE_SIZE_320x240 = new int[]{320, 240, -804651006, 1600, 1200, -805042284, 100692579, 11408};
   private static final int IMAGE_QUALITY_DEFAULT_INDEX = 2;
   private static final int[] IMAGE_QUALITY_TABLE = new int[]{0, 1, 2, -804651001, 0, 1, 2, 3, -1, 5, 6, -804651005};
   public static final int FLASH_MODE_OFF_INDEX = 0;
   public static final int FLASH_MODE_ON_INDEX = 1;
   public static final int FLASH_MODE_AUTO_INDEX = 2;
   private static final int FLASH_MODE_DEFAULT_INDEX = 2;
   public static final int[] FLASH_MODE_TABLE = new int[]{0, 1, 2, -804651001, 0, 1, 2, 3, -1, 5, 6, -804651005};
   static final int WHITE_BALANCE_UNUSED_INDEX = -1;
   private static final int WHITE_BALANCE_DEFAULT_INDEX = 0;
   private static final int[] WHITE_BALANCE_TABLE = new int[]{
      0,
      1,
      2,
      3,
      -1,
      5,
      6,
      -804651005,
      0,
      5,
      6,
      -804651006,
      1024,
      768,
      -804651006,
      1280,
      1024,
      51,
      -804651006,
      320,
      240,
      -804651006,
      1600,
      1200,
      -805042284,
      100692579,
      11408,
      1935999488
   };
   public static final int VIEWFINDER_MODE_TOGGLE = -1;
   public static final int VIEWFINDER_MODE_NORMAL = 0;
   public static final int VIEWFINDER_MODE_FULLSCREEN = 1;
   public static final int VIEWFINDER_MODE_DEFAULT = 0;
   private static final int SUPPORTED_COLOUR_EFFECTS = Camera.getColourEffects();
   private static final int COLOUR_EFFECTS_DEFAULT_INDEX = 0;
   private static final int[] COLOUR_EFFECTS_TABLE = new int[]{0, 5, 6, -804651006, 1024, 768, -804651006, 1280, 1024, 51, -804651006, 320};
   public static final int CAMERA_OPTION_FLASHMODE = 1;
   public static final int CAMERA_OPTION_ZOOMMODE = 2;
   public static final int CAMERA_OPTION_WHITEBALANCE = 3;
   public static final int CAMERA_OPTION_QUALITY = 4;
   public static final int CAMERA_OPTION_SIZE = 5;
   public static final int CAMERA_MEMORY_TYPE = 6;
   public static final int CAMERA_DESTINATION_FOLDER = 7;
   public static final int CAMERA_VIEWFINDER_MODE = 8;
   public static final int CAMERA_COLOUR_EFFECTS = 9;
   public static final int CAMERA_FREQ_SELECT = 10;
   static final int DEV_OPTION_PREVIEW = 1;
   static final int DEV_OPTION_SOUND = 2;
   static final int DEV_OPTION_LOGGING = 4;
   private static CameraOptions _options;

   private CameraOptions() {
      if (Arrays.equals(Camera.getMaxPictureDimensions(), IMAGE_SIZE_1280x1024)) {
         _imageSizeTable[0] = (int[][])IMAGE_SIZE_1280x1024;
         _imageSizeTable[1] = (int[][])IMAGE_SIZE_640x480;
         _imageSizeTable[2] = (int[][])IMAGE_SIZE_320x240;
      } else {
         _imageSizeTable[0] = (int[][])IMAGE_SIZE_1600x1200;
         _imageSizeTable[1] = (int[][])IMAGE_SIZE_1024x768;
         _imageSizeTable[2] = (int[][])IMAGE_SIZE_640x480;
      }
   }

   public static final CameraOptions getOptions() {
      if (_options == null) {
         _options = new CameraOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1621984282985383160L);
      synchronized (persistentObject) {
         this._persistedCameraOptions = (CameraOptions$PersistedCameraOptions)persistentObject.getContents();
         if (this._persistedCameraOptions == null) {
            this._persistedCameraOptions = new CameraOptions$PersistedCameraOptions();
            persistentObject.setContents(this._persistedCameraOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-2704008073494369964L);
         if (syncItem == null) {
            syncItem = new CameraOptions$CameraOptionsSyncItem();
            ar.put(-2704008073494369964L, syncItem);
         }

         return syncItem;
      }
   }

   public final int getMemoryType() {
      return this._persistedCameraOptions._memoryType;
   }

   public final void setMemoryType(int type) {
      if (type != 0 && type != 1) {
         type = 0;
         this.setDestinationFolder(getDefaultPath(type));
      }

      if (type != this._persistedCameraOptions._memoryType) {
         this._persistedCameraOptions._memoryType = type;
         this.fireOptionsChanged(6);
      }
   }

   public static final String getDefaultPath(int type) {
      return FileIndexService.getDefaultMediaFolderForFileSystem(1, type == 1 ? 1 : 3, -1);
   }

   public final String getDestinationFolder() {
      return this._persistedCameraOptions._destinationFolder;
   }

   public final void setDestinationFolder(String folder) {
      boolean folderValid = FileUtilities.ensureDirectoryExists(FileUtilities.makeFileURL(folder));
      if (!folderValid) {
         folder = getDefaultPath(this._persistedCameraOptions._memoryType);
      }

      if (!folder.equals(this._persistedCameraOptions._destinationFolder)) {
         this._persistedCameraOptions._destinationFolder = folder;
         this.fireOptionsChanged(7);
      }
   }

   public final int getImageSizeIndex() {
      return this._persistedCameraOptions._imageSizeIndex;
   }

   public final int[] getImageSize() {
      return this.getImageSize(this.getImageSizeIndex());
   }

   public final int[] getImageSize(int sizeIndex) {
      return (int[])_imageSizeTable[sizeIndex];
   }

   public final void setImageSizeIndex(int sizeIndex) {
      if (sizeIndex < 0 || sizeIndex >= _imageSizeTable.length) {
         sizeIndex = 0;
      }

      if (sizeIndex != this._persistedCameraOptions._imageSizeIndex) {
         this._persistedCameraOptions._imageSizeIndex = sizeIndex;
         this.fireOptionsChanged(5);
      }
   }

   public final int getImageQualityIndex() {
      return this._persistedCameraOptions._imageQualityIndex;
   }

   public final int getImageQuality() {
      return IMAGE_QUALITY_TABLE[this._persistedCameraOptions._imageQualityIndex];
   }

   public final void setImageQualityIndex(int qualityIndex) {
      if (qualityIndex < 0 || qualityIndex >= IMAGE_QUALITY_TABLE.length) {
         qualityIndex = 2;
      }

      if (qualityIndex != this._persistedCameraOptions._imageQualityIndex) {
         this._persistedCameraOptions._imageQualityIndex = qualityIndex;
         this.fireOptionsChanged(4);
      }
   }

   public final int getFlashModeIndex() {
      return this._persistedCameraOptions._flashModeIndex;
   }

   public final void setFlashModeIndex(int flashModeIndex) {
      if (flashModeIndex < 0 || flashModeIndex >= FLASH_MODE_TABLE.length) {
         flashModeIndex = 2;
      }

      if (flashModeIndex != this._persistedCameraOptions._flashModeIndex) {
         this._persistedCameraOptions._flashModeIndex = flashModeIndex;
         this.fireOptionsChanged(1);
      }
   }

   public final int getWhiteBalanceIndex() {
      return this._persistedCameraOptions._whiteBalanceIndex;
   }

   public final int getWhiteBalance() {
      return WHITE_BALANCE_TABLE[this._persistedCameraOptions._whiteBalanceIndex];
   }

   final int[] getWhiteBalanceTable() {
      return WHITE_BALANCE_TABLE;
   }

   public final void setWhiteBalanceIndex(int whiteBalanceIndex) {
      if (whiteBalanceIndex < 0 || whiteBalanceIndex >= WHITE_BALANCE_TABLE.length || WHITE_BALANCE_TABLE[whiteBalanceIndex] == -1) {
         whiteBalanceIndex = 0;
      }

      if (whiteBalanceIndex != this._persistedCameraOptions._whiteBalanceIndex) {
         this._persistedCameraOptions._whiteBalanceIndex = whiteBalanceIndex;
         this.fireOptionsChanged(3);
      }
   }

   public final int getViewfinderMode() {
      return Display.getWidth() == Display.getHeight() * 4 / 3 ? this._persistedCameraOptions._viewfinderMode : 0;
   }

   public final void setViewfinderMode(int setting) {
      if (setting == -1) {
         setting = (this._persistedCameraOptions._viewfinderMode + 1) % 2;
      }

      if (!isViewfinderModeValid(setting)) {
         setting = 0;
      }

      if (setting != this._persistedCameraOptions._viewfinderMode) {
         this._persistedCameraOptions._viewfinderMode = setting;
         this.fireOptionsChanged(8);
      }
   }

   public static final boolean isViewfinderModeValid(int mode) {
      return mode == 1 ? Display.getWidth() == Display.getHeight() * 4 / 3 : mode == 0;
   }

   public final int getColourEffectIndex() {
      return this._persistedCameraOptions._colourEffectIndex;
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

      if (index != this._persistedCameraOptions._colourEffectIndex) {
         this._persistedCameraOptions._colourEffectIndex = index;
         this.fireOptionsChanged(9);
      }
   }

   final boolean toggleDevOption(int opCode) {
      this._persistedCameraOptions._devOptions ^= opCode;
      return this.isDevOptionEnabled(opCode);
   }

   public final boolean isDevOptionEnabled(int opCode) {
      return (this._persistedCameraOptions._devOptions & opCode) == opCode;
   }
}
