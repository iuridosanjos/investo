package e.investo.common;

import e.investo.OnLoadCompletedEventListener;

public class LoadingSemaphore {
    private int mSeekCount;
    private int mCurrentCount;
    private OnLoadCompletedEventListener mListener;
    private boolean mLoadFinished;

    public Object result;

    public LoadingSemaphore(int seekCount, OnLoadCompletedEventListener listener)
    {
        if (listener == null)
            throw new IllegalArgumentException("Listener required");

        mSeekCount = seekCount;
        mListener = listener;

        reset();

        // Se a quantidade de itens a serem carregados (seekCount) for zero, então não terá o que carregar. Pode finalizar.
        checkCompleted();
    }

    private void reset()
    {
        mCurrentCount = 0;
        mLoadFinished = false;
    }

    public void registerLoaded() {
        if (mLoadFinished)
            return;

        synchronized (this) {
            mCurrentCount++;
            checkCompleted();
        }
    }

    private void checkCompleted() {
        if (mCurrentCount == mSeekCount) {
            mLoadFinished = true;
            mListener.OnLoadCompleted(result);
        }
    }
}
