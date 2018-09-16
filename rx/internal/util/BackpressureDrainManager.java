package rx.internal.util;

import java.util.concurrent.atomic.AtomicLong;
import rx.Producer;
import rx.annotations.Experimental;

@Experimental
public final class BackpressureDrainManager extends AtomicLong
  implements Producer
{
  private static final long serialVersionUID = 2826241102729529449L;
  final BackpressureQueueCallback actual;
  boolean emitting;
  Throwable exception;
  volatile boolean terminated;

  public BackpressureDrainManager(BackpressureQueueCallback paramBackpressureQueueCallback)
  {
    this.actual = paramBackpressureQueueCallback;
  }

  // ERROR //
  public void drain()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   6: ifeq +6 -> 12
    //   9: aload_0
    //   10: monitorexit
    //   11: return
    //   12: aload_0
    //   13: iconst_1
    //   14: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   17: aload_0
    //   18: getfield 30	rx/internal/util/BackpressureDrainManager:terminated	Z
    //   21: istore_2
    //   22: aload_0
    //   23: monitorexit
    //   24: aload_0
    //   25: invokevirtual 34	rx/internal/util/BackpressureDrainManager:get	()J
    //   28: lstore_3
    //   29: iconst_0
    //   30: istore 5
    //   32: aload_0
    //   33: getfield 25	rx/internal/util/BackpressureDrainManager:actual	Lrx/internal/util/BackpressureDrainManager$BackpressureQueueCallback;
    //   36: astore 8
    //   38: goto +311 -> 349
    //   41: iconst_0
    //   42: istore 5
    //   44: iload_2
    //   45: ifeq +132 -> 177
    //   48: aload 8
    //   50: invokeinterface 40 1 0
    //   55: ifnonnull +44 -> 99
    //   58: iconst_1
    //   59: istore 5
    //   61: aload 8
    //   63: aload_0
    //   64: getfield 42	rx/internal/util/BackpressureDrainManager:exception	Ljava/lang/Throwable;
    //   67: invokeinterface 46 2 0
    //   72: iload 5
    //   74: ifne +274 -> 348
    //   77: aload_0
    //   78: monitorenter
    //   79: aload_0
    //   80: iconst_0
    //   81: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   84: aload_0
    //   85: monitorexit
    //   86: return
    //   87: astore 10
    //   89: aload_0
    //   90: monitorexit
    //   91: aload 10
    //   93: athrow
    //   94: astore_1
    //   95: aload_0
    //   96: monitorexit
    //   97: aload_1
    //   98: athrow
    //   99: lload_3
    //   100: lconst_0
    //   101: lcmp
    //   102: ifne +75 -> 177
    //   105: aload_0
    //   106: monitorenter
    //   107: aload_0
    //   108: getfield 30	rx/internal/util/BackpressureDrainManager:terminated	Z
    //   111: istore_2
    //   112: aload 8
    //   114: invokeinterface 40 1 0
    //   119: ifnull +122 -> 241
    //   122: iconst_1
    //   123: istore 15
    //   125: aload_0
    //   126: invokevirtual 34	rx/internal/util/BackpressureDrainManager:get	()J
    //   129: ldc2_w 47
    //   132: lcmp
    //   133: ifne +149 -> 282
    //   136: iload 15
    //   138: ifne +109 -> 247
    //   141: iload_2
    //   142: ifne +105 -> 247
    //   145: iconst_1
    //   146: istore 5
    //   148: aload_0
    //   149: iconst_0
    //   150: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   153: aload_0
    //   154: monitorexit
    //   155: iload 5
    //   157: ifne +191 -> 348
    //   160: aload_0
    //   161: monitorenter
    //   162: aload_0
    //   163: iconst_0
    //   164: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   167: aload_0
    //   168: monitorexit
    //   169: return
    //   170: astore 16
    //   172: aload_0
    //   173: monitorexit
    //   174: aload 16
    //   176: athrow
    //   177: aload 8
    //   179: invokeinterface 51 1 0
    //   184: astore 11
    //   186: iconst_0
    //   187: istore 5
    //   189: aload 11
    //   191: ifnull -86 -> 105
    //   194: aload 8
    //   196: aload 11
    //   198: invokeinterface 55 2 0
    //   203: istore 12
    //   205: iload 12
    //   207: ifeq +24 -> 231
    //   210: iconst_1
    //   211: ifne +137 -> 348
    //   214: aload_0
    //   215: monitorenter
    //   216: aload_0
    //   217: iconst_0
    //   218: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   221: aload_0
    //   222: monitorexit
    //   223: return
    //   224: astore 13
    //   226: aload_0
    //   227: monitorexit
    //   228: aload 13
    //   230: athrow
    //   231: lload_3
    //   232: lconst_1
    //   233: lsub
    //   234: lstore_3
    //   235: iinc 9 1
    //   238: goto +114 -> 352
    //   241: iconst_0
    //   242: istore 15
    //   244: goto -119 -> 125
    //   247: ldc2_w 47
    //   250: lstore_3
    //   251: aload_0
    //   252: monitorexit
    //   253: goto +96 -> 349
    //   256: astore 14
    //   258: aload_0
    //   259: monitorexit
    //   260: aload 14
    //   262: athrow
    //   263: astore 6
    //   265: iload 5
    //   267: ifne +12 -> 279
    //   270: aload_0
    //   271: monitorenter
    //   272: aload_0
    //   273: iconst_0
    //   274: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   277: aload_0
    //   278: monitorexit
    //   279: aload 6
    //   281: athrow
    //   282: iload 9
    //   284: ineg
    //   285: i2l
    //   286: lstore 17
    //   288: aload_0
    //   289: lload 17
    //   291: invokevirtual 59	rx/internal/util/BackpressureDrainManager:addAndGet	(J)J
    //   294: lstore_3
    //   295: lload_3
    //   296: lconst_0
    //   297: lcmp
    //   298: ifeq +67 -> 365
    //   301: iload 15
    //   303: ifne -52 -> 251
    //   306: goto +59 -> 365
    //   309: iconst_1
    //   310: istore 5
    //   312: aload_0
    //   313: iconst_0
    //   314: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   317: aload_0
    //   318: monitorexit
    //   319: iload 5
    //   321: ifne +27 -> 348
    //   324: aload_0
    //   325: monitorenter
    //   326: aload_0
    //   327: iconst_0
    //   328: putfield 28	rx/internal/util/BackpressureDrainManager:emitting	Z
    //   331: aload_0
    //   332: monitorexit
    //   333: return
    //   334: astore 19
    //   336: aload_0
    //   337: monitorexit
    //   338: aload 19
    //   340: athrow
    //   341: astore 7
    //   343: aload_0
    //   344: monitorexit
    //   345: aload 7
    //   347: athrow
    //   348: return
    //   349: iconst_0
    //   350: istore 9
    //   352: lload_3
    //   353: lconst_0
    //   354: lcmp
    //   355: ifgt -314 -> 41
    //   358: iload_2
    //   359: ifeq -254 -> 105
    //   362: goto -321 -> 41
    //   365: iload_2
    //   366: ifeq -57 -> 309
    //   369: iload 15
    //   371: ifeq -120 -> 251
    //   374: goto -65 -> 309
    //
    // Exception table:
    //   from	to	target	type
    //   79	86	87	finally
    //   89	91	87	finally
    //   2	11	94	finally
    //   12	24	94	finally
    //   95	97	94	finally
    //   162	169	170	finally
    //   172	174	170	finally
    //   216	223	224	finally
    //   226	228	224	finally
    //   107	122	256	finally
    //   125	136	256	finally
    //   148	155	256	finally
    //   251	253	256	finally
    //   258	260	256	finally
    //   288	295	256	finally
    //   312	319	256	finally
    //   32	38	263	finally
    //   48	58	263	finally
    //   61	72	263	finally
    //   105	107	263	finally
    //   177	186	263	finally
    //   194	205	263	finally
    //   260	263	263	finally
    //   326	333	334	finally
    //   336	338	334	finally
    //   272	279	341	finally
    //   343	345	341	finally
  }

  public boolean isTerminated()
  {
    return this.terminated;
  }

  public void request(long paramLong)
  {
    if (paramLong == 0L)
      return;
    label29: label72: label96: 
    while (true)
    {
      long l1 = get();
      int i;
      if (l1 == 0L)
        i = 1;
      long l2;
      while (true)
        if (l1 == 9223372036854775807L)
        {
          if (i == 0)
            break;
          drain();
          return;
          i = 0;
          continue;
        }
        else
        {
          if (paramLong != 9223372036854775807L)
            break label72;
          l2 = paramLong;
          i = 1;
        }
      while (true)
      {
        if (!compareAndSet(l1, l2))
          break label96;
        break label29;
        break;
        if (l1 > 9223372036854775807L - paramLong)
        {
          l2 = 9223372036854775807L;
          continue;
        }
        l2 = l1 + paramLong;
      }
    }
  }

  public void terminate()
  {
    this.terminated = true;
  }

  public void terminate(Throwable paramThrowable)
  {
    if (!this.terminated)
    {
      this.exception = paramThrowable;
      this.terminated = true;
    }
  }

  public void terminateAndDrain()
  {
    this.terminated = true;
    drain();
  }

  public void terminateAndDrain(Throwable paramThrowable)
  {
    if (!this.terminated)
    {
      this.exception = paramThrowable;
      this.terminated = true;
      drain();
    }
  }

  public static abstract interface BackpressureQueueCallback
  {
    public abstract boolean accept(Object paramObject);

    public abstract void complete(Throwable paramThrowable);

    public abstract Object peek();

    public abstract Object poll();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.util.BackpressureDrainManager
 * JD-Core Version:    0.6.0
 */