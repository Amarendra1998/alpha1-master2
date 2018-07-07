package com.example.admin.task3;

import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Welcome extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button refresh;
    private RecyclerView mUserlist;
    private Toolbar mtoolbar;
    private Long backpressed;
    private RelativeLayout relativeLayout;
    public boolean isUserclickedback = false;
    private DatabaseReference mdatabasereference;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAuth = FirebaseAuth.getInstance();
        refresh= (Button)findViewById(R.id.button3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mtoolbar = (Toolbar)findViewById(R.id.myappbar);
        mdatabasereference = FirebaseDatabase.getInstance().getReference().child("Users");
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());
        mUserlist = (RecyclerView)findViewById(R.id.users_list);
        mUserlist.addItemDecoration(dividerItemDecoration);
        mUserlist.setHasFixedSize(true);
        mUserlist.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("User Information");
        info();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().getCurrentUser()
                        .reload()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                info();
                            }
                        });
            }
        });
    }
    private void info() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            mUserlist.setVisibility(View.VISIBLE);
            Toast.makeText(Welcome.this," see your information ",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(Welcome.this,"email is not verified,please verify your email to see your information ",Toast.LENGTH_SHORT).show();
            mUserlist.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, UsersViewHolder>(
                users.class,
                R.layout.information,
                UsersViewHolder.class,
                mdatabasereference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setPassword(model.getPassword());
                viewHolder.setEmail(model.getEmail());
                final String user_id  = getRef(position).getKey();

            }
        };
        mUserlist.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==R.id.log){
            Intent m = new Intent(Welcome.this,Main2Activity.class);
            startActivity(m);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public UsersViewHolder(View itemView){
            super(itemView);
            mview=itemView;
        }

        public void setName(String name) {
            TextView userview = (TextView)mview.findViewById(R.id.textView3);
            userview.setText("Your Name:"+name);
        }

        public void setStatus(String status) {
            TextView userstatus = (TextView)mview.findViewById(R.id.textView7);
            userstatus.setText("Your Status:"+status);
        }


        public void setPassword(String password) {
            TextView userpass = (TextView)mview.findViewById(R.id.textView6);
            userpass.setText("Your Password:"+password);
        }
        public void setEmail(String email) {
            TextView useremail = (TextView)mview.findViewById(R.id.textView4);
            useremail.setText("Your Email:"+email);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isUserclickedback){
            Toast.makeText(getBaseContext()," Press again to exit ",Toast.LENGTH_SHORT).show();
            isUserclickedback=true;
        }else {
            super.onBackPressed();
        }
        new CountDownTimer(3000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                isUserclickedback=false;
            }
        }.start();

    }
}
