package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.system.MMS$ClientOptions;

public final class ExternalClientOptions implements MMS$ClientOptions {
   @Override
   public final int getMaximumVoiceNoteRecordTime() {
      return MMSClientServiceBook.getMaxVoiceNoteRecordTime();
   }

   @Override
   public final int getMaximumVoiceNoteRecordSize() {
      return MMSClientServiceBook.getMaxVoiceNoteRecordSize();
   }
}
