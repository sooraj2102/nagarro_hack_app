package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.NetworkInfo;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

class BitmapHunter
  implements Runnable
{
  private static final Object DECODE_LOCK = new Object();
  private static final RequestHandler ERRORING_HANDLER;
  private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal()
  {
    protected StringBuilder initialValue()
    {
      return new StringBuilder("Picasso-");
    }
  };
  private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
  Action action;
  List<Action> actions;
  final Cache cache;
  final Request data;
  final Dispatcher dispatcher;
  Exception exception;
  int exifRotation;
  Future<?> future;
  final String key;
  Picasso.LoadedFrom loadedFrom;
  final int memoryPolicy;
  int networkPolicy;
  final Picasso picasso;
  Picasso.Priority priority;
  final RequestHandler requestHandler;
  Bitmap result;
  int retryCount;
  final int sequence = SEQUENCE_GENERATOR.incrementAndGet();
  final Stats stats;

  static
  {
    ERRORING_HANDLER = new RequestHandler()
    {
      public boolean canHandleRequest(Request paramRequest)
      {
        return true;
      }

      public RequestHandler.Result load(Request paramRequest, int paramInt)
        throws IOException
      {
        throw new IllegalStateException("Unrecognized type of request: " + paramRequest);
      }
    };
  }

  BitmapHunter(Picasso paramPicasso, Dispatcher paramDispatcher, Cache paramCache, Stats paramStats, Action paramAction, RequestHandler paramRequestHandler)
  {
    this.picasso = paramPicasso;
    this.dispatcher = paramDispatcher;
    this.cache = paramCache;
    this.stats = paramStats;
    this.action = paramAction;
    this.key = paramAction.getKey();
    this.data = paramAction.getRequest();
    this.priority = paramAction.getPriority();
    this.memoryPolicy = paramAction.getMemoryPolicy();
    this.networkPolicy = paramAction.getNetworkPolicy();
    this.requestHandler = paramRequestHandler;
    this.retryCount = paramRequestHandler.getRetryCount();
  }

  static Bitmap applyCustomTransformations(List<Transformation> paramList, Bitmap paramBitmap)
  {
    int i = 0;
    int j = paramList.size();
    while (true)
    {
      Transformation localTransformation;
      Bitmap localBitmap;
      StringBuilder localStringBuilder;
      if (i < j)
      {
        localTransformation = (Transformation)paramList.get(i);
        try
        {
          localBitmap = localTransformation.transform(paramBitmap);
          if (localBitmap != null)
            break label168;
          localStringBuilder = new StringBuilder().append("Transformation ").append(localTransformation.key()).append(" returned null after ").append(i).append(" previous transformation(s).\n\nTransformation list:\n");
          Iterator localIterator = paramList.iterator();
          while (localIterator.hasNext())
            localStringBuilder.append(((Transformation)localIterator.next()).key()).append('\n');
        }
        catch (RuntimeException localRuntimeException)
        {
          Picasso.HANDLER.post(new Runnable(localTransformation, localRuntimeException)
          {
            public void run()
            {
              throw new RuntimeException("Transformation " + this.val$transformation.key() + " crashed with exception.", this.val$e);
            }
          });
          paramBitmap = null;
        }
      }
      else
      {
        return paramBitmap;
      }
      Picasso.HANDLER.post(new Runnable(localStringBuilder)
      {
        public void run()
        {
          throw new NullPointerException(this.val$builder.toString());
        }
      });
      return null;
      label168: if ((localBitmap == paramBitmap) && (paramBitmap.isRecycled()))
      {
        Picasso.HANDLER.post(new Runnable(localTransformation)
        {
          public void run()
          {
            throw new IllegalStateException("Transformation " + this.val$transformation.key() + " returned input Bitmap but recycled it.");
          }
        });
        return null;
      }
      if ((localBitmap != paramBitmap) && (!paramBitmap.isRecycled()))
      {
        Picasso.HANDLER.post(new Runnable(localTransformation)
        {
          public void run()
          {
            throw new IllegalStateException("Transformation " + this.val$transformation.key() + " mutated input Bitmap but failed to recycle the original.");
          }
        });
        return null;
      }
      paramBitmap = localBitmap;
      i++;
    }
  }

  private Picasso.Priority computeNewPriority()
  {
    Object localObject = Picasso.Priority.LOW;
    int i;
    if ((this.actions != null) && (!this.actions.isEmpty()))
    {
      i = 1;
      if ((this.action == null) && (i == 0))
        break label49;
    }
    label49: for (int j = 1; ; j = 0)
    {
      if (j != 0)
        break label54;
      return localObject;
      i = 0;
      break;
    }
    label54: if (this.action != null)
      localObject = this.action.getPriority();
    if (i != 0)
    {
      int k = 0;
      int m = this.actions.size();
      while (k < m)
      {
        Picasso.Priority localPriority = ((Action)this.actions.get(k)).getPriority();
        if (localPriority.ordinal() > ((Picasso.Priority)localObject).ordinal())
          localObject = localPriority;
        k++;
      }
    }
    return (Picasso.Priority)localObject;
  }

  static Bitmap decodeStream(InputStream paramInputStream, Request paramRequest)
    throws IOException
  {
    MarkableInputStream localMarkableInputStream = new MarkableInputStream(paramInputStream);
    long l = localMarkableInputStream.savePosition(65536);
    BitmapFactory.Options localOptions = RequestHandler.createBitmapOptions(paramRequest);
    boolean bool1 = RequestHandler.requiresInSampleSize(localOptions);
    boolean bool2 = Utils.isWebPFile(localMarkableInputStream);
    localMarkableInputStream.reset(l);
    Bitmap localBitmap;
    if (bool2)
    {
      byte[] arrayOfByte = Utils.toByteArray(localMarkableInputStream);
      if (bool1)
      {
        BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, localOptions);
        RequestHandler.calculateInSampleSize(paramRequest.targetWidth, paramRequest.targetHeight, localOptions, paramRequest);
      }
      localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, localOptions);
    }
    do
    {
      return localBitmap;
      if (bool1)
      {
        BitmapFactory.decodeStream(localMarkableInputStream, null, localOptions);
        RequestHandler.calculateInSampleSize(paramRequest.targetWidth, paramRequest.targetHeight, localOptions, paramRequest);
        localMarkableInputStream.reset(l);
      }
      localBitmap = BitmapFactory.decodeStream(localMarkableInputStream, null, localOptions);
    }
    while (localBitmap != null);
    throw new IOException("Failed to decode stream.");
  }

  static BitmapHunter forRequest(Picasso paramPicasso, Dispatcher paramDispatcher, Cache paramCache, Stats paramStats, Action paramAction)
  {
    Request localRequest = paramAction.getRequest();
    List localList = paramPicasso.getRequestHandlers();
    int i = 0;
    int j = localList.size();
    while (i < j)
    {
      RequestHandler localRequestHandler = (RequestHandler)localList.get(i);
      if (localRequestHandler.canHandleRequest(localRequest))
        return new BitmapHunter(paramPicasso, paramDispatcher, paramCache, paramStats, paramAction, localRequestHandler);
      i++;
    }
    return new BitmapHunter(paramPicasso, paramDispatcher, paramCache, paramStats, paramAction, ERRORING_HANDLER);
  }

  private static boolean shouldResize(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return (!paramBoolean) || (paramInt1 > paramInt3) || (paramInt2 > paramInt4);
  }

  static Bitmap transformResult(Request paramRequest, Bitmap paramBitmap, int paramInt)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    boolean bool1 = paramRequest.onlyScaleDown;
    int k = i;
    int m = j;
    Matrix localMatrix = new Matrix();
    boolean bool2 = paramRequest.needsMatrixTransform();
    int n = 0;
    int i1 = 0;
    int i2;
    int i3;
    float f1;
    float f7;
    float f8;
    float f9;
    float f10;
    if (bool2)
    {
      i2 = paramRequest.targetWidth;
      i3 = paramRequest.targetHeight;
      f1 = paramRequest.rotationDegrees;
      if (f1 != 0.0F)
      {
        if (!paramRequest.hasRotationPivot)
          break label240;
        localMatrix.setRotate(f1, paramRequest.rotationPivotX, paramRequest.rotationPivotY);
      }
      if (!paramRequest.centerCrop)
        break label296;
      f7 = i2 / i;
      f8 = i3 / j;
      if (f7 <= f8)
        break label250;
      int i5 = (int)Math.ceil(j * (f8 / f7));
      i1 = (j - i5) / 2;
      m = i5;
      f9 = f7;
      f10 = i3 / m;
      label171: if (shouldResize(bool1, i, j, i2, i3))
        localMatrix.preScale(f9, f10);
    }
    label240: label250: 
    do
    {
      do
      {
        if (paramInt != 0)
          localMatrix.preRotate(paramInt);
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, n, i1, k, m, localMatrix, true);
        if (localBitmap != paramBitmap)
        {
          paramBitmap.recycle();
          paramBitmap = localBitmap;
        }
        return paramBitmap;
        localMatrix.setRotate(f1);
        break;
        int i4 = (int)Math.ceil(i * (f7 / f8));
        n = (i - i4) / 2;
        k = i4;
        f9 = i2 / k;
        f10 = f8;
        i1 = 0;
        break label171;
        if (paramRequest.centerInside)
        {
          float f4 = i2 / i;
          float f5 = i3 / j;
          float f6;
          if (f4 < f5)
            f6 = f4;
          while (true)
          {
            boolean bool4 = shouldResize(bool1, i, j, i2, i3);
            n = 0;
            i1 = 0;
            if (!bool4)
              break;
            localMatrix.preScale(f6, f6);
            n = 0;
            i1 = 0;
            break;
            f6 = f5;
          }
        }
        if (i2 != 0)
          break label399;
        n = 0;
        i1 = 0;
      }
      while (i3 == 0);
      if (i2 != i)
        break label418;
      n = 0;
      i1 = 0;
    }
    while (i3 == j);
    label296: float f2;
    label399: label418: label431: float f3;
    if (i2 != 0)
    {
      f2 = i2 / i;
      if (i3 == 0)
        break label501;
      f3 = i3 / j;
    }
    while (true)
    {
      boolean bool3 = shouldResize(bool1, i, j, i2, i3);
      n = 0;
      i1 = 0;
      if (!bool3)
        break;
      localMatrix.preScale(f2, f3);
      n = 0;
      i1 = 0;
      break;
      f2 = i3 / j;
      break label431;
      label501: f3 = i2 / i;
    }
  }

  static void updateThreadName(Request paramRequest)
  {
    String str = paramRequest.getName();
    StringBuilder localStringBuilder = (StringBuilder)NAME_BUILDER.get();
    localStringBuilder.ensureCapacity("Picasso-".length() + str.length());
    localStringBuilder.replace("Picasso-".length(), localStringBuilder.length(), str);
    Thread.currentThread().setName(localStringBuilder.toString());
  }

  void attach(Action paramAction)
  {
    boolean bool = this.picasso.loggingEnabled;
    Request localRequest = paramAction.request;
    if (this.action == null)
    {
      this.action = paramAction;
      if (bool)
      {
        if ((this.actions != null) && (!this.actions.isEmpty()))
          break label65;
        Utils.log("Hunter", "joined", localRequest.logId(), "to empty hunter");
      }
    }
    label65: Picasso.Priority localPriority;
    do
    {
      return;
      Utils.log("Hunter", "joined", localRequest.logId(), Utils.getLogIdsForHunter(this, "to "));
      return;
      if (this.actions == null)
        this.actions = new ArrayList(3);
      this.actions.add(paramAction);
      if (bool)
        Utils.log("Hunter", "joined", localRequest.logId(), Utils.getLogIdsForHunter(this, "to "));
      localPriority = paramAction.getPriority();
    }
    while (localPriority.ordinal() <= this.priority.ordinal());
    this.priority = localPriority;
  }

  boolean cancel()
  {
    Action localAction = this.action;
    int i = 0;
    if (localAction == null)
      if (this.actions != null)
      {
        boolean bool2 = this.actions.isEmpty();
        i = 0;
        if (!bool2);
      }
      else
      {
        Future localFuture = this.future;
        i = 0;
        if (localFuture != null)
        {
          boolean bool1 = this.future.cancel(false);
          i = 0;
          if (bool1)
            i = 1;
        }
      }
    return i;
  }

  void detach(Action paramAction)
  {
    boolean bool;
    if (this.action == paramAction)
    {
      this.action = null;
      bool = true;
    }
    while (true)
    {
      if ((bool) && (paramAction.getPriority() == this.priority))
        this.priority = computeNewPriority();
      if (this.picasso.loggingEnabled)
        Utils.log("Hunter", "removed", paramAction.request.logId(), Utils.getLogIdsForHunter(this, "from "));
      return;
      List localList = this.actions;
      bool = false;
      if (localList == null)
        continue;
      bool = this.actions.remove(paramAction);
    }
  }

  Action getAction()
  {
    return this.action;
  }

  List<Action> getActions()
  {
    return this.actions;
  }

  Request getData()
  {
    return this.data;
  }

  Exception getException()
  {
    return this.exception;
  }

  String getKey()
  {
    return this.key;
  }

  Picasso.LoadedFrom getLoadedFrom()
  {
    return this.loadedFrom;
  }

  int getMemoryPolicy()
  {
    return this.memoryPolicy;
  }

  Picasso getPicasso()
  {
    return this.picasso;
  }

  Picasso.Priority getPriority()
  {
    return this.priority;
  }

  Bitmap getResult()
  {
    return this.result;
  }

  // ERROR //
  Bitmap hunt()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 114	com/squareup/picasso/BitmapHunter:memoryPolicy	I
    //   4: invokestatic 489	com/squareup/picasso/MemoryPolicy:shouldReadFromMemoryCache	(I)Z
    //   7: istore_1
    //   8: aconst_null
    //   9: astore_2
    //   10: iload_1
    //   11: ifeq +66 -> 77
    //   14: aload_0
    //   15: getfield 85	com/squareup/picasso/BitmapHunter:cache	Lcom/squareup/picasso/Cache;
    //   18: aload_0
    //   19: getfield 97	com/squareup/picasso/BitmapHunter:key	Ljava/lang/String;
    //   22: invokeinterface 494 2 0
    //   27: astore_2
    //   28: aload_2
    //   29: ifnull +48 -> 77
    //   32: aload_0
    //   33: getfield 87	com/squareup/picasso/BitmapHunter:stats	Lcom/squareup/picasso/Stats;
    //   36: invokevirtual 499	com/squareup/picasso/Stats:dispatchCacheHit	()V
    //   39: aload_0
    //   40: getstatic 504	com/squareup/picasso/Picasso$LoadedFrom:MEMORY	Lcom/squareup/picasso/Picasso$LoadedFrom;
    //   43: putfield 476	com/squareup/picasso/BitmapHunter:loadedFrom	Lcom/squareup/picasso/Picasso$LoadedFrom;
    //   46: aload_0
    //   47: getfield 81	com/squareup/picasso/BitmapHunter:picasso	Lcom/squareup/picasso/Picasso;
    //   50: getfield 416	com/squareup/picasso/Picasso:loggingEnabled	Z
    //   53: ifeq +22 -> 75
    //   56: ldc_w 421
    //   59: ldc_w 506
    //   62: aload_0
    //   63: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   66: invokevirtual 426	com/squareup/picasso/Request:logId	()Ljava/lang/String;
    //   69: ldc_w 508
    //   72: invokestatic 432	com/squareup/picasso/Utils:log	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   75: aload_2
    //   76: areturn
    //   77: aload_0
    //   78: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   81: astore_3
    //   82: aload_0
    //   83: getfield 128	com/squareup/picasso/BitmapHunter:retryCount	I
    //   86: ifne +280 -> 366
    //   89: getstatic 514	com/squareup/picasso/NetworkPolicy:OFFLINE	Lcom/squareup/picasso/NetworkPolicy;
    //   92: getfield 517	com/squareup/picasso/NetworkPolicy:index	I
    //   95: istore 4
    //   97: aload_3
    //   98: iload 4
    //   100: putfield 518	com/squareup/picasso/Request:networkPolicy	I
    //   103: aload_0
    //   104: getfield 121	com/squareup/picasso/BitmapHunter:requestHandler	Lcom/squareup/picasso/RequestHandler;
    //   107: aload_0
    //   108: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   111: aload_0
    //   112: getfield 119	com/squareup/picasso/BitmapHunter:networkPolicy	I
    //   115: invokevirtual 522	com/squareup/picasso/RequestHandler:load	(Lcom/squareup/picasso/Request;I)Lcom/squareup/picasso/RequestHandler$Result;
    //   118: astore 5
    //   120: aload 5
    //   122: ifnull +57 -> 179
    //   125: aload_0
    //   126: aload 5
    //   128: invokevirtual 526	com/squareup/picasso/RequestHandler$Result:getLoadedFrom	()Lcom/squareup/picasso/Picasso$LoadedFrom;
    //   131: putfield 476	com/squareup/picasso/BitmapHunter:loadedFrom	Lcom/squareup/picasso/Picasso$LoadedFrom;
    //   134: aload_0
    //   135: aload 5
    //   137: invokevirtual 529	com/squareup/picasso/RequestHandler$Result:getExifOrientation	()I
    //   140: putfield 531	com/squareup/picasso/BitmapHunter:exifRotation	I
    //   143: aload 5
    //   145: invokevirtual 534	com/squareup/picasso/RequestHandler$Result:getBitmap	()Landroid/graphics/Bitmap;
    //   148: astore_2
    //   149: aload_2
    //   150: ifnonnull +29 -> 179
    //   153: aload 5
    //   155: invokevirtual 538	com/squareup/picasso/RequestHandler$Result:getStream	()Ljava/io/InputStream;
    //   158: astore 8
    //   160: aload 8
    //   162: aload_0
    //   163: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   166: invokestatic 540	com/squareup/picasso/BitmapHunter:decodeStream	(Ljava/io/InputStream;Lcom/squareup/picasso/Request;)Landroid/graphics/Bitmap;
    //   169: astore 10
    //   171: aload 10
    //   173: astore_2
    //   174: aload 8
    //   176: invokestatic 543	com/squareup/picasso/Utils:closeQuietly	(Ljava/io/InputStream;)V
    //   179: aload_2
    //   180: ifnull +184 -> 364
    //   183: aload_0
    //   184: getfield 81	com/squareup/picasso/BitmapHunter:picasso	Lcom/squareup/picasso/Picasso;
    //   187: getfield 416	com/squareup/picasso/Picasso:loggingEnabled	Z
    //   190: ifeq +19 -> 209
    //   193: ldc_w 421
    //   196: ldc_w 506
    //   199: aload_0
    //   200: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   203: invokevirtual 426	com/squareup/picasso/Request:logId	()Ljava/lang/String;
    //   206: invokestatic 546	com/squareup/picasso/Utils:log	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   209: aload_0
    //   210: getfield 87	com/squareup/picasso/BitmapHunter:stats	Lcom/squareup/picasso/Stats;
    //   213: aload_2
    //   214: invokevirtual 550	com/squareup/picasso/Stats:dispatchBitmapDecoded	(Landroid/graphics/Bitmap;)V
    //   217: aload_0
    //   218: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   221: invokevirtual 553	com/squareup/picasso/Request:needsTransformation	()Z
    //   224: ifne +10 -> 234
    //   227: aload_0
    //   228: getfield 531	com/squareup/picasso/BitmapHunter:exifRotation	I
    //   231: ifeq +133 -> 364
    //   234: getstatic 57	com/squareup/picasso/BitmapHunter:DECODE_LOCK	Ljava/lang/Object;
    //   237: astore 6
    //   239: aload 6
    //   241: monitorenter
    //   242: aload_0
    //   243: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   246: invokevirtual 325	com/squareup/picasso/Request:needsMatrixTransform	()Z
    //   249: ifne +10 -> 259
    //   252: aload_0
    //   253: getfield 531	com/squareup/picasso/BitmapHunter:exifRotation	I
    //   256: ifeq +42 -> 298
    //   259: aload_0
    //   260: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   263: aload_2
    //   264: aload_0
    //   265: getfield 531	com/squareup/picasso/BitmapHunter:exifRotation	I
    //   268: invokestatic 555	com/squareup/picasso/BitmapHunter:transformResult	(Lcom/squareup/picasso/Request;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
    //   271: astore_2
    //   272: aload_0
    //   273: getfield 81	com/squareup/picasso/BitmapHunter:picasso	Lcom/squareup/picasso/Picasso;
    //   276: getfield 416	com/squareup/picasso/Picasso:loggingEnabled	Z
    //   279: ifeq +19 -> 298
    //   282: ldc_w 421
    //   285: ldc_w 557
    //   288: aload_0
    //   289: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   292: invokevirtual 426	com/squareup/picasso/Request:logId	()Ljava/lang/String;
    //   295: invokestatic 546	com/squareup/picasso/Utils:log	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   298: aload_0
    //   299: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   302: invokevirtual 560	com/squareup/picasso/Request:hasCustomTransformations	()Z
    //   305: ifeq +44 -> 349
    //   308: aload_0
    //   309: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   312: getfield 563	com/squareup/picasso/Request:transformations	Ljava/util/List;
    //   315: aload_2
    //   316: invokestatic 565	com/squareup/picasso/BitmapHunter:applyCustomTransformations	(Ljava/util/List;Landroid/graphics/Bitmap;)Landroid/graphics/Bitmap;
    //   319: astore_2
    //   320: aload_0
    //   321: getfield 81	com/squareup/picasso/BitmapHunter:picasso	Lcom/squareup/picasso/Picasso;
    //   324: getfield 416	com/squareup/picasso/Picasso:loggingEnabled	Z
    //   327: ifeq +22 -> 349
    //   330: ldc_w 421
    //   333: ldc_w 557
    //   336: aload_0
    //   337: getfield 103	com/squareup/picasso/BitmapHunter:data	Lcom/squareup/picasso/Request;
    //   340: invokevirtual 426	com/squareup/picasso/Request:logId	()Ljava/lang/String;
    //   343: ldc_w 567
    //   346: invokestatic 432	com/squareup/picasso/Utils:log	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   349: aload 6
    //   351: monitorexit
    //   352: aload_2
    //   353: ifnull +11 -> 364
    //   356: aload_0
    //   357: getfield 87	com/squareup/picasso/BitmapHunter:stats	Lcom/squareup/picasso/Stats;
    //   360: aload_2
    //   361: invokevirtual 570	com/squareup/picasso/Stats:dispatchBitmapTransformed	(Landroid/graphics/Bitmap;)V
    //   364: aload_2
    //   365: areturn
    //   366: aload_0
    //   367: getfield 119	com/squareup/picasso/BitmapHunter:networkPolicy	I
    //   370: istore 4
    //   372: goto -275 -> 97
    //   375: astore 9
    //   377: aload 8
    //   379: invokestatic 543	com/squareup/picasso/Utils:closeQuietly	(Ljava/io/InputStream;)V
    //   382: aload 9
    //   384: athrow
    //   385: astore 7
    //   387: aload 6
    //   389: monitorexit
    //   390: aload 7
    //   392: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   160	171	375	finally
    //   242	259	385	finally
    //   259	298	385	finally
    //   298	349	385	finally
    //   349	352	385	finally
    //   387	390	385	finally
  }

  boolean isCancelled()
  {
    return (this.future != null) && (this.future.isCancelled());
  }

  public void run()
  {
    try
    {
      updateThreadName(this.data);
      if (this.picasso.loggingEnabled)
        Utils.log("Hunter", "executing", Utils.getLogIdsForHunter(this));
      this.result = hunt();
      if (this.result == null)
        this.dispatcher.dispatchFailed(this);
      while (true)
      {
        return;
        this.dispatcher.dispatchComplete(this);
      }
    }
    catch (Downloader.ResponseException localResponseException)
    {
      if ((!localResponseException.localCacheOnly) || (localResponseException.responseCode != 504))
        this.exception = localResponseException;
      this.dispatcher.dispatchFailed(this);
      return;
    }
    catch (NetworkRequestHandler.ContentLengthException localContentLengthException)
    {
      this.exception = localContentLengthException;
      this.dispatcher.dispatchRetry(this);
      return;
    }
    catch (IOException localIOException)
    {
      this.exception = localIOException;
      this.dispatcher.dispatchRetry(this);
      return;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      StringWriter localStringWriter = new StringWriter();
      this.stats.createSnapshot().dump(new PrintWriter(localStringWriter));
      this.exception = new RuntimeException(localStringWriter.toString(), localOutOfMemoryError);
      this.dispatcher.dispatchFailed(this);
      return;
    }
    catch (Exception localException)
    {
      this.exception = localException;
      this.dispatcher.dispatchFailed(this);
      return;
    }
    finally
    {
      Thread.currentThread().setName("Picasso-Idle");
    }
    throw localObject;
  }

  boolean shouldRetry(boolean paramBoolean, NetworkInfo paramNetworkInfo)
  {
    if (this.retryCount > 0);
    for (int i = 1; i == 0; i = 0)
      return false;
    this.retryCount = (-1 + this.retryCount);
    return this.requestHandler.shouldRetry(paramBoolean, paramNetworkInfo);
  }

  boolean supportsReplay()
  {
    return this.requestHandler.supportsReplay();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.squareup.picasso.BitmapHunter
 * JD-Core Version:    0.6.0
 */