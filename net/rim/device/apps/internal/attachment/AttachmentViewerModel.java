package net.rim.device.apps.internal.attachment;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;

public class AttachmentViewerModel extends UnknownMimePartModel implements PersistableRIMModel, ConversionProvider, FieldProvider, MorePartModel {
   public byte[][][] _conversionsAvailable;
   public static final long ATTACHMENT_VIEWER_MODEL = -811824568997914181L;

   public AttachmentViewerModel(Object initialData) {
      super(initialData);
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      Object parametersObject = contextObject.get(-7353832199068708928L);
      if (parametersObject instanceof Object) {
         Parameters parameters = (Parameters)parametersObject;
         this._conversionsAvailable = (byte[][][])parameters.get((byte)-10);
      }

      if (this._conversionsAvailable == null) {
         this._conversionsAvailable = new byte[0][0][];
      }
   }

   @Override
   protected void writeCMIMEParameters(CMIMEParameters parameters) {
      super.writeCMIMEParameters(parameters);
      int length = this._conversionsAvailable.length;

      for (int i = 0; i < length; i++) {
         parameters.add((byte)-10, (byte[])this._conversionsAvailable[i]);
      }
   }

   public int getPreferredConversion() {
      return getPreferredConversion(this._conversionsAvailable);
   }

   public static int getPreferredConversion(byte[][][] conversionsAvailable) {
      if (conversionsAvailable == null) {
         return -1;
      }

      String[] stringConversionsAvailable = new Object[conversionsAvailable.length];

      for (int i = 0; i < conversionsAvailable.length; i++) {
         stringConversionsAvailable[i] = CMIMEContentType.getFullType((byte[])conversionsAvailable[i]);
      }

      return CMIMEConverterRegistry.getPreferredConversion(stringConversionsAvailable);
   }

   @Override
   public boolean isMoreAvailable() {
      int preferredConvertersion = getPreferredConversion(this._conversionsAvailable);
      return preferredConvertersion >= 0 ? super.isMoreAvailable() : false;
   }

   @Override
   public boolean suppressNotification() {
      return true;
   }

   @Override
   public boolean equals(Object object) {
      return this == object;
   }
}
