package net.printer.sdk.task;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 14:21
 * @UpdateRemark: 更新说明：
 */

import android.os.AsyncTask;

import net.printer.sdk.common.BackgroundInit;
import net.printer.sdk.common.TaskCallback;

public class PosAsynncTask extends AsyncTask<Void, Void, Boolean> {
    TaskCallback callback;

    BackgroundInit init;

    public PosAsynncTask(TaskCallback callback, BackgroundInit init) {
        this.callback = callback;
        this.init = init;
    }

    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        result = this.init.doinbackground();
        return Boolean.valueOf(result);
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result.booleanValue()) {
            this.callback.OnSucceed();
            cancel(true);
        } else {
            this.callback.OnFailed();
            cancel(true);
        }
    }
}