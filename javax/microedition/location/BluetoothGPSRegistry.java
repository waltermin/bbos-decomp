package javax.microedition.location;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Process;

class BluetoothGPSRegistry implements BluetoothSerialPortListener {
   private IntVector _processes;
   private BluetoothSerialPort _port;
   private Location _lastLocation;
   private QualifiedCoordinates _lastCoord;
   Object _BTLocationLock = new Object();
   int _state;
   int _reconnectCount;
   private byte[] _receiveBuffer = new byte[2048];
   private int _posEndOfData = 0;
   private int _posStartOfData = -1;
   private int _posScanned = 0;
   private int[] _field = new int[20];
   private int _status = 0;
   private static final long REGISTRY_NAME = 6865174307939138986L;
   public static final long BT_DISCONNECT_EVENT = 3338410696610913563L;
   private static BluetoothGPSRegistry _registry;
   private static final long GPGGA = 39888708126529L;
   private static final long GPGLL = 39888708127820L;
   private static final long GPRMC = 39888708848963L;
   private static final long GPVTG = 39888709112903L;
   private static final long GPRMA = 39888708848961L;
   private static final long GPGSA = 39888708129601L;
   private static final int RX_BUFFER_SIZE = 2048;
   private static final int TX_BUFFER_SIZE = 256;
   private static final int SENTENCE_ID = 0;
   private static final int GPGGA_TIME = 1;
   private static final int GPGGA_LATITUDE = 2;
   private static final int GPGGA_NORTH_SOUTH = 3;
   private static final int GPGGA_LONGITUDE = 4;
   private static final int GPGGA_EAST_WEST = 5;
   private static final int GPGGA_POSITION_FIX = 6;
   private static final int GPGGA_SATALITES_USED = 7;
   private static final int GPGGA_HDOP = 8;
   private static final int GPGGA_ALTITUDE = 9;
   private static final int GPGGA_ALTITUDE_UNITS = 10;
   private static final int GPGGA_GEOID_SEPERATION = 11;
   private static final int GPGGA_SEPERATION_UNITS = 12;
   private static final int GPGGA_DGPS_AGE = 13;
   private static final int GPGGA_DGPS_STATION = 14;
   private static final int GPGGA_FIELD_COUNT = 15;
   private static final int GPGLL_LATITUDE = 1;
   private static final int GPGLL_NORTH_SOUTH = 2;
   private static final int GPGLL_LONGITUDE = 3;
   private static final int GPGLL_EAST_WEST = 4;
   private static final int GPGLL_STATUS = 6;
   private static final int GPGLL_FIELD_COUNT = 7;
   private static final int GPGSA_STATUS = 2;
   private static final int GPGSA_HDOP = 16;
   private static final int GPGSA_VDOP = 17;
   private static final int GPGSA_FIELD_COUNT = 18;
   private static final int GPRMC_STATUS = 2;
   private static final int GPRMC_LATITUDE = 3;
   private static final int GPRMC_NORTH_SOUTH = 4;
   private static final int GPRMC_LONGITUDE = 5;
   private static final int GPRMC_EAST_WEST = 6;
   private static final int GPRMC_SPEED = 7;
   private static final int GPRMC_COURSE = 8;
   private static final int GPRMC_FIELD_COUNT = 13;
   private static final int GPRMC_FIELD_COUNT_OLD = 12;
   private static final int GPVTG_COURSE = 1;
   private static final int GPVTG_SPEED = 7;
   private static final int GPVTG_SPEED_UNIT = 8;
   private static final int GPVTG_FIELD_COUNT = 9;
   private static final int GPRMA_STATUS = 1;
   private static final int GPRMA_LATITUDE = 2;
   private static final int GPRMA_NORTH_SOUTH = 3;
   private static final int GPRMA_LONGITUDE = 4;
   private static final int GPRMA_EAST_WEST = 5;
   private static final int GPRMA_SPEED = 8;
   private static final int GPRMA_COURSE = 9;
   private static final int GPRMA_FIELD_COUNT = 12;
   private static int LONGITUDE_FLAG;
   private static int LATITUDE_FLAG;
   private static int ALTITUDE_FLAG;
   private static int SPEED_FLAG;
   private static int COURSE_FLAG;
   private static int HACC_FLAG;
   private static int VACC_FLAG;
   private static int SATT_FLAG;
   private static int ALL_FOUND;

   public Location getLocation() {
      return this._lastLocation;
   }

   public Object getBTLocationLock() {
      return this._BTLocationLock;
   }

   public int getState() {
      return this._state;
   }

