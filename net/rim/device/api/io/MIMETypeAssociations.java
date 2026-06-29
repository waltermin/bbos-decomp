package net.rim.device.api.io;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringCaseInsensitiveHashtable;
import net.rim.device.api.util.StringToIntCaseInsensitiveHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.MediaNatives;

public final class MIMETypeAssociations {
   private Hashtable _mimeTypeToExt;
   private StringCaseInsensitiveHashtable _extToMimeType = new StringCaseInsensitiveHashtable();
   private Hashtable _typeMappings;
   private StringToIntCaseInsensitiveHashtable _extToMediaType;
   public static final int MEDIA_TYPE_UNKNOWN = 0;
   public static final int MEDIA_TYPE_IMAGE = 1;
   public static final int MEDIA_TYPE_AUDIO = 2;
   public static final int MEDIA_TYPE_VIDEO = 3;
   public static final int MEDIA_TYPE_TEXT = 4;
   public static final int MEDIA_TYPE_ARCHIVE = 5;
   public static final int MEDIA_TYPE_APPLICATION = 6;
   public static final int MEDIA_TYPE_PLAY_LIST = 7;
   private static final long FILE_TYPE_REGISTRY = -2905222580274580678L;
   private static MIMETypeAssociations _instance;
   private static String ENCRYPTED_MEDIA_EXTENSION = ".rem";
   private static String DIGITAL_RIGHTS_EXTENSION = ".dm";

