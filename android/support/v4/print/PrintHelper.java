package android.support.v4.print;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PrintHelper
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  private final PrintHelperVersionImpl mImpl;

  public PrintHelper(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      this.mImpl = new PrintHelperApi24(paramContext);
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      this.mImpl = new PrintHelperApi23(paramContext);
      return;
    }
    if (Build.VERSION.SDK_INT >= 20)
    {
      this.mImpl = new PrintHelperApi20(paramContext);
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      this.mImpl = new PrintHelperApi19(paramContext);
      return;
    }
    this.mImpl = new PrintHelperStub(null);
  }

  public static boolean systemSupportsPrint()
  {
    return Build.VERSION.SDK_INT >= 19;
  }

  public int getColorMode()
  {
    return this.mImpl.getColorMode();
  }

  public int getOrientation()
  {
    return this.mImpl.getOrientation();
  }

  public int getScaleMode()
  {
    return this.mImpl.getScaleMode();
  }

  public void printBitmap(String paramString, Bitmap paramBitmap)
  {
    this.mImpl.printBitmap(paramString, paramBitmap, null);
  }

  public void printBitmap(String paramString, Bitmap paramBitmap, OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    this.mImpl.printBitmap(paramString, paramBitmap, paramOnPrintFinishCallback);
  }

  public void printBitmap(String paramString, Uri paramUri)
    throws FileNotFoundException
  {
    this.mImpl.printBitmap(paramString, paramUri, null);
  }

  public void printBitmap(String paramString, Uri paramUri, OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    this.mImpl.printBitmap(paramString, paramUri, paramOnPrintFinishCallback);
  }

  public void setColorMode(int paramInt)
  {
    this.mImpl.setColorMode(paramInt);
  }

  public void setOrientation(int paramInt)
  {
    this.mImpl.setOrientation(paramInt);
  }

  public void setScaleMode(int paramInt)
  {
    this.mImpl.setScaleMode(paramInt);
  }

  @Retention(RetentionPolicy.SOURCE)
  private static @interface ColorMode
  {
  }

  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }

  @Retention(RetentionPolicy.SOURCE)
  private static @interface Orientation
  {
  }

  @RequiresApi(19)
  private static class PrintHelperApi19
    implements PrintHelper.PrintHelperVersionImpl
  {
    private static final String LOG_TAG = "PrintHelperApi19";
    private static final int MAX_PRINT_SIZE = 3500;
    int mColorMode = 2;
    final Context mContext;
    BitmapFactory.Options mDecodeOptions = null;
    protected boolean mIsMinMarginsHandlingCorrect = true;
    private final Object mLock = new Object();
    int mOrientation;
    protected boolean mPrintActivityRespectsOrientation = true;
    int mScaleMode = 2;

    PrintHelperApi19(Context paramContext)
    {
      this.mContext = paramContext;
    }

    private Bitmap convertBitmapForColorMode(Bitmap paramBitmap, int paramInt)
    {
      if (paramInt != 1)
        return paramBitmap;
      Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      Paint localPaint = new Paint();
      ColorMatrix localColorMatrix = new ColorMatrix();
      localColorMatrix.setSaturation(0.0F);
      localPaint.setColorFilter(new ColorMatrixColorFilter(localColorMatrix));
      localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
      localCanvas.setBitmap(null);
      return localBitmap;
    }

    private Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
    {
      Matrix localMatrix = new Matrix();
      float f1 = paramRectF.width() / paramInt1;
      float f2;
      if (paramInt3 == 2)
        f2 = Math.max(f1, paramRectF.height() / paramInt2);
      while (true)
      {
        localMatrix.postScale(f2, f2);
        localMatrix.postTranslate((paramRectF.width() - f2 * paramInt1) / 2.0F, (paramRectF.height() - f2 * paramInt2) / 2.0F);
        return localMatrix;
        f2 = Math.min(f1, paramRectF.height() / paramInt2);
      }
    }

    private static boolean isPortrait(Bitmap paramBitmap)
    {
      return paramBitmap.getWidth() <= paramBitmap.getHeight();
    }

    private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
      throws FileNotFoundException
    {
      if ((paramUri == null) || (this.mContext == null))
        throw new IllegalArgumentException("bad argument to loadBitmap");
      InputStream localInputStream = null;
      try
      {
        localInputStream = this.mContext.getContentResolver().openInputStream(paramUri);
        Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream, null, paramOptions);
        if (localInputStream != null);
        try
        {
          localInputStream.close();
          return localBitmap;
        }
        catch (IOException localIOException2)
        {
          Log.w("PrintHelperApi19", "close fail ", localIOException2);
          return localBitmap;
        }
      }
      finally
      {
        if (localInputStream == null);
      }
      try
      {
        localInputStream.close();
        throw localObject;
      }
      catch (IOException localIOException1)
      {
        while (true)
          Log.w("PrintHelperApi19", "close fail ", localIOException1);
      }
    }

    // ERROR //
    private Bitmap loadConstrainedBitmap(Uri paramUri)
      throws FileNotFoundException
    {
      // Byte code:
      //   0: aload_1
      //   1: ifnull +10 -> 11
      //   4: aload_0
      //   5: getfield 47	android/support/v4/print/PrintHelper$PrintHelperApi19:mContext	Landroid/content/Context;
      //   8: ifnonnull +13 -> 21
      //   11: new 164	java/lang/IllegalArgumentException
      //   14: dup
      //   15: ldc 202
      //   17: invokespecial 169	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
      //   20: athrow
      //   21: new 204	android/graphics/BitmapFactory$Options
      //   24: dup
      //   25: invokespecial 205	android/graphics/BitmapFactory$Options:<init>	()V
      //   28: astore_2
      //   29: aload_2
      //   30: iconst_1
      //   31: putfield 208	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   34: aload_0
      //   35: aload_1
      //   36: aload_2
      //   37: invokespecial 210	android/support/v4/print/PrintHelper$PrintHelperApi19:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   40: pop
      //   41: aload_2
      //   42: getfield 213	android/graphics/BitmapFactory$Options:outWidth	I
      //   45: istore 4
      //   47: aload_2
      //   48: getfield 216	android/graphics/BitmapFactory$Options:outHeight	I
      //   51: istore 5
      //   53: iload 4
      //   55: ifle +8 -> 63
      //   58: iload 5
      //   60: ifgt +5 -> 65
      //   63: aconst_null
      //   64: areturn
      //   65: iload 4
      //   67: iload 5
      //   69: invokestatic 219	java/lang/Math:max	(II)I
      //   72: istore 6
      //   74: iconst_1
      //   75: istore 7
      //   77: iload 6
      //   79: sipush 3500
      //   82: if_icmple +18 -> 100
      //   85: iload 6
      //   87: iconst_1
      //   88: iushr
      //   89: istore 6
      //   91: iload 7
      //   93: iconst_1
      //   94: ishl
      //   95: istore 7
      //   97: goto -20 -> 77
      //   100: iload 7
      //   102: ifle -39 -> 63
      //   105: iload 4
      //   107: iload 5
      //   109: invokestatic 221	java/lang/Math:min	(II)I
      //   112: iload 7
      //   114: idiv
      //   115: ifle -52 -> 63
      //   118: aload_0
      //   119: getfield 37	android/support/v4/print/PrintHelper$PrintHelperApi19:mLock	Ljava/lang/Object;
      //   122: astore 8
      //   124: aload 8
      //   126: monitorenter
      //   127: aload_0
      //   128: new 204	android/graphics/BitmapFactory$Options
      //   131: dup
      //   132: invokespecial 205	android/graphics/BitmapFactory$Options:<init>	()V
      //   135: putfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
      //   138: aload_0
      //   139: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
      //   142: iconst_1
      //   143: putfield 224	android/graphics/BitmapFactory$Options:inMutable	Z
      //   146: aload_0
      //   147: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
      //   150: iload 7
      //   152: putfield 227	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   155: aload_0
      //   156: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
      //   159: astore 10
      //   161: aload 8
      //   163: monitorexit
      //   164: aload_0
      //   165: aload_1
      //   166: aload 10
      //   168: invokespecial 210	android/support/v4/print/PrintHelper$PrintHelperApi19:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   171: astore 14
      //   173: aload_0
      //   174: getfield 37	android/support/v4/print/PrintHelper$PrintHelperApi19:mLock	Ljava/lang/Object;
      //   177: astore 15
      //   179: aload 15
      //   181: monitorenter
      //   182: aload_0
      //   183: aconst_null
      //   184: putfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
      //   187: aload 15
      //   189: monitorexit
      //   190: aload 14
      //   192: areturn
      //   193: astore 16
      //   195: aload 15
      //   197: monitorexit
      //   198: aload 16
      //   200: athrow
      //   201: astore 9
      //   203: aload 8
      //   205: monitorexit
      //   206: aload 9
      //   208: athrow
      //   209: astore 11
      //   211: aload_0
      //   212: getfield 37	android/support/v4/print/PrintHelper$PrintHelperApi19:mLock	Ljava/lang/Object;
      //   215: astore 12
      //   217: aload 12
      //   219: monitorenter
      //   220: aload_0
      //   221: aconst_null
      //   222: putfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
      //   225: aload 12
      //   227: monitorexit
      //   228: aload 11
      //   230: athrow
      //   231: astore 13
      //   233: aload 12
      //   235: monitorexit
      //   236: aload 13
      //   238: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   182	190	193	finally
      //   195	198	193	finally
      //   127	164	201	finally
      //   203	206	201	finally
      //   164	173	209	finally
      //   220	228	231	finally
      //   233	236	231	finally
    }

    private void writeBitmap(PrintAttributes paramPrintAttributes, int paramInt, Bitmap paramBitmap, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
    {
      if (this.mIsMinMarginsHandlingCorrect);
      for (PrintAttributes localPrintAttributes = paramPrintAttributes; ; localPrintAttributes = copyAttributes(paramPrintAttributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build())
      {
        new AsyncTask(paramCancellationSignal, localPrintAttributes, paramBitmap, paramPrintAttributes, paramInt, paramParcelFileDescriptor, paramWriteResultCallback)
        {
          // ERROR //
          protected Throwable doInBackground(Void[] paramArrayOfVoid)
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 31	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$cancellationSignal	Landroid/os/CancellationSignal;
            //   4: invokevirtual 63	android/os/CancellationSignal:isCanceled	()Z
            //   7: ifeq +5 -> 12
            //   10: aconst_null
            //   11: areturn
            //   12: new 65	android/print/pdf/PrintedPdfDocument
            //   15: dup
            //   16: aload_0
            //   17: getfield 29	android/support/v4/print/PrintHelper$PrintHelperApi19$2:this$0	Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
            //   20: getfield 69	android/support/v4/print/PrintHelper$PrintHelperApi19:mContext	Landroid/content/Context;
            //   23: aload_0
            //   24: getfield 33	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$pdfAttributes	Landroid/print/PrintAttributes;
            //   27: invokespecial 72	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
            //   30: astore_3
            //   31: aload_0
            //   32: getfield 29	android/support/v4/print/PrintHelper$PrintHelperApi19$2:this$0	Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
            //   35: aload_0
            //   36: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$bitmap	Landroid/graphics/Bitmap;
            //   39: aload_0
            //   40: getfield 33	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$pdfAttributes	Landroid/print/PrintAttributes;
            //   43: invokevirtual 78	android/print/PrintAttributes:getColorMode	()I
            //   46: invokestatic 82	android/support/v4/print/PrintHelper$PrintHelperApi19:access$100	(Landroid/support/v4/print/PrintHelper$PrintHelperApi19;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
            //   49: astore 4
            //   51: aload_0
            //   52: getfield 31	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$cancellationSignal	Landroid/os/CancellationSignal;
            //   55: invokevirtual 63	android/os/CancellationSignal:isCanceled	()Z
            //   58: istore 5
            //   60: iload 5
            //   62: ifne +348 -> 410
            //   65: aload_3
            //   66: iconst_1
            //   67: invokevirtual 86	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
            //   70: astore 9
            //   72: aload_0
            //   73: getfield 29	android/support/v4/print/PrintHelper$PrintHelperApi19$2:this$0	Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
            //   76: getfield 90	android/support/v4/print/PrintHelper$PrintHelperApi19:mIsMinMarginsHandlingCorrect	Z
            //   79: ifeq +129 -> 208
            //   82: new 92	android/graphics/RectF
            //   85: dup
            //   86: aload 9
            //   88: invokevirtual 98	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
            //   91: invokevirtual 104	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
            //   94: invokespecial 107	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
            //   97: astore 10
            //   99: aload_0
            //   100: getfield 29	android/support/v4/print/PrintHelper$PrintHelperApi19$2:this$0	Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
            //   103: aload 4
            //   105: invokevirtual 112	android/graphics/Bitmap:getWidth	()I
            //   108: aload 4
            //   110: invokevirtual 115	android/graphics/Bitmap:getHeight	()I
            //   113: aload 10
            //   115: aload_0
            //   116: getfield 39	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fittingMode	I
            //   119: invokestatic 119	android/support/v4/print/PrintHelper$PrintHelperApi19:access$200	(Landroid/support/v4/print/PrintHelper$PrintHelperApi19;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
            //   122: astore 11
            //   124: aload_0
            //   125: getfield 29	android/support/v4/print/PrintHelper$PrintHelperApi19$2:this$0	Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
            //   128: getfield 90	android/support/v4/print/PrintHelper$PrintHelperApi19:mIsMinMarginsHandlingCorrect	Z
            //   131: ifeq +178 -> 309
            //   134: aload 9
            //   136: invokevirtual 123	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
            //   139: aload 4
            //   141: aload 11
            //   143: aconst_null
            //   144: invokevirtual 129	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
            //   147: aload_3
            //   148: aload 9
            //   150: invokevirtual 133	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
            //   153: aload_0
            //   154: getfield 31	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$cancellationSignal	Landroid/os/CancellationSignal;
            //   157: invokevirtual 63	android/os/CancellationSignal:isCanceled	()Z
            //   160: istore 12
            //   162: iload 12
            //   164: ifeq +175 -> 339
            //   167: aload_3
            //   168: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
            //   171: aload_0
            //   172: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   175: astore 15
            //   177: aload 15
            //   179: ifnull +10 -> 189
            //   182: aload_0
            //   183: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   186: invokevirtual 139	android/os/ParcelFileDescriptor:close	()V
            //   189: aload 4
            //   191: aload_0
            //   192: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$bitmap	Landroid/graphics/Bitmap;
            //   195: if_acmpeq +215 -> 410
            //   198: aload 4
            //   200: invokevirtual 142	android/graphics/Bitmap:recycle	()V
            //   203: aconst_null
            //   204: areturn
            //   205: astore_2
            //   206: aload_2
            //   207: areturn
            //   208: new 65	android/print/pdf/PrintedPdfDocument
            //   211: dup
            //   212: aload_0
            //   213: getfield 29	android/support/v4/print/PrintHelper$PrintHelperApi19$2:this$0	Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
            //   216: getfield 69	android/support/v4/print/PrintHelper$PrintHelperApi19:mContext	Landroid/content/Context;
            //   219: aload_0
            //   220: getfield 37	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$attributes	Landroid/print/PrintAttributes;
            //   223: invokespecial 72	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
            //   226: astore 19
            //   228: aload 19
            //   230: iconst_1
            //   231: invokevirtual 86	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
            //   234: astore 20
            //   236: new 92	android/graphics/RectF
            //   239: dup
            //   240: aload 20
            //   242: invokevirtual 98	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
            //   245: invokevirtual 104	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
            //   248: invokespecial 107	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
            //   251: astore 10
            //   253: aload 19
            //   255: aload 20
            //   257: invokevirtual 133	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
            //   260: aload 19
            //   262: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
            //   265: goto -166 -> 99
            //   268: astore 6
            //   270: aload_3
            //   271: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
            //   274: aload_0
            //   275: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   278: astore 7
            //   280: aload 7
            //   282: ifnull +10 -> 292
            //   285: aload_0
            //   286: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   289: invokevirtual 139	android/os/ParcelFileDescriptor:close	()V
            //   292: aload 4
            //   294: aload_0
            //   295: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$bitmap	Landroid/graphics/Bitmap;
            //   298: if_acmpeq +8 -> 306
            //   301: aload 4
            //   303: invokevirtual 142	android/graphics/Bitmap:recycle	()V
            //   306: aload 6
            //   308: athrow
            //   309: aload 11
            //   311: aload 10
            //   313: getfield 146	android/graphics/RectF:left	F
            //   316: aload 10
            //   318: getfield 149	android/graphics/RectF:top	F
            //   321: invokevirtual 155	android/graphics/Matrix:postTranslate	(FF)Z
            //   324: pop
            //   325: aload 9
            //   327: invokevirtual 123	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
            //   330: aload 10
            //   332: invokevirtual 159	android/graphics/Canvas:clipRect	(Landroid/graphics/RectF;)Z
            //   335: pop
            //   336: goto -202 -> 134
            //   339: aload_3
            //   340: new 161	java/io/FileOutputStream
            //   343: dup
            //   344: aload_0
            //   345: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   348: invokevirtual 165	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
            //   351: invokespecial 168	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
            //   354: invokevirtual 172	android/print/pdf/PrintedPdfDocument:writeTo	(Ljava/io/OutputStream;)V
            //   357: aload_3
            //   358: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
            //   361: aload_0
            //   362: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   365: astore 13
            //   367: aload 13
            //   369: ifnull +10 -> 379
            //   372: aload_0
            //   373: getfield 41	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
            //   376: invokevirtual 139	android/os/ParcelFileDescriptor:close	()V
            //   379: aload 4
            //   381: aload_0
            //   382: getfield 35	android/support/v4/print/PrintHelper$PrintHelperApi19$2:val$bitmap	Landroid/graphics/Bitmap;
            //   385: if_acmpeq +25 -> 410
            //   388: aload 4
            //   390: invokevirtual 142	android/graphics/Bitmap:recycle	()V
            //   393: aconst_null
            //   394: areturn
            //   395: astore 8
            //   397: goto -105 -> 292
            //   400: astore 14
            //   402: goto -23 -> 379
            //   405: astore 16
            //   407: goto -218 -> 189
            //   410: aconst_null
            //   411: areturn
            //
            // Exception table:
            //   from	to	target	type
            //   0	10	205	java/lang/Throwable
            //   12	60	205	java/lang/Throwable
            //   167	177	205	java/lang/Throwable
            //   182	189	205	java/lang/Throwable
            //   189	203	205	java/lang/Throwable
            //   270	280	205	java/lang/Throwable
            //   285	292	205	java/lang/Throwable
            //   292	306	205	java/lang/Throwable
            //   306	309	205	java/lang/Throwable
            //   357	367	205	java/lang/Throwable
            //   372	379	205	java/lang/Throwable
            //   379	393	205	java/lang/Throwable
            //   65	99	268	finally
            //   99	134	268	finally
            //   134	162	268	finally
            //   208	265	268	finally
            //   309	336	268	finally
            //   339	357	268	finally
            //   285	292	395	java/io/IOException
            //   372	379	400	java/io/IOException
            //   182	189	405	java/io/IOException
          }

          protected void onPostExecute(Throwable paramThrowable)
          {
            if (this.val$cancellationSignal.isCanceled())
            {
              this.val$writeResultCallback.onWriteCancelled();
              return;
            }
            if (paramThrowable == null)
            {
              PrintDocumentAdapter.WriteResultCallback localWriteResultCallback = this.val$writeResultCallback;
              PageRange[] arrayOfPageRange = new PageRange[1];
              arrayOfPageRange[0] = PageRange.ALL_PAGES;
              localWriteResultCallback.onWriteFinished(arrayOfPageRange);
              return;
            }
            Log.e("PrintHelperApi19", "Error writing printed content", paramThrowable);
            this.val$writeResultCallback.onWriteFailed(null);
          }
        }
        .execute(new Void[0]);
        return;
      }
    }

    protected PrintAttributes.Builder copyAttributes(PrintAttributes paramPrintAttributes)
    {
      PrintAttributes.Builder localBuilder = new PrintAttributes.Builder().setMediaSize(paramPrintAttributes.getMediaSize()).setResolution(paramPrintAttributes.getResolution()).setMinMargins(paramPrintAttributes.getMinMargins());
      if (paramPrintAttributes.getColorMode() != 0)
        localBuilder.setColorMode(paramPrintAttributes.getColorMode());
      return localBuilder;
    }

    public int getColorMode()
    {
      return this.mColorMode;
    }

    public int getOrientation()
    {
      if (this.mOrientation == 0)
        return 1;
      return this.mOrientation;
    }

    public int getScaleMode()
    {
      return this.mScaleMode;
    }

    public void printBitmap(String paramString, Bitmap paramBitmap, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback)
    {
      if (paramBitmap == null)
        return;
      int i = this.mScaleMode;
      PrintManager localPrintManager = (PrintManager)this.mContext.getSystemService("print");
      if (isPortrait(paramBitmap));
      for (PrintAttributes.MediaSize localMediaSize = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT; ; localMediaSize = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE)
      {
        PrintAttributes localPrintAttributes = new PrintAttributes.Builder().setMediaSize(localMediaSize).setColorMode(this.mColorMode).build();
        localPrintManager.print(paramString, new PrintDocumentAdapter(paramString, i, paramBitmap, paramOnPrintFinishCallback)
        {
          private PrintAttributes mAttributes;

          public void onFinish()
          {
            if (this.val$callback != null)
              this.val$callback.onFinish();
          }

          public void onLayout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramLayoutResultCallback, Bundle paramBundle)
          {
            int i = 1;
            this.mAttributes = paramPrintAttributes2;
            PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(this.val$jobName).setContentType(i).setPageCount(i).build();
            if (!paramPrintAttributes2.equals(paramPrintAttributes1));
            while (true)
            {
              paramLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
              return;
              int j = 0;
            }
          }

          public void onWrite(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
          {
            PrintHelper.PrintHelperApi19.this.writeBitmap(this.mAttributes, this.val$fittingMode, this.val$bitmap, paramParcelFileDescriptor, paramCancellationSignal, paramWriteResultCallback);
          }
        }
        , localPrintAttributes);
        return;
      }
    }

    public void printBitmap(String paramString, Uri paramUri, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback)
      throws FileNotFoundException
    {
      3 local3 = new PrintDocumentAdapter(paramString, paramUri, paramOnPrintFinishCallback, this.mScaleMode)
      {
        private PrintAttributes mAttributes;
        Bitmap mBitmap = null;
        AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;

        private void cancelLoad()
        {
          synchronized (PrintHelper.PrintHelperApi19.this.mLock)
          {
            if (PrintHelper.PrintHelperApi19.this.mDecodeOptions != null)
            {
              PrintHelper.PrintHelperApi19.this.mDecodeOptions.requestCancelDecode();
              PrintHelper.PrintHelperApi19.this.mDecodeOptions = null;
            }
            return;
          }
        }

        public void onFinish()
        {
          super.onFinish();
          cancelLoad();
          if (this.mLoadBitmap != null)
            this.mLoadBitmap.cancel(true);
          if (this.val$callback != null)
            this.val$callback.onFinish();
          if (this.mBitmap != null)
          {
            this.mBitmap.recycle();
            this.mBitmap = null;
          }
        }

        public void onLayout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramLayoutResultCallback, Bundle paramBundle)
        {
          int i = 1;
          monitorenter;
          try
          {
            this.mAttributes = paramPrintAttributes2;
            monitorexit;
            if (paramCancellationSignal.isCanceled())
            {
              paramLayoutResultCallback.onLayoutCancelled();
              return;
            }
          }
          finally
          {
            monitorexit;
          }
          if (this.mBitmap != null)
          {
            PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(this.val$jobName).setContentType(i).setPageCount(i).build();
            if (!paramPrintAttributes2.equals(paramPrintAttributes1));
            while (true)
            {
              paramLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
              return;
              int j = 0;
            }
          }
          this.mLoadBitmap = new AsyncTask(paramCancellationSignal, paramPrintAttributes2, paramPrintAttributes1, paramLayoutResultCallback)
          {
            protected Bitmap doInBackground(Uri[] paramArrayOfUri)
            {
              try
              {
                Bitmap localBitmap = PrintHelper.PrintHelperApi19.this.loadConstrainedBitmap(PrintHelper.PrintHelperApi19.3.this.val$imageFile);
                return localBitmap;
              }
              catch (FileNotFoundException localFileNotFoundException)
              {
              }
              return null;
            }

            protected void onCancelled(Bitmap paramBitmap)
            {
              this.val$layoutResultCallback.onLayoutCancelled();
              PrintHelper.PrintHelperApi19.3.this.mLoadBitmap = null;
            }

            protected void onPostExecute(Bitmap paramBitmap)
            {
              super.onPostExecute(paramBitmap);
              if ((paramBitmap != null) && ((!PrintHelper.PrintHelperApi19.this.mPrintActivityRespectsOrientation) || (PrintHelper.PrintHelperApi19.this.mOrientation == 0)))
                monitorenter;
              while (true)
              {
                try
                {
                  PrintAttributes.MediaSize localMediaSize = PrintHelper.PrintHelperApi19.3.this.mAttributes.getMediaSize();
                  monitorexit;
                  if ((localMediaSize == null) || (localMediaSize.isPortrait() == PrintHelper.PrintHelperApi19.access$600(paramBitmap)))
                    continue;
                  Matrix localMatrix = new Matrix();
                  localMatrix.postRotate(90.0F);
                  int i = paramBitmap.getWidth();
                  int j = paramBitmap.getHeight();
                  paramBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
                  PrintHelper.PrintHelperApi19.3.this.mBitmap = paramBitmap;
                  if (paramBitmap == null)
                    break label195;
                  PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(PrintHelper.PrintHelperApi19.3.this.val$jobName).setContentType(1).setPageCount(1).build();
                  if (!this.val$newPrintAttributes.equals(this.val$oldPrintAttributes))
                  {
                    bool = true;
                    this.val$layoutResultCallback.onLayoutFinished(localPrintDocumentInfo, bool);
                    PrintHelper.PrintHelperApi19.3.this.mLoadBitmap = null;
                    return;
                  }
                }
                finally
                {
                  monitorexit;
                }
                boolean bool = false;
                continue;
                label195: this.val$layoutResultCallback.onLayoutFailed(null);
              }
            }

            protected void onPreExecute()
            {
              this.val$cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
              {
                public void onCancel()
                {
                  PrintHelper.PrintHelperApi19.3.this.cancelLoad();
                  PrintHelper.PrintHelperApi19.3.1.this.cancel(false);
                }
              });
            }
          }
          .execute(new Uri[0]);
        }

        public void onWrite(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
        {
          PrintHelper.PrintHelperApi19.this.writeBitmap(this.mAttributes, this.val$fittingMode, this.mBitmap, paramParcelFileDescriptor, paramCancellationSignal, paramWriteResultCallback);
        }
      };
      PrintManager localPrintManager = (PrintManager)this.mContext.getSystemService("print");
      PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
      localBuilder.setColorMode(this.mColorMode);
      if ((this.mOrientation == 1) || (this.mOrientation == 0))
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
      while (true)
      {
        localPrintManager.print(paramString, local3, localBuilder.build());
        return;
        if (this.mOrientation != 2)
          continue;
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      }
    }

    public void setColorMode(int paramInt)
    {
      this.mColorMode = paramInt;
    }

    public void setOrientation(int paramInt)
    {
      this.mOrientation = paramInt;
    }

    public void setScaleMode(int paramInt)
    {
      this.mScaleMode = paramInt;
    }
  }

  @RequiresApi(20)
  private static class PrintHelperApi20 extends PrintHelper.PrintHelperApi19
  {
    PrintHelperApi20(Context paramContext)
    {
      super();
      this.mPrintActivityRespectsOrientation = false;
    }
  }

  @RequiresApi(23)
  private static class PrintHelperApi23 extends PrintHelper.PrintHelperApi20
  {
    PrintHelperApi23(Context paramContext)
    {
      super();
      this.mIsMinMarginsHandlingCorrect = false;
    }

    protected PrintAttributes.Builder copyAttributes(PrintAttributes paramPrintAttributes)
    {
      PrintAttributes.Builder localBuilder = super.copyAttributes(paramPrintAttributes);
      if (paramPrintAttributes.getDuplexMode() != 0)
        localBuilder.setDuplexMode(paramPrintAttributes.getDuplexMode());
      return localBuilder;
    }
  }

  @RequiresApi(24)
  private static class PrintHelperApi24 extends PrintHelper.PrintHelperApi23
  {
    PrintHelperApi24(Context paramContext)
    {
      super();
      this.mIsMinMarginsHandlingCorrect = true;
      this.mPrintActivityRespectsOrientation = true;
    }
  }

  private static final class PrintHelperStub
    implements PrintHelper.PrintHelperVersionImpl
  {
    int mColorMode = 2;
    int mOrientation = 1;
    int mScaleMode = 2;

    public int getColorMode()
    {
      return this.mColorMode;
    }

    public int getOrientation()
    {
      return this.mOrientation;
    }

    public int getScaleMode()
    {
      return this.mScaleMode;
    }

    public void printBitmap(String paramString, Bitmap paramBitmap, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback)
    {
    }

    public void printBitmap(String paramString, Uri paramUri, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback)
    {
    }

    public void setColorMode(int paramInt)
    {
      this.mColorMode = paramInt;
    }

    public void setOrientation(int paramInt)
    {
      this.mOrientation = paramInt;
    }

    public void setScaleMode(int paramInt)
    {
      this.mScaleMode = paramInt;
    }
  }

  static abstract interface PrintHelperVersionImpl
  {
    public abstract int getColorMode();

    public abstract int getOrientation();

    public abstract int getScaleMode();

    public abstract void printBitmap(String paramString, Bitmap paramBitmap, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback);

    public abstract void printBitmap(String paramString, Uri paramUri, PrintHelper.OnPrintFinishCallback paramOnPrintFinishCallback)
      throws FileNotFoundException;

    public abstract void setColorMode(int paramInt);

    public abstract void setOrientation(int paramInt);

    public abstract void setScaleMode(int paramInt);
  }

  @Retention(RetentionPolicy.SOURCE)
  private static @interface ScaleMode
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.print.PrintHelper
 * JD-Core Version:    0.6.0
 */