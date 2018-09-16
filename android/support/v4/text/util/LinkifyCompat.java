package android.support.v4.text.util;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkifyCompat
{
  private static final Comparator<LinkSpec> COMPARATOR;
  private static final String[] EMPTY_STRING = new String[0];

  static
  {
    COMPARATOR = new Comparator()
    {
      public final int compare(LinkifyCompat.LinkSpec paramLinkSpec1, LinkifyCompat.LinkSpec paramLinkSpec2)
      {
        if (paramLinkSpec1.start < paramLinkSpec2.start);
        do
        {
          return -1;
          if (paramLinkSpec1.start > paramLinkSpec2.start)
            return 1;
          if (paramLinkSpec1.end < paramLinkSpec2.end)
            return 1;
        }
        while (paramLinkSpec1.end > paramLinkSpec2.end);
        return 0;
      }
    };
  }

  private static void addLinkMovementMethod(@NonNull TextView paramTextView)
  {
    MovementMethod localMovementMethod = paramTextView.getMovementMethod();
    if (((localMovementMethod == null) || (!(localMovementMethod instanceof LinkMovementMethod))) && (paramTextView.getLinksClickable()))
      paramTextView.setMovementMethod(LinkMovementMethod.getInstance());
  }

  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      Linkify.addLinks(paramTextView, paramPattern, paramString);
      return;
    }
    addLinks(paramTextView, paramPattern, paramString, null, null, null);
  }

  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      Linkify.addLinks(paramTextView, paramPattern, paramString, paramMatchFilter, paramTransformFilter);
      return;
    }
    addLinks(paramTextView, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }

  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    if (Build.VERSION.SDK_INT >= 26)
      Linkify.addLinks(paramTextView, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter);
    SpannableString localSpannableString;
    do
    {
      return;
      localSpannableString = SpannableString.valueOf(paramTextView.getText());
    }
    while (!addLinks(localSpannableString, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter));
    paramTextView.setText(localSpannableString);
    addLinkMovementMethod(paramTextView);
  }

  public static final boolean addLinks(@NonNull Spannable paramSpannable, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return Linkify.addLinks(paramSpannable, paramInt);
    if (paramInt == 0)
      return false;
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    for (int i = -1 + arrayOfURLSpan.length; i >= 0; i--)
      paramSpannable.removeSpan(arrayOfURLSpan[i]);
    if ((paramInt & 0x4) != 0)
      Linkify.addLinks(paramSpannable, 4);
    ArrayList localArrayList = new ArrayList();
    if ((paramInt & 0x1) != 0)
      gatherLinks(localArrayList, paramSpannable, PatternsCompat.AUTOLINK_WEB_URL, new String[] { "http://", "https://", "rtsp://" }, Linkify.sUrlMatchFilter, null);
    if ((paramInt & 0x2) != 0)
      gatherLinks(localArrayList, paramSpannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[] { "mailto:" }, null, null);
    if ((paramInt & 0x8) != 0)
      gatherMapLinks(localArrayList, paramSpannable);
    pruneOverlaps(localArrayList, paramSpannable);
    if (localArrayList.size() == 0)
      return false;
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      LinkSpec localLinkSpec = (LinkSpec)localIterator.next();
      if (localLinkSpec.frameworkAddedSpan != null)
        continue;
      applyLink(localLinkSpec.url, localLinkSpec.start, localLinkSpec.end, paramSpannable);
    }
    return true;
  }

  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return Linkify.addLinks(paramSpannable, paramPattern, paramString);
    return addLinks(paramSpannable, paramPattern, paramString, null, null, null);
  }

  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    if (Build.VERSION.SDK_INT >= 26)
      return Linkify.addLinks(paramSpannable, paramPattern, paramString, paramMatchFilter, paramTransformFilter);
    return addLinks(paramSpannable, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }

  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    boolean bool1;
    if (Build.VERSION.SDK_INT >= 26)
      bool1 = Linkify.addLinks(paramSpannable, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter);
    while (true)
    {
      return bool1;
      if (paramString == null)
        paramString = "";
      if ((paramArrayOfString == null) || (paramArrayOfString.length < 1))
        paramArrayOfString = EMPTY_STRING;
      String[] arrayOfString = new String[1 + paramArrayOfString.length];
      arrayOfString[0] = paramString.toLowerCase(Locale.ROOT);
      int i = 0;
      if (i < paramArrayOfString.length)
      {
        String str1 = paramArrayOfString[i];
        int m = i + 1;
        if (str1 == null);
        for (String str2 = ""; ; str2 = str1.toLowerCase(Locale.ROOT))
        {
          arrayOfString[m] = str2;
          i++;
          break;
        }
      }
      bool1 = false;
      Matcher localMatcher = paramPattern.matcher(paramSpannable);
      while (localMatcher.find())
      {
        int j = localMatcher.start();
        int k = localMatcher.end();
        boolean bool2 = true;
        if (paramMatchFilter != null)
          bool2 = paramMatchFilter.acceptMatch(paramSpannable, j, k);
        if (!bool2)
          continue;
        applyLink(makeUrl(localMatcher.group(0), arrayOfString, localMatcher, paramTransformFilter), j, k, paramSpannable);
        bool1 = true;
      }
    }
  }

  public static final boolean addLinks(@NonNull TextView paramTextView, int paramInt)
  {
    boolean bool1;
    if (Build.VERSION.SDK_INT >= 26)
      bool1 = Linkify.addLinks(paramTextView, paramInt);
    SpannableString localSpannableString;
    boolean bool2;
    do
    {
      CharSequence localCharSequence;
      while (true)
      {
        return bool1;
        bool1 = false;
        if (paramInt == 0)
          continue;
        localCharSequence = paramTextView.getText();
        if (!(localCharSequence instanceof Spannable))
          break;
        boolean bool3 = addLinks((Spannable)localCharSequence, paramInt);
        bool1 = false;
        if (!bool3)
          continue;
        addLinkMovementMethod(paramTextView);
        return true;
      }
      localSpannableString = SpannableString.valueOf(localCharSequence);
      bool2 = addLinks(localSpannableString, paramInt);
      bool1 = false;
    }
    while (!bool2);
    addLinkMovementMethod(paramTextView);
    paramTextView.setText(localSpannableString);
    return true;
  }

  private static void applyLink(String paramString, int paramInt1, int paramInt2, Spannable paramSpannable)
  {
    paramSpannable.setSpan(new URLSpan(paramString), paramInt1, paramInt2, 33);
  }

  private static void gatherLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable, Pattern paramPattern, String[] paramArrayOfString, Linkify.MatchFilter paramMatchFilter, Linkify.TransformFilter paramTransformFilter)
  {
    Matcher localMatcher = paramPattern.matcher(paramSpannable);
    while (localMatcher.find())
    {
      int i = localMatcher.start();
      int j = localMatcher.end();
      if ((paramMatchFilter != null) && (!paramMatchFilter.acceptMatch(paramSpannable, i, j)))
        continue;
      LinkSpec localLinkSpec = new LinkSpec();
      localLinkSpec.url = makeUrl(localMatcher.group(0), paramArrayOfString, localMatcher, paramTransformFilter);
      localLinkSpec.start = i;
      localLinkSpec.end = j;
      paramArrayList.add(localLinkSpec);
    }
  }

  // ERROR //
  private static final void gatherMapLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 248	java/lang/Object:toString	()Ljava/lang/String;
    //   4: astore_2
    //   5: iconst_0
    //   6: istore_3
    //   7: aload_2
    //   8: invokestatic 254	android/webkit/WebView:findAddress	(Ljava/lang/String;)Ljava/lang/String;
    //   11: astore 5
    //   13: aload 5
    //   15: ifnull +124 -> 139
    //   18: aload_2
    //   19: aload 5
    //   21: invokevirtual 258	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   24: istore 6
    //   26: iload 6
    //   28: ifge +4 -> 32
    //   31: return
    //   32: new 158	android/support/v4/text/util/LinkifyCompat$LinkSpec
    //   35: dup
    //   36: invokespecial 236	android/support/v4/text/util/LinkifyCompat$LinkSpec:<init>	()V
    //   39: astore 7
    //   41: iload 6
    //   43: aload 5
    //   45: invokevirtual 259	java/lang/String:length	()I
    //   48: iadd
    //   49: istore 8
    //   51: aload 7
    //   53: iload_3
    //   54: iload 6
    //   56: iadd
    //   57: putfield 169	android/support/v4/text/util/LinkifyCompat$LinkSpec:start	I
    //   60: aload 7
    //   62: iload_3
    //   63: iload 8
    //   65: iadd
    //   66: putfield 172	android/support/v4/text/util/LinkifyCompat$LinkSpec:end	I
    //   69: aload_2
    //   70: iload 8
    //   72: invokevirtual 262	java/lang/String:substring	(I)Ljava/lang/String;
    //   75: astore 9
    //   77: aload 9
    //   79: astore_2
    //   80: iload_3
    //   81: iload 8
    //   83: iadd
    //   84: istore_3
    //   85: aload 5
    //   87: ldc_w 264
    //   90: invokestatic 270	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   93: astore 11
    //   95: aload 7
    //   97: new 272	java/lang/StringBuilder
    //   100: dup
    //   101: invokespecial 273	java/lang/StringBuilder:<init>	()V
    //   104: ldc_w 275
    //   107: invokevirtual 279	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: aload 11
    //   112: invokevirtual 279	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 280	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: putfield 166	android/support/v4/text/util/LinkifyCompat$LinkSpec:url	Ljava/lang/String;
    //   121: aload_0
    //   122: aload 7
    //   124: invokevirtual 240	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   127: pop
    //   128: goto -121 -> 7
    //   131: astore 4
    //   133: return
    //   134: astore 10
    //   136: goto -129 -> 7
    //   139: return
    //
    // Exception table:
    //   from	to	target	type
    //   7	13	131	java/lang/UnsupportedOperationException
    //   18	26	131	java/lang/UnsupportedOperationException
    //   32	77	131	java/lang/UnsupportedOperationException
    //   85	95	131	java/lang/UnsupportedOperationException
    //   95	128	131	java/lang/UnsupportedOperationException
    //   85	95	134	java/io/UnsupportedEncodingException
  }

  private static String makeUrl(@NonNull String paramString, @NonNull String[] paramArrayOfString, Matcher paramMatcher, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    if (paramTransformFilter != null)
      paramString = paramTransformFilter.transformUrl(paramMatcher, paramString);
    for (int i = 0; ; i++)
    {
      int j = paramArrayOfString.length;
      int k = 0;
      if (i < j)
      {
        String str1 = paramArrayOfString[i];
        int m = paramArrayOfString[i].length();
        if (!paramString.regionMatches(true, 0, str1, 0, m))
          continue;
        k = 1;
        String str2 = paramArrayOfString[i];
        int n = paramArrayOfString[i].length();
        if (!paramString.regionMatches(false, 0, str2, 0, n))
          paramString = paramArrayOfString[i] + paramString.substring(paramArrayOfString[i].length());
      }
      if ((k == 0) && (paramArrayOfString.length > 0))
        paramString = paramArrayOfString[0] + paramString;
      return paramString;
    }
  }

  private static final void pruneOverlaps(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable)
  {
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    for (int i = 0; i < arrayOfURLSpan.length; i++)
    {
      LinkSpec localLinkSpec1 = new LinkSpec();
      localLinkSpec1.frameworkAddedSpan = arrayOfURLSpan[i];
      localLinkSpec1.start = paramSpannable.getSpanStart(arrayOfURLSpan[i]);
      localLinkSpec1.end = paramSpannable.getSpanEnd(arrayOfURLSpan[i]);
      paramArrayList.add(localLinkSpec1);
    }
    Collections.sort(paramArrayList, COMPARATOR);
    int j = paramArrayList.size();
    int k = 0;
    while (k < j - 1)
    {
      LinkSpec localLinkSpec2 = (LinkSpec)paramArrayList.get(k);
      LinkSpec localLinkSpec3 = (LinkSpec)paramArrayList.get(k + 1);
      int m = -1;
      if ((localLinkSpec2.start <= localLinkSpec3.start) && (localLinkSpec2.end > localLinkSpec3.start))
      {
        if (localLinkSpec3.end <= localLinkSpec2.end)
          m = k + 1;
        while (true)
        {
          if (m == -1)
            break label294;
          URLSpan localURLSpan = ((LinkSpec)paramArrayList.get(m)).frameworkAddedSpan;
          if (localURLSpan != null)
            paramSpannable.removeSpan(localURLSpan);
          paramArrayList.remove(m);
          j--;
          break;
          if (localLinkSpec2.end - localLinkSpec2.start > localLinkSpec3.end - localLinkSpec3.start)
          {
            m = k + 1;
            continue;
          }
          if (localLinkSpec2.end - localLinkSpec2.start >= localLinkSpec3.end - localLinkSpec3.start)
            continue;
          m = k;
        }
      }
      label294: k++;
    }
  }

  private static class LinkSpec
  {
    int end;
    URLSpan frameworkAddedSpan;
    int start;
    String url;
  }

  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface LinkifyMask
  {
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.text.util.LinkifyCompat
 * JD-Core Version:    0.6.0
 */