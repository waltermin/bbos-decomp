package net.rim.device.internal.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

public final class HTTPBufferingManager extends Thread {
   private InputStream _subIn;
   private HttpConnection _inputConnection;
   private byte[] _buffer;
   private int _readOffset;
   private int _writeOffset;
   private int _dataLength;
   private long _totalBytesBuffered;
   private long _totalInputLength;
   private final Object _lock;
   private boolean _shutdown;
   private int _progressStatusId;
   private HTTPBufferingCallback _callback;
   private long _estimatedTime;
   private int _consumeRate;
   static final int MAX_STREAMING_BUFFER_SIZE = 2097152;
   static final int MAX_BUFFER_SIZE_FOR_COMPLETE_READ = 1048576;
   static final int DEFAULT_STREAMING_BUFFER_SIZE = 524288;
   static final int RESIZE_INTERVAL = 8192;
   static final int READ_INTERVAL = 6144;
   private static final int WATERMARK_CHECK_INTERVAL = 30720;

   public HTTPBufferingManager(HttpConnection input, InputStream inputStream, HTTPBufferingCallback callback) {
      this._inputConnection = input;
      this._subIn = inputStream;
      this._callback = callback;
      this._lock = this;
      this._totalInputLength = this.getInputLength(input);
      if (this._totalInputLength != -1 && this._totalInputLength <= 1048576) {
         this._buffer = new byte[(int)this._totalInputLength];
      } else {
         this._buffer = new byte[524288];
      }

      this._progressStatusId = Application.getApplication().invokeLater(new HTTPBufferingManager$UpdateBufferRunner(this), 1000, true);
   }

   private final long getInputLength(HttpConnection input) {
      long length = input.getLength();
      if (length > -1) {
         try {
            String encoding = input.getHeaderField("Content-Encoding");
            if (StringUtilities.strEqualIgnoreCase(encoding, "gzip", 1701707776) || StringUtilities.strEqualIgnoreCase(encoding, "deflate", 1701707776)) {
               return -1;
            }
         } catch (IOException var5) {
         }
      }

      return length;
   }

   @Override
   public final synchronized void start() {
      if (!this.isAlive()) {
         super.start();
      }
   }

   public final void shutdown() {
      this._shutdown = true;

      try {
         this._subIn.close();
      } catch (IOException var5) {
      }

      try {
         this._inputConnection.close();
      } catch (IOException var4) {
      }

      synchronized (this._lock) {
         this._lock.notifyAll();
      }
   }

   public final void setHTTPBufferingCallback(HTTPBufferingCallback callback) {
      this._callback = callback;
   }

   public final byte[] getBuffer() {
      return this._buffer;
   }

   public final long getStreamLength() {
      return this.bufferContainsAllContent() ? this._buffer.length : this._totalInputLength;
   }

   public final void setStreamLength(long length) {
      this._totalInputLength = length;
   }

   public final boolean bufferWillContainAllContent() {
      return this.bufferContainsAllContent() || this._totalInputLength == this._buffer.length;
   }

   public final boolean bufferContainsAllContent() {
      return this._totalInputLength == -1
         ? this._shutdown && this._totalBytesBuffered == this._buffer.length
         : this._totalInputLength == this._totalBytesBuffered && this._totalInputLength == this._buffer.length;
   }

   public final InputStream getInputStream() {
      return this.bufferContainsAllContent() ? new ByteArrayInputStream(this._buffer) : new HTTPBufferingManager$HTTPBufferedInputStream(this);
   }

   public final void setEstimatedTime(long time) {
      if (this._totalInputLength != this._buffer.length) {
         this._estimatedTime = time;
         if (this._totalInputLength != -1 && time > 0) {
            this.setBandwidthRequired((int)((float)this._totalInputLength / ((float)time / 1148846080)));
         }
      }
   }

   public final void setBandwidthRequired(int bytesPerSecond) {
      this._consumeRate = bytesPerSecond;
   }

   private final void resizeBuffer(int newBufferSize) {
      int oldBufferSize = this._buffer.length;
      Array.resize(this._buffer, newBufferSize);
      if (this._readOffset >= this._writeOffset) {
         int diff = newBufferSize - oldBufferSize;
         System.arraycopy(this._buffer, this._readOffset, this._buffer, this._readOffset + diff, oldBufferSize - this._readOffset);
         this._readOffset += diff;
      }
   }

   private final void notifyCallbackBufferReady(HTTPBufferingCallback callback) {
      if (callback != null) {
         callback.streamingBufferReady();
      }
   }

   private final void notifyCallbackStreamingDone(HTTPBufferingCallback callback, double percent) {
      if (callback != null) {
         callback.streamingDone(percent);
      }
   }

   private final void notifyCallbackBufferStatus(HTTPBufferingCallback callback, long buffered, long expected) {
      if (callback != null) {
         callback.updateStreamingBufferStatus(buffered, expected);
      }
   }

