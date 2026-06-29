package net.rim.device.apps.internal.iota;

import java.io.ByteArrayInputStream;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.browser.stack.StackManager;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.device.cldc.io.tunnel.TunnelListener;
import net.rim.device.internal.system.EngineeringDataListener;
import net.rim.device.internal.system.RadioInternal;

public final class MMCProcessor extends Thread implements EngineeringDataListener, TunnelListener, RadioStatusListener {
   private MMCDocHandler _parserHandler;
   private HttpUserAgent _httpUserAgent;
   private String _connURL;
   private int _currentMode;
   private ProvisioningServiceAgent _psa;
   private boolean _cancelRequested;
   private boolean _errorOccurred;
   private boolean _sendRegistrationRequest;
   private IOTARequestBuffer _buffer;
   private IOTARequest _theRequest;
   private Tunnel _tunnel;
   private Object _tunnelSync = new Object();
   private Object _phoneSync = new Object();
   private IOTATimerThread _timer;
   public static final String CONTENT_TYPE_MMC_XML = "application/vnd.phonecom.mmc-xml";
   public static final String CONTENT_TYPE_MULTIPART = "multipart/related";
   private static byte[] _is683Result;
   private static Object _is683Sync = new Object();
   private static ResourceBundle _resources = ResourceBundle.getBundle(1200788073156220994L, "net.rim.device.apps.internal.resource.IOTA");

   public MMCProcessor(ProvisioningServiceAgent provisioningServiceAgent, IOTARequestBuffer buffer) {
      this._psa = provisioningServiceAgent;
      this._connURL = null;
      this._errorOccurred = false;
      this._buffer = buffer;
      Application.getApplication().addRadioListener(this);
      VoiceServices.addPhoneEventListener(new MMCProcessor$IOTAPhoneListener(this));
   }

   public final synchronized void openTunnel() {
      EventLogger.logEvent(4411276428801970910L, 1414426480);
      if (this._tunnel == null) {
         TunnelConfig config = (TunnelConfig)(new Object("iota", "IOTA - TUNNEL", this));
         config.setPriority(0);
         config.setLingerTimeout(0);
         this._tunnel = TunnelFactory.openTunnel(config);
      } else {
         this._tunnel.reset();
         this._tunnel.kick();
      }
   }