   private MIMETypeAssociations() {
      this._mimeTypeToExt = new Hashtable();
      this._extToMediaType = new StringToIntCaseInsensitiveHashtable();
      this._typeMappings = new Hashtable();
      this.registerTypeInternal("text/html", "htm", 4);
      this.registerTypeInternal("text/html", "html", 4);
      this.registerTypeInternal("text/vnd.wap.wml", "wml", 4);
      this.registerTypeInternal("text/plain", "txt", 4);
      this.registerTypeInternal("text/x-vcard", "vcf", 4);
      this.registerTypeInternal("image/bmp", "bmp", 1);
      this.registerMapping("image/x-windows-bmp", "image/bmp");
      this.registerMapping("image/x-bmp", "image/bmp");
      this.registerMapping("image/x-bitmap", "image/bmp");
      this.registerMapping("image/x-ms-bitmap", "image/bmp");
      this.registerTypeInternal("image/gif", "gif", 1);
      this.registerTypeInternal("image/jpeg", "jpg", 1);
      this.registerTypeInternal("image/jpeg", "jpeg", 1);
      this.registerMapping("image/jpg", "image/jpeg");
      this.registerMapping("image/pjpeg", "image/jpeg");
      this.registerTypeInternal("image/png", "png", 1);
      this.registerMapping("image/x-png", "image/png");
      this.registerMapping("image/vnd.rim.png", "image/png");
      this.registerTypeInternal("image/tiff", "tif", 1);
      this.registerTypeInternal("image/tiff", "tiff", 1);
      this.registerTypeInternal("image/vnd.wap.wbmp", "wbmp", 1);
      this.registerTypeInternal("audio/3gpp", "3gp", 2);
      this.registerTypeInternal("audio/3gpp", "3gpp", 2);
      if (RadioInfo.getNetworkType() == 4) {
         this.registerTypeInternal("audio/3gpp2", "3g2", 2);
         this.registerTypeInternal("audio/3gpp2", "3gpp2", 2);
         this.registerTypeInternal("audio/qcelp", "qcp", 2);
         this.registerMapping("audio/vnd.qcelp", "audio/qcelp");
         this.registerMapping("audio/qcp", "audio/qcelp");
         this.registerMapping("audio/vnd.qcp", "audio/qcelp");
      }

      this.registerTypeInternal("audio/x-wav", "wav", 2);
      this.registerMapping("audio/wav", "audio/x-wav");
      this.registerTypeInternal("audio/midi", "mid", 2);
      this.registerMapping("audio/mid", "audio/midi");
      this.registerMapping("audio/sp-midi", "audio/midi");
      this.registerMapping("audio/x-mid", "audio/midi");
      this.registerMapping("audio/x-midi", "audio/midi");
      this.registerTypeInternal("audio/mpeg", "mp3", 2);
      this.registerMapping("audio/x-mpeg", "audio/mpeg");
      this.registerMapping("audio/mp3", "audio/mpeg");
      this.registerMapping("audio/x-mp3", "audio/mpeg");
      this.registerMapping("audio/mpeg3", "audio/mpeg");
      this.registerMapping("audio/x-mpeg3", "audio/mpeg");
      this.registerMapping("audio/mpg3", "audio/mpeg");
      this.registerMapping("audio/x-mpg3", "audio/mpeg");
      this.registerMapping("audio/mpg", "audio/mpeg");
      this.registerMapping("audio/x-mpg", "audio/mpeg");
      this.registerTypeInternal("audio/mp4", "m4a", 2);
      this.registerTypeInternal("audio/mp4", "m4b", 2);
      this.registerMapping("audio/m4a", "audio/mp4");
      this.registerMapping("audio/x-m4a", "audio/mp4");
      this.registerMapping("audio/x-mp4", "audio/mp4");
      this.registerTypeInternal("audio/aac", "aac", 2);
      this.registerMapping("audio/x-aac", "audio/aac");
      this.registerTypeInternal("audio/aac", "apl", 2);
      this.registerTypeInternal("audio/amr", "amr", 2);
      this.registerMapping("audio/x-amr", "audio/amr");
      this.registerTypeInternal("audio/x-gsm", "gsm", 2);
      this.registerTypeInternal("audio/basic", "au", 2);
      this.registerTypeInternal("audio/basic", "snd", 2);
      this.registerTypeInternal("audio/x-mpegurl", "m3u", 7);
      this.registerMapping("audio/mpegurl", "audio/x-mpegurl");
      if (MediaNatives.isAudioDecoderCodecSupported(12)) {
         this.registerTypeInternal("audio/x-ms-wma", "wma", 2);
      }

      this.registerTypeInternal("video/3gpp", "3gp", 3);
      this.registerTypeInternal("video/3gpp", "3gpp", 3);
      this.registerMapping("video/3gp", "video/3gpp");
      if (RadioInfo.getNetworkType() == 4) {
         this.registerTypeInternal("video/3gpp2", "3g2", 3);
         this.registerTypeInternal("video/3gpp2", "3gpp2", 3);
      }

      this.registerTypeInternal("video/mpeg", "mpg", 3);
      this.registerTypeInternal("video/mpeg", "mpeg", 3);
      this.registerTypeInternal("video/mp4", "mp4", 3);
      this.registerTypeInternal("video/mp4", "mpg4", 3);
      this.registerTypeInternal("video/mp4", "m4v", 3);
      this.registerTypeInternal("video/mp4", "mp4v", 3);
      this.registerMapping("video/avi", "video/x-msvideo");
      if (MediaNatives.isVideoDecoderCodecSupported(4)) {
         this.registerTypeInternal("video/x-ms-asf", "asf", 3);
         this.registerTypeInternal("video/x-ms-asf", "asx", 3);
         this.registerTypeInternal("video/x-ms-asf", "nsc", 3);
         this.registerTypeInternal("video/x-ms-wm", "wm", 3);
         this.registerTypeInternal("video/x-ms-wmv", "wmv", 3);
         this.registerTypeInternal("video/x-ms-wmx", "wmx", 3);
         this.registerTypeInternal("video/x-ms-wvx", "wvx", 3);
      }

      this.registerTypeInternal("video/x-msvideo", "avi", 3);
      this.registerTypeInternal("video/quicktime", "mov", 3);
      this.registerTypeInternal("video/quicktime", "qt", 3);
      this.registerMapping("video/x-quicktime", "video/quicktime");
      this.registerTypeInternal("text/vnd.sun.j2me.app-descriptor", "jad", 6);
      this.registerTypeInternal("application/java-archive", "jar", 6);
      this.registerMapping("application/java", "application/java-archive");
      this.registerMapping("application/x-java-archive", "application/java-archive");
      this.registerTypeInternal("application/vnd.wap.wmlc", "wmlc", 4);
      this.registerTypeInternal("application/vnd.rim.html", "rhtml", 4);
      this.registerTypeInternal("application/vnd.rim.html", "rimhtml", 4);
      this.registerTypeInternal("application/xhtml+xml", "xhtml", 4);
      this.registerTypeInternal("application/xhtml+xml", "xht", 4);
      this.registerMapping("application/x-zip-compressed", "application/zip");
      this.registerTypeInternal("application/x-vnd.rim.pme", "pme", 6);
      this.registerTypeInternal("application/x-vnd.rim.pme.b", "pmb", 6);
      this.registerTypeInternal("application/msword", "doc", 6);
      this.registerTypeInternal("application/vnd.ms-excel", "xls", 6);
      this.registerTypeInternal("application/vnd.ms-powerpoint", "ppt", 6);
      this.registerTypeInternal("application/pdf", "pdf", 6);
   }

