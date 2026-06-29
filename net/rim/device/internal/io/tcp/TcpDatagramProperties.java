package net.rim.device.internal.io.tcp;

public class TcpDatagramProperties {
   public int _sequenceNumber;
   public int _acknowledgementNumber;
   public int _flags;
   public int _window;
   public int _urgentPointer;
   public int _maxSegmentSize;
   public boolean _sackPermitted;
   public int _windowScale;
   public TcpDataBlock[] _sackBlocks;
   public int _dataOffset;
   public boolean _isEssential;
   protected boolean _freshnessSealed = true;
   private static final int SEPARATOR = 124;

   public TcpDatagramProperties() {
   }

   public void reset() {
      this._maxSegmentSize = -1;
      this._windowScale = -1;
      if (!this._freshnessSealed) {
         this._urgentPointer = 0;
         this._sackPermitted = false;
         this._sackBlocks = null;
         this._isEssential = false;
      }

      this._freshnessSealed = false;
   }

   public TcpDatagramProperties(int sequenceNumber, int acknowledgementNumber, int dataOffset, int flags, int window, int urgentPointer, boolean isEssential) {
      this.setData(sequenceNumber, acknowledgementNumber, dataOffset, flags, window, urgentPointer, isEssential);
   }

   public void setData(int sequenceNumber, int acknowledgementNumber, int dataOffset, int flags, int window, int urgentPointer, boolean isEssential) {
      this._sequenceNumber = sequenceNumber;
      this._acknowledgementNumber = acknowledgementNumber;
      this._dataOffset = dataOffset;
      this._flags = flags;
      this._window = window;
      this._urgentPointer = urgentPointer;
      this._maxSegmentSize = -1;
      this._windowScale = -1;
      this._sackPermitted = false;
      this._sackBlocks = null;
      this._isEssential = isEssential;
   }
}
