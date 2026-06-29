package net.rim.device.apps.api.phone;

import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.Phone;

final class PhoneState {
   int _flags;
   byte _state;
   int _activeId;
   int _heldId;
   int _incomingId;
   int _connectingId;
   private final byte[] _stateMap = new byte[]{0, 6, 1, -1, 3, 7, 5, -1, 2, -1, 4, -1, 8, -1, 8, -1};
   public static final int ACTIVE_CONFERENCE;
   public static final int HELD_CONFERENCE;
   private static final byte ACTIVE_BIT;
   private static final byte HELD_BIT;
   private static final byte INCOMING_BIT;
   private static final byte CONNECTING_BIT;

   public PhoneState() {
      this.reset();
   }

   public final byte getState() {
      return this._state;
   }

   public final int getConnectingId() {
      return this._connectingId;
   }

   public final int getActiveId() {
      return this._activeId;
   }

   public final int getHeldId() {
      return this._heldId;
   }

   public final int getIncomingId() {
      return this._incomingId;
   }

   public final boolean activeConference() {
      return (this._flags & 2) != 0;
   }

   public final boolean heldConference() {
      return (this._flags & 4) != 0;
   }

   final void calculateState() {
      this.reset();
      if (DirectConnect.isSupported()) {
         label76:
         try {
            int activeCallType = DirectConnect.getActiveCallType();
            if (activeCallType != 0) {
               if (activeCallType == 2) {
                  this._state = 11;
               } else {
                  this._state = 10;
               }

               return;
            }
         } finally {
            break label76;
         }
      }

      Phone phone = Phone.getInstance();
      int activeId = phone.getActiveCallId();
      if (activeId != 0) {
         int callState = VoiceServices.getCallState(activeId);
         if (callState == 1) {
            this._connectingId = activeId;
         } else {
            this._activeId = activeId;
            if ((activeId & 32768) != 0) {
               this._flags |= 2;
            }
         }
      }

      this._heldId = phone.getHeldCallId();
      if ((this._heldId & 32768) != 0) {
         this._flags |= 4;
      }

      this._incomingId = phone.getIncomingCallId();
      int activeBit = this._activeId != 0 ? 1 : 0;
      int heldBit = this._heldId != 0 ? 1 : 0;
      int connectingBit = this._connectingId != 0 ? 1 : 0;
      int incomingBit = this._incomingId != 0 ? 1 : 0;
      byte stateIndex = (byte)(activeBit << 3 | heldBit << 2 | incomingBit << 1 | connectingBit << 0);
      this._state = this._stateMap[stateIndex];
   }

   final void reset() {
      this._flags = 0;
      this._state = -1;
      this._activeId = this._heldId = this._connectingId = this._incomingId = 0;
   }

   public final void print() {
      System.out
         .println(
            ((StringBuffer)(new Object("PHONESTATE:\n\tstate=")))
               .append(this._state)
               .append("\n\tactive=")
               .append(this._activeId)
               .append("\n\theld=")
               .append(this._heldId)
               .append("\n\tincoming=")
               .append(this._incomingId)
               .append("\n\tconnecting=")
               .append(this._connectingId)
               .toString()
         );
   }
}
