package net.rim.device.apps.internal.camera;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.file.FilenameEditField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.internal.camera.Camera;
import net.rim.vm.Array;

final class CameraOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private ObjectChoiceField _memorySetting;
   private FilenameEditField _folder;
   private ObjectChoiceField _whiteBalance;
   private ObjectChoiceField _flashMode;
   private ObjectChoiceField _imageSize;
   private ObjectChoiceField _imageQuality;
   private ObjectChoiceField _viewfinderMode;
   private ObjectChoiceField _colourEffect;
   private CameraOptions _cameraOptions = CameraOptions.getOptions();
   private ResourceBundle _rb;
   private int _startingFlashMode;
   private String[] _rWbOptions = null;
   private String[] _wbOptions = null;

   private CameraOptionsScreen() {
      super(CameraMain._rb.getString(2));
      ContextObject.put(super._context, 244, new Object(244387));
      this._rb = ResourceBundle.getBundle(7839140414916824787L, "net.rim.device.apps.internal.camera.Camera");
   }

   @Override
   protected final Field getTitleField() {
      return ThemeUtilities.getTitleField(this.getDisplayName());
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._memorySetting) {
         int memType = this._memorySetting.getSelectedIndex();
         int mediaType = this._folder.getPath().startsWith("/SDCard/", 0) ? 1 : 0;
         if (memType != mediaType) {
            this._folder.setPath(CameraOptions.getDefaultPath(memType));
            this._folder.setDirty(true);
            return;
         }
      } else if (field == this._folder) {
         this._folder.setDirty(true);
         String folder = this._folder.getPath();
         int mediaType = folder.startsWith("/SDCard/", 0) ? 1 : 0;
         if (mediaType != this._memorySetting.getSelectedIndex()) {
            this._memorySetting.setSelectedIndex(mediaType);
            this._memorySetting.setDirty(true);
         }
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      screen.setTag(ThemeUtilities.TAG_CAMERA_SCREEN);
      Manager manager = screen.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.TAG_CAMERA_SCREEN);
      }

      this._startingFlashMode = this._cameraOptions.getFlashModeIndex();
      this._flashMode = (ObjectChoiceField)(new Object(this._rb.getString(13), this._rb.getStringArray(14), this._startingFlashMode));
      screen.add(this._flashMode);
      int[] wbOptionTable = this._cameraOptions.getWhiteBalanceTable();
      int count = 0;

      for (int i = 0; i < wbOptionTable.length; i++) {
         if (wbOptionTable[i] != -1) {
            count++;
         }
      }

      this._rWbOptions = new Object[count];
      this._wbOptions = this._rb.getStringArray(16);
      count = 0;

      for (int i = 0; i < this._wbOptions.length; i++) {
         if (wbOptionTable[i] != -1) {
            this._rWbOptions[count++] = this._wbOptions[i];
         }
      }

      int rWbIndex = 0;
      String string = this._wbOptions[this._cameraOptions.getWhiteBalanceIndex()];

      for (int i = 0; i < this._rWbOptions.length; i++) {
         if (string == this._rWbOptions[i]) {
            rWbIndex = i;
            break;
         }
      }

      this._whiteBalance = (ObjectChoiceField)(new Object(this._rb.getString(15), this._rWbOptions, rWbIndex));
      screen.add(this._whiteBalance);
      this._imageSize = (ObjectChoiceField)(new Object(this._rb.getString(7), this.getImageSizeChoices(), this._cameraOptions.getImageSizeIndex()));
      screen.add(this._imageSize);
      String[] choices = this._rb.getStringArray(8);
      String[] rChoices = new Object[choices.length];

      for (int i = rChoices.length; i > 0; i--) {
         rChoices[i - 1] = choices[choices.length - i];
      }

      this._imageQuality = (ObjectChoiceField)(new Object(this._rb.getString(9), rChoices, rChoices.length - this._cameraOptions.getImageQualityIndex() - 1));
      screen.add(this._imageQuality);
      if (CameraOptions.isViewfinderModeValid(1)) {
         this._viewfinderMode = (ObjectChoiceField)(new Object(this._rb.getString(36), this._rb.getStringArray(37), this._cameraOptions.getViewfinderMode()));
         screen.add(this._viewfinderMode);
      }

      int colourEffects = Camera.getColourEffects();
      if (colourEffects != 0) {
         String[] allChoices = this._rb.getStringArray(35);
         String[] supportedChoices = new Object[allChoices.length];
         int current = this._cameraOptions.getColourEffectIndex();
         int selected = 0;
         count = 0;

         for (int i = 0; i < allChoices.length; i++) {
            if ((colourEffects & 1 << this._cameraOptions.getColourEffect(i)) != 0) {
               if (i == current) {
                  selected = count;
               }

               supportedChoices[count++] = allChoices[i];
            }
         }

         Array.resize(supportedChoices, count);
         this._colourEffect = (ObjectChoiceField)(new Object(this._rb.getString(34), supportedChoices, selected));
         screen.add(this._colourEffect);
      }

      screen.add((Field)(new Object()));
      this._memorySetting = (ObjectChoiceField)(new Object(
         this._rb.getString(4), this._rb.getStringArray(5), this._cameraOptions.getMemoryType(), CameraMain.isMediaCardPresent() ? 0 : 9007199254740992L
      ));
      screen.add(this._memorySetting);
      this._folder = (FilenameEditField)(new Object(this._cameraOptions.getDestinationFolder(), null, 1, 134217728, true));
      this._folder.getFileSelector().setSelectFolder(true);
      LabelField folderLabel = (LabelField)(new Object(this._rb.getString(3)));
      folderLabel.setTag(ThemeUtilities.TAG_CAMERA_SELECTABLE_TEXT);
      screen.add(folderLabel);
      screen.add(this._folder);
      this._memorySetting.setChangeListener(this);
      this._folder.setChangeListener(this);
      int i = screen.getFieldCount();

      while (--i >= 0) {
         Field field = screen.getField(i);
         if (!(field instanceof Object)) {
            field.setTag(ThemeUtilities.TAG_CAMERA_SELECTABLE_TEXT);
         }
      }
   }

   private final String[] getImageSizeChoices() {
      String[] choices = this._rb.getStringArray(46);
      String[] args = new Object[2];
      String[] labels = new Object[choices.length];

      for (int i = choices.length; --i >= 0; labels[i] = MessageFormat.format(choices[i], args)) {
         int[] imageSize = this._cameraOptions.getImageSize(i);
         args[0] = Integer.toString(imageSize[0]);
         args[1] = Integer.toString(imageSize[1]);
      }

      return labels;
   }

   @Override
   protected final boolean save() {
      boolean saveFlash = true;
      if (this._startingFlashMode != this._flashMode.getSelectedIndex() && this._flashMode.getSelectedIndex() == 1) {
         saveFlash = Dialog.ask(3, this._rb.getString(21), -1) == 4;
      }

      if (saveFlash) {
         this._cameraOptions.setFlashModeIndex(this._flashMode.getSelectedIndex());
      }

      this._cameraOptions.setImageSizeIndex(this._imageSize.getSelectedIndex());
      this._cameraOptions.setImageQualityIndex(this._imageQuality.getSize() - this._imageQuality.getSelectedIndex() - 1);
      String selectedWbOption = this._rWbOptions[this._whiteBalance.getSelectedIndex()];
      int idx = 0;

      while (idx < this._rWbOptions.length && selectedWbOption != this._wbOptions[idx]) {
         idx++;
      }

      this._cameraOptions.setWhiteBalanceIndex(idx);
      this._cameraOptions.setMemoryType(this._memorySetting.getSelectedIndex());
      this._cameraOptions.setDestinationFolder(this._folder.getPath());
      if (this._viewfinderMode != null) {
         this._cameraOptions.setViewfinderMode(this._viewfinderMode.getSelectedIndex());
      }

      if (this._colourEffect != null) {
         String[] allChoices = this._rb.getStringArray(35);
         String choice = (String)this._colourEffect.getChoice(this._colourEffect.getSelectedIndex());

         for (int i = 0; i < allChoices.length; i++) {
            if (choice.equals(allChoices[i])) {
               this._cameraOptions.setColourEffectIndex(i);
               break;
            }
         }
      }

      this._cameraOptions.commit();
      return super.save();
   }

   public static final void showEditOptionsScreen() {
      CameraOptionsScreen optionsScreen = new CameraOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1162628934:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1162628935:
         default:
            PopupStatus.show(((StringBuffer)(new Object("Event Log ="))).append(this._cameraOptions.toggleDevOption(4)).toString(), 3000);
            return true;
      }
   }
}
