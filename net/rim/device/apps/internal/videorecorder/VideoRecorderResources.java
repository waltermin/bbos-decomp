package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.internal.resource.VideoRecorderResource;

public final class VideoRecorderResources implements VideoRecorderResource {
   private static ResourceBundle _rb = ResourceBundle.getBundle(5325037524743121547L, "net.rim.device.apps.internal.resource.VideoRecorder");

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
