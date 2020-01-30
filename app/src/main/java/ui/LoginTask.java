package ui;

import android.os.AsyncTask;
import model.LoginRequest;
import model.Model;

import java.util.ArrayList;
import java.util.List;

public class LoginTask extends AsyncTask<LoginRequest, Void, Boolean> {

    interface LoginTaskListener {
        void taskCompleted(Boolean result);
    }

    private final List<LoginTaskListener> listeners = new ArrayList<>();

    void registerListener(LoginTaskListener listener) {
        listeners.add(listener);
    }

    private void fireTaskCompleted(boolean response) {
        for (LoginTaskListener listener: listeners) {
            listener.taskCompleted(response);
        }
    }

    @Override
    public Boolean doInBackground(LoginRequest... requests) {

        Model model = Model.initialize();
        return model.login(requests[0].getUserName(), requests[0].getPassword());

    }

    @Override
    protected void onPostExecute(Boolean result) {
        fireTaskCompleted(result);
    }
}
