package net.rim.plazmic.internal.contentpreview.dispatcher.io;

import java.io.DataInput;
import java.io.InputStream;
import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;
import net.rim.plazmic.internal.contentpreview.message.AbstractMessageReader;
import net.rim.plazmic.internal.contentpreview.message.MessageFormatException;
import net.rim.plazmic.internal.contentpreview.message.MessageNotFoundException;

public final class DispatcherMessageReader extends AbstractMessageReader {
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/DispatcherMessageReader.java#1 $";

   public static final void parse(InputStream is, DispatcherEventHandler handler) {
      parse(is, handler, 1);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void parse(InputStream is, DispatcherEventHandler handler, int minFormatVersion) throws MessageFormatException, MessageNotFoundException {
      DataInput di = (DataInput)(new Object(is));
      int version = 1;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         int e = di.readByte();
         if (e != 76) {
            if (e != 77) {
               throw new MessageFormatException("not a dispatcher message");
            }

            version = di.readInt();
            var7 = false;
         } else {
            var7 = false;
         }
      } finally {
         if (var7) {
            throw new MessageNotFoundException();
         }
      }

      if (version < minFormatVersion) {
         throw new MessageFormatException("earlier version message");
      }

      switch (di.readByte()) {
         case 1:
            handler.openSession(AbstractMessageReader.readString(di), di.readBoolean());
            return;
         case 2:
            handler.enumerateDevices();
            return;
         case 3:
            handler.getValidDevice(AbstractMessageReader.readString(di));
            return;
         case 4:
            handler.waitForSessionReady(AbstractMessageReader.readString(di), di.readInt());
            return;
         case 5:
            handler.pushFile(AbstractMessageReader.readString(di), AbstractMessageReader.readString(di));
            return;
         case 6:
            handler.raiseWindow(AbstractMessageReader.readString(di));
            return;
         case 7:
            handler.closeSession(AbstractMessageReader.readString(di));
            return;
         case 8:
            handler.shutdownDispatcherService();
            return;
         case 9:
            handler.getRecentSession();
            return;
         case 10:
            handler.getSpecificSession(di.readInt());
            return;
         case 11:
            handler.getControlPanelPort(AbstractMessageReader.readString(di));
            return;
         case 12:
            handler.getPlaybackCommandPort(AbstractMessageReader.readString(di));
            return;
         case 13:
            handler.sessionReady(di.readInt());
            return;
         case 14:
            handler.logMessage(
               AbstractMessageReader.readString(di), di.readInt(), AbstractMessageReader.readString(di), AbstractMessageReader.readStringArray(di)
            );
            return;
         case 15:
            handler.getSessionProgress(AbstractMessageReader.readString(di));
            return;
         case 16:
            handler.getServerVersion();
            return;
         case 17:
            handler.dequeueThemeRegistrationRequest(di.readInt());
            return;
         case 18:
            handler.dequeueThemeActivationRequest(di.readInt());
            return;
         case 65:
            handler.voidMessage();
            return;
         case 66:
            handler.sessionOk(AbstractMessageReader.readString(di));
            return;
         case 67:
            handler.sessionPort(di.readInt());
            return;
         case 68:
            handler.deviceType(AbstractMessageReader.readString(di));
            return;
         case 69:
            handler.dispatcherServiceFailure(AbstractMessageReader.readString(di));
            return;
         case 70:
            handler.noSuchSession(AbstractMessageReader.readString(di));
            return;
         case 71:
            handler.invalidDevice(AbstractMessageReader.readString(di));
            return;
         case 72:
            handler.timeoutExpiry(AbstractMessageReader.readString(di));
            return;
         case 73:
            handler.filePushFailure(AbstractMessageReader.readString(di));
            return;
         case 74:
            handler.sessionProgress(di.readInt());
            return;
         case 75:
            handler.themeRequest(AbstractMessageReader.readString(di));
            return;
         case 76:
            handler.serverProperties(di.readInt());
            return;
         default:
            throw new MessageFormatException("unrecognized message type");
      }
   }
}
