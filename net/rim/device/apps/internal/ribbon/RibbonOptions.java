package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.api.options.OptionsBase;

public final class RibbonOptions extends OptionsBase {
   private RibbonOptions$PersistedRibbonOptions _persistedRibbonOptions;
   private static final long RIBBON_OPTIONS_SYNC_ITEM;
   private static final long PERSISTED_RIBBON_OPTIONS;
   public static final long NEW_OPTIONS_SYNCED;
   public static final long EVENT_LOGGER_GUID;
   private static RibbonOptions _options;

   private RibbonOptions() {
   }

   public static final RibbonOptions getOptions() {
      if (_options == null) {
         _options = new RibbonOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(2160146156658040375L);
      synchronized (persistentObject) {
         this._persistedRibbonOptions = (RibbonOptions$PersistedRibbonOptions)persistentObject.getContents();
         if (this._persistedRibbonOptions == null) {
            this._persistedRibbonOptions = new RibbonOptions$PersistedRibbonOptions();
            persistentObject.setContents(this._persistedRibbonOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      SyncItem syncItem = (SyncItem)ar.getOrWaitFor(293775760836449343L);
      if (syncItem == null) {
         syncItem = new RibbonOptions$RibbonOptionsSyncItem();
         ar.put(293775760836449343L, syncItem);
      }

      return syncItem;
   }

   final void resetToDefaults() {
      this._persistedRibbonOptions.resetToDefaults();
      this.commit();
   }

   public final int getPriority(String applicationName, int defaultPriority) {
      ToIntHashtable priorities = this._persistedRibbonOptions._applicationPriorities;
      if (priorities.containsKey(applicationName)) {
         return priorities.get(applicationName);
      }

      if (priorities.contains(defaultPriority)) {
         while (priorities.contains(++defaultPriority)) {
         }

         this.setPriority(applicationName, defaultPriority);
         this.commit();
      }

      return defaultPriority;
   }

   public final void setPriority(String applicationName, int newPriority) {
      this.setTableValue(this._persistedRibbonOptions._applicationPriorities, applicationName, newPriority);
   }

   public final boolean isVisible(String applicationName, boolean defaultVisibility) {
      ToIntHashtable visibilities = this._persistedRibbonOptions._applicationVisibilities;
      return visibilities.containsKey(applicationName) ? visibilities.get(applicationName) == 1 : defaultVisibility;
   }

   public final void setVisible(String applicationName, boolean newVisibility) {
      this.setTableValue(this._persistedRibbonOptions._applicationVisibilities, applicationName, newVisibility ? 1 : 0);
   }

   public final boolean getShowHiddenApps() {
      return this._persistedRibbonOptions._showHiddenApps;
   }

   public final void setShowHiddenApps(boolean showHiddenApps) {
      if (this._persistedRibbonOptions._showHiddenApps != showHiddenApps) {
         this._persistedRibbonOptions._showHiddenApps = showHiddenApps;
         this.commit();
      }
   }

   final ToIntHashtable getPriorities() {
      return this._persistedRibbonOptions._applicationPriorities;
   }

   final ToIntHashtable getVisibilities() {
      return this._persistedRibbonOptions._applicationVisibilities;
   }

   private final void setTableValue(ToIntHashtable table, String key, int newValue) {
      if (key.indexOf(32) != -1 || key.indexOf(818) != -1) {
         StringBuffer buffer = (StringBuffer)(new Object());
         int length = key.length();

         for (int i = 0; i < length; i++) {
            char c = key.charAt(i);
            if (c != ' ' && c != 818) {
               buffer.append(c);
            }
         }

         key = buffer.toString();
      }

      if (!table.containsKey(key) || table.get(key) != newValue) {
         synchronized (this._persistedRibbonOptions) {
            table.put(key, newValue);
            this.commit();
         }
      }
   }

   public final BackgroundImage getBackgroundImage(String themeName) {
      synchronized (this._persistedRibbonOptions._backgroundImages) {
         BackgroundImage image = null;
         if (themeName != null && themeName.length() > 0) {
            image = (BackgroundImage)this._persistedRibbonOptions._backgroundImages.get(themeName);
         }

         return image;
      }
   }

   public final void setBackgroundImage(String themeName, String imageName, String[] imageProperties) {
      synchronized (this._persistedRibbonOptions._backgroundImages) {
         if (themeName != null && themeName.length() > 0) {
            BackgroundImage image = new BackgroundImage();
            image._name = imageName;
            image._properties = imageProperties;
            this._persistedRibbonOptions._backgroundImages.put(themeName, image);
            this.commit();
         }
      }
   }
}
