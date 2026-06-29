package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.IntHashtable;

class ContactStatus extends QMTimerTask {
   IntHashtable _persistentData;
   protected int _contactStatus;
   private Object _customStatusMessage;
   static final boolean ACTIVE_PRESENCE;
   protected static final int SECOND;
   protected static final int MINUTE;
   static final int CONTACT_STATUS_ID;
   static final int CUSTOM_STATUS_MESSAGE_ID;
   private static final int DEFAULT_DECAY_INTERVAL;
   private static final int AVAILABLE_SUBSTATES_SHIFT;
   public static final int AVAILABLE_1;
   public static final int AVAILABLE_2;
   public static final int AVAILABLE_3;
   public static final int AVAILABLE_4;
   public static final int AVAILABLE_5;
   public static final int AVAILABLE_6;
   public static final int AVAILABLE_7;
   public static final int NO_PRESENCE_SUPPORT;
   public static final int READ_ONLY_PRESENCE_SUPPORT;
   public static final int AVAILABLE;
   public static final int UNAVAILABLE;
   public static final int BUSY;
   static final int UNREACHABLE;
   protected static final int AVAILABILITY_MASK;
   protected static final int OTHER_PRESENCE_STATES_MASK;
   protected static final int PRESENCE_MASK;
   private static final int STATES_SHIFT;
   private static final int STATE_TYPING;
   private static final int STATE_PENDING;
   private static final int STATE_UNAUTHORIZED;
   private static QMTimer _timer = new QMTimer();
   private static final int _decayInterval;

   public ContactStatus() {
   }

   ContactStatus(IntHashtable contactData) {
      if (contactData != null) {
         this._persistentData = contactData;
         this._customStatusMessage = contactData.get(7);
         Object status = contactData.get(4);
         if (status instanceof Object) {
            this._contactStatus = status;
            this.setPresenceStatus(this._contactStatus & 32543);
         } else {
            this.setPresenceStatus(2048);
         }
      } else {
         this._persistentData = (IntHashtable)(new Object());
         this._persistentData.put(4, new Object(this._contactStatus));
      }
   }

   void lock() {
      this._customStatusMessage = PersistentContent.reEncode(this._customStatusMessage, true, true);
   }

   public void setPresenceStatus(int status) {
      this.setPresenceStatus(status, this.getCustomStatusMessage());
   }

   public void setPresenceStatus(int status, String message) {
      status &= 32543;
      synchronized (this) {
         switch (status) {
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
               this._contactStatus &= -32544;
               this._contactStatus |= status;
               this.timer(false);
               break;
            case 256:
            case 512:
            case 1024:
            case 2048:
            case 4096:
            case 8192:
            case 16384:
               this._contactStatus &= -32544;
               this._contactStatus |= status;
               this.timer(true);
               break;
            default:
               if (!this.isPending()) {
                  String error = ((StringBuffer)(new Object("ContactStatus:Wrong presence status detected - "))).append(status).toString();
                  EventLogger.logEvent(-9029900896793868512L, error.getBytes(), 3);
               }

               return;
         }

         this.setCustomStatusMessage(message);
      }

      this.contactStatusChanged();
   }

   protected void timer(boolean restart) {
   }

   @Override
   public void run() {
   }

   public boolean isAlertable() {
      return !this.isAvailable() || this.isBusy() || this.isUnreachable();
   }

   public boolean isAvailable() {
      return !this.isFlagSet(4);
   }

   public boolean isBusy() {
      return this.isFlagSet(8);
   }

   public boolean isUnreachable() {
      return this.isFlagSet(16);
   }

   public boolean supportsPresence() {
      return !this.isFlagSet(1);
   }

   public String getCustomStatusMessage() {
      try {
         return PersistentContent.decodeString(this._customStatusMessage);
      } finally {
         ;
      }
   }

   public void setCustomStatusMessage(String message) {
      this._customStatusMessage = PersistentContent.encode(message, true, true);
      this.contactStatusChanged();
   }

   public void setPending(boolean value) {
      if (value) {
         this._contactStatus &= -131073;
         this._contactStatus |= 65536;
      } else {
         this._contactStatus &= -65537;
      }

      this.contactStatusChanged();
   }

   public boolean isPending() {
      return this.isFlagSet(65536);
   }

   boolean isAuthorized() {
      return !this.isFlagSet(131072);
   }

   void setAuthorized(boolean set) {
      if (set) {
         this._contactStatus &= -131073;
      } else {
         this._contactStatus |= 131072;
      }
   }

   public void setTyping(boolean typing) {
      if (typing) {
         this._contactStatus |= 32768;
         this.setPresenceStatus(16384);
      } else {
         this._contactStatus &= -32769;
      }

      this.contactStatusChanged();
   }

   public boolean isTyping() {
      return this.isFlagSet(32768);
   }

   private boolean isFlagSet(int flag) {
      return (this._contactStatus & flag) != 0;
   }

   @Override
   public String toString() {
      return this.formatStatus();
   }

   public String formatStatus() {
      if (this.isFlagSet(1)) {
         return PeerResources.getString(3000);
      } else if (this.isPending()) {
         return PeerResources.getString(3013);
      } else if (!this.isAuthorized()) {
         return PeerResources.getString(2044);
      } else if (this.isFlagSet(2)) {
         return PeerResources.getString(3001);
      } else if (this.isUnreachable()) {
         return PeerResources.getString(3002);
      } else if (!this.isAvailable()) {
         return PeerResources.getString(2034);
      } else {
         return this.isBusy() ? PeerResources.getString(3003) : this.getAvailabiltyStatus();
      }
   }

   private String getAvailabiltyStatus() {
      switch (this._contactStatus & 32536) {
         case 256:
            return PeerResources.getString(3010);
         case 512:
            return PeerResources.getString(3009);
         case 1024:
            return PeerResources.getString(3008);
         case 2048:
            return PeerResources.getString(3007);
         case 4096:
            return PeerResources.getString(3006);
         case 8192:
            return PeerResources.getString(3005);
         case 16384:
            return PeerResources.getString(3004);
         default:
            return PeerResources.getString(2033);
      }
   }

   int drawIcon(Graphics graphics, int x, int y, boolean unread) {
      int icon = this.getPresenceIconId();
      int overlay = -1;
      if (this.isUnreachable()) {
         icon = 0;
         overlay = 8;
      } else if (!this.isAvailable()) {
         icon = 0;
         overlay = 7;
      } else if (this.isBusy()) {
         icon = 0;
         overlay = 6;
      } else if (!this.isAuthorized()) {
         icon = 0;
      }

      if (unread) {
         overlay = 11;
         icon = icon == 0 ? 2 : 3;
      }

      PeerResources.drawIcon(graphics, x, y, icon);
      if (overlay != -1) {
         PeerResources.drawIcon(graphics, x, y, overlay);
      }

      return PeerResources.getIconHeight(graphics.getFont());
   }

   public int getPresenceIconId() {
      return 1;
   }

   void contactStatusChanged() {
      this._persistentData.put(4, new Object(this._contactStatus));
      if (this._customStatusMessage != null) {
         this._persistentData.put(7, this._customStatusMessage);
      }

      this.commit();
   }

   protected void commit() {
      throw null;
   }
}
