package net.rim.blackberry.api.menuitem;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;

public final class ApplicationMenuItemRepository {
   private LongHashtable _applicationSpecificProcessing = (LongHashtable)(new Object());
   private static final long ID;
   public static final long MENUITEM_EMAIL_VIEW;
   public static final long MENUITEM_EMAIL_EDIT;
   public static final long MENUITEM_SMS_VIEW;
   public static final long MENUITEM_SMS_EDIT;
   public static final long MENUITEM_MMS_VIEW;
   public static final long MENUITEM_MMS_EDIT;
   public static final long MENUITEM_ADDRESSBOOK_LIST;
   public static final long MENUITEM_ADDRESSCARD_EDIT;
   public static final long MENUITEM_ADDRESSCARD_VIEW;
   public static final long MENUITEM_CALENDAR;
   public static final long MENUITEM_CALENDAR_EVENT;
   public static final long MENUITEM_MESSAGE_LIST;
   public static final long MENUITEM_TASK_LIST;
   public static final long MENUITEM_TASK_EDIT;
   public static final long MENUITEM_MEMO_LIST;
   public static final long MENUITEM_MEMO_EDIT;
   public static final long MENUITEM_MEMO_VIEW;
   public static final long MENUITEM_PHONELOG_VIEW;
   public static final long MENUITEM_PHONE;
   public static final long MENUITEM_ALARM;
   public static final long MENUITEM_SEARCH;
   public static final long MENUITEM_GROUPADDRESS_EDIT;
   public static final long MENUITEM_GROUPADDRESS_VIEW;
   public static final long MENUITEM_BROWSER;
   public static final long MENUITEM_FILE_EXPLORER;
   public static final long MENUITEM_FILE_EXPLORER_BROWSE;
   public static final long MENUITEM_FILE_EXPLORER_ITEM;
   public static final long MENUITEM_MAPS;
   public static final long MENUITEM_SYSTEM;
   private static final long[] MAPS_ITEMS = new long[]{
      4804476335504286437L,
      72168242854101248L,
      7425316484398411638L,
      1956822834319723422L,
      7929559284197623585L,
      576572255751874079L,
      8436172026060763201L,
      8102047232258962505L
   };
   private static final long[] PIM_ITEMS = new long[]{
      5911208747185054768L,
      -1011653757168863700L,
      -5544721730296222436L,
      -2786162410658704605L,
      5182228461004335870L,
      5244072729690617291L,
      -6481681929958323011L,
      -8839945759096901113L,
      -2362642699356376043L,
      1967755168374878363L,
      -7444654586207082127L,
      -446032573701822616L,
      -5653680829824974669L,
      -4137793966879797982L,
      222533648386L,
      -3457638609614388654L,
      -3455386801215111102L,
      3504265587951702900L,
      -7944553129300517756L,
      -2166984963208053554L,
      -3455386779740274688L,
      5244072729690617291L,
      -6481681929958323011L,
      -2204303273264560528L,
      2946406880720845997L,
      5529224403653746205L,
      -2516548103282563172L,
      4101976187669332923L,
      9096799525298506811L,
      -3455386809805045760L,
      4804476335504286437L,
      72168242854101248L,
      7425316484398411638L,
      1956822834319723422L,
      7929559284197623585L,
      576572255751874079L,
      8436172026060763201L,
      8102047232258962505L,
      576473702188565197L,
      2481575490816602177L,
      6090037706880804457L,
      8102047232263348833L,
      576510910670095053L,
      7295658126394815553L,
      576599722987489280L,
      7861400420826594626L,
      28552639052767007L,
      816849182367372040L,
      -6889932015794097310L,
      576593900642463600L,
      7280350768864552837L,
      837219881975414848L,
      2325657816933403648L,
      8726009547332821101L,
      -5960965497428308992L,
      8534159717205350772L,
      16734882471562597L,
      3321173918711414798L,
      1585393545662919436L,
      8318175097989764398L,
      3348712038119381550L,
      1585384827596337766L,
      8318175097989764398L,
      3332884860899301934L,
      1526181514631716707L,
      1886572219194021678L,
      8029124566458524462L,
      -6986274752056562682L,
      1526181513104811531L,
      1886572219194021678L,
      3344795568797017646L,
      3320842610378958604L,
      3347142197221207573L,
      7717438221397143066L,
      3320842239196753198L,
      3347142197221207573L,
      7717438221397143066L,
      1526181514625906222L,
      1886572219194021678L,
      30240346695578414L,
      8077256608781250070L,
      7074626622147473011L,
      7006243172224033703L,
      3320842717461425883L,
      3347142197221207573L,
      8317424131686402975L,
      1670295799482817070L,
      7929606590769928238L,
      1886556415060880927L,
      1742355592530236928L,
      3323496046217032560L,
      3320610972287331699L,
      1585508133237304855L,
      7579024008170837294L,
      3320610972286990385L,
      3320842381596175895L,
      63664034713906709L,
      837438732806467094L,
      7188533836562440046L,
      3320610972286853308L,
      7579029780412968471L,
      3320610972286990385L,
      -2148745414707302889L,
      -3734021554352286208L,
      1585393982654811438L,
      -8273450967103564498L,
      1526181514917540654L,
      3245796169823511086L,
      4252371199464446510L,
      7180816080928904704L,
      7867254384689373803L,
      7074615352327435032L
   };
   private static final long[] FILE_ITEMS = new long[]{
      3504265587951702900L,
      -7944553129300517756L,
      -2166984963208053554L,
      -3455386779740274688L,
      5244072729690617291L,
      -6481681929958323011L,
      -2204303273264560528L,
      2946406880720845997L,
      5529224403653746205L,
      -2516548103282563172L,
      4101976187669332923L,
      9096799525298506811L,
      -3455386809805045760L,
      4804476335504286437L,
      72168242854101248L,
      7425316484398411638L,
      1956822834319723422L,
      7929559284197623585L,
      576572255751874079L,
      8436172026060763201L,
      8102047232258962505L,
      576473702188565197L,
      2481575490816602177L,
      6090037706880804457L
   };
   private static final long[] EMAIL_MESSAGING_ITEMS = new long[]{
      5244072729690617291L,
      -6481681929958323011L,
      -2204303273264560528L,
      2946406880720845997L,
      5529224403653746205L,
      -2516548103282563172L,
      4101976187669332923L,
      9096799525298506811L,
      -3455386809805045760L,
      4804476335504286437L,
      72168242854101248L,
      7425316484398411638L,
      1956822834319723422L,
      7929559284197623585L,
      576572255751874079L,
      8436172026060763201L,
      8102047232258962505L,
      576473702188565197L,
      2481575490816602177L,
      6090037706880804457L,
      8102047232263348833L,
      576510910670095053L,
      7295658126394815553L,
      576599722987489280L,
      7861400420826594626L,
      28552639052767007L,
      816849182367372040L,
      -6889932015794097310L,
      576593900642463600L,
      7280350768864552837L,
      837219881975414848L,
      2325657816933403648L,
      8726009547332821101L,
      -5960965497428308992L,
      8534159717205350772L,
      16734882471562597L,
      3321173918711414798L,
      1585393545662919436L,
      8318175097989764398L,
      3348712038119381550L,
      1585384827596337766L,
      8318175097989764398L,
      3332884860899301934L,
      1526181514631716707L,
      1886572219194021678L,
      8029124566458524462L,
      -6986274752056562682L,
      1526181513104811531L,
      1886572219194021678L,
      3344795568797017646L,
      3320842610378958604L,
      3347142197221207573L,
      7717438221397143066L,
      3320842239196753198L,
      3347142197221207573L,
      7717438221397143066L,
      1526181514625906222L,
      1886572219194021678L,
      30240346695578414L,
      8077256608781250070L,
      7074626622147473011L,
      7006243172224033703L,
      3320842717461425883L,
      3347142197221207573L
   };
   private static final long[] PHONE_ITEMS = new long[]{
      7558275355255656232L,
      2810978548042580997L,
      -3455386753970470912L,
      5911208747185054768L,
      -1011653757168863700L,
      -5544721730296222436L,
      -2786162410658704605L,
      5182228461004335870L,
      5244072729690617291L,
      -6481681929958323011L,
      -8839945759096901113L,
      -2362642699356376043L,
      1967755168374878363L,
      -7444654586207082127L,
      -446032573701822616L,
      -5653680829824974669L
   };

