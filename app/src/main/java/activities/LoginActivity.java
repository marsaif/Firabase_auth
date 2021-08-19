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
import android.widget.Toast;

import com.example.firabase_auth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth ;
    private EditText editTextEmail , editTextPassword ;
    private String email , password ;
    private Button btnLogin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_email) ;
        editTextPassword = findViewById(R.id.editText_password) ;
        btnLogin = findViewById(R.id.button_sign_in) ;

        btnLogin.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.button_sign_in:
                if (checkFields()) {
                    signIn();
                }
        }
    }
}