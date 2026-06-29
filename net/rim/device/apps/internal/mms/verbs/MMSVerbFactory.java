package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.internal.mms.MMSPolicy;
import net.rim.device.apps.internal.mms.MMSPolicy$ChangeListener;

public final class MMSVerbFactory implements MMSPolicy$ChangeListener {
   private MMSComposeVerb _composeVerb = new TemplateComposeVerb();
   private MMSUseOnceVerb _useOnceVerb = new MMSUseOnceVerb(this._composeVerb);
   private MMSComposeVerb _sendAsVerb = new MMSComposeVerb(0, 1267040, 48);
   private MMSForwardVerb _forwardAsVerb = new MMSForwardVerb(true);
   private MMSManualDialVerb _manualDialVerb = new MMSManualDialVerb(this._composeVerb);
   private MMSSendVCalVerb _sendVCalVerb = new MMSSendVCalVerb();
   private static final boolean ENABLE_COMPOSE_VERBS;
   private static final boolean ENABLE_SEND_AS_VERBS;

   public static final void registerOnceOnSystemStart() {
      TemplateComposeVerb.registerTemplateAlias();
      MMSPolicy.addListener(new MMSVerbFactory());
      VerbCombinerRepository.addCombiner(-1018434224218757332L, new MMSComposeVerbCombiner());
   }

   private MMSVerbFactory() {
   }

   @Override
   public final void onPolicyEnabled() {
      VerbRepository.getVerbRepository(-7881764549058890736L).register(this._composeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-7881764549058890736L).register(this._composeVerb, -2985347935260258684L);
      VerbRepository.getVerbRepository(-6761056765378641298L).register(this._composeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(8016149483483360697L).register(this._useOnceVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-5389783330697330291L).register(this._manualDialVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-110058785485458643L).register(this._forwardAsVerb, 3797587162219887872L);
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/gif");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/jpg");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/jpeg");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/pjpeg");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/tiff");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/png");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/vnd.rim.png");
      MIMEContentVerbRepository.register(this._sendAsVerb, "image/vnd.wap.wbmp");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/mpeg");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/mp3");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/x-mpeg");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/mid");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/midi");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/x-mid");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/x-midi");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/x-wav");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/wav");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/amr");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/mp4");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/aac");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/3gpp");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/3gpp2");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/x-gsm");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/basic");
      MIMEContentVerbRepository.register(this._sendAsVerb, "audio/qcelp");
      MIMEContentVerbRepository.register(this._sendAsVerb, "video/3gpp");
      MIMEContentVerbRepository.register(this._sendAsVerb, "video/3gpp2");
      MIMEContentVerbRepository.register(this._sendAsVerb, "video/mp4");
      MIMEContentVerbRepository.register(this._sendAsVerb, "video/x-ms-wmv");
      MIMEContentVerbRepository.register(this._sendAsVerb, "video/quicktime");
      MIMEContentVerbRepository.register(this._sendAsVerb, "video/x-msvideo");
      MIMEContentVerbRepository.register(this._sendVCalVerb, "text/x-vcalendar");
      MIMEContentVerbRepository.register(this._sendVCalVerb, "text/calendar");
   }

   @Override
   public final void onPolicyDisabled() {
      VerbRepository.getVerbRepository(-7881764549058890736L).deregister(this._composeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-7881764549058890736L).deregister(this._composeVerb, -2985347935260258684L);
      VerbRepository.getVerbRepository(-6761056765378641298L).deregister(this._composeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(8016149483483360697L).deregister(this._useOnceVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-5389783330697330291L).deregister(this._manualDialVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-110058785485458643L).deregister(this._forwardAsVerb, 3797587162219887872L);
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/gif");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/jpg");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/jpeg");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/pjpeg");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/tiff");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/png");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/vnd.rim.png");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "image/vnd.wap.wbmp");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/mpeg");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/mp3");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/x-mpeg");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/mid");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/midi");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/x-mid");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/x-midi");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/sp-midi");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/x-wav");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/wav");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/amr");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/mp4");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/aac");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/3gpp");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/x-gsm");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/basic");
      MIMEContentVerbRepository.deregister(this._sendAsVerb, "audio/qcelp");
      MIMEContentVerbRepository.deregister(this._sendVCalVerb, "text/x-vcalendar");
      MIMEContentVerbRepository.deregister(this._sendVCalVerb, "text/calendar");
   }
}
