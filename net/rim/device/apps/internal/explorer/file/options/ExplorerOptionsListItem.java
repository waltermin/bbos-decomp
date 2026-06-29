package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.explorer.Media.ThemeUtilities;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.store.ContentStoreConnection;
import net.rim.device.internal.media.MediaOptionsRegistry;
import net.rim.device.internal.media.MediaOptionsUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.util.OptionsRegistry$Listener;

public final class ExplorerOptionsListItem
   extends SaveableMainScreenOptionsListItem
   implements FieldChangeListener,
   OptionsRegistry$Listener,
   ScreenUiEngineAttachedListener {
   private NumericChoiceField _slideShowDisplayTimeField;
   private NumericChoiceField _numberOfColumns;
   private ObjectChoiceField _picturesReservedSize;
   private ObjectChoiceField _contentStoreSize;
   private ObjectChoiceField _filelistSortProperty;
   private boolean _originalVolumeBoostSetting;
   private ObjectChoiceField _volumeBoostMode;
   private ObjectChoiceField _autoBacklighting;
   private ExplorerOptions _options = ExplorerOptions.getOptions();
   private static final int MIN_NUMBER_OF_COLUMNS = ExplorerOptions.MIN_NUMBER_OF_COLUMNS;
   private static final int MIN_SLIDESHOW_TIME;
   private static final int MAX_SLIDESHOW_TIME;

   public ExplorerOptionsListItem() {
      super(ExplorerResources.getString(88));
   }

   @Override
   protected final Field getTitleField() {
      return ThemeUtilities.getTitleField(this.getDisplayName());
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      screen.setTag(ThemeUtilities.SCREEN_TAG);
      Manager manager = screen.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.SCREEN_TAG);
      }

      this.addHeading(164, screen);
      this._contentStoreSize = (ObjectChoiceField)(new Object(
         ExplorerResources.getString(101), ExplorerResources.getStringArray(104), getDeviceIndex(FileSystemOptions.getContentStoreTotalSize())
      ));
      screen.add(this._contentStoreSize);
      this.addHeading(165, screen);
      this._filelistSortProperty = (ObjectChoiceField)(new Object(
         ExplorerResources.getString(107), ExplorerResources.getStringArray(106), this._options.getFilelistSortProperty()
      ));
      screen.add(this._filelistSortProperty);
      this._numberOfColumns = (NumericChoiceField)(new Object(
         ExplorerResources.getString(96),
         MIN_NUMBER_OF_COLUMNS,
         ExplorerOptions.MAX_NUMBER_OF_COLUMNS,
         1,
         this._options.getNumberOfColumns() - MIN_NUMBER_OF_COLUMNS
      ));
      screen.add(this._numberOfColumns);
      this._slideShowDisplayTimeField = (NumericChoiceField)(new Object(ExplorerResources.getString(87), 1, 15, 1, this._options.getSlideShowDisplayTime() - 1));
      screen.add(this._slideShowDisplayTimeField);
      this._picturesReservedSize = (ObjectChoiceField)(new Object(
         ExplorerResources.getString(102), ExplorerResources.getStringArray(103), getReservedIndex(FileSystemOptions.getPicturesReservedSize())
      ));
      if (InternalServices.isDeviceCapable(21)) {
         screen.add(this._picturesReservedSize);
      }

      this.addHeading(173, screen);
      this._autoBacklighting = (ObjectChoiceField)(new Object(
         ExplorerResources.getString(190), ExplorerResources.getStringArray(191), MediaOptionsRegistry.getInstance().getBoolean(-1314075862077144981L) ? 0 : 1
      ));
      screen.add(this._autoBacklighting);
      if (AudioRouter.getInstance().isVolumeBoostModeSupported()) {
         this._originalVolumeBoostSetting = MediaOptionsRegistry.getInstance().getBoolean(2886183832722201160L);
         this._volumeBoostMode = (ObjectChoiceField)(new Object(
            ExplorerResources.getString(171), ExplorerResources.getStringArray(172), this._originalVolumeBoostSetting ? 0 : 1
         ));
         this._volumeBoostMode.setChangeListener(this);
         MediaOptionsRegistry.getInstance().addOptionsRegistryChangeListener(this);
         screen.addScreenUiEngineAttachedListener(this);
         screen.add(this._volumeBoostMode);
      }

      int i = screen.getFieldCount();

      while (--i >= 0) {
         Field field = screen.getField(i);
         if (!(field instanceof Object)) {
            field.setTag(ThemeUtilities.SELECTABLE_TEXT_TAG);
         }
      }
   }

   private final void addHeading(int id, MainScreen screen) {
      if (screen.getFieldCount() > 0) {
         screen.add(new SpacerField());
      }

      LabelField label = (LabelField)(new Object(ExplorerResources.getString(id)));
      label.setTag(ThemeUtilities.HEADING_TEXT_TAG);
      screen.add(label);
      screen.add(new MySeparatorField());
   }

   private static final int getReservedIndex(long value) {
      if (value >= 5242880) {
         return 2;
      } else {
         return value >= 2097152 ? 1 : 0;
      }
   }

   private static final long getReservedValue(int index) {
      switch (index) {
         case 0:
            return 0;
         case 1:
            return 2097152;
         case 2:
         default:
            return 5242880;
      }
   }

   private static final int getDeviceIndex(long value) {
      return value >= 15728640 ? 1 : 0;
   }

   private static final long getDeviceValue(int index) {
      switch (index) {
         case 1:
            return 15728640;
         default:
            return 12582912;
      }
   }

   @Override
   protected final boolean save() {
      if (this._picturesReservedSize.isDirty() || this._contentStoreSize.isDirty()) {
         long contentStoreTotal = getDeviceValue(this._contentStoreSize.getSelectedIndex());
         long picturesReservedSize = getReservedValue(this._picturesReservedSize.getSelectedIndex());
         long totalUsedSize = ContentStoreConnection.totalSizeUsed();
         long picturesTotalSize = ContentStoreConnection.picturesTotalSizeUsed();
         long additionalQuotaRequired = Math.max(picturesReservedSize - picturesTotalSize, 0);
         if (contentStoreTotal < totalUsedSize + additionalQuotaRequired) {
            this._contentStoreSize.setSelectedIndex(getDeviceIndex(FileSystemOptions.getContentStoreTotalSize()));
            this._contentStoreSize.setDirty(false);
            this._picturesReservedSize.setSelectedIndex(getReservedIndex(FileSystemOptions.getPicturesReservedSize()));
            this._picturesReservedSize.setDirty(false);
            Dialog.alert(ExplorerResources.getString(126));
            return false;
         }

         FileSystemOptions.setContentSizes(contentStoreTotal, picturesReservedSize);
         FileSystemOptions.save();
      }

      this._options.setSlideShowDisplayTime(this._slideShowDisplayTimeField.getSelectedValue());
      this._options.setNumberOfColumns(this._numberOfColumns.getSelectedValue());
      int sortIndex = this._filelistSortProperty.getSelectedIndex();
      this._options.setFilelistSortDirection(sortIndex == 0 ? 1 : 0);
      this._options.setFilelistSortProperty(sortIndex);
      this._options.commit();
      if (this._volumeBoostMode != null
         && this._volumeBoostMode.isDirty()
         && !this.getBoostModeSelection()
         && MediaOptionsRegistry.getInstance().getBoolean(2886183832722201160L)) {
         MediaOptionsRegistry.getInstance().setBoolean(2886183832722201160L, false);
         AudioRouter.getInstance().setVolumeBoostMode(false);
      }

      if (this._autoBacklighting != null && this._autoBacklighting.isDirty()) {
         MediaOptionsRegistry.getInstance().setBoolean(-1314075862077144981L, this._autoBacklighting.getSelectedIndex() == 0);
      }

      return super.save();
   }

   @Override
   protected final boolean discard() {
      if (this._volumeBoostMode != null
         && this._volumeBoostMode.isDirty()
         && MediaOptionsRegistry.getInstance().getBoolean(2886183832722201160L) != this._originalVolumeBoostSetting) {
         MediaOptionsRegistry.getInstance().setBoolean(2886183832722201160L, this._originalVolumeBoostSetting);
         AudioRouter.getInstance().setVolumeBoostMode(this._originalVolumeBoostSetting);
      }

      return super.discard();
   }

   private final boolean getBoostModeSelection() {
      int boostIndex = this._volumeBoostMode.getSelectedIndex();
      return boostIndex == 0;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._volumeBoostMode && context != Integer.MIN_VALUE && this.getBoostModeSelection()) {
         try {
            MediaOptionsUtilities.getInstance().showBoostVolumeWarning();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void onOptionsRegistryChange(long key) {
      if (key == 2886183832722201160L) {
         boolean boost = MediaOptionsRegistry.getInstance().getBoolean(key);
         if (this._volumeBoostMode != null) {
            this._volumeBoostMode.setSelectedIndex(boost ? 0 : 1);
            this._volumeBoostMode.setDirty(true);
         }
      }
   }

   @Override
   public final void onScreenUiEngineAttached(Screen screen, boolean attached) {
      if (screen == super._mainScreen && !attached) {
         MediaOptionsRegistry.getInstance().removeOptionsRegistryChangeListener(this);
         super._mainScreen.removeScreenUiEngineAttachedListener(this);
      }
   }
}
