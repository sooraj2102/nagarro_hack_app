package android.support.v4.app;

import java.util.List;

public class FragmentManagerNonConfig
{
  private final List<FragmentManagerNonConfig> mChildNonConfigs;
  private final List<Fragment> mFragments;

  FragmentManagerNonConfig(List<Fragment> paramList, List<FragmentManagerNonConfig> paramList1)
  {
    this.mFragments = paramList;
    this.mChildNonConfigs = paramList1;
  }

  List<FragmentManagerNonConfig> getChildNonConfigs()
  {
    return this.mChildNonConfigs;
  }

  List<Fragment> getFragments()
  {
    return this.mFragments;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.FragmentManagerNonConfig
 * JD-Core Version:    0.6.0
 */