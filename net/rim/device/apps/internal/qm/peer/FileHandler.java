package net.rim.device.apps.internal.qm.peer;

final class FileHandler implements MessageHandler {
   private static final String IMAGE_TYPE = "image/*";
   private static final String AUDIO_TYPE = "audio/*";
   private static final String[] TYPES = new String[]{"image/*", "audio/*"};

   static final void regsiter() {
      ContentHandlerManager.getInstance().registerSystemHandler(new FileHandler(), TYPES);
   }

   @Override
   public final MessengerMessage handle(PeerContact contact, String contentType, String filename, byte[] data, int integer, Object context) {
      return new FileMessage(contact, contentType, data, filename, data.length);
   }
}
