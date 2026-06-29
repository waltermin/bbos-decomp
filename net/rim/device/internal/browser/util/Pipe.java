package net.rim.device.internal.browser.util;

import java.io.EOFException;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class Pipe implements Persistable {
   private boolean _writeClosed;
   private int _totalLength;
   private int _estimatedSize;
   private int _timeout = 120000;
   private boolean _readKicked;
   private boolean _notifyOnWrite;
   private byte[][] _window;
   private static final int WAIT_START = 1114666867;
   private static final int WAIT_FINISH = 1114666854;

   public Pipe() {
      this._window = new byte[1][];
      this._window[0] = new byte[0];
   }

   public Pipe(byte[] data, int length) {
      this(data, length, !Memory.isPlaintext(data));
   }

   public Pipe(byte[] data, int length, boolean groupData) {
      int maxGroupSize = Memory.getMaxGroupSize();
      int windowSize = groupData ? (length + maxGroupSize - 1) / maxGroupSize : 1;
      windowSize = windowSize == 0 ? 1 : windowSize;
      this._window = new byte[windowSize][];
      if (windowSize == 1) {
         if (data.length != length) {
            this._window[0] = Arrays.copy(data, 0, length);
         } else {
            this._window[0] = data;
         }

         if (groupData) {
            Memory.createGroup(this._window[0]);
         }
      } else {
         for (int i = 0; i < windowSize; i++) {
            this._window[i] = Arrays.copy(data, i * maxGroupSize, MathUtilities.clamp(0, length - i * maxGroupSize, maxGroupSize));
            Memory.createGroup(this._window[i]);
         }
      }

      if (groupData) {
         Memory.createGroup(this._window);
      }

      this._totalLength = length;
      this._writeClosed = true;
   }

   public Pipe(byte[][] data) {
      this(data, true);
   }

   public Pipe(byte[][] data, boolean groupData) {
      int maxGroupSize = Memory.getMaxGroupSize();
      int dataLength = data.length;
      int tooLargeCount = 0;
      if (groupData) {
         for (int i = 0; i < dataLength; i++) {
            if (data[i].length > maxGroupSize) {
               tooLargeCount += (data[i].length + maxGroupSize - 1) / maxGroupSize;
            }
         }
      }

      if (tooLargeCount == 0) {
         if (dataLength == 0) {
            this._window = new byte[1][];
            this._window[0] = new byte[0];
         } else {
            this._window = data;

            for (int i = 0; i < dataLength; i++) {
               this._totalLength = this._totalLength + data[i].length;
            }
         }
      } else {
         this._window = new byte[tooLargeCount + dataLength][];
         int currentPos = 0;

         for (int i = 0; i < dataLength; i++) {
            if (data[i].length <= maxGroupSize) {
               this._window[currentPos++] = data[i];
            } else {
               int num = (data[i].length + maxGroupSize - 1) / maxGroupSize;

               for (int j = 0; j < num; j++) {
                  this._window[currentPos++] = Arrays.copy(data[i], j * maxGroupSize, MathUtilities.clamp(0, data[i].length - j * maxGroupSize, maxGroupSize));
               }
            }

            this._totalLength = this._totalLength + data[i].length;
         }
      }

      if (groupData) {
         for (int i = this._window.length - 1; i >= 0; i--) {
            Memory.createGroup(this._window[i]);
         }

         Memory.createGroup(this._window);
      }

      this._writeClosed = true;
   }

   public final synchronized void write(byte[] bytes, int off, int length, int packetNo) {
      if (!this._writeClosed && length != 0) {
         if (off != 0 || length != bytes.length) {
            bytes = Arrays.copy(bytes, off, length);
         }

         if (packetNo >= this._window.length) {
            if (packetNo != this._window.length) {
               this._notifyOnWrite = false;
            }

            Array.resize(this._window, packetNo + 1);
         }

         this._window[packetNo] = bytes;
         this._totalLength += bytes.length;
         if (!this._notifyOnWrite) {
            this._notifyOnWrite = true;

            for (int i = 0; i < packetNo; i++) {
               if (this._window[i] == null) {
                  this._notifyOnWrite = false;
                  break;
               }
            }
         }

         if (this._notifyOnWrite && this._window[0] != null && this._window[0].length > 0) {
            this.notify();
         }
      }
   }

   public final synchronized int read(PipeContext position) {
      if (!position._readClosed && position._availableBytes > 0) {
         try {
            byte[] curPacket = this._window[position._currentPacket];
            if (position._currentReadPos < curPacket.length) {
               position._numRead++;
               if (position._availableBytes != Integer.MAX_VALUE) {
                  position._availableBytes--;
               }

               return curPacket[position._currentReadPos++] & 0xFF;
            } else {
               if (this._writeClosed) {
                  return this.fastRead(position);
               }

               while (true) {
                  int nextPos = position._currentPacket + 1;
                  if (this._window.length > nextPos && this._window[nextPos] != null && this._window[nextPos].length > 0) {
                     position._currentReadPos = 1;
                     position._currentPacket = nextPos;
                     position._numRead++;
                     if (position._availableBytes != Integer.MAX_VALUE) {
                        position._availableBytes--;
                     }

                     return this._window[position._currentPacket][0] & 0xFF;
                  }

                  try {
                     long timeBefore = System.currentTimeMillis();
                     if (TimeLogger._loggingEnabled) {
                        TimeLogger.getInstance().startTimer(13, (int)timeBefore);
                     }

                     EventLogger.logEvent(1907089860548946979L, 1114666867, 5);
                     this.wait(this._timeout);
                     EventLogger.logEvent(1907089860548946979L, 1114666854, 5);
                     if (TimeLogger._loggingEnabled) {
                        TimeLogger.getInstance().stopTimer(13, (int)timeBefore);
                     }

                     if (this._readKicked) {
                        this._readKicked = false;
                     } else {
                        if (System.currentTimeMillis() > timeBefore + this._timeout || position._readClosed) {
                           return -1;
                        }

                        if (this._writeClosed) {
                           return this.fastRead(position);
                        }

                        if (position._currentReadPos < this._window[position._currentPacket].length) {
                           position._numRead++;
                           if (position._availableBytes != Integer.MAX_VALUE) {
                              position._availableBytes--;
                           }

                           return this._window[position._currentPacket][position._currentReadPos++] & 0xFF;
                        }
                     }
                  } catch (InterruptedException var6) {
                  }
               }
            }
         } catch (Exception var7) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public final synchronized int removeSections(int[] offsets, int[] lengths) {
      if (offsets != null && lengths != null && offsets.length != 0 && offsets.length == lengths.length) {
         Arrays.sort(offsets, 0, offsets.length, lengths);
         int numRemoves = lengths.length;
         int removeTotalSize = 0;

         for (int i = 0; i < numRemoves; i++) {
            removeTotalSize += lengths[i];
         }

         if (removeTotalSize < this._totalLength >> 1) {
            int adjustedOffset = 0;
            int numItems = offsets.length;

            for (int i = 0; i < numItems; i++) {
               this.removeSection(offsets[i] - adjustedOffset, lengths[i]);
               adjustedOffset += lengths[i];
            }
         } else {
            byte[] newData = new byte[this._totalLength - removeTotalSize];
            int newDataOffset = 0;
            int removeItemIndex = 0;
            int packetOffset = 0;
            int nextOffset = offsets[removeItemIndex];
            int nextLength = lengths[removeItemIndex];

            for (int i = 0; i < this._window.length; i++) {
               int packetStartOffset = packetOffset;
               int packetEndOffset = packetOffset + this._window[i].length;

               while (packetOffset < packetEndOffset) {
                  if (nextOffset >= packetEndOffset) {
                     int toCopy = packetEndOffset - packetOffset;
                     System.arraycopy(this._window[i], packetOffset - packetStartOffset, newData, newDataOffset, toCopy);
                     packetOffset = packetEndOffset;
                     newDataOffset += toCopy;
                  } else {
                     int toCopy = nextOffset - packetOffset;
                     System.arraycopy(this._window[i], packetOffset - packetStartOffset, newData, newDataOffset, toCopy);
                     newDataOffset += toCopy;
                     if (nextOffset + nextLength >= packetEndOffset) {
                        nextLength -= packetEndOffset - nextOffset;
                        nextOffset = packetEndOffset;
                        packetOffset = packetEndOffset;
                     } else {
                        packetOffset += toCopy + nextLength;
                        if (++removeItemIndex >= offsets.length) {
                           nextOffset = Integer.MAX_VALUE;
                           nextLength = 0;
                        } else {
                           nextOffset = offsets[removeItemIndex];
                           nextLength = lengths[removeItemIndex];
                        }
                     }
                  }
               }
            }

            if (Memory.isObjectInGroup(this._window)) {
               this._window = (byte[][])Memory.expandGroup(this._window);
            }

            this._window = new byte[][]{newData};
         }

         return removeTotalSize;
      } else {
         return 0;
      }
   }

   public final synchronized void removeSection(int offset, int length) {
      if (length > 0) {
         if (Memory.isObjectInGroup(this._window)) {
            this._window = (byte[][])Memory.expandGroup(this._window);

            for (int i = 0; i < this._window.length; i++) {
               if (Memory.isObjectInGroup(this._window[i])) {
                  this._window[i] = (byte[])Memory.expandGroup(this._window[i]);
               }
            }
         }

         int packetsSoFar = 0;

         for (int i = 0; i < this._window.length; i++) {
            int packetLength = this._window[i].length;
            int packetOffset = offset - packetsSoFar;
            if (packetOffset == 0 && length >= packetLength) {
               this._totalLength -= packetLength;
               length -= packetLength;
               offset += packetLength;
               System.arraycopy(this._window, i + 1, this._window, i, this._window.length - i - 1);
               Array.resize(this._window, this._window.length - 1);
               i--;
            } else if (packetOffset < packetLength && packetOffset + length > packetLength) {
               if (i > 0 && packetOffset < 2048 && this._window[i - 1].length < 8192) {
                  int oldWindowSize = this._window[i - 1].length;
                  Array.resize(this._window[i - 1], oldWindowSize + packetOffset);
                  System.arraycopy(this._window[i], 0, this._window[i - 1], oldWindowSize, packetOffset);
                  System.arraycopy(this._window, i + 1, this._window, i, this._window.length - i - 1);
                  Array.resize(this._window, this._window.length - 1);
                  i--;
               } else {
                  Array.resize(this._window[i], packetOffset);
               }

               int truncationSize = packetLength - packetOffset;
               this._totalLength -= truncationSize;
               length -= truncationSize;
               offset += truncationSize;
            } else if (packetOffset < packetLength) {
               if (i > 0 && packetLength - length < 2048 && this._window[i - 1].length < 8192) {
                  int oldWindowSize = this._window[i - 1].length;
                  Array.resize(this._window[i - 1], oldWindowSize + (packetLength - length));
                  if (packetOffset > 0) {
                     System.arraycopy(this._window[i], 0, this._window[i - 1], oldWindowSize, packetOffset);
                     oldWindowSize += packetOffset;
                  }

                  int endOffset = packetOffset + length;
                  System.arraycopy(this._window[i], endOffset, this._window[i - 1], oldWindowSize, packetLength - endOffset);
                  System.arraycopy(this._window, i + 1, this._window, i, this._window.length - i - 1);
                  Array.resize(this._window, this._window.length - 1);
                  i--;
               } else {
                  int endOffset = packetOffset + length;
                  System.arraycopy(this._window[i], endOffset, this._window[i], packetOffset, packetLength - endOffset);
                  Array.resize(this._window[i], packetLength - length);
               }

               this._totalLength -= length;
               length = 0;
            }

            packetsSoFar += packetLength;
            if (length == 0) {
               return;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized int read(byte[] bytes, int offset, int length, PipeContext position) {
      if (position._readClosed || position._availableBytes <= 0) {
         return -1;
      }

      if (length <= 0) {
         return 0;
      }

      if (length > position._availableBytes) {
         length = position._availableBytes;
      }

      int bytesRead = 0;
      boolean var16 = false /* VF: Semaphore variable */;

      int nextPos;
      label258: {
         int var21;
         label259: {
            label246: {
               label245: {
                  label260: {
                     int var11;
                     try {
                        var16 = true;
                        byte[] curPacket = this._window[position._currentPacket];
                        if (position._currentReadPos + length < curPacket.length) {
                           System.arraycopy(curPacket, position._currentReadPos, bytes, offset, length);
                           position._currentReadPos += length;
                           bytesRead = length;
                           nextPos = length;
                           var16 = false;
                           break label258;
                        }

                        nextPos = position._currentPacket + 1;

                        while (true) {
                           if (length <= 0) {
                              var16 = false;
                              break label245;
                           }

                           int bytesRemaining = curPacket.length - position._currentReadPos;
                           if (bytesRemaining >= length) {
                              System.arraycopy(curPacket, position._currentReadPos, bytes, offset, length);
                              position._currentReadPos += length;
                              bytesRead += length;
                              var21 = bytesRead;
                              var16 = false;
                              break label259;
                           }

                           if (bytesRemaining > 0) {
                              System.arraycopy(curPacket, position._currentReadPos, bytes, offset, bytesRemaining);
                              position._currentReadPos += bytesRemaining;
                              offset += bytesRemaining;
                              length -= bytesRemaining;
                              bytesRead += bytesRemaining;
                           } else if (this._window.length > nextPos && this._window[nextPos] != null && this._window[nextPos].length > 0) {
                              position._currentReadPos = 0;
                              position._currentPacket = nextPos;
                              curPacket = this._window[position._currentPacket];
                              nextPos = position._currentPacket + 1;
                           } else {
                              if (this._writeClosed) {
                                 var16 = false;
                                 break label245;
                              }

                              try {
                                 long timeBefore = System.currentTimeMillis();
                                 if (TimeLogger._loggingEnabled) {
                                    TimeLogger.getInstance().startTimer(13, (int)timeBefore);
                                 }

                                 EventLogger.logEvent(1907089860548946979L, 1114666867, 5);
                                 this.wait(this._timeout);
                                 EventLogger.logEvent(1907089860548946979L, 1114666854, 5);
                                 if (TimeLogger._loggingEnabled) {
                                    TimeLogger.getInstance().stopTimer(13, (int)timeBefore);
                                 }

                                 if (this._readKicked) {
                                    this._readKicked = false;
                                 } else {
                                    if (System.currentTimeMillis() > timeBefore + this._timeout || position._readClosed) {
                                       var11 = bytesRead > 0 ? bytesRead : -1;
                                       var16 = false;
                                       break;
                                    }

                                    curPacket = this._window[position._currentPacket];
                                 }
                              } catch (InterruptedException var17) {
                              }
                           }
                        }
                     } catch (Exception var18) {
                        var16 = false;
                        break label260;
                     } finally {
                        if (var16) {
                           if (bytesRead > 0) {
                              if (position._availableBytes != Integer.MAX_VALUE) {
                                 position._availableBytes -= bytesRead;
                              }

                              position._numRead += bytesRead;
                           }
                        }
                     }

                     if (bytesRead > 0) {
                        if (position._availableBytes != Integer.MAX_VALUE) {
                           position._availableBytes -= bytesRead;
                        }

                        position._numRead += bytesRead;
                     }

                     return var11;
                  }

                  if (bytesRead > 0) {
                     if (position._availableBytes != Integer.MAX_VALUE) {
                        position._availableBytes -= bytesRead;
                     }

                     position._numRead += bytesRead;
                  }
                  break label246;
               }

               if (bytesRead > 0) {
                  if (position._availableBytes != Integer.MAX_VALUE) {
                     position._availableBytes -= bytesRead;
                  }

                  position._numRead += bytesRead;
               }
            }

            if (bytesRead > 0) {
               return bytesRead;
            }

            return -1;
         }

         if (bytesRead > 0) {
            if (position._availableBytes != Integer.MAX_VALUE) {
               position._availableBytes -= bytesRead;
            }

            position._numRead += bytesRead;
         }

         return var21;
      }

      if (bytesRead > 0) {
         if (position._availableBytes != Integer.MAX_VALUE) {
            position._availableBytes -= bytesRead;
         }

         position._numRead += bytesRead;
      }

      return nextPos;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized long skip(PipeContext position, long length) {
      if (length > 0 && !position._readClosed && position._availableBytes > 0) {
         if (length > position._availableBytes) {
            length = position._availableBytes;
         }

         long bytesRead = 0;
         boolean var17 = false /* VF: Semaphore variable */;

         long var21;
         label255: {
            long timeBefore;
            label256: {
               label243: {
                  label242: {
                     label257: {
                        long var11;
                        try {
                           var17 = true;
                           byte[] curPacket = this._window[position._currentPacket];
                           if (position._currentReadPos + length < curPacket.length) {
                              position._currentReadPos = (int)(position._currentReadPos + length);
                              bytesRead += length;
                              var21 = length;
                              var17 = false;
                              break label255;
                           }

                           int nextPos = position._currentPacket + 1;

                           while (true) {
                              if (length <= 0) {
                                 var17 = false;
                                 break label242;
                              }

                              int bytesRemaining = curPacket.length - position._currentReadPos;
                              if (bytesRemaining >= length) {
                                 position._currentReadPos = (int)(position._currentReadPos + length);
                                 bytesRead += length;
                                 timeBefore = bytesRead;
                                 var17 = false;
                                 break label256;
                              }

                              if (bytesRemaining > 0) {
                                 position._currentReadPos += bytesRemaining;
                                 length -= bytesRemaining;
                                 bytesRead += bytesRemaining;
                              } else if (this._window.length > nextPos && this._window[nextPos] != null && this._window[nextPos].length > 0) {
                                 position._currentReadPos = 0;
                                 position._currentPacket = nextPos;
                                 curPacket = this._window[position._currentPacket];
                                 nextPos = position._currentPacket + 1;
                              } else {
                                 if (this._writeClosed) {
                                    var17 = false;
                                    break label242;
                                 }

                                 try {
                                    timeBefore = System.currentTimeMillis();
                                    if (TimeLogger._loggingEnabled) {
                                       TimeLogger.getInstance().startTimer(13, (int)timeBefore);
                                    }

                                    EventLogger.logEvent(1907089860548946979L, 1114666867, 5);
                                    this.wait(this._timeout);
                                    EventLogger.logEvent(1907089860548946979L, 1114666854, 5);
                                    if (TimeLogger._loggingEnabled) {
                                       TimeLogger.getInstance().stopTimer(13, (int)timeBefore);
                                    }

                                    if (this._readKicked) {
                                       this._readKicked = false;
                                    } else {
                                       if (System.currentTimeMillis() > timeBefore + this._timeout || position._readClosed) {
                                          var11 = bytesRead > 0 ? bytesRead : -1;
                                          var17 = false;
                                          break;
                                       }

                                       curPacket = this._window[position._currentPacket];
                                    }
                                 } catch (InterruptedException var18) {
                                 }
                              }
                           }
                        } catch (Exception var19) {
                           var17 = false;
                           break label257;
                        } finally {
                           if (var17) {
                              if (bytesRead > 0) {
                                 if (position._availableBytes != Integer.MAX_VALUE) {
                                    position._availableBytes = (int)(position._availableBytes - bytesRead);
                                 }

                                 position._numRead = (int)(position._numRead + bytesRead);
                              }
                           }
                        }

                        if (bytesRead > 0) {
                           if (position._availableBytes != Integer.MAX_VALUE) {
                              position._availableBytes = (int)(position._availableBytes - bytesRead);
                           }

                           position._numRead = (int)(position._numRead + bytesRead);
                        }

                        return var11;
                     }

                     if (bytesRead > 0) {
                        if (position._availableBytes != Integer.MAX_VALUE) {
                           position._availableBytes = (int)(position._availableBytes - bytesRead);
                        }

                        position._numRead = (int)(position._numRead + bytesRead);
                     }
                     break label243;
                  }

                  if (bytesRead > 0) {
                     if (position._availableBytes != Integer.MAX_VALUE) {
                        position._availableBytes = (int)(position._availableBytes - bytesRead);
                     }

                     position._numRead = (int)(position._numRead + bytesRead);
                  }
               }

               if (bytesRead > 0) {
                  return bytesRead;
               }

               return -1;
            }

            if (bytesRead > 0) {
               if (position._availableBytes != Integer.MAX_VALUE) {
                  position._availableBytes = (int)(position._availableBytes - bytesRead);
               }

               position._numRead = (int)(position._numRead + bytesRead);
            }

            return timeBefore;
         }

         if (bytesRead > 0) {
            if (position._availableBytes != Integer.MAX_VALUE) {
               position._availableBytes = (int)(position._availableBytes - bytesRead);
            }

            position._numRead = (int)(position._numRead + bytesRead);
         }

         return var21;
      } else {
         return 0;
      }
   }

   public final int fastRead(PipeContext position) {
      if (!position._readClosed && position._availableBytes > 0) {
         try {
            int result;
            try {
               result = this._window[position._currentPacket][position._currentReadPos++];
            } catch (ArrayIndexOutOfBoundsException aiob) {
               if (position._currentPacket >= this._window.length) {
                  return -1;
               }

               position._currentReadPos = 1;
               position._currentPacket++;
               result = this._window[position._currentPacket][0];
            }

            position._numRead++;
            if (position._availableBytes != Integer.MAX_VALUE) {
               position._availableBytes--;
            }

            return result & 0xFF;
         } catch (Exception var5) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public final synchronized int readCompressedInt(PipeContext position) {
      if (position._readClosed) {
         throw new IOCancelledException();
      }

      int i = 0;
      int result = 0;

      do {
         i = this.read(position);
         if (i == -1) {
            throw new EOFException();
         }

         result = result << 7 | i & 127;
      } while ((i & 128) != 0);

      return result;
   }

   public final synchronized boolean skipInlineString(PipeContext position) {
      boolean result = false;
      int i = 0;

      while (true) {
         i = this.read(position);
         switch (i) {
            case -1:
               throw new EOFException();
            case 13:
               result = true;
            default:
               if (i == 0) {
                  return result;
               }
         }
      }
   }

   public final synchronized String readInlineString(PipeContext position, String encoding) {
      if (position._readClosed) {
         throw new IOCancelledException();
      }

      int packet = position._currentPacket;
      int pos = position._currentReadPos;
      boolean crlfEncountered = this.skipInlineString(position);
      if (!crlfEncountered && position._currentPacket == packet) {
         return new String(this._window[packet], pos, position._currentReadPos - pos - 1, encoding);
      }

      int size = 0;

      for (int i = packet; i <= position._currentPacket; i++) {
         size += this._window[i].length;
      }

      size -= pos;
      size -= this._window[position._currentPacket].length - position._currentReadPos;
      byte[] tmpArray = new byte[size];
      position._currentPacket = packet;
      position._currentReadPos = pos;
      int readSize = this.read(tmpArray, 0, size, position);
      if (readSize != size) {
         throw new EOFException();
      }

      size--;
      boolean carriageReturn = false;
      int j = 0;

      for (int i = 0; i < size; i++) {
         if (tmpArray[i] == 10 && carriageReturn) {
            carriageReturn = false;
         } else {
            if (tmpArray[i] == 13) {
               carriageReturn = true;
               tmpArray[i] = 10;
            } else {
               carriageReturn = false;
            }

            tmpArray[j++] = tmpArray[i];
         }
      }

      return new String(tmpArray, 0, j, encoding);
   }

   public final synchronized byte[] readPacket(int packetNo) {
      if (packetNo < 0) {
         return null;
      } else {
         return packetNo >= this._window.length ? null : this._window[packetNo];
      }
   }

   public final synchronized void closeWrite() {
      this._writeClosed = true;
      this.notify();
   }

   public final synchronized void closeRead(PipeContext position) {
      position._readClosed = true;
      this.notify();
   }

   final synchronized boolean isPacketIncluded(int packetNo) {
      if (packetNo < 0) {
         return true;
      } else if (packetNo >= this._window.length) {
         return false;
      } else {
         return this._window[packetNo] == null ? false : packetNo != 0 || this._window[0].length != 0;
      }
   }

   public final synchronized int available(PipeContext context) {
      if (context._availableBytes != Integer.MAX_VALUE) {
         return context._availableBytes;
      }

      int count = 0;

      for (int i = context._currentPacket; i < this._window.length; i++) {
         if (this._window[i] != null) {
            count += this._window[i].length;
         }
      }

      count -= context._currentReadPos;
      if (count > 0) {
         return count;
      } else {
         return this._writeClosed ? -1 : 0;
      }
   }

   public final synchronized void waitUntilClosed() {
      while (!this._writeClosed) {
         try {
            long timeBefore = System.currentTimeMillis();
            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().startTimer(13, (int)timeBefore);
            }

            EventLogger.logEvent(1907089860548946979L, 1114666867, 5);
            this.wait(this._timeout);
            EventLogger.logEvent(1907089860548946979L, 1114666854, 5);
            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().stopTimer(13, (int)timeBefore);
            }

            if (this._readKicked) {
               this._readKicked = false;
            } else if (System.currentTimeMillis() > timeBefore + this._timeout) {
               return;
            }
         } catch (InterruptedException var3) {
         }
      }
   }

   public final boolean isAborted() {
      return false;
   }

   public final int getLength() {
      return this._totalLength;
   }

   public final boolean isClosed() {
      return this._writeClosed;
   }

   public final synchronized byte[] toArray() {
      if (this._window.length > 1) {
         byte[] data = new byte[this._totalLength];
         int offset = 0;

         for (int i = 0; i < this._window.length; i++) {
            if (this._window[i] != null) {
               System.arraycopy(this._window[i], 0, data, offset, this._window[i].length);
               offset += this._window[i].length;
            }
         }

         return data;
      } else {
         return this._window[0];
      }
   }

   public final synchronized byte[][] toSegmentedArray() {
      return this._window;
   }

   public final synchronized void setCacheStart(int packet, int start) {
      if (this._window[packet] != null) {
         this._totalLength -= start;
         int length = this._window[packet].length - start;
         if (length >= 0) {
            System.arraycopy(this._window[packet], start, this._window[packet], 0, length);
            Array.resize(this._window[packet], length);
         }

         int i = 0;

         for (int j = packet; j < this._window.length; j++) {
            this._window[i] = this._window[j];
            i++;
         }

         while (i < this._window.length) {
            this._window[i] = null;
            i++;
         }
      }
   }

   public final int getEstimatedSize() {
      return Math.max(this._estimatedSize, this._totalLength);
   }

   public final void setEstimatedSize(int size) {
      this._estimatedSize = size;
   }

   public final synchronized int readByteArray(PipePtr ptr, int length, PipeContext position) {
      if (position._readClosed || length < 0 || position._availableBytes <= 0) {
         ptr.setLength(-1);
         return -1;
      }

      if (length <= 0) {
         ptr.setLength(0);
         return 0;
      }

      if (length > position._availableBytes) {
         length = position._availableBytes;
      }

      byte[] curPacket = this._window[position._currentPacket];
      if (position._currentReadPos + length < curPacket.length) {
         ptr.setData(curPacket, position._currentReadPos, length, true);
         position._currentReadPos += length;
         position._numRead += length;
         if (position._availableBytes != Integer.MAX_VALUE) {
            position._availableBytes -= length;
         }

         return length;
      } else {
         byte[] data = new byte[length];
         length = this.read(data, 0, length, position);
         if (length > 0) {
            ptr.setData(data, 0, length, false);
            return length;
         } else {
            ptr.setLength(-1);
            return -1;
         }
      }
   }

   public final synchronized PipeInputStream getInputStream() {
      return this._writeClosed ? new FastPipeInputStream(this) : new PipeInputStream(this);
   }

   public final synchronized PipeInputStream getInputStream(int packet, int offset, int length) {
      return this._writeClosed ? new FastPipeInputStream(this, packet, offset, length) : new PipeInputStream(this, packet, offset, length);
   }

   public final PipeOutputStream getOutputStream() {
      return new PipeOutputStream(this);
   }

   public final void setTimeout(int timeout) {
      this._timeout = timeout;
   }

   public final synchronized void kickReadTimer() {
      this._readKicked = true;
      this.notify();
   }
}
