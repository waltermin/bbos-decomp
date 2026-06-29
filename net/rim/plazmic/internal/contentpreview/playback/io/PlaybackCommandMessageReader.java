package net.rim.plazmic.internal.contentpreview.playback.io;

import java.io.DataInput;
import java.io.InputStream;
import net.rim.plazmic.internal.contentpreview.message.AbstractMessageReader;
import net.rim.plazmic.internal.contentpreview.message.MessageFormatException;
import net.rim.plazmic.internal.contentpreview.playback.PlaybackCommandHandler;

public final class PlaybackCommandMessageReader extends AbstractMessageReader {
   public static final String rcsid = "$Id:$";

   public static final void parse(InputStream is, PlaybackCommandHandler handler) throws MessageFormatException {
      DataInput di = (DataInput)(new Object(is));
      byte type = di.readByte();
      if (type != 0) {
         throw new MessageFormatException(((StringBuffer)(new Object("not a playback message: "))).append(type).toString());
      }

      byte subtype = di.readByte();
      if (subtype != 32) {
         throw new MessageFormatException(((StringBuffer)(new Object("not a playback command message: "))).append(subtype).toString());
      }

      byte command = di.readByte();
      switch (command) {
         case 32:
            throw new MessageFormatException(((StringBuffer)(new Object("unknown playback command: "))).append(command).toString());
         case 33:
         default:
            handler.play();
            return;
         case 34:
            handler.pause();
            return;
         case 35:
            handler.seek(di.readLong());
            return;
         case 36:
            handler.skip(di.readLong());
            return;
         case 37:
            handler.changeRate(di.readFloat());
            return;
         case 38:
            handler.requestSceneInfo();
            return;
         case 39:
            handler.seekFast(di.readLong(), di.readLong());
      }
   }
}
