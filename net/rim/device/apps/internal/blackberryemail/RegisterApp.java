package net.rim.device.apps.internal.blackberryemail;

import net.rim.device.api.servicebook.TransportRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.internal.blackberryemail.address.PackageManager;
import net.rim.device.apps.internal.blackberryemail.email.EmailOptionsManager;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.device.apps.internal.blackberryemail.properties.MessageServicesCMIMEOptionsProviderFactory;
import net.rim.device.apps.internal.blackberryemail.sendmethods.DefaultSendMethodFactory;
import net.rim.device.apps.internal.blackberryemail.sendmethods.RIMEmailTransport;
import net.rim.device.apps.internal.blackberryemail.sendmethods.SendMethodSelector;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;

public final class RegisterApp {
   public static final void libMain(String[] args) {
      PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.header.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.replywithouttextstub.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.body.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.email.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.email.options.PackageManager.registerOnceOnSystemStart();
      EmailOptionsManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.folder.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.blackberryemail.unknown.PackageManager.registerOnceOnSystemStart();
      SendMethodSelector.getInstance().registerSendMethodFactory(new DefaultSendMethodFactory());
      RIMModelFactoryRepository.addFactory(-1203249910507515082L, new MessageServicesCMIMEOptionsProviderFactory());
      OTAMessageSync.intializeOTAMessageSync();
      TransportRegistry.getInstance().register("CMIME", new RIMEmailTransport());
      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         oac.addDeviceCapabilities((byte)12, new byte[]{1});
         oac.setDeviceCapabilitiesFlag((byte)2, new byte[]{32});
      }
   }
}
