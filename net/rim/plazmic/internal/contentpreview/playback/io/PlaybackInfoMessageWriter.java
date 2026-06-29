package net.rim.plazmic.internal.contentpreview.playback.io;

import java.io.OutputStream;
import net.rim.plazmic.internal.contentpreview.message.AbstractMessageWriter;
import net.rim.plazmic.internal.contentpreview.playback.PlaybackInfoHandler;

public final class PlaybackInfoMessageWriter extends AbstractMessageWriter implements PlaybackInfoHandler {
   private final byte[] HEADER = new byte[]{0, 64};
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/playback/PlaybackInfoMessageWriter.java#1 $";

   public PlaybackInfoMessageWriter(OutputStream out) {
      super(out);
   }

   public final void sceneTimeChanged(long msTime) {
      this.writeMessage((byte)65, msTime);
   }

   public final void sceneInfo(int version, String mime, String[] custom, String[] font, String[] image, String[] media, String[] sound) {
      this.writeMessage((byte)66, new Object(version), mime, custom, font, image, media, sound);
   }

   public final void started() {
      this.writeMessage((byte)67);
   }

   public final void stopped() {
      this.writeMessage((byte)68);
   }

   public final void rateChanged(float newRate) {
      this.writeMessage((byte)69, newRate);
   }

   public final void clientInfo(int version, int reserved) {
      this.writeMessage((byte)70, new Object(version), new Object(reserved));
   }

   @Override
   protected final byte[] getHeader() {
      return this.HEADER;
   }
}
