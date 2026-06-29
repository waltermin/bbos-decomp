package net.rim.blackberry.api.invoke;

import java.util.Calendar;
import java.util.Vector;
import javax.microedition.pim.Contact;
import javax.wireless.messaging.TextMessage;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.maps.MapView;
import net.rim.blackberry.api.pdap.ContactImpl;
import net.rim.blackberry.api.pdap.EventImpl;
import net.rim.blackberry.api.pdap.MemoImpl;
import net.rim.blackberry.api.pdap.ToDoImpl;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.RunnableVerbWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.memo.MemoCollection;
import net.rim.device.apps.api.memo.MemoCollectionHolder;
import net.rim.device.apps.api.memo.MemoVerbs;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskCollectionHolder;
import net.rim.device.apps.api.task.TaskVerbs;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSModelFactory;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;

public final class Invoke {
   public static final int APP_TYPE_ADDRESSBOOK = 0;
   public static final int APP_TYPE_CALENDAR = 1;
   public static final int APP_TYPE_MESSAGES = 2;
   public static final int APP_TYPE_MEMOPAD = 3;
   public static final int APP_TYPE_PHONE = 4;
   public static final int APP_TYPE_TASKS = 5;
   public static final int APP_TYPE_CAMERA = 6;
   public static final int APP_TYPE_MAPS = 7;
   public static final int APP_TYPE_BLUETOOTH_CONFIG = 8;
   public static final int APP_TYPE_CALCULATOR = 9;
   public static final int APP_TYPE_SEARCH = 10;

   private Invoke() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void invokeApplication(int appType, ApplicationArguments args) {
      switch (appType) {
         case -1:
            int handle = -1;
            throw new Object("Invalid application type");
         case 0:
         default:
            if (args == null) {
               args = new AddressBookArguments();
            }

            if (!(args instanceof AddressBookArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            String[] stringArgs = args.getArgs();
            if (stringArgs != null && stringArgs.length > 0 && stringArgs[0].equals("new")) {
               ApplicationControl.assertPIMAllowed(true);
               Verb addToABVerb = AddressBookServices.getAddToAddressBookVerb();
               Contact pimContact = ((AddressBookArguments)args).getContactArg();
               if (pimContact != null) {
                  invokeVerb(addToABVerb, ((ContactImpl)pimContact).getInternalModel());
                  return;
               }

               invokeVerb(addToABVerb, null);
               return;
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_addressbook_app");
            if (!startApp(handle, stringArgs)) {
               throw new Object("Could not start internal application");
            }
            break;
         case 1:
            if (args == null) {
               args = new CalendarArguments();
            }

            if (!(args instanceof CalendarArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            String[] stringArgs = args.getArgs();
            ContextObject co = null;
            if (stringArgs != null) {
               co = (ContextObject)(new Object());
               if (stringArgs[0].equals("newview")) {
                  if (!stringArgs[1].equals("-1")) {
                     boolean var20 = false /* VF: Semaphore variable */;

                     try {
                        var20 = true;
                        co.putIntegerData(Integer.parseInt(stringArgs[1]));
                        var20 = false;
                     } finally {
                        if (var20) {
                           throw new Object("Invalid ApplicationArguments");
                        }
                     }
                  }
               } else if (stringArgs[0].equals("newappt")) {
                  ApplicationControl.assertPIMAllowed(true);
                  ContextObject.put(co, -8485899342890396495L, stringArgs[0]);
               }

               Calendar date = ((CalendarArguments)args).getDateArg();
               if (date != null) {
                  ContextObject.put(co, 4143325197084129318L, date);
               }

               EventImpl event = (EventImpl)((CalendarArguments)args).getEventArg();
               if (event != null) {
                  ContextObject.put(co, 4143325197084129318L, event.getRimEvent());
               }
            }

            Verb viewCalendarVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(8025740836317336000L);
            invokeVerb(viewCalendarVerb, co);
            return;
         case 2:
            if (args == null) {
               args = new MessageArguments();
            }

            if (!(args instanceof MessageArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            String[] stringArgs = args.getArgs();
            Folder folderArg = ((MessageArguments)args).getFolderArg();
            if (stringArgs != null && stringArgs.length > 0 && stringArgs[0].startsWith("new")) {
               ApplicationControl.assertEmailAllowed(true);
               VerbRepository verbRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
               Verb composeVerb = null;
               ContextObject context = null;
               Message m = ((MessageArguments)args).getMessageArg();
               if (m != null) {
                  context = (ContextObject)(new Object());
                  m.setAddressesToFreeForm(true);
                  ContextObject.put(context, -8485899342890396495L, m.getInternalModel());
               }

               TextMessage tm = ((MessageArguments)args).getTextMessageArg();
               if (tm != null) {
                  SMSModel sms = SMSModelFactory.createSMSModel(0, null);
                  String number = tm.getAddress();
                  if (number == null) {
                     number = "";
                  }

                  if (number.startsWith("sms://")) {
                     number = number.substring(6);
                  }

                  ContextObject numberContext = (ContextObject)(new Object());
                  ContextObject.put(numberContext, 253, number);
                  PhoneNumberModel pnm = (PhoneNumberModel)FactoryUtil.createInstance(3797587162219887872L, numberContext);
                  String payloadText = tm.getPayloadText();
                  sms.setBody(payloadText == null ? "" : payloadText);
                  sms.setFlags(320);
                  context = (ContextObject)(new Object(117));
                  ContextObject.put(context, 247, pnm);
                  ContextObject.put(context, -8485899342890396495L, sms);
               }

               if (!stringArgs[0].equals("new")) {
                  if (!stringArgs[0].equals("newpin")) {
                     if (stringArgs[0].equals("newsms")) {
                        Verb[] composeVerbs = verbRepository.getVerbs(3797587162219887872L);
                        if (composeVerbs != null && composeVerbs.length > 0) {
                           composeVerb = composeVerbs[0];
                           if (context != null && composeVerb instanceof Object && composeVerb instanceof Object) {
                              composeVerb = (Verb)((Copyable)composeVerb).copy();
                              ((SetParameter)composeVerb).setParameter(context);
                           }
                        }
                     } else if (stringArgs[0].equals("newmms")) {
                        Verb[] composeVerbs = verbRepository.getVerbs(-2985347935260258684L);
                        if (composeVerbs != null && composeVerbs.length > 1) {
                           composeVerb = composeVerbs[1];
                        }
                     }
                  } else {
                     if (stringArgs.length > 1) {
                        ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
                        Factory factory = (Factory)ar.waitFor(4246852237058296601L);
                        PINAddressModel address = (PINAddressModel)factory.createInstance(stringArgs[1]);
                        context = (ContextObject)(new Object());
                        ContextObject.put(context, 254, address);
                        if (stringArgs[2] != null) {
                           ContextObject.put(context, -1188330299125235844L, stringArgs[2]);
                        }

                        if (stringArgs[3] != null) {
                           ContextObject.put(context, -8478555129720928586L, stringArgs[3]);
                        }
                     }

                     Verb[] composeVerbs = verbRepository.getVerbs(4246852237058296601L);
                     if (composeVerbs != null && composeVerbs.length > 0) {
                        composeVerb = composeVerbs[0];
                     }
                  }
               } else {
                  if (stringArgs.length > 1) {
                     ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
                     Factory factory = (Factory)ar.waitFor(-2985347935260258684L);
                     EmailAddressModel address = (EmailAddressModel)factory.createInstance(stringArgs[1]);
                     context = (ContextObject)(new Object());
                     ContextObject.put(context, 254, address);
                     if (stringArgs[2] != null) {
                        ContextObject.put(context, -1188330299125235844L, stringArgs[2]);
                     }

                     if (stringArgs[3] != null) {
                        ContextObject.put(context, -8478555129720928586L, stringArgs[3]);
                     }
                  }

                  Verb[] composeVerbs = verbRepository.getVerbs(-2985347935260258684L);
                  if (composeVerbs != null && composeVerbs.length > 0) {
                     composeVerb = composeVerbs[0];
                  }
               }

               invokeVerb(composeVerb, context);
               return;
            }

            if (folderArg != null) {
               net.rim.device.apps.api.messaging.Folder folder = (net.rim.device.apps.api.messaging.Folder)folderArg.getEmailFolder();
               ShowMessageApp.invokeVerb(new Invoke$OpenFolderVerb(), folder);
               return;
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_messaging_app");
            if (!startApp(handle, args.getArgs())) {
               throw new Object("Could not start internal application");
            }
            break;
         case 3:
            if (args == null) {
               args = new MemoArguments();
            }

            if (!(args instanceof MemoArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            MemoCollection collection = MemoCollectionHolder.getMemoCollection();
            if (collection == null) {
               throw new Object("Could not start internal application");
            }

            String[] stringArgs = args.getArgs();
            if (stringArgs != null && stringArgs.length > 0) {
               MemoImpl memo = (MemoImpl)((MemoArguments)args).getMemoArg();
               boolean memoExists = memo == null ? false : collection.contains(memo.getMemoModel());
               Verb v = null;
               if (stringArgs[0].equals("new")) {
                  ApplicationControl.assertPIMAllowed(true);
                  if (memoExists) {
                     throw new Object("Memo already exists");
                  }

                  v = MemoVerbs.getMemoVerb(0);
               } else if (stringArgs[0].equals("edit") || stringArgs[0].equals("view")) {
                  ApplicationControl.assertPIMAllowed(true);
                  if (!memoExists) {
                     throw new Object("Memo does not exist");
                  }

                  v = MemoVerbs.getMemoVerb(1);
               }

               if (memo != null && v instanceof Object) {
                  ((SetParameter)v).setParameter(memo.getMemoModel());
               }

               invokeVerb(v, null);
               return;
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_memo_app");
            if (!startApp(handle, args.getArgs())) {
               throw new Object("Could not start internal application");
            }
            break;
         case 4:
            if (args == null) {
               args = new PhoneArguments();
            }

            if (!(args instanceof PhoneArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            String[] stringArgs = args.getArgs();
            int handle;
            if (stringArgs == null) {
               handle = CodeModuleManager.getModuleHandle("net_rim_bb_phone_app");
            } else {
               ApplicationControl.assertPhonePermitted(true, CommonResource.getBundle(), 10045);
               handle = CodeModuleManager.getModuleHandle("net_rim_bb_phone_entry");
            }

            if (stringArgs != null && stringArgs.length > 0 && stringArgs[1].equals("voicemail")) {
               Verb vmVerb = (Verb)(new Object());
               if (vmVerb.invoke(null) == null) {
                  throw new Object("Could not start internal application");
               }
            } else if (!startApp(handle, stringArgs)) {
               throw new Object("Could not start internal application");
            }
            break;
         case 5:
            if (args == null) {
               args = new TaskArguments();
            }

            if (!(args instanceof TaskArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            TaskCollection collection = TaskCollectionHolder.getTaskCollection();
            if (collection == null) {
               throw new Object("Could not start internal application");
            }

            String[] stringArgs = args.getArgs();
            if (stringArgs != null && stringArgs.length > 0) {
               ToDoImpl todo = (ToDoImpl)((TaskArguments)args).getToDoArg();
               boolean todoExists = todo == null ? false : collection.contains(todo.getInternalModel());
               Verb v = null;
               if (stringArgs[0].equals("new")) {
                  ApplicationControl.assertPIMAllowed(true);
                  if (todoExists) {
                     throw new Object("Cannot create ToDo: ToDo already exists");
                  }

                  v = TaskVerbs.getTaskVerb(2);
               } else if (stringArgs[0].equals("view")) {
                  if (!todoExists) {
                     throw new Object("Cannot view ToDo: ToDo does not exist");
                  }

                  v = TaskVerbs.getTaskVerb(1);
               }

               if (todo != null && v instanceof Object) {
                  ((SetParameter)v).setParameter(todo.getInternalModel());
               }

               invokeVerb(v, null);
               return;
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_task_app");
            if (!startApp(handle, args.getArgs())) {
               throw new Object("Could not start internal application");
            }
            break;
         case 6:
            if (args == null) {
               args = new CameraArguments();
            }

            if (!(args instanceof CameraArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_camera");
            if (!startApp(handle, args.getArgs())) {
               throw new Object("Could not start internal application");
            }
            break;
         case 7:
            if (args == null) {
               args = new MapsArguments();
            }

            if (!(args instanceof MapsArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_lbs");
            String[] stringArgs = args.getArgs();
            Contact contact = ((MapsArguments)args).getContactArg();
            MapView mapView = ((MapsArguments)args).getMapViewArg();
            int addressIndex = ((MapsArguments)args).getAddressIndexArg();
            if (stringArgs == null && contact == null && mapView == null) {
               if (!startApp(handle, null)) {
                  throw new Object("Could not start internal application");
               }
            } else {
               if (handle == -1) {
                  throw new Object("Could not start internal application");
               }

               ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(handle);
               if (descriptors == null || descriptors.length <= 0) {
                  throw new Object("Could not start internal application");
               }

               ApplicationDescriptor mapsDescriptor = null;

               for (int cnt = descriptors.length - 1; cnt >= 0; cnt--) {
                  String[] appargs = descriptors[cnt].getArgs();
                  if (appargs == null || appargs.length == 0) {
                     mapsDescriptor = descriptors[cnt];
                     break;
                  }
               }

               if (mapsDescriptor == null) {
                  throw new Object("Could not start internal application");
               }

               boolean var17 = false /* VF: Semaphore variable */;

               try {
                  var17 = true;
                  int var59 = ApplicationManager.getApplicationManager().runApplication(mapsDescriptor, true);
                  if (stringArgs != null && stringArgs.length > 0 && stringArgs[0].equals("location_document")) {
                     RIMGlobalMessagePoster.postGlobalEvent(var59, 5481368234910671574L, 0, 0, "text/vnd.rim.location", stringArgs[1]);
                     var17 = false;
                  } else if (contact != null) {
                     Vector addresses = ((ContactImpl)contact).getMailingAddressModels();
                     if (addresses == null) {
                        var17 = false;
                     } else if (addressIndex < 0) {
                        var17 = false;
                     } else if (addressIndex >= addresses.size()) {
                        var17 = false;
                     } else {
                        MailingAddressModel address = (MailingAddressModel)addresses.elementAt(addressIndex);
                        RIMGlobalMessagePoster.postGlobalEvent(var59, 9060179257426397655L, 0, 0, address, null);
                        var17 = false;
                     }
                  } else {
                     if (mapView != null) {
                        ContextObject context = mapView.toContextObject();
                        RIMGlobalMessagePoster.postGlobalEvent(var59, 6982071258373876883L, 0, 0, context, null);
                        return;
                     }

                     var17 = false;
                  }
               } finally {
                  if (var17) {
                     throw new Object("Could not start internal application");
                  }
               }
            }
            break;
         case 8:
            if (args != null) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            new Invoke$1().start();
            return;
         case 9:
            if (args == null) {
               args = new CalculatorArguments();
            }

            if (!(args instanceof CalculatorArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_standardcalculator_app");
            if (!startApp(handle, args.getArgs())) {
               throw new Object("Could not start internal application");
            }
            break;
         case 10:
            if (args == null) {
               args = new SearchArguments();
            }

            if (!(args instanceof SearchArguments)) {
               throw new Object("ApplicationArguments object type does not match specified application type");
            }

            int handle = CodeModuleManager.getModuleHandle("net_rim_bb_globalsearch_app");
            if (!startApp(handle, args.getArgs())) {
               throw new Object("Could not start internal application");
            }
      }
   }

   private static final boolean startApp(int handle, String[] args) {
      if (handle <= 0) {
         return false;
      }

      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(handle);
      if (descriptors != null && descriptors.length > 0) {
         ApplicationDescriptor descriptor = (ApplicationDescriptor)(new Object(descriptors[0], args));

         try {
            ApplicationManager.getApplicationManager().runApplication(descriptor);
            return true;
         } finally {
            ;
         }
      } else {
         return false;
      }
   }

   private static final void invokeVerb(Verb verb, Object parameter) {
      if (verb != null) {
         Application myApp = Application.getApplication();
         if (myApp.isEventThread()) {
            if (CodeModuleManager.isMidlet()) {
               synchronized (Application.getEventLock()) {
                  verb.invoke(parameter);
               }
            } else {
               verb.invoke(parameter);
            }
         } else {
            RunnableVerbWrapper rvw = (RunnableVerbWrapper)(new Object(myApp, verb, parameter, false));
            myApp.invokeLater(rvw);
         }
      } else {
         throw new Object("Could not start internal application");
      }
   }
}
