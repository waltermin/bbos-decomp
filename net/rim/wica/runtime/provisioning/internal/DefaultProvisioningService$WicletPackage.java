package net.rim.wica.runtime.provisioning.internal;

import net.rim.device.api.io.IOUtilities;
import net.rim.wica.packaging.PackageUtilities;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.script.ScriptCompiler;
import net.rim.wica.runtime.util.zip.ZipEntry;
import net.rim.wica.runtime.util.zip.ZipFile;

final class DefaultProvisioningService$WicletPackage {
   boolean _isBinary;
   ZipFile _zipFile;
   private Resource _compiledScript;
   byte[] _wicletDefinition;

   public DefaultProvisioningService$WicletPackage(ZipFile zipFile) {
      this._zipFile = zipFile;
      this._isBinary = true;
      this.extract();
   }

   private final void extract() {
      ZipEntry entry = this._zipFile.getEntry("wiclet.wbxml");
      if (entry == null) {
         entry = this._zipFile.getEntry(DefaultProvisioningService.PLAINTEXT_WICLET_XML_FILENAME);
         this._isBinary = false;
      }

      if (entry == null) {
         throw new Object("Application definition not found in package.");
      }

      this._wicletDefinition = this.getEntryContents(entry);
      entry = this._zipFile.getEntry("wiclet.code");
      if (entry != null) {
         byte[] scriptCode = this.getEntryContents(entry);
         this._compiledScript = new Resource("wiclet.code", ScriptCompiler.compile(scriptCode));
         this._compiledScript.setContentType("application/js");
      }
   }

   private final Resource getScriptResource() {
      return this._compiledScript;
   }

   private final Resource getImmediateResource(String url, String language) {
      Resource resource = null;
      byte[] data = null;
      if (language != null) {
         data = this.getZipEntry(PackageUtilities.constructResourcePath(url, '/', language));
         if (data == null) {
            data = this.getZipEntry(PackageUtilities.constructResourcePath(url, '\\', language));
         }
      }

      if (data == null) {
         data = this.getZipEntry(PackageUtilities.constructResourcePath(url, '/', null));
         if (data == null) {
            data = this.getZipEntry(PackageUtilities.constructResourcePath(url, '\\', null));
         }
      }

      if (data != null) {
         resource = new Resource(url, data);
      }

      return resource;
   }

   private final byte[] getEntryContents(ZipEntry entry) {
      if (entry != null) {
         try {
            return IOUtilities.streamToBytes(this._zipFile.getInputStream(entry));
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   private final byte[] getZipEntry(String entryName) {
      ZipEntry entry = this._zipFile.getEntry(entryName);
      return entry != null ? this.getEntryContents(entry) : null;
   }

   private final byte[] getWicletDefinition() {
      return this._wicletDefinition;
   }

   private final boolean hasBinaryWicletDefinition() {
      return this._isBinary;
   }
}
