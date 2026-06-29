package net.rim.device.apps.internal.ribbon.launcher;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.utility.props.BooleanProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.ribbon.ApplicationLauncher;
import net.rim.device.apps.internal.ribbon.ApplicationMenu;
import net.rim.device.internal.ui.ScaleBitmap;
import net.rim.device.internal.util.StringUtilitiesInternal;

public final class FolderEntryPointDescriptor
   extends AbstractEntryPointDescriptor
   implements EntryPointDescriptor,
   StringProps,
   ObjectProps,
   BooleanProps,
   Runnable {
   private int _customBitmapWidth = -1;
   private int _customBitmapHeight = -1;
   private Bitmap _originalCustomBitmap;
   private Bitmap _scaledCustomBitmap;
   private InternalApplicationFolder _folder;
   private String _launchURL;
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private static final String ROOT_FOLDER_IMAGE = "up_folder";
   public static final String FOLDER_IMAGE = "folder";
   public static final String COMMON_FOLDER_LOCATION_URL = "file:///store/samples/folder icons/";

   public final void resetIcon() {
      String propertiesName = this.get(9, "");
      if (propertiesName != null) {
         ApplicationProperties ap = HierarchyManager.getInstance().getActiveHierarchy().getApplicationProperties(propertiesName);
         this._originalCustomBitmap = null;
         if (ap != null) {
            this._originalCustomBitmap = getScaledImage("file:///store/samples/folder icons/" + ap.getCustomImageName());
         }
      }

      this._scaledCustomBitmap = null;
   }

   public final String getFolderName() {
      return this._launchURL.substring(ApplicationLauncher.FOLDER_URL_PREFIX.length());
   }

   public final void setLaunchURL(String url) {
      this._launchURL = url;
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return this._folder.getName();
      } else if (propID == 3) {
         return this.getDescription();
      } else {
         return propID == 9 ? this._folder.getName().trim().replace(' ', '\n') : defaultReturned;
      }
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      boolean result = defaultReturned;
      if (propID == 7) {
         InternalApplicationHierarchy hierarchy = HierarchyManager.getInstance().getActiveHierarchy();
         String name = this.get(9, "");
         ApplicationProperties properties = null;
         if (hierarchy != null) {
            properties = hierarchy.getApplicationProperties(name);
            if (properties == null) {
               properties = hierarchy.createDefaultProperties(name);
            }
         }

         if (properties != null) {
            result = properties.canHide();
         }
      }

      return new Boolean(result);
   }

   @Override
   public final void set(long propID, Boolean valueToSet) {
   }

   @Override
   public final void set(long propID, Object valueToSet) {
   }

   @Override
   public final void run() {
      ApplicationLauncher.launch(this._launchURL);
      if (ApplicationMenu.containsApplicationMenu() && !ApplicationMenu.isAppMenuDisplayed()) {
         ApplicationMenu.setSkipRootFolder(true);
         ApplicationMenu.show();
      } else {
         ApplicationMenu.setSkipRootFolder(false);
      }
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      Theme theme = ThemeManager.getActiveTheme();
      if (this._originalCustomBitmap == null) {
         EncodedImage img = null;
         String name = this._folder.getName();
         if (name.length() == 0) {
            img = this.getImage("up_folder", propID, theme);
         } else if (this.isThemeFolder()) {
            img = this.getImage(name, propID, theme);
         }

         if (img == null) {
            img = this.getImage("folder", propID, theme);
         }

         return img == null ? defaultReturned : img.getBitmap();
      } else {
         if (this._scaledCustomBitmap == null
            || this._customBitmapWidth != theme.getRibbonIconWidth()
            || this._customBitmapHeight != theme.getRibbonIconHeight()) {
            this._customBitmapWidth = theme.getRibbonIconWidth();
            this._customBitmapHeight = theme.getRibbonIconHeight();
            int origW = this._originalCustomBitmap.getWidth();
            int origH = this._originalCustomBitmap.getHeight();
            if (origW > origH) {
               this._scaledCustomBitmap = ScaleBitmap.scaleBitmap(
                  1, this._originalCustomBitmap, this._customBitmapWidth, this._customBitmapWidth * origH / origW
               );
            } else {
               this._scaledCustomBitmap = ScaleBitmap.scaleBitmap(
                  1, this._originalCustomBitmap, this._customBitmapHeight * origW / origH, this._customBitmapHeight
               );
            }
         }

         return this._scaledCustomBitmap;
      }
   }

   @Override
   public final void set(long propID, String valueToSet) {
   }

   private final String getFolderNameWithFocus(String name) {
      StringBuffer _buffer = StringUtilitiesInternal.getScratchBuffer();
      synchronized (_buffer) {
         _buffer.append(name);
         _buffer.append('~');
         _buffer.append("focus");
         String nameWithFocus = _buffer.toString();
         _buffer.setLength(0);
         return nameWithFocus;
      }
   }

   @Override
   protected final String getDefaultDescription() {
      return this._folder.getName().length() > 0 ? this._folder.getName() : _resources.getString(172);
   }

   public static final boolean isUpFolder(ApplicationEntry entry) {
      return entry instanceof UpFolderApplicationEntry;
   }

   public static final String createLaunchURL(InternalApplicationFolder folder) {
      return ApplicationLauncher.FOLDER_URL_PREFIX + folder.getName();
   }

   public static final Bitmap getScaledImage(String url) {
      try {
         FileConnection conn = null;
         conn = (FileConnection)Connector.open(url);
         if (!conn.exists()) {
            return null;
         }

         int size = (int)conn.fileSize();
         byte[] buffer = new byte[size];
         InputStream in = conn.openInputStream();
         int amount = 0;

         while (amount != -1) {
            amount = in.read(buffer, amount, size);
         }

         conn.close();
         return Bitmap.createBitmapFromBytes(buffer, 0, -1, 1);
      } finally {
         ;
      }
   }

   FolderEntryPointDescriptor(InternalApplicationFolder folder, String bundleName, int resourceId) {
      super(bundleName, resourceId);
      this._folder = folder;
      this._launchURL = createLaunchURL(folder);
      String propertiesName = this.get(9, "");
      if (propertiesName != null && propertiesName.length() > 0) {
         ApplicationProperties ap = HierarchyManager.getInstance().getActiveHierarchy().getApplicationProperties(propertiesName);
         if (ap != null) {
            String alias = ap.getAlias();
            if (alias != null && alias.length() > 0) {
               this.setDescription(alias);
            }
         }
      }

      this.resetIcon();
   }

   private final boolean isThemeFolder() {
      return this.getResourceId() > 0 && this.getBundleName().length() > 0;
   }

   private final EncodedImage getImage(String name, long propID, Theme theme) {
      EncodedImage img = null;
      if (propID == 10) {
         img = theme.getImage(this.getFolderNameWithFocus(name), true);
      }

      if (img == null) {
         img = theme.getImage(name, true);
      }

      return img;
   }
}
