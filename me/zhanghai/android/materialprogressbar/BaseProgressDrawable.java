package me.zhanghai.android.materialprogressbar;

abstract class BaseProgressDrawable extends BasePaintDrawable
  implements IntrinsicPaddingDrawable
{
  protected boolean mUseIntrinsicPadding = true;

  public boolean getUseIntrinsicPadding()
  {
    return this.mUseIntrinsicPadding;
  }

  public void setUseIntrinsicPadding(boolean paramBoolean)
  {
    if (this.mUseIntrinsicPadding != paramBoolean)
    {
      this.mUseIntrinsicPadding = paramBoolean;
      invalidateSelf();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.BaseProgressDrawable
 * JD-Core Version:    0.6.0
 */