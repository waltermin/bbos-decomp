package net.rim.device.cldc.io.waphttp;

import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.bearer.RIM_UDP_Bearer;
import com.fourthpass.wapstack.ota.Dispatcher;
import com.fourthpass.wapstack.util.InetAddress;
import com.fourthpass.wapstack.util.PushEvent;
import com.fourthpass.wapstack.util.RedirectEvent;
import com.fourthpass.wapstack.util.UserDefinedEvent;
import com.fourthpass.wapstack.wdp.WDPLayer;
import com.fourthpass.wapstack.wsp.WSPConnectionless;
import com.fourthpass.wapstack.wsp.WSPContextObject;
import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import com.fourthpass.wapstack.wsp.WSPHeaders;
import com.fourthpass.wapstack.wsp.WSPMethod;
import com.fourthpass.wapstack.wtp.WTPLayer;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import java.io.InputStream;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.device.cldc.io.tunnel.TunnelListener;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.browser.util.PipeInputStream;
import net.rim.device.internal.browser.wap.IWAPPushProvider;
import net.rim.device.internal.browser.wap.WAPPushProviderRegistry;
import net.rim.device.internal.proxy.Proxy;

final class WAPConnectionImpl extends WAPConnection implements IWapStackLayer, TunnelListener, GlobalEventListener {
   private Object _syncObject = new Object();
   private WAPConnectionParams _params;
   private WAPConnectionImpl$CurrentConfig _currentConfig;
   private boolean _newConnectionParameters;
   private Object _tunnelSyncObject = new Object();
   private int _tunnelStatus;
   private Tunnel _tunnel;
   private String _key;
   private Object _semaphore = new Object();
   private boolean _semaphoreInUse;
   public static final long WAP_STACK_EVENT_LOGGER_GUID = -2968315138570648581L;
   public static final String WAP_STACK_EVENT_LOGGER_TITLE = "net.rim.wap";
   public static final int WAPSA_BEFORE_OPEN_CONNECTION = 1466068835;
   public static final int WAPSA_BEFORE_CONNECT_REQUEST = 1466065778;
   public static final int WAPSA_CONNECT_SUCCESSFUL = 1466135328;
   public static final int WAPSA_CONNECT_FAILED = 1466132000;
   public static final int WAPSA_GET_URI_TOP = 1466398068;
   public static final int WAPSA_GET_URI_INVOKE = 1466398057;
   public static final int WAPSA_GET_URI_AFTER_WAIT = 1466398049;
   public static final int WAPSA_GET_URI_BOTTOM = 1466398050;
   public static final int WAPSA_ABORT_START = 1466004256;
   public static final int WAPSA_ABORT_END = 1466000672;
   public static final int WAPSA_SET_PARAMS_TOP = 1467183220;
   public static final int WAPSA_SET_PARAMS_NO_REC = 1467182706;
   public static final int WAPSA_SET_PARAMS_NOT_GPRS = 1467182695;
   public static final int WAPSA_SET_PARAMS_NOT_IPV4 = 1467182697;
   public static final int WAPSA_SET_PARAMS_NO_ADDRESSES = 1467182689;
   private static final int MAX_RETRIES = 3;
   private static final int MAX_REDIRECTS = 30;
   private static final int WSP_TIMEOUT = 30;
   private static final int WSP_TIMEOUT_SECURE_ACCESS = 60;
   private static final int SESSION_TIMEOUT = 165000;
   private static final int STATUS_ABORTED = -1;
   private static final InetAddress LOCAL_ADDRESS = InetAddress.getLocalHost();
   private static final String[] CONNECT_HEADER_REMOVE = new String[]{
      "Cookie",
      "Content-Length",
      "Content-Type",
      "ETag",
      "If-Modified-Since",
      "If-Match",
      "If-None-Match",
      "If-Range",
      "If-Unmodified-Since",
      "Referer",
      "Transfer-Encoding",
      "WWW-Authenticate"
   };

