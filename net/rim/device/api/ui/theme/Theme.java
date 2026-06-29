package net.rim.device.api.ui.theme;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ImageBitmap;
import net.rim.device.internal.ui.ImageEncoded;
import net.rim.device.internal.ui.ImageOverlay;
import net.rim.device.internal.ui.NamedIconCollection;
import net.rim.device.internal.ui.ScaleBitmap;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.util.StringUtilitiesInternal;
import net.rim.device.resources.Resource;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.TraceBack;
import net.rim.vm.WeakReference;

public class Theme {
   private Bitmap[] _themeBitmaps = new Bitmap[4];
   private int _borderStyle = 0;
   private int _themeLoadingCount = 0;
   private WeakReference _weakReference = new WeakReference(this);
   private LongHashtable _attributeSets = new LongHashtable(20);
   private Hashtable _moduleDefaults;
   private Hashtable _defaultImageDescriptors;
   private Hashtable _themeImageDescriptors = new Hashtable(200);
   private Hashtable _borders = new Hashtable(10);
   private Hashtable _scrollbars = new Hashtable(10);
   private Theme$LayoutFactory _layoutFactory;
   private Hashtable _fonts = new Hashtable(10);
   private Hashtable _registeredFontNames = new Hashtable(10);
   private int[] _fontHandles = new int[0];
   private Hashtable _ringtones = new Hashtable(10);
   private ToIntHashtable _palette = new ToIntHashtable(10);
   private ToIntHashtable _iconCollectionNames;
   private EncodedImage[] _osIcons = new EncodedImage[11];
   private int _osKeyIconWidth;
   private boolean _ribbonIconSizeSet;
   private int _ribbonIconWidth = 28;
   private int _ribbonIconHeight = 28;
   private boolean _isKeyStateIconsVisible = true;
   private boolean _isRadioIconsVisible = true;
   private boolean _isLabelOnOwnLine;
   private Theme$Writer _writer = new Theme$Writer(this);
   private boolean _idleScreenNameSet;
   private String _idleScreenName;
   private String _thumbnailName;
   private boolean _thumbnailNameSet;
   private Hashtable _aliasList = new Hashtable();
   private int _appIconSize;
   private Hashtable _appIcons = new Hashtable();
   private ResourceBundle _resourceBundle;
   private Hashtable _options = new Hashtable();
   public static final int FOCUS_STYLE_INVERT;
   public static final int FOCUS_STYLE_INVERT_STRIKE;
   public static final int FOCUS_STYLE_INVERT_BOX;
   public static final int FOCUS_STYLE_DRAW;
   public static final int FOCUS_STYLE_CUSTOM;
   public static final int FOCUS_STYLES;
   public static final int STATE_ALL;
   public static final int STATE_FIRST_CHILD;
   public static final int STATE_LINK;
   public static final int STATE_VISITED;
   public static final int STATE_ACTIVE;
   public static final int STATE_HOVER;
   public static final int STATE_FOCUS;
   public static final int STATE_DISABLED;
   public static final int STATE_DISABLED_FOCUS;
   public static final int TEXT_ALIGN_INHERIT;
   public static final int TEXT_ALIGN_LEFT;
   public static final int TEXT_ALIGN_RIGHT;
   public static final int TEXT_ALIGN_CENTER;
   public static final int TEXT_ALIGN_JUSTIFY;
   public static final int TEXT_ALIGN_LEADING;
   public static final int TEXT_ALIGN_TRAILING;
   public static final int THEME_UPARROW;
   public static final int THEME_DOWNARROW;
   public static final int THEME_SCROLLBAR_ELEVATOR;
   public static final int THEME_SCROLLBAR_INDICATOR;
   private static final int THEME_BITMAPS;
   private static final int BORDER_STYLES;
   public static final int BORDER_ROUNDED;
   public static final int BORDER_BOX;
   public static final int BORDER_CUSTOM;
   public static final int METHOD_COMPRESSED;
   public static final int METHOD_NO_DEFAULT;
   public static final int METHOD_FOLDER;
   public static final int METHOD_SCALE_TO_FIT;
   public static final int METHOD_CONVERT_TO_GREY;
   public static final int REMOVE_OK;
   public static final int REMOVE_LATER;
   public static final int REMOVE_FAILED;
   public static final int REMOVE_IGNORED;
   private static final int[] OS_ICONS = new int[]{0, 0, 0, 0};
   private static final int[] OS_ICONS_KEY_STATE;
   private static final int[] OS_ICONS_RADIO;

