package net.rim.plazmic.internal.contentpreview.playback.io;

import java.io.OutputStream;
import net.rim.plazmic.internal.contentpreview.message.AbstractMessageWriter;
import net.rim.plazmic.internal.contentpreview.playback.PlaybackCommandHandler;

public final class PlaybackCommandMessageWriter extends AbstractMessageWriter implements PlaybackCommandHandler {
   private final byte[] HEADER = new byte[]{0, 32};
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/playback/PlaybackCommandMessageWriter.java#1 $";

   public PlaybackCommandMessageWriter(OutputStream out) {
      super(out);
   }

   @Override
   public final void pause() {
      this.writeMessage((byte)34);
   }

   @Override
   public final void play() {
      this.writeMessage((byte)33);
   }

   @Override
   public final void changeRate(float rate) {
      this.writeMessage((byte)37, rate);
   }

   @Override
   public final void seek(long time) {
      this.writeMessage((byte)35, time);
   }

   @Override
   public final void seekFast(long requestTime, long targetTime) {
      this.writeMessage((byte)39, new Object(requestTime), new Object(targetTime));
   }

   @Override
   public final void skip(long amount) {
      this.writeMessage((byte)36, amount);
   }

   @Override
   public final void requestSceneInfo() {
      this.writeMessage((byte)38);
   }

   @Override
   protected final byte[] getHeader() {
      return this.HEADER;
   }
}
