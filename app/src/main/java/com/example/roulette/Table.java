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
    private TextView textView,user_amount,bet_view;
    private Button spin,bet;
    private ImageView roulette_image;
    private Random r;
    private int degree ;
    private static int NUMBER;
    private boolean isSpinning = false;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String UserID,str_bets;
    // 0 32 15 19 4 21 2 25 17 34 6 27 13 36 11 30 8 23 10 5 24 16 33 1 20 14 31 9 22 18 29 7 28 12 35 3 26
    private static final String [] sectors = {"32 red","15 black","19 red","4 black","21 red","2 black",
            "25 red","17 black","34 red","6 black","27 red","13 black","36 red","11 black","30 red","8 black",
            "23 red","10 black","5 red","24 black","16 red","33 black","1 red","20 black","14 red","31 black",
            "9 red","22 black","18 red","29 black","7 red","28 black","12 red","35 black","3 red","0"};
    private static final int [] numbers = {32,15,19,4,21,2,25,17,34,6,27,13,36,11,30,8,23,10,5,24,
                                            16,33,1,20,14,31,9,22,18,29,7,28,12,35,3,26,0};
    private static final int [] sectorsDegrees = new int [sectors.length];
    private static  final Random random = new Random();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        roulette_image = findViewById(R.id.imageView) ;
        bet_view = findViewById(R.id.User_bet);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        user_amount =  findViewById(R.id.user_amount_play);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        UserID = user.getUid();
        textView = findViewById(R.id.textView7) ;
        spin = findViewById(R.id.spin);
         str_bets = "";

                reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user_amount.setText(snapshot.child("balance").getValue().toString());
                        for(int i = 0; i<37; i++){

                            if(Integer.parseInt(snapshot.child("bet").child(""+i).getValue().toString()) > 0) {
                                str_bets += i +", ";
                            }
                            }
                        bet_view.setText(str_bets);
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        getDegree();
        r = new Random();

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        String number = sectors[sectors.length - (degree + 1)];
                        textView.setText(number);
                        NUMBER = numbers[sectors.length - (degree + 1)];
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String str2 = snapshot.child("bet").child(""+NUMBER).getValue().toString();
                                int win = Integer.parseInt(str2)*32;
                                if(win > 0 ){
                                    int new_amount = win + Integer.parseInt(snapshot.child("balance").getValue().toString());
                                    reference.child(UserID).child("balance").setValue(""+new_amount);
                                    Toast.makeText(Table.this,"win!!!, your prize is "+win+"$",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(Table.this,"LOSER!",Toast.LENGTH_LONG).show();
                                }
                                for(int i = 0; i<37; i++){
                                    reference.child(UserID).child("bet").child(""+i).setValue("0");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                        isSpinning = false;
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                roulette_image.startAnimation(rotate);
            }
        });
            bet = findViewById(R.id.bet);
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

}
