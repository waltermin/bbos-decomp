package net.rim.device.internal.media;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.RIMConnector;
import net.rim.device.internal.io.file.FileHandleProvider;
import net.rim.vm.TraceBack;

public class FileDataSource extends DataSourceImpl implements SourceInformationProvider {
   private FileConnection _connection;

   public void close() {
      try {
         this.stop();
      } catch (IOException var2) {
      }
   }

   @Override
   public int getFileHandle() {
      if (super._is instanceof FileHandleProvider) {
         FileHandleProvider fhp = (FileHandleProvider)super._is;
         if (!fhp.isInputCiphering()) {
            return fhp.getFileHandle();
         }
      }

      return -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void connect() throws IOException {
      if (!super._connected) {
         if (super._locator == null) {
            throw new IOException("Locator is null");
         }

         String protocol = "";
         int index = super._locator.indexOf(58);
         if (index == -1) {
            throw new IOException("Invalid URL");
         }

         protocol = super._locator.substring(0, index);
         if (!StringUtilities.strEqualIgnoreCase(protocol, "file", 1701707776)) {
            throw new IOException("Not a File locator");
         }

         boolean isValidFile = false;
         boolean securityException = false;
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            var13 = true;
            int e = TraceBack.getCallingModule(2);
            this._connection = (FileConnection)RIMConnector.open(e, super._locator);
            isValidFile = this._connection.exists()
               && this._connection.canRead()
               && !this._connection.isDirectory()
               && (super._contentType = MIMETypeAssociations.getMIMEType(super._locator)) != null;
            var13 = false;
         } catch (SecurityException e) {
            securityException = true;
            throw e;
         } catch (Exception e) {
            throw new IOException(e.getMessage());
         } finally {
            if (var13) {
               if (!securityException) {
                  if (!isValidFile) {
                     if (this._connection != null) {
                        try {
                           this._connection.close();
                        } catch (IOException var14) {
                        }
                     }

                     throw new IOException("Locator does not reference a valid media file");
                  }

                  super._contentType = StringUtilities.toLowerCase(super._contentType, 1701707776);
                  super._contentLength = this._connection.fileSize();
                  super._seekType = 2;
               }
            }
         }

         if (!securityException) {
            if (!isValidFile) {
               if (this._connection != null) {
                  try {
                     this._connection.close();
                  } catch (IOException var15) {
                  }
               }

               throw new IOException("Locator does not reference a valid media file");
            }

            super._contentType = StringUtilities.toLowerCase(super._contentType, 1701707776);
            super._contentLength = this._connection.fileSize();
            super._seekType = 2;
         }

         super._connected = true;
      }
   }

   @Override
   public void disconnect() {
      if (super._connected) {
         if (super._started) {
            try {
               this.stop();
            } catch (IOException var3) {
            }
         }

         try {
            if (this._connection != null) {
               this._connection.close();
            }
         } catch (IOException var2) {
         }

         super._connected = false;
      }
   }

   @Override
   public void start() throws IOException {
      if (super._connected) {
         if (!super._started) {
            if (this._connection != null) {
               super._is = this._connection.openInputStream();
               super._started = true;
            } else {
               throw new IOException("Connection is null");
            }
         }
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   public FileDataSource(InputStream stream, String contentType, long contentLength) {
      super(null);
      this.setInputStream(stream);
      if (contentType != null) {
         super._contentType = StringUtilities.toLowerCase(contentType, 1701707776);
      }

      super._contentLength = contentLength;
      super._started = true;
      super._connected = true;
   }

   public FileDataSource(String locator) {
      super(locator);
   }
}
