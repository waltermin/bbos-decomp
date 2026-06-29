package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.VoiceMailVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class VoicemailEntry extends Action implements GlobalEventListener, PersistentContentListener {
   private Bitmap _icon = Bitmap.getBitmapResource("Phone28.gif");
   private VoiceMailVerb _verb = new VoiceMailVerb(131585);
   private VoicemailIconManager _voicemailIconManager = VoicemailIconManager.getInstance();
   String _state;
   boolean _disabled;
   boolean _uncertain;
   private static final boolean _debugState = false;

   VoicemailEntry() {
      super(new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"show"}), "net_rim_bb_phone_app.Voicemail", 19);
      this.setDisabledState();
      VoiceServices.getUiApplication().addGlobalEventListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void setDisabledState() {
      String voicemailNumber = null;
      boolean var4 = false /* VF: Semaphore variable */;

      label35:
      try {
         var4 = true;
         voicemailNumber = PhoneUtilities.getVoiceMailNumber();
         this._uncertain = false;
         var4 = false;
      } finally {
         if (var4) {
            this._uncertain = true;
            PersistentContent.addListener(this);
            break label35;
         }
      }

      if (voicemailNumber != null) {
         voicemailNumber.trim();
      }

      if (voicemailNumber != null && voicemailNumber.length() != 0) {
         this._state = null;
         this._disabled = false;
      } else {
         this._state = "disabled";
         this._disabled = true;
      }
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         return this._icon;
      } else if (propID == 12) {
         int count = this._voicemailIconManager.getVoicemailCount();
         return count > 0 ? Integer.toString(count) : "";
      } else {
         return defaultReturned;
      }
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return propID == 11 ? Boolean.TRUE : defaultReturned;
   }

   @Override
   protected final String getDescription() {
      return PhoneResources.getString(190);
   }

   @Override
   protected final String getState() {
      return this._state;
   }

   @Override
   public final void run() {
      this._verb.invoke(null);
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (this._uncertain && state == 1) {
         PersistentContent.removeListener(this);
         this.setDisabledState();
         this.update();
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6314221642431705640L) {
         this.setDisabledState();
         this.update();
      } else {
         if (guid == 6291453494459897456L) {
            if (this._disabled) {
               return;
            }

            int count = this._voicemailIconManager.getVoicemailCount();
            if (count == 0) {
               this._state = null;
            } else if (count == -1) {
               this._state = "new";
            }

            this.update();
         }
      }
   }
}
