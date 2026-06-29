package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.iConferenceCall;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

public final class SimpleCallStatusField extends LabelField implements PhoneEventListener {
   private boolean _listening;
   private LiveCall _call;

   public SimpleCallStatusField(LiveCall call) {
      this(call, 0, null);
   }

   public SimpleCallStatusField(LiveCall call, Font font) {
      this(call, 0, font);
   }

   public SimpleCallStatusField(LiveCall call, int fontSize, Font font) {
      super(null, 1152921504606846976L);
      if (fontSize != 0) {
         if (PhoneUtilities.smallScreen()) {
            FontFamily systemFamily = null;

            label35:
            try {
               systemFamily = FontFamily.forName(FontFamily.FAMILY_SYSTEM);
            } finally {
               break label35;
            }

            if (systemFamily != null) {
               this.setFont(systemFamily.getFont(1, fontSize, 3));
            } else {
               this.setFont(this.getFont().derive(1, fontSize, 3));
            }
         } else {
            this.setFont(this.getFont().derive(1, fontSize));
         }
      } else if (font != null) {
         this.setFont(font);
      }

      this._call = call;
      this.setText(call.getStatusString());
   }

   public final void update() {
      this.setText(this._call.getStatusString());
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         if (!this._listening) {
            VoiceServices.addPhoneEventListener(this);
            this._listening = true;
            if (this._call != null) {
               this.setText(this._call.getStatusString());
               return;
            }
         }
      } else {
         VoiceServices.removePhoneEventListener(this);
         this._listening = false;
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1001:
         case 1002:
         case 1003:
         case 1004:
         case 1006:
         case 150040:
         case 150060:
         case 201010:
            if (this.matchCallId(callId)) {
               this.setText(this._call.getStatusString());
            }
      }
   }

   private final boolean matchCallId(int callId) {
      if (callId == this._call.getCallId()) {
         return true;
      }

      if (this._call instanceof iConferenceCall) {
         int[] callIds = this._call.getCallIds();
         if (callIds != null && callIds.length > 0) {
            for (int idx = 0; idx < callIds.length; idx++) {
               if (callIds[idx] == callId) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
