package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui;

import net.rim.device.api.system.Display;
import net.rim.device.internal.ui.component.MMAPIMediaField;

public class VideoField extends MMAPIMediaField {
   public VideoField() {
      this(Display.getWidth(), Display.getHeight());
   }

   public VideoField(int width, int height) {
      super(width, height, 18014398509481984L);
   }
}
