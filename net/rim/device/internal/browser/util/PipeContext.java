package net.rim.device.internal.browser.util;

public final class PipeContext {
   public boolean _readClosed;
   public int _currentReadPos;
   public int _currentPacket;
   public int _availableBytes = Integer.MAX_VALUE;
   public int _numRead;
}
