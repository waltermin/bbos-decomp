package net.rim.device.apps.api.framework.file;

import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.Recognizer;

public class FileSelectionFilter {
   protected int _selectAttribs;
   private Recognizer _recognizer;
   private String _sampleFolder;
   public static final int ROOT_VIEW_EXPLORE = 0;
   public static final int ROOT_VIEW_PICTURES = 1;
   public static final int ROOT_VIEW_RINGTONES = 2;
   public static final int ROOT_VIEW_VIDEOS = 3;
   public static final int ROOT_VIEW_MUSIC = 4;
   public static final int ROOT_VIEW_VOICE_NOTES = 5;
   public static final int MEDIA_TYPE_FIELD = 255;
   public static final int FORWARD_LOCKED = 256;
   public static final int FORWARD_UNLOCKED = 512;
   public static final int SELECT_FOLDER = 1024;
   public static final int SELECT_FILE = 2048;
   public static final int SELECT_WRITEABLE = 4096;
   public static final int PRELOADED = 8192;
   public static final int HIDE_FILTERED = Integer.MIN_VALUE;
   private static final int DEFAULT_SAVE_ATTRIBS = -2147476480;
   private static final int DEFAULT_LOAD_ATTRIBS = -2147473408;

   public FileSelectionFilter() {
      this(0);
   }

   public FileSelectionFilter(int mediaType, int selectAttribs, Recognizer recognizer) {
      this._selectAttribs = selectAttribs;
      this.setMediaType(mediaType);
      this._recognizer = recognizer;
   }

   public FileSelectionFilter(int mediaType, int selectAttribs) {
      this(mediaType, selectAttribs, null);
   }

   public FileSelectionFilter(int mediaType, boolean writeable) {
      this(mediaType, writeable ? -2147476480 : -2147473408, null);
   }

   public FileSelectionFilter(int mediaType, boolean writeable, Recognizer recognizer) {
      this(mediaType, writeable ? -2147476480 : -2147473408, recognizer);
   }

   public FileSelectionFilter(int mediaType) {
      this(mediaType, -2147481600, null);
   }

   public void resetFilter() {
      this._selectAttribs = 0;
      this._sampleFolder = null;
   }

   public void setMediaType(int mediaType) {
      mediaType = MathUtilities.clamp(0, mediaType, 5);
      this._selectAttribs = mediaType & 0xFF | this._selectAttribs & -256;
   }

   public int getMediaType() {
      return this._selectAttribs & 0xFF;
   }

   public void onlySelectForwardLocked() {
      this._selectAttribs = (this._selectAttribs | 256) & -513;
      this.onlySelectFile();
   }

   public void onlySelectForwardUnlocked() {
      this._selectAttribs = (this._selectAttribs | 512) & -257;
      this.onlySelectFile();
   }

   public void setSelectForwardLocked(boolean allowed) {
      if (allowed) {
         this._selectAttribs |= 256;
      } else {
         this._selectAttribs &= -257;
      }
   }

   public boolean isSelectForwardLockedOn() {
      return (this._selectAttribs & 256) != 0;
   }

   public void setSelectForwardUnlocked(boolean allowed) {
      if (allowed) {
         this._selectAttribs |= 512;
      } else {
         this._selectAttribs &= -513;
      }
   }

   public boolean isSelectForwardUnlockedOn() {
      return (this._selectAttribs & 512) != 0;
   }

   public void onlySelectFolder() {
      this._selectAttribs = (this._selectAttribs | 1024) & -2049;
   }

   public void onlySelectFile() {
      this._selectAttribs = (this._selectAttribs | 2048) & -1025;
   }

   public void setSelectFile(boolean allowed) {
      if (allowed) {
         this._selectAttribs |= 2048;
      } else {
         this._selectAttribs &= -2049;
      }
   }

   public boolean isSelectFileOn() {
      return (this._selectAttribs & 2048) != 0;
   }

   public void setSelectFolder(boolean allowed) {
      if (allowed) {
         this._selectAttribs |= 1024;
      } else {
         this._selectAttribs &= -1025;
      }
   }

   public int getSelectFilter() {
      return this._selectAttribs & -256;
   }

   public boolean isSelectFolderOn() {
      return (this._selectAttribs & 1024) != 0;
   }

   public void setHideFiltered(boolean hide) {
      if (hide) {
         this._selectAttribs |= Integer.MIN_VALUE;
      } else {
         this._selectAttribs &= Integer.MAX_VALUE;
      }
   }

   public boolean isHideFilteredOn() {
      return (this._selectAttribs & -2147483648) != 0;
   }

   public boolean isSelectWriteable() {
      return (this._selectAttribs & 4096) != 0;
   }

   public void setSelectWriteable(boolean writeable) {
      if (writeable) {
         this._selectAttribs |= 4096;
      } else {
         this._selectAttribs &= -4097;
      }
   }

   public void setFilter(int include, int exclude) {
      this._selectAttribs |= include;
      this._selectAttribs &= ~exclude;
   }

   public boolean isSet(int attrib) {
      return (this._selectAttribs & attrib) == attrib;
   }

   public Recognizer getRecognizer() {
      return this._recognizer;
   }

   public static int getDefaultRootViewForMediaType(int mediaType) {
      return getDefaultRootViewForMediaType(mediaType, null);
   }

   public static int getDefaultRootViewForMediaType(int mediaType, String fileName) {
      switch (mediaType) {
         case 0:
            return 0;
         case 1:
            return 1;
         case 2:
         default:
            return fileName == null
                  || !fileName.toLowerCase().startsWith("/sdcard/blackberry/voicenotes/") && !fileName.toLowerCase().startsWith("/store/home/user/voicenotes/")
               ? 2
               : 5;
         case 3:
            return 3;
      }
   }

   public void setSampleFolder(String folder) {
      this._sampleFolder = folder;
      if (folder == null) {
         this._selectAttribs &= -8193;
      } else {
         this._selectAttribs |= 8192;
      }
   }

   public String getSampleFolder() {
      return this._sampleFolder;
   }
}
