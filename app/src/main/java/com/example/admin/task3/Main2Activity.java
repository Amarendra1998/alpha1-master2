package com.example.admin.task3;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_SHORT;

public class Main2Activity extends AppCompatActivity {
    ProgressDialog mprogressDialog;
    private TextInputEditText mname,email,confirmpass,pass,mstatus;
    private Button register;
    private DatabaseReference muserdatabase;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mcurrentuser;
     private String superb;
     private String superbr;
     private String superbs;
     private String superbed;
     private String superbrs;
     private String str,strs,stre,strd,strw;
    public Main2Activity() {
        mprogressDialog = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (!isConnected(Main2Activity.this))builddialogue(Main2Activity.this).show();
         else {
            Toast.makeText(Main2Activity.this,"you are connected to wifi or mobile data",Toast.LENGTH_SHORT).show();
            }
        mname = (TextInputEditText)findViewById(R.id.edittext);
        mstatus = (TextInputEditText)findViewById(R.id.edittextq);
        email = (TextInputEditText)findViewById(R.id.edittextd);
        pass = (TextInputEditText)findViewById(R.id.edittextc);
        confirmpass = (TextInputEditText)findViewById(R.id.edittexte);
        register = (Button)findViewById(R.id.button6);
        textView = (TextView)findViewById(R.id.textView2);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        str = sharedPreferences.getString("superbr","");
        mname.setText(str);
        strs = sharedPreferences.getString("superbed","");
        email.setText(strs);
        stre = sharedPreferences.getString("superb","");
        pass.setText(stre);
        strd = sharedPreferences.getString("superbs","");
        confirmpass.setText(strd);
        strw = sharedPreferences.getString("superbrs","");
        mstatus.setText(strw);

        firebaseAuth = FirebaseAuth.getInstance();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myne = new Intent(Main2Activity.this,LginActivity.class);
                startActivity(myne);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mname.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmpass.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your confirmed password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mstatus.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your status",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmpass.getText().toString().matches(pass.getText().toString()))
                {
                    Toast.makeText(Main2Activity.this,"your password is confirmed",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Main2Activity.this,"password does not match",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                register_user(mname,email,pass,confirmpass,mstatus);
            }
        });
    }

    @Override
    protected void onDestroy() {
        superbr = mname.getText().toString();
        superbed = email.getText().toString();
        superb = pass.getText().toString();
        superbs = confirmpass.getText().toString();
        superbrs = mstatus.getText().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("superbr",superbr);
        editor.putString("superbed",superbed);
        editor.putString("superb",superb);
        editor.putString("superbs",superbs);
        editor.putString("superbrs",superbrs);
        editor.apply();
        super.onDestroy();
    }


    private void register_user(final TextInputEditText mname, final TextInputEditText email, final TextInputEditText pass, TextInputEditText confirmpass, final TextInputEditText mstatus) {
        mprogressDialog = ProgressDialog.show(Main2Activity.this, "Please wait...", "processing", true);
        (firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mprogressDialog.dismiss();
                if (task.isSuccessful()) {
                    mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
                    assert mcurrentuser != null;
                    String current_uid = mcurrentuser.getUid();
                    muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String,String> usermap = new HashMap<>();
                    usermap.put("name", mname.getText().toString());
                    usermap.put("email", email.getText().toString());
                    usermap.put("status",mstatus.getText().toString());
                    usermap.put("password",pass.getText().toString());
                    usermap.put("device_token",device_token);
                    usermap.put("user_id",current_uid);
                    muserdatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Main2Activity.this,"you signup successfully",Toast.LENGTH_SHORT).show();
                            Intent m=new Intent(Main2Activity.this,LginActivity.class);
                            m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(m);
                        }
                    });

                } else {
                    Log.e("Error", task.getException().toString());
                    Toast.makeText(Main2Activity.this, task.getException().getMessage(), LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setMessage("Do you really wanna exit");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