   Theme(Hashtable moduleDefaults, Hashtable defaultImageDescriptors, ResourceFetcher resourceFetcher) {
      this._moduleDefaults = moduleDefaults;
      this._defaultImageDescriptors = defaultImageDescriptors;
   }

   public final void $init0() {
      String moduleName = TraceBack.getCallingModuleName(0);
      this.addDefaultResources(moduleName);
   }

   private void setResourceBundle(ResourceBundle resourceBundle) {
      if (this._resourceBundle == null) {
         this._resourceBundle = resourceBundle;
      }
   }

   private synchronized void addDefaultResources(String moduleName) {
      if (!this._moduleDefaults.containsKey(moduleName)) {
         this._moduleDefaults.put(moduleName, this._moduleDefaults);
         DefaultResourceFetcher fetcher = new DefaultResourceFetcher(moduleName);
         this.addResources(fetcher, true);
      }
   }

   protected void addFont(ResourceFetcher resourceFetcher, String name) {
      byte[] data = resourceFetcher.fetchResource(name);
      int dot = name.lastIndexOf(46);
      String familyName = name.substring(0, dot);
      if (this._fonts.get(familyName) == null && this._fonts.get(familyName + "$_sf1") == null) {
         this._fonts.put(familyName, data);
      }
   }

   protected void addImage(ResourceFetcher resourceFetcher, String name, boolean isDefault) {
      Theme$ImageDescriptor descriptor = new Theme$ImageDescriptor(name, resourceFetcher, isDefault);
      if (isDefault) {
         this._defaultImageDescriptors.put(descriptor.getName(), descriptor);
      } else {
         boolean passed = this._themeLoadingCount == 0;
         boolean isIconCollection = false;
         int iconIndex = name.indexOf(ThemeConstants.ICONS_SUFFIX);
         if (iconIndex >= 0) {
            isIconCollection = true;
            String subName = name.substring(0, iconIndex);
            if (this._iconCollectionNames == null) {
               this._iconCollectionNames = new ToIntHashtable(100);
            }

            int themeIndex = this._iconCollectionNames.get(subName);
            if (themeIndex < 0 || themeIndex == this._themeLoadingCount) {
               passed = true;
            }

            if (themeIndex < 0) {
               this._iconCollectionNames.put(subName, this._themeLoadingCount);
            }
         }

         if (!passed && !isIconCollection) {
            Theme$ImageDescriptor extant = (Theme$ImageDescriptor)this._themeImageDescriptors.get(descriptor.getName());
            if (extant == null) {
               passed = true;
            }
         }

         if (passed) {
            this._themeImageDescriptors.put(descriptor.getName(), descriptor);
         }
      }
   }

   synchronized void addResources(ResourceFetcher resourceFetcher, boolean isDefault) {
      Enumeration enumeration = resourceFetcher.listResources();

      while (enumeration.hasMoreElements()) {
         String name = (String)enumeration.nextElement();
         if (name.indexOf(47) == -1) {
            if (name.endsWith(ThemeConstants.EXT_GIF) || name.endsWith(ThemeConstants.EXT_PNG)) {
               this.addImage(resourceFetcher, name, isDefault);
            }

            if (name.endsWith(ThemeConstants.EXT_CBTF) || name.endsWith(ThemeConstants.EXT_SFF4)) {
               this.addFont(resourceFetcher, name);
            }

            if (name.endsWith(ThemeConstants.EXT_MID)) {
               this.addTune(resourceFetcher, name);
            }
         }
      }
   }

   protected void addTune(ResourceFetcher resourceFetcher, String name) {
      byte[] data = resourceFetcher.fetchResource(name);
      int dot = name.lastIndexOf(46);
      String tuneName = name.substring(0, dot);
      if (tuneName.startsWith(ThemeConstants.RINGTONE_PREFIX)) {
         String ringtone = tuneName.substring(ThemeConstants.RINGTONE_PREFIX.length());
         ringtone = ringtone.replace('_', ' ');
         this._ringtones.put(ringtone, data);
      }
   }

