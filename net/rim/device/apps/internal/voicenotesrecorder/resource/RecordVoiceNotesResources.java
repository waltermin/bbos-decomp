package net.rim.device.apps.internal.voicenotesrecorder.resource;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.ThemeManager;

public final class RecordVoiceNotesResources {
   private static ResourceBundle _VoiceNotesRecorderBundle = ResourceBundle.getBundle(
      -5703172391667637902L, "net.rim.device.apps.internal.resource.VoiceNotesRecorder"
   );

   public static final String getString(int id) {
      return _VoiceNotesRecorderBundle.getString(id);
   }

   public static final Bitmap getVoiceNoteRecorderImage() {
      EncodedImage image = ThemeManager.getActiveTheme().getImage("VoiceNoteRecorder", true);
      return image != null ? image.getBitmap() : Bitmap.getBitmapResource("VoiceNoteRecorder.png");
   }

   public static final EncodedImage getAnimatedRecorderImage() {
      EncodedImage image = ThemeManager.getActiveTheme().getImage("VoiceNoteRecorderAnimation", true);
      return image != null ? image : EncodedImage.getEncodedImageResource("VoiceNoteRecorderAnimation.gif");
   }
}
