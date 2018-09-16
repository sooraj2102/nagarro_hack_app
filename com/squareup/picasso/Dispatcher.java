package com.squareup.picasso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

class Dispatcher
{
  static final int AIRPLANE_MODE_CHANGE = 10;
  private static final int AIRPLANE_MODE_OFF = 0;
  private static final int AIRPLANE_MODE_ON = 1;
  private static final int BATCH_DELAY = 200;
  private static final String DISPATCHER_THREAD_NAME = "Dispatcher";
  static final int HUNTER_BATCH_COMPLETE = 8;
  static final int HUNTER_COMPLETE = 4;
  static final int HUNTER_DECODE_FAILED = 6;
  static final int HUNTER_DELAY_NEXT_BATCH = 7;
  static final int HUNTER_RETRY = 5;
  static final int NETWORK_STATE_CHANGE = 9;
  static final int REQUEST_BATCH_RESUME = 13;
  static final int REQUEST_CANCEL = 2;
  static final int REQUEST_GCED = 3;
  static final int REQUEST_SUBMIT = 1;
  private static final int RETRY_DELAY = 500;
  static final int TAG_PAUSE = 11;
  static final int TAG_RESUME = 12;
  boolean airplaneMode;
  final List<BitmapHunter> batch;
  final Cache cache;
  final Context context;
  final DispatcherThread dispatcherThread = new DispatcherThread();
  final Downloader downloader;
  final Map<Object, Action> failedActions;
  final Handler handler;
  final Map<String, BitmapHunter> hunterMap;
  final Handler mainThreadHandler;
  final Map<Object, Action> pausedActions;
  final Set<Object> pausedTags;
  final NetworkBroadcastReceiver receiver;
  final boolean scansNetworkChanges;
  final ExecutorService service;
  final Stats stats;

  Dispatcher(Context paramContext, ExecutorService paramExecutorService, Handler paramHandler, Downloader paramDownloader, Cache paramCache, Stats paramStats)
  {
    this.dispatcherThread.start();
    Utils.flushStackLocalLeaks(this.dispatcherThread.getLooper());
    this.context = paramContext;
    this.service = paramExecutorService;
    this.hunterMap = new LinkedHashMap();
    this.failedActions = new WeakHashMap();
    this.pausedActions = new WeakHashMap();
    this.pausedTags = new HashSet();
    this.handler = new DispatcherHandler(this.dispatcherThread.getLooper(), this);
    this.downloader = paramDownloader;
    this.mainThreadHandler = paramHandler;
    this.cache = paramCache;
    this.stats = paramStats;
    this.batch = new ArrayList(4);
    this.airplaneMode = Utils.isAirplaneModeOn(this.context);
    this.scansNetworkChanges = Utils.hasPermission(paramContext, "android.permission.ACCESS_NETWORK_STATE");
    this.receiver = new NetworkBroadcastReceiver(this);
    this.receiver.register();
  }

  private void batch(BitmapHunter paramBitmapHunter)
  {
    if (paramBitmapHunter.isCancelled());
    do
    {
      return;
      this.batch.add(paramBitmapHunter);
    }
    while (this.handler.hasMessages(7));
    this.handler.sendEmptyMessageDelayed(7, 200L);
  }

  private void flushFailedActions()
  {
    if (!this.failedActions.isEmpty())
    {
      Iterator localIterator = this.failedActions.values().iterator();
      while (localIterator.hasNext())
      {
        Action localAction = (Action)localIterator.next();
        localIterator.remove();
        if (localAction.getPicasso().loggingEnabled)
          Utils.log("Dispatcher", "replaying", localAction.getRequest().logId());
        performSubmit(localAction, false);
      }
    }
  }