   public synchronized void apply() {
      this.applyIconCollections();
      String prefix = "osicon_";
      Enumeration descriptors = this._themeImageDescriptors.elements();

      while (descriptors.hasMoreElements()) {
         Theme$ImageDescriptor descriptor = (Theme$ImageDescriptor)descriptors.nextElement();
         String name = descriptor.getName();
         this.applyUiIcon(name, descriptor);
         if (name.startsWith(prefix)) {
            this.applyOsIcon(name, descriptor);
         }
      }

      descriptors = this._defaultImageDescriptors.elements();

      while (descriptors.hasMoreElements()) {
         Theme$ImageDescriptor descriptor = (Theme$ImageDescriptor)descriptors.nextElement();
         String name = descriptor.getName();
         this.applyUiIcon(name, descriptor);
         if (name.startsWith(prefix)) {
            this.applyOsIcon(name, descriptor);
         }
      }

      this.applyOsIcons(OS_ICONS, true);
      this.applyKeyStateIcons(this._isKeyStateIconsVisible);
      this.applyRadioIcons(this._isRadioIconsVisible);
      descriptors = this._fonts.keys();

      while (descriptors.hasMoreElements()) {
         String name = (String)descriptors.nextElement();
         String realName = name;
         int dash = name.indexOf("$_sf");
         if (dash != -1) {
            realName = name.substring(0, dash);
         }

         int handle = FontRegistry.loadFont((byte[])this._fonts.get(name), realName, false);
         if (handle >> 16 == -2) {
            String originalName = FontRegistry.getTypefaceName(handle & 0xFF);
            handle = FontRegistry.loadFont((byte[])this._fonts.get(name), originalName, false);
            if (this._registeredFontNames == null) {
               this._registeredFontNames = new Hashtable(10);
            }

            this._registeredFontNames.put(name, originalName);
         }

         if (handle >= 0) {
            Arrays.add(this._fontHandles, handle);
         }
      }

      this.applyFont();
   }

   synchronized String getRegisteredFontNameHack(String name) {
      String registeredName = (String)this._registeredFontNames.get(name);
      return registeredName != null ? registeredName : name;
   }

   synchronized void applyFont() {
      Enumeration enumeration = this._attributeSets.elements();

      while (enumeration.hasMoreElements()) {
         ThemeAttributeSet attributes = (ThemeAttributeSet)enumeration.nextElement();
         attributes.apply();
      }
   }

   private void applyIconCollections() {
      ThemeManager.clearIconCollections();
      Enumeration collections = ThemeManager.enumerateIconCollections();

      while (collections.hasMoreElements()) {
         NamedIconCollection collection = (NamedIconCollection)collections.nextElement();
         this.initializeIconCollectionHelper(collection, this._defaultImageDescriptors);
         this.initializeIconCollectionHelper(collection, this._themeImageDescriptors);
      }
   }

   public synchronized void initializeFastReset() {
      this.applyOsIcons(OS_ICONS, true);
      this.applyKeyStateIcons(this._isKeyStateIconsVisible);
      this.applyRadioIcons(this._isRadioIconsVisible);
   }

   public synchronized void applyKeyStateIcons(boolean visible) {
      this._isKeyStateIconsVisible = visible;
      this.applyOsIcons(OS_ICONS_KEY_STATE, visible);

      for (int lv = OS_ICONS_KEY_STATE.length - 1; lv >= 0; lv--) {
         int iconIndex = OS_ICONS_KEY_STATE[lv];
         EncodedImage image = this._osIcons[iconIndex];
         if (image != null) {
            this._osKeyIconWidth = Math.max(this._osKeyIconWidth, image.getWidth());
         }
      }
   }

   public synchronized void applyRadioIcons(boolean visible) {
      this._isRadioIconsVisible = visible;
      this.applyOsIcons(OS_ICONS_RADIO, visible);
   }