   public final synchronized void closeTunnel() {
      EventLogger.logEvent(4411276428801970910L, 1414423404);
      if (this._tunnel != null) {
         this._tunnel.close();
         this._tunnel = null;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void waitForTunnel() {
      EventLogger.logEvent(4411276428801970910L, 1414428513, 5);
      if (this._tunnel != null && this._tunnel.getStatus() != 3) {
         synchronized (this._tunnelSync) {
            label40:
            try {
               this._tunnelSync.wait(45000);
            } catch (Throwable var6) {
               ProvisioningServiceAgent.logEvents(e.toString());
               break label40;
            }

            this._tunnelSync.notify();
         }

         if (this._tunnel != null && this._tunnel.getStatus() != 3) {
            throw new Object("TNac - timeout");
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void waitOnPhone() {
      EventLogger.logEvent(4411276428801970910L, 1346926433);
      synchronized (this._phoneSync) {
         if (StackManager.isVoiceActive()) {
            boolean var6 = false /* VF: Semaphore variable */;

            try {
               var6 = true;
               this._phoneSync.wait();
               var6 = false;
            } finally {
               if (var6) {
                  EventLogger.logEvent(4411276428801970910L, 1346926438, 5);
                  return;
               }
            }
         }
      }
   }

   private final void notifyOnPhone() {
      EventLogger.logEvent(4411276428801970910L, 1346922098, 5);
      synchronized (this._phoneSync) {
         this._phoneSync.notify();
      }
   }

   private final void parseMMCDocument(byte[] data, String contentType) {
      ByteArrayInputStream mmcByteArrayInputStream = (ByteArrayInputStream)(new Object(data));
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.setAllowUndefinedNamespaces(true);
      String contentTypeWithoutParameters = contentType;
      if (contentTypeWithoutParameters != null) {
         int semicolon = contentTypeWithoutParameters.indexOf(59);
         if (semicolon >= 0) {
            contentTypeWithoutParameters = contentTypeWithoutParameters.substring(0, semicolon);
         }

         contentTypeWithoutParameters = contentTypeWithoutParameters.trim();
         if (StringUtilities.strEqualIgnoreCase(contentTypeWithoutParameters, "multipart/related", 1701707776)) {
            EventLogger.logEvent(4411276428801970910L, 1299017842, 5);
            MimeMultipart multipart = new MimeMultipart(mmcByteArrayInputStream, contentType);
            BodyPart mmcBodyPart = multipart.getRootBodyPart();
            if (mmcBodyPart == null) {
               throw new Object("Multipart Body missing");
            }

            byte[] mmcBytes = mmcBodyPart.getBytes();
            mmcByteArrayInputStream = (ByteArrayInputStream)(new Object(mmcBytes));
            this._parserHandler = new MMCDocHandler(this, this._psa, this._httpUserAgent, multipart);
         } else {
            if (!StringUtilities.strEqualIgnoreCase(contentTypeWithoutParameters, "application/vnd.phonecom.mmc-xml", 1701707776)) {
               throw new Object(((StringBuffer)(new Object("Unexpected Content-Type: "))).append(contentTypeWithoutParameters).toString());
            }

            EventLogger.logEvent(4411276428801970910L, 1299737964, 5);
            this._parserHandler = new MMCDocHandler(this, this._psa, this._httpUserAgent, null);
         }
      }

      if (this._parserHandler != null) {
         EventLogger.logEvent(4411276428801970910L, 1299215220, 5);
         saxParser.parse(mmcByteArrayInputStream, this._parserHandler);
         EventLogger.logEvent(4411276428801970910L, 1299213924, 5);
      }
   }

   public final IOTARequest getCurrentRequest() {
      return this._theRequest;
   }

   public final synchronized boolean cancelProcessing() {
      this._cancelRequested = true;
      if (this._parserHandler != null) {
         label28:
         try {
            this._parserHandler.cancelParsing();
         } finally {
            break label28;
         }

         this._parserHandler = null;
      }

      if (this._httpUserAgent != null) {
         this._httpUserAgent.cleanup();
      }

      this.closeTunnel();
      this._psa.setBusy(false);
      return true;
   }

   public final void setOTAResult(byte[] result) {
      synchronized (_is683Sync) {
         _is683Result = result;
         _is683Sync.notify();
      }
   }

   public final byte[] getOTAResult() {
      byte[] result = null;
      synchronized (_is683Sync) {
         result = _is683Result;
         _is683Sync.notify();
         return result;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      String msg = null;

      while (true) {
         this._theRequest = this._buffer.removeRequest();
         synchronized (this._theRequest) {
            this._connURL = this._theRequest.getUrl();
            this._currentMode = this._theRequest.getMode();
         }

         if (this._currentMode == 2 && !FastDormancyManager.getInstance().getFastDormancy()) {
            EventLogger.logEvent(4411276428801970910L, 1296986224);
            Application.getApplication().invokeLater(new MMCProcessor$BufferRequestRunnable(this, this._theRequest), 60000, false);
         } else {
            EventLogger.logEvent(4411276428801970910L, 1297109808 | this._currentMode);
            if (StackManager.isVoiceActive()) {
               if (this._currentMode != 2) {
                  this._psa.showMessage(_resources.getString(6), this._theRequest.getMode(), 1);
                  continue;
               }

               this.waitOnPhone();
            }

            if (RadioInfo.getState() != 1) {
               this._psa.showMessage(_resources.getString(3), this._theRequest.getMode(), 1);
            } else {
               this._psa.setBusy(true);
               this._psa.showMessage(_resources.getString(4), this._currentMode, 0);
               this.openTunnel();
               boolean var11 = false /* VF: Semaphore variable */;
               boolean var16 = false /* VF: Semaphore variable */;

               label560: {
                  label561: {
                     label562: {
                        try {
                           try {
                              var16 = true;
                              var11 = true;
                              if (this._cancelRequested) {
                                 var11 = false;
                                 var16 = false;
                                 break label561;
                              }

                              this.waitForTunnel();
                              if (this._cancelRequested) {
                                 var11 = false;
                                 var16 = false;
                                 break label562;
                              }

                              this._psa.createTempStorage();
                              this._timer = new IOTATimerThread(this, 900000);
                              this._timer.start();
                              String prxy = decodeProxy((String)this._psa.readFromStorage("phone:proxy..0.address..0.wdp"));
                              EventLogger.logEvent(4411276428801970910L, 1299079529, 5);
                              byte[] nai = RadioInternal.getNAI(0);
                              if (nai == null && DeviceInfo.isSimulator()) {
                                 nai = new byte[]{117, 115, 101, 114};
                              }

                              if (nai == null) {
                                 this._errorOccurred = true;
                                 msg = "Invalid username, Provisioning is cancelled..";
                                 var11 = false;
                                 var16 = false;
                              } else {
                                 this._httpUserAgent = new HttpUserAgent((String)(new Object(nai)), "pcs", prxy);
                                 byte[] data = this._httpUserAgent.get(this._connURL);
                                 this._connURL = null;

                                 while (true) {
                                    if (data == null) {
                                       var11 = false;
                                       var16 = false;
                                       break;
                                    }

                                    if (this._cancelRequested) {
                                       var11 = false;
                                       var16 = false;
                                       break;
                                    }

                                    if (!this._psa.trustURL(this._httpUserAgent.getURL())) {
                                       throw new Object("Untrusted URL");
                                    }

                                    this.parseMMCDocument(data, this._httpUserAgent.getContentType());
                                    if (this._cancelRequested) {
                                       var11 = false;
                                       var16 = false;
                                       break;
                                    }

                                    if (this._parserHandler == null) {
                                       var11 = false;
                                       var16 = false;
                                       break;
                                    }

                                    try {
                                       data = this._parserHandler.sendReport();
                                    } catch (Throwable var22) {
                                       this._errorOccurred = true;
                                       ProvisioningServiceAgent.logEvents(ioe.toString());
                                       msg = ioe.getMessage();
                                       var11 = false;
                                       var16 = false;
                                       break;
                                    }

                                    this._parserHandler = null;
                                 }
                              }
                           } finally {
                              if (var16) {
                                 this._errorOccurred = true;
                                 msg = _resources.getString(0);
                                 var11 = false;
                                 break label560;
                              }
                           }
                        } finally {
                           if (var11) {
                              if (this._cancelRequested) {
                                 this._psa.showMessage(_resources.getString(2), this._currentMode, 1);
                              } else {
                                 this._psa.showMessage(this._errorOccurred ? msg : _resources.getString(1), this._currentMode, 1);
                              }

                              if (!this._errorOccurred && !this._cancelRequested) {
                                 this._sendRegistrationRequest = true;
                              } else {
                                 RadioInternal.processOTASPMessage(0, new byte[0]);
                                 this._psa.discardTempStorage();
                              }

                              this.cleanup();
                           }
                        }

                        if (this._cancelRequested) {
                           this._psa.showMessage(_resources.getString(2), this._currentMode, 1);
                        } else {
                           this._psa.showMessage(this._errorOccurred ? msg : _resources.getString(1), this._currentMode, 1);
                        }

                        if (!this._errorOccurred && !this._cancelRequested) {
                           this._sendRegistrationRequest = true;
                        } else {
                           RadioInternal.processOTASPMessage(0, new byte[0]);
                           this._psa.discardTempStorage();
                        }

                        this.cleanup();
                        continue;
                     }

                     if (this._cancelRequested) {
                        this._psa.showMessage(_resources.getString(2), this._currentMode, 1);
                     } else {
                        this._psa.showMessage(this._errorOccurred ? msg : _resources.getString(1), this._currentMode, 1);
                     }

                     if (!this._errorOccurred && !this._cancelRequested) {
                        this._sendRegistrationRequest = true;
                     } else {
                        RadioInternal.processOTASPMessage(0, new byte[0]);
                        this._psa.discardTempStorage();
                     }

                     this.cleanup();
                     continue;
                  }

                  if (this._cancelRequested) {
                     this._psa.showMessage(_resources.getString(2), this._currentMode, 1);
                  } else {
                     this._psa.showMessage(this._errorOccurred ? msg : _resources.getString(1), this._currentMode, 1);
                  }

                  if (!this._errorOccurred && !this._cancelRequested) {
                     this._sendRegistrationRequest = true;
                  } else {
                     RadioInternal.processOTASPMessage(0, new byte[0]);
                     this._psa.discardTempStorage();
                  }

                  this.cleanup();
                  continue;
               }

               if (this._cancelRequested) {
                  this._psa.showMessage(_resources.getString(2), this._currentMode, 1);
               } else {
                  this._psa.showMessage(this._errorOccurred ? msg : _resources.getString(1), this._currentMode, 1);
               }

               if (!this._errorOccurred && !this._cancelRequested) {
                  this._sendRegistrationRequest = true;
               } else {
                  RadioInternal.processOTASPMessage(0, new byte[0]);
                  this._psa.discardTempStorage();
               }

               this.cleanup();
            }
         }
      }
   }

   private final void cleanup() {
      this._errorOccurred = false;
      if (this._timer != null) {
         this._timer.notifyDone();
         this._timer = null;
      }

      if (this._httpUserAgent != null) {
         this._httpUserAgent.cleanup();
      }

      this._theRequest = null;
      this._cancelRequested = false;
      this.closeTunnel();
      this._psa.setBusy(false);
   }

   private static final String decodeProxy(String proxyEncoded) {
      if (proxyEncoded == null) {
         return null;
      }

      int port = -1;
      int byteIndex = 0;
      byte firstByte = getByteFromHexString(proxyEncoded, byteIndex);
      if ((firstByte & 128) != 0) {
         if (getByteFromHexString(proxyEncoded, ++byteIndex) != 0) {
            throw new Object("Only IPv4 proxy addresses are supported");
         }
      }

      if ((firstByte & 64) != 0) {
         byte portByte1 = getByteFromHexString(proxyEncoded, ++byteIndex);
         byte portByte2 = getByteFromHexString(proxyEncoded, ++byteIndex);
         port = (portByte1 & 255) << 8 | portByte2 & 255;
      }

      int addressLength = firstByte & 63;
      if (addressLength != 4) {
         throw new Object(((StringBuffer)(new Object("Expected 4-byte address length but got "))).append(addressLength).toString());
      }

      StringBuffer address = (StringBuffer)(new Object());

      for (int i = 0; i < addressLength; i++) {
         if (i > 0) {
            address.append('.');
         }

         address.append(getByteFromHexString(proxyEncoded, ++byteIndex) & 255);
      }

      if (port >= 0) {
         address.append(':').append(port);
      }

      return address.toString();
   }

   private static final byte getByteFromHexString(String hexString, int byteIndex) {
      char digit1 = hexString.charAt(byteIndex << 1);
      char digit2 = hexString.charAt((byteIndex << 1) + 1);
      int h4 = NumberUtilities.hexDigitToInt(digit1);
      int h0 = NumberUtilities.hexDigitToInt(digit2);
      return (byte)(h4 << 4 | h0);
   }

   @Override
   public final void statusChanged(int status, int mode) {
      switch (status) {
         case 3:
            synchronized (this._tunnelSync) {
               this._tunnelSync.notify();
               return;
            }
         case 5:
         case 7:
            EventLogger.logEvent(4411276428801970910L, 1414423657, 5);
            this.cancelProcessing();
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void radioTurnedOff() {
      if (this._theRequest != null) {
         EventLogger.logEvent(4411276428801970910L, 1298362991, 5);
         this.cancelProcessing();
      }
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      if (state == 0 && this._sendRegistrationRequest) {
         this._sendRegistrationRequest = false;
         EventLogger.logEvent(4411276428801970910L, 1299411570);
         HRUtils.getThunks().sendRegistrationRequest();
      }
   }

   @Override
   public final void engResponseMasterReset(int type) {
   }

   @Override
   public final void engServiceProgramEvent(int type) {
   }

   @Override
   public final void engOTASPResponse(byte[] response) {
      EventLogger.logEvent(4411276428801970910L, 1297052274, 5);
      this.setOTAResult(response);
      if (this._parserHandler != null) {
         this._parserHandler.notifyResults();
      }
   }

   @Override
   public final void engDataInitialized() {
   }

   @Override
   public final void engDataChanged() {
   }

   @Override
   public final void engDataLogworthy(int type) {
   }
}
