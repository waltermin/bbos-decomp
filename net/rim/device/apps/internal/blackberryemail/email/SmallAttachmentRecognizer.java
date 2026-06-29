package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;

class SmallAttachmentRecognizer implements Recognizer {
   @Override
   public boolean recognize(Object o) {
      return o instanceof CompressedFileAttachmentModel;
   }
}
