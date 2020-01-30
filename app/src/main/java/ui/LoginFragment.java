package ui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.text.PrecomputedTextCompat;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.familymap.R;
import model.LoginRequest;
import model.Model;
import model.RegisterRequest;

import java.net.URI;

public class LoginFragment extends Fragment implements RegisterTask.RegisterTaskListener, LoginTask.LoginTaskListener {

    private Model model;
    private LoginListener listener;
    private Button mSignInButton;
    private Button mRegisterButton;
    private EditText ipAdress;
    private EditText portNum;
    private EditText username;
    private EditText password;
    private EditText fName;
    private EditText lName;
    private EditText email;

    private String gender;

    String taskType;


    private RadioGroup radioGenderGroup;
    private RadioButton genderButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.login_frag, container, false);


        model = Model.initialize();
        ipAdress = (EditText) v.findViewById(R.id.host);
        portNum = (EditText) v.findViewById(R.id.port);
        username = (EditText) v.findViewById(R.id.username);
        password = (EditText) v.findViewById(R.id.passwordIn);
        fName = v.findViewById(R.id.fName);
        lName = v.findViewById(R.id.lName);
        email = v.findViewById(R.id.emailIn);

        ipAdress.addTextChangedListener(listenerTextChangeStatus);
        portNum.addTextChangedListener(listenerTextChangeStatus);
        username.addTextChangedListener(listenerTextChangeStatus);
        password.addTextChangedListener(listenerTextChangeStatus);
        fName.addTextChangedListener(listenerTextChangeStatus);
        lName.addTextChangedListener(listenerTextChangeStatus);
        email.addTextChangedListener(listenerTextChangeStatus);

        radioGenderGroup = (RadioGroup) v.findViewById(R.id.genderGroup);
        radioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedGender = radioGenderGroup.getCheckedRadioButtonId();
                genderButton = (RadioButton) getView().findViewById(selectedGender);
                listenerTextChangeStatus.afterTextChanged(null);
            }
        });
        mSignInButton = (Button) v.findViewById(R.id.signIn);
        mRegisterButton = (Button) v.findViewById(R.id.register);

        mSignInButton.setEnabled(false);
        mRegisterButton.setEnabled(false);

        ipAdress.setText("10.37.40.1");
        portNum.setText("8080");
        username.setText("sheila");
        password.setText("parker");
        return v;
    }

    private TextWatcher listenerTextChangeStatus = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isSignInReady()){

                mSignInButton.setEnabled(true);
                mSignInButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(getContext(),"You clicked Sign In", Toast.LENGTH_LONG).show();

                        model.connect( ipAdress.getText().toString(), portNum.getText().toString());

                        LoginRequest request = new LoginRequest();
                        request.setUserName(username.getText().toString());
                        request.setPassword(password.getText().toString());

                        LoginTask loginTask = new LoginTask();
                        loginTask.registerListener(LoginFragment.this);
                        loginTask.execute(request);

                    }
                });
            }
            else {
                mSignInButton.setEnabled(false);
            }
            if (isRegisterReady()) {
                mRegisterButton.setEnabled(true);
                mRegisterButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), "You clicked Register", Toast.LENGTH_SHORT).show();
                        taskType = "register";
                        if (genderButton.getText().equals("Male")) {gender = "m";}
                        else { gender = "f"; }
                        model.connect(ipAdress.getText().toString(), portNum.getText().toString());

                        RegisterRequest request = new RegisterRequest();
                        request.setUserName(username.getText().toString());
                        request.setPassword(password.getText().toString());
                        request.setEmail(email.getText().toString());
                        request.setFirstName(fName.getText().toString());
                        request.setLastName(lName.getText().toString());
                        request.setGender(gender);

                        RegisterTask registerTask = new RegisterTask();
                        registerTask.registerListener(LoginFragment.this);
                        registerTask.execute(request);

                        Toast.makeText(getContext(), "debugging", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                mRegisterButton.setEnabled(false);
            }
        }

    };



    public boolean isLoggedIn(){return model.isLoggedIn();}

    private boolean isRegisterReady(){
        if (isSignInReady() &&
            !fName.getText().toString().isEmpty() &&
            !lName.getText().toString().isEmpty() &&
            genderButton != null) {
            return true;
        }
        return false;
    }

    private boolean isSignInReady(){
        if (!ipAdress.getText().toString().isEmpty() &&
                !portNum.getText().toString().isEmpty() &&
                !username.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void taskCompleted(Boolean result) {
        if (result){
            listener.login(true);
        }
        else {
            Toast.makeText(getContext(),"Information Incorrect", Toast.LENGTH_LONG).show();
        }
    }

    public Model getModel() {
        return this.model;
    }

    public interface LoginListener{
        void login(boolean isLoggedIn);

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof LoginListener){
            listener = (LoginListener) context;
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
