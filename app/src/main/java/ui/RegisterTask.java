package ui;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class RegisterTask extends AsyncTask<RegisterRequest, Void, Boolean> {


    interface RegisterTaskListener {
        void taskCompleted(Boolean result);
    }

    private final List<RegisterTaskListener> listeners = new ArrayList<>();

    void registerListener(RegisterTaskListener listener){
        listeners.add(listener);
    }

    private void fireTaskCompleted(boolean response){
        for (RegisterTaskListener listener: listeners) {
            listener.taskCompleted(response);
        }
    }

    @Override
    public Boolean doInBackground(RegisterRequest... requests) {

        model.Model model = Model.initialize();
        return model.register(requests[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        fireTaskCompleted(result);
    }
}
