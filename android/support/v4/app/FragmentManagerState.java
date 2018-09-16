package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

final class FragmentManagerState
  implements Parcelable
{
  public static final Parcelable.Creator<FragmentManagerState> CREATOR = new Parcelable.Creator()
  {
    public FragmentManagerState createFromParcel(Parcel paramParcel)
    {
      return new FragmentManagerState(paramParcel);
    }

    public FragmentManagerState[] newArray(int paramInt)
    {
      return new FragmentManagerState[paramInt];
    }
  };
  FragmentState[] mActive;
  int[] mAdded;
  BackStackState[] mBackStack;
  int mNextFragmentIndex;
  int mPrimaryNavActiveIndex = -1;

  public FragmentManagerState()
  {
  }

  public FragmentManagerState(Parcel paramParcel)
  {
    this.mActive = ((FragmentState[])paramParcel.createTypedArray(FragmentState.CREATOR));
    this.mAdded = paramParcel.createIntArray();
    this.mBackStack = ((BackStackState[])paramParcel.createTypedArray(BackStackState.CREATOR));
    this.mPrimaryNavActiveIndex = paramParcel.readInt();
    this.mNextFragmentIndex = paramParcel.readInt();
  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedArray(this.mActive, paramInt);
    paramParcel.writeIntArray(this.mAdded);
    paramParcel.writeTypedArray(this.mBackStack, paramInt);
    paramParcel.writeInt(this.mPrimaryNavActiveIndex);
    paramParcel.writeInt(this.mNextFragmentIndex);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.FragmentManagerState
 * JD-Core Version:    0.6.0
 */