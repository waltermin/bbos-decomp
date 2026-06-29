package net.rim.device.apps.internal.camera;

import net.rim.vm.Persistable;

final class CameraScreen$PersistedCameraData implements Persistable {
   int _pictureCounter = 0;
   int[] _avgImageSizeFactor = new int[3];

   CameraScreen$PersistedCameraData() {
      this._avgImageSizeFactor[0] = 40;
      this._avgImageSizeFactor[1] = 130;
      this._avgImageSizeFactor[2] = 210;
   }
}
