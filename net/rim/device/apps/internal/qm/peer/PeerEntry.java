package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.qm.peer.common.Entry;

final class PeerEntry extends Entry implements LowMemoryListener {
   private boolean _hold;
   private static final String UID = "net_rim_bb_qm_peer.BlackBerryMessenger";

   public static final PeerEntry getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      long luid = Entry.stringHashToLong("net_rim_bb_qm_peer.BlackBerryMessenger");
      Object instance = applicationRegistry.get(luid);
      if (instance == null) {
         instance = new PeerEntry();
         ApplicationRegistry.getApplicationRegistry().replace(luid, instance);
      }

      return (PeerEntry)instance;
   }

   PeerEntry() {
      super(Bitmap.getBitmapResource("icon.png"), null, 15, "net_rim_bb_qm_peer.BlackBerryMessenger");
      UnreadCountManager.setAction(10, this);
   }

   final void hold(boolean hold) {
      this._hold = hold;
      if (this._hold) {
         this.clearNew();
      }
   }

   final void clearNew() {
      if (super._state != null) {
         super._state = null;
         this.updateIcons();
         this.update();
         UnreadCountManager.modifyUnreadCount(10, 0);
      }
   }

   final void setNew() {
      if (!this._hold) {
         super._state = "new";
         this.updateIcons();
         this.update();
      }
   }

   @Override
   public final String getUid() {
      return "net_rim_bb_qm_peer.BlackBerryMessenger";
   }

   @Override
   protected final void deviceUnlocked() {
      PeerApplication app = PeerApplication.getInstance();
      if (app != null) {
         app.deviceUnlocked();
      }
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      return propID == 2 ? super._state : super.get(propID, defaultReturned);
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      return propID == 12 ? UnreadCountManager.getUnreadCountObject(10) : super.get(propID, defaultReturned);
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      PeerApplication app = PeerApplication.getInstance();
      return app != null ? app.freeStaleObject(priority) : false;
   }

   @Override
   protected final void deviceLocked() {
      PeerApplication app = PeerApplication.getInstance();
      if (app != null) {
         app.deviceLocked();
      } else {
         PeerData.lock();
      }
   }
}