   public synchronized void registerGPSConsumer() {
      int processId = Process.currentProcess().getProcessId();
      if (this._processes.size() == 0 || this._state != 1) {
         Proxy proxy = Proxy.getInstance();
         proxy.startThread(new BluetoothGPSRegistry$BluetoothConnectThread(this));
      }

      if (!this._processes.contains(processId)) {
         this._processes.addElement(processId);
         ApplicationProcess process = (ApplicationProcess)Process.currentProcess();
         process.addCleanupRunnable(new BluetoothGPSRegistry$GPSCleanerRunnable(this, processId));
      }
   }

   public synchronized void removeGPSConsumer(int processId) {
      this._processes.removeElement(processId);
      if (this._processes.size() == 0) {
         Proxy proxy = Proxy.getInstance();
         proxy.startThread(new BluetoothGPSRegistry$BluetoothDisconnectThread(this));
      }
   }

   void parseSentence(long sentenceId, int markerEnd) {
      try {
         int fieldCount = 0;
         this._field[fieldCount++] = this._posStartOfData;

         for (int index = this._posStartOfData; index < markerEnd; index++) {
            if (this._receiveBuffer[index] == 44) {
               if (fieldCount >= this._field.length - 1) {
                  return;
               }

               this._field[fieldCount++] = index + 1;
            }
         }

         this._field[fieldCount] = markerEnd;
         if (sentenceId != 39888708126529L) {
            if (sentenceId != 39888708127820L) {
               if (sentenceId != 39888708848963L) {
                  if (sentenceId != 39888709112903L) {
                     if (sentenceId != 39888708848961L) {
                        if (sentenceId == 39888708129601L) {
                           if (fieldCount != 18) {
                              return;
                           }

                           if (this._receiveBuffer[this._field[2]] != 49) {
                              if ((this._status & HACC_FLAG) == 0) {
                                 this._lastCoord
                                    .setHorizontalAccuracy((float)parseDouble(this._receiveBuffer, this._field[16], this._field[17] - this._field[16] - 1));
                                 this._status = this._status | HACC_FLAG;
                              }

                              if ((this._status & VACC_FLAG) == 0) {
                                 int endIndex = 0;
                                 endIndex = this._field[17];

                                 while (endIndex < this._field[18] && this._receiveBuffer[endIndex] != 42) {
                                    endIndex++;
                                 }

                                 this._lastCoord.setVerticalAccuracy((float)parseDouble(this._receiveBuffer, this._field[17], endIndex - this._field[17] - 1));
                                 this._status = this._status | VACC_FLAG;
                                 return;
                              }
                           }
                        }
                     } else {
                        if (fieldCount != 12) {
                           return;
                        }

                        if (this._receiveBuffer[this._field[1]] == 65) {
                           if ((this._status & LATITUDE_FLAG) == 0) {
                              double latitude = this.parseLL(this._receiveBuffer, this._field[2], this._field[3] - this._field[2] - 1);
                              if (this._receiveBuffer[this._field[3]] == 83) {
                                 latitude = -latitude;
                              }

                              this._lastCoord.setLatitude(latitude);
                              this._status = this._status | LATITUDE_FLAG;
                           }

                           if ((this._status & LONGITUDE_FLAG) == 0) {
                              double longitude = this.parseLL(this._receiveBuffer, this._field[4], this._field[5] - this._field[4] - 1);
                              if (this._receiveBuffer[this._field[5]] == 87) {
                                 longitude = -longitude;
                              }

                              this._lastCoord.setLongitude(longitude);
                              this._status = this._status | LONGITUDE_FLAG;
                           }

                           if ((this._status & COURSE_FLAG) == 0) {
                              this._lastLocation.setCourse((float)parseDouble(this._receiveBuffer, this._field[9], this._field[10] - this._field[9] - 1));
                              this._status = this._status | COURSE_FLAG;
                           }

                           if ((this._status & SPEED_FLAG) == 0) {
                              this._lastLocation
                                 .setSpeed((float)parseDouble(this._receiveBuffer, this._field[8], this._field[9] - this._field[8] - 1) * 1057206946);
                              this._status = this._status | SPEED_FLAG;
                           }
                        }
                     }
                  } else {
                     if (fieldCount != 9) {
                        return;
                     }

                     if ((this._status & COURSE_FLAG) == 0) {
                        this._lastLocation.setCourse((float)parseDouble(this._receiveBuffer, this._field[1], this._field[2] - this._field[1] - 1));
                        this._status = this._status | COURSE_FLAG;
                     }

                     if ((this._status & SPEED_FLAG) == 0 && this._receiveBuffer[this._field[8]] == 75) {
                        this._lastLocation.setSpeed((float)parseDouble(this._receiveBuffer, this._field[7], this._field[8] - this._field[7] - 1) * 1049508068);
                        this._status = this._status | SPEED_FLAG;
                     }
                  }
               } else {
                  if (fieldCount != 13 && fieldCount != 12) {
                     return;
                  }

                  if (this._receiveBuffer[this._field[2]] == 65) {
                     if ((this._status & LATITUDE_FLAG) == 0) {
                        double latitude = this.parseLL(this._receiveBuffer, this._field[3], this._field[4] - this._field[3] - 1);
                        if (this._receiveBuffer[this._field[4]] == 83) {
                           latitude = -latitude;
                        }

                        this._lastCoord.setLatitude(latitude);
                        this._status = this._status | LATITUDE_FLAG;
                     }

                     if ((this._status & LONGITUDE_FLAG) == 0) {
                        double longitude = this.parseLL(this._receiveBuffer, this._field[5], this._field[6] - this._field[5] - 1);
                        if (this._receiveBuffer[this._field[6]] == 87) {
                           longitude = -longitude;
                        }

                        this._lastCoord.setLongitude(longitude);
                        this._status = this._status | LONGITUDE_FLAG;
                     }

                     if ((this._status & SPEED_FLAG) == 0) {
                        this._lastLocation.setSpeed((float)parseDouble(this._receiveBuffer, this._field[7], this._field[8] - this._field[7] - 1) * 1057206946);
                        this._status = this._status | SPEED_FLAG;
                     }

                     if ((this._status & COURSE_FLAG) == 0) {
                        this._lastLocation.setCourse((float)parseDouble(this._receiveBuffer, this._field[8], this._field[9] - this._field[8] - 1));
                        this._status = this._status | COURSE_FLAG;
                     }
                  }
               }
            } else {
               if (fieldCount != 7) {
                  return;
               }

               if ((this._status & LATITUDE_FLAG) == 0) {
                  double latitude = this.parseLL(this._receiveBuffer, this._field[1], this._field[2] - this._field[1] - 1);
                  if (this._receiveBuffer[this._field[2]] == 83) {
                     latitude = -latitude;
                  }

                  this._lastCoord.setLatitude(latitude);
                  this._status = this._status | LATITUDE_FLAG;
               }

               if ((this._status & LONGITUDE_FLAG) == 0) {
                  double longitude = this.parseLL(this._receiveBuffer, this._field[3], this._field[4] - this._field[3] - 1);
                  if (this._receiveBuffer[this._field[4]] == 87) {
                     longitude = -longitude;
                  }

                  this._lastCoord.setLongitude(longitude);
                  this._status = this._status | LONGITUDE_FLAG;
               }
            }
         } else {
            if (fieldCount != 15) {
               return;
            }

            if (this._receiveBuffer[this._field[6]] != 48) {
               if ((this._status & LATITUDE_FLAG) == 0) {
                  double latitude = this.parseLL(this._receiveBuffer, this._field[2], this._field[3] - this._field[2] - 1);
                  if (this._receiveBuffer[this._field[3]] == 83) {
                     latitude = -latitude;
                  }

                  this._lastCoord.setLatitude(latitude);
                  this._status = this._status | LATITUDE_FLAG;
               }

               if ((this._status & LONGITUDE_FLAG) == 0) {
                  double longitude = this.parseLL(this._receiveBuffer, this._field[4], this._field[5] - this._field[4] - 1);
                  if (this._receiveBuffer[this._field[5]] == 87) {
                     longitude = -longitude;
                  }

                  this._lastCoord.setLongitude(longitude);
                  this._status = this._status | LONGITUDE_FLAG;
               }

               if ((this._status & SATT_FLAG) == 0) {
                  this._lastLocation.setSatellites(parseInt(this._receiveBuffer, this._field[7], this._field[8] - this._field[7] - 1));
                  this._status = this._status | SATT_FLAG;
               }

               if ((this._status & HACC_FLAG) == 0) {
                  this._lastCoord.setHorizontalAccuracy((float)parseDouble(this._receiveBuffer, this._field[8], this._field[9] - this._field[8] - 1));
                  this._status = this._status | HACC_FLAG;
               }

               if ((this._status & ALTITUDE_FLAG) == 0 && this._receiveBuffer[this._field[10]] == 77) {
                  this._lastCoord.setAltitude((float)parseDouble(this._receiveBuffer, this._field[9], this._field[10] - this._field[9] - 1));
                  this._status = this._status | ALTITUDE_FLAG;
               }
            }
         }
      } finally {
         return;
      }
   }

