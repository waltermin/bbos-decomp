package net.rim.device.apps.internal.vad;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.vad.VADParameters;

final class VADEngineManager$VADSyncItem extends OTASyncCapableSyncItem {
   private final VADEngineManager this$0;
   private static final int TYPE_FLAGS;
   private static final int TYPE_CONFIRMATION;
   private static final int TYPE_SENSITIVITY;
   private static final int TYPE_TTS_SPEED;
   private static final int TYPE_TTS_VOLUME;
   private static final int TYPE_LANGUAGE;
   private static final int FLAG_PLAY_PROMPTS;
   private static final int FLAG_PLAY_DIGITS;
   private static final int FLAG_PLAY_NAMES;

   VADEngineManager$VADSyncItem(VADEngineManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final String getSyncName() {
      return "Voice Activated Dialing Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      VADParameters data = this.this$0._persistentData._parameters;
      int flags = 0;
      if (data._playPrompts) {
         flags |= 1;
      }

      if (data._playDigits) {
         flags |= 2;
      }

      if (data._playNames) {
         flags |= 4;
      }

      ConverterUtilities.writeInt(buffer, 0, flags);
      ConverterUtilities.writeInt(buffer, 1, data._confirmation);
      ConverterUtilities.writeInt(buffer, 2, data._sensitivity);
      ConverterUtilities.writeInt(buffer, 4, data._ttsSpeed);
      ConverterUtilities.writeInt(buffer, 5, data._ttsVolume);
      ConverterUtilities.writeInt(buffer, 6, data._language);
      return true;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      VADParameters data = this.this$0._persistentData._parameters;

      label85:
      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               break;
            }

            switch (type) {
               case -1:
               case 3:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 0:
               default:
                  int flags = ConverterUtilities.readInt(buffer);
                  data._playPrompts = (flags & 1) != 0;
                  data._playDigits = (flags & 2) != 0;
                  data._playNames = (flags & 4) != 0;
                  break;
               case 1:
                  data._confirmation = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  data._sensitivity = ConverterUtilities.readInt(buffer);
                  break;
               case 4:
                  data._ttsSpeed = ConverterUtilities.readInt(buffer);
                  break;
               case 5:
                  data._ttsVolume = ConverterUtilities.readInt(buffer);
                  break;
               case 6:
                  this.this$0.setLanguage(ConverterUtilities.readInt(buffer));
            }
         }
      } finally {
         break label85;
      }

      this.this$0._persistentObject.commit();
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }
}
