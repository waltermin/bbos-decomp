package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class CallControlLogger {
   private CallControlLogger$CallEventLogger _myEventLogger;
   private CallControlLogger$CallCommandLogger _myCommandLogger;
   private final String _name;
   private final long _eventGUID;
   private byte[] _buf = new byte[0];
   private StringBuffer _strbuf;
   private int _strbufRootLen;
   private static final long LOGGER_GUID = -1833974700407082449L;
   private static final byte[] ACTIVATEBARRING = "ActivateBarring".getBytes();
   private static final byte[] ACTIVATEWAITING = "ActivateWaiting".getBytes();
   private static final byte[] ADDED = "Added".getBytes();
   private static final byte[] ADDTOCONF = "AddToConf".getBytes();
   private static final byte[] ANSWER = "Answer".getBytes();
   private static final byte[] CALLNAME = "CallName".getBytes();
   private static final byte[] CANHOLD = "CanHold".getBytes();
   private static final byte[] CANSWAP = "CanSwap".getBytes();
   private static final byte[] CANINVOKEXFER = "CanInvokeXfer".getBytes();
   private static final byte[] CANJOIN = "CanJoin".getBytes();
   private static final byte[] CANPARK = "CanPark".getBytes();
   private static final byte[] CANSENDTOVM = "CanSendToVM".getBytes();
   private static final byte[] CONNECTED = "Conn".getBytes();
   private static final byte[] DEACTIVATECALLFWD = "DeactivateCallFwd".getBytes();
   private static final byte[] DELIVERED = "Delivered".getBytes();
   private static final byte[] DISABLEDTMFECHO = "DisableDtmfEcho".getBytes();
   private static final byte[] DISCONNECTED = "Disc".getBytes();
   private static final byte[] DTMFDATA = "DtmfData".getBytes();
   private static final byte[] ENABLEFDN = "EnableFDN".getBytes();
   private static final byte[] END911CALLBACKMODE = "End911CallbackMode".getBytes();
   private static final byte[] FAILED = "Failed".getBytes();
   private static final byte[] FALSE = "false".getBytes();
   private static final byte[] FEATUREREADY = "FeatureReady".getBytes();
   private static final byte[] FLASH = "Flash".getBytes();
   private static final byte[] GETACTIVECALL = "GetActiveCall".getBytes();
   private static final byte[] GETCALLFWDNUMBER = "GetCallFwdNumber".getBytes();
   private static final byte[] GETCALLNUMBER = "GetCallNumber".getBytes();
   private static final byte[] GETCALLSTATE = "GetCallState".getBytes();
   private static final byte[] GETCLIP = "GetClip".getBytes();
   private static final byte[] GETDURATION = "GetDuration".getBytes();
   private static final byte[] GETEMERGENCYNUMBER = "GetEmergencyNumber".getBytes();
   private static final byte[] GETFWDNUMBER = "GetFwdNumber".getBytes();
   private static final byte[] GETHELD = "GetHeld".getBytes();
   private static final byte[] GETINCOMING = "GetIncoming".getBytes();
   private static final byte[] GETLINE = "GetLine".getBytes();
   private static final byte[] GETLINELABEL = "GetLineLabel".getBytes();
   private static final byte[] GETLINENUMBER = "GetLineNumber".getBytes();
   private static final byte[] GETLINES = "GetLines".getBytes();
   private static final byte[] GETMAXCONFMEMBERS = "GetMaxConfMembers".getBytes();
   private static final byte[] GETNETWORKFEATURES = "GetNetworkFeatures".getBytes();
   private static final byte[] GETNUMBER = "GetNumber".getBytes();
   private static final byte[] GETVMCOUNT = "GetVMCount".getBytes();
   private static final byte[] GETVMNUMBER = "GetVMNumber".getBytes();
   private static final byte[] GETWAFS = "GetWAFs".getBytes();
   private static final byte[] GETXFERSTATE = "GetXferState".getBytes();
   private static final byte[] HELD = "Held".getBytes();
   private static final byte[] HOLD = "Hold".getBytes();
   private static final byte[] INCALLDTMF = "InCallDtmf".getBytes();
   private static final byte[] INCOMING = "Incoming".getBytes();
   private static final byte[] INITIATED = "Init".getBytes();
   private static final byte[] ISACTIVE = "IsActive".getBytes();
   private static final byte[] ISCFUACTIVE = "IsCfuActive".getBytes();
   private static final byte[] ISEMERGENCYNUMBER = "IsEmergencyNumber".getBytes();
   private static final byte[] ISFDNAVAILABLE = "IsFDNAvailable".getBytes();
   private static final byte[] ISFDNENABLED = "IsFDNEnabled".getBytes();
   private static final byte[] ISLINEAVAILABLE = "IsLineAvailable".getBytes();
   private static final byte[] ISREDIRECTED = "IsRedirected".getBytes();
   private static final byte[] LINECHANGED = "LineChanged".getBytes();
   private static final byte[] LINESUPDATED = "LinesUpdated".getBytes();
   private static final byte[] MANIPULATEFAILED = "ManipulateFailed".getBytes();
   private static final byte[] NULL = "null".getBytes();
   private static final byte[] OTAUPDATED = "OTAUpdated".getBytes();
   private static final byte[] PARK = "Park".getBytes();
   private static final byte[] PRIVACY = "Privacy".getBytes();
   private static final byte[] QUERYSSOPTION = "QuerySsOption".getBytes();
   private static final byte[] QUERYSSOPTIONRESULT = "QuerySsOptionResult".getBytes();
   private static final byte[] REJECTCALL = "RejectCall".getBytes();
   private static final byte[] REMOVECALL = "RemoveCall".getBytes();
   private static final byte[] REMOVED = "Removed".getBytes();
   private static final byte[] REQUESTENABLEFDN = "RequestEnableFDN".getBytes();
   private static final byte[] RESULT = "Ret".getBytes();
   private static final byte[] RESUMECALL = "ResumeCall".getBytes();
   private static final byte[] RESUMED = "Resumed".getBytes();
   private static final byte[] SENDSSPWDRESPONSE = "SendSsPwdResponse".getBytes();
   private static final byte[] SENDTOVM = "SendToVM".getBytes();
   private static final byte[] SETBARRINGPWD = "SetBarringPwd".getBytes();
   private static final byte[] SETFWDNUMBER = "SetFwdNumber".getBytes();
   private static final byte[] SETLINE = "SetLine".getBytes();
   private static final byte[] SETLINELABEL = "SetLineLabel".getBytes();
   private static final byte[] SETSSBASICSERVICE = "SetSsBasicService".getBytes();
   private static final byte[] SETUSSDRESPONSE = "SetUssdResponse".getBytes();
   private static final byte[] SSNOTIFY = "SSNotify".getBytes();
   private static final byte[] SSPWDREQUESTED = "SSPwdRequested".getBytes();
   private static final byte[] SSRQFAIL = "SSRqFail".getBytes();
   private static final byte[] SSRQINVALIDPWD = "SSRqInvalidPwd".getBytes();
   private static final byte[] SSRQREJECTED = "SSRqRejected".getBytes();
   private static final byte[] SSRQRELEASED = "SSRqReleased".getBytes();
   private static final byte[] SSRQSUCCESS = "SSRqSuccess".getBytes();
   private static final byte[] SSUPDATED = "SSUpdated".getBytes();
   private static final byte[] SSUSSD = "SSUssd".getBytes();
   private static final byte[] STARTCALL = "StartCall".getBytes();
   private static final byte[] STARTDTMF = "StartDtmf".getBytes();
   private static final byte[] STOPALLCALLS = "StopAllCalls".getBytes();
   private static final byte[] STOPCALL = "StopCall".getBytes();
   private static final byte[] STOPDTMF = "StopDtmf".getBytes();
   private static final byte[] SUPPORTSEXT = "SupportsExt".getBytes();
   private static final byte[] SWAP = "Swap".getBytes();
   private static final byte[] TIMER = "Timer".getBytes();
   private static final byte[] TRUE = "true".getBytes();
   private static final byte[] UPDATED = "Updated".getBytes();
   private static final byte[] VMCOUNTUPDATED = "VmCountUpdated".getBytes();
   private static final byte[] WAITING = "Waiting".getBytes();
   private static final byte[] XFER = "Xfer".getBytes();
   private static final byte[] XFERACTION = "XferAction".getBytes();
   private static final byte[] XFERUPDATED = "XferUpdated".getBytes();

   public CallControlLogger(String name, int order) {
      this._strbuf = new StringBuffer(name + ": ");
      this._strbufRootLen = this._strbuf.length();
      this._name = name;
      this._eventGUID = -1833974700407082449L + order;
      EventLogger.register(this._eventGUID, name, 2);
      this._myCommandLogger = new CallControlLogger$CallCommandLogger(this, order);
      this._myEventLogger = new CallControlLogger$CallEventLogger(this, order);
      AbstractCallCommandHandler.internalRegister(this._myCommandLogger);
      AbstractCallEventHandler.internalRegister(this._myEventLogger);
   }

   private final void log(int len) {
      Array.resize(this._buf, len);
      this.log(this._buf);
   }

   private final void log(byte[] buf) {
      EventLogger.logEvent(this._eventGUID, buf);
      this._strbuf.setLength(this._strbufRootLen);
      StringUtilities.append(this._strbuf, buf, 0, buf.length);
      System.out.println(this._strbuf);
   }

   private final int copy(byte[] arg) {
      Array.resize(this._buf, 64);
      int argLen = arg.length;
      System.arraycopy(arg, 0, this._buf, 0, argLen);
      return argLen;
   }

   private final int append(int offset, byte[] arg) {
      try {
         if (arg == null) {
            arg = NULL;
         }

         this._buf[offset++] = 44;
         int argLen = arg.length;
         System.arraycopy(arg, 0, this._buf, offset, argLen);
         return offset + argLen;
      } catch (IndexOutOfBoundsException ioobe) {
         return offset;
      } catch (Exception e) {
         return offset;
      }
   }

   private final int append(int offset, long arg) {
      try {
         this._buf[offset++] = 44;
         int argLen = NumberUtilities.appendNumber(offset, this._buf, arg, 10);
         return offset + argLen;
      } catch (IndexOutOfBoundsException ioobe) {
         return offset;
      } catch (Exception e) {
         return offset;
      }
   }

   private final int append(int offset, char arg) {
      try {
         this._buf[offset++] = (byte)arg;
         return offset;
      } catch (IndexOutOfBoundsException ioobe) {
         return offset;
      } catch (Exception e) {
         return offset;
      }
   }

   private final int append(int offset, boolean arg) {
      return this.append(offset, arg ? TRUE : FALSE);
   }

   private final int append(int offset, Object arg) {
      try {
         if (arg == null) {
            return this.append(offset, NULL);
         }

         if (arg instanceof String) {
            StringBuffer strbuf = new StringBuffer((String)arg);
            strbuf.insert(0, '"');
            strbuf.append('"');
            return this.append(offset, strbuf.toString().getBytes());
         }

         if (!(arg instanceof int[])) {
            return this.append(offset, arg.toString().getBytes());
         }

         int[] array = (int[])arg;
         int len = array.length;
         offset = this.append(offset, '[');
         if (len > 0) {
            offset += NumberUtilities.appendNumber(offset, this._buf, array[0], 10);

            for (int idx = 1; idx < len; idx++) {
               offset = this.append(offset, ',');
               offset += NumberUtilities.appendNumber(offset, this._buf, array[idx], 10);
            }
         }

         return this.append(offset, (char)93);
      } catch (IndexOutOfBoundsException ioobe) {
         return offset;
      } catch (Exception e) {
         return offset;
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, boolean argB) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA) {
      if (EventLogger.getMinimumLevel() >= level) {
         this.log(argA);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, Object argB) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, byte[] argB) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, int argC) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, boolean argC) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, Object argC) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, Object argB, int argC) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, Object argB, Object argC) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, byte[] argB, byte[] argC) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, int argC, int argD) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         len = this.append(len, argD);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, boolean argB, int argC, Object argD) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         len = this.append(len, argD);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, int argC, Object argD) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         len = this.append(len, argD);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, byte[] argB, int argC, boolean argD) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         len = this.append(len, argD);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, int argC, boolean argD) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         len = this.append(len, argD);
         this.log(len);
      }
   }

   private final synchronized void logEvent(int level, byte[] argA, int argB, int argC, int argD, int argE, boolean argF, boolean argG) {
      if (EventLogger.getMinimumLevel() >= level) {
         int len = this.copy(argA);
         len = this.append(len, argB);
         len = this.append(len, argC);
         len = this.append(len, argD);
         len = this.append(len, argE);
         len = this.append(len, argF);
         len = this.append(len, argG);
         this.log(len);
      }
   }

   private static final String obfuscate(String str) {
      return str != null && str.length() > 0 && PersistentContent.isEncryptionEnabled() ? "-----" : str;
   }
}
