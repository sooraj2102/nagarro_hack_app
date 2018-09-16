package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.ImageView;
import android.widget.RemoteViews;
import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

public class Picasso
{
  static final Handler HANDLER = new Handler()
  {
    public void handleMessage(Message paramMessage)
    {
      List localList2;
      int k;
      int m;
      switch (paramMessage.what)
      {
      default:
        throw new AssertionError("Unknown handler message received: " + paramMessage.what);
      case 8:
        localList2 = (List)paramMessage.obj;
        k = 0;
        m = localList2.size();
      case 3:
        while (k < m)
        {
          BitmapHunter localBitmapHunter = (BitmapHunter)localList2.get(k);
          localBitmapHunter.picasso.complete(localBitmapHunter);
          k++;
          continue;
          Action localAction2 = (Action)paramMessage.obj;
          if (localAction2.getPicasso().loggingEnabled)
            Utils.log("Main", "canceled", localAction2.request.logId(), "target got garbage collected");
          localAction2.picasso.cancelExistingRequest(localAction2.getTarget());
        }
      case 13:
      }
      while (true)
      {
        return;
        List localList1 = (List)paramMessage.obj;
        int i = 0;
        int j = localList1.size();
        while (i < j)
        {
          Action localAction1 = (Action)localList1.get(i);
          localAction1.picasso.resumeAction(localAction1);
          i++;
        }
      }
    }
  };
  static final String TAG = "Picasso";
  static volatile Picasso singleton = null;
  final Cache cache;
  private final CleanupThread cleanupThread;
  final Context context;
  final Bitmap.Config defaultBitmapConfig;
  final Dispatcher dispatcher;
  boolean indicatorsEnabled;
  private final Listener listener;
  volatile boolean loggingEnabled;
  final ReferenceQueue<Object> referenceQueue;
  private final List<RequestHandler> requestHandlers;
  private final RequestTransformer requestTransformer;
  boolean shutdown;
  final Stats stats;
  final Map<Object, Action> targetToAction;
  final Map<ImageView, DeferredRequestCreator> targetToDeferredRequestCreator;

  Picasso(Context paramContext, Dispatcher paramDispatcher, Cache paramCache, Listener paramListener, RequestTransformer paramRequestTransformer, List<RequestHandler> paramList, Stats paramStats, Bitmap.Config paramConfig, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.context = paramContext;
    this.dispatcher = paramDispatcher;
    this.cache = paramCache;
    this.listener = paramListener;
    this.requestTransformer = paramRequestTransformer;
    this.defaultBitmapConfig = paramConfig;
    if (paramList != null);
    for (int i = paramList.size(); ; i = 0)
    {
      ArrayList localArrayList = new ArrayList(7 + i);
      localArrayList.add(new ResourceRequestHandler(paramContext));
      if (paramList != null)
        localArrayList.addAll(paramList);
      localArrayList.add(new ContactsPhotoRequestHandler(paramContext));
      localArrayList.add(new MediaStoreRequestHandler(paramContext));
      localArrayList.add(new ContentStreamRequestHandler(paramContext));
      localArrayList.add(new AssetRequestHandler(paramContext));
      localArrayList.add(new FileRequestHandler(paramContext));
      localArrayList.add(new NetworkRequestHandler(paramDispatcher.downloader, paramStats));
      this.requestHandlers = Collections.unmodifiableList(localArrayList);
      this.stats = paramStats;
      this.targetToAction = new WeakHashMap();
      this.targetToDeferredRequestCreator = new WeakHashMap();
      this.indicatorsEnabled = paramBoolean1;
      this.loggingEnabled = paramBoolean2;
      this.referenceQueue = new ReferenceQueue();
      this.cleanupThread = new CleanupThread(this.referenceQueue, HANDLER);
      this.cleanupThread.start();
      return;
    }
  }

