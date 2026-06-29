package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.options.OptionsBase;

public final class ExplorerOptions extends OptionsBase {
   private ExplorerOptions$PersistedExplorerOptions _persistedExplorerOptions = (ExplorerOptions$PersistedExplorerOptions)this.getPersistentObject()
      .getContents();
   private static final long EXPLORER_OPTIONS_SYNC_ITEM = -4805530286105058278L;
   private static final long PERSISTED_EXPLORER_OPTIONS = -7371350987134899973L;
   public static final int MIN_NUMBER_OF_COLUMNS = Display.getWidth() / 100;
   public static final int MAX_NUMBER_OF_COLUMNS = Display.getWidth() / 60;
   public static final int DEFAULT_NUMBER_OF_COLUMNS = Display.getWidth() / 100;
   public static final int FILELIST_SORT_PROPERTY_DATE = 0;
   public static final int FILELIST_SORT_PROPERTY_FILENAME = 1;
   public static final int FILELIST_SORT_DIRECTION_ASCENDING = 0;
   public static final int FILELIST_SORT_DIRECTION_DESCENDING = 1;
   public static final int DEFAULT_SORT_PROPERTY = 0;
   public static final int DEFAULT_SORT_DIRECTION = 1;
   public static final int OPTION_SLIDE_SHOW_DISPLAY_TIME = 1;
   public static final int OPTION_VIEW_MODE = 2;
   public static final int OPTION_NUMBER_OF_COLUMNS = 3;
   public static final int OPTION_SORT_PROPERTY = 4;
   public static final int OPTION_SORT_DIRECTION = 5;
   public static final int MIN_SLIDESHOW_TIME = 1;
   public static final int MAX_SLIDESHOW_TIME = 15;
   private static ExplorerOptions _options;

   private ExplorerOptions() {
   }

   public static final ExplorerOptions getOptions() {
      if (_options == null) {
         _options = new ExplorerOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-7371350987134899973L);
      synchronized (persistentObject) {
         Object persistedExplorerOptions = persistentObject.getContents();
         if (persistedExplorerOptions == null) {
            persistedExplorerOptions = new ExplorerOptions$PersistedExplorerOptions();
            persistentObject.setContents(persistedExplorerOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-4805530286105058278L);
         if (syncItem == null) {
            syncItem = new ExplorerOptions$ExplorerOptionsSyncItem();
            ar.put(-4805530286105058278L, syncItem);
         }

         return syncItem;
      }
   }

   public final int getSlideShowDisplayTime() {
      return this._persistedExplorerOptions._slideShowDisplayTime;
   }

   public final int getNumberOfColumns() {
      return this._persistedExplorerOptions._numberOfColumns;
   }

   public final int getFilelistSortDirection() {
      return this._persistedExplorerOptions._filelistSortDirection;
   }

   public final int getFilelistSortProperty() {
      return this._persistedExplorerOptions._filelistSortProperty;
   }

   public final void setSlideShowDisplayTime(int seconds) {
      seconds = MathUtilities.clamp(1, seconds, 15);
      if (this._persistedExplorerOptions._slideShowDisplayTime != seconds) {
         this._persistedExplorerOptions._slideShowDisplayTime = seconds;
         this.fireOptionsChanged(1);
      }
   }

   public final void setNumberOfColumns(int columns) {
      columns = MathUtilities.clamp(MIN_NUMBER_OF_COLUMNS, columns, MAX_NUMBER_OF_COLUMNS);
      if (this._persistedExplorerOptions._numberOfColumns != columns) {
         this._persistedExplorerOptions._numberOfColumns = columns;
         this.fireOptionsChanged(3);
      }
   }

   public final void setFilelistSortProperty(int propertyIndex) {
      propertyIndex = MathUtilities.clamp(0, propertyIndex, 1);
      if (this._persistedExplorerOptions._filelistSortProperty != propertyIndex) {
         this._persistedExplorerOptions._filelistSortProperty = propertyIndex;
         this.fireOptionsChanged(4);
      }
   }

   public final void setFilelistSortDirection(int orderIndex) {
      orderIndex = MathUtilities.clamp(0, orderIndex, 1);
      if (this._persistedExplorerOptions._filelistSortDirection != orderIndex) {
         this._persistedExplorerOptions._filelistSortDirection = orderIndex;
         this.fireOptionsChanged(5);
      }
   }

   public final void setViewMode(int rootView, int viewMode) {
      viewMode = MathUtilities.clamp(0, viewMode, 3);
      if (this._persistedExplorerOptions._viewMode.get(rootView) != viewMode) {
         this._persistedExplorerOptions._viewMode.put(rootView, viewMode);
         this.fireOptionsChanged(2);
      }
   }
}