   private static final void assertPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   private final void registerTypeInternal(String type, String ext, int mediaType) {
      this._extToMimeType.put(ext, type);
      if (!this._mimeTypeToExt.containsKey(type)) {
         this._mimeTypeToExt.put(type, ext);
      }

      this._extToMediaType.put(ext, mediaType);
   }

   private final void registerMapping(String typeFrom, String typeTo) {
      this._typeMappings.put(typeFrom, typeTo);
   }

   public static final void registerMIMETypeMapping(String typeFrom, String typeTo) {
      assertPermission();
      typeFrom = StringUtilities.toLowerCase(typeFrom, 1701707776);
      typeTo = StringUtilities.toLowerCase(typeTo, 1701707776);
      _instance.registerMapping(typeFrom, typeTo);
   }

   public static final String getMIMEType(String filename) {
      if (filename == null) {
         return null;
      }

      int endIndex = filename.length();
      if (filename.endsWith(ENCRYPTED_MEDIA_EXTENSION)) {
         endIndex -= 4;
      }

      int indexOfDot = filename.lastIndexOf(46, endIndex - 1);
      if (indexOfDot != -1 && endIndex > indexOfDot + 1) {
         String result = (String)_instance._extToMimeType.get(filename, indexOfDot + 1, endIndex);
         if (result == null && filename.endsWith(DIGITAL_RIGHTS_EXTENSION)) {
            endIndex -= 3;
            indexOfDot = filename.lastIndexOf(46, endIndex - 1);
            if (indexOfDot != -1 && endIndex > indexOfDot + 1) {
               result = (String)_instance._extToMimeType.get(filename, indexOfDot + 1, endIndex);
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public static final int getMediaType(String filename) {
      if (filename == null) {
         return 0;
      }

      int endIndex = filename.length();
      if (filename.endsWith(ENCRYPTED_MEDIA_EXTENSION)) {
         endIndex -= 4;
      }

      int indexOfDot = filename.lastIndexOf(46, endIndex - 1);
      if (indexOfDot != -1 && endIndex > indexOfDot + 1) {
         int result = _instance._extToMediaType.get(filename, indexOfDot + 1, endIndex);
         if (result == -1 && filename.endsWith(DIGITAL_RIGHTS_EXTENSION)) {
            endIndex -= 3;
            indexOfDot = filename.lastIndexOf(46, endIndex - 1);
            if (indexOfDot != -1 && endIndex > indexOfDot + 1) {
               result = _instance._extToMediaType.get(filename, indexOfDot + 1, endIndex);
            }
         }

         return result == -1 ? 0 : result;
      } else {
         return 0;
      }
   }

   public static final int getMediaTypeFromMIMEType(String mimeType) {
      if (mimeType == null) {
         return 0;
      }

      String ext = (String)_instance._mimeTypeToExt.get(getNormalizedType(mimeType));
      if (ext == null) {
         return 0;
      }

      int result = _instance._extToMediaType.get(ext);
      return result == -1 ? 0 : result;
   }

   public static final String getExtensionFromMIMEType(String mimeType) {
      return mimeType == null ? null : (String)_instance._mimeTypeToExt.get(getNormalizedType(mimeType));
   }

   public static final void registerType(String extension, String mimeType, int mediaType) {
      assertPermission();
      if (extension != null && mimeType != null) {
         synchronized (_instance) {
            if (!_instance._extToMimeType.containsKey(extension)) {
               extension = StringUtilities.toLowerCase(extension, 1701707776);
               mimeType = StringUtilities.toLowerCase(mimeType, 1701707776);
               _instance.registerTypeInternal(mimeType, extension, mediaType);
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public static final String getNormalizedType(String mimeType) {
      if (mimeType == null) {
         return null;
      }

      int indexOfSemicolon = mimeType.indexOf(59);
      if (indexOfSemicolon > 0) {
         mimeType = mimeType.substring(0, indexOfSemicolon).trim();
      }

      mimeType = StringUtilities.toLowerCase(mimeType, 1701707776);
      String normal = (String)_instance._typeMappings.get(mimeType);
      return normal != null ? normal : mimeType;
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Object obj = registry.getOrWaitFor(-2905222580274580678L);
      if (!(obj instanceof MIMETypeAssociations)) {
         _instance = new MIMETypeAssociations();
         registry.put(-2905222580274580678L, _instance);
      } else {
         _instance = (MIMETypeAssociations)obj;
      }
   }
}
