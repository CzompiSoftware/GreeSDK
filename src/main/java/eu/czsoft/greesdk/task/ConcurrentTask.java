package eu.czsoft.greesdk.task;

import lombok.extern.java.Log;

import java.util.concurrent.*;

@Log
public abstract class ConcurrentTask<TParams, TProgress, TResult> {

    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 128, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

    private boolean mIsInterrupted = false;
    private TResult _result = null;

    protected void onPreExecute(){}
    protected abstract TResult doInBackground(TParams... params);
    protected void onPostExecute(){}
    protected void onCancelled() {}

    public TResult get() {
        return _result;
    }

    @SafeVarargs
    public final void execute(TParams... params) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            try {
                checkInterrupted();
                backgroundExecutor.execute(this::onPreExecute);

                checkInterrupted();
                _result = doInBackground(params);

                checkInterrupted();
                backgroundExecutor.execute(this::onPostExecute);
            } catch (InterruptedException ex) {
                backgroundExecutor.execute(this::onCancelled);
            } catch (Exception ex) {
                LOGGER.severe("Execution error occurred. Description: " + ex.getMessage() + "\n Exception:\n" + ex.toString());
            }
        });
    }

    private void checkInterrupted() throws InterruptedException {
        if (isInterrupted()){
            throw new InterruptedException();
        }
    }

    public void cancel(boolean mayInterruptIfRunning){
        setInterrupted(mayInterruptIfRunning);
    }

    public boolean isInterrupted() {
        return mIsInterrupted;
    }

    public void setInterrupted(boolean interrupted) {
        mIsInterrupted = interrupted;
    }
}