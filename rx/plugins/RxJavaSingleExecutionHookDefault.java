package rx.plugins;

final class RxJavaSingleExecutionHookDefault extends RxJavaSingleExecutionHook
{
  private static final RxJavaSingleExecutionHookDefault INSTANCE = new RxJavaSingleExecutionHookDefault();

  public static RxJavaSingleExecutionHook getInstance()
  {
    return INSTANCE;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaSingleExecutionHookDefault
 * JD-Core Version:    0.6.0
 */