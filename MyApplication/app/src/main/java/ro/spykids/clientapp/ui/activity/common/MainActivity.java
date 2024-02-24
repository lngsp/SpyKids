package ro.spykids.clientapp.ui.activity.common;

import static ro.spykids.clientapp.util.Constants.LOCATION_REQUEST;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.VolleyError;
import com.auth0.android.jwt.JWT;

import java.util.Date;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AuthAPI;
import ro.spykids.clientapp.clientapi.EncryptionAPI;
import ro.spykids.clientapp.clientapi.UserAPI;
import ro.spykids.clientapp.pojo.User;
import ro.spykids.clientapp.ui.activity.child.ChildMainActivity;
import ro.spykids.clientapp.ui.activity.parent.ParentMainActivity;
import ro.spykids.clientapp.util.Constants;
import ro.spykids.clientapp.util.TokenManager;

public class MainActivity extends AppCompatActivity {

    //Variables
    Animation topAnimation, bottomAnimation;
    ImageView image;
    TextView logo, slogan;
    private UserAPI userAPI = new UserAPI();
    private AuthAPI authAPI = new AuthAPI();
    private  EncryptionAPI encryptionAPI = new EncryptionAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animation
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        image = this.findViewById(R.id.image);
        logo = this.findViewById(R.id.textViewLogo);
        slogan = this.findViewById(R.id.textViewSlogan);

        image.setAnimation(topAnimation);
        logo.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.reassuring);

        mediaPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String token = TokenManager.getInstance(getApplicationContext()).getToken();
                if (token != null) {
                    JWT jwt = new JWT(token);
                    //the email in jwt is encrypted -> we need to decrypt to give it to the next activity
                    String userEmail = jwt.getClaim("sub").asString();
                    Date expirationDate = jwt.getExpiresAt();
                    Date currentDate = new Date();

                    //decrypt the email
                    encryptionAPI.decrypt(getApplicationContext(), userEmail, new EncryptionAPI.DecryptCallback() {
                        @Override
                        public void onDecryptSuccess(String decryptedText) {
                            //we have the decryptedEmail, now we need to find if the expiration date is valid
                            // if its not valid -> logout the user and detele the token

                            if (currentDate.after(expirationDate)) {
                                Toast.makeText(MainActivity.this, "Tokenul is expired.", Toast.LENGTH_SHORT).show();
                                authAPI.logout(MainActivity.this, decryptedText);
                                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                                startActivity(intent);

                            }

                            //get user type
                            userAPI.getUserByEmail(getApplicationContext(), decryptedText, token, new UserAPI.UserResponseListener() {
                                @Override
                                public void onSuccess(User user) {
                                    //check user type
                                    if (user.getType().matches("PARENT")) {
                                        Intent intent = new Intent(MainActivity.this, ParentMainActivity.class);
                                        intent.putExtra("email", decryptedText);
                                        intent.putExtra("token", token);
                                        startActivity(intent);
                                    } else if (user.getType().matches("CHILD")) {
                                        Intent intent = new Intent(MainActivity.this, ChildMainActivity.class);
                                        intent.putExtra("email", decryptedText);
                                        intent.putExtra("token", token);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    //error on getting user
                                    Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onDecryptError(VolleyError error) {
                            //error on decrypt
                            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(intent);
                }
            }
        }, Constants.SPLASH_SCREEN);

    }

}