package net.rim.device.apps.api.framework.file;

import java.io.InputStream;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

public class ExplorerServices {
   public static final int VIEW_EXPLORE_ALL;
   public static final int VIEW_EXPLORE_PICTURES;
   public static final int VIEW_EXPLORE_RINGTONES;
   public static final int VIEW_EXPLORE_VIDEOS;
   public static final int VIEW_EXPLORE_MUSIC;
   public static final int VIEW_EXPLORE_VOICE_NOTES;
   private static final int TOTAL_VIEW_EXPLORE;
   private static final int VIEW_MEDIA_OFFSET;
   public static final int VIEW_MEDIA_PICTURES;
   public static final int VIEW_MEDIA_RINGTONES;
   public static final int VIEW_MEDIA_VIDEOS;
   public static final int VIEW_MEDIA_MUSIC;
   public static final int VIEW_MEDIA_VOICE_NOTES;
   private static final int TOTAL_VIEW_MEDIA;
   private static String[] _lastUsedFolder = new Object[8];

   private ExplorerServices() {
   }

   public static Verb getSelectVerb(String defaultPath, ContextObject context, int mediaType, Recognizer filter) {
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-2843135760572915788L);
      context = ContextObject.castOrCreate(context);
      context.setFlag(5);
      mediaType = MathUtilities.clamp(0, mediaType, 5);
      context.putIntegerData(mediaType);
      if (defaultPath != null) {
         context.put(2765042845091913199L, defaultPath);
      }

      if (filter != null) {
         context.put(-409744358660961448L, filter);
      }

      if (verbFactories != null && verbFactories.length > 0) {
         int i = verbFactories.length;

         while (--i >= 0) {
            VerbFactory verbFactory = verbFactories[i];
            Verb[] verbs = verbFactory.getVerbs(context);
            if (verbs != null && verbs.length > 0) {
               return verbs[0];
            }
         }
      }

      return null;
   }

   public static Verb getBrowseVerb(String defaultPath, int view, VerbProvider verbProvider) {
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-2843135760572915788L);
      ContextObject ctx = (ContextObject)(new Object(45));
      if ((0 > view || view >= 6) && (128 > view || view >= 133)) {
         view = 0;
      }

      ContextObject.put(ctx, 3941043584844673548L, new Object(view));
      if (defaultPath != null) {
         ctx.put(2765042845091913199L, defaultPath);
      }

      if (verbProvider != null) {
         ctx.put(424670468422402792L, verbProvider);
      }

      if (verbFactories != null && verbFactories.length > 0) {
         int i = verbFactories.length;

         while (--i >= 0) {
            VerbFactory verbFactory = verbFactories[i];
            Verb[] verbs = verbFactory.getVerbs(ctx);
            if (verbs != null && verbs.length > 0) {
               return verbs[0];
            }
         }
      }

      return null;
   }

   public static Verb getRenameVerb(String defaultPath, VerbProvider verbProvider) {
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-2843135760572915788L);
      ContextObject ctx = (ContextObject)(new Object());
      if (defaultPath != null) {
         ctx.put(2765042845091913199L, defaultPath);
      }

      if (verbProvider != null) {
         ctx.put(424670468422402792L, verbProvider);
      }

      if (verbFactories != null && verbFactories.length > 0) {
         int i = verbFactories.length;

         while (--i >= 0) {
            VerbFactory verbFactory = verbFactories[i];
            Verb[] verbs = verbFactory.getVerbs(ctx);
            if (verbs != null && verbs.length > 0) {
               return verbs[0];
            }
         }
      }

      return null;
   }

   public static Verb getSaveInputStreamVerb(String defaultFilename, int mediaType, boolean confirmName, boolean overwrite) {
      ContextObject context = (ContextObject)(new Object());
      if (defaultFilename != null) {
         context.put(2765042845091913199L, defaultFilename);
      }

      return new SaveInputStreamVerb(context, confirmName, overwrite);
   }

   public static String saveInputStream(String defaultFilename, InputStream input, int mediaType, boolean confirmName, boolean overwrite) {
      return saveInputStream(defaultFilename, input, mediaType, confirmName, overwrite, false);
   }

   public static String saveInputStream(String defaultFilename, InputStream input, int mediaType, boolean confirmName, boolean overwrite, boolean drmProtected) {
      String path;
      if (FileUtilities.isDirectory(defaultFilename)) {
         path = defaultFilename;
         defaultFilename = "";
      } else {
         int size = Integer.MAX_VALUE;

         label25:
         try {
            size = input.available();
         } finally {
            break label25;
         }

         path = FileUtilities.getDefaultWritablePathForMIMEType(MIMETypeAssociations.getMIMEType(defaultFilename), size);
         defaultFilename = FileUtilities.getDisplayName(FileUtilities.getName(defaultFilename));
      }

      return saveInputStream(path, defaultFilename, input, mediaType, confirmName, overwrite, drmProtected);
   }

   public static String saveInputStream(String defaultPath, String defaultFilename, InputStream input, int mediaType, boolean confirmName, boolean overwrite) {
      return saveInputStream(defaultPath, defaultFilename, input, mediaType, confirmName, overwrite, false);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static String saveInputStream(
      String defaultPath, String defaultFilename, InputStream input, int mediaType, boolean confirmName, boolean overwrite, boolean drmProtected
   ) {
      ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
      FileDialog dialog = null;
      String destFileURL = null;
      int retVal = 0;
      if (defaultPath == null) {
         defaultPath = _lastUsedFolder[mediaType];
      }

      if (!confirmName && defaultFilename != null) {
         destFileURL = FileUtilities.makeFileURL(defaultPath, defaultFilename);
      } else {
         dialog = new FileDialog(defaultPath, defaultFilename, mediaType, rb.getString(39), 134217728);
         retVal = dialog.doModal();
         if (retVal == -1) {
            return null;
         }

         destFileURL = dialog.getURL();
      }

      while (retVal != -1 && destFileURL != null) {
         String msg = rb.getString(39);
         String okButtonLabel = CommonResource.getString(10070);
         String path = FileUtilities.getPath(destFileURL);

         try {
            FileUtilities.saveInputStream(input, destFileURL, overwrite, drmProtected);
            _lastUsedFolder[mediaType] = path;
            return destFileURL;
         } catch (Throwable var17) {
            String errStr = ioe.getMessage();
            if (errStr == null) {
               errStr = rb.getString(50);
            }

            msg = errStr;
            if (ioe instanceof Object && ((FileIOException)ioe).getErrorCode() == 7) {
               overwrite = true;
               okButtonLabel = rb.getString(99);
            } else {
               overwrite = false;
               okButtonLabel = CommonResource.getString(10070);
            }

            dialog = null;
            if (dialog == null) {
               dialog = new FileDialog(path, FileUtilities.getName(destFileURL), mediaType, msg, okButtonLabel, 134217728);
            }

            retVal = dialog.doModal();
            if (retVal == -1) {
               return null;
            }

            destFileURL = dialog.getURL();
            continue;
         }
      }

      return null;
   }

   static {
      int mediaType = 3;

      while (--mediaType >= 0) {
         _lastUsedFolder[mediaType] = FileUtilities.getDefaultPath(mediaType);
      }
   }
}
