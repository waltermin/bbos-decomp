package net.rim.plazmic.internal.contentpreview.device.dispatcherclient;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.rim.plazmic.internal.contentpreview.MishandleException;
import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherServiceException;
import net.rim.plazmic.internal.contentpreview.dispatcher.ServiceInfo;
import net.rim.plazmic.internal.contentpreview.dispatcher.client.AbstractDispatcher;
import net.rim.plazmic.internal.contentpreview.dispatcher.io.DispatcherMessageReader;
import net.rim.plazmic.internal.contentpreview.dispatcher.io.DispatcherMessageWriter;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.Model;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.ModelBuilder;
import net.rim.plazmic.internal.contentpreview.message.MessageFormatException;
import net.rim.plazmic.internal.contentpreview.message.MessageNotFoundException;

public final class DispatcherClient extends AbstractDispatcher {
   private StreamConnection _connection;
   private OutputStream _out;
   private InputStream _in;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/device/dispatcherclient/DispatcherClient.java#1 $";
   private static final boolean USE_DIRECT_TCP = true;
   private static final boolean DEBUG = false;

   public DispatcherClient() {
      ServiceInfo info = ServiceInfo.getDispatcherServiceInfo();
      String url = ((StringBuffer)(new Object("socket://"))).append(info.getHost()).append(":").append(info.getPort()).append(";deviceside=true").toString();
      this._connection = (StreamConnection)Connector.open(url);
      this._out = this._connection.openOutputStream();
      this._in = this._connection.openInputStream();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final synchronized Model request(Model req) throws DispatcherServiceException {
      ModelBuilder builder = new ModelBuilder();

      MishandleException eme;
      try {
         try {
            req.toEvent(new DispatcherMessageWriter(this._out));
            DispatcherMessageReader.parse(this._in, builder);
            return builder.getModel();
         } catch (MessageNotFoundException var8) {
            throw new DispatcherServiceException(var8);
         } catch (MessageFormatException var9) {
            throw new DispatcherServiceException(var9);
         } catch (MishandleException var10) {
            eme = var10;
         }
      } catch (Throwable var11) {
         throw new DispatcherServiceException(ioe);
      }

      throw new DispatcherServiceException(eme);
   }

   public final synchronized void close() {
      this._connection.close();
   }
}
