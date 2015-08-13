package com.example.android.mytodoapp.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by aude on 01/07/2015.
 */
public abstract class WorkerThread extends Thread
{
    private boolean isCanceled=false;

    protected boolean isCancelled()
    {
        return isCanceled;
    }

    private Context context;
    private ProgressDialog mProgressDialog;
    private String result;
    private long totalDuration = 0;
    private boolean isProgressBarEnabled = false;
    private String progressBarMsg;
    private Handler startHandler, finishHandler;

    public WorkerThread(Context context)
    {
        this.context = context;
        startHandler = new Handler();
        finishHandler = new Handler();
        progressBarMsg = "Please wait...";
    }

    public void enableProgressBar(boolean isEnabled)
    {
        this.isProgressBarEnabled = isEnabled;
    }

    public void setProgressBarText(String progressBarMsg)
    {
        if (progressBarMsg != null)
            this.progressBarMsg = progressBarMsg;
    }

    /**
     * Returns the time taken by the worker thread to execute given task.
     *
     * @return totalDuration time in milliseconds execute the task.
     */
    public long getWorkTime()
    {
        return totalDuration;
    }

    /**
     *
     * @return context of the worker thread.
     */
    public Context getContext()
    {
        return context;
    }

    /**
     * This method is called before onWorkInBackground(). All the tasks in this
     * method will be performed on UI thread.
     */
    protected void onWorkStarted()
    {

        synchronized (this)
        {
            if (isProgressBarEnabled)
            {
                try
                {
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage(progressBarMsg);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                } catch (Exception e)
                {
                }
            }
        }
    };

    /**
     * This method does all the work in a separate background thread, please do
     * not perform any UI related tasks in this method. For UI related tasks,
     * please use onWorkStarted() and onWorkFinished() methods.
     */

    public abstract String onWorkInBackground();

    /**
     * This method is called after onWorkInBackground(). All the tasks in this
     * method will be performed on UI thread.
     */
    protected synchronized void onWorkFinished(String result)
    {
        if (isProgressBarEnabled)
        {
            try
            {
                mProgressDialog.dismiss();
            } catch (Exception e)
            {
            }
        }
    };


    protected synchronized void onWorkCancelled(String result)
    {
        if (isProgressBarEnabled)
        {
            try
            {
                mProgressDialog.dismiss();
            } catch (Exception e)
            {
            }
        }
    };

    @Override
    public void run()
    {

        startHandler.post(new Runnable()
        {

            @Override
            public void run()
            {
                onWorkStarted();
            }
        });

        long ms = System.nanoTime();

        result = onWorkInBackground();

        totalDuration = (System.nanoTime() - ms) / 1000000;
//        Log.d("log_tag", String.format("Process ended in %d ms", totalDuration));

        finishHandler.post(new Runnable()
        {

            @Override
            public void run()
            {
                if(isCanceled)
                {
                    onWorkCancelled(result);
                }
                else
                {
                    onWorkFinished(result);
                }
            }
        });
    }

    public void execute()
    {
        start();
    }

    public void cancel(boolean b)
    {
        // TODO Auto-generated method stub
        isCanceled=true;
    }
}
