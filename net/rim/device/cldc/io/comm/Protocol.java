package net.rim.device.cldc.io.comm;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.IOPort;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.USBPortInternal;

public final class Protocol implements StreamConnection, USBPortListener, ConnectionBaseInterface {
   private InputStream _in;
   private OutputStream _out;
   private IOPort _port;
   private Protocol$Semaphore _readSemaphore = new Protocol$Semaphore();
   private Protocol$Semaphore _writeSemaphore = new Protocol$Semaphore();
   private IOException _exception;
   private int _channel;
   static final int BUFFER_SIZE = 1024;

   @Override
   public final void close() {
      ApplicationControl.assertLocalConnectionAllowed(true);
      Application.getApplication().removeIOPortListener(this);
      if (this._port != null) {
         this._port.close();
         this._port = null;
      }

      USBPortInternal.deregisterChannel(this._channel);
      this._exception = null;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws ConnectionNotFoundException {
      ApplicationControl.assertLocalConnectionAllowed(true);
      if ((mode & 3) != mode) {
         throw new IllegalArgumentException();
      }

      String channelName = null;
      if (!name.startsWith("USB")) {
         throw new ConnectionNotFoundException();
      }

      String[] params = new String[]{";baudrate=", ";bitsperchar=", ";stopbits=", ";parity=", ";channel="};

      for (int i = 0; i < params.length; i++) {
         int start = name.indexOf(params[i]);
         if (start != -1) {
            int end = name.indexOf(59, start + 1);
            if (end == -1) {
               end = name.length();
            }

            String value = name.substring(start + params[i].length(), end);

            try {
               switch (i) {
                  case 4:
                     channelName = value;
               }
            } catch (NumberFormatException ex) {
               throw new IllegalArgumentException(value);
            }
         }
      }

      this._channel = USBPortInternal.registerChannel(channelName, 1024, 1024);
      Application.getApplication().addIOPortListener(this);
      return this;
   }

   @Override
   public final int getProperties(String name) {
      return 128;
   }

   final void write(byte[] buffer, int offset, int length) {
      ApplicationControl.assertLocalConnectionAllowed(true);

      while (true) {
         synchronized (this._writeSemaphore) {
            if (this._exception != null) {
               throw this._exception;
            }

            if (this._port != null && this._writeSemaphore.ready) {
               this._writeSemaphore.ready = false;
               int len = length;
               if (len > 1024) {
                  len = 1024;
               }

               int ret = this._port.write(buffer, offset, len);
               offset += ret;
               length -= ret;
               if (length == 0) {
                  return;
               }
            }

            EventThreadCheck.throwException();

            try {
               this._writeSemaphore.wait();
            } catch (InterruptedException var8) {
            }
         }
      }
   }

   final int read(byte[] buffer, int offset, int length) {
      ApplicationControl.assertLocalConnectionAllowed(true);

      while (true) {
         synchronized (this._readSemaphore) {
            if (this._exception != null) {
               throw this._exception;
            }

            if (this._port != null && this._readSemaphore.ready) {
               if (length < this._readSemaphore.length) {
                  throw new IOException("read buffer too small");
               }

               length = this._readSemaphore.length;
               this._readSemaphore.length = 0;
               int ret = this._port.read(buffer, offset, length);
               this._readSemaphore.ready = false;
               if (ret > 0) {
                  return ret;
               }
            }

            EventThreadCheck.throwException();

            try {
               this._readSemaphore.wait();
            } catch (InterruptedException var7) {
            }
         }
      }
   }

   @Override
   public final OutputStream openOutputStream() {
      if (this._out == null) {
         this._out = new IOPortOutputStream(this);
      }

      return this._out;
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return new DataOutputStream(this.openOutputStream());
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public final InputStream openInputStream() {
      if (this._in == null) {
         this._in = new IOPortInputStream(this);
      }

      return this._in;
   }

   @Override
   public final void connected() {
      synchronized (this._readSemaphore) {
         this._readSemaphore.notify();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.ready = true;
         this._writeSemaphore.notify();
      }
   }

   @Override
   public final void disconnected() {
      this._exception = new IOException("Disconnected");
      synchronized (this._readSemaphore) {
         this._readSemaphore.ready = false;
         this._readSemaphore.notify();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.ready = false;
         this._writeSemaphore.notify();
      }
   }

   @Override
   public final void receiveError(int error) {
      String s = null;
      switch (error) {
         case 1:
            break;
         case 2:
         default:
            s = "Parity error";
            break;
         case 3:
            s = "Framing error";
            break;
         case 4:
            s = "Buffer overrun";
      }

      this._exception = new IOException(s);
      synchronized (this._readSemaphore) {
         this._readSemaphore.notify();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notify();
      }
   }

   @Override
   public final void dataReceived(int length) {
      synchronized (this._readSemaphore) {
         this._readSemaphore.ready = true;
         this._readSemaphore.length = length;
         this._readSemaphore.notify();
      }
   }

   @Override
   public final void dataSent() {
      synchronized (this._writeSemaphore) {
         this._writeSemaphore.ready = true;
         this._writeSemaphore.notify();
      }
   }

   @Override
   public final void patternReceived(byte[] pattern) {
   }

   @Override
   public final int getChannel() {
      return this._channel;
   }

   @Override
   public final void dataNotSent() {
      this._exception = new IOException("Data not sent");
      synchronized (this._writeSemaphore) {
         this._writeSemaphore.ready = true;
         this._writeSemaphore.notify();
      }
   }

   @Override
   public final void connectionRequested() {
      ApplicationControl.assertLocalConnectionAllowed(true);

      try {
         this._port = new USBPortInternal(this._channel);
      } catch (IOException ioe) {
         this._exception = ioe;
      }

      synchronized (this._readSemaphore) {
         this._readSemaphore.notify();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notify();
      }
   }
}
