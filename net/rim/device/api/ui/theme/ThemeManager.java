package net.rim.device.api.ui.theme;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ImageThemed;
import net.rim.device.internal.ui.NamedIconCollection;
import net.rim.device.internal.ui.UiOptionsRegistry;
import net.rim.device.internal.ui.UiSettings;
import net.rim.device.internal.ui.UiThemeManager;
import net.rim.vm.TraceBack;

public class ThemeManager {
   private Theme$Factory[] _factories = new Theme$Factory[0];
   private Theme$LayoutFactory[] _layoutFactories = new Theme$LayoutFactory[0];
   private String _activeThemeName;
   private Theme _activeTheme;
   private boolean _activated;
   private Theme$Factory _defaultThemeFactory;
   private int _generation = 1;
   private Hashtable _iconCollections = new Hashtable(60);
   private int _nextTagId = 2;
   private IntHashtable _intToTag = new IntHashtable(80);
   private Hashtable _nameToTag = new Hashtable(80);
   private ThemeManager$Listeners _listeners = new ThemeManager$Listeners();
   private Hashtable _moduleDefaults = new Hashtable(10);
   private Hashtable _defaultImageDescriptors = new Hashtable(200);
   private final Comparator _comparatorFactoryFactory = new ThemeManager$1(this);
   private final Comparator _comparatorStringFactory = new ThemeManager$2(this);
   public static final long GUID_THEME_CHANGED;
   public static final long GUID_THEME_ADDED;
   public static final long GUID_THEME_RESET;
   public static final int THEME_CHANGED_FROM_ACTION;
   public static final int THEME_CHANGED_FROM_RESTORE;
   public static final Tag ATTRIBUTE_INHERIT = new Tag(null, 0);
   private static final Tag ATTRIBUTE_ROOT = new Tag("", 1);
   private static final long GUID;
   private static ThemeManager _instance;

   protected ThemeManager() {
      this.$initColors();
      this._nameToTag.put(ATTRIBUTE_ROOT.toString(), ATTRIBUTE_ROOT);
      this._intToTag.put(ATTRIBUTE_ROOT.hashCode(), ATTRIBUTE_ROOT);
      this.verifyActiveTheme();
      this._defaultThemeFactory = new ThemeManager$DefaultThemeFactory();
      this.addInternal(this._defaultThemeFactory);
      DefaultResourceFetcher fetcher = new DefaultResourceFetcher();
      fetcher.setResourcesFromModule(TraceBack.getCallingModuleName(0));
      this._activeTheme = new Theme(this._moduleDefaults, this._defaultImageDescriptors, fetcher);
      UiSettings.addListener(this._listeners);
      LowMemoryManager.addLowMemoryListener(this._listeners);
      Proxy.getInstance().addGlobalEventListener(this._listeners);
   }

   public static void activateTheme() {
      String saved = UiOptionsRegistry.getInstance().getString(-7276267599751932452L);
      if (saved != null) {
         try {
            setActiveTheme(saved);
         } catch (IllegalStateException var3) {
         }
      }

      if (!_instance._activated) {
         try {
            setActiveTheme(_instance.getDefaultId());
         } catch (IllegalStateException var2) {
         }
      }

      if (!_instance._activated) {
         Theme theme = createTheme(_instance._defaultThemeFactory);
         _instance._activeTheme = theme;
      }
   }

   public static void add(Theme$Factory factory) {
      ApplicationControl.assertThemeDataAllowed(CodeModuleManager.getModuleHandleForObject(factory));
      _instance.addInternal(factory);
   }

   private void addInternal(Theme$Factory factory) {
      synchronized (this) {
         if (_instance != null && getThemeFactory(factory.getName()) != null) {
            throw new IllegalStateException("Theme already present");
         }

         Arrays.add(this._factories, factory);
         Arrays.sort(this._factories, this._comparatorFactoryFactory);
      }

      RIMGlobalMessagePoster.postGlobalEvent(9057101852544553212L);
   }

   public static void addLayoutFactory(Theme$LayoutFactory factory) {
      synchronized (_instance) {
         Arrays.add(_instance._layoutFactories, factory);
      }
   }

   public static void clearIconCollections() {
      Enumeration elements = _instance._iconCollections.elements();

      while (elements.hasMoreElements()) {
         IconCollection collection = (IconCollection)elements.nextElement();
         collection.clear();
      }
   }

   public static Theme createTheme(Theme$Factory factory) {
      Theme theme = new Theme(_instance._moduleDefaults, _instance._defaultImageDescriptors, factory.getResourceFetcher());
      theme.$init0();
      createThemeHelper(factory, theme);
      return theme;
   }

   public static void createThemeHelper(Theme$Factory factory, Theme theme) {
      if (factory != null) {
         String parent = factory.getParent();
         Theme$Writer writer = theme.getWriterInternalDeprecated();
         writer.setResourceFetcher(factory.getResourceFetcher());
         factory.populate(writer);
         writer.setThumbnailName(null);
         theme.incrementThemeLoadingCount();
         if (parent != null) {
            String id = getPersistableIdForName(parent);
            if (id == null) {
               throw new IllegalStateException("Unable to find parent");
            }

            Theme$Factory parentFactory = getThemeFactory(id);
            createThemeHelper(parentFactory, theme);
         }

         theme.decrementThemeLoadingCount();
      }
   }

   static Enumeration enumerateIconCollections() {
      return _instance._iconCollections.elements();
   }

   public static String getActiveName() {
      return _instance._activeThemeName;
   }

   public static Theme getActiveTheme() {
      return _instance._activeTheme;
   }

   public static int getCount() {
      return _instance._factories.length;
   }

   private Theme$Factory getBrandingTheme() {
      for (int lv = _instance._factories.length - 1; lv >= 0; lv--) {
         Theme$Factory factory = _instance._factories[lv];
         if (factory.isVendorIDValid(Branding.getVendorId())) {
            return factory;
         }
      }

      return null;
   }

   public static String getNameOfDefaulTheme() {
      return _instance.getDefaultId();
   }

   private String getDefaultId() {
      int priority = Integer.MAX_VALUE;
      Theme$Factory defaultTheme = this.getBrandingTheme();
      if (defaultTheme != null) {
         return getPersistableId(defaultTheme);
      }

      Theme$Factory specialCaseDefault = null;
      int displayDepth = log2(Graphics.getNumColors());
      int displayWidth = Display.getWidth();
      int displayHeight = Display.getHeight();

      for (int lv = _instance._factories.length - 1; lv >= 0; lv--) {
         Theme$Factory factory = _instance._factories[lv];
         if (factory.getName().equals("Blackberry_icon_240x260") && DeviceInfo.getDeviceName().equals("7100i")) {
            specialCaseDefault = factory;
         }

         if (factory.getPriority() < priority) {
            int colorDepth = factory.getTargetDisplayColorDepth();
            if (colorDepth == 0 && displayDepth > 1 || colorDepth == displayDepth) {
               int width = factory.getTargetDisplayWidth();
               int height = factory.getTargetDisplayHeight();
               if ((width == 0 || width == displayWidth) && (height == 0 || height == displayHeight)) {
                  priority = factory.getPriority();
                  defaultTheme = factory;
               }
            }
         }
      }

      if (specialCaseDefault != null) {
         defaultTheme = specialCaseDefault;
      }

      if (defaultTheme == null) {
         throw new IllegalStateException("Null theme not found.");
      } else {
         return getPersistableId(defaultTheme);
      }
   }

   private static int log2(int number) {
      number >>= 1;

      int log;
      for (log = 0; number != 0; log++) {
         number >>= 1;
      }

      return log;
   }

   public static String[] getNameChoices() {
      String[] result = new String[_instance._factories.length];

      for (int lv = result.length - 1; lv >= 0; lv--) {
         result[lv] = getPersistableIdForName(_instance._factories[lv].getName());
      }

      return result;
   }

   public static String[] getNameChoices(Locale locale) {
      String[] result = new String[_instance._factories.length];

      for (int lv = result.length - 1; lv >= 0; lv--) {
         result[lv] = _instance._factories[lv].getName(locale);
      }

      return result;
   }

   static synchronized IconCollection getIconCollection(String name) {
      return (IconCollection)_instance._iconCollections.get(name);
   }

   public static IconCollection getIconCollection(String name, int columns, int rows, String moduleName) {
      synchronized (_instance) {
         NamedIconCollection collection = (NamedIconCollection)_instance._iconCollections.get(name);
         if (collection == null) {
            collection = new NamedIconCollection(name, columns, rows, moduleName);
            getActiveTheme().initializeIconCollection(collection, moduleName);
            _instance._iconCollections.put(name, collection);
         } else {
            collection.verifyModule(moduleName);
         }

         return collection;
      }
   }

   private static int getIndex(String name) {
      if (name != null) {
         Theme$Factory[] factories = _instance._factories;

         for (int lv = factories.length - 1; lv >= 0; lv--) {
            String temp = getPersistableId(factories[lv]);
            if (name.equals(temp)) {
               return lv;
            }
         }
      }

      return -1;
   }

   public static Manager getLayout(String name, Object context) {
      Manager manager = null;
      Theme$LayoutFactory[] factories = _instance._layoutFactories;
      int last = factories.length;

      for (int lv = 0; lv < last && manager == null; lv++) {
         manager = factories[lv].getLayout(name, context);
      }

      return manager;
   }

   public static String getPersistableId(Theme$Factory factory) {
      String id = "java:/" + factory.getClass().getName();
      String ext = factory.getIdExtension();
      return ext == null ? id : id + "?ext=" + ext;
   }

   public static String getPersistableIdForName(String name) {
      int index = Arrays.binarySearch(_instance._factories, name, _instance._comparatorStringFactory, 0, _instance._factories.length);
      String id = null;
      if (index >= 0) {
         id = getPersistableId(_instance._factories[index]);
      }

      return id;
   }

   static Tag createTag(String tagName) {
      if (tagName == null) {
         return ATTRIBUTE_INHERIT;
      }

      for (int lv = tagName.length() - 1; lv >= 0; lv--) {
         char ch = tagName.charAt(lv);
         if (('a' > ch || ch > 'z') && ch != '-' && ch != '_' && ('0' > ch || ch > '9')) {
            throw new IllegalArgumentException();
         }
      }

      synchronized (_instance) {
         Tag tag = (Tag)_instance._nameToTag.get(tagName);
         if (tag == null) {
            int id = _instance._nextTagId++;
            tag = new Tag(tagName, id);
            _instance._nameToTag.put(tagName, tag);
            _instance._intToTag.put(id, tag);
         }

         return tag;
      }
   }

   static Tag getTag(String tagName) {
      return tagName == null ? ATTRIBUTE_INHERIT : (Tag)_instance._nameToTag.get(tagName);
   }

   public static Theme getTheme(String name) {
      Theme$Factory factory = getThemeFactory(name);
      return factory != null ? createTheme(factory) : null;
   }

   public static Image getThemeAwareImage(String name) {
      String moduleName = TraceBack.getCallingModuleName(0);
      return new ImageThemed(name, moduleName);
   }

   public static Theme$Factory getThemeFactory(String name) {
      if (name == null) {
         return null;
      }

      if (name.indexOf(58) == -1) {
         name = getPersistableIdForName(name);
      }

      int index = getIndex(name);
      return index != -1 ? _instance._factories[index] : null;
   }

   private void internalSetActiveTheme(Theme$Factory factory, boolean restore) {
      Theme theme = createTheme(factory);
      Theme oldTheme = this._activeTheme;
      this._activeThemeName = getPersistableId(factory);
      this._activeTheme = theme;
      this._generation++;
      UiOptionsRegistry.getInstance().setString(-7276267599751932452L, this._activeThemeName);
      if (oldTheme != null) {
         oldTheme.disposeFonts();
      }

      this._activeTheme.apply();
      if (!FontRegistry.isDefaultFontSet()) {
         resetDefaultFont();
      }

      UiOptionsRegistry.getInstance().setString(-3809895234519942708L, this._activeThemeName);
      RIMGlobalMessagePoster.postGlobalEvent(2573494863350550132L, restore ? 2 : 1, 0, null, null);
      if (oldTheme != null) {
         oldTheme.dispose();
      }
   }

   public static void resetDefaultFont() {
      Theme theme = getActiveTheme();
      ThemeAttributeSet defaultFontAttributes = theme.getAttributeSet(Tag.create("default-font"));
      if (defaultFontAttributes != null) {
         Font font = defaultFontAttributes.getFont();
         if (font != null) {
            Font.setDefaultFontForSystem(font);
         }
      }
   }

   private void $initColors() {
   }

   public static int getGeneration() {
      return _instance._generation;
   }

