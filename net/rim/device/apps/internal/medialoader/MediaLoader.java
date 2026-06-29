package net.rim.device.apps.internal.medialoader;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.store.ContentStoreInjector;
import net.rim.device.resources.Resource;
import net.rim.vm.Process;

final class MediaLoader {
   private static String SUFFIX_GIF = ".gif";
   private static String SUFFIX_JPG = ".jpg";
   private static String SUFFIX_PNG = ".png";
   private static String SUFFIX_MID = ".mid";
   private static String SUFFIX_MMS = ".mms";
   private static String SUFFIX_MP3 = ".mp3";
   private static String SUFFIX_MP4 = ".mp4";
   private static String SUFFIX_AVI = ".avi";
   private static String SUFFIX_AAC = ".aac";
   private static String SUFFIX_M4A = ".m4a";
   private static String SUFFIX_WAV = ".wav";
   private static String SUFFIX_MOV = ".mov";
   private static String PATH_PICTURE = "pictures/";
   private static String PATH_CONTACTS = "pictures/contacts/";
   private static String PATH_MMS = "mms/";
   private static String PATH_RINGTONES = "ringtones/";
   private static String PATH_VIDEOS = "videos/";
   private static String PATH_VIDEOS_83XX = "videos_83xx/";
   private static String PATH_VIDEOS_81XX = "videos_81xx/";
   private static String PATH_VIDEOS_8100 = "videos_8100/";
   private static String PATH_VIDEOS_8330 = "videos_8330/";
   private static String PATH_MUSIC = "music/";
   private static String PATH_FOLDER_ICONS = "folders/";
   private static String USER_PICTURE_FOLDER = "/home/user/pictures/";
   private static String JSR_PICTURE_FOLDER = "/store/samples/pictures/";
   private static String JSR_CONTACTS_FOLDER = "/store/samples/contacts/";
   private static String JSR_RINGTONES_FOLDER = "/store/samples/ringtones/";
   private static String JSR_VIDEOS_FOLDER = "/store/samples/videos/";
   private static String JSR_MUSIC_FOLDER = "/store/samples/music/";
   private static String JSR_MMS_PICTURE_FOLDER = "/store/samples/mms/pictures/";
   private static String JSR_MMS_TUNE_FOLDER = "/store/samples/mms/tunes/";
   private static String JSR_MMS_TEMPLATE_FOLDER = "/store/samples/mms/templates/";
   private static String PATH_FIVE_ICON = "pictures/5_icons";
   private static String FIVE_ICON_FOLDER = "/store/home/user/pictures/myFaves icons/";
   private static String JSR_FOLDER_ICONS_FOLDER = "/store/samples/folder icons/";
   static String[] _fileNames = new String[0];

