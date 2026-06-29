package net.rim.device.apps.internal.docview.gui;

import java.io.InputStream;
import java.util.Vector;

final class AttachmentViewerFactory$ParseThread extends Thread {
   private Object _data;
   private DocViewParser _coreData;
   private BaseParsingThread _progressThread;
   private final int _currentBlockIndex;
   private final boolean _pausable;
   private final int _totalBlockCount;
   private static final long WAIT_TIMEOUT;

   AttachmentViewerFactory$ParseThread(
      Object data, DocViewParser coreData, BaseParsingThread progressThread, int currentBlockIndex, boolean pausable, int totalBlockCount
   ) {
      this._data = data;
      this._coreData = coreData;
      this._progressThread = progressThread;
      this._currentBlockIndex = currentBlockIndex;
      this._pausable = pausable;
      this._totalBlockCount = totalBlockCount;
   }

   @Override
   public final void run() {
      new AttachmentViewerFactory$ParseThread$1(this).start();
      if (this._data instanceof byte[]) {
         this._progressThread.waitForContinueSignal(6000);
         this._coreData.parseDocument((byte[])this._data, true, this._currentBlockIndex, this._pausable, this._currentBlockIndex == this._totalBlockCount - 1);
      } else if (this._data instanceof Object) {
         this._progressThread.waitForContinueSignal(6000);
         this._coreData.parseDocument((InputStream)this._data, this._pausable);
      } else {
         if (this._data instanceof Object) {
            boolean continueParse = true;
            Vector ucsDataVector = (Vector)this._data;
            int size = ucsDataVector.size();

            for (int i = 0; continueParse && i < size; i++) {
               this._progressThread.waitForContinueSignal(6000);
               this._coreData
                  .parseDocument(
                     (byte[])ucsDataVector.elementAt(i),
                     i == 0,
                     this._currentBlockIndex + i,
                     this._pausable,
                     this._currentBlockIndex + i == this._totalBlockCount - 1
                  );
               continueParse = this._coreData.getLastParsingStatus() == 0;
            }
         }
      }
   }
}
