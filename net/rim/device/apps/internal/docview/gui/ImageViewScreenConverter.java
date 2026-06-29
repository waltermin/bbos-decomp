package net.rim.device.apps.internal.docview.gui;

import java.io.DataInput;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.utility.serialization.BaseConverter;

final class ImageViewScreenConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) {
      return !(contextObject instanceof IntHashtable) ? null : new DocViewImageDisplayScreen((IntHashtable)contextObject);
   }
}
