package net.rim.device.apps.internal.camera;

import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.Persistable;

final class CameraOptions$PersistedCameraOptions implements Persistable {
   int _devOptions = 3;
   int _imageSizeIndex = 0;
   int _imageQualityIndex = 2;
   int _memoryType = FileUtilities.isSDCardMounted() ? 1 : 0;
   String _destinationFolder = CameraOptions.getDefaultPath(this._memoryType);
   int _flashModeIndex = 2;
   int _whiteBalanceIndex = 0;
   int _viewfinderMode = 0;
   int _colourEffectIndex = 0;
}
