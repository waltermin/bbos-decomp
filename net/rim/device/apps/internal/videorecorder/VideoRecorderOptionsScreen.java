package net.rim.device.apps.internal.videorecorder;

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
import net.rim.device.apps.internal.mediarecorder.ThemeUtilities;

final class VideoRecorderOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private FilenameEditField _folder;
   private ObjectChoiceField _colourEffect;
   private ObjectChoiceField _flashMode;
   private ObjectChoiceField _videoFormat;
   private VideoRecorderOptions _options = VideoRecorderOptions.getOptions();
   private int _startingFlashMode;

   private VideoRecorderOptionsScreen() {
      super(VideoRecorderResources.getString(5));
      ContextObject.put(super._context, 244, new Object(244387));
   }

   @Override
   protected final Field getTitleField() {
      return ThemeUtilities.getTitleField(this.getDisplayName());
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._folder) {
         this._folder.setDirty(true);
         String folder = this._folder.getPath();
         if (!folder.startsWith("/SDCard/", 0)) {
            this._folder.setPath(VideoRecorderOptions.getDefaultPath());
            Dialog.alert(VideoRecorderResources.getString(22));
         }
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      screen.setTag(ThemeUtilities.TAG_RECORDER_OPTION_SCREEN);
      Manager manager = screen.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.TAG_RECORDER_OPTION_SCREEN);
      }

      this._startingFlashMode = this._options.getFlashModeIndex();
      this._flashMode = (ObjectChoiceField)(new Object(VideoRecorderResources.getString(8), VideoRecorderResources.getStringArray(9), this._startingFlashMode));
      screen.add(this._flashMode);
      this._colourEffect = (ObjectChoiceField)(new Object(
         VideoRecorderResources.getString(14), VideoRecorderResources.getStringArray(15), this._options.getColourEffectIndex()
      ));
      screen.add(this._colourEffect);
      this._videoFormat = (ObjectChoiceField)(new Object(VideoRecorderResources.getString(12), this.getImageSizeChoices(), this._options.getVideoFormatIndex()));
      screen.add(this._videoFormat);
      screen.add((Field)(new Object()));
      this._folder = (FilenameEditField)(new Object(this._options.getDestinationFolder(), null, 3, 134217728, true));
      this._folder.getFileSelector().setSelectFolder(true);
      LabelField folderLabel = (LabelField)(new Object(VideoRecorderResources.getString(18)));
      folderLabel.setTag(ThemeUtilities.TAG_RECORDER_SELECTABLE_TEXT);
      screen.add(folderLabel);
      screen.add(this._folder);
      this._folder.setChangeListener(this);
      int i = screen.getFieldCount();

      while (--i >= 0) {
         Field field = screen.getField(i);
         if (!(field instanceof Object)) {
            field.setTag(ThemeUtilities.TAG_RECORDER_SELECTABLE_TEXT);
         }
      }
   }

   private final String[] getImageSizeChoices() {
      String[] choices = VideoRecorderResources.getStringArray(13);
      String[] labels = new Object[choices.length];
      int i = choices.length;

      while (--i >= 0) {
         int[] imageSize = this._options.getVideoFormat(i);
         labels[i] = ((StringBuffer)(new Object()))
            .append(choices[i])
            .append(" (")
            .append(imageSize[0])
            .append(" x ")
            .append(imageSize[1])
            .append(")")
            .toString();
      }

      return labels;
   }

   @Override
   protected final boolean save() {
      boolean saveFlash = true;
      if (this._startingFlashMode != this._flashMode.getSelectedIndex() && this._flashMode.getSelectedIndex() == 1) {
         saveFlash = Dialog.ask(3, VideoRecorderResources.getString(7), -1) == 4;
      }

      if (saveFlash) {
         this._options.setFlashModeIndex(this._flashMode.getSelectedIndex());
      }

      this._options.setVideoFormatIndex(this._videoFormat.getSelectedIndex());
      this._options.setColourEffectIndex(this._colourEffect.getSelectedIndex());
      this._options.setDestinationFolder(this._folder.getPath());
      this._options.commit();
      return super.save();
   }

   public static final void showEditOptionsScreen() {
      VideoRecorderOptionsScreen optionsScreen = new VideoRecorderOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }
}
