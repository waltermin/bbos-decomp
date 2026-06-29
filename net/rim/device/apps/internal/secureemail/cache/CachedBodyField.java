package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.secureemail.CursorProviderActiveRichTextField;

public class CachedBodyField extends CachedField {
   private String _body;
   private static final int BODY_SPACER_HEIGHT;

   public CachedBodyField(String body) {
      this._body = body;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      manager.add((Field)(new Object(4)));
      manager.add(new CursorProviderActiveRichTextField(this._body));
      manager.add((Field)(new Object(4)));
   }

   @Override
   public void fillStringBuffer(StringBuffer stringBuffer, Object context) {
      stringBuffer.append(this._body);
      stringBuffer.append('\n');
      CachedMessage cachedMessage = this.getCachedMessage();
      if (cachedMessage != null && cachedMessage.isBodyTruncated()) {
         stringBuffer.append('\n');
         stringBuffer.append(EmailResources.getString(1008));
         stringBuffer.append('\n');
      }
   }
}
