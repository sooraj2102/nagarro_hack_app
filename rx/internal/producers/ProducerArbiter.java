package rx.internal.producers;

import rx.Producer;

public final class ProducerArbiter
  implements Producer
{
  static final Producer NULL_PRODUCER = new Producer()
  {
    public void request(long paramLong)
    {
    }
  };
  Producer currentProducer;
  boolean emitting;
  long missedProduced;
  Producer missedProducer;
  long missedRequested;
  long requested;

  public void emitLoop()
  {
    while (true)
    {
      monitorenter;
      long l1;
      Producer localProducer1;
      long l3;
      while (true)
      {
        long l2;
        long l4;
        try
        {
          l1 = this.missedRequested;
          l2 = this.missedProduced;
          localProducer1 = this.missedProducer;
          if ((l1 != 0L) || (l2 != 0L) || (localProducer1 != null))
            continue;
          this.emitting = false;
          return;
          this.missedRequested = 0L;
          this.missedProduced = 0L;
          this.missedProducer = null;
          monitorexit;
          l3 = this.requested;
          if (l3 == 9223372036854775807L)
            continue;
          l4 = l3 + l1;
          if ((l4 < 0L) || (l4 == 9223372036854775807L))
          {
            l3 = 9223372036854775807L;
            this.requested = l3;
            if (localProducer1 == null)
              break label191;
            if (localProducer1 != NULL_PRODUCER)
              break label173;
            this.currentProducer = null;
            break;
          }
        }
        finally
        {
          monitorexit;
        }
        long l5 = l4 - l2;
        if (l5 < 0L)
          throw new IllegalStateException("more produced than requested");
        l3 = l5;
        this.requested = l5;
      }
      label173: this.currentProducer = localProducer1;
      localProducer1.request(l3);
      continue;
      label191: Producer localProducer2 = this.currentProducer;
      if ((localProducer2 == null) || (l1 == 0L))
        continue;
      localProducer2.request(l1);
    }
  }

  // ERROR //
  public void produced(long paramLong)
  {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifgt +13 -> 16
    //   6: new 55	java/lang/IllegalArgumentException
    //   9: dup
    //   10: ldc 57
    //   12: invokespecial 58	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   15: athrow
    //   16: aload_0
    //   17: monitorenter
    //   18: aload_0
    //   19: getfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   22: ifeq +16 -> 38
    //   25: aload_0
    //   26: lload_1
    //   27: aload_0
    //   28: getfield 31	rx/internal/producers/ProducerArbiter:missedProduced	J
    //   31: ladd
    //   32: putfield 31	rx/internal/producers/ProducerArbiter:missedProduced	J
    //   35: aload_0
    //   36: monitorexit
    //   37: return
    //   38: aload_0
    //   39: iconst_1
    //   40: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   43: aload_0
    //   44: monitorexit
    //   45: aload_0
    //   46: getfield 37	rx/internal/producers/ProducerArbiter:requested	J
    //   49: lstore 6
    //   51: lload 6
    //   53: ldc2_w 38
    //   56: lcmp
    //   57: ifeq +55 -> 112
    //   60: lload 6
    //   62: lload_1
    //   63: lsub
    //   64: lstore 9
    //   66: lload 9
    //   68: lconst_0
    //   69: lcmp
    //   70: ifge +36 -> 106
    //   73: new 43	java/lang/IllegalStateException
    //   76: dup
    //   77: ldc 60
    //   79: invokespecial 48	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   82: athrow
    //   83: astore 4
    //   85: iconst_0
    //   86: ifne +12 -> 98
    //   89: aload_0
    //   90: monitorenter
    //   91: aload_0
    //   92: iconst_0
    //   93: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   96: aload_0
    //   97: monitorexit
    //   98: aload 4
    //   100: athrow
    //   101: astore_3
    //   102: aload_0
    //   103: monitorexit
    //   104: aload_3
    //   105: athrow
    //   106: aload_0
    //   107: lload 9
    //   109: putfield 37	rx/internal/producers/ProducerArbiter:requested	J
    //   112: aload_0
    //   113: invokevirtual 62	rx/internal/producers/ProducerArbiter:emitLoop	()V
    //   116: iconst_1
    //   117: ifne +27 -> 144
    //   120: aload_0
    //   121: monitorenter
    //   122: aload_0
    //   123: iconst_0
    //   124: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   127: aload_0
    //   128: monitorexit
    //   129: return
    //   130: astore 8
    //   132: aload_0
    //   133: monitorexit
    //   134: aload 8
    //   136: athrow
    //   137: astore 5
    //   139: aload_0
    //   140: monitorexit
    //   141: aload 5
    //   143: athrow
    //   144: return
    //
    // Exception table:
    //   from	to	target	type
    //   45	51	83	finally
    //   73	83	83	finally
    //   106	112	83	finally
    //   112	116	83	finally
    //   18	37	101	finally
    //   38	45	101	finally
    //   102	104	101	finally
    //   122	129	130	finally
    //   132	134	130	finally
    //   91	98	137	finally
    //   139	141	137	finally
  }

  // ERROR //
  public void request(long paramLong)
  {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifge +13 -> 16
    //   6: new 55	java/lang/IllegalArgumentException
    //   9: dup
    //   10: ldc 64
    //   12: invokespecial 58	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   15: athrow
    //   16: lload_1
    //   17: lconst_0
    //   18: lcmp
    //   19: ifne +4 -> 23
    //   22: return
    //   23: aload_0
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   29: ifeq +21 -> 50
    //   32: aload_0
    //   33: lload_1
    //   34: aload_0
    //   35: getfield 29	rx/internal/producers/ProducerArbiter:missedRequested	J
    //   38: ladd
    //   39: putfield 29	rx/internal/producers/ProducerArbiter:missedRequested	J
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: astore_3
    //   46: aload_0
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    //   50: aload_0
    //   51: iconst_1
    //   52: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   55: aload_0
    //   56: monitorexit
    //   57: lload_1
    //   58: aload_0
    //   59: getfield 37	rx/internal/producers/ProducerArbiter:requested	J
    //   62: ladd
    //   63: lstore 6
    //   65: lload 6
    //   67: lconst_0
    //   68: lcmp
    //   69: ifge +8 -> 77
    //   72: ldc2_w 38
    //   75: lstore 6
    //   77: aload_0
    //   78: lload 6
    //   80: putfield 37	rx/internal/producers/ProducerArbiter:requested	J
    //   83: aload_0
    //   84: getfield 41	rx/internal/producers/ProducerArbiter:currentProducer	Lrx/Producer;
    //   87: astore 8
    //   89: aload 8
    //   91: ifnull +11 -> 102
    //   94: aload 8
    //   96: lload_1
    //   97: invokeinterface 52 3 0
    //   102: aload_0
    //   103: invokevirtual 62	rx/internal/producers/ProducerArbiter:emitLoop	()V
    //   106: iconst_1
    //   107: ifne -85 -> 22
    //   110: aload_0
    //   111: monitorenter
    //   112: aload_0
    //   113: iconst_0
    //   114: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   117: aload_0
    //   118: monitorexit
    //   119: return
    //   120: astore 9
    //   122: aload_0
    //   123: monitorexit
    //   124: aload 9
    //   126: athrow
    //   127: astore 4
    //   129: iconst_0
    //   130: ifne +12 -> 142
    //   133: aload_0
    //   134: monitorenter
    //   135: aload_0
    //   136: iconst_0
    //   137: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   140: aload_0
    //   141: monitorexit
    //   142: aload 4
    //   144: athrow
    //   145: astore 5
    //   147: aload_0
    //   148: monitorexit
    //   149: aload 5
    //   151: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   25	44	45	finally
    //   46	48	45	finally
    //   50	57	45	finally
    //   112	119	120	finally
    //   122	124	120	finally
    //   57	65	127	finally
    //   77	89	127	finally
    //   94	102	127	finally
    //   102	106	127	finally
    //   135	142	145	finally
    //   147	149	145	finally
  }

  // ERROR //
  public void setProducer(Producer paramProducer)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   6: ifeq +19 -> 25
    //   9: aload_1
    //   10: ifnonnull +7 -> 17
    //   13: getstatic 25	rx/internal/producers/ProducerArbiter:NULL_PRODUCER	Lrx/Producer;
    //   16: astore_1
    //   17: aload_0
    //   18: aload_1
    //   19: putfield 33	rx/internal/producers/ProducerArbiter:missedProducer	Lrx/Producer;
    //   22: aload_0
    //   23: monitorexit
    //   24: return
    //   25: aload_0
    //   26: iconst_1
    //   27: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   30: aload_0
    //   31: monitorexit
    //   32: aload_0
    //   33: aload_1
    //   34: putfield 41	rx/internal/producers/ProducerArbiter:currentProducer	Lrx/Producer;
    //   37: aload_1
    //   38: ifnull +13 -> 51
    //   41: aload_1
    //   42: aload_0
    //   43: getfield 37	rx/internal/producers/ProducerArbiter:requested	J
    //   46: invokeinterface 52 3 0
    //   51: aload_0
    //   52: invokevirtual 62	rx/internal/producers/ProducerArbiter:emitLoop	()V
    //   55: iconst_1
    //   56: ifne +48 -> 104
    //   59: aload_0
    //   60: monitorenter
    //   61: aload_0
    //   62: iconst_0
    //   63: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   66: aload_0
    //   67: monitorexit
    //   68: return
    //   69: astore 5
    //   71: aload_0
    //   72: monitorexit
    //   73: aload 5
    //   75: athrow
    //   76: astore_2
    //   77: aload_0
    //   78: monitorexit
    //   79: aload_2
    //   80: athrow
    //   81: astore_3
    //   82: iconst_0
    //   83: ifne +12 -> 95
    //   86: aload_0
    //   87: monitorenter
    //   88: aload_0
    //   89: iconst_0
    //   90: putfield 35	rx/internal/producers/ProducerArbiter:emitting	Z
    //   93: aload_0
    //   94: monitorexit
    //   95: aload_3
    //   96: athrow
    //   97: astore 4
    //   99: aload_0
    //   100: monitorexit
    //   101: aload 4
    //   103: athrow
    //   104: return
    //
    // Exception table:
    //   from	to	target	type
    //   61	68	69	finally
    //   71	73	69	finally
    //   2	9	76	finally
    //   13	17	76	finally
    //   17	24	76	finally
    //   25	32	76	finally
    //   77	79	76	finally
    //   32	37	81	finally
    //   41	51	81	finally
    //   51	55	81	finally
    //   88	95	97	finally
    //   99	101	97	finally
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.internal.producers.ProducerArbiter
 * JD-Core Version:    0.6.0
 */