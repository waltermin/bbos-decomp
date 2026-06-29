package net.rim.device.cldc.io.proxyhttp.compression;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.proxyhttp.ClientProtocol;
import net.rim.device.cldc.io.proxyhttp.Protocol;

public final class HttpCompressionManager {
   private final Object _loadLock = new Object();
   private Hashtable _uidToMessageEncoder = (Hashtable)(new Object(5));
   private IntHashtable _messageEncoderCache = (IntHashtable)(new Object(3));
   private static final String STRING_codebookURLPrefix;
   private static final String STRING_codebookURLPostfix;
   private static final String STRING_codebookXslURL;
   private static final String STRING_xRimContentTranscoder;
   private static final String STRING_xRimXsltUrl;
   private static final String STRING_httpAccept;
   private static final String STRING_xRimForcedHttpCompression;
   private static final String STRING_transformedCodebookMIMEType;
   private static final String STRING_xmlTranscoder;
   public static final int DEFAULT_COMPRESSION_VERSION;
   private static final MessageEncoder DEFAULT_MESSAGE_ENCODER = new DefaultMessageEncoder();
   private static final HttpCompressionManager INSTANCE = new HttpCompressionManager();

   protected HttpCompressionManager() {
   }

   public final MessageEncoder getMessageEncoderFor(String uid) {
      if (uid == null) {
         return DEFAULT_MESSAGE_ENCODER;
      }

      MessageEncoder me = (MessageEncoder)this._uidToMessageEncoder.get(uid);
      if (me == null) {
         me = DEFAULT_MESSAGE_ENCODER;
         this._uidToMessageEncoder.put(uid, me);
      }

      return me;
   }

   public final MessageEncoder getDefaultMessageEncoder() {
      return DEFAULT_MESSAGE_ENCODER;
   }

   public final MessageEncoder setCompressionVersionFor(String uid, int newVersion) {
      if (uid == null) {
         return DEFAULT_MESSAGE_ENCODER;
      }

      MessageEncoder me = (MessageEncoder)this._uidToMessageEncoder.get(uid);
      if (me == null || me.getVersion() != newVersion) {
         if (newVersion == 16) {
            me = DEFAULT_MESSAGE_ENCODER;
         } else {
            me = (MessageEncoder)this._messageEncoderCache.get(newVersion);
            if (me == null) {
               me = this.loadCodebook(newVersion);
            }
         }

         if (me != null) {
            this._uidToMessageEncoder.put(uid, me);
         }
      }

      return me;
   }

   public final synchronized void setDefaultCompressionVersionFor(String uid) {
      if (uid != null) {
         this._uidToMessageEncoder.put(uid, DEFAULT_MESSAGE_ENCODER);
      }
   }

   public static final HttpCompressionManager getInstance() {
      return INSTANCE;
   }

   public final void reset() {
      this._messageEncoderCache.clear();
      this._uidToMessageEncoder.clear();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final MessageEncoder loadCodebook(int version) {
      MessageEncoder messageEncoder = null;
      synchronized (this._loadLock) {
         messageEncoder = (MessageEncoder)this._messageEncoderCache.get(version);
         if (messageEncoder != null) {
            return messageEncoder;
         }

         int majorVersion = version >> 4;
         int minorVersion = version & 15;
         StringBuffer codebookURL = (StringBuffer)(new Object(55));
         codebookURL.append("www.blackberry.net/go/mobile/mds/http/codebook_").append(majorVersion).append('.');
         codebookURL.append(minorVersion).append(".xml");
         Protocol protocol = new Protocol();
         ClientProtocol conn = null;
         boolean var37 = false /* VF: Semaphore variable */;
         boolean var44 = false /* VF: Semaphore variable */;

         label231: {
            try {
               label210:
               try {
                  var44 = true;
                  var37 = true;
                  conn = (ClientProtocol)protocol.openPrim(codebookURL.toString(), 1, true);
                  conn.setRequestProperty("Content-Transcoder", "vnd.rim.xml");
                  conn.setRequestProperty("x-rim-xsl-url", "http://www.blackberry.net/go/mobile/mds/http/codebook2prop.xsl");
                  conn.setRequestProperty("Accept", "text/plain");
                  conn.setRequestProperty("x-rim-forced-http-compression", Integer.toString(16));
                  InputStream ioe = conn.openInputStream();
                  if (conn.getResponseCode() == 200) {
                     messageEncoder = new GenericMessageEncoder(version);
                     ((GenericMessageEncoder)messageEncoder).initialize(ioe);
                     this._messageEncoderCache.put(version, messageEncoder);
                     var37 = false;
                     var44 = false;
                  } else {
                     var37 = false;
                     var44 = false;
                  }
                  break label231;
               } finally {
                  if (var44) {
                     messageEncoder = null;
                     var37 = false;
                     break label210;
                  }
               }
            } finally {
               if (var37) {
                  if (conn != null) {
                     label196:
                     try {
                        conn.close();
                        ClientProtocol var52 = null;
                     } finally {
                        break label196;
                     }
                  }
               }
            }

            if (conn != null) {
               try {
                  conn.close();
                  ClientProtocol var53 = null;
               } finally {
                  return messageEncoder;
               }
            }

            return messageEncoder;
         }

         if (conn != null) {
            try {
               conn.close();
               ClientProtocol var54 = null;
            } finally {
               return messageEncoder;
            }
         }

         return messageEncoder;
      }
   }
}
