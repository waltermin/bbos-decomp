package net.rim.device.cldc.io.btspp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.bluetooth.BluetoothSerialPort;

public final class BluetoothStreamConnection implements SocketConnection {
   private boolean _connected;
   private int _bytesAvailable;
   private Object _parentSemaphore;
   private Object _readSemaphore;
   private Object _writeSemaphore;
   private InputStream _input;
   private OutputStream _output;
   private BluetoothSerialPort _port;

   @Override
   public final void close() {
      synchronized (this._parentSemaphore) {
         if (this._connected) {
            this._connected = false;
            this._port.disconnect();
         }
      }
   }

   public final void dataReceived(int length) {
      synchronized (this._readSemaphore) {
         this._bytesAvailable += length;
         this._readSemaphore.notifyAll();
      }
   }

   public final void dataSent() {
      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notifyAll();
      }
   }

   public final byte[] getRemoteAddress() {
      synchronized (this._parentSemaphore) {
         return !this._connected ? null : this._port.getRemoteAddress();
      }
   }

   public final void disconnected() {
      synchronized (this._parentSemaphore) {
         this._connected = false;
      }

      synchronized (this._readSemaphore) {
         this._readSemaphore.notifyAll();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notifyAll();
      }
   }

   public final boolean isConnected() {
      synchronized (this._parentSemaphore) {
         return this._connected;
      }
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return new DataOutputStream(this._output);
   }

   @Override
   public final OutputStream openOutputStream() {
      return this._output;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this._input);
   }

   @Override
   public final InputStream openInputStream() {
      return this._input;
   }

   @Override
   public final void setSocketOption(byte option, int value) {
   }

   @Override
   public final int getSocketOption(byte option) {
      return -1;
   }

   @Override
   public final String getLocalAddress() {
      return null;
   }

   @Override
   public final int getLocalPort() {
      return -1;
   }

   @Override
   public final String getAddress() {
      return null;
   }

   @Override
   public final int getPort() {
      return -1;
   }

   public BluetoothStreamConnection(Object parentSemaphore, BluetoothSerialPort port) {
      this._parentSemaphore = parentSemaphore;
      this._port = port;
      this._readSemaphore = new Object();
      this._writeSemaphore = new Object();
      this._input = new BluetoothStreamConnection$BluetoothInputStream(this);
      this._output = new BluetoothStreamConnection$BluetoothOutputStream(this);
      this._bytesAvailable = 0;
      this._connected = true;
   }

   static final int access$110(BluetoothStreamConnection x0) {
      return x0._bytesAvailable--;
   }

   static final int access$120(BluetoothStreamConnection x0, int x1) {
      return x0._bytesAvailable -= x1;
   }
}