   @Override
   public final String toString() {
      return "HTTPBufferingManager[" + this.hashCode() + "]";
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean calledReady = false;
      long startTime = InternalServices.getUptime();
      int watermarkCheckSize = 30720;

      while (true) {
         boolean var25 = false /* VF: Semaphore variable */;

         try {
            var25 = true;
            if (this._shutdown) {
               var25 = false;
               break;
            }

            int bytesToRead = 0;
            synchronized (this._lock) {
               if (!calledReady && watermarkCheckSize <= 0) {
                  watermarkCheckSize = 30720;
                  long now = InternalServices.getUptime();
                  if (now - startTime >= 3000 && this._consumeRate > 0) {
                     long transferRate = this._totalBytesBuffered / ((now - startTime) / 1000);
                     if (this._consumeRate * 6 / 5 <= transferRate) {
                        this.notifyCallbackBufferReady(this._callback);
                        calledReady = true;
                     } else {
                        long secondsLeftToTransfer = (this._totalInputLength - this._totalBytesBuffered) * transferRate;
                        long secondsToPlay = this._totalBytesBuffered / this._consumeRate;
                        if (secondsToPlay * 6 / 5 < secondsLeftToTransfer) {
                           this.notifyCallbackBufferReady(this._callback);
                           calledReady = true;
                        }
                     }
                  }
               }

               if (this._dataLength == this._buffer.length) {
                  long now = InternalServices.getUptime();
                  if (this._estimatedTime > 0 && startTime < now) {
                     long transferRate = this._totalBytesBuffered / ((now - startTime) / 1000);
                     if (this._consumeRate > transferRate) {
                        int newBufferSize = (int)((this._consumeRate - transferRate) * (this._estimatedTime / 1000));
                        if (newBufferSize > this._buffer.length << 1) {
                           newBufferSize = this._buffer.length << 1;
                        }

                        if (newBufferSize > 2097152) {
                           newBufferSize = 2097152;
                        }

                        if (newBufferSize > this._buffer.length) {
                           this.resizeBuffer(newBufferSize);
                        }
                     }
                  }

                  if (this._totalInputLength != -1
                     && this._totalInputLength <= 1048576
                     && this._dataLength == this._buffer.length
                     && this._totalBytesBuffered < 1048576) {
                     this.resizeBuffer(this._buffer.length + 8192);
                  }

                  while (!this._shutdown && this._dataLength == this._buffer.length) {
                     this.notifyCallbackBufferReady(this._callback);
                     calledReady = true;

                     try {
                        this._lock.wait();
                     } catch (InterruptedException var28) {
                     }
                  }
               }
            }

            int nextWriteOffset = this._writeOffset == this._buffer.length ? 0 : this._writeOffset;
            if (nextWriteOffset >= this._readOffset) {
               bytesToRead = this._buffer.length - nextWriteOffset;
            } else {
               bytesToRead = this._readOffset - nextWriteOffset;
            }

            if (bytesToRead > 6144) {
               bytesToRead = 6144;
            }

            int numRead = this._subIn.read(this._buffer, nextWriteOffset, bytesToRead);
            if (numRead == -1) {
               synchronized (this._lock) {
                  this._totalInputLength = this._totalBytesBuffered;
                  if (this._buffer.length > this._totalBytesBuffered) {
                     Array.resize(this._buffer, (int)this._totalBytesBuffered);
                  }

                  this._lock.notifyAll();
                  var25 = false;
                  break;
               }
            }

            watermarkCheckSize -= numRead;
            synchronized (this._lock) {
               this._writeOffset = nextWriteOffset + numRead;
               this._dataLength += numRead;
               this._totalBytesBuffered += numRead;
               this._lock.notifyAll();
               continue;
            }
         } catch (IOException var30) {
            var25 = false;
         } finally {
            if (var25) {
               Application.getApplication().cancelInvokeLater(this._progressStatusId);
               if (!this._shutdown) {
                  this.shutdown();
               }

               if (this._totalInputLength <= 0) {
                  this.notifyCallbackStreamingDone(this._callback, (double)4607182418800017408L);
               } else {
                  this.notifyCallbackStreamingDone(this._callback, (double)this._totalBytesBuffered / this._totalInputLength);
               }
            }
         }

         Application.getApplication().cancelInvokeLater(this._progressStatusId);
         if (!this._shutdown) {
            this.shutdown();
         }

         if (this._totalInputLength <= 0) {
            this.notifyCallbackStreamingDone(this._callback, (double)4607182418800017408L);
            return;
         }

         this.notifyCallbackStreamingDone(this._callback, (double)this._totalBytesBuffered / this._totalInputLength);
         return;
      }

      Application.getApplication().cancelInvokeLater(this._progressStatusId);
      if (!this._shutdown) {
         this.shutdown();
      }

      if (this._totalInputLength <= 0) {
         this.notifyCallbackStreamingDone(this._callback, (double)4607182418800017408L);
      } else {
         this.notifyCallbackStreamingDone(this._callback, (double)this._totalBytesBuffered / this._totalInputLength);
      }
   }

   static final int access$512(HTTPBufferingManager x0, int x1) {
      return x0._readOffset += x1;
   }

   static final int access$620(HTTPBufferingManager x0, int x1) {
      return x0._dataLength -= x1;
   }
}
