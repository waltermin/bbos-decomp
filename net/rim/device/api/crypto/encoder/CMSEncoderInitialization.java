package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.Initialization;

public final class CMSEncoderInitialization implements Initialization {
   @Override
   public final void initialize() {
      try {
         SignatureDecoder.register(new CMS_RIM_SignatureDecoder(), new CMS_SignatureDecoder());
         SignatureEncoder.register(new CMS_RIM_SignatureEncoder());
      } finally {
         throw new Object();
      }
   }
}
