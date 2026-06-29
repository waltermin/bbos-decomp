package javax.microedition.midlet;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.PushRegistry;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.microedition.io.UDPDatagramConnection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.udp.Protocol;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.firewall.FirewallDialog;
import net.rim.device.internal.io.PushRegistryHelper;
import net.rim.device.internal.ui.PopupDialogWorkerThread;

final class MIDletMain$MIDletPushRegistryWorkerThread extends Thread {
   private String _connectionString;
   private Connection _connection;
   private boolean _shutdown;
   private boolean _resume;
   private boolean _paused;
   private boolean _askUser;
   private boolean _allowConnection;
   private final MIDletMain this$0;
   private static final long LOGGER_GUID;
   private static final String LOGGER_NAME;

   public MIDletMain$MIDletPushRegistryWorkerThread(MIDletMain _1, String connection) {
      this.this$0 = _1;
      this._askUser = true;
      this._connectionString = connection;
      EventLogger.register(3284866745973826674L, "MIDletPushRegistryWorkerThread", 2);
   }

   public final void shutdown() {
      synchronized (this) {
         this._shutdown = true;

         try {
            if (this._connection != null) {
               this._connection.close();
            }
         } catch (IOException var4) {
         }
      }
   }

   public final synchronized void pause() {
      this._paused = true;
   }

   public final synchronized void resume() {
      if (this._paused) {
         this._resume = true;
         this.notify();
      }
   }

   @Override
   public final void run() {
      while (true) {
         try {
            String hostString = null;
            synchronized (this) {
               if (this._paused) {
                  this.waitForResume();
               }

               if (this._shutdown) {
                  return;
               }

               this._connection = Connector.open(this._connectionString);
               if (null == this._connection) {
                  throw new ConnectionNotFoundException(this._connectionString + " not supported");
               }
            }

            Connection c = null;
            PushRegistryHelper prh = PushRegistryHelper.getInstance();
            if (!(this._connection instanceof StreamConnectionNotifier)) {
               if (!(this._connection instanceof MessageConnection)) {
                  if (this._connection instanceof UDPDatagramConnection) {
                     UDPDatagramConnection udc = (UDPDatagramConnection)this._connection;
                     Datagram d = null;
                     if (!(udc instanceof Protocol)) {
                        d = udc.newDatagram(udc.getMaximumLength());
                     } else {
                        Protocol p = (Protocol)udc;
                        d = p.newMidletDatagram(null, udc.getMaximumLength(), null);
                     }

                     udc.receive(d);
                     hostString = d.getAddress();
                     String scheme = "datagram";
                     URL u;
                     if (hostString.startsWith(scheme)) {
                        u = new URL(hostString);
                     } else {
                        u = new URL(scheme, hostString);
                     }

                     hostString = u.getHost();
                     MIDletMain$UDPDatagramConnection_PushRegistryConnectionWrapper udc_prcw = new MIDletMain$UDPDatagramConnection_PushRegistryConnectionWrapper(
                        udc
                     );
                     udc_prcw.pushBack(d);
                     c = udc_prcw;
                  }
               } else {
                  MessageConnection mc = (MessageConnection)this._connection;
                  MIDletMain$MessageConnection_PushRegistryConnectionWrapper mc_prcw = new MIDletMain$MessageConnection_PushRegistryConnectionWrapper(mc);
                  prh.put(this._connectionString, mc_prcw);
                  Message m = ((net.rim.device.cldc.io.sms.Protocol)mc).receiveInternal();
                  if (m == null) {
                     throw new IOException("PushRegistry connection closed");
                  }

                  hostString = m.getAddress();
                  mc_prcw.pushBack(m);
                  c = mc_prcw;
               }
            } else {
               StreamConnectionNotifier scn = (StreamConnectionNotifier)this._connection;
               StreamConnection sc = scn.acceptAndOpen();
               MIDletMain$StreamConnectionNotifier_PushRegistryConnectionWrapper sc_prcw = null;
               if (!(sc instanceof HttpServerConnection)) {
                  if (!(sc instanceof SocketConnection) || !(this._connection instanceof ServerSocketConnection)) {
                     throw new ConnectionNotFoundException(this._connectionString + " not supported");
                  }

                  SocketConnection socket = (SocketConnection)sc;
                  ServerSocketConnection ssc = (ServerSocketConnection)this._connection;
                  hostString = socket.getAddress();
                  sc_prcw = new MIDletMain$ServerSocketConnection_PushRegistryConnectionWrapper(ssc);
               } else {
                  HttpServerConnection http = (HttpServerConnection)sc;
                  hostString = http.getRequestURI();
                  sc_prcw = new MIDletMain$StreamConnectionNotifier_PushRegistryConnectionWrapper(scn);
               }

               sc_prcw.pushBack(sc);
               c = sc_prcw;
            }

            if (this.checkFilters(hostString)) {
               if (this._askUser) {
                  String[] args = new String[]{this.this$0._moduleClassName, this._connectionString, hostString};
                  FirewallDialog fd = new FirewallDialog(args, false, MIDletMain._resources.getString(10066));
                  PopupDialogWorkerThread pdwt = new PopupDialogWorkerThread(fd);
                  pdwt.doWork();
                  this._allowConnection = fd.getSelectedValue() == 2;
                  if (fd.isProtocolChecked()) {
                     this._askUser = false;
                  }
               }

               if (this._allowConnection) {
                  prh.put(this._connectionString, c);
                  synchronized (this) {
                     this.this$0.bringToForeground();
                     this._paused = true;
                     this.waitForResume();
                  }
               }
            }

            this._connection.close();
            prh.remove(this._connectionString);
         } catch (ConnectionNotFoundException cnfe) {
            EventLogger.logEvent(3284866745973826674L, cnfe.toString().getBytes(), 3);
            throw new RuntimeException(cnfe.toString());
         } catch (IOException e) {
            EventLogger.logEvent(3284866745973826674L, e.toString().getBytes(), 3);
         }

         synchronized (this) {
            if (this._shutdown) {
               return;
            }
         }
      }
   }

   private final synchronized void waitForResume() {
      if (!this._resume) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
         }
      }

      this._resume = false;
      this._paused = false;
   }

   private final boolean checkFilters(String hostString) {
      String pattern = PushRegistry.getFilter(this._connectionString);
      return new SimplePatternMatch(pattern).match(hostString);
   }
}