   private ApplicationMenuItemRepository() {
      ApplicationSpecificProcessing asp = new ApplicationMenuItemRepository$1(this);
      this._applicationSpecificProcessing.put(9096799525298506811L, asp);
      this._applicationSpecificProcessing.put(4101976187669332923L, asp);
      this._applicationSpecificProcessing.put(5529224403653746205L, asp);
      this._applicationSpecificProcessing.put(2946406880720845997L, asp);
      this._applicationSpecificProcessing.put(-5544721730296222436L, asp);
      this._applicationSpecificProcessing.put(4804476335504286437L, asp);
   }

   public static final ApplicationMenuItemRepository getInstance() {
      ApplicationControl.assertIPCAllowed(true);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         ApplicationMenuItemRepository instance = (ApplicationMenuItemRepository)ar.get(504808597225456274L);
         if (instance == null) {
            instance = new ApplicationMenuItemRepository();
            ar.put(504808597225456274L, instance);
         }

         return instance;
      }
   }

   final LongHashtable getApplicationSpecificProcessingTable() {
      return this._applicationSpecificProcessing;
   }

   public final void addMenuItem(long id, ApplicationMenuItem item) {
      this.addMenuItem(id, item, null, null);
   }

   public final void addMenuItem(long id, ApplicationMenuItem item, ApplicationDescriptor application) {
      this.addMenuItem(id, item, application, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void addMenuItem(long id, ApplicationMenuItem item, ApplicationDescriptor application, Object context) {
      if (Arrays.contains(MAPS_ITEMS, id)) {
         ApplicationControl.assertLocationApiPermitted(true, CommonResource.getBundle(), 10160);
      } else if (Arrays.contains(PIM_ITEMS, id)) {
         ApplicationControl.assertPIMAllowed(true);
      } else if (Arrays.contains(FILE_ITEMS, id)) {
         ApplicationControl.assertFileApiAllowed(true);
      } else if (Arrays.contains(EMAIL_MESSAGING_ITEMS, id)) {
         ApplicationControl.assertEmailAllowed(true);
      } else if (Arrays.contains(PHONE_ITEMS, id)) {
         ApplicationControl.assertPhonePermitted(true, CommonResource.getBundle(), 10167);
      }

      boolean var11 = false /* VF: Semaphore variable */;

      try {
         var11 = true;
         String vr = item.toString();
         if (vr == null || vr.trim().equals("")) {
            return;
         }

         var11 = false;
      } finally {
         if (var11) {
            return;
         }
      }

      if (application == null || this.checkApplication(application)) {
         if (id != 8522643724050848398L && id != 4804476335504286437L) {
            if (id == -2786162410658704605L || id == 5182228461004335870L) {
               CalendarProxy cp = CalendarProxy.getInstance();
               SdkProxyVerb spv = new SdkProxyVerb(item, id, application);
               item.setInternalCookie(spv);
               cp.addToRepository(id, spv);
            } else if (id != 3504265587951702900L && id != -7944553129300517756L && id != -2166984963208053554L) {
               VerbRepository vr = VerbRepository.getVerbRepository(id);
               Verb[] verbs = vr.getVerbs(null);
               if (verbs != null) {
                  for (int i = 0; i < verbs.length; i++) {
                     Verb var10000 = verbs[i];
                     if (verbs[i] instanceof SdkProxyVerb) {
                        SdkProxyVerb pverb = (SdkProxyVerb)var10000;
                        if (pverb.getApplicationMenuItem() != null && pverb.getApplicationMenuItem() == item) {
                           return;
                        }
                     }
                  }
               }

               SdkProxyVerb spv = new SdkProxyVerb(item, id, application);
               item.setInternalCookie(spv);
               vr.register(spv, id);
            } else if (application != null) {
               if (context == null || context instanceof Object) {
                  VerbRepository vr = VerbRepository.getVerbRepository(id);
                  SdkProxyMimeTypeVerb spmtv = new SdkProxyMimeTypeVerb(item, id, application, (String)context);
                  item.setInternalCookie(spmtv);
                  vr.register(spmtv, id);
               }
            }
         } else if (id != 4804476335504286437L || application != null) {
            ApplicationMenuItemRepository$SdkVerbFactory svf = new ApplicationMenuItemRepository$SdkVerbFactory(id, item, application);
            item.setInternalCookie(svf);
            VerbFactoryRepository.addFactory(id, svf);
         }
      }
   }

   public final boolean removeMenuItem(long id, ApplicationMenuItem item) {
      if (null == item) {
         return false;
      }

      boolean retval = false;
      if (id == 8522643724050848398L || id == 4804476335504286437L) {
         ApplicationMenuItemRepository$SdkVerbFactory svf = (ApplicationMenuItemRepository$SdkVerbFactory)item.getInternalCookie();
         if (svf == null) {
            return false;
         }

         item.setInternalCookie(null);
         VerbFactoryRepository.removeFactory(id, svf);
         retval = true;
      } else if (id != -2786162410658704605L && id != 5182228461004335870L) {
         VerbRepository vr = VerbRepository.getVerbRepository(id);
         Verb[] verbs = vr.getVerbs(null);
         if (verbs != null) {
            for (int i = 0; i < verbs.length; i++) {
               Verb var10000 = verbs[i];
               if (verbs[i] instanceof SdkProxyVerb) {
                  SdkProxyVerb pverb = (SdkProxyVerb)var10000;
                  if (pverb.getApplicationMenuItem() != null && pverb.getApplicationMenuItem() == item) {
                     vr.deregister(pverb, id);
                     item.setInternalCookie(null);
                     retval = true;
                  }
               }
            }
         }
      } else {
         CalendarProxy cp = CalendarProxy.getInstance();
         SdkProxyVerb spv = (SdkProxyVerb)item.getInternalCookie();
         if (spv == null) {
            return false;
         }

         item.setInternalCookie(null);
         cp.removeFromRepository(id, spv);
         retval = true;
      }

      return retval;
   }

   private final boolean checkApplication(ApplicationDescriptor application) {
      return application.getModuleHandle() != ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle()
         ? false
         : !ControlledAccess.verifyRRISignature(application.getModuleHandle()) || ControlledAccess.verifyRRISignatures(true);
   }
}