  private void cancelExistingRequest(Object paramObject)
  {
    Utils.checkMain();
    Action localAction = (Action)this.targetToAction.remove(paramObject);
    if (localAction != null)
    {
      localAction.cancel();
      this.dispatcher.dispatchCancel(localAction);
    }
    if ((paramObject instanceof ImageView))
    {
      ImageView localImageView = (ImageView)paramObject;
      DeferredRequestCreator localDeferredRequestCreator = (DeferredRequestCreator)this.targetToDeferredRequestCreator.remove(localImageView);
      if (localDeferredRequestCreator != null)
        localDeferredRequestCreator.cancel();
    }
  }

  private void deliverAction(Bitmap paramBitmap, LoadedFrom paramLoadedFrom, Action paramAction)
  {
    if (paramAction.isCancelled());
    do
    {
      while (true)
      {
        return;
        if (!paramAction.willReplay())
          this.targetToAction.remove(paramAction.getTarget());
        if (paramBitmap == null)
          break;
        if (paramLoadedFrom == null)
          throw new AssertionError("LoadedFrom cannot be null.");
        paramAction.complete(paramBitmap, paramLoadedFrom);
        if (!this.loggingEnabled)
          continue;
        Utils.log("Main", "completed", paramAction.request.logId(), "from " + paramLoadedFrom);
        return;
      }
      paramAction.error();
    }
    while (!this.loggingEnabled);
    Utils.log("Main", "errored", paramAction.request.logId());
  }

  public static void setSingletonInstance(Picasso paramPicasso)
  {
    monitorenter;
    try
    {
      if (singleton != null)
        throw new IllegalStateException("Singleton instance already exists.");
    }
    finally
    {
      monitorexit;
    }
    singleton = paramPicasso;
    monitorexit;
  }