   public final void sendRequest(WAPRequestImpl param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 004: dup
      // 005: astore 2
      // 006: monitorenter
      // 007: aload 0
      // 008: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 00b: ifnonnull 019
      // 00e: new java/lang/Object
      // 011: dup
      // 012: ldc_w "Connection closed"
      // 015: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 018: athrow
      // 019: aload 0
      // 01a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 01d: invokestatic net/rim/vm/Process.currentProcess ()Lnet/rim/vm/Process;
      // 020: invokevirtual net/rim/vm/Process.getProcessId ()I
      // 023: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentPid I
      // 026: aload 0
      // 027: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 02a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 02d: iflt 046
      // 030: invokestatic net/rim/device/internal/proxy/Proxy.getInstance ()Lnet/rim/device/internal/proxy/Proxy;
      // 033: aload 0
      // 034: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 037: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 03a: invokevirtual net/rim/device/api/system/Application.cancelInvokeLater (I)V
      // 03d: aload 0
      // 03e: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 041: bipush -1
      // 043: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 046: aload 2
      // 047: monitorexit
      // 048: goto 050
      // 04b: astore 3
      // 04c: aload 2
      // 04d: monitorexit
      // 04e: aload 3
      // 04f: athrow
      // 050: aconst_null
      // 051: astore 2
      // 052: bipush 0
      // 053: istore 3
      // 054: aload 0
      // 055: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 058: dup
      // 059: astore 4
      // 05b: monitorenter
      // 05c: aload 0
      // 05d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphoreInUse Z
      // 060: ifeq 08d
      // 063: aload 1
      // 064: invokevirtual net/rim/device/cldc/io/waphttp/WAPRequestImpl.isAborted ()Z
      // 067: ifeq 075
      // 06a: new java/lang/Object
      // 06d: dup
      // 06e: sipush 1000
      // 071: invokespecial net/rim/device/cldc/io/waphttp/WAPIOException.<init> (I)V
      // 074: athrow
      // 075: aload 1
      // 076: aload 0
      // 077: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 07a: invokevirtual net/rim/device/cldc/io/waphttp/WAPRequestImpl.setAbortContext (Ljava/lang/Object;)V
      // 07d: aload 0
      // 07e: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 081: invokevirtual java/lang/Object.wait ()V
      // 084: goto 089
      // 087: astore 5
      // 089: aload 1
      // 08a: invokevirtual net/rim/device/cldc/io/waphttp/WAPRequestImpl.clearAbortContext ()V
      // 08d: aload 1
      // 08e: invokevirtual net/rim/device/cldc/io/waphttp/WAPRequestImpl.isAborted ()Z
      // 091: ifeq 09f
      // 094: new java/lang/Object
      // 097: dup
      // 098: sipush 1000
      // 09b: invokespecial net/rim/device/cldc/io/waphttp/WAPIOException.<init> (I)V
      // 09e: athrow
      // 09f: aload 0
      // 0a0: bipush 1
      // 0a1: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphoreInUse Z
      // 0a4: aload 4
      // 0a6: monitorexit
      // 0a7: goto 0b2
      // 0aa: astore 6
      // 0ac: aload 4
      // 0ae: monitorexit
      // 0af: aload 6
      // 0b1: athrow
      // 0b2: aload 0
      // 0b3: aload 1
      // 0b4: invokespecial net/rim/device/cldc/io/waphttp/WAPConnectionImpl.sendRequestInternal (Lnet/rim/device/cldc/io/waphttp/WAPRequestImpl;)V
      // 0b7: bipush 1
      // 0b8: istore 3
      // 0b9: aload 0
      // 0ba: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 0bd: dup
      // 0be: astore 4
      // 0c0: monitorenter
      // 0c1: aload 0
      // 0c2: bipush 0
      // 0c3: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphoreInUse Z
      // 0c6: aload 0
      // 0c7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 0ca: invokevirtual java/lang/Object.notify ()V
      // 0cd: aload 4
      // 0cf: monitorexit
      // 0d0: goto 168
      // 0d3: astore 7
      // 0d5: aload 4
      // 0d7: monitorexit
      // 0d8: aload 7
      // 0da: athrow
      // 0db: astore 4
      // 0dd: aload 4
      // 0df: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.getError ()I
      // 0e2: sipush 1000
      // 0e5: if_icmpeq 0ef
      // 0e8: aload 0
      // 0e9: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionImpl.close ()V
      // 0ec: goto 0f1
      // 0ef: bipush 1
      // 0f0: istore 3
      // 0f1: aload 4
      // 0f3: astore 2
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 0f8: dup
      // 0f9: astore 4
      // 0fb: monitorenter
      // 0fc: aload 0
      // 0fd: bipush 0
      // 0fe: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphoreInUse Z
      // 101: aload 0
      // 102: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 105: invokevirtual java/lang/Object.notify ()V
      // 108: aload 4
      // 10a: monitorexit
      // 10b: goto 168
      // 10e: astore 8
      // 110: aload 4
      // 112: monitorexit
      // 113: aload 8
      // 115: athrow
      // 116: astore 4
      // 118: aload 0
      // 119: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionImpl.close ()V
      // 11c: aload 4
      // 11e: astore 2
      // 11f: aload 0
      // 120: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 123: dup
      // 124: astore 4
      // 126: monitorenter
      // 127: aload 0
      // 128: bipush 0
      // 129: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphoreInUse Z
      // 12c: aload 0
      // 12d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 130: invokevirtual java/lang/Object.notify ()V
      // 133: aload 4
      // 135: monitorexit
      // 136: goto 168
      // 139: astore 9
      // 13b: aload 4
      // 13d: monitorexit
      // 13e: aload 9
      // 140: athrow
      // 141: astore 10
      // 143: aload 0
      // 144: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 147: dup
      // 148: astore 11
      // 14a: monitorenter
      // 14b: aload 0
      // 14c: bipush 0
      // 14d: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphoreInUse Z
      // 150: aload 0
      // 151: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._semaphore Ljava/lang/Object;
      // 154: invokevirtual java/lang/Object.notify ()V
      // 157: aload 11
      // 159: monitorexit
      // 15a: goto 165
      // 15d: astore 12
      // 15f: aload 11
      // 161: monitorexit
      // 162: aload 12
      // 164: athrow
      // 165: aload 10
      // 167: athrow
      // 168: aload 0
      // 169: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 16c: dup
      // 16d: astore 4
      // 16f: monitorenter
      // 170: aload 0
      // 171: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 174: ifnull 1de
      // 177: aload 0
      // 178: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 17b: invokestatic net/rim/vm/Process.currentProcess ()Lnet/rim/vm/Process;
      // 17e: invokevirtual net/rim/vm/Process.getProcessId ()I
      // 181: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentPid I
      // 184: iload 3
      // 185: ifeq 1de
      // 188: aload 0
      // 189: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 18c: ifnull 1de
      // 18f: invokestatic net/rim/device/internal/proxy/Proxy.getInstance ()Lnet/rim/device/internal/proxy/Proxy;
      // 192: astore 5
      // 194: aload 0
      // 195: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 198: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 19b: iflt 1aa
      // 19e: aload 5
      // 1a0: aload 0
      // 1a1: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 1a4: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 1a7: invokevirtual net/rim/device/api/system/Application.cancelInvokeLater (I)V
      // 1aa: aload 0
      // 1ab: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 1ae: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._timeout I
      // 1b1: ifle 1d5
      // 1b4: aload 0
      // 1b5: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 1b8: aload 5
      // 1ba: new net/rim/device/cldc/io/waphttp/WAPConnectionImpl$ShutdownConnection
      // 1bd: dup
      // 1be: aload 0
      // 1bf: bipush 0
      // 1c0: invokespecial net/rim/device/cldc/io/waphttp/WAPConnectionImpl$ShutdownConnection.<init> (Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl;Z)V
      // 1c3: aload 0
      // 1c4: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 1c7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._timeout I
      // 1ca: i2l
      // 1cb: bipush 0
      // 1cc: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;JZ)I
      // 1cf: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 1d2: goto 1de
      // 1d5: aload 0
      // 1d6: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 1d9: bipush -1
      // 1db: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentTimer I
      // 1de: aload 4
      // 1e0: monitorexit
      // 1e1: goto 1ec
      // 1e4: astore 13
      // 1e6: aload 4
      // 1e8: monitorexit
      // 1e9: aload 13
      // 1eb: athrow
      // 1ec: aload 2
      // 1ed: ifnull 1f2
      // 1f0: aload 2
      // 1f1: athrow
      // 1f2: return
      // try (5 -> 33): 34 null
      // try (34 -> 37): 34 null
      // try (63 -> 66): 67 null
      // try (48 -> 83): 84 null
      // try (84 -> 87): 84 null
      // try (99 -> 107): 108 null
      // try (108 -> 111): 108 null
      // try (89 -> 94): 113 null
      // try (130 -> 138): 139 null
      // try (139 -> 142): 139 null
      // try (89 -> 94): 144 null
      // try (154 -> 162): 163 null
      // try (163 -> 166): 163 null
      // try (89 -> 94): 168 null
      // try (113 -> 125): 168 null
      // try (144 -> 149): 168 null
      // try (174 -> 182): 183 null
      // try (183 -> 186): 183 null
      // try (168 -> 169): 168 null
      // try (195 -> 245): 246 null
      // try (246 -> 249): 246 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final boolean getURI(WSPHeaders headers, WAPRequestImpl fetchRequest) {
      EventLogger.logEvent(-2968315138570648581L, 1466398068, 5);
      InputStream resultStream = null;
      int rawStatus = 0;
      byte[] postData = fetchRequest.getPostData();
      byte methodType = fetchRequest.getRequestMethodByte();
      String url = fetchRequest.getURLWithoutRIMParameters();
      if (url != null && !url.equals("rim://openconnection")) {
         synchronized (this._syncObject) {
            if (this._currentConfig == null) {
               return false;
            }

            int maxServerSDUSize = this._currentConfig._currentWapSession.getMaxServerSDUSize();
            if (postData != null && maxServerSDUSize != 0 && postData.length > maxServerSDUSize) {
               throw new Object(1005, 230);
            }
         }

         if (!this._params._connectionMode) {
            synchronized (this._syncObject) {
               if (this._currentConfig == null) {
                  return false;
               }

               boolean var35 = false /* VF: Semaphore variable */;

               try {
                  var35 = true;
                  resultStream = this._currentConfig._connectionless.getURL(url.getBytes(), headers, methodType, postData);
                  var35 = false;
               } finally {
                  if (var35) {
                     throw new Object(1006);
                  }
               }

               rawStatus = this._currentConfig._connectionless.getStatus();
            }
         } else {
            WSPContextObject contextObject;
            synchronized (fetchRequest.getAbortLock()) {
               label381: {
                  boolean var10000;
                  synchronized (this._syncObject) {
                     if (this._currentConfig != null) {
                        this._currentConfig._method = new WSPMethod(
                           this._currentConfig._wspSession, this._currentConfig._wtpLayer, url.getBytes(), headers, methodType, postData
                        );
                        fetchRequest.setAbortContext(this._currentConfig._method);
                        if (fetchRequest.isAborted()) {
                           throw new Object(1000);
                        }

                        EventLogger.logEvent(-2968315138570648581L, 1466398057, 5);
                        contextObject = this._currentConfig._wspSession.invokeMethodCommand(this._currentConfig._method, headers);
                        break label381;
                     }

                     var10000 = false;
                  }

                  return var10000;
               }
            }

            synchronized (contextObject) {
               if (!contextObject.getCompleted()) {
                  label363:
                  try {
                     contextObject.wait();
                  } finally {
                     break label363;
                  }
               }
            }

            resultStream = (InputStream)contextObject.getResultObject();
            EventLogger.logEvent(-2968315138570648581L, 1466398049, 5);
            int abortCode = 0;
            synchronized (fetchRequest.getAbortLock()) {
               label356: {
                  boolean var54;
                  synchronized (this._syncObject) {
                     if (this._currentConfig != null) {
                        if (this._currentConfig._method != null) {
                           if (resultStream == null) {
                              abortCode = this._currentConfig._method.getAbortReason() & 255;
                           }

                           rawStatus = this._currentConfig._method.getStatus();
                        }

                        if (this._currentConfig._wspSession != null) {
                           this._currentConfig._wspSession.unregisterMethod(this._currentConfig._method);
                        }

                        fetchRequest.clearAbortContext();
                        this._currentConfig._method = null;
                        break label356;
                     }

                     var54 = false;
                  }

                  return var54;
               }
            }

            if (resultStream == null && (abortCode >= 1 && abortCode <= 9 || abortCode == 230)) {
               throw new Object(1005, abortCode);
            }
         }

         int status = 0;
         if (rawStatus < 80) {
            status = rawStatus / 16 * 100 + rawStatus % 16;
         } else if (rawStatus < 96) {
            status = (rawStatus / 16 - 1) * 100 + rawStatus % 16 + 16;
         } else {
            status = (rawStatus / 16 - 1) * 100 + rawStatus % 16;
         }

         if (resultStream != null) {
            WSPHeaderDecoder oldHeaderDecoder = (WSPHeaderDecoder)(new Object(fetchRequest.getResponseHeaders()));
            oldHeaderDecoder.decode(headers.getAttributeList(), true);
            fetchRequest.setPipe(((PipeInputStream)resultStream).getCacheableData());
            fetchRequest.setInputStream(resultStream);
            fetchRequest.setStatus(status);
            EventLogger.logEvent(-2968315138570648581L, 1466398050, 5);
            return true;
         } else if (fetchRequest.isAborted()) {
            throw new Object(1000);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public final void abort(Object abortContext) {
      EventLogger.logEvent(-2968315138570648581L, 1466004256, 5);
      if (abortContext instanceof WSPMethod) {
         synchronized (this._syncObject) {
            if (this._currentConfig == null) {
               return;
            }

            if (this._currentConfig._method != null && this._currentConfig._method == abortContext) {
               this._currentConfig._method.abort();
            }
         }
      } else if (abortContext instanceof Object) {
         synchronized (this._tunnelSyncObject) {
            this._tunnelStatus = -1;
            this._tunnelSyncObject.notify();
         }
      } else if (abortContext instanceof Object) {
         this.close();
      } else if (abortContext == this._semaphore) {
         synchronized (this._semaphore) {
            this._semaphore.notify();
         }
      }

      EventLogger.logEvent(-2968315138570648581L, 1466000672, 5);
   }

   public final void close(boolean force) {
      WAPConnectionRegistry.getInstance();
      WAPConnectionRegistry.removeConnection(this);
      Proxy.getInstance().removeGlobalEventListener(this);
      synchronized (this._syncObject) {
         if (this._currentConfig != null && this._currentConfig._wspSession != null) {
            if (this._currentConfig._method != null) {
               this._currentConfig._method.abort();
            }

            if (!force && this._currentConfig._suspendSession) {
               this._currentConfig._currentWapSession.setId(this._currentConfig._wspSession.getSessionId());
               WAPSessionManager.getInstance().addSession(this._currentConfig._currentWapSession);
               this._currentConfig._wspSession.suspendRequest();
            } else {
               this._currentConfig._wspSession.disconnectRequest();
            }

            this._currentConfig._wspSession.close();
         } else if (this._currentConfig != null && this._currentConfig._wtpLayer != null) {
            this._currentConfig._wtpLayer.close();
         } else if (this._currentConfig != null && this._currentConfig._bearer != null) {
            this._currentConfig._bearer.closeConnection();
         }

         Proxy proxyInstance = Proxy.getInstance();
         if (this._currentConfig != null && this._currentConfig._currentTimer >= 0) {
            proxyInstance.cancelInvokeLater(this._currentConfig._currentTimer);
            this._currentConfig._currentTimer = -1;
         }

         if (this._currentConfig != null && this._currentConfig._wspContext != null) {
            synchronized (this._currentConfig._wspContext) {
               this._currentConfig._wspContext.notifyAll();
            }
         }

         this._currentConfig = null;
         if (this._tunnel != null) {
            this._tunnel.close();
            this._tunnel = null;
         }

         RIMGlobalMessagePoster.postGlobalEvent(3729075673904713354L, 0, 0, this, null);
      }

      synchronized (this._tunnelSyncObject) {
         this._tunnelStatus = -1;
         this._tunnelSyncObject.notify();
      }
   }

   public final IWapStackLayer getSubmissionLayer() {
      return null;
   }

   public final IWapStackLayer getUserLayer() {
      return null;
   }

   public final SessionStats getSessionStats() {
      synchronized (this._syncObject) {
         return this._currentConfig != null && this._currentConfig._bearer != null ? this._currentConfig._bearer.getSessionStats() : null;
      }
   }

   public final SecurityInfo getRIMSecurityInfo() {
      synchronized (this._syncObject) {
         return this._currentConfig != null && this._currentConfig._wtlsLayer != null ? this._currentConfig._wtlsLayer.getRIMSecurityInfo() : null;
      }
   }

   @Override
   public final void statusChanged(int status, int code) {
      synchronized (this._tunnelSyncObject) {
         this._tunnelStatus = status;
         this._tunnelSyncObject.notify();
      }
   }

   @Override
   public final void setUserLayer(IWapStackLayer userLayer) {
   }

   @Override
   public final void eventOccured(Object object) {
      if (object instanceof UserDefinedEvent) {
         UserDefinedEvent event = (UserDefinedEvent)object;
         switch (event.getEventType()) {
            case 0:
            case 2:
               break;
            case 1:
            default:
               RedirectEvent evt = (RedirectEvent)event.getEventData();
               synchronized (this._syncObject) {
                  if (this._currentConfig != null) {
                     this._currentConfig._redirectAddress = evt.getRedirectAddress()[0];
                     this._currentConfig._reuseSecurity = evt.isReuseSecurity();
                  }

                  return;
               }
            case 3:
               PushEvent evt = (PushEvent)event.getEventData();
               if (evt.getPushData() == null) {
                  break;
               }

               WTPLayer wtpLayer;
               synchronized (this._syncObject) {
                  if (this._currentConfig == null) {
                     return;
                  }

                  wtpLayer = this._currentConfig._wtpLayer;
               }

               WTP_PDU pdu = evt.getPushData();

               try {
                  Object[] data = Dispatcher.processRawData(
                     false, pdu.getUserData(), 0, new InetAddress(this._params._wapServerAddress).toString(), wtpLayer, pdu
                  );
                  if (data != null) {
                     IWAPPushProvider provider = WAPPushProviderRegistry.getInstanceNoWait();
                     if (provider != null) {
                        provider.pushReceived((HttpHeaders)data[0], (PushInputStream)data[1]);
                        return;
                     }
                  }
                  break;
               } finally {
                  break;
               }
            case 4:
               synchronized (this._syncObject) {
                  if (this._currentConfig != null) {
                     if (this._currentConfig._method != null) {
                        this._currentConfig._method.abort();
                     }

                     if (this._currentConfig._wspSession != null) {
                        this._currentConfig._wspSession.disconnectRegisteredMethods();
                        this._currentConfig._wspSession.disconnectRegisteredTransactions();
                        this._currentConfig._wspSession.close();
                        this._currentConfig._wspSession = null;
                     } else if (this._currentConfig._wtpLayer != null) {
                        this._currentConfig._wtpLayer.close();
                     } else if (this._currentConfig._bearer != null) {
                        this._currentConfig._bearer.closeConnection();
                     }
                  }

                  return;
               }
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -1270659756336956134L) {
         boolean found = false;
         synchronized (this._syncObject) {
            if (object0 instanceof int[]) {
               int[] pids = (int[])object0;
               int pid = -1;
               if (this._currentConfig != null) {
                  pid = this._currentConfig._currentPid;
               }

               if (pid != -1) {
                  for (int i = 0; !found && i < pids.length; i++) {
                     found = pid == pids[i];
                  }
               }
            }
         }

         if (found) {
            Proxy.getInstance().invokeLater(new WAPConnectionImpl$ShutdownConnection(this, true));
         }
      }
   }

   private final WSPHeaders encodeHeaders(HttpHeaders requestHeaders, byte[] postData, boolean sendConnectHeaders) {
      if (requestHeaders == null) {
         requestHeaders = (HttpHeaders)(new Object());
      }

      if (!sendConnectHeaders) {
         return new WSPHeaders(requestHeaders, postData);
      }

      HttpHeaders connectHeaders = (HttpHeaders)(new Object());
      int index = 0;

      while (true) {
         String key = requestHeaders.getPropertyKey(index);
         if (key == null) {
            return new WSPHeaders(connectHeaders, null);
         }

         int i = 0;

         while (i < CONNECT_HEADER_REMOVE.length && !StringUtilities.strEqualIgnoreCase(CONNECT_HEADER_REMOVE[i], key, 1701707776)) {
            i++;
         }

         if (i == CONNECT_HEADER_REMOVE.length) {
            connectHeaders.addProperty(key, requestHeaders.getPropertyValue(index));
         }

         index++;
      }
   }

   @Override
   public final String getKey() {
      return this._key;
   }

   @Override
   public final void init(String key, WAPConnectionParams param) {
      this._key = key;
      this._params = param;
      this._currentConfig = new WAPConnectionImpl$CurrentConfig();
      WAPConnectionRegistry.getInstance();
      WAPConnectionRegistry.addConnection(this);
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private final boolean openConnectionModeConnection(
      InetAddress param1, int param2, String param3, int param4, WSPHeaders param5, WAPRequestImpl param6, InetAddress param7, int param8
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: invokespecial java/lang/StringBuffer.<init> ()V
      // 007: aload 7
      // 009: invokevirtual com/fourthpass/wapstack/util/InetAddress.toString ()Ljava/lang/String;
      // 00c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 00f: aload 3
      // 010: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 013: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 016: astore 9
      // 018: aload 0
      // 019: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 01c: dup
      // 01d: astore 13
      // 01f: monitorenter
      // 020: aload 0
      // 021: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 024: ifnonnull 02c
      // 027: bipush 0
      // 028: aload 13
      // 02a: monitorexit
      // 02b: ireturn
      // 02c: aload 0
      // 02d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 030: invokestatic net/rim/device/cldc/io/waphttp/WAPSessionManager.getInstance ()Lnet/rim/device/cldc/io/waphttp/WAPSessionManager;
      // 033: aload 9
      // 035: invokevirtual net/rim/device/cldc/io/waphttp/WAPSessionManager.removeSession (Ljava/lang/String;)Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 038: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentWapSession Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 03b: aload 0
      // 03c: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 03f: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._useSessionResume Z
      // 042: ifeq 069
      // 045: aload 0
      // 046: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 049: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentWapSession Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 04c: ifnull 069
      // 04f: bipush 1
      // 050: istore 10
      // 052: new com/fourthpass/wapstack/wsp/WSPHeaders
      // 055: dup
      // 056: aload 0
      // 057: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 05a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentWapSession Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 05d: invokevirtual net/rim/device/cldc/io/waphttp/WAPSession.getHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 060: aconst_null
      // 061: invokespecial com/fourthpass/wapstack/wsp/WSPHeaders.<init> (Lnet/rim/device/api/io/http/HttpHeaders;Ljava/lang/Object;)V
      // 064: astore 5
      // 066: goto 081
      // 069: aload 0
      // 06a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 06d: new net/rim/device/cldc/io/waphttp/WAPSession
      // 070: dup
      // 071: aload 9
      // 073: aload 5
      // 075: invokevirtual com/fourthpass/wapstack/wsp/WSPHeaders.getHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 078: invokespecial net/rim/device/cldc/io/waphttp/WAPSession.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 07b: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentWapSession Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 07e: bipush 0
      // 07f: istore 10
      // 081: aload 0
      // 082: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 085: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._reuseSecurity Z
      // 088: istore 11
      // 08a: new com/fourthpass/wapstack/bearer/RIM_UDP_Bearer
      // 08d: dup
      // 08e: aload 1
      // 08f: iload 2
      // 090: aload 3
      // 091: iload 4
      // 093: invokespecial com/fourthpass/wapstack/bearer/RIM_UDP_Bearer.<init> (Lcom/fourthpass/wapstack/util/InetAddress;ILjava/lang/String;I)V
      // 096: astore 12
      // 098: aload 0
      // 099: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 09c: aload 12
      // 09e: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._bearer Lcom/fourthpass/wapstack/bearer/RIM_UDP_Bearer;
      // 0a1: aload 13
      // 0a3: monitorexit
      // 0a4: goto 0af
      // 0a7: astore 14
      // 0a9: aload 13
      // 0ab: monitorexit
      // 0ac: aload 14
      // 0ae: athrow
      // 0af: new com/fourthpass/wapstack/wdp/WDPLayer
      // 0b2: dup
      // 0b3: aload 12
      // 0b5: invokespecial com/fourthpass/wapstack/wdp/WDPLayer.<init> (Lcom/fourthpass/wapstack/bearer/IBearer;)V
      // 0b8: astore 13
      // 0ba: aconst_null
      // 0bb: astore 15
      // 0bd: aload 0
      // 0be: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 0c1: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._secureAccess Z
      // 0c4: ifne 0ca
      // 0c7: goto 1d5
      // 0ca: iload 11
      // 0cc: ifeq 0d7
      // 0cf: aload 7
      // 0d1: invokevirtual com/fourthpass/wapstack/util/InetAddress.getAddress ()[B
      // 0d4: goto 0db
      // 0d7: aload 1
      // 0d8: invokevirtual com/fourthpass/wapstack/util/InetAddress.getAddress ()[B
      // 0db: astore 16
      // 0dd: aload 16
      // 0df: bipush 0
      // 0e0: baload
      // 0e1: sipush 255
      // 0e4: iand
      // 0e5: bipush 24
      // 0e7: ishl
      // 0e8: aload 16
      // 0ea: bipush 1
      // 0eb: baload
      // 0ec: sipush 255
      // 0ef: iand
      // 0f0: bipush 16
      // 0f2: ishl
      // 0f3: ior
      // 0f4: aload 16
      // 0f6: bipush 2
      // 0f8: baload
      // 0f9: sipush 255
      // 0fc: iand
      // 0fd: bipush 8
      // 0ff: ishl
      // 100: ior
      // 101: aload 16
      // 103: bipush 3
      // 105: baload
      // 106: sipush 255
      // 109: iand
      // 10a: ior
      // 10b: istore 17
      // 10d: new com/fourthpass/wapstack/wtls/RIMWTLSLayer
      // 110: dup
      // 111: aload 13
      // 113: aload 3
      // 114: iload 11
      // 116: ifeq 121
      // 119: aload 7
      // 11b: invokevirtual com/fourthpass/wapstack/util/InetAddress.toString ()Ljava/lang/String;
      // 11e: goto 125
      // 121: aload 1
      // 122: invokevirtual com/fourthpass/wapstack/util/InetAddress.toString ()Ljava/lang/String;
      // 125: aload 0
      // 126: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 129: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._wtlsMode I
      // 12c: bipush 1
      // 12d: if_icmpne 134
      // 130: bipush 1
      // 131: goto 135
      // 134: bipush 0
      // 135: aload 0
      // 136: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 139: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._wtlsClientIdType I
      // 13c: aload 0
      // 13d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 140: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._wtlsClientIdValue Ljava/lang/String;
      // 143: iload 17
      // 145: iload 11
      // 147: ifeq 14f
      // 14a: iload 8
      // 14c: goto 150
      // 14f: iload 2
      // 150: aload 0
      // 151: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 154: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._wap20Conformance I
      // 157: bipush 1
      // 158: if_icmpne 15f
      // 15b: bipush 1
      // 15c: goto 160
      // 15f: bipush 0
      // 160: invokespecial com/fourthpass/wapstack/wtls/RIMWTLSLayer.<init> (Lcom/fourthpass/wapstack/IWapStackLayer;Ljava/lang/String;Ljava/lang/String;ZILjava/lang/String;IIZ)V
      // 163: astore 15
      // 165: goto 1a3
      // 168: astore 16
      // 16a: aload 13
      // 16c: invokevirtual com/fourthpass/wapstack/wdp/WDPLayer.close ()V
      // 16f: new java/lang/Object
      // 172: dup
      // 173: sipush 1000
      // 176: invokespecial net/rim/device/cldc/io/waphttp/WAPIOException.<init> (I)V
      // 179: athrow
      // 17a: astore 16
      // 17c: aload 13
      // 17e: invokevirtual com/fourthpass/wapstack/wdp/WDPLayer.close ()V
      // 181: new java/lang/Object
      // 184: dup
      // 185: sipush 1007
      // 188: aload 16
      // 18a: invokestatic com/fourthpass/wapstack/wtls/RIMWTLSLayer.getErrorDescription (Lnet/rim/device/cldc/io/ssl/TLSException;)B
      // 18d: invokespecial net/rim/device/cldc/io/waphttp/WAPIOException.<init> (II)V
      // 190: athrow
      // 191: astore 16
      // 193: aload 13
      // 195: invokevirtual com/fourthpass/wapstack/wdp/WDPLayer.close ()V
      // 198: new java/lang/Object
      // 19b: dup
      // 19c: sipush 1004
      // 19f: invokespecial net/rim/device/cldc/io/waphttp/WAPIOException.<init> (I)V
      // 1a2: athrow
      // 1a3: aload 15
      // 1a5: ifnonnull 1b8
      // 1a8: aload 13
      // 1aa: invokevirtual com/fourthpass/wapstack/wdp/WDPLayer.close ()V
      // 1ad: new java/lang/Object
      // 1b0: dup
      // 1b1: sipush 1008
      // 1b4: invokespecial net/rim/device/cldc/io/waphttp/WAPIOException.<init> (I)V
      // 1b7: athrow
      // 1b8: aload 13
      // 1ba: aload 15
      // 1bc: invokevirtual com/fourthpass/wapstack/wdp/WDPLayer.setUserLayer (Lcom/fourthpass/wapstack/IWapStackLayer;)V
      // 1bf: new com/fourthpass/wapstack/wtp/WTPLayer
      // 1c2: dup
      // 1c3: aload 15
      // 1c5: bipush 0
      // 1c6: invokespecial com/fourthpass/wapstack/wtp/WTPLayer.<init> (Lcom/fourthpass/wapstack/IWapStackLayer;Z)V
      // 1c9: astore 14
      // 1cb: aload 15
      // 1cd: aload 14
      // 1cf: invokevirtual com/fourthpass/wapstack/wtls/RIMWTLSLayer.setUserLayer (Lcom/fourthpass/wapstack/IWapStackLayer;)V
      // 1d2: goto 1e8
      // 1d5: new com/fourthpass/wapstack/wtp/WTPLayer
      // 1d8: dup
      // 1d9: aload 13
      // 1db: bipush 0
      // 1dc: invokespecial com/fourthpass/wapstack/wtp/WTPLayer.<init> (Lcom/fourthpass/wapstack/IWapStackLayer;Z)V
      // 1df: astore 14
      // 1e1: aload 13
      // 1e3: aload 14
      // 1e5: invokevirtual com/fourthpass/wapstack/wdp/WDPLayer.setUserLayer (Lcom/fourthpass/wapstack/IWapStackLayer;)V
      // 1e8: new com/fourthpass/wapstack/wsp/WSPAddress
      // 1eb: dup
      // 1ec: aload 12
      // 1ee: invokevirtual com/fourthpass/wapstack/bearer/RIM_UDP_Bearer.getBearerType ()B
      // 1f1: iload 2
      // 1f2: aload 1
      // 1f3: invokespecial com/fourthpass/wapstack/wsp/WSPAddress.<init> (BILcom/fourthpass/wapstack/util/InetAddress;)V
      // 1f6: astore 16
      // 1f8: new com/fourthpass/wapstack/wsp/WSPLayer
      // 1fb: dup
      // 1fc: aload 14
      // 1fe: aload 16
      // 200: invokespecial com/fourthpass/wapstack/wsp/WSPLayer.<init> (Lcom/fourthpass/wapstack/wtp/WTPLayer;Lcom/fourthpass/wapstack/wsp/WSPAddress;)V
      // 203: astore 17
      // 205: aload 14
      // 207: aload 17
      // 209: invokevirtual com/fourthpass/wapstack/wtp/WTPLayer.setUserLayer (Lcom/fourthpass/wapstack/IWapStackLayer;)V
      // 20c: aload 17
      // 20e: aload 0
      // 20f: invokevirtual com/fourthpass/wapstack/wsp/WSPLayer.setUserLayer (Lcom/fourthpass/wapstack/IWapStackLayer;)V
      // 212: aload 17
      // 214: aload 0
      // 215: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._params Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;
      // 218: getfield net/rim/device/cldc/io/waphttp/WAPConnectionParams._secureAccess Z
      // 21b: ifeq 223
      // 21e: bipush 60
      // 220: goto 225
      // 223: bipush 30
      // 225: invokevirtual com/fourthpass/wapstack/wsp/WSPLayer.setSessionTimeout (I)V
      // 228: aload 0
      // 229: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 22c: dup
      // 22d: astore 18
      // 22f: monitorenter
      // 230: aload 0
      // 231: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 234: ifnonnull 241
      // 237: aload 17
      // 239: invokevirtual com/fourthpass/wapstack/wsp/WSPLayer.close ()V
      // 23c: bipush 0
      // 23d: aload 18
      // 23f: monitorexit
      // 240: ireturn
      // 241: new com/fourthpass/wapstack/wsp/WSPAddress
      // 244: dup
      // 245: aload 0
      // 246: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 249: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._bearer Lcom/fourthpass/wapstack/bearer/RIM_UDP_Bearer;
      // 24c: invokevirtual com/fourthpass/wapstack/bearer/RIM_UDP_Bearer.getBearerType ()B
      // 24f: getstatic net/rim/device/cldc/io/waphttp/WAPConnectionImpl.LOCAL_ADDRESS Lcom/fourthpass/wapstack/util/InetAddress;
      // 252: invokespecial com/fourthpass/wapstack/wsp/WSPAddress.<init> (BLcom/fourthpass/wapstack/util/InetAddress;)V
      // 255: astore 19
      // 257: aload 0
      // 258: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 25b: new com/fourthpass/wapstack/wsp/WSPClientSession
      // 25e: dup
      // 25f: aload 17
      // 261: aload 0
      // 262: aload 19
      // 264: aload 16
      // 266: invokespecial com/fourthpass/wapstack/wsp/WSPClientSession.<init> (Lcom/fourthpass/wapstack/wsp/WSPLayer;Lcom/fourthpass/wapstack/IWapStackLayer;Lcom/fourthpass/wapstack/wsp/WSPAddress;Lcom/fourthpass/wapstack/wsp/WSPAddress;)V
      // 269: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 26c: aload 0
      // 26d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 270: aload 14
      // 272: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wtpLayer Lcom/fourthpass/wapstack/wtp/WTPLayer;
      // 275: aload 0
      // 276: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 279: aload 15
      // 27b: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wtlsLayer Lcom/fourthpass/wapstack/wtls/RIMWTLSLayer;
      // 27e: aload 18
      // 280: monitorexit
      // 281: goto 28c
      // 284: astore 20
      // 286: aload 18
      // 288: monitorexit
      // 289: aload 20
      // 28b: athrow
      // 28c: new com/fourthpass/wapstack/wsp/WSPCapabilities
      // 28f: dup
      // 290: bipush 1
      // 291: invokespecial com/fourthpass/wapstack/wsp/WSPCapabilities.<init> (Z)V
      // 294: astore 18
      // 296: bipush 32
      // 298: istore 19
      // 29a: invokestatic net/rim/device/internal/browser/wap/WAPPushProviderRegistry.getInstanceNoWait ()Lnet/rim/device/internal/browser/wap/IWAPPushProvider;
      // 29d: astore 20
      // 29f: aload 20
      // 2a1: ifnull 2b6
      // 2a4: aload 20
      // 2a6: invokeinterface net/rim/device/internal/browser/wap/IWAPPushProvider.pushEnabled ()Z 1
      // 2ab: ifeq 2b6
      // 2ae: iload 19
      // 2b0: sipush 128
      // 2b3: ior
      // 2b4: istore 19
      // 2b6: aload 18
      // 2b8: iload 19
      // 2ba: i2l
      // 2bb: invokevirtual com/fourthpass/wapstack/wsp/WSPCapabilities.setProtocolOptions (J)V
      // 2be: aload 6
      // 2c0: invokevirtual net/rim/device/cldc/io/waphttp/WAPRequestImpl.isAborted ()Z
      // 2c3: ifeq 30d
      // 2c6: aload 0
      // 2c7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 2ca: dup
      // 2cb: astore 21
      // 2cd: monitorenter
      // 2ce: aload 0
      // 2cf: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 2d2: ifnonnull 2da
      // 2d5: bipush 0
      // 2d6: aload 21
      // 2d8: monitorexit
      // 2d9: ireturn
      // 2da: aload 0
      // 2db: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 2de: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 2e1: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.disconnectRegisteredMethods ()V
      // 2e4: aload 0
      // 2e5: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 2e8: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 2eb: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.disconnectRegisteredTransactions ()V
      // 2ee: aload 0
      // 2ef: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 2f2: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 2f5: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.close ()V
      // 2f8: aload 0
      // 2f9: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 2fc: aconst_null
      // 2fd: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 300: aload 21
      // 302: monitorexit
      // 303: bipush 0
      // 304: ireturn
      // 305: astore 22
      // 307: aload 21
      // 309: monitorexit
      // 30a: aload 22
      // 30c: athrow
      // 30d: ldc2_w -2968315138570648581
      // 310: ldc_w 1466065778
      // 313: bipush 5
      // 315: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 318: pop
      // 319: iload 10
      // 31b: ifne 321
      // 31e: goto 42c
      // 321: aload 0
      // 322: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 325: dup
      // 326: astore 22
      // 328: monitorenter
      // 329: aload 0
      // 32a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 32d: ifnonnull 335
      // 330: bipush 0
      // 331: aload 22
      // 333: monitorexit
      // 334: ireturn
      // 335: aload 0
      // 336: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 339: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 33c: aload 0
      // 33d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 340: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentWapSession Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 343: invokevirtual net/rim/device/cldc/io/waphttp/WAPSession.getId ()J
      // 346: aload 5
      // 348: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.invokeResumeCommand (JLcom/fourthpass/wapstack/wsp/WSPHeaders;)Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 34b: astore 21
      // 34d: aload 0
      // 34e: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 351: aload 21
      // 353: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 356: aload 22
      // 358: monitorexit
      // 359: goto 364
      // 35c: astore 23
      // 35e: aload 22
      // 360: monitorexit
      // 361: aload 23
      // 363: athrow
      // 364: aload 21
      // 366: dup
      // 367: astore 22
      // 369: monitorenter
      // 36a: aload 21
      // 36c: invokevirtual com/fourthpass/wapstack/wsp/WSPContextObject.getCompleted ()Z
      // 36f: ifne 37c
      // 372: aload 21
      // 374: invokevirtual java/lang/Object.wait ()V
      // 377: goto 37c
      // 37a: astore 23
      // 37c: aload 22
      // 37e: monitorexit
      // 37f: goto 38a
      // 382: astore 24
      // 384: aload 22
      // 386: monitorexit
      // 387: aload 24
      // 389: athrow
      // 38a: aload 21
      // 38c: invokevirtual com/fourthpass/wapstack/wsp/WSPContextObject.getResult ()Z
      // 38f: ifne 3f6
      // 392: aload 0
      // 393: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 396: dup
      // 397: astore 22
      // 399: monitorenter
      // 39a: aload 0
      // 39b: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 39e: ifnonnull 3a6
      // 3a1: bipush 0
      // 3a2: aload 22
      // 3a4: monitorexit
      // 3a5: ireturn
      // 3a6: aload 0
      // 3a7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 3aa: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 3ad: aload 5
      // 3af: aload 18
      // 3b1: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.invokeConnectCommand (Lcom/fourthpass/wapstack/wsp/WSPHeaders;Lcom/fourthpass/wapstack/wsp/WSPCapabilities;)Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 3b4: astore 21
      // 3b6: aload 0
      // 3b7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 3ba: aload 21
      // 3bc: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 3bf: bipush 0
      // 3c0: istore 10
      // 3c2: aload 22
      // 3c4: monitorexit
      // 3c5: goto 3d0
      // 3c8: astore 25
      // 3ca: aload 22
      // 3cc: monitorexit
      // 3cd: aload 25
      // 3cf: athrow
      // 3d0: aload 21
      // 3d2: dup
      // 3d3: astore 22
      // 3d5: monitorenter
      // 3d6: aload 21
      // 3d8: invokevirtual com/fourthpass/wapstack/wsp/WSPContextObject.getCompleted ()Z
      // 3db: ifne 3e8
      // 3de: aload 21
      // 3e0: invokevirtual java/lang/Object.wait ()V
      // 3e3: goto 3e8
      // 3e6: astore 23
      // 3e8: aload 22
      // 3ea: monitorexit
      // 3eb: goto 3f6
      // 3ee: astore 26
      // 3f0: aload 22
      // 3f2: monitorexit
      // 3f3: aload 26
      // 3f5: athrow
      // 3f6: aload 0
      // 3f7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 3fa: dup
      // 3fb: astore 22
      // 3fd: monitorenter
      // 3fe: aload 0
      // 3ff: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 402: ifnonnull 40a
      // 405: bipush 0
      // 406: aload 22
      // 408: monitorexit
      // 409: ireturn
      // 40a: aload 21
      // 40c: aload 0
      // 40d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 410: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 413: if_acmpne 41e
      // 416: aload 0
      // 417: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 41a: aconst_null
      // 41b: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 41e: aload 22
      // 420: monitorexit
      // 421: goto 4c3
      // 424: astore 27
      // 426: aload 22
      // 428: monitorexit
      // 429: aload 27
      // 42b: athrow
      // 42c: aload 0
      // 42d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 430: dup
      // 431: astore 22
      // 433: monitorenter
      // 434: aload 0
      // 435: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 438: ifnonnull 440
      // 43b: bipush 0
      // 43c: aload 22
      // 43e: monitorexit
      // 43f: ireturn
      // 440: aload 0
      // 441: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 444: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 447: aload 5
      // 449: aload 18
      // 44b: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.invokeConnectCommand (Lcom/fourthpass/wapstack/wsp/WSPHeaders;Lcom/fourthpass/wapstack/wsp/WSPCapabilities;)Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 44e: astore 21
      // 450: aload 0
      // 451: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 454: aload 21
      // 456: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 459: aload 22
      // 45b: monitorexit
      // 45c: goto 467
      // 45f: astore 28
      // 461: aload 22
      // 463: monitorexit
      // 464: aload 28
      // 466: athrow
      // 467: aload 21
      // 469: dup
      // 46a: astore 22
      // 46c: monitorenter
      // 46d: aload 21
      // 46f: invokevirtual com/fourthpass/wapstack/wsp/WSPContextObject.getCompleted ()Z
      // 472: ifne 47f
      // 475: aload 21
      // 477: invokevirtual java/lang/Object.wait ()V
      // 47a: goto 47f
      // 47d: astore 23
      // 47f: aload 22
      // 481: monitorexit
      // 482: goto 48d
      // 485: astore 29
      // 487: aload 22
      // 489: monitorexit
      // 48a: aload 29
      // 48c: athrow
      // 48d: aload 0
      // 48e: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 491: dup
      // 492: astore 22
      // 494: monitorenter
      // 495: aload 0
      // 496: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 499: ifnonnull 4a1
      // 49c: bipush 0
      // 49d: aload 22
      // 49f: monitorexit
      // 4a0: ireturn
      // 4a1: aload 21
      // 4a3: aload 0
      // 4a4: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 4a7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 4aa: if_acmpne 4b5
      // 4ad: aload 0
      // 4ae: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 4b1: aconst_null
      // 4b2: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspContext Lcom/fourthpass/wapstack/wsp/WSPContextObject;
      // 4b5: aload 22
      // 4b7: monitorexit
      // 4b8: goto 4c3
      // 4bb: astore 30
      // 4bd: aload 22
      // 4bf: monitorexit
      // 4c0: aload 30
      // 4c2: athrow
      // 4c3: aload 6
      // 4c5: invokevirtual net/rim/device/cldc/io/waphttp/WAPRequestImpl.getAbortLock ()Ljava/lang/Object;
      // 4c8: dup
      // 4c9: astore 21
      // 4cb: monitorenter
      // 4cc: aload 0
      // 4cd: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._syncObject Ljava/lang/Object;
      // 4d0: dup
      // 4d1: astore 22
      // 4d3: monitorenter
      // 4d4: aload 0
      // 4d5: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 4d8: ifnonnull 4e3
      // 4db: bipush 0
      // 4dc: aload 22
      // 4de: monitorexit
      // 4df: aload 21
      // 4e1: monitorexit
      // 4e2: ireturn
      // 4e3: aload 0
      // 4e4: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 4e7: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 4ea: ifnull 569
      // 4ed: iload 10
      // 4ef: ifne 4ff
      // 4f2: aload 0
      // 4f3: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 4f6: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 4f9: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.ConnectionCnf ()Z
      // 4fc: ifne 511
      // 4ff: iload 10
      // 501: ifeq 569
      // 504: aload 0
      // 505: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 508: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 50b: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.resumeCnf ()Z
      // 50e: ifeq 569
      // 511: aload 0
      // 512: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 515: bipush 1
      // 516: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._suspendSession Z
      // 519: iload 10
      // 51b: ifne 555
      // 51e: aload 0
      // 51f: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 522: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 525: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.getTargetCapabilities ()Lcom/fourthpass/wapstack/wsp/WSPCapabilities;
      // 528: astore 23
      // 52a: aload 23
      // 52c: ifnull 555
      // 52f: aload 23
      // 531: invokevirtual com/fourthpass/wapstack/wsp/WSPCapabilities.getProtocolOptions ()J
      // 534: bipush 32
      // 536: i2l
      // 537: land
      // 538: bipush 0
      // 539: i2l
      // 53a: lcmp
      // 53b: ifne 546
      // 53e: aload 0
      // 53f: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 542: bipush 0
      // 543: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._suspendSession Z
      // 546: aload 0
      // 547: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 54a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._currentWapSession Lnet/rim/device/cldc/io/waphttp/WAPSession;
      // 54d: aload 23
      // 54f: invokevirtual com/fourthpass/wapstack/wsp/WSPCapabilities.getServerSDUSize ()I
      // 552: invokevirtual net/rim/device/cldc/io/waphttp/WAPSession.setMaxServerSDUSize (I)V
      // 555: ldc2_w -2968315138570648581
      // 558: ldc_w 1466135328
      // 55b: bipush 5
      // 55d: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 560: pop
      // 561: bipush 1
      // 562: aload 22
      // 564: monitorexit
      // 565: aload 21
      // 567: monitorexit
      // 568: ireturn
      // 569: aload 17
      // 56b: aconst_null
      // 56c: invokevirtual com/fourthpass/wapstack/wsp/WSPLayer.setUserLayer (Lcom/fourthpass/wapstack/IWapStackLayer;)V
      // 56f: aload 0
      // 570: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 573: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 576: ifnull 5a1
      // 579: aload 0
      // 57a: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 57d: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 580: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.ConnectionFailed ()Z
      // 583: ifeq 597
      // 586: aload 0
      // 587: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 58a: aload 0
      // 58b: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 58e: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 591: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.getLastReplyPDU ()Lcom/fourthpass/wapstack/wsp/pdu/WSP_ReplyPDU;
      // 594: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._connectAbortReply Lcom/fourthpass/wapstack/wsp/pdu/WSP_ReplyPDU;
      // 597: aload 0
      // 598: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 59b: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 59e: invokevirtual com/fourthpass/wapstack/wsp/WSPClientSession.close ()V
      // 5a1: ldc2_w -2968315138570648581
      // 5a4: ldc_w 1466132000
      // 5a7: bipush 5
      // 5a9: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 5ac: pop
      // 5ad: aload 0
      // 5ae: getfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl._currentConfig Lnet/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig;
      // 5b1: aconst_null
      // 5b2: putfield net/rim/device/cldc/io/waphttp/WAPConnectionImpl$CurrentConfig._wspSession Lcom/fourthpass/wapstack/wsp/WSPClientSession;
      // 5b5: bipush 0
      // 5b6: aload 22
      // 5b8: monitorexit
      // 5b9: aload 21
      // 5bb: monitorexit
      // 5bc: ireturn
      // 5bd: astore 31
      // 5bf: aload 22
      // 5c1: monitorexit
      // 5c2: aload 31
      // 5c4: athrow
      // 5c5: astore 32
      // 5c7: aload 21
      // 5c9: monitorexit
      // 5ca: aload 32
      // 5cc: athrow
      // try (15 -> 21): 78 null
      // try (22 -> 77): 78 null
      // try (78 -> 81): 78 null
      // try (95 -> 174): 175 null
      // try (95 -> 174): 183 null
      // try (95 -> 174): 193 null
      // try (266 -> 274): 305 null
      // try (275 -> 304): 305 null
      // try (305 -> 308): 305 null
      // try (340 -> 346): 367 null
      // try (347 -> 365): 367 null
      // try (367 -> 370): 367 null
      // try (385 -> 391): 409 null
      // try (392 -> 408): 409 null
      // try (409 -> 412): 409 null
      // try (421 -> 423): 424 null
      // try (418 -> 427): 428 null
      // try (428 -> 431): 428 null
      // try (441 -> 447): 464 null
      // try (448 -> 463): 464 null
      // try (464 -> 467): 464 null
      // try (476 -> 478): 479 null
      // try (473 -> 482): 483 null
      // try (483 -> 486): 483 null
      // try (493 -> 499): 512 null
      // try (500 -> 511): 512 null
      // try (512 -> 515): 512 null
      // try (522 -> 528): 543 null
      // try (529 -> 542): 543 null
      // try (543 -> 546): 543 null
      // try (555 -> 557): 558 null
      // try (552 -> 561): 562 null
      // try (562 -> 565): 562 null
      // try (572 -> 578): 591 null
      // try (579 -> 590): 591 null
      // try (591 -> 594): 591 null
      // try (606 -> 612): 714 null
      // try (615 -> 673): 714 null
      // try (676 -> 711): 714 null
      // try (714 -> 717): 714 null
      // try (601 -> 614): 719 null
      // try (615 -> 675): 719 null
      // try (676 -> 713): 719 null
      // try (714 -> 722): 719 null
   }

   private final boolean openConnection(HttpHeaders connectHeaders, WAPRequestImpl fetchRequest) {
      boolean connectionMode = this._params._connectionMode;
      if (!connectionMode) {
         synchronized (this._syncObject) {
            if (this._currentConfig == null) {
               return false;
            }

            this._currentConfig._bearer = new RIM_UDP_Bearer(
               new InetAddress(this._params._wapServerAddress), this._params._wapServerPort, this._params._wapServerAPN, this._params._srcPort
            );
            WDPLayer wdpLayer = new WDPLayer(this._currentConfig._bearer);
            this._currentConfig._connectionless = new WSPConnectionless(wdpLayer);
            return true;
         }
      } else {
         synchronized (this._syncObject) {
            if (this._currentConfig == null) {
               throw new Object(1000);
            }

            if (this._currentConfig._wspSession != null) {
               this._currentConfig._wspSession.disconnectRegisteredMethods();
               this._currentConfig._wspSession.disconnectRegisteredTransactions();
               this._currentConfig._wspSession.close();
            } else if (this._currentConfig._wtpLayer != null) {
               this._currentConfig._wtpLayer.close();
            } else if (this._currentConfig._bearer != null) {
               this._currentConfig._bearer.closeConnection();
            }

            this._currentConfig._wspSession = null;
            this._currentConfig._wtpLayer = null;
            this._currentConfig._bearer = null;
         }

         WSPHeaders headers = this.encodeHeaders(connectHeaders, null, true);
         boolean firstTime = true;
         int redirectCount = 0;
         InetAddress originalServerAddress = new InetAddress(this._params._wapServerAddress);
         int originalServerPort = this._params._wapServerPort;
         InetAddress server = originalServerAddress;
         int destPort = originalServerPort;
         String apn = this._params._wapServerAPN;
         int srcPort = this._params._srcPort;

         while (redirectCount < 30) {
            synchronized (this._syncObject) {
               if (this._currentConfig == null) {
                  return false;
               }

               if (!firstTime && this._currentConfig._redirectAddress == null) {
                  break;
               }

               this._currentConfig._redirectAddress = null;
               this._currentConfig._connectAbortReply = null;
            }

            firstTime = false;
            boolean connectionOpened = this.openConnectionModeConnection(
               server, destPort, apn, srcPort, headers, fetchRequest, originalServerAddress, originalServerPort
            );
            if (!connectionOpened) {
               synchronized (this._syncObject) {
                  if (this._currentConfig == null) {
                     return false;
                  }

                  if (this._currentConfig._redirectAddress == null) {
                     if (this._currentConfig._connectAbortReply == null) {
                        throw new Object(1003);
                     }

                     headers = this._currentConfig._connectAbortReply.getHeaderInfo(headers);
                     InputStream resultStream = this._currentConfig._connectAbortReply.getData();
                     WSPHeaderDecoder oldHeaderDecoder = (WSPHeaderDecoder)(new Object(fetchRequest.getResponseHeaders()));
                     oldHeaderDecoder.decode(headers.getAttributeList(), true);
                     fetchRequest.setPipe(((PipeInputStream)resultStream).getCacheableData());
                     fetchRequest.setInputStream(resultStream);
                     int status = 0;
                     int rawStatus = this._currentConfig._connectAbortReply.getStatusCode();
                     if (rawStatus < 80) {
                        status = rawStatus / 16 * 100 + rawStatus % 16;
                     } else if (rawStatus < 96) {
                        status = (rawStatus / 16 - 1) * 100 + rawStatus % 16 + 16;
                     } else {
                        status = (rawStatus / 16 - 1) * 100 + rawStatus % 16;
                     }

                     fetchRequest.setStatus(status);
                     return false;
                  }

                  server = this._currentConfig._redirectAddress.getInetAddress();
                  destPort = this._currentConfig._redirectAddress.getPortNumber();
               }
            }

            redirectCount++;
         }

         if (redirectCount == 30) {
            throw new Object(1002);
         } else {
            return true;
         }
      }
   }

   @Override
   public final void close() {
      this.close(false);
   }

   private final void sendRequestInternal(WAPRequestImpl request) {
      if (request.isAborted()) {
         throw new Object(1000);
      }

      HttpHeaders htHeaders = request.getRequestHeaders2();
      WSPHeaders requestHeaders = this.encodeHeaders(htHeaders, request.getPostData(), false);
      boolean successful = false;

      for (int retryCounter = 0; !successful && retryCounter < 3; retryCounter++) {
         if (this._tunnel == null) {
            this._tunnelStatus = 0;
            TunnelConfig config = (TunnelConfig)(new Object(
               this._params._wapServerAPN, "wap", null, this._params._authUsername, this._params._authPassword, this
            ));
            config.setLingerTimeout(0);
            config.setMaxAttempts(3);
            synchronized (this._syncObject) {
               this._tunnel = TunnelFactory.openTunnel(config);
            }
         } else {
            synchronized (this._tunnelSyncObject) {
               if (this._tunnelStatus != 3) {
                  this._tunnel.kick();
               }
            }
         }

         request.setAbortContext(this._tunnel);
         synchronized (this._tunnelSyncObject) {
            while (this._tunnelStatus != 3) {
               label193:
               try {
                  this._tunnelSyncObject.wait(165000);
               } finally {
                  break label193;
               }

               switch (this._tunnelStatus) {
                  case -1:
                     throw new Object(1000);
                  case 4:
                     throw new Object(1009);
               }
            }
         }

         request.clearAbortContext();
         synchronized (this._tunnelSyncObject) {
            if (this._tunnelStatus != 3) {
               this.close(true);
               return;
            }
         }

         if (request.isAborted()) {
            throw new Object(1000);
         }

         boolean openConnection = false;
         synchronized (this._syncObject) {
            if (this._currentConfig == null) {
               return;
            }

            openConnection = this._currentConfig._wspSession == null || this._newConnectionParameters;
         }

         request.setAbortContext(this);
         if (openConnection && !this.openConnection(htHeaders, request)) {
            request.clearAbortContext();
            return;
         }

         this._newConnectionParameters = false;
         request.clearAbortContext();
         if (request.isAborted()) {
            throw new Object(1000);
         }

         successful = this.getURI(requestHeaders, request);
         if (!successful) {
            this._newConnectionParameters = true;
         }
      }

      if (!successful) {
         throw new Object(1001);
      }
   }

   static {
      EventLogger.register(-2968315138570648581L, "net.rim.wap", 2);
   }
}
