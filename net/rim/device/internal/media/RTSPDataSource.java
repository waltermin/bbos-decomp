package net.rim.device.internal.media;

import java.io.IOException;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.device.cldc.io.tunnel.TunnelListener;

public final class RTSPDataSource extends DataSource implements SourceStream, TunnelListener {
   private boolean _connected;
   private boolean _started;
   private String _apn;
   private String _apnUsername;
   private String _apnPassword;
   private Tunnel _tunnel;
   private int _tunnelStatus;
   private String _userAgent;
   private Object _tunnelSyncObject = new Object();
   private static final int MAX_RETRIES = 3;
   private static final int SESSION_TIMEOUT = 165000;

   public RTSPDataSource(String locator, String userAgent, String apn, String apnUsername, String apnPassword) {
      super(locator);
      this._userAgent = userAgent;
      this._apn = apn;
      this._apnUsername = apnUsername;
      this._apnPassword = apnPassword;
   }

   @Override
   public final void connect() {
      if (!this._connected) {
         for (int retryCounter = 0; retryCounter < 3; retryCounter++) {
            if (this._tunnel == null) {
               this._tunnelStatus = 0;
               TunnelConfig config = new TunnelConfig(this._apn, "rtsp", null, this._apnUsername, this._apnPassword, this);
               config.setMaxAttempts(3);
               this._tunnel = TunnelFactory.openTunnel(config);
            } else {
               synchronized (this._tunnelSyncObject) {
                  if (this._tunnelStatus != 3) {
                     this._tunnel.kick();
                  }
               }
            }

            synchronized (this._tunnelSyncObject) {
               while (this._tunnelStatus != 3) {
                  try {
                     this._tunnelSyncObject.wait(165000);
                  } catch (InterruptedException var6) {
                  }

                  if (this._tunnelStatus == 4) {
                     throw new IOException("Cannot open tunnel");
                  }
               }
            }

            synchronized (this._tunnelSyncObject) {
               if (this._tunnelStatus != 3) {
                  this.disconnect();
                  throw new IOException("Could not get active tunnel");
               }
            }
         }

         this._connected = true;
      }
   }

   @Override
   public final void disconnect() {
      if (this._connected) {
         if (this._started) {
            try {
               this.stop();
            } catch (IOException var2) {
            }
         }

         if (this._tunnel != null) {
            this._tunnel.close();
            this._tunnel = null;
         }

         this._connected = false;
      }
   }

   @Override
   public final void start() {
      if (this._connected) {
         if (!this._started) {
            ;
         }
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   public final void close() {
      try {
         this.stop();
      } catch (IOException var2) {
      }
   }

   @Override
   public final String getContentType() {
      if (this._connected) {
         return "application/rtsp";
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   @Override
   public final void stop() {
      if (this._started && this._connected) {
         this._started = false;
      }
   }

   public final int getAccessPointNumber() {
      if (this._tunnel == null) {
         throw new MediaException("no tunnel");
      }

      if (this._tunnelStatus != 3) {
         throw new MediaException("tunnel not active");
      }

      try {
         return RadioInfo.getAccessPointNumber(this._apn);
      } catch (RadioException e) {
         throw new MediaException(e.toString());
      }
   }

   public final String getUserAgent() {
      return this._userAgent;
   }

   @Override
   public final SourceStream[] getStreams() {
      return new SourceStream[]{this};
   }

   @Override
   public final ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor("application/rtsp");
   }

   @Override
   public final long getContentLength() {
      return -1;
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      return 0;
   }

   @Override
   public final int getTransferSize() {
      return -1;
   }

   @Override
   public final long seek(long where) {
      return 0;
   }

   @Override
   public final long tell() {
      return 0;
   }

   @Override
   public final int getSeekType() {
      return 2;
   }

   @Override
   public final Control[] getControls() {
      if (!this._connected) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   @Override
   public final Control getControl(String controlType) {
      if (!this._connected) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   @Override
   public final void statusChanged(int status, int code) {
      synchronized (this._tunnelSyncObject) {
         this._tunnelStatus = status;
         this._tunnelSyncObject.notify();
      }
   }
}
