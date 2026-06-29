package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.internal.commonmodels.title.TitleModelImpl;

public final class FilterComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      TitleModelImpl title1 = ((FilterModel)o1)._titleModel;
      TitleModelImpl title2 = ((FilterModel)o2)._titleModel;
      return StringUtilities.compareObjectToStringIgnoreCase(title1, title2);
   }

   static {
      RecognizerRepository.getRecognizers(-4904857078378172834L);
   }
}