   public static final void main(String[] args) {
      String resourceModuleName = Process.currentProcess().getModuleName();
      String nameKey = "net.rim.device.apps.internal.medialoader.MediaLoader." + resourceModuleName;
      if (args != null && args.length > 0) {
         label169:
         try {
            int vendorId = Integer.valueOf(args[0]);
            int brandingId = Branding.getVendorId();
            if (vendorId != 0 && brandingId != vendorId && brandingId != 1) {
               String logData = "Bailing out of Medialoader (mismatched vendor id) for: " + nameKey;
               EventLogger.logEvent(-7509200465648525729L, logData.getBytes(), 0);
               return;
            }
         } finally {
            break label169;
         }
      }

      String logData = "Loading media for: " + nameKey;
      EventLogger.logEvent(-7509200465648525729L, logData.getBytes(), 0);
      Resource resources = Resource.getResourceClass();
      Enumeration enumeration = resources.getResourceKeys();
      String fiveURL = FileUtilities.makeFileURL(FIVE_ICON_FOLDER);
      boolean folderAdded = FileUtilities.checkFileExists(fiveURL);
      if (enumeration != null) {
         while (enumeration.hasMoreElements()) {
            String inname = (String)enumeration.nextElement();
            String name = FileUtilities.getName(inname);
            String codResourceName = "cod://" + resourceModuleName + '/' + inname;
            if (inname.startsWith(PATH_FIVE_ICON) && isImageSuffix(inname)) {
               if (!folderAdded) {
                  FileUtilities.ensureDirectoryExists(fiveURL);
                  FileUtilities.setHidden(fiveURL, true);
                  folderAdded = true;
               }

               ContentStoreInjector.injectLinkToCodFile(codResourceName, FIVE_ICON_FOLDER + name, true);
            } else if (inname.startsWith(PATH_FOLDER_ICONS) && isImageSuffix(inname)) {
               ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_FOLDER_ICONS_FOLDER + name, true);
            } else if (inname.startsWith(PATH_CONTACTS) && isImageSuffix(inname)) {
               ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_CONTACTS_FOLDER + name, true);
            } else if (inname.startsWith(PATH_PICTURE) && isImageSuffix(inname)) {
               ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_PICTURE_FOLDER + name, true);
               ContentStoreInjector.removeLegacyLink(USER_PICTURE_FOLDER + name);
            } else if (inname.startsWith(PATH_RINGTONES) && isAudioSuffix(inname)) {
               String dstName = JSR_RINGTONES_FOLDER + name;
               ContentStoreInjector.injectLinkToCodFile(codResourceName, dstName, true);
               Arrays.add(_fileNames, dstName);
            } else if (inname.startsWith(PATH_MUSIC) && isAudioSuffix(inname)) {
               ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_MUSIC_FOLDER + name, true);
            } else if ((
                  inname.startsWith(PATH_VIDEOS)
                     || inname.startsWith(PATH_VIDEOS_83XX)
                     || inname.startsWith(PATH_VIDEOS_81XX)
                     || inname.startsWith(PATH_VIDEOS_8100)
                     || inname.startsWith(PATH_VIDEOS_8330)
               )
               && isVideoSuffix(inname)) {
               ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_VIDEOS_FOLDER + name, true);
            } else if (inname.startsWith(PATH_MMS)) {
               if (isImageSuffix(inname)) {
                  ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_MMS_PICTURE_FOLDER + name, false);
               } else if (isAudioSuffix(inname)) {
                  ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_MMS_TUNE_FOLDER + name, false);
               } else if (isMMSTemplateSuffix(inname)) {
                  ContentStoreInjector.injectLinkToCodFile(codResourceName, JSR_MMS_TEMPLATE_FOLDER + name, false);
               }
            }
         }
      }

      if (resourceModuleName.indexOf("tunebundles") != -1) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         Object tuneMediaLoader = registry.getOrWaitFor(6675644780973176756L);
         if (tuneMediaLoader == null) {
            Arrays.sort(_fileNames, MediaLoader$StringComparator.getInstance());
            tuneMediaLoader = new MediaLoader$TuneBundleMediaLoader(_fileNames);
            registry.put(6675644780973176756L, tuneMediaLoader);
         }
      }
   }

   private static final boolean isImageSuffix(String inname) {
      return inname.endsWith(SUFFIX_GIF) || inname.endsWith(SUFFIX_JPG) || inname.endsWith(SUFFIX_PNG);
   }

   private static final boolean isAudioSuffix(String inname) {
      return inname.endsWith(SUFFIX_MID)
         || inname.endsWith(SUFFIX_MP3)
         || inname.endsWith(SUFFIX_AAC)
         || inname.endsWith(SUFFIX_M4A)
         || inname.endsWith(SUFFIX_WAV);
   }

   private static final boolean isVideoSuffix(String inname) {
      return inname.endsWith(SUFFIX_MP4) || inname.endsWith(SUFFIX_AVI) || inname.endsWith(SUFFIX_MOV);
   }

   private static final boolean isMMSTemplateSuffix(String inname) {
      return inname.endsWith(SUFFIX_MMS);
   }
}
