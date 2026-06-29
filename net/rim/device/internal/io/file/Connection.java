package net.rim.device.internal.io.file;

import java.io.IOException;
import net.rim.device.internal.system.USBPortInternal;

final class Connection {
   USBPortInternal _port;
   Semaphore _readSemaphore = new Semaphore();
   Semaphore _writeSemaphore = new Semaphore();
   ReceiveThread _receiveThread;
   IOException _exception;
   int _writeBufferSize;
   int _readBufferSize;

   final void start() {
      synchronized (this._writeSemaphore) {
         this._writeSemaphore._ready = true;
      }

      this._receiveThread = new ReceiveThread(this);
      this._receiveThread.start();
   }

   final void stop() {
      if (this._receiveThread != null) {
         this._receiveThread.shutdown();
         this._receiveThread = null;
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notify();
      }

      synchronized (this._readSemaphore) {
         this._readSemaphore.notify();
      }

      if (this._port != null) {
         this._port.close();
         this._port = null;
      }
   }

   final void sendRequest(UpdateRequest request) {
      if (this._receiveThread != null) {
         this._receiveThread.sendRequest(request);
      }
   }
}