   private void applyOsIcons(int[] icons, boolean visible) {
      if (!visible) {
         byte[] imageData = Resource.getResourceClass().getResource("empty.gif");
         EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);

         for (int lv = icons.length - 1; lv >= 0; lv--) {
            UiInternal.setThemeIcon(icons[lv], image);
         }
      } else {
         for (int lv = icons.length - 1; lv >= 0; lv--) {
            int iconIndex = icons[lv];
            EncodedImage image = this._osIcons[iconIndex];
            if (image != null) {
               UiInternal.setThemeIcon(iconIndex, image);
            } else {
               UiInternal.setThemeIconToDefault(iconIndex);
            }
         }
      }
   }

   private void applyOsIcon(String name, Theme$ImageDescriptor descriptor) {
      int id = -1;
      if (name.startsWith("osicon_hourglass")) {
         id = 0;
      } else if (name.startsWith("osicon_txrach")) {
         id = 7;
      } else if (name.startsWith("osicon_txrx")) {
         id = 3;
      } else if (name.startsWith("osicon_tx")) {
         id = 1;
      } else if (name.startsWith("osicon_rx")) {
         id = 2;
      } else if (name.startsWith("osicon_num")) {
         id = 4;
      } else if (name.startsWith("osicon_alt")) {
         id = 5;
      } else if (name.startsWith("osicon_cap")) {
         id = 6;
      } else if (name.startsWith("osicon_multitap")) {
         id = 8;
      }

      if (id != -1 && this._osIcons[id] == null) {
         this._osIcons[id] = descriptor.getImage();
      }
   }

   private void applyUiIcon(String name, Theme$ImageDescriptor descriptor) {
      int id = -1;
      if (name.equals(ThemeConstants.NAVIGATION_UP_ARROW)) {
         id = 0;
      } else if (name.equals(ThemeConstants.NAVIGATION_DOWN_ARROW)) {
         id = 1;
      } else if (name.equals(ThemeConstants.SCROLLBAR_ELEVATOR)) {
         id = 2;
      } else if (name.equals(ThemeConstants.SCROLLBAR_INDICATOR)) {
         id = 3;
      }

      if (id != -1 && this._themeBitmaps[id] == null) {
         this._themeBitmaps[id] = descriptor.getImage().getBitmap();
      }
   }

   protected void calcIconSize(EncodedImage image) {
      if (!this._ribbonIconSizeSet) {
         if (image.getWidth() > this._ribbonIconWidth) {
            this._ribbonIconWidth = image.getWidth();
         }

         if (image.getHeight() > this._ribbonIconHeight) {
            this._ribbonIconHeight = image.getHeight();
         }
      }
   }

   void clearAppIconCache() {
      this._appIcons.clear();
   }

   void decrementThemeLoadingCount() {
      this._themeLoadingCount--;
      if (this._iconCollectionNames != null) {
         this._iconCollectionNames = null;
      }
   }

   public void dispose() {
      for (int lv = this._themeBitmaps.length - 1; lv >= 0; lv--) {
         this._themeBitmaps[lv] = null;
      }

      this._themeBitmaps = null;
      this._attributeSets.clear();
      this._themeImageDescriptors.clear();
      this._themeImageDescriptors = null;
      this._defaultImageDescriptors = null;
      this._moduleDefaults = null;
      this._borders.clear();
      this._borders = null;

      for (int lv = this._osIcons.length - 1; lv >= 0; lv--) {
         this._osIcons[lv] = null;
      }

      this._osIcons = null;
      this._fonts = null;
      this._registeredFontNames = null;
      this._ringtones = null;
   }

   public void disposeFonts() {
      for (int lv = this._fontHandles.length - 1; lv >= 0; lv--) {
         FontRegistry.unloadFont(this._fontHandles[lv]);
      }

      this._fontHandles = null;
   }

   boolean freeStaleObject(int priority) {
      boolean ret = false;
      Enumeration enumeration = this._attributeSets.elements();

      while (enumeration.hasMoreElements()) {
         ThemeAttributeSet tas = (ThemeAttributeSet)enumeration.nextElement();
         ret = tas.freeStaleObject(priority) || ret;
      }

      return ret;
   }

   private String getNameWithState(String name, int state) {
      StringBuffer _buffer = StringUtilitiesInternal.getScratchBuffer();
      synchronized (_buffer) {
         _buffer.append(name);
         _buffer.append('~');
         _buffer.append(this.getNameForState(state));
         String nameWithState = _buffer.toString();
         _buffer.setLength(0);
         return nameWithState;
      }
   }

   private String getNameWithAppState(String name, String appState) {
      if (appState == null) {
         return name;
      }

      StringBuffer _buffer = StringUtilitiesInternal.getScratchBuffer();
      synchronized (_buffer) {
         _buffer.append(name);
         _buffer.append('.');
         _buffer.append(appState);
         String nameWithAppState = _buffer.toString();
         _buffer.setLength(0);
         return nameWithAppState;
      }
   }

   public String getString(int id) {
      return this._resourceBundle.getString(id);
   }

   public String getOption(String key) {
      return (String)this._options.get(key);
   }

   public Image getApplicationIcon(String name, int state, int size, Image defaultValue, int method) {
      return this.getApplicationIcon(name, null, state, size, defaultValue, method);
   }

   public Image getApplicationIcon(String name, String appState, int state, int size, Image defaultValue, int method) {
      String nameWithAppState = this.getNameWithAppState(name, appState);
      String nameWithState = nameWithAppState;
      boolean cacheIcon = true;
      if (this._appIconSize != size && size != 0 && (method & 8) == 0) {
         this._appIconSize = size;
         this._appIcons.clear();
      }

      int sizeForKey = (method & 24) != 0 ? size : 0;
      if ((method & 16) != 0) {
         sizeForKey = -sizeForKey;
      }

      Theme$AppIconKey key = new Theme$AppIconKey(nameWithState, state, sizeForKey);
      Image cached = (Image)this._appIcons.get(key);
      if (cached != null) {
         return cached;
      }

      if (state != 0) {
         nameWithState = this.getNameWithState(nameWithAppState, state);
      }

      EncodedImage image = this.getImage(nameWithState, true);
      Image overlay = null;
      if (image == null && appState != null) {
         if (state != 0) {
            nameWithState = this.getNameWithState(name, state);
         } else {
            nameWithState = name;
         }

         image = this.getImage(nameWithState, true);
         if (image != null && appState.equals("new")) {
            overlay = this.getApplicationIcon("new_overlay", 0, size, null, method | 2);
         }
      }

      if (image == null) {
         do {
            int index = name.lastIndexOf(46);
            if (index < 0) {
               break;
            }

            name = name.substring(0, index);
            nameWithAppState = this.getNameWithAppState(name, appState);
            if (state != 0) {
               nameWithState = this.getNameWithState(nameWithAppState, state);
            } else {
               nameWithState = nameWithAppState;
            }

            image = this.getImage(nameWithState, true);
            if (image == null && appState != null) {
               if (state != 0) {
                  nameWithState = this.getNameWithState(name, state);
               } else {
                  nameWithState = name;
               }

               image = this.getImage(nameWithState, true);
               if (image != null && appState.equals("new")) {
                  overlay = this.getApplicationIcon("new_overlay", 0, size, null, method | 2);
               }
            }
         } while (image == null);
      }

      Image result;
      if (image == null) {
         if (defaultValue == null && (method & 2) == 0) {
            if ((method & 4) == 0) {
               nameWithState = "default_application";
            } else {
               nameWithState = "default_folder";
            }

            if (state != 0) {
               nameWithState = this.getNameWithState(nameWithState, state);
            }

            try {
               result = ImageEncoded.create(this.getImage(nameWithState));
            } catch (IllegalArgumentException e) {
               result = defaultValue;
            }
         } else {
            result = defaultValue;
            cacheIcon = false;
         }

         if (result != null && (method & 16) != 0) {
            Bitmap resultBmp = Bitmap.createGreyscaleBitmap(result, this._ribbonIconWidth, this._ribbonIconHeight);
            result = ImageBitmap.create(resultBmp);
         }
      } else {
         if ((method & 8) != 0) {
            Bitmap bitmap = image.getBitmap();
            bitmap = ScaleBitmap.scaleBitmap(0, bitmap, size, size * bitmap.getHeight() / bitmap.getWidth());
            if ((method & 16) != 0) {
               bitmap = Bitmap.createGreyscaleBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
            }

            result = ImageBitmap.create(bitmap);
         } else if ((method & 16) != 0) {
            Bitmap imageBmp = image.getBitmap();
            Bitmap bitmap = Bitmap.createGreyscaleBitmap(imageBmp, imageBmp.getWidth(), imageBmp.getHeight());
            result = ImageBitmap.create(bitmap);
         } else if ((method & 1) != 0) {
            result = ImageEncoded.create(image);
         } else {
            result = ImageBitmap.create(image.getBitmap());
         }

         if (overlay != null) {
            result = ImageOverlay.create(result, overlay);
         }
      }

      if (result != null && cacheIcon) {
         this._appIcons.put(key, result);
      }

      return result;
   }

   public ThemeAttributeSet getAttributeSet(Tag tag) {
      if (tag == null) {
         return null;
      }

      long lkey = this.getKey(tag.hashCode(), null, 0);
      return (ThemeAttributeSet)this._attributeSets.get(lkey);
   }

   public ThemeAttributeSet getAttributeSet(Tag tag, String idname, int state) {
      return this.getAttributeSet(tag, idname, state, false);
   }

   public ThemeAttributeSet getAttributeSet(Tag tag, String idname, int state, boolean allowsNull) {
      if (tag == null) {
         return null;
      }

      ThemeAttributeSet attributes = null;

      while (true) {
         long key = this.getKey(tag.hashCode(), idname, state);
         attributes = (ThemeAttributeSet)this._attributeSets.get(key);
         if (allowsNull || attributes != null) {
            break;
         }

         if (state != 0) {
            state = 0;
         } else if (idname != null) {
            idname = null;
         } else {
            if (tag.hashCode() == 0) {
               break;
            }

            tag = Tag.get(null);
         }
      }

      return attributes;
   }

   public ThemeAttributeSet getAttributeSet(Field element) {
      return !this._attributeSets.isEmpty() && element != null ? this.getAttributeSet(element.getTag(), element.getId(), element.getState()) : null;
   }

   public ThemeAttributeSet getAttributeSet(Field element, int state) {
      return !this._attributeSets.isEmpty() && element != null ? this.getAttributeSet(element.getTag(), element.getId(), state) : null;
   }

   public Bitmap getBitmap(String name) {
      Bitmap bitmap = null;
      EncodedImage image = this.getImage(name);
      if (image != null) {
         bitmap = image.getBitmap();
      }

      return bitmap;
   }

   public int getColor(String name) {
      int color = this._palette.get(name);
      if (color == -1 && !this._palette.containsKey(name)) {
         throw new IllegalArgumentException("Unknown color: " + name);
      } else {
         return color;
      }
   }

   public String getIdleScreenName() {
      return this._idleScreenName;
   }

   public String getThumbnailName() {
      return this._thumbnailName;
   }

   public EncodedImage getImage(String name) {
      String moduleName = TraceBack.getCallingModuleName(0);
      return this.getImage(name, moduleName, false);
   }

   public EncodedImage getImage(String name, boolean allowNull) {
      String moduleName = TraceBack.getCallingModuleName(0);
      return this.getImage(name, moduleName, allowNull);
   }

   public EncodedImage getImage(String name, String moduleName, boolean allowNull) {
      Theme$ImageDescriptor descriptor = (Theme$ImageDescriptor)this._themeImageDescriptors.get(name);
      if (descriptor == null) {
         String alias = (String)this._aliasList.get(name);
         if (alias != null) {
            descriptor = (Theme$ImageDescriptor)this._themeImageDescriptors.get(alias);
         }
      }

      if (descriptor == null) {
         descriptor = (Theme$ImageDescriptor)this._defaultImageDescriptors.get(name);
      }

      if (descriptor == null) {
         this.addDefaultResources(moduleName);
         descriptor = (Theme$ImageDescriptor)this._defaultImageDescriptors.get(name);
         if (descriptor == null) {
            if (allowNull) {
               return null;
            }

            throw new IllegalArgumentException("Cannot find image: " + name);
         }
      }

      return descriptor.getImage();
   }

   private long getKey(int tag, String idname, int state) {
      if (idname == null && state == 0) {
         return tag;
      }

      long key = tag | state << 24;
      if (idname != null) {
         key |= (long)idname.hashCode() << 32;
      }

      return key;
   }

   public Manager getLayout(String name, Object context) {
      Manager manager = null;
      if (name.equals("empty")) {
         return new VerticalFieldManager();
      }

      if (this._layoutFactory != null) {
         manager = this._layoutFactory.getLayout(name, context);
      }

      if (manager == null) {
         manager = ThemeManager.getLayout(name, context);
      }

      return manager;
   }

   public int getRibbonIconHeight() {
      return this._ribbonIconHeight;
   }

   public int getRibbonIconWidth() {
      return this._ribbonIconWidth;
   }

   public Border getBorder(String name) {
      return (Border)this._borders.get(name);
   }

   public Bitmap[] getScrollbar(String name) {
      return (Bitmap[])this._scrollbars.get(name);
   }

   public int getBorderStyle() {
      return this._borderStyle;
   }

   public Enumeration ringtones() {
      return this._ringtones.keys();
   }

   public byte[] getRingtone(String name) {
      return name == null ? null : (byte[])this._ringtones.get(name);
   }

   public int getWidthOfKeyStateIcons() {
      return this._osKeyIconWidth;
   }

   Theme$Writer getWriter() {
      return this._writer;
   }

   public Theme$Writer getWriterInternalDeprecated() {
      return this._writer;
   }

   private String getNameForState(int state) {
      switch (state) {
         case -1:
            throw new IllegalArgumentException("Illegal state");
         case 0:
         default:
            return "";
         case 1:
            return "first-child";
         case 2:
            return "link";
         case 3:
            return "visited";
         case 4:
            return "active";
         case 5:
            return "hover";
         case 6:
            return "focus";
         case 7:
            return "disabled";
         case 8:
            return "disabled-focus";
      }
   }

   public static Bitmap getThemeBitmap(int type) {
      if (type >= 0 && type < 4) {
         return ThemeManager.getActiveTheme()._themeBitmaps[type];
      } else {
         throw new IllegalArgumentException();
      }
   }

   public WeakReference getWeakReference() {
      return this._weakReference;
   }

   void incrementThemeLoadingCount() {
      this._themeLoadingCount++;
   }

   void initializeIconCollection(NamedIconCollection collection, String moduleName) {
      if (!collection.isDefaultSet()) {
         DefaultResourceFetcher fetcher = new DefaultResourceFetcher(moduleName);
         this.addResources(fetcher, true);
         this.initializeIconCollectionHelper(collection, this._defaultImageDescriptors);
         this.initializeIconCollectionHelper(collection, this._themeImageDescriptors);
         if (!collection.isDefaultSet()) {
            throw new IllegalStateException("Default icon collection must be provided in calling module.");
         }
      }
   }

   void initializeIconCollectionHelper(NamedIconCollection collection, Hashtable descriptors) {
      String collectionName = collection.getName();
      Enumeration keys = descriptors.elements();

      while (keys.hasMoreElements()) {
         Theme$ImageDescriptor descriptor = (Theme$ImageDescriptor)keys.nextElement();
         String name = descriptor.getName();
         if (name.startsWith(collectionName)) {
            int iconsSuffixPos = name.indexOf(ThemeConstants.ICONS_SUFFIX);
            if (iconsSuffixPos == collectionName.length() && name.length() > iconsSuffixPos + ThemeConstants.ICONS_SUFFIX.length()) {
               EncodedImage image = descriptor.getImage();
               int xindex = name.indexOf(120, iconsSuffixPos + ThemeConstants.ICONS_SUFFIX.length());
               int width = Integer.parseInt(name.substring(iconsSuffixPos + ThemeConstants.ICONS_SUFFIX.length(), xindex));
               int height = Integer.parseInt(name.substring(xindex + 1));
               collection.addImage(image, width, height, descriptor.isDefault());
            }
         }
      }
   }

   public boolean isLabelOnOwnLine() {
      return this._isLabelOnOwnLine;
   }

   static {
      int[] keyIcons = new int[]{4, 5, 6, -804651004, 7, 3, 1, 2, 51, 4408146, 4801362, 5391186};
      if (InputContext.getInstance(false).hasSureType()) {
         Arrays.add(keyIcons, 8);
      }

      OS_ICONS_KEY_STATE = keyIcons;
      OS_ICONS_RADIO = new int[]{
         7, 3, 1, 2, 51, 4408146, 4801362, 5391186, 5526098, 1867317504, 13919606, 1829528321, 424025994, 16781677, -1972564893, 2036419615
      };
   }
}
