package com.example.admin.task3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LginActivity extends AppCompatActivity {
    ProgressDialog mprogressDialog;
    private TextInputEditText email,pass;
    private Button logbtn;
    public String superb;
    public String superbed;
    private String strs,stre;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference muserdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lgin);
        if (!isConnected(LginActivity.this))builddialogue(LginActivity.this).show();
        else {
            Toast.makeText(LginActivity.this,"you are connected to wifi or mobile data",Toast.LENGTH_SHORT).show();
        }
        email = (TextInputEditText)findViewById(R.id.edittextd);
        pass = (TextInputEditText)findViewById(R.id.edittextc);
        muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        logbtn = (Button)findViewById(R.id.button2);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        strs = sharedPreferences.getString("superbed","");
        email.setText(strs);
        stre = sharedPreferences.getString("superb","");
        pass.setText(stre);
        firebaseAuth = FirebaseAuth.getInstance();
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(LginActivity.this,"please enter your email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(LginActivity.this,"please enter your password",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginuser(email,pass);
            }
        });
    }

    @Override
    protected void onDestroy() {
        superbed = email.getText().toString();
        superb = pass.getText().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LginActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("superbed",superbed);
        editor.putString("superb",superb);
        editor.apply();
        super.onDestroy();
    }

    private void loginuser(TextInputEditText email, TextInputEditText pass) {
        mprogressDialog = ProgressDialog.show(LginActivity.this, "Please wait...", "processing", true);
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String current_user_id = firebaseAuth.getCurrentUser().getUid();
                    String devicetoken = FirebaseInstanceId.getInstance().getToken();
                    muserdatabase.child(current_user_id).child("device_token").setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent m=new Intent(LginActivity.this,Welcome.class);
                            Toast.makeText(LginActivity.this,"Successfully login",Toast.LENGTH_SHORT).show();
                            m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(m);
                            sendtowelcome();
                        }
                    });

                }else {
                    Toast.makeText(LginActivity.this,"Couldn't Login, Somthing went wrong",Toast.LENGTH_SHORT).show();
                    mprogressDialog.dismiss();
                }
            }
        });
    }

    private void sendtowelcome() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            Intent m=new Intent(LginActivity.this,Welcome.class);
            startActivity(m);
        }else {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(LginActivity.this, "Verification email sent to :" + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LginActivity.this, "Fail to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
    public boolean isConnected(Context context){
        ConnectivityManager connectivityManager =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile!=null && mobile.isConnectedOrConnecting())||(wifi!=null&&wifi.isConnectedOrConnecting()))return true;
            else return false;
        }else
            return false;
    }
    public AlertDialog.Builder builddialogue(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have mobile data or wifi,press ok to exit");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        return builder;
    }
}
