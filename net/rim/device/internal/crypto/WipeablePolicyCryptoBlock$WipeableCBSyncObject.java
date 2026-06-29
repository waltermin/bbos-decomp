package net.rim.device.internal.crypto;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Memory;

public final class WipeablePolicyCryptoBlock$WipeableCBSyncObject implements SyncObject {
   int _uid;
   byte[] _key;

   public final byte[] getKeyData() {
      return this._key;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   WipeablePolicyCryptoBlock$WipeableCBSyncObject(int uid, byte[] keyData) {
      this._uid = uid;
      this._key = keyData;
   }

   WipeablePolicyCryptoBlock$WipeableCBSyncObject() {
      this._uid = UIDGenerator.getUID();
      this._key = WipeablePolicyCryptoBlock.getWLANKey();
   }

   public static final boolean convert(SyncObject object, DataBuffer buffer) {
      if (!(object instanceof WipeablePolicyCryptoBlock$WipeableCBSyncObject)) {
         return false;
      }

      WipeablePolicyCryptoBlock$WipeableCBSyncObject wipeableCBSyncObject = (WipeablePolicyCryptoBlock$WipeableCBSyncObject)object;
      buffer.write(wipeableCBSyncObject.getKeyData());
      return true;
   }

   public static final SyncObject convert(DataBuffer data, int UID) {
      WipeablePolicyCryptoBlock.access$702(new byte[1][]);
      WipeablePolicyCryptoBlock._wlanKey[0] = Memory.allocRAMOnlyBytes(32);
      System.arraycopy(data.getArray(), 0, WipeablePolicyCryptoBlock._wlanKey[0], 0, 32);
      WipeablePolicyCryptoBlock.setNvStoreWLANKey(Arrays.copy(WipeablePolicyCryptoBlock._wlanKey[0]));
      WipeablePolicyCryptoBlock.handleInternalUpgradeCase(WipeablePolicyCryptoBlock._wlanKey[0]);
      ApplicationRegistry.getApplicationRegistry()
         .replace(7837055873320154475L, new ControlledAccess(WipeablePolicyCryptoBlock._wlanKey, CodeSigningKey.getBuiltInKey(51)));
      return new WipeablePolicyCryptoBlock$WipeableCBSyncObject(UID, data.getArray());
   }
}
