package com.example.roulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DeviceAdminService;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Table extends AppCompatActivity {
    TextView textView, textView12,user_amount;
    Button spin,bet;
    ImageView imageView;
    FirebaseFirestore db;
    Random r;
    private int degree ;
    private static int NUMBER;
    private boolean win = false;
    private boolean isSpinning = false;
    private FirebaseUser user,user2;
    private DatabaseReference reference,reference2;
    // 0 32 15 19 4 21 2 25 17 34 6 27 13 36 11 30 8 23 10 5 24 16 33 1 20 14 31 9 22 18 29 7 28 12 35 3 26
    private static final String [] sectors = {"32 red","15 black","19 red","4 black","21 red","2 black",
            "25 red","17 black","34 red","6 black","27 red","13 black","36 red","11 black","30 red","8 black",
            "23 red","10 black","5 red","24 black","16 red","33 black","1 red","20 black","14 red","31 black",
            "9 red","22 black","18 red","29 black","7 red","28 black","12 red","35 black","3 red","0"};
    private static final int [] numbers = {32,15,19,4,21,2,25,17,34,6,27,13,36,11,30,8,23,10,5,24,
                                            16,33,1,20,14,31,9,22,18,29,7,28,12,35,3,26,0};
    private static final int [] sectorsDegrees = new int [sectors.length];
    private HashMap<Integer,Integer> MAP,M;
    private  String BET_STRING,UserID;
    private EditText bet_amount;
    private static  final Random random = new Random();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
//        user_submit = (TextView) findViewById(R.id.usersubmit);
        imageView = (ImageView)findViewById(R.id.imageView) ;
        BET_STRING = "";
        reference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        user_amount = (TextView)findViewById(R.id.user_amount_play);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        UserID = user.getUid();
        MAP = new HashMap<Integer,Integer>();
        M = new HashMap<Integer,Integer>();
        textView = (TextView) findViewById(R.id.textView7) ;

        textView12 = (TextView)findViewById(R.id.textView12);

        spin = (Button) findViewById(R.id.spin);

                reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);
                        if(user1 != null){
        //                  user_amount is a Textbox
                            user_amount.setText(user1.balance);
        //                    user_submit.setText(user1.bet_display);
                        }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        getDegree();
        r = new Random();

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(!isSpinning){
                    spin();
                    isSpinning = true;


                }
            }

            private void spin() {
                degree = random.nextInt(sectors.length - 1);
                RotateAnimation rotate = new RotateAnimation(0, (360 * sectors.length) + sectorsDegrees[degree],
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(3600);
                rotate.setFillAfter(true);
                rotate.setInterpolator(new DecelerateInterpolator());
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        String number = sectors[sectors.length - (degree + 1)];
                        textView.setText(number);
                        NUMBER = numbers[sectors.length - (degree + 1)];
                        //if(win >0)
                        reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        db = FirebaseFirestore.getInstance();
                        db.collection("Users").document(UserID).get().addOnCompleteListener(task ->{
                            if(task.isSuccessful() && task.getResult() != null){

                                Toast.makeText(Table.this,"success, not null",Toast.LENGTH_LONG).show();

                                String number_bet = task.getResult().getString("bet");
                                Toast.makeText(Table.this,number_bet,Toast.LENGTH_LONG).show();
                            }
                        } );
                        reference.child(UserID).child("bet").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user1 = snapshot.getValue(User.class);
//                                Users_bet user2 = snapshot.getValue(Users_bet.class);


                                if (user1 != null) {

                                    Toast.makeText(Table.this, "not null", Toast.LENGTH_LONG).show();
                                    MAP.clear();
//                                    MAP.put(1,Integer.parseInt((user1.a1)));
//                                    MAP.put(0,Integer.parseInt(user1.a0));
//                                    MAP.put(2,Integer.parseInt(user1.a2));
//                                    MAP.put(3,Integer.parseInt(user1.a3));
//                                    MAP.put(4,Integer.parseInt(user1.a4));
//                                    MAP.put(5,Integer.parseInt(user1.a5));
//                                    MAP.put(6,Integer.parseInt(user1.a6));
//                                    MAP.put(7,Integer.parseInt(user1.a7));
//                                    MAP.put(8,Integer.parseInt(user1.a8));
//                                    MAP.put(9,Integer.parseInt(user1.a9));
//                                    MAP.put(10,Integer.parseInt(user1.a10));
//                                    MAP.put(11,Integer.parseInt(user1.a11));
//                                    MAP.put(12,Integer.parseInt(user1.a12));
//                                    MAP.put(13,Integer.parseInt(user1.a13));.
//                                    MAP.put(14,Integer.parseInt(user1.a14));
//                                    MAP.put(15,Integer.parseInt(user1.a15));
//                                    MAP.put(16,Integer.parseInt(user1.a16));
//                                    MAP.put(17,Integer.parseInt(user1.a17));
//                                    MAP.put(18,Integer.parseInt(user1.a18));
//                                    MAP.put(19,Integer.parseInt(user1.a19));
//                                    MAP.put(20,Integer.parseInt(user1.a20));
//                                    MAP.put(30,Integer.parseInt(user1.a30));
//                                    MAP.put(31,Integer.parseInt(user1.a31));
//                                    MAP.put(32,Integer.parseInt(user1.a32));
//                                    MAP.put(33,Integer.parseInt(user1.a33));
//                                    MAP.put(34,Integer.parseInt(user1.a34));
//                                    MAP.put(35,Integer.parseInt(user1.a35));
//                                    MAP.put(36,Integer.parseInt(user1.a36));

//                                    if(MAP.containsKey(NUMBER)) {
//                                        int prize = MAP.get(NUMBER) * 2;
//                                        Toast.makeText(Table.this,""+MAP.get(NUMBER),Toast.LENGTH_LONG).show();
//                                        int user_balance = Integer.parseInt(user2.balance);
//                                        reference.child(UserID).child("balance").setValue(user_balance + prize);
//                                    }




                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        isSpinning = false;

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }


                });







                imageView.startAnimation(rotate);



            }
        });

            bet = (Button) findViewById(R.id.bet);
            bet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Table.this,betTable.class));
                }
            });



    }

    private void getDegree(){
        int sectorDegree = 360/sectors.length;
        for (int i=0 ; i< sectors.length; i++){
            sectorsDegrees[i] = (i+1) * sectorDegree;
        }

    }
    public int getDrawnNumber (int number){
        return number;
    }




}