  private void logBatch(List<BitmapHunter> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty()));
    do
      return;
    while (!((BitmapHunter)paramList.get(0)).getPicasso().loggingEnabled);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      BitmapHunter localBitmapHunter = (BitmapHunter)localIterator.next();
      if (localStringBuilder.length() > 0)
        localStringBuilder.append(", ");
      localStringBuilder.append(Utils.getLogIdsForHunter(localBitmapHunter));
    }
    Utils.log("Dispatcher", "delivered", localStringBuilder.toString());
  }

  private void markForReplay(Action paramAction)
  {
    Object localObject = paramAction.getTarget();
    if (localObject != null)
    {
      paramAction.willReplay = true;
      this.failedActions.put(localObject, paramAction);
    }
  }

  private void markForReplay(BitmapHunter paramBitmapHunter)
  {
    Action localAction = paramBitmapHunter.getAction();
    if (localAction != null)
      markForReplay(localAction);
    List localList = paramBitmapHunter.getActions();
    if (localList != null)
    {
      int i = 0;
      int j = localList.size();
      while (i < j)
      {
        markForReplay((Action)localList.get(i));
        i++;
      }
    }
  }

  void dispatchAirplaneModeChange(boolean paramBoolean)
  {
    Handler localHandler1 = this.handler;
    Handler localHandler2 = this.handler;
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      localHandler1.sendMessage(localHandler2.obtainMessage(10, i, 0));
      return;
    }
  }

  void dispatchCancel(Action paramAction)
  {
    this.handler.sendMessage(this.handler.obtainMessage(2, paramAction));
  }

  void dispatchComplete(BitmapHunter paramBitmapHunter)
  {
    this.handler.sendMessage(this.handler.obtainMessage(4, paramBitmapHunter));
  }

  void dispatchFailed(BitmapHunter paramBitmapHunter)
  {
    this.handler.sendMessage(this.handler.obtainMessage(6, paramBitmapHunter));
  }

  void dispatchNetworkStateChange(NetworkInfo paramNetworkInfo)
  {
    this.handler.sendMessage(this.handler.obtainMessage(9, paramNetworkInfo));
  }

  void dispatchPauseTag(Object paramObject)
  {
    this.handler.sendMessage(this.handler.obtainMessage(11, paramObject));
  }

  void dispatchResumeTag(Object paramObject)
  {
    this.handler.sendMessage(this.handler.obtainMessage(12, paramObject));
  }

  void dispatchRetry(BitmapHunter paramBitmapHunter)
  {
    this.handler.sendMessageDelayed(this.handler.obtainMessage(5, paramBitmapHunter), 500L);
  }

  void dispatchSubmit(Action paramAction)
  {
    this.handler.sendMessage(this.handler.obtainMessage(1, paramAction));
  }

  void performAirplaneModeChange(boolean paramBoolean)
  {
    this.airplaneMode = paramBoolean;
  }

  void performBatchComplete()
  {
    ArrayList localArrayList = new ArrayList(this.batch);
    this.batch.clear();
    this.mainThreadHandler.sendMessage(this.mainThreadHandler.obtainMessage(8, localArrayList));
    logBatch(localArrayList);
  }

  void performCancel(Action paramAction)
  {
    String str = paramAction.getKey();
    BitmapHunter localBitmapHunter = (BitmapHunter)this.hunterMap.get(str);
    if (localBitmapHunter != null)
    {
      localBitmapHunter.detach(paramAction);
      if (localBitmapHunter.cancel())
      {
        this.hunterMap.remove(str);
        if (paramAction.getPicasso().loggingEnabled)
          Utils.log("Dispatcher", "canceled", paramAction.getRequest().logId());
      }
    }
    if (this.pausedTags.contains(paramAction.getTag()))
    {
      this.pausedActions.remove(paramAction.getTarget());
      if (paramAction.getPicasso().loggingEnabled)
        Utils.log("Dispatcher", "canceled", paramAction.getRequest().logId(), "because paused request got canceled");
    }
    Action localAction = (Action)this.failedActions.remove(paramAction.getTarget());
    if ((localAction != null) && (localAction.getPicasso().loggingEnabled))
      Utils.log("Dispatcher", "canceled", localAction.getRequest().logId(), "from replaying");
  }

  void performComplete(BitmapHunter paramBitmapHunter)
  {
    if (MemoryPolicy.shouldWriteToMemoryCache(paramBitmapHunter.getMemoryPolicy()))
      this.cache.set(paramBitmapHunter.getKey(), paramBitmapHunter.getResult());
    this.hunterMap.remove(paramBitmapHunter.getKey());
    batch(paramBitmapHunter);
    if (paramBitmapHunter.getPicasso().loggingEnabled)
      Utils.log("Dispatcher", "batched", Utils.getLogIdsForHunter(paramBitmapHunter), "for completion");
  }

  void performError(BitmapHunter paramBitmapHunter, boolean paramBoolean)
  {
    String str1;
    StringBuilder localStringBuilder;
    if (paramBitmapHunter.getPicasso().loggingEnabled)
    {
      str1 = Utils.getLogIdsForHunter(paramBitmapHunter);
      localStringBuilder = new StringBuilder().append("for error");
      if (!paramBoolean)
        break label80;
    }
    label80: for (String str2 = " (will replay)"; ; str2 = "")
    {
      Utils.log("Dispatcher", "batched", str1, str2);
      this.hunterMap.remove(paramBitmapHunter.getKey());
      batch(paramBitmapHunter);
      return;
    }
  }

  void performNetworkStateChange(NetworkInfo paramNetworkInfo)
  {
    if ((this.service instanceof PicassoExecutorService))
      ((PicassoExecutorService)this.service).adjustThreadCount(paramNetworkInfo);
    if ((paramNetworkInfo != null) && (paramNetworkInfo.isConnected()))
      flushFailedActions();
  }

  void performPauseTag(Object paramObject)
  {
    if (!this.pausedTags.add(paramObject));
    while (true)
    {
      return;
      Iterator localIterator = this.hunterMap.values().iterator();
      while (localIterator.hasNext())
      {
        BitmapHunter localBitmapHunter = (BitmapHunter)localIterator.next();
        boolean bool = localBitmapHunter.getPicasso().loggingEnabled;
        Action localAction1 = localBitmapHunter.getAction();
        List localList = localBitmapHunter.getActions();
        int i;
        label87: int j;
        label200: Action localAction2;
        if ((localList != null) && (!localList.isEmpty()))
        {
          i = 1;
          if ((localAction1 == null) && (i == 0))
            break label241;
          if ((localAction1 != null) && (localAction1.getTag().equals(paramObject)))
          {
            localBitmapHunter.detach(localAction1);
            this.pausedActions.put(localAction1.getTarget(), localAction1);
            if (bool)
              Utils.log("Dispatcher", "paused", localAction1.request.logId(), "because tag '" + paramObject + "' was paused");
          }
          if (i == 0)
            break label316;
          j = -1 + localList.size();
          if (j < 0)
            break label316;
          localAction2 = (Action)localList.get(j);
          if (localAction2.getTag().equals(paramObject))
            break label243;
        }
        while (true)
        {
          j--;
          break label200;
          i = 0;
          break label87;
          label241: break;
          label243: localBitmapHunter.detach(localAction2);
          this.pausedActions.put(localAction2.getTarget(), localAction2);
          if (!bool)
            continue;
          Utils.log("Dispatcher", "paused", localAction2.request.logId(), "because tag '" + paramObject + "' was paused");
        }
        label316: if (!localBitmapHunter.cancel())
          continue;
        localIterator.remove();
        if (!bool)
          continue;
        Utils.log("Dispatcher", "canceled", Utils.getLogIdsForHunter(localBitmapHunter), "all actions paused");
      }
    }
  }

  void performResumeTag(Object paramObject)
  {
    if (!this.pausedTags.remove(paramObject));
    ArrayList localArrayList;
    do
    {
      return;
      localArrayList = null;
      Iterator localIterator = this.pausedActions.values().iterator();
      while (localIterator.hasNext())
      {
        Action localAction = (Action)localIterator.next();
        if (!localAction.getTag().equals(paramObject))
          continue;
        if (localArrayList == null)
          localArrayList = new ArrayList();
        localArrayList.add(localAction);
        localIterator.remove();
      }
    }
    while (localArrayList == null);
    this.mainThreadHandler.sendMessage(this.mainThreadHandler.obtainMessage(13, localArrayList));
  }

  void performRetry(BitmapHunter paramBitmapHunter)
  {
    if (paramBitmapHunter.isCancelled());
    label69: boolean bool3;
    label130: label136: 
    do
    {
      return;
      if (this.service.isShutdown())
      {
        performError(paramBitmapHunter, false);
        return;
      }
      boolean bool1 = this.scansNetworkChanges;
      NetworkInfo localNetworkInfo = null;
      if (bool1)
        localNetworkInfo = ((ConnectivityManager)Utils.getService(this.context, "connectivity")).getActiveNetworkInfo();
      int i;
      if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()))
      {
        i = 1;
        boolean bool2 = paramBitmapHunter.shouldRetry(this.airplaneMode, localNetworkInfo);
        bool3 = paramBitmapHunter.supportsReplay();
        if (bool2)
          break label136;
        if ((!this.scansNetworkChanges) || (!bool3))
          break label130;
      }
      for (boolean bool4 = true; ; bool4 = false)
      {
        performError(paramBitmapHunter, bool4);
        if (!bool4)
          break;
        markForReplay(paramBitmapHunter);
        return;
        i = 0;
        break label69;
      }
      if ((!this.scansNetworkChanges) || (i != 0))
      {
        if (paramBitmapHunter.getPicasso().loggingEnabled)
          Utils.log("Dispatcher", "retrying", Utils.getLogIdsForHunter(paramBitmapHunter));
        if ((paramBitmapHunter.getException() instanceof NetworkRequestHandler.ContentLengthException))
          paramBitmapHunter.networkPolicy |= NetworkPolicy.NO_CACHE.index;
        paramBitmapHunter.future = this.service.submit(paramBitmapHunter);
        return;
      }
      performError(paramBitmapHunter, bool3);
    }
    while (!bool3);
    markForReplay(paramBitmapHunter);
  }

  void performSubmit(Action paramAction)
  {
    performSubmit(paramAction, true);
  }

  void performSubmit(Action paramAction, boolean paramBoolean)
  {
    if (this.pausedTags.contains(paramAction.getTag()))
    {
      this.pausedActions.put(paramAction.getTarget(), paramAction);
      if (paramAction.getPicasso().loggingEnabled)
        Utils.log("Dispatcher", "paused", paramAction.request.logId(), "because tag '" + paramAction.getTag() + "' is paused");
    }
    do
    {
      while (true)
      {
        return;
        BitmapHunter localBitmapHunter1 = (BitmapHunter)this.hunterMap.get(paramAction.getKey());
        if (localBitmapHunter1 != null)
        {
          localBitmapHunter1.attach(paramAction);
          return;
        }
        if (!this.service.isShutdown())
          break;
        if (!paramAction.getPicasso().loggingEnabled)
          continue;
        Utils.log("Dispatcher", "ignored", paramAction.request.logId(), "because shut down");
        return;
      }
      BitmapHunter localBitmapHunter2 = BitmapHunter.forRequest(paramAction.getPicasso(), this, this.cache, this.stats, paramAction);
      localBitmapHunter2.future = this.service.submit(localBitmapHunter2);
      this.hunterMap.put(paramAction.getKey(), localBitmapHunter2);
      if (!paramBoolean)
        continue;
      this.failedActions.remove(paramAction.getTarget());
    }
    while (!paramAction.getPicasso().loggingEnabled);
    Utils.log("Dispatcher", "enqueued", paramAction.request.logId());
  }

  void shutdown()
  {
    if ((this.service instanceof PicassoExecutorService))
      this.service.shutdown();
    this.downloader.shutdown();
    this.dispatcherThread.quit();
    Picasso.HANDLER.post(new Runnable()
    {
      public void run()
      {
        Dispatcher.this.receiver.unregister();
      }
    });
  }

  private static class DispatcherHandler extends Handler
  {
    private final Dispatcher dispatcher;

    public DispatcherHandler(Looper paramLooper, Dispatcher paramDispatcher)
    {
      super();
      this.dispatcher = paramDispatcher;
    }

    public void handleMessage(Message paramMessage)
    {
      int i = 1;
      switch (paramMessage.what)
      {
      case 3:
      case 8:
      default:
        Picasso.HANDLER.post(new Runnable(paramMessage)
        {
          public void run()
          {
            throw new AssertionError("Unknown handler message received: " + this.val$msg.what);
          }
        });
        return;
      case 1:
        Action localAction2 = (Action)paramMessage.obj;
        this.dispatcher.performSubmit(localAction2);
        return;
      case 2:
        Action localAction1 = (Action)paramMessage.obj;
        this.dispatcher.performCancel(localAction1);
        return;
      case 11:
        Object localObject2 = paramMessage.obj;
        this.dispatcher.performPauseTag(localObject2);
        return;
      case 12:
        Object localObject1 = paramMessage.obj;
        this.dispatcher.performResumeTag(localObject1);
        return;
      case 4:
        BitmapHunter localBitmapHunter3 = (BitmapHunter)paramMessage.obj;
        this.dispatcher.performComplete(localBitmapHunter3);
        return;
      case 5:
        BitmapHunter localBitmapHunter2 = (BitmapHunter)paramMessage.obj;
        this.dispatcher.performRetry(localBitmapHunter2);
        return;
      case 6:
        BitmapHunter localBitmapHunter1 = (BitmapHunter)paramMessage.obj;
        this.dispatcher.performError(localBitmapHunter1, false);
        return;
      case 7:
        this.dispatcher.performBatchComplete();
        return;
      case 9:
        NetworkInfo localNetworkInfo = (NetworkInfo)paramMessage.obj;
        this.dispatcher.performNetworkStateChange(localNetworkInfo);
        return;
      case 10:
      }
      Dispatcher localDispatcher = this.dispatcher;
      if (paramMessage.arg1 == i);
      while (true)
      {
        localDispatcher.performAirplaneModeChange(i);
        return;
        int j = 0;
      }
    }
  }

  static class DispatcherThread extends HandlerThread
  {
    DispatcherThread()
    {
      super(10);
    }
  }

  static class NetworkBroadcastReceiver extends BroadcastReceiver
  {
    static final String EXTRA_AIRPLANE_STATE = "state";
    private final Dispatcher dispatcher;

    NetworkBroadcastReceiver(Dispatcher paramDispatcher)
    {
      this.dispatcher = paramDispatcher;
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent == null);
      String str;
      do
        while (true)
        {
          return;
          str = paramIntent.getAction();
          if (!"android.intent.action.AIRPLANE_MODE".equals(str))
            break;
          if (!paramIntent.hasExtra("state"))
            continue;
          this.dispatcher.dispatchAirplaneModeChange(paramIntent.getBooleanExtra("state", false));
          return;
        }
      while (!"android.net.conn.CONNECTIVITY_CHANGE".equals(str));
      ConnectivityManager localConnectivityManager = (ConnectivityManager)Utils.getService(paramContext, "connectivity");
      this.dispatcher.dispatchNetworkStateChange(localConnectivityManager.getActiveNetworkInfo());
    }

    void register()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
      if (this.dispatcher.scansNetworkChanges)
        localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
      this.dispatcher.context.registerReceiver(this, localIntentFilter);
    }

    void unregister()
    {
      this.dispatcher.context.unregisterReceiver(this);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.Dispatcher
 * JD-Core Version:    0.6.0
 */