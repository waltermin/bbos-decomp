package net.rim.device.apps.internal.browser.util;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.stack.BSMConstants;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

public final class BSMUtil implements BSMConstants {
   private static SHA1Digest _digest = (SHA1Digest)(new Object());

   private static final byte[] getHash(String string) {
      synchronized (_digest) {
         _digest.reset();
         _digest.update(string.getBytes());
         return _digest.getDigest();
      }
   }

   public static final DataBuffer[] getConnectData(BrowserSession browserSession) {
      if (browserSession == null) {
         return null;
      }

      DataBuffer[] data = new Object[2];
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      RawDataCache rawDataCache = browser.getRawDataCache();
      DataBuffer buffer = getNewBuffer(browserSession, true);
      buffer.write(76);
      buffer.write(3);
      buffer.write(browserSession.getHmac().getBytes());
      buffer.write(0);
      buffer.write(1);
      buffer.write(73);
      buffer.write(74);
      buffer.write(3);
      buffer.write(DeviceInfo.getPlatformVersion().getBytes());
      buffer.write(0);
      buffer.write(1);
      synchronized (rawDataCache) {
         int maxSize = rawDataCache.getMaxCacheSize() * 1024;
         int shortTermCacheSize = rawDataCache.getShortTermCacheSize();
         Enumeration entries = rawDataCache.getShortTermCacheElements();
         writeTagMemFree(buffer, maxSize - shortTermCacheSize);
         buffer.write(1);
         data[0] = buffer;
         buffer = (DataBuffer)(new Object());
         buffer.write(70);
         writeTagCacheSize(buffer, shortTermCacheSize);

         while (entries.hasMoreElements()) {
            CacheNode node = (CacheNode)entries.nextElement();
            if (node != null) {
               CacheResult cacheResult = node.getContents();
               if (cacheResult != null) {
                  HttpHeaders headers = cacheResult.getResponseHeaders();
                  if (headers != null) {
                     buffer.write(136);
                     writeAttributeUrlHash(buffer, node.getUrl());
                     writeAttributeSize(buffer, cacheResult.getSize());
                     writeAttributeEtag(buffer, headers.getPropertyValue("etag"));
                     writeAttributeLastModified(buffer, cacheResult.getLastModified());
                     writeAttributeExpiry(buffer, node.getExpiryDate());
                     buffer.write(1);
                  }
               }
            }
         }

         buffer.write(1);
      }

      buffer.write(1);
      data[1] = buffer;
      return data;
   }

   public static final DataBuffer getClearData(BrowserSession browserSession) {
      if (browserSession == null) {
         return null;
      }

      DataBuffer buffer = getNewBuffer(browserSession, true);
      buffer.write(134);
      buffer.write(13);
      buffer.write(1);
      buffer.write(1);
      return buffer;
   }

