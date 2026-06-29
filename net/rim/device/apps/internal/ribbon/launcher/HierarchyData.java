package net.rim.device.apps.internal.ribbon.launcher;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.ribbon.ApplicationHierarchy;
import net.rim.device.apps.api.ribbon.ApplicationProperties;

final class HierarchyData implements Persistable {
   private String[] _folders;
   private Hashtable _properties = (Hashtable)(new Object());
   private String[] _deletedThemeFolders;
   private boolean _disableHotKeys;
   private static final int END_OF_DATA = 0;
   private static final int APPLICATION_PROPERTY_KEY = 1;
   private static final int APPLICATION_PROPERTY = 2;
   private static final int FOLDER = 3;
   private static final int DELETED_THEME_FOLDER = 4;

   HierarchyData() {
      this._folders = new Object[0];
      this._deletedThemeFolders = new Object[0];
   }

   final boolean getDisableHotKeys() {
      return this._disableHotKeys;
   }

   final void setDisableHotKeys(boolean disableHotKeys) {
      if (this._disableHotKeys != disableHotKeys) {
         this._disableHotKeys = disableHotKeys;
         PersistentObject.commit(this);
      }
   }

   final void clearApplicationProperties() {
      this._properties.clear();
   }

   final void setApplicationProperties(String name, ApplicationProperties properties) {
      this._properties.put(name, properties);
   }

   final ApplicationProperties getApplicationProperties(String name) {
      return (ApplicationProperties)this._properties.get(name);
   }

   final synchronized void clearFolders() {
      this._folders = new Object[0];
   }

   final String[] getFolders() {
      return this._folders;
   }

   final String[] getDeletedThemeFolders() {
      return this._deletedThemeFolders;
   }

   final boolean addFolder(String folder) {
      boolean result = false;
      if (!Arrays.contains(this._folders, folder)) {
         Arrays.add(this._folders, folder);
         result = true;
      }

      return result;
   }

   final void removeFolder(String folder) {
      Arrays.remove(this._folders, folder);
      this._properties.remove(folder);
      if (!Arrays.contains(this._folders, folder) && !Arrays.contains(this._deletedThemeFolders, folder)) {
         Arrays.add(this._deletedThemeFolders, folder);
      }
   }

   final synchronized void writeHierarchyData(DataBuffer buffer) {
      Enumeration apps = this._properties.keys();

      while (apps.hasMoreElements()) {
         String key = (String)apps.nextElement();
         InternalApplicationProperties properties = (InternalApplicationProperties)this._properties.get(key);
         if (properties.getDirtyFlag()) {
            ConverterUtilities.writeStringSmart(buffer, 1, key);
            ConverterUtilities.writeEmptyField(buffer, 2);
            properties.writeData(buffer);
         }
      }

      for (int i = 0; i < this._folders.length; i++) {
         ConverterUtilities.writeStringSmart(buffer, 3, this._folders[i]);
      }

      for (int i = 0; i < this._deletedThemeFolders.length; i++) {
         ConverterUtilities.writeStringSmart(buffer, 4, this._deletedThemeFolders[i]);
      }

      ConverterUtilities.writeEmptyField(buffer, 0);
   }

   final synchronized void readHierarchyData(DataBuffer buffer, ApplicationHierarchy baseHierarchy) {
      try {
         this._properties.clear();
         String name = null;
         String folder = null;
         this._folders = new Object[0];
         this._deletedThemeFolders = new Object[0];

         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case -1:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 0:
               default:
                  ConverterUtilities.skipField(buffer);
                  return;
               case 1:
                  name = ConverterUtilities.readString(buffer);
                  name = ApplicationEntry.removeControlCharacters(name);
                  break;
               case 2:
                  ApplicationProperties properties = baseHierarchy.getApplicationProperties(name);
                  InternalApplicationProperties internalProperties = new InternalApplicationProperties(properties);
                  ConverterUtilities.skipField(buffer);
                  internalProperties.readData(buffer);
                  ApplicationEntry appEntry = HierarchyManager.getInstance().getApplicationEntry(name);
                  if (appEntry != null && !appEntry.canHide()) {
                     internalProperties.setCanHide(false);
                     internalProperties.setVisible(true);
                  }

                  this._properties.put(name, internalProperties);
                  name = null;
                  break;
               case 3:
                  folder = ConverterUtilities.readString(buffer);
                  Arrays.add(this._folders, folder);
                  break;
               case 4:
                  folder = ConverterUtilities.readString(buffer);
                  Arrays.add(this._deletedThemeFolders, folder);
            }
         }
      } finally {
         return;
      }
   }

   final synchronized void clearDefaultProperties() {
      Enumeration enumeration = this._properties.keys();

      while (enumeration.hasMoreElements()) {
         String key = (String)enumeration.nextElement();
         ApplicationProperties properties = this.getApplicationProperties(key);
         if (properties != null && !properties.getDirtyFlag()) {
            this._properties.remove(key);
         }
      }
   }
}
