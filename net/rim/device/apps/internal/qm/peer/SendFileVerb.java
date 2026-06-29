package net.rim.device.apps.internal.qm.peer;

import java.io.InputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.io.file.FileUtilities;

final class SendFileVerb extends PeerVerb {
   private PeerContact _lastSelectedContact;
   private static final long GUID = -3003787819375479772L;
   private static final String MIME_TYPE_TEXT = "text/*";
   private static final String MIME_TYPE_TEXT_HTML = "text/html";
   private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
   private static final String MIME_TYPE_VCALENDAR = "text/x-vcalendar";
   private static final String MIME_TYPE_VCARD = "text/x-vcard";
   private static final String MIME_TYPE_WML = "text/vnd.wap.wml";
   private static final String MIME_TYPE_WMLC = "application/vnd.wap.wmlc";
   private static final String MIME_TYPE_IMAGE = "image/*";
   private static final String MIME_TYPE_IMAGE_GIF = "image/gif";
   private static final String MIME_TYPE_IMAGE_JPG = "image/jpg";
   private static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
   private static final String MIME_TYPE_IMAGE_PJPEG = "image/pjpeg";
   private static final String MIME_TYPE_IMAGE_TIFF = "image/tiff";
   private static final String MIME_TYPE_IMAGE_PNG = "image/png";
   private static final String MIME_TYPE_IMAGE_RIM_PNG = "image/vnd.rim.png";
   private static final String MIME_TYPE_IMAGE_BMP = "image/bmp";
   private static final String MIME_TYPE_IMAGE_WBMP = "image/vnd.wap.wbmp";
   private static final String MIME_TYPE_WAP_MULTIPART_MIXED = "application/vnd.wap.multipart.mixed";
   private static final String MIME_TYPE_WAP_MULTIPART_RELATED = "application/vnd.wap.multipart.related";
   private static final String MIME_TYPE_XHTML_XML = "application/xhtml+xml";
   private static final String MIME_TYPE_MMS = "application/vnd.wap.mms-message";
   private static final String MIME_TYPE_WAP_XHTML_XML = "application/vnd.wap.xhtml+xml";
   private static final String MIME_TYPE_DRM_MESSAGE = "application/vnd.oma.drm.message";
   private static final String MIME_TYPE_DRM_CONTENT = "application/vnd.oma.drm.content";
   private static final String MIME_TYPE_DRM_RIGHTS_XML = "application/vnd.oma.drm.rights+xml";
   private static final String MIME_TYPE_SMIL = "application/smil";
   private static final String MIME_TYPE_MID = "audio/mid";
   private static final String MIME_TYPE_MIDI = "audio/midi";
   private static final String MIME_TYPE_X_MIDI = "audio/x-midi";
   private static final String MIME_TYPE_SP_MIDI = "audio/sp-midi";
   private static final String MIME_TYPE_AMR = "audio/amr";
   private static final String MIME_TYPE_PME = "application/x-vnd.rim.pme";
   private static final String MIME_TYPE_PMB = "application/x-vnd.rim.pme.b";

   static final void register() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      Object obj = applicationRegistry.get(-3003787819375479772L);
      if (obj == null) {
         SendFileVerb verb = new SendFileVerb(1267024);
         ApplicationRegistry.getApplicationRegistry().replace(-3003787819375479772L, verb);
         registerWithDefaulRepository(verb);
         MIMEContentVerbRepository.register(new SendFileVerb(16863999), "text/x-vcard");
         FileHandler.regsiter();
         VCardHandler.register();
      }
   }

   private static final void registerWithDefaulRepository(Verb verb) {
      MIMEContentVerbRepository.register(verb, "image/gif");
      MIMEContentVerbRepository.register(verb, "image/jpg");
      MIMEContentVerbRepository.register(verb, "image/jpeg");
      MIMEContentVerbRepository.register(verb, "image/pjpeg");
      MIMEContentVerbRepository.register(verb, "image/tiff");
      MIMEContentVerbRepository.register(verb, "image/png");
      MIMEContentVerbRepository.register(verb, "image/vnd.rim.png");
      MIMEContentVerbRepository.register(verb, "image/vnd.wap.wbmp");
      MIMEContentVerbRepository.register(verb, "audio/mid");
      MIMEContentVerbRepository.register(verb, "audio/midi");
      MIMEContentVerbRepository.register(verb, "audio/x-midi");
      MIMEContentVerbRepository.register(verb, "audio/sp-midi");
   }

   private SendFileVerb(int ordering) {
      super(ordering, 893);
   }

   private final String getFilename(Object context, String contentType) {
      String filename = (String)ContextObject.get(context, -4886909117188079897L);
      if (filename == null) {
         if (contentType.startsWith("image")) {
            return PeerResources.getString(2038);
         }

         if (contentType.startsWith("audio")) {
            filename = PeerResources.getString(2039);
         }
      }

      return filename;
   }

   private final byte[] getData(Object context) {
      byte[] data = null;
      InputStream mimeInputStream = (InputStream)ContextObject.get(context, 5473606008898265655L);
      String path = (String)ContextObject.get(context, 2765042845091913199L);
      if (mimeInputStream != null) {
         try {
            return IOUtilities.streamToBytes(mimeInputStream);
         } finally {
            ;
         }
      } else {
         if (path != null) {
            data = FileUtilities.getData(path);
         }

         return data;
      }
   }

   private final void sendData(byte[] data, String contentType, String filename) {
      PeerContact[] contacts = PeerApplication.getInstance().getContactListCollection().getContacts(0);
      int selectedIndex = this._lastSelectedContact != null ? Arrays.getIndex(contacts, this._lastSelectedContact) : 0;
      SelectContactDialog scd = new SelectContactDialog(contacts, selectedIndex != -1 ? selectedIndex : 0);
      if (scd.doModal()) {
         this._lastSelectedContact = scd.getSelection();
         if (Dialog.ask(4, PeerResources.format(2059, filename, this._lastSelectedContact.getDisplayName()), 0) == 0) {
            PeerApplication.getInstance().sendFile(this._lastSelectedContact, contentType, filename, data);
         }
      }
   }

   @Override
   public final Object invoke(Object context) {
      String contentType = (String)ContextObject.get(context, -4241241545455759532L);
      if (contentType != null) {
         String filename = this.getFilename(context, contentType);
         byte[] data = this.getData(context);
         if (data != null) {
            this.sendData(data, contentType, filename);
         }
      }

      return null;
   }
}