   public static final DataBuffer getAddData(BrowserSession browserSession, Vector nodes) {
      if (browserSession != null && nodes != null && nodes.size() != 0) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         RawDataCache rawDataCache = browser.getRawDataCache();
         int transientCacheSize = rawDataCache.getShortTermCacheSize();
         DataBuffer buffer = getNewBuffer(browserSession, true);
         buffer.write(198);
         buffer.write(14);
         buffer.write(1);
         writeTagCacheSize(buffer, transientCacheSize);
         synchronized (nodes) {
            Enumeration entries = nodes.elements();

            while (entries.hasMoreElements()) {
               CacheNode node = (CacheNode)entries.nextElement();
               if (node != null) {
                  buffer.write(136);
                  writeAttributeUrlHash(buffer, node.getUrl());
                  writeAttributeExpiry(buffer, node.getExpiryDate());
                  buffer.write(1);
               }
            }
         }

         buffer.write(1);
         buffer.write(73);
         int maxSize = rawDataCache.getMaxCacheSize() * 1024;
         writeTagMemFree(buffer, maxSize - transientCacheSize);
         buffer.write(1);
         buffer.write(1);
         return buffer;
      } else {
         return null;
      }
   }

   public static final DataBuffer getRemoveData(BrowserSession browserSession, Vector nodes) {
      if (browserSession != null && nodes != null && nodes.size() != 0) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         RawDataCache rawDataCache = browser.getRawDataCache();
         int transientCacheSize = rawDataCache.getShortTermCacheSize();
         DataBuffer buffer = getNewBuffer(browserSession, true);
         buffer.write(198);
         buffer.write(12);
         buffer.write(1);
         writeTagCacheSize(buffer, transientCacheSize);
         synchronized (nodes) {
            Enumeration entries = nodes.elements();

            while (entries.hasMoreElements()) {
               CacheNode node = (CacheNode)entries.nextElement();
               if (node != null) {
                  buffer.write(136);
                  writeAttributeUrlHash(buffer, node.getUrl());
                  buffer.write(1);
               }
            }
         }

         buffer.write(1);
         buffer.write(73);
         int maxSize = rawDataCache.getMaxCacheSize() * 1024;
         writeTagMemFree(buffer, maxSize - transientCacheSize);
         buffer.write(1);
         buffer.write(1);
         return buffer;
      } else {
         return null;
      }
   }

   public static final DataBuffer getDisconnectData(BrowserSession browserSession) {
      if (browserSession == null) {
         return null;
      }

      DataBuffer buffer = getNewBuffer(browserSession, true);
      buffer.write(1);
      return buffer;
   }

   public static final DataBuffer getNewBuffer(BrowserSession browserSession, boolean hasContent) {
      if (browserSession == null) {
         return null;
      }

      DataBuffer buffer = (DataBuffer)(new Object());
      buffer.write(3);
      buffer.write(1);
      buffer.write(3);
      buffer.write(0);
      buffer.write(133 | (hasContent ? 64 : 0));
      buffer.write(5);
      buffer.write(195);
      writeOpaqueInteger(buffer, browserSession.getSessionId());
      buffer.write(1);
      return buffer;
   }

   private static final void writeTagMemFree(DataBuffer buffer, int size) {
      buffer.write(75);
      buffer.write(195);
      writeOpaqueInteger(buffer, size);
      buffer.write(1);
   }

   private static final void writeAttributeUrlHash(DataBuffer buffer, String url) {
      buffer.write(6);
      buffer.write(195);
      byte[] urlHash = getHash(url);
      buffer.writeByteArray(urlHash);
   }

   private static final void writeAttributeSize(DataBuffer buffer, int size) {
      buffer.write(9);
      buffer.write(195);
      writeOpaqueInteger(buffer, size);
   }

   private static final void writeAttributeEtag(DataBuffer buffer, String etag) {
      if (etag != null && etag.length() > 0) {
         buffer.write(7);
         buffer.write(3);
         buffer.write(etag.getBytes());
         buffer.write(0);
      }
   }

   private static final void writeAttributeLastModified(DataBuffer buffer, long lastModified) {
      if (lastModified != 0) {
         buffer.write(10);
         buffer.write(195);
         writeOpaqueLong(buffer, lastModified / 1000);
      }
   }

   private static final void writeAttributeExpiry(DataBuffer buffer, long expiry) {
      buffer.write(8);
      buffer.write(195);
      writeOpaqueLong(buffer, expiry / 1000);
   }

   private static final void writeTagCacheSize(DataBuffer buffer, int size) {
      buffer.write(71);
      buffer.write(195);
      writeOpaqueInteger(buffer, size);
      buffer.write(1);
   }

   private static final void writeOpaqueInteger(DataBuffer buffer, int value) {
      if (value >= 0 && value <= 255) {
         buffer.writeCompressedInt(1);
         buffer.write(value & 0xFF);
      } else if (value >= 256 && value <= 65535) {
         buffer.writeCompressedInt(2);
         buffer.write(value >> 8 & 0xFF);
         buffer.write(value & 0xFF);
      } else if (value >= 65536 && value <= 16777215) {
         buffer.writeCompressedInt(3);
         buffer.write(value >> 16 & 0xFF);
         buffer.write(value >> 8 & 0xFF);
         buffer.write(value & 0xFF);
      } else {
         if (value < 0 || value >= 16777216) {
            buffer.writeCompressedInt(4);
            buffer.write(value >> 24 & 0xFF);
            buffer.write(value >> 16 & 0xFF);
            buffer.write(value >> 8 & 0xFF);
            buffer.write(value & 0xFF);
         }
      }
   }

   private static final void writeOpaqueLong(DataBuffer buffer, long value) {
      int size = 0;
      if (value >= 0 && value <= 255) {
         size = 1;
      } else if (value >= 256 && value <= 65535) {
         size = 2;
      } else if (value >= 65536 && value <= 16777215) {
         size = 3;
      } else if (value >= 16777216 && value <= 4294967295L) {
         size = 4;
      } else if (value >= 4294967296L && value <= 1099511627775L) {
         size = 5;
      } else if (value >= 1099511627776L && value <= 281474976710655L) {
         size = 6;
      } else if (value >= 281474976710656L && value <= 72057594037927935L) {
         size = 7;
      } else if (value < 0 && value <= Long.MAX_VALUE) {
         size = 8;
      }

      buffer.writeCompressedInt(size);
      switch (size) {
         case 8:
         default:
            buffer.write((int)(value >> 56 & 255));
         case 7:
            buffer.write((int)(value >> 48 & 255));
         case 6:
            buffer.write((int)(value >> 40 & 255));
         case 5:
            buffer.write((int)(value >> 32 & 255));
         case 4:
            buffer.write((int)(value >> 24 & 255));
         case 3:
            buffer.write((int)(value >> 16 & 255));
         case 2:
            buffer.write((int)(value >> 8 & 255));
         case 1:
            buffer.write((int)(value & 255));
         case 0:
      }
   }
}
