package activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firabase_auth.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private TextView textViewSignUp ;
    private FirebaseAuth mAuth ;
    private EditText editTextEmail , editTextPassword ;
    private String email , password ;
    private Button btnLogin ;
    private SignInButton signInButtonGoogle ;
    GoogleSignInClient mGoogleSignInClient ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_email) ;
        editTextPassword = findViewById(R.id.editText_password) ;
        btnLogin = findViewById(R.id.button_sign_in) ;
        textViewSignUp = findViewById(R.id.text_view_sign_up) ;
        signInButtonGoogle = findViewById(R.id.sign_in_button) ;

        signInButtonGoogle.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    public boolean checkFields()
    {
        email = editTextEmail.getText().toString().trim() ;
        password = editTextPassword.getText().toString().trim() ;

        if (email.isEmpty())
        {
            editTextEmail.setError("Email must not be empty");
            return false ;
        }

        // check email format

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Verify your email format");
            return false ;
        }

        if (password.isEmpty())
        {
            editTextPassword.setError("Password must not be empty");
            return false ;
        }

        return true ;

    }

    public void signIn()
    {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this , HomeActivity.class) ;
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {
                                Toast.makeText(LoginActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Log.d("Exception : ", e.getMessage());
                            }
                        }
                    }
                });
    }

    public void signInGoogle()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void updateUI()
    {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.button_sign_in:
                if (checkFields()) {
                    signIn();
                }
                break;

            case R.id.text_view_sign_up:
                Intent intent = new Intent(this,SignUpActivity.class);
                startActivity(intent);
                break;

            case R.id.sign_in_button:
                signInGoogle();
                break;
        }
    }
}