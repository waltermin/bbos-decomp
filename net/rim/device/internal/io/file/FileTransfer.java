package net.rim.device.internal.io.file;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.USBPortInternal;

public final class FileTransfer implements USBPortListener, FileSystemJournalListener, FileSystemListener {
   private int _channel;
   private Connection _connection;
   private int _readBufferSize;
   private int _writeBufferSize;
   private long _firstUnprocessedUSN;

   private FileTransfer() {
      if (USBPortInternal.isSupported()) {
         try {
            this._readBufferSize = USBPortInternal.getMaximumRxSize();
            this._writeBufferSize = USBPortInternal.getMaximumTxSize();
            this._channel = USBPortInternal.registerChannel("RIM_DFTP", this._readBufferSize, this._writeBufferSize);
            ProtocolDaemon.getInstance().addIOPortListener(this);
         } finally {
            return;
         }
      }
   }

   public static final void register() {
      new FileTransfer();
   }

   @Override
   public final synchronized void connected() {
      if (this._connection != null) {
         this._connection.start();
      }

      Proxy proxy = Proxy.getInstance();
      this._firstUnprocessedUSN = FileSystemJournal.getNextUSN();
      FileSystem.addJournalListener(this, proxy, true);
   }

   @Override
   public final synchronized void disconnected() {
      FileSystem.removeJournalListener(this);
      if (this._connection != null) {
         this._connection.stop();
         this._connection = null;
      }
   }

   @Override
   public final void dataReceived(int length) {
      Connection conn;
      synchronized (this) {
         conn = this._connection;
      }

      if (conn != null) {
         synchronized (conn._readSemaphore) {
            conn._readSemaphore._ready = true;
            conn._readSemaphore._length = length;
            conn._readSemaphore.notify();
         }
      }
   }

   @Override
   public final void dataSent() {
      Connection conn;
      synchronized (this) {
         conn = this._connection;
      }

      if (conn != null) {
         synchronized (conn._writeSemaphore) {
            conn._writeSemaphore._ready = true;
            conn._writeSemaphore.notify();
         }
      }
   }

   @Override
   public final void receiveError(int error) {
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
      Connection conn;
      synchronized (this) {
         conn = this._connection;
      }

      if (conn != null) {
         synchronized (conn._readSemaphore) {
            conn._readSemaphore._ready = true;
            conn._readSemaphore.notify();
         }
      }
   }

   @Override
   public final synchronized void connectionRequested() {
      if (this._connection != null) {
         this._connection.stop();
      }

      try {
         this._connection = new Connection();
         this._connection._port = new USBPortInternal(this._channel);
         this._connection._writeBufferSize = this._writeBufferSize;
         this._connection._readBufferSize = this._readBufferSize;
      } finally {
         this._connection = null;
         return;
      }
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      HttpHeaders headers = new HttpHeaders();
      switch (state) {
         case -1:
            break;
         case 0:
         default:
            headers.setProperty("Action-type", "ROOT_ADD");
            break;
         case 1:
            headers.setProperty("Action-type", "ROOT_REMOVE");
      }

      this.sendUpdateRequest(rootName, headers);
   }

   @Override
   public final void fileJournalChanged() {
      long nextUSN = FileSystemJournal.getNextUSN();

      for (long i = nextUSN - 1; i >= this._firstUnprocessedUSN; i -= 1) {
         FileSystemJournalEntry journalEntry = FileSystem.getJournalEntry(i);
         if (journalEntry == null) {
            break;
         }

         HttpHeaders headers = new HttpHeaders();
         switch (journalEntry.getEvent()) {
            case -1:
            case 2:
               break;
            case 0:
            default:
               headers.setProperty("Action-type", "ADD");
               break;
            case 1:
               headers.setProperty("Action-type", "DELETE");
               break;
            case 3:
               headers.setProperty("Action-type", "RENAME");
               String originalFilename = journalEntry.getOldPath();
               if (originalFilename != null) {
                  headers.setProperty("Original-Filename", originalFilename);
               }
         }

         this.sendUpdateRequest(journalEntry.getPath(), headers);
      }

      this._firstUnprocessedUSN = nextUSN;
   }

   private final void sendUpdateRequest(String uri, HttpHeaders headers) {
      if (this._connection != null) {
         try {
            this._connection.sendRequest(new UpdateRequest(uri, headers));
         } finally {
            return;
         }
      }
   }
}
