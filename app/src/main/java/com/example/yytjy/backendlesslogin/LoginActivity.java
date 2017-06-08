package com.example.yytjy.backendlesslogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


/**
 * Created by yytjy on 5/30/2017.
 */

public class LoginActivity extends AppCompatActivity {
    public static final String APPLICATION_ID = "02FC23DC-BA34-FE97-FF77-A54ED1203000";
    public static final String SECRET_KEY = "B9BF82A4-888A-1A5D-FFA0-C2A972584600";
    public static final String VERSION = "v1";
    public static final int REGISTER_REQUEST_CODE = 1;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView tigerIcon;
    private Button signIn;
    private EditText emailField, passwordField;
    private TextView forgetpassword, registration, guest,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Backendless.initApp(this, APPLICATION_ID, SECRET_KEY, VERSION);
        wireWidgets();
        onClickListener();
        makeRegistrationLink();
        makeGuestBrowseLink();
        makeResetPasswordLink();
    }

    /**
     * Wire every object in this class to its own xml id.
     */

    private void wireWidgets() {
        tigerIcon = (ImageView) findViewById(R.id.imageView);
        signIn = (Button) findViewById(R.id.button_signIn);
        emailField = (EditText) findViewById(R.id.editText_email);
        passwordField = (EditText) findViewById(R.id.editText_password);
        forgetpassword = (TextView) findViewById(R.id.textView_forgetPassword);
        registration = (TextView) findViewById(R.id.textView_signUp);
        guest = (TextView) findViewById(R.id.textView_guestBrowse);
        title = (TextView) findViewById(R.id.textView_tigerNewspaper);
    }

    /**
     * The method sets up onClickListener for signIn button.
     */

    private void onClickListener(){

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailField = (EditText) findViewById(R.id.editText_email);
                passwordField = (EditText) findViewById(R.id.editText_password);
                CharSequence email = emailField.getText();
                CharSequence password = passwordField.getText();

                Backendless.UserService.login( email.toString(), password.toString(), new AsyncCallback<BackendlessUser>()
                {
                    public void handleResponse( BackendlessUser user )
                    {
                        // user has been logged in
                        Log.d(TAG, "onCreate: User registration successful");
                        Toast.makeText(LoginActivity.this, "User login successful!",
                                Toast.LENGTH_SHORT).show();
                        afterSignIn();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        // login failed, to get the error code call fault.getCode()
                        Log.e(TAG, "onCreate: User login FAILED: " + fault.getCode());
                    }
                });
                //Backendless.UserService.loginWithFacebook();

            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestBrowse();
            }
        });
    }

    /**
     * The method makes link to reset password dialog via a clickableSpan.
     */

    private void makeResetPasswordLink() {
//        emailField = (EditText) findViewById(R.id.editText_email);
//        final CharSequence email = emailField.getText();
        SpannableString resetPasswordPrompt = new SpannableString(forgetpassword.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                View mView =  LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_resetpassword, null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this).setView(mView);

                final EditText email = (EditText) mView.findViewById(R.id.editText_emailConfirm);
                Button sentEmail = (Button) mView.findViewById(R.id.button_emailSent);
                sentEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetPassword(email);
                    }
                });

                //mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                mBuilder.show();
            }
        };

        String linkText = "Forget your password?";
        int linkStartIndex = resetPasswordPrompt.toString().indexOf(linkText);
        int linkEndIndex = linkStartIndex + linkText.length();
        resetPasswordPrompt.setSpan(clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView resetPasswordPromptView = (TextView) findViewById(R.id.textView_forgetPassword);
        resetPasswordPromptView.setText(resetPasswordPrompt);
        resetPasswordPromptView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * The method handles reset password request through backendless.
     * @param email
     */

    private void resetPassword(EditText email) {
        Log.d(TAG, "EMAIL ::: " + email.getText().toString());
        CharSequence emailAddress = email.getText();
        Backendless.UserService.restorePassword(emailAddress.toString(), new AsyncCallback<Void>()
        {
            public void handleResponse( Void response )
            {
                Log.d(TAG, "onCreate: Reset Password Email Sent successfully");
                Toast.makeText(LoginActivity.this, "Reset Password Email Sent successfully!",
                        Toast.LENGTH_SHORT).show();
                // Backendless has completed the operation - an email has been sent to the user
            }

            public void handleFault( BackendlessFault fault )
            {
                // password recovery failed, to get the error code call fault.getCode()
                Log.d(TAG, "onCreate: Reset Password Email not sent");
                Log.e(TAG, fault.getMessage());
                Toast.makeText(LoginActivity.this, "Reset Password Email not sent!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * The method allows user to browse news without logging in. It takes user to the main news browsing site via a clickableSpan.
     */

    private void makeGuestBrowseLink() {
        SpannableString guestBrowsePrompt = new SpannableString(guest.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                guestBrowse();
            }
        };

        String linkText = "Guest";
        int linkStartIndex = guestBrowsePrompt.toString().indexOf(linkText);
        int linkEndIndex = linkStartIndex + linkText.length();
        guestBrowsePrompt.setSpan(clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView guestBrowsePromptView = (TextView) findViewById(R.id.textView_guestBrowse);
        guestBrowsePromptView.setText(guestBrowsePrompt);
        guestBrowsePromptView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * The method links the user registration prompt to registration dialog via a clickableSpan.
     */

    private void makeRegistrationLink() {
        SpannableString registrationPrompt = new SpannableString(registration.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //startRegistrationLink();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_signup, null);
                final EditText email = (EditText) mView.findViewById(R.id.editText_email);
                final EditText password = (EditText) mView.findViewById(R.id.editText_password);
                Button signUp = (Button) mView.findViewById(R.id.button_signUp);
                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerUser(email, password);
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        };


        String linkText = "Register";
        int linkStartIndex = registrationPrompt.toString().indexOf(linkText);
        int linkEndIndex = linkStartIndex + linkText.length();
        registrationPrompt.setSpan(clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView registerPromptView = (TextView) findViewById(R.id.textView_signUp);
        registerPromptView.setText(registrationPrompt);
        registerPromptView.setMovementMethod(LinkMovementMethod.getInstance());


    }

    /**
     * This method takes the user ID (email address) and user's password from editTexts to register user via backendless.
     * @param email
     * @param password
     */

    private void registerUser(EditText email, EditText password) {
        BackendlessUser user = new BackendlessUser();
        Log.d(TAG, "registerUser: User: " + (user == null));
        //Reporting false so user is created, not null
        user.setEmail(email.getText().toString());
        Log.d(TAG, "registerUser: Password: " + (password == null));
        user.setPassword(password.getText().toString());
        //Log.d(TAG, "registerUser: after " + password.getText());

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse( BackendlessUser registeredUser )
            {
                // user has been registered and now can login
                Log.d(TAG, "onCreate: User registration successful");
                Toast.makeText(LoginActivity.this, "User registration successful!",
                        Toast.LENGTH_SHORT).show();
            }

            public void handleFault( BackendlessFault fault )
            {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Log.e(TAG, "onCreate: User registration FAILED: " + fault.getCode());
            }
        } );
    }


    /**
     * This method links LoginActivity to the news browse site via an intent.
     */

    private void guestBrowse() {
        Intent guestIntent = new Intent(this, MainActivity.class);
        startActivity(guestIntent);
    }

    /**
     * The method allows the user who has log on successfully to go to the news browse site.
     */

    private void afterSignIn() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }


}