  public static Picasso with(Context paramContext)
  {
    if (singleton == null)
      monitorenter;
    try
    {
      if (singleton == null)
        singleton = new Builder(paramContext).build();
      return singleton;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public boolean areIndicatorsEnabled()
  {
    return this.indicatorsEnabled;
  }

  public void cancelRequest(ImageView paramImageView)
  {
    cancelExistingRequest(paramImageView);
  }

  public void cancelRequest(RemoteViews paramRemoteViews, int paramInt)
  {
    cancelExistingRequest(new RemoteViewsAction.RemoteViewsTarget(paramRemoteViews, paramInt));
  }

  public void cancelRequest(Target paramTarget)
  {
    cancelExistingRequest(paramTarget);
  }

  public void cancelTag(Object paramObject)
  {
    Utils.checkMain();
    ArrayList localArrayList = new ArrayList(this.targetToAction.values());
    int i = 0;
    int j = localArrayList.size();
    while (i < j)
    {
      Action localAction = (Action)localArrayList.get(i);
      if (localAction.getTag().equals(paramObject))
        cancelExistingRequest(localAction.getTarget());
      i++;
    }
  }

  void complete(BitmapHunter paramBitmapHunter)
  {
    Action localAction = paramBitmapHunter.getAction();
    List localList = paramBitmapHunter.getActions();
    int i;
    if ((localList != null) && (!localList.isEmpty()))
    {
      i = 1;
      int j;
      if (localAction == null)
      {
        j = 0;
        if (i == 0);
      }
      else
      {
        j = 1;
      }
      if (j != 0)
        break label53;
    }
    label53: Uri localUri;
    Exception localException;
    do
    {
      return;
      i = 0;
      break;
      localUri = paramBitmapHunter.getData().uri;
      localException = paramBitmapHunter.getException();
      Bitmap localBitmap = paramBitmapHunter.getResult();
      LoadedFrom localLoadedFrom = paramBitmapHunter.getLoadedFrom();
      if (localAction != null)
        deliverAction(localBitmap, localLoadedFrom, localAction);
      if (i == 0)
        continue;
      int k = 0;
      int m = localList.size();
      while (k < m)
      {
        deliverAction(localBitmap, localLoadedFrom, (Action)localList.get(k));
        k++;
      }
    }
    while ((this.listener == null) || (localException == null));
    this.listener.onImageLoadFailed(this, localUri, localException);
  }

  void defer(ImageView paramImageView, DeferredRequestCreator paramDeferredRequestCreator)
  {
    this.targetToDeferredRequestCreator.put(paramImageView, paramDeferredRequestCreator);
  }

  void enqueueAndSubmit(Action paramAction)
  {
    Object localObject = paramAction.getTarget();
    if ((localObject != null) && (this.targetToAction.get(localObject) != paramAction))
    {
      cancelExistingRequest(localObject);
      this.targetToAction.put(localObject, paramAction);
    }
    submit(paramAction);
  }

  List<RequestHandler> getRequestHandlers()
  {
    return this.requestHandlers;
  }

  public StatsSnapshot getSnapshot()
  {
    return this.stats.createSnapshot();
  }

  public void invalidate(Uri paramUri)
  {
    if (paramUri == null)
      throw new IllegalArgumentException("uri == null");
    this.cache.clearKeyUri(paramUri.toString());
  }

  public void invalidate(File paramFile)
  {
    if (paramFile == null)
      throw new IllegalArgumentException("file == null");
    invalidate(Uri.fromFile(paramFile));
  }

  public void invalidate(String paramString)
  {
    if (paramString == null)
      throw new IllegalArgumentException("path == null");
    invalidate(Uri.parse(paramString));
  }

  @Deprecated
  public boolean isDebugging()
  {
    return (areIndicatorsEnabled()) && (isLoggingEnabled());
  }

  public boolean isLoggingEnabled()
  {
    return this.loggingEnabled;
  }

  public RequestCreator load(int paramInt)
  {
    if (paramInt == 0)
      throw new IllegalArgumentException("Resource ID must not be zero.");
    return new RequestCreator(this, null, paramInt);
  }

  public RequestCreator load(Uri paramUri)
  {
    return new RequestCreator(this, paramUri, 0);
  }

  public RequestCreator load(File paramFile)
  {
    if (paramFile == null)
      return new RequestCreator(this, null, 0);
    return load(Uri.fromFile(paramFile));
  }

  public RequestCreator load(String paramString)
  {
    if (paramString == null)
      return new RequestCreator(this, null, 0);
    if (paramString.trim().length() == 0)
      throw new IllegalArgumentException("Path must not be empty.");
    return load(Uri.parse(paramString));
  }

  public void pauseTag(Object paramObject)
  {
    this.dispatcher.dispatchPauseTag(paramObject);
  }

  Bitmap quickMemoryCacheCheck(String paramString)
  {
    Bitmap localBitmap = this.cache.get(paramString);
    if (localBitmap != null)
    {
      this.stats.dispatchCacheHit();
      return localBitmap;
    }
    this.stats.dispatchCacheMiss();
    return localBitmap;
  }

  void resumeAction(Action paramAction)
  {
    boolean bool = MemoryPolicy.shouldReadFromMemoryCache(paramAction.memoryPolicy);
    Bitmap localBitmap = null;
    if (bool)
      localBitmap = quickMemoryCacheCheck(paramAction.getKey());
    if (localBitmap != null)
    {
      deliverAction(localBitmap, LoadedFrom.MEMORY, paramAction);
      if (this.loggingEnabled)
        Utils.log("Main", "completed", paramAction.request.logId(), "from " + LoadedFrom.MEMORY);
    }
    do
    {
      return;
      enqueueAndSubmit(paramAction);
    }
    while (!this.loggingEnabled);
    Utils.log("Main", "resumed", paramAction.request.logId());
  }

  public void resumeTag(Object paramObject)
  {
    this.dispatcher.dispatchResumeTag(paramObject);
  }

  @Deprecated
  public void setDebugging(boolean paramBoolean)
  {
    setIndicatorsEnabled(paramBoolean);
  }

  public void setIndicatorsEnabled(boolean paramBoolean)
  {
    this.indicatorsEnabled = paramBoolean;
  }

  public void setLoggingEnabled(boolean paramBoolean)
  {
    this.loggingEnabled = paramBoolean;
  }

  public void shutdown()
  {
    if (this == singleton)
      throw new UnsupportedOperationException("Default singleton instance cannot be shutdown.");
    if (this.shutdown)
      return;
    this.cache.clear();
    this.cleanupThread.shutdown();
    this.stats.shutdown();
    this.dispatcher.shutdown();
    Iterator localIterator = this.targetToDeferredRequestCreator.values().iterator();
    while (localIterator.hasNext())
      ((DeferredRequestCreator)localIterator.next()).cancel();
    this.targetToDeferredRequestCreator.clear();
    this.shutdown = true;
  }

  void submit(Action paramAction)
  {
    this.dispatcher.dispatchSubmit(paramAction);
  }

  Request transformRequest(Request paramRequest)
  {
    Request localRequest = this.requestTransformer.transformRequest(paramRequest);
    if (localRequest == null)
      throw new IllegalStateException("Request transformer " + this.requestTransformer.getClass().getCanonicalName() + " returned null for " + paramRequest);
    return localRequest;
  }

  public static class Builder
  {
    private Cache cache;
    private final Context context;
    private Bitmap.Config defaultBitmapConfig;
    private Downloader downloader;
    private boolean indicatorsEnabled;
    private Picasso.Listener listener;
    private boolean loggingEnabled;
    private List<RequestHandler> requestHandlers;
    private ExecutorService service;
    private Picasso.RequestTransformer transformer;

    public Builder(Context paramContext)
    {
      if (paramContext == null)
        throw new IllegalArgumentException("Context must not be null.");
      this.context = paramContext.getApplicationContext();
    }

    public Builder addRequestHandler(RequestHandler paramRequestHandler)
    {
      if (paramRequestHandler == null)
        throw new IllegalArgumentException("RequestHandler must not be null.");
      if (this.requestHandlers == null)
        this.requestHandlers = new ArrayList();
      if (this.requestHandlers.contains(paramRequestHandler))
        throw new IllegalStateException("RequestHandler already registered.");
      this.requestHandlers.add(paramRequestHandler);
      return this;
    }

    public Picasso build()
    {
      Context localContext = this.context;
      if (this.downloader == null)
        this.downloader = Utils.createDefaultDownloader(localContext);
      if (this.cache == null)
        this.cache = new LruCache(localContext);
      if (this.service == null)
        this.service = new PicassoExecutorService();
      if (this.transformer == null)
        this.transformer = Picasso.RequestTransformer.IDENTITY;
      Stats localStats = new Stats(this.cache);
      return new Picasso(localContext, new Dispatcher(localContext, this.service, Picasso.HANDLER, this.downloader, this.cache, localStats), this.cache, this.listener, this.transformer, this.requestHandlers, localStats, this.defaultBitmapConfig, this.indicatorsEnabled, this.loggingEnabled);
    }

    @Deprecated
    public Builder debugging(boolean paramBoolean)
    {
      return indicatorsEnabled(paramBoolean);
    }

    public Builder defaultBitmapConfig(Bitmap.Config paramConfig)
    {
      if (paramConfig == null)
        throw new IllegalArgumentException("Bitmap config must not be null.");
      this.defaultBitmapConfig = paramConfig;
      return this;
    }

    public Builder downloader(Downloader paramDownloader)
    {
      if (paramDownloader == null)
        throw new IllegalArgumentException("Downloader must not be null.");
      if (this.downloader != null)
        throw new IllegalStateException("Downloader already set.");
      this.downloader = paramDownloader;
      return this;
    }

    public Builder executor(ExecutorService paramExecutorService)
    {
      if (paramExecutorService == null)
        throw new IllegalArgumentException("Executor service must not be null.");
      if (this.service != null)
        throw new IllegalStateException("Executor service already set.");
      this.service = paramExecutorService;
      return this;
    }

    public Builder indicatorsEnabled(boolean paramBoolean)
    {
      this.indicatorsEnabled = paramBoolean;
      return this;
    }

    public Builder listener(Picasso.Listener paramListener)
    {
      if (paramListener == null)
        throw new IllegalArgumentException("Listener must not be null.");
      if (this.listener != null)
        throw new IllegalStateException("Listener already set.");
      this.listener = paramListener;
      return this;
    }

    public Builder loggingEnabled(boolean paramBoolean)
    {
      this.loggingEnabled = paramBoolean;
      return this;
    }

    public Builder memoryCache(Cache paramCache)
    {
      if (paramCache == null)
        throw new IllegalArgumentException("Memory cache must not be null.");
      if (this.cache != null)
        throw new IllegalStateException("Memory cache already set.");
      this.cache = paramCache;
      return this;
    }

    public Builder requestTransformer(Picasso.RequestTransformer paramRequestTransformer)
    {
      if (paramRequestTransformer == null)
        throw new IllegalArgumentException("Transformer must not be null.");
      if (this.transformer != null)
        throw new IllegalStateException("Transformer already set.");
      this.transformer = paramRequestTransformer;
      return this;
    }
  }

  private static class CleanupThread extends Thread
  {
    private final Handler handler;
    private final ReferenceQueue<Object> referenceQueue;

    CleanupThread(ReferenceQueue<Object> paramReferenceQueue, Handler paramHandler)
    {
      this.referenceQueue = paramReferenceQueue;
      this.handler = paramHandler;
      setDaemon(true);
      setName("Picasso-refQueue");
    }

    public void run()
    {
      Process.setThreadPriority(10);
      try
      {
        while (true)
        {
          Action.RequestWeakReference localRequestWeakReference = (Action.RequestWeakReference)this.referenceQueue.remove(1000L);
          Message localMessage = this.handler.obtainMessage();
          if (localRequestWeakReference != null)
          {
            localMessage.what = 3;
            localMessage.obj = localRequestWeakReference.action;
            this.handler.sendMessage(localMessage);
            continue;
          }
          localMessage.recycle();
        }
      }
      catch (Exception localException)
      {
        this.handler.post(new Runnable(localException)
        {
          public void run()
          {
            throw new RuntimeException(this.val$e);
          }
        });
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }

    void shutdown()
    {
      interrupt();
    }
  }

  public static abstract interface Listener
  {
    public abstract void onImageLoadFailed(Picasso paramPicasso, Uri paramUri, Exception paramException);
  }

  public static enum LoadedFrom
  {
    final int debugColor;

    static
    {
      DISK = new LoadedFrom("DISK", 1, -16776961);
      NETWORK = new LoadedFrom("NETWORK", 2, -65536);
      LoadedFrom[] arrayOfLoadedFrom = new LoadedFrom[3];
      arrayOfLoadedFrom[0] = MEMORY;
      arrayOfLoadedFrom[1] = DISK;
      arrayOfLoadedFrom[2] = NETWORK;
      $VALUES = arrayOfLoadedFrom;
    }

    private LoadedFrom(int paramInt)
    {
      this.debugColor = paramInt;
    }
  }

  public static enum Priority
  {
    static
    {
      HIGH = new Priority("HIGH", 2);
      Priority[] arrayOfPriority = new Priority[3];
      arrayOfPriority[0] = LOW;
      arrayOfPriority[1] = NORMAL;
      arrayOfPriority[2] = HIGH;
      $VALUES = arrayOfPriority;
    }
  }

  public static abstract interface RequestTransformer
  {
    public static final RequestTransformer IDENTITY = new RequestTransformer()
    {
      public Request transformRequest(Request paramRequest)
      {
        return paramRequest;
      }
    };

    public abstract Request transformRequest(Request paramRequest);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.Picasso
 * JD-Core Version:    0.6.0
 */