package net.rim.device.apps.internal.ribbon.launcher;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.Theme$Factory;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.ApplicationFolder;
import net.rim.device.apps.api.ribbon.ApplicationHierarchy;
import net.rim.device.apps.api.ribbon.ApplicationHierarchyProvider;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.apps.api.ribbon.ModuleApplication;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.apps.api.utility.props.BooleanProps;
import net.rim.device.apps.internal.ribbon.ApplicationMenu;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public final class HierarchyManager implements GlobalEventListener {
   private Manager _launcherField;
   private Hashtable _hierarchyCollection;
   private String _activeHierarchyName;
   private InternalApplicationHierarchy _activeHierarchy;
   private Hashtable _activeFolders;
   private Hashtable _temporaryApplications;
   private Hashtable _moduleInfo;
   private int _modulesDeletedCounter;
   private int _flagsID;
   private Hashtable _baseHierarchies;
   HierarchyManager$EntryChangeListener[] _entryChangeListeners = new HierarchyManager$EntryChangeListener[0];
   private static final int GRID = 0;
   private static final int STANDARD_VL = 1;
   private static final int MENU_VL = 2;
   private static final byte HIERARCHY_DATA_COMPLETE = 0;
   private static final byte HIERARCHY_NAME = 1;
   private static final byte HIERARCHY_DATA = 2;
   private static final int MODULE_DELETE_COUNT_BEFORE_PROPERTY_CLEAN = 10;
   private static final int FLAG_NO_SUB_FOLDERS = 1;
   private static final long HIERARCHY_MANAGER_GUID = -5218883541955797034L;
   private static final long HIERARCHY_COLLECTION_GUID = -1404412532621532483L;
   private static final long USER_HIERARCHY_GUID = -5359506686284008855L;
   public static final long ACTIVE_HIERARCHY_CHANGED_GUID = 3536078662966037734L;
   public static final long FLAGS_GUID = -7870816478501154318L;

   public final void moveToFolder(ApplicationEntry applicationEntry, String newFolder) {
      this.removeApplication(applicationEntry);
      String name = applicationEntry.getPropertiesName();
      ApplicationProperties properties = this._activeHierarchy.getApplicationProperties(name);
      if (properties == null) {
         if (applicationEntry.isDisabledByDefault()) {
            return;
         }

         properties = new InternalApplicationProperties();
         properties.setFolderName(this._activeHierarchy.getDefaultFolderName());
         HierarchyData hierarchyData = (HierarchyData)this._hierarchyCollection.get(this._activeHierarchyName);
         hierarchyData.setApplicationProperties(name, properties);
         properties.setPosition(applicationEntry.getPriority());
         properties.setVisible(applicationEntry.isVisible());
         properties.setCanHide(applicationEntry.canHide());
         properties.setDisabled(applicationEntry.isDisabledByDefault());
         properties.clearDirtyFlag();
      }

      if (!properties.getDisabled()) {
         ApplicationEntry[] apps = this.getAppsInFolder(newFolder);
         if (apps == null) {
            apps = this.getAppsInFolder("");
            newFolder = "";
         }

         if (apps != null) {
            int maxPriority = 0;

            for (int i = apps.length - 1; i >= 0; i--) {
               if (maxPriority < apps[i].getPriority()) {
                  maxPriority = apps[i].getPriority();
               }
            }

            properties.setPosition(maxPriority + 1);
         }

         properties.setFolderName(newFolder);
         applicationEntry.setApplicationProperties(properties);
         InternalApplicationFolder folder = (InternalApplicationFolder)this._activeFolders.get(newFolder);
         if (folder == null) {
            folder = (InternalApplicationFolder)this._activeFolders.get(this._activeHierarchy.getDefaultFolderName());
         }

         folder.addApplication(applicationEntry);
      }

      PersistentObject.commit(this._hierarchyCollection);
   }

   public final void resetHierarchies() {
      this._baseHierarchies.clear();
      this._hierarchyCollection.clear();
      this._activeFolders.clear();
      this.activateHierarchy(this._activeHierarchyName);
   }

   public final void resetActiveHierarchy() {
      this._hierarchyCollection.remove(this._activeHierarchyName);
      this._baseHierarchies.remove(this._activeHierarchyName);
      this._activeFolders.clear();
      this.activateHierarchy(this._activeHierarchyName);
   }

   public final synchronized void loadActiveHierarchy() {
      if (this._activeHierarchy != null) {
         this.locateApplications();
      } else {
         this.onThemeChangedEvent();
      }
   }

   final InternalApplicationHierarchy getActiveHierarchy() {
      return this._activeHierarchy;
   }

   public final Hashtable getActiveFolders() {
      return this._activeFolders;
   }

   public final ApplicationEntry getApplicationEntry(String propName) {
      ApplicationEntry entry = this.getTemporaryApplication(propName);
      if (entry != null) {
         return entry;
      }

      Hashtable folders = this.getActiveFolders();
      if (folders != null) {
         Enumeration enumeration = folders.keys();

         while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            ApplicationEntry[] entries = getInstance().getAppsInFolder(key);
            if (entries != null) {
               for (int i = entries.length - 1; i >= 0; i--) {
                  entry = entries[i];
                  if (entry.getPropertiesName().equals(propName)) {
                     return entry;
                  }
               }
            }
         }
      }

      return null;
   }

   public final void moduleDeleted() {
      this._moduleInfo.clear();
      this._modulesDeletedCounter++;
   }

   public final ApplicationLauncherField getLauncherField(String launcherID) {
      int switchingTo;
      if (StringUtilities.strEqual(launcherID, "GridAppChooser")) {
         switchingTo = 0;
      } else if (ApplicationMenu.containsApplicationMenu()) {
         switchingTo = 2;
      } else {
         switchingTo = 1;
      }

      int switchingFrom;
      if (this._launcherField instanceof GridApplicationLauncherField) {
         switchingFrom = 0;
      } else if (((VerticalApplicationLauncherField)this._launcherField).isForApplicationMenu()) {
         switchingFrom = 2;
      } else {
         switchingFrom = 1;
      }

      switch (switchingTo) {
         case -1:
            break;
         case 0:
         default:
            switch (switchingFrom) {
               case -1:
                  return (ApplicationLauncherField)this._launcherField;
               case 0:
               default:
                  return (ApplicationLauncherField)this._launcherField;
               case 1:
                  this.unhookAndClearField(this._launcherField);
               case 2:
                  GridApplicationLauncherField lf = new GridApplicationLauncherField(this);
                  this._launcherField = lf;
                  return lf;
            }
         case 1:
            switch (switchingFrom) {
               case -1:
                  return (ApplicationLauncherField)this._launcherField;
               case 0:
                  this.unhookAndClearField(this._launcherField);
               case 2:
                  VerticalApplicationLauncherField lf = new VerticalApplicationLauncherField(this, false);
                  this._launcherField = lf;
                  return lf;
               case 1:
               default:
                  return (ApplicationLauncherField)this._launcherField;
            }
         case 2:
            switch (switchingFrom) {
               case -1:
                  break;
               case 0:
               case 1:
               default:
                  this.unhookAndClearField(this._launcherField);
               case 2:
                  VerticalApplicationLauncherField lf = new VerticalApplicationLauncherField(this, true);
                  this._launcherField = lf;
                  return lf;
            }
      }

      return (ApplicationLauncherField)this._launcherField;
   }

   protected final ApplicationLauncherField getApplicationLauncher() {
      return (ApplicationLauncherField)this._launcherField;
   }

   public final void onThemeChangedEvent() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         String themeName = ThemeManager.getActiveName();
         if (themeName == null) {
            themeName = "";
         }

         if (!StringUtilities.strEqual(this._activeHierarchyName, themeName)) {
            this.activateHierarchy(themeName);
         }

         ApplicationMenu.dismiss();
      }
   }

   public final ApplicationEntry[] getAppsInFolder(String key) {
      InternalApplicationFolder folder = (InternalApplicationFolder)this._activeFolders.get(key);
      return folder != null ? folder.getApplications() : null;
   }

   public final ApplicationEntry getAppByIndexInFolder(String key, int index) {
      InternalApplicationFolder folder = (InternalApplicationFolder)this._activeFolders.get(key);
      return folder != null ? folder.getApplicationByIndex(index) : null;
   }

   final ApplicationEntry getApplicationByHotKey(char key) {
      if (key == 0) {
         return null;
      }

      Hashtable folders = this.getActiveFolders();
      ApplicationEntry hiddenApplication = null;
      if (folders != null) {
         Enumeration enumeration = folders.keys();

         while (enumeration.hasMoreElements()) {
            String folderKey = (String)enumeration.nextElement();
            ApplicationEntry[] entries = getInstance().getAppsInFolder(folderKey);
            if (entries != null) {
               for (int i = entries.length - 1; i >= 0; i--) {
                  ApplicationEntry entry = entries[i];
                  if (key == entry.getHotKey()) {
                     if (entry.isVisible()) {
                        return entry;
                     }

                     if (hiddenApplication == null) {
                        hiddenApplication = entry;
                     }
                  }
               }
            }
         }
      }

      return hiddenApplication;
   }

   public final void setApplicationEntryPointProperty(String uniqueName, long propertyId, Object property) {
      Hashtable folders = this.getActiveFolders();
      if (folders != null) {
         Enumeration enumeration = folders.keys();

         while (enumeration.hasMoreElements()) {
            String folderKey = (String)enumeration.nextElement();
            ApplicationEntry[] entries = getInstance().getAppsInFolder(folderKey);
            if (entries != null) {
               for (int i = entries.length - 1; i >= 0; i--) {
                  ApplicationEntry entry = entries[i];
                  if (entry.getUniqueName().equals(uniqueName)) {
                     Object descriptor = entry.getDescriptor();
                     if (descriptor instanceof BooleanProps && property instanceof Boolean) {
                        ((BooleanProps)descriptor).set(propertyId, (Boolean)property);
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   public final void showApplication(ApplicationEntry application, boolean show) {
      application.setVisible(show);
      PersistentObject.commit(this._hierarchyCollection);
   }

   public final synchronized void registerTemporaryApplication(String name, ApplicationEntry application) {
      this.unregisterTemporaryApplication(name);
      this._temporaryApplications.put(name, application);
      this.addApplication(application, false);
   }

   public final synchronized boolean unregisterTemporaryApplication(String name) {
      ApplicationEntry application = (ApplicationEntry)this._temporaryApplications.get(name);
      if (application != null) {
         this._temporaryApplications.remove(name);
         this.removeApplication(application);
         return true;
      } else {
         return false;
      }
   }

   public final void refreshTemporaryApplication(String name) {
      ApplicationEntry application = (ApplicationEntry)this._temporaryApplications.get(name);
      if (application != null) {
         application.refreshBitmap();
         application.clearDescription();
         ((ApplicationLauncherField)this._launcherField).refreshApplication(application);
         this.fireOnEntryChange(application);
      }
   }

   public final ApplicationEntry getTemporaryApplication(String name) {
      return (ApplicationEntry)this._temporaryApplications.get(name);
   }

   public final void resetApplicationDescriptions() {
      if (this._activeFolders != null) {
         Enumeration enumeration = this._activeFolders.elements();

         while (enumeration.hasMoreElements()) {
            InternalApplicationFolder folder = (InternalApplicationFolder)enumeration.nextElement();
            ApplicationEntry[] apps = folder.getApplications();

            for (int i = apps.length - 1; i >= 0; i--) {
               apps[i].clearDescription();
            }
         }
      }
   }

   public final void moveApplication(ApplicationEntry application, int fromIndex, int toIndex) {
      ApplicationLauncherField appLauncher = this.getApplicationLauncher();
      if (fromIndex < toIndex) {
         appLauncher.getActiveFolder().moveApplication(application, fromIndex, toIndex, appLauncher.getApplicationAt(toIndex - 1));
      } else if (fromIndex > toIndex) {
         appLauncher.getActiveFolder().moveApplication(application, fromIndex, toIndex, appLauncher.getApplicationAt(toIndex + 1));
      }

      PersistentObject.commit(this._hierarchyCollection);
   }

   public final void setHotKeysDisabled(boolean disableHotKeys) {
      if (this._activeHierarchy != null) {
         this._activeHierarchy.setDisableHotKeys(disableHotKeys);
         this.getApplicationLauncher().setHotKeysDisabled(disableHotKeys);
      }

      this.resetApplicationDescriptions();
   }

   public final boolean getDisableHotKeys() {
      return this._activeHierarchy != null ? this._activeHierarchy.getDisableHotKeys() : false;
   }

   final InternalApplicationFolder getFolder(String folderName) {
      return (InternalApplicationFolder)this._activeFolders.get(folderName);
   }

   public final synchronized void newOptionsSyncing() {
      Enumeration keys = this._hierarchyCollection.keys();

      while (keys.hasMoreElements()) {
         String name = (String)keys.nextElement();
         HierarchyData data = (HierarchyData)this._hierarchyCollection.get(name);
         data.clearApplicationProperties();
         data.clearFolders();
      }

      this._baseHierarchies.clear();
      this._activeHierarchy = null;
      this._activeHierarchyName = null;
      this._moduleInfo.clear();
   }

   public final void writeHierarchyData(DataBuffer buffer) {
      synchronized (this._hierarchyCollection) {
         Enumeration keys = this._hierarchyCollection.keys();

         while (keys.hasMoreElements()) {
            String name = (String)keys.nextElement();
            HierarchyData data = (HierarchyData)this._hierarchyCollection.get(name);
            DataBuffer tmpBuffer = new DataBuffer();
            tmpBuffer.setBigEndian(buffer.isBigEndian());
            ConverterUtilities.writeStringSmart(buffer, 1, name);
            data.writeHierarchyData(tmpBuffer);
            tmpBuffer.trim();
            ConverterUtilities.writeByteArray(buffer, 2, tmpBuffer.getArray());
         }

         ConverterUtilities.writeEmptyField(buffer, 0);
      }
   }

   public final void readHierarchyData(DataBuffer buffer) {
      synchronized (this._hierarchyCollection) {
         label42:
         try {
            String name = null;

            label39:
            while (!buffer.eof()) {
               switch (ConverterUtilities.getType(buffer, true)) {
                  case -1:
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 0:
                  default:
                     ConverterUtilities.skipField(buffer);
                     break label39;
                  case 1:
                     name = ConverterUtilities.readString(buffer);
                     break;
                  case 2:
                     HierarchyData data = new HierarchyData();
                     buffer.skipBytes(3);
                     data.readHierarchyData(buffer, this.getBaseHierarchy(name));
                     this._hierarchyCollection.put(name, data);
                     name = null;
               }
            }
         } finally {
            break label42;
         }

         PersistentObject.commit(this._hierarchyCollection);
      }
   }

   public final boolean movingIconsAllowed() {
      return this._activeHierarchy != null && this._activeHierarchy.getAllowMoveIcons() && this.getApplicationLauncher().getActiveFolder().getAllowMoveIcons();
   }

   public final boolean hidingIconsAllowed() {
      return this._activeHierarchy != null && this._activeHierarchy.getAllowHideIcons() && this.getApplicationLauncher().hidingIconsAllowed();
   }

   public final void toggleSubFolders() {
      if (this.isFlagSet(1)) {
         this.clearFlag(1);
      } else {
         this.setFlag(1);
      }

      this.loadActiveHierarchy();
   }

   public final boolean folderNameExists(String param1, String param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/ribbon/launcher/HierarchyManager._activeFolders Ljava/util/Hashtable;
      // 04: aload 2
      // 05: invokevirtual java/util/Hashtable.containsKey (Ljava/lang/Object;)Z
      // 08: ifne 80
      // 0b: aload 0
      // 0c: getfield net/rim/device/apps/internal/ribbon/launcher/HierarchyManager._activeFolders Ljava/util/Hashtable;
      // 0f: invokevirtual java/util/Hashtable.elements ()Ljava/util/Enumeration;
      // 12: astore 3
      // 13: aload 3
      // 14: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 19: ifeq 7e
      // 1c: aload 3
      // 1d: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 22: astore 4
      // 24: aload 4
      // 26: dup
      // 27: instanceof net/rim/device/apps/internal/ribbon/launcher/InternalApplicationFolder
      // 2a: ifne 31
      // 2d: pop
      // 2e: goto 13
      // 31: checkcast net/rim/device/apps/internal/ribbon/launcher/InternalApplicationFolder
      // 34: astore 5
      // 36: aload 5
      // 38: invokevirtual net/rim/device/apps/internal/ribbon/launcher/InternalApplicationFolder.getResourceId ()I
      // 3b: istore 6
      // 3d: aload 5
      // 3f: invokevirtual net/rim/device/apps/internal/ribbon/launcher/InternalApplicationFolder.getResourceBundleName ()Ljava/lang/String;
      // 42: astore 7
      // 44: iload 6
      // 46: ifle 13
      // 49: aload 7
      // 4b: invokevirtual java/lang/String.length ()I
      // 4e: ifle 13
      // 51: aload 1
      // 52: aload 5
      // 54: invokevirtual net/rim/device/apps/internal/ribbon/launcher/InternalApplicationFolder.getName ()Ljava/lang/String;
      // 57: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 5a: ifne 13
      // 5d: aload 7
      // 5f: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (Ljava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 62: iload 6
      // 64: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 67: astore 8
      // 69: aload 2
      // 6a: aload 8
      // 6c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 6f: ifeq 13
      // 72: bipush 1
      // 73: ireturn
      // 74: astore 8
      // 76: goto 13
      // 79: astore 8
      // 7b: goto 13
      // 7e: bipush 0
      // 7f: ireturn
      // 80: bipush 1
      // 81: ireturn
      // try (29 -> 49): 50 null
      // try (29 -> 49): 52 null
   }

   public final void addEntryChangeListener(HierarchyManager$EntryChangeListener changeListener) {
      if (changeListener == null) {
         throw new NullPointerException();
      }

      HierarchyManager$EntryChangeListener[] changeListeners = this._entryChangeListeners;
      synchronized (changeListeners) {
         int len = changeListeners.length;
         Array.resize(changeListeners, len + 1);
         changeListeners[len] = changeListener;
      }
   }

   public final void fireOnEntryChange(ApplicationEntry entry) {
      HierarchyManager$EntryChangeListener[] changeListeners = this._entryChangeListeners;
      synchronized (changeListeners) {
         int count = changeListeners.length;

         for (int i = 0; i < count; i++) {
            changeListeners[i].onEntryChange(entry);
         }
      }
   }

   public final RibbonIconField addFolder(String name, int position, String imageName) {
      ApplicationFolder newFolder = this._activeHierarchy.addFolder(name);
      if (newFolder == null) {
         return null;
      }

      RibbonIconField newField = null;
      InternalApplicationFolder internalFolder = new InternalApplicationFolder(name, newFolder);
      this.addFolder(internalFolder);
      ApplicationEntry folderEntry = new ApplicationEntry(
         new FolderEntryPointDescriptor(internalFolder, internalFolder.getResourceBundleName(), internalFolder.getResourceId()), false
      );
      this.addApplication(folderEntry, true);
      this.updateIconImage(folderEntry, imageName);
      ApplicationLauncherField appLauncher = this.getApplicationLauncher();
      appLauncher.deleteAllApplications();
      appLauncher.loadApplications();
      appLauncher.setFocus(folderEntry);
      Manager manager = (Manager)appLauncher;
      newField = folderEntry.getRibbonIcon();
      if (position >= 0) {
         int currentPosition = ((Manager)appLauncher).getFieldWithFocusIndex();
         manager.delete(newField);
         manager.insert(newField, position);
         this.moveApplication(folderEntry, currentPosition, position);
         appLauncher.setFocus(folderEntry);
      }

      manager.invalidate();
      PersistentObject.commit(this._hierarchyCollection);
      return newField;
   }

   public final void deleteFolder(ApplicationEntry entry, String folder) {
      FolderEntryPointDescriptor desc = (FolderEntryPointDescriptor)entry.getDescriptor();
      String id = desc.get(1, "");
      ApplicationFolder[] folders = this._activeHierarchy.getFolders();
      ApplicationEntry[] apps = this.getAppsInFolder(id);
      ApplicationEntry[] newApps = new ApplicationEntry[0];
      Manager manager = (Manager)this.getApplicationLauncher();
      if (apps != null) {
         Arrays.append(newApps, apps);
         int length = newApps.length;

         for (int i = 0; i < length; i++) {
            this.moveToFolder(newApps[i], folder);
         }
      }

      for (int i = folders.length - 1; i >= 0; i--) {
         if (folders[i].getName().equals(id)) {
            this._activeHierarchy.deleteFolder(folders[i]);
            this.removeApplication(entry);
            this._activeFolders.remove(id);
            manager.delete(entry.getRibbonIcon());
            if (folder.length() == 0) {
               int length = newApps.length;

               for (int j = 0; j < length; j++) {
                  manager.add(newApps[j].getRibbonIcon());
               }
            }

            PersistentObject.commit(this._hierarchyCollection);
            return;
         }
      }
   }

   public final RibbonIconField renameFolder(ApplicationEntry folderEntry, String newFolderName, int position, String imageName) {
      FolderEntryPointDescriptor desc = (FolderEntryPointDescriptor)folderEntry.getDescriptor();
      String oldFolderName = desc.get(1, "");
      if (oldFolderName.equals(newFolderName)) {
         this.updateIconImage(folderEntry, imageName);
         this._activeHierarchy.commit();
         return folderEntry.getRibbonIcon();
      } else {
         RibbonIconField field = this.addFolder(newFolderName, position, imageName);
         this.deleteFolder(folderEntry, newFolderName);
         this._activeHierarchy.commit();
         return field;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4645495483836102462L) {
         this.resetActiveHierarchy();
      }
   }

   private final void addFolder(InternalApplicationFolder folder) {
      String name = folder.getName();
      if (!this.isFlagSet(1) || name == InternalApplicationFolder.ROOT_FOLDER_NAME) {
         if (this._activeFolders.get(name) == null) {
            this._activeFolders.put(name, folder);
         }
      }
   }

   private final void processHierarchyFolders() {
      ApplicationFolder[] folders = this._activeHierarchy.getFolders();

      for (int i = 0; i < folders.length; i++) {
         InternalApplicationFolder folder = new InternalApplicationFolder(folders[i]);
         this.addFolder(folder);
      }
   }

   private final void processHierarchyApplications() {
      Enumeration folders = this._activeFolders.elements();

      while (folders.hasMoreElements()) {
         InternalApplicationFolder folder = (InternalApplicationFolder)folders.nextElement();
         if (folder.getName().length() != 0) {
            FolderEntryPointDescriptor folderDescriptor = new FolderEntryPointDescriptor(folder, folder.getResourceBundleName(), folder.getResourceId());
            ApplicationEntry application = new ApplicationEntry(folderDescriptor, false);
            this.addApplication(application, true);
         }
      }

      if (this._activeHierarchy != null) {
         ModuleApplication[] modules = this._activeHierarchy.getModules();

         for (int i = 0; i < modules.length; i++) {
            ModuleApplication module = modules[i];
            ModuleEntryPointDescriptor moduleDescriptor = new ModuleEntryPointDescriptor(
               module.getName(), module.getLaunchString(), module.getResourceBundleName(), module.getResourceId()
            );
            ApplicationEntry application = new ApplicationEntry(moduleDescriptor, false);
            this.addApplication(application, false);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processLoadedApplications() {
      int[] handles = CodeModuleManager.getModuleHandles();

      for (int i = handles.length - 1; i >= 0; i--) {
         int handle = handles[i];

         String moduleName;
         try {
            if ((CodeModuleManager.getModuleFlags(handle) & 1) != 0) {
               continue;
            }

            moduleName = CodeModuleManager.getModuleName(handle);
            if (moduleName == null) {
               System.out.println("Module " + handle + " does not have a module name");
               continue;
            }
         } finally {
            continue;
         }

         HierarchyManager$ModuleInfo moduleInfo = (HierarchyManager$ModuleInfo)this._moduleInfo.get(moduleName);
         boolean refreshRequired = true;
         byte[] moduleHash = null;
         boolean var12 = false /* VF: Semaphore variable */;

         try {
            var12 = true;
            if (moduleInfo != null) {
               if (moduleInfo._timestamp == CodeModuleManager.getModuleTimestamp(handle)) {
                  moduleHash = CodeModuleManager.getModuleHash(handle);
                  if (this.byteArraysEqual(moduleHash, moduleInfo._hash)) {
                     refreshRequired = false;
                  }
               }
            } else {
               moduleInfo = new HierarchyManager$ModuleInfo();
               this._moduleInfo.put(moduleName, moduleInfo);
            }

            if (refreshRequired) {
               moduleInfo._timestamp = CodeModuleManager.getModuleTimestamp(handle);
               if (moduleHash == null) {
                  moduleInfo._hash = CodeModuleManager.getModuleHash(handle);
               } else {
                  moduleInfo._hash = moduleHash;
               }

               moduleInfo._applications = this.getModuleApplications(handle);
               var12 = false;
            } else {
               var12 = false;
            }
         } finally {
            if (var12) {
               this._moduleInfo.remove(moduleName);
               continue;
            }
         }

         ApplicationEntry[] applications = moduleInfo._applications;
         if (applications != null) {
            for (int j = applications.length - 1; j >= 0; j--) {
               this.addApplication(applications[j], false);
            }
         }
      }
   }

   private final void processTemporaryApplications() {
      Enumeration temporaryApplications = this._temporaryApplications.elements();

      while (temporaryApplications.hasMoreElements()) {
         ApplicationEntry application = (ApplicationEntry)temporaryApplications.nextElement();
         this.addApplication(application, false);
      }
   }

   private final ApplicationEntry[] getModuleApplications(int handle) {
      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(handle);
      ApplicationEntry[] applications = null;
      if (descriptors != null) {
         boolean hotKeysDisabled = false;
         if (this._activeHierarchy != null) {
            hotKeysDisabled = this._activeHierarchy.getDisableHotKeys();
         }

         for (int i = descriptors.length - 1; i >= 0; i--) {
            ApplicationDescriptor descriptor = descriptors[i];
            if ((descriptor.getFlags() & 2) == 0) {
               ApplicationEntry application = new ApplicationEntry(new ApplicationEntryPoint(descriptor), hotKeysDisabled);
               RibbonApiProxy rap = (RibbonApiProxy)RibbonApi.getInstance();
               if (rap != null) {
                  rap.applyTo(handle, application);
               }

               if (applications == null) {
                  applications = new ApplicationEntry[0];
               }

               Arrays.add(applications, application);
            }
         }
      }

      return applications;
   }

   private final void addApplication(ApplicationEntry application, boolean isFolder) {
      if (this._activeHierarchy != null) {
         String name = application.getPropertiesName();
         ApplicationProperties properties = this._activeHierarchy.getApplicationProperties(name);
         if (properties == null) {
            String themeName = ThemeManager.getActiveName();
            if (themeName != null) {
               ApplicationHierarchy hierarchy = this.getBaseHierarchy(themeName);
               ApplicationProperties themeProperties = hierarchy.getApplicationProperties(name);
               if (themeProperties != null) {
                  properties = this._activeHierarchy.createDefaultProperties(name);
                  properties.setPosition(themeProperties.getPosition());
                  properties.setVisible(themeProperties.getVisible());
                  properties.setCanHide(themeProperties.canHide());
                  properties.setDisabled(themeProperties.getDisabled());
                  properties.setFolderName(themeProperties.getFolderName());
                  properties.clearDirtyFlag();
                  properties.setDescriptionResource(themeProperties.getBundleName(), themeProperties.getResourceId());
               }
            }
         }

         if (properties == null) {
            if (application.isDisabledByDefault()) {
               return;
            }

            properties = this._activeHierarchy.createDefaultProperties(name);
            properties.setPosition(application.getPriority());
            properties.setVisible(application.isVisible());
            properties.setCanHide(application.canHide());
            properties.clearDirtyFlag();
            if (isFolder) {
               properties.setFolderName(InternalApplicationFolder.ROOT_FOLDER_NAME);
            }
         }

         if (!properties.getDisabled()) {
            String folderName;
            if (this.isFlagSet(1)) {
               folderName = InternalApplicationFolder.ROOT_FOLDER_NAME;
            } else {
               folderName = properties.getFolderName();
            }

            InternalApplicationFolder folder = (InternalApplicationFolder)this._activeFolders.get(folderName);
            if (folder == null) {
               folder = (InternalApplicationFolder)this._activeFolders.get(this._activeHierarchy.getDefaultFolderName());
            }

            application.setApplicationProperties(properties);
            if (folder != null) {
               folder.addApplication(application);
            }
         }
      }
   }

   private final void removeApplication(ApplicationEntry application) {
      boolean removed = false;
      if (this._activeHierarchy != null) {
         String name = application.getPropertiesName();
         ApplicationProperties properties = this._activeHierarchy.getApplicationProperties(name);
         if (properties != null) {
            String folderName = properties.getFolderName();
            InternalApplicationFolder folder = (InternalApplicationFolder)this._activeFolders.get(folderName);
            if (folder != null) {
               removed = folder.removeApplication(application);
            }
         }
      }

      if (!removed) {
         Hashtable folders = this._activeFolders;
         if (folders != null) {
            Enumeration enumeration = folders.keys();

            while (enumeration.hasMoreElements()) {
               String key = (String)enumeration.nextElement();
               InternalApplicationFolder folder = (InternalApplicationFolder)folders.get(key);
               if (folder != null) {
                  folder.removeApplication(application);
               }
            }
         }
      }
   }

   public static final boolean isApplicationMenuValid(int menuType) {
      return ApplicationMenu.isApplicationMenuValid(menuType);
   }

   private final void unhookAndClearField(Manager field) {
      Manager manager = field.getManager();
      if (manager != null) {
         manager.delete(field);
      }

      if (!(field instanceof ApplicationLauncherField)) {
         field.deleteAll();
      } else {
         ((ApplicationLauncherField)field).deleteAllApplications();
      }
   }

   private final synchronized void locateApplications() {
      this._activeFolders = new Hashtable();
      if (this._modulesDeletedCounter >= 10) {
         this.clearDefaultProperties();
      }

      this.processHierarchyFolders();
      this.processHierarchyApplications();
      this.processLoadedApplications();
      this.processTemporaryApplications();
      ApplicationEntry activeApplication = this.getApplicationLauncher().getSelectedApplication();
      ApplicationLauncherField appLauncher = this.getApplicationLauncher();
      appLauncher.clearFolderStack();
      appLauncher.setActiveFolder((InternalApplicationFolder)this._activeFolders.get(InternalApplicationFolder.ROOT_FOLDER_NAME));
      appLauncher.setFocus(activeApplication);
   }

   public static final HierarchyManager getInstance() {
      synchronized (RIMPersistentStore.getPersistentObject(-1404412532621532483L)) {
         ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
         HierarchyManager hierarchyManager = (HierarchyManager)appRegistry.get(-5218883541955797034L);
         if (hierarchyManager == null) {
            hierarchyManager = new HierarchyManager();
            appRegistry.put(-5218883541955797034L, hierarchyManager);
         }

         return hierarchyManager;
      }
   }

   private final void resetTemporaryApplicationProperties() {
      Enumeration temporaryApplications = this._temporaryApplications.elements();

      while (temporaryApplications.hasMoreElements()) {
         ApplicationEntry application = (ApplicationEntry)temporaryApplications.nextElement();
         application.setApplicationProperties(null);
      }
   }

   public static final boolean isApplicationMenuValid() {
      return ApplicationMenu.isApplicationMenuValid(0) || ApplicationMenu.isApplicationMenuValid(1);
   }

   private final void updateIconImage(ApplicationEntry entry, String imageName) {
      String name = entry.getPropertiesName();
      ApplicationProperties properties = this._activeHierarchy.getApplicationProperties(name);
      if (properties != null) {
         properties.setCustomImageName(imageName);
         properties.setCustomFocusImageName(imageName);
         ((FolderEntryPointDescriptor)entry.getDescriptor()).resetIcon();
      }
   }

   private final ApplicationHierarchy getBaseHierarchy(String name) {
      ApplicationHierarchy hierarchy = (ApplicationHierarchy)this._baseHierarchies.get(name);
      if (hierarchy != null) {
         return hierarchy;
      }

      for (Theme$Factory factory = ThemeManager.getThemeFactory(name); factory != null; factory = ThemeManager.getThemeFactory(factory.getParent())) {
         if (factory instanceof ApplicationHierarchyProvider) {
            ApplicationHierarchyProvider provider = (ApplicationHierarchyProvider)factory;
            hierarchy = provider.getApplicationHierarchyInfo();
            break;
         }
      }

      if (hierarchy == null) {
         hierarchy = new DefaultApplicationHierarchy(name);
      }

      this._baseHierarchies.put(name, hierarchy);
      return hierarchy;
   }

   private final synchronized void activateHierarchy(String name) {
      ApplicationHierarchy baseHierarchy = this.getBaseHierarchy(name);
      HierarchyData hierarchyData = (HierarchyData)this._hierarchyCollection.get(name);
      if (hierarchyData == null) {
         hierarchyData = new HierarchyData();
         this._hierarchyCollection.put(name, hierarchyData);
         PersistentObject.commit(this._hierarchyCollection);
      }

      this._activeHierarchy = new InternalApplicationHierarchy(baseHierarchy, hierarchyData);
      this._activeHierarchyName = name;
      this._moduleInfo.clear();
      this.resetTemporaryApplicationProperties();
      this.getApplicationLauncher().setHotKeysDisabled(this._activeHierarchy.getDisableHotKeys());
      this.clearDefaultProperties();
      this.locateApplications();
      this.getApplicationLauncher().resetFocusToTop();
      this.resetApplicationDescriptions();
      RIMGlobalMessagePoster.postGlobalEvent(3536078662966037734L);
      PersistentObject.commit(this._hierarchyCollection);
   }

   private final void clearDefaultProperties() {
      this._activeHierarchy.clearDefaultProperties();
      this._modulesDeletedCounter = 0;
   }

   private HierarchyManager() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1404412532621532483L);
      this._hierarchyCollection = (Hashtable)persistentObject.getContents();
      if (this._hierarchyCollection == null) {
         this._hierarchyCollection = new Hashtable();
         persistentObject.setContents(this._hierarchyCollection, 51);
         persistentObject.commit();
      }

      this._flagsID = PersistentInteger.getId(-7870816478501154318L, 0);
      this._launcherField = new GridApplicationLauncherField(this);
      this._temporaryApplications = new Hashtable();
      this._baseHierarchies = new Hashtable();
      this._moduleInfo = new Hashtable(CodeModuleManager.getModuleHandles().length * 4 / 3 + 1);
      Application.getApplication().addGlobalEventListener(this);
   }

   private final boolean isFlagSet(int flag) {
      return (PersistentInteger.get(this._flagsID) & flag) != 0;
   }

   private final void setFlag(int flag) {
      PersistentInteger.set(this._flagsID, PersistentInteger.get(this._flagsID) | flag);
   }

   private final void clearFlag(int flag) {
      PersistentInteger.set(this._flagsID, PersistentInteger.get(this._flagsID) & ~flag);
   }

   private final boolean byteArraysEqual(byte[] array1, byte[] array2) {
      if (array1 == null && array2 == null) {
         return true;
      }

      if (array1 != null && array2 != null) {
         int length1 = array1.length;
         int length2 = array2.length;
         if (length1 != length2) {
            return false;
         }

         for (int i = length1 - 1; i >= 0; i--) {
            if (array1[i] != array2[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
