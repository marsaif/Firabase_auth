package activities;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewSignIn ;
    private FirebaseAuth mAuth ;
    private EditText editTextEmail , editTextPassword ;
    private String email , password ;
    private Button btnSignUp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_email) ;
        editTextPassword = findViewById(R.id.editText_password) ;
        textViewSignIn = findViewById(R.id.textView_sign_in);
        btnSignUp = findViewById(R.id.button_sign_up) ;

        textViewSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);


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

        if (password.length()<6)
        {
            editTextPassword.setError("Password must be at least 6 chars");
            return false ;
        }

        return true ;

    }

    public void SignUp()
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign Up success

                            Intent intent = new Intent(SignUpActivity.this , LoginActivity.class) ;
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            try
                            {
                                throw task.getException();
                            }

                            // if the user enters a weak password ,  we already tested the length of the password
                            // on checkFields methode
                            catch (FirebaseAuthWeakPasswordException weakPasswordException)
                            {
                                Toast.makeText(SignUpActivity.this, "Weak Password", Toast.LENGTH_SHORT).show();
                            }

                            // if the user enters a malformed email , we already tested the format of email on checkFields methode
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Toast.makeText(SignUpActivity.this, "check your email format ", Toast.LENGTH_SHORT).show();
                            }

                            // if the user enters an email already exist
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Toast.makeText(SignUpActivity.this, "Email already exist", Toast.LENGTH_SHORT).show();
                            }

                            // other exception
                            catch (Exception e)
                            {
                                Log.d("error Sign up" , e.getMessage()) ;
                            }

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.button_sign_up:
                if(checkFields())
                {
                    SignUp();
                }
                break;

            case R.id.textView_sign_in:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }

    }
}