   public static Bitmap getPredefinedBitmap(int id) {
      Theme theme = _instance._activeTheme;
      switch (id) {
         case -1:
            return theme.getBitmap("dialog_information");
         case 0:
         default:
            return theme.getBitmap("dialog_information");
         case 1:
            return theme.getBitmap("dialog_question");
         case 2:
            return theme.getBitmap("dialog_exclamation");
         case 3:
            return theme.getBitmap("dialog_hourglass");
      }
   }

   public static boolean isActivatable(String name) {
      if (name.indexOf(58) == -1) {
         name = getPersistableIdForName(name);
      }

      synchronized (_instance) {
         int index = getIndex(name);
         if (index < 0) {
            throw new IllegalArgumentException();
         }

         Theme$Factory factory = _instance._factories[index];
         if (!factory.isActivatable()) {
            return false;
         }

         while (factory != null) {
            String parent = factory.getParent();
            if (parent != null && parent.length() != 0) {
               String id = getPersistableIdForName(parent);
               if (id == null) {
                  return false;
               }

               factory = getThemeFactory(id);
            } else {
               factory = null;
            }
         }

         return true;
      }
   }

   public static void onSystemFontChangeInternal() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      getActiveTheme().applyFont();
   }

   public static void remove(String name) {
      if (name.indexOf(58) == -1) {
         name = getPersistableIdForName(name);
      }

      synchronized (_instance) {
         int index = getIndex(name);
         if (index >= 0) {
            Theme$Factory factory = _instance._factories[index];
            if (!factory.isRemovable()) {
               throw new SecurityException();
            }

            boolean removingActive = _instance._defaultThemeFactory == factory;
            int rc = factory.remove();
            int message;
            if (rc == 0) {
               removeFromList(index, removingActive);
               message = 401;
            } else if (rc == 1) {
               removeFromList(index, removingActive);
               message = 402;
            } else {
               message = 403;
            }

            if (rc != 3) {
               UiApplication.getUiApplication().invokeLater(new ThemeManager$3(message));
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   private static void removeFromList(int index, boolean removingActive) {
      Arrays.removeAt(_instance._factories, index);
      if (removingActive) {
         activateTheme();
      }
   }

   public static boolean isRemoveable(String name) {
      if (name.indexOf(58) == -1) {
         name = getPersistableIdForName(name);
      }

      synchronized (_instance) {
         int index = getIndex(name);
         if (index >= 0) {
            Theme$Factory factory = _instance._factories[index];
            return factory.isRemovable();
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public static boolean allowUserWallpaper(String name) {
      if (name == null) {
         return true;
      }

      if (name.indexOf(58) == -1) {
         name = getPersistableIdForName(name);
      }

      synchronized (_instance) {
         int index = getIndex(name);
         if (index >= 0) {
            Theme$Factory factory = _instance._factories[index];
            return factory.allowUserWallpaper();
         } else {
            return true;
         }
      }
   }

   private static void setActiveTheme(String name, boolean restore) {
      if (name == null) {
         name = _instance.getDefaultId();
      }

      Theme$Factory factory = getThemeFactory(name);
      if (factory != null) {
         if (!factory.isActivatable()) {
            throw new IllegalArgumentException("Theme not activatable: " + name);
         }

         String newThemeName = getPersistableId(factory);
         if (!newThemeName.equals(_instance._activeThemeName)) {
            _instance.internalSetActiveTheme(factory, restore);
         }

         _instance._activated = true;
      }
   }

   public static void setActiveTheme(String name) {
      setActiveTheme(name, false);
   }

   private void verifyActiveTheme() {
      String active = UiOptionsRegistry.getInstance().getString(-7276267599751932452L);
      String fixed = active;
      if (active != null) {
         if (active.startsWith("java:/")) {
            try {
               int ext = active.indexOf(63);
               String clazz = ext == -1 ? active.substring(6) : active.substring(6, ext);
               Class.forName(clazz);
            } catch (Exception e) {
               fixed = null;
            }
         } else {
            fixed = null;
         }
      }

      if (!StringUtilities.strEqual(fixed, active)) {
         UiOptionsRegistry.getInstance().setString(-7276267599751932452L, fixed);
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (ThemeManager)ar.getOrWaitFor(-6338698576635425001L);
      if (_instance == null) {
         _instance = new UiThemeManager();
         ar.put(-6338698576635425001L, _instance);
      }
   }
}
