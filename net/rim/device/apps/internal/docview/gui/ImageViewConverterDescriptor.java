package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;

public final class ImageViewConverterDescriptor implements ConverterDescriptor {
   private static final String IMAGESCREEN_MESSAGE_VALUE;
   private static final String IMAGEFIELD_CONTEXT;

   public final void register() {
      try {
         SerializationManager.registerConverter("image-screen", this);
      } finally {
         return;
      }
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return false;
   }

   @Override
   public final Converter createConverterInstance(String typeString) {
      return StringUtilities.strEqualIgnoreCase(typeString, "image-screen", 1701707776) ? new ImageViewScreenConverter() : null;
   }

   @Override
   public final Object getContext() {
      return "imgfield";
   }

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      return true;
   }
}
