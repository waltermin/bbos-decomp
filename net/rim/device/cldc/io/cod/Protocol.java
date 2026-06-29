package net.rim.device.cldc.io.cod;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.compress.GZIPInputStream;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.Array;

public final class Protocol implements ConnectionBaseInterface, HttpConnection {
   private Pipe _pipe;
   private String _type;
   private String _file;
   private String _name;
   private int _responseCode;
   private boolean _gzipped;

   @Override
   public final void close() {
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      ControlledAccess.verifyRRISignatures(true);
      int semicolon = name.indexOf(59);
      if (semicolon != -1) {
         name = name.substring(0, semicolon);
      }

      this._name = name;
      StringTokenizer tokenizer = new StringTokenizer(name, "/");
      String module = tokenizer.nextToken();
      this._file = tokenizer.nextToken(";?#");
      if (module == null || this._file == null || this._file.equals("/")) {
         this._responseCode = 404;
         this._pipe = new Pipe(new byte[0], 0, false);
         return this;
      }

      if (module == null) {
         this._type = "text/html";
         StringBuffer page = new StringBuffer("<html><body>");
         int[] handles = CodeModuleManager.getModuleHandles();
         int count = handles.length;

         for (int i = 0; i < count; i++) {
            String codeModule = CodeModuleManager.getModuleName(handles[i]);
            page.append("<a href='").append(codeModule).append("'>").append(codeModule).append("</a><br/>");
         }

         page.append("</body></html>");
         byte[] data = page.toString().getBytes();
         this._pipe = new Pipe(data, data.length, false);
         return this;
      } else if (this._file != null && !this._file.equals("/")) {
         if (this._file.startsWith("/")) {
            this._file = this._file.substring(1);
         }

         Resource resource = Resource$Internal.getResourceClass(module);
         if (resource != null) {
            byte[][] data = this.getResourceSegments(module, this._file);
            if (data == null) {
               data = this.getResourceSegments(module, this._file + ".gz");
               this._gzipped = data != null;
            }

            if (data != null) {
               this._pipe = new Pipe(data, false);
            } else {
               byte[] singleArrayData = resource.getResource(this._file);
               if (singleArrayData != null) {
                  this._pipe = new Pipe(singleArrayData, singleArrayData.length, false);
               } else {
                  singleArrayData = resource.getResource(this._file + ".gz");
                  if (singleArrayData != null) {
                     this._pipe = new Pipe(singleArrayData, singleArrayData.length, false);
                     this._gzipped = true;
                  }
               }
            }
         }

         if (this._pipe == null) {
            this._responseCode = 404;
            this._pipe = new Pipe(new byte[0], 0, false);
            return this;
         } else {
            int dot = this._file.lastIndexOf(46);
            if (dot == -1) {
               this._responseCode = 400;
               return this;
            } else {
               this._type = MIMETypeAssociations.getMIMEType(this._file);
               this._responseCode = 200;
               return this;
            }
         }
      } else {
         this._type = "text/html";
         StringBuffer page = new StringBuffer("<html><body>");
         Resource resources = Resource$Internal.getResourceClass(module);
         Enumeration enumeration = resources.getResourceKeys();
         if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
               String resource = (String)enumeration.nextElement();
               int dot = resource.lastIndexOf(46);
               if (dot != -1) {
                  String type = MIMETypeAssociations.getMIMEType(resource);
                  if (type != null) {
                     page.append("<a href='").append(resource).append("'>").append(resource).append("</a><br/>");
                  }
               }
            }
         }

         page.append("</body></html>");
         byte[] data = page.toString().getBytes();
         this._pipe = new Pipe(data, data.length, false);
         return this;
      }
   }

   @Override
   public final String getType() {
      return this._type;
   }

   @Override
   public final String getEncoding() {
      return null;
   }

   @Override
   public final long getLength() {
      return this._gzipped ? -1 : this._pipe.getLength();
   }

   @Override
   public final InputStream openInputStream() {
      InputStream is = this._pipe.getInputStream();
      return this._gzipped ? new GZIPInputStream(is) : is;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public final OutputStream openOutputStream() throws IOException {
      throw new IOException("Read only");
   }

   @Override
   public final DataOutputStream openDataOutputStream() throws IOException {
      throw new IOException("Read only");
   }

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final String getURL() {
      return "cod:" + this._name;
   }

   @Override
   public final String getProtocol() {
      return "cod";
   }

   @Override
   public final String getHost() {
      return "";
   }

   @Override
   public final String getFile() {
      return this._file;
   }

   @Override
   public final String getRef() {
      int fragmentIdentifier = this._name.indexOf(35);
      return fragmentIdentifier != -1 ? this._name.substring(fragmentIdentifier + 1) : null;
   }

   @Override
   public final String getQuery() {
      return null;
   }

   @Override
   public final int getPort() {
      return 80;
   }

   @Override
   public final String getRequestMethod() {
      return "GET";
   }

   @Override
   public final void setRequestMethod(String method) {
   }

   @Override
   public final String getRequestProperty(String key) {
      return null;
   }

   @Override
   public final void setRequestProperty(String key, String value) {
   }

   @Override
   public final int getResponseCode() {
      return this._responseCode;
   }

   @Override
   public final String getResponseMessage() {
      return this._responseCode == 200 ? "OK" : "Error";
   }

   @Override
   public final long getExpiration() {
      return 0;
   }

   @Override
   public final long getDate() {
      return 0;
   }

   @Override
   public final long getLastModified() {
      return 0;
   }

   @Override
   public final String getHeaderField(String name) {
      return null;
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      return def;
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      return def;
   }

   @Override
   public final String getHeaderField(int n) {
      return n == 0 ? this._type : null;
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return n == 0 && this._type != null ? "Content-Type" : null;
   }

   private final byte[][] getResourceSegments(String module, String name) {
      int moduleHandle = CodeModuleManager.getModuleHandle(module);
      byte[][] result = new byte[0][];
      int offset = 0;
      StringBuffer nameBuffer = new StringBuffer();

      while (true) {
         nameBuffer.setLength(0);
         nameBuffer.append('_').append('_').append(name).append('@').append(offset);
         byte[] more = Resource$Internal.getResource(nameBuffer.toString(), moduleHandle);
         if (more == null) {
            return result.length > 0 ? result : (byte[][])null;
         }

         Array.resize(result, result.length + 1);
         result[result.length - 1] = more;
         offset += more.length;
      }
   }
}
