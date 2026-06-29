package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class RecordVoiceNoteScreen$VoiceNoteStatusManager extends Manager {
   private final RecordVoiceNoteScreen this$0;
   private static final int IMAGE = 0;
   private static final int LABEL = 1;

   RecordVoiceNoteScreen$VoiceNoteStatusManager(RecordVoiceNoteScreen _1) {
      super(0);
      this.this$0 = _1;
   }

   @Override
   protected final void sublayout(int width, int height) {
      int count = this.getFieldCount();
      int x = 0;
      int y = 0;

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               int imageWidth;
               int imageHeight;
               if (this.this$0.isRecording()) {
                  imageWidth = this.this$0._animatedImage.getWidth();
                  imageHeight = this.this$0._animatedImage.getHeight();
               } else {
                  imageWidth = this.this$0._staticBitmap.getWidth();
                  imageHeight = this.this$0._staticBitmap.getHeight();
               }

               x = (Display.getWidth() >> 1) - (imageWidth >> 1);
               y = 75 - (imageHeight >> 1);
               this.setPositionChild(field, x, y);
               this.layoutChild(field, width, height);
               break;
            case 1:
               x = (Display.getWidth() >> 1) - (this.this$0._labelField.getFont().getAdvance(this.this$0._labelField.getText()) >> 1);
               if (x < 0) {
                  x = 0;
               }

               this.setPositionChild(field, x, 140);
               this.layoutChild(field, width, height);
         }
      }

      this.setExtent(width, height);
   }
}