   double parseLL(byte[] buffer, int offset, int length) {
      int decimal = offset;

      while (decimal < offset + length && buffer[decimal] != 46) {
         decimal++;
      }

      int degrees = parseInt(buffer, offset, decimal - offset - 2);
      double minutes = parseDouble(buffer, decimal - 2, offset + length - decimal + 2);
      return degrees + minutes / 4633641066610819072L;
   }

   @Override
   public void dataSent() {
   }

   @Override
   public void deviceConnected(boolean success) {
      if (success) {
         this.notifyLocationListeners(1);
         this._state = 1;
         this._reconnectCount = 0;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void dataReceived(int length) {
      try {
         this._posEndOfData = this._posEndOfData
            + this._port.read(this._receiveBuffer, this._posEndOfData, length == -1 ? this._receiveBuffer.length - this._posEndOfData : length);
         if (this._posEndOfData - this._posStartOfData > 200) {
            for (int i = this._posScanned; i < this._posEndOfData && this._status != ALL_FOUND; i++) {
               if (this._receiveBuffer[i] == 36) {
                  if (this._posStartOfData == -1) {
                     this._posStartOfData = i;
                  } else {
                     long sentenceId = 0;

                     for (int index = this._posStartOfData; index < this._posStartOfData + 6; index++) {
                        sentenceId = sentenceId << 8 | this._receiveBuffer[index];
                     }

                     if (sentenceId == 39888708126529L
                        || sentenceId == 39888708127820L
                        || sentenceId == 39888708848963L
                        || sentenceId == 39888709112903L
                        || sentenceId == 39888708848961L
                        || sentenceId == 39888708129601L) {
                        this.parseSentence(sentenceId, i);
                     }

                     this._posStartOfData = i;
                  }
               }
            }

            this._posEndOfData = this._posEndOfData - this._posStartOfData;
            System.arraycopy(this._receiveBuffer, this._posStartOfData, this._receiveBuffer, 0, this._posEndOfData);
            this._posStartOfData = 0;
            this._posScanned = this._posEndOfData;
            if ((this._status & (LONGITUDE_FLAG | LATITUDE_FLAG)) != 0) {
               this._lastLocation.setTimestamp(System.currentTimeMillis());
               synchronized (this._BTLocationLock) {
                  this._BTLocationLock.notifyAll();
               }
            }

            this._status = 0;
         }
      } catch (Throwable var10) {
         throw new Object(ex.getMessage());
      }
   }

   @Override
   public void dtrStateChange(boolean high) {
   }

   @Override
   public void deviceDisconnected() {
      this.notifyLocationListeners(2);
      this._state = 2;
      if (this._reconnectCount > 3) {
         this._reconnectCount = 0;
      } else {
         Proxy proxy = Proxy.getInstance();
         proxy.startThread(new BluetoothGPSRegistry$BluetoothConnectThread(this));
         this._reconnectCount++;
      }
   }

   private BluetoothGPSRegistry() {
      this._processes = (IntVector)(new Object());
      this._lastLocation = new Location();
      this._lastCoord = new QualifiedCoordinates((double)0L, (double)0L, (float)2143289344, (float)2143289344, (float)2143289344);
      this._lastLocation.setCoordinates(this._lastCoord);
      this._state = 2;
   }

   private static double parseDouble(byte[] buffer, int offset, int length) {
      int decimal = offset;

      while (decimal < offset + length && buffer[decimal] != 46) {
         decimal++;
      }

      double integer = parseInt(buffer, offset, decimal - offset);
      double fraction = parseInt(buffer, decimal + 1, offset + length - decimal - 1);
      int divisor = 1;
      decimal++;

      while (decimal < offset + length) {
         divisor *= 10;
         decimal++;
      }

      return integer + fraction / divisor;
   }

   private static int parseInt(byte[] buffer, int offset, int length) {
      int end = offset + length;
      int sign = 1;
      if (buffer[offset] == 45) {
         sign = -1;
         offset++;
      } else if (buffer[offset] == 43) {
         offset++;
      }

      int value = 0;

      for (int index = offset; index < end; index++) {
         int digit = buffer[index] - 48;
         if (digit < 0 || 9 < digit) {
            throw new Object();
         }

         value = 10 * value + digit;
      }

      return sign * value;
   }

   public static BluetoothGPSRegistry getInstance() {
      return _registry;
   }

   private void notifyLocationListeners(int state) {
      for (int i = 0; i < this._processes.size(); i++) {
         RIMGlobalMessagePoster.postGlobalEvent(this._processes.elementAt(i), 3338410696610913563L, state, 0, null, null);
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _registry = (BluetoothGPSRegistry)ar.getOrWaitFor(6865174307939138986L);
      if (_registry == null) {
         _registry = new BluetoothGPSRegistry();
         ar.put(6865174307939138986L, _registry);
      }

      LONGITUDE_FLAG = 1;
      LATITUDE_FLAG = 2;
      ALTITUDE_FLAG = 4;
      SPEED_FLAG = 8;
      COURSE_FLAG = 16;
      HACC_FLAG = 32;
      VACC_FLAG = 64;
      SATT_FLAG = 128;
      ALL_FOUND = 255;
   }
}
