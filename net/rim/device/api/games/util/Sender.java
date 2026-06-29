package net.rim.device.api.games.util;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.util.UAProf;
import net.rim.device.api.browser.util.UserAgent;

final class Sender implements Runnable {
   private byte[] data;
   private int len;
   private String url;
   private HttpConnection conn = null;
   private HighScoreServerListener listener;
   private int status;

   public final int getStatus() {
      return this.status;
   }

   public final String getData() {
      return new String(this.data);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      InputStream s = null;
      boolean tryagain = false;

      try {
         this.conn = (HttpConnection)Connector.open(this.url + ";connectionType=mds-public;deviceside=false");
         this.conn.setRequestProperty("profile", UAProf.getDefaultUAProfURI());
         this.conn.setRequestProperty("user-agent", UserAgent.getDefaultUserAgent());
         s = this.conn.openInputStream();
         this.len = s.read(this.data);
      } finally {
         ;
      }

      if (tryagain) {
         tryagain = false;

         try {
            this.conn = (HttpConnection)Connector.open(this.url);
            this.conn.setRequestProperty("profile", UAProf.getDefaultUAProfURI());
            this.conn.setRequestProperty("user-agent", UserAgent.getDefaultUserAgent());
            s = this.conn.openInputStream();
            this.len = s.read(this.data);
         } finally {
            ;
         }
      }

      if (tryagain) {
         boolean var12 = false /* VF: Semaphore variable */;

         label169:
         try {
            var12 = true;
            this.conn = (HttpConnection)Connector.open(this.url + ";deviceside=true");
            this.conn.setRequestProperty("profile", UAProf.getDefaultUAProfURI());
            this.conn.setRequestProperty("user-agent", UserAgent.getDefaultUserAgent());
            s = this.conn.openInputStream();
            this.len = s.read(this.data);
            var12 = false;
         } finally {
            if (var12) {
               this.status = -1;
               this.data = null;
               if (this.listener != null) {
                  this.listener.serverReply(null);
               }
               break label169;
            }
         }
      }

      label163:
      try {
         if (s != null) {
            s.close();
         }

         if (this.conn != null) {
            this.conn.close();
         }
      } finally {
         break label163;
      }

      if (this.data != null && this.listener != null) {
         this.listener.serverReply(new String(this.data, 0, this.len));
      }
   }

   Sender(String u, HighScoreServerListener l) {
      this.len = 0;
      this.data = new byte[256];
      this.url = u;
      this.listener = l;
   }
}
