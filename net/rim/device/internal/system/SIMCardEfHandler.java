package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardEFListener;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.ui.UiApplication;

public class SIMCardEfHandler implements Runnable, SIMCardEFListener {
   private int _efId;
   private int _code;
   private byte[] _buffer;
   private int _fileStatus;
   private int _fileStructure;
   private int _fileSize;
   private int _recordLength;
   private int _numRecords;
   private int _recordNumber;
   private int _state;
   private SIMCardEfTask _task;
   private SIMCardEfHandlerCallback _callback;
   private SIMCardEfHandler$PleaseWaitDialog _dialog;
   private Application _application;
   private Object _gate;
   private static final int STATE_IDLE = 0;
   private static final int STATE_RUNNING = 1;
   private static final int STATE_INFO_WAIT = 2;
   private static final int STATE_READ_WAIT = 3;
   private static final int STATE_WRITE_WAIT = 4;

   public void startTask(SIMCardEfTask task, boolean wait) {
      if (task == null) {
         throw new IllegalArgumentException();
      }

      if (this._task != null) {
         throw new IllegalStateException();
      }

      Application app = this._application = Application.getApplication();
      if (app == null) {
         throw new NullPointerException();
      }

      this._task = task;
      this._state = 1;
      if (wait && !app.isEventThread()) {
         this.run();
      } else {
         new Thread(this).start();
         if (wait) {
            this.waitForComplete();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized int writeRequest(int efId, int structure, int record, byte[] buffer) {
      if (this._state != 1) {
         throw new IllegalStateException();
      }

      this._efId = efId;
      this._fileStructure = structure;
      this._recordNumber = record;
      this._buffer = buffer;
      this._state = 4;
      boolean var10 = false /* VF: Semaphore variable */;

      label59: {
         label60: {
            try {
               var10 = true;
               SIMCard.requestEFWrite(this._efId, this._fileStructure, this._recordNumber, this._buffer);
               this.wait();
               var10 = false;
               break label59;
            } catch (InterruptedException ie) {
               this._code = 11;
               var10 = false;
               break label60;
            } catch (SIMCardException scEx) {
               this._code = 11;
               var10 = false;
            } finally {
               if (var10) {
                  this._state = 1;
               }
            }

            this._state = 1;
            return this._code;
         }

         this._state = 1;
         return this._code;
      }

      this._state = 1;
      return this._code;
   }

   public boolean isRunning() {
      return this._task != null;
   }

   public int getReturnCode() {
      return this._code;
   }

   public int getFileStatus() {
      return this._fileStatus;
   }

   public int getFileStructure() {
      return this._fileStructure;
   }

   public int getFileSize() {
      return this._fileSize;
   }

   public int getRecordLength() {
      return this._recordLength;
   }

   public int getNumRecords() {
      return this._numRecords;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized int infoRequest(int efId) {
      this._efId = efId;
      if (this._state != 1) {
         throw new IllegalStateException();
      }

      boolean var7 = false /* VF: Semaphore variable */;

      label57: {
         label58: {
            try {
               var7 = true;
               this._state = 2;
               SIMCard.requestEFInfo(this._efId);
               this.wait();
               var7 = false;
               break label57;
            } catch (InterruptedException ie) {
               this._code = 11;
               var7 = false;
               break label58;
            } catch (SIMCardException scEx) {
               this._code = 11;
               var7 = false;
            } finally {
               if (var7) {
                  this._state = 1;
               }
            }

            this._state = 1;
            return this._code;
         }

         this._state = 1;
         return this._code;
      }

      this._state = 1;
      return this._code;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized int readRequest(int efId, int structure, int record, byte[] buffer) {
      int len = -1;
      if (this._state != 1) {
         throw new IllegalStateException();
      }

      this._efId = efId;
      this._fileStructure = structure;
      this._recordNumber = record;
      this._buffer = buffer;
      this._state = 3;
      boolean var11 = false /* VF: Semaphore variable */;

      label80: {
         label81: {
            try {
               var11 = true;
               int scEx = 2;

               while (--scEx >= 0) {
                  len = SIMCard.requestEFRead(this._efId, this._fileStructure, this._recordNumber, this._buffer);
                  if (len >= 0) {
                     var11 = false;
                     break label80;
                  }

                  this.wait();
               }

               var11 = false;
               break label80;
            } catch (InterruptedException ie) {
               this._code = 11;
               var11 = false;
               break label81;
            } catch (SIMCardException scEx) {
               this._code = 11;
               var11 = false;
            } finally {
               if (var11) {
                  this._state = 1;
               }
            }

            this._state = 1;
            return this._code;
         }

         this._state = 1;
         return this._code;
      }

      this._state = 1;
      return this._code;
   }

   public int readRequest(int record, byte[] buffer) {
      return this.readRequest(this._efId, this._fileStructure, record, buffer);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      Application application = this._application;
      boolean var6 = false /* VF: Semaphore variable */;

      label57: {
         try {
            var6 = true;
            SIMCard.addListener(application, this);
            this._task.doWork(this);
            var6 = false;
            break label57;
         } catch (Exception var7) {
            var6 = false;
         } finally {
            if (var6) {
               this._state = 0;
               this.closeWaitingDialog();
               SIMCard.removeListener(application, this);
               if (this._callback != null) {
                  this._callback.taskComplete(this._code, this._task);
               }

               this._task = null;
               this._application = null;
            }
         }

         this._state = 0;
         this.closeWaitingDialog();
         SIMCard.removeListener(application, this);
         if (this._callback != null) {
            this._callback.taskComplete(this._code, this._task);
         }

         this._task = null;
         this._application = null;
         return;
      }

      this._state = 0;
      this.closeWaitingDialog();
      SIMCard.removeListener(application, this);
      if (this._callback != null) {
         this._callback.taskComplete(this._code, this._task);
      }

      this._task = null;
      this._application = null;
   }

   @Override
   public void responseEFInfo(int code, int id, int fileStatus, int structure, int fileSize, int recordLength, int numRecords) {
      synchronized (this) {
         if (this._state == 2 && this._efId == id) {
            this._fileStatus = fileStatus;
            this._fileStructure = structure;
            this._fileSize = fileSize;
            this._recordLength = recordLength;
            this._numRecords = numRecords;
            this.processResponse(code);
         }
      }
   }

   @Override
   public void responseEFRead(int code, int id, int structure, int length, int recordNumber) {
      synchronized (this) {
         if (this._state == 3 && this._efId == id) {
            this.processResponse(code);
         }
      }
   }

   @Override
   public void responseEFWrite(int code, int id, int structure, int recordNumber) {
      synchronized (this) {
         if (this._state == 4 && this._efId == id) {
            this.processResponse(code);
         }
      }
   }

   public SIMCardEfHandler(SIMCardEfHandlerCallback callback) {
      this._callback = callback;
   }

   public SIMCardEfHandler() {
   }

   private void processResponse(int code) {
      this._code = code;
      this.notify();
   }

   private void waitForComplete() {
      if (this._state != 0) {
         Application application = this._application;
         if (application.isEventThread()) {
            if (!(application instanceof UiApplication)) {
               throw new IllegalStateException();
            }

            SIMCardEfHandler$PleaseWaitDialog dialog = new SIMCardEfHandler$PleaseWaitDialog();
            if (this._state != 0) {
               this._dialog = dialog;
               ((UiApplication)application).pushModalScreen(dialog);
            }
         } else {
            try {
               if (this._gate == null) {
                  this._gate = new Object();
               }

               synchronized (this._gate) {
                  this._gate.wait();
               }
            } catch (InterruptedException var5) {
            }
         }

         this._state = 0;
      }
   }

   private void closeWaitingDialog() {
      if (this._gate != null) {
         synchronized (this._gate) {
            this._gate.notify();
         }
      } else {
         Application app = this._application;
         app.invokeAndWait(new SIMCardEfHandler$1(this, app));
      }
   }
}
