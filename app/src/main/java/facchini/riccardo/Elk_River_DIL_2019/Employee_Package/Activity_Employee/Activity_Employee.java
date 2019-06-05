package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Activity_Employee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import facchini.riccardo.Elk_River_DIL_2019.Activity_Login;
import facchini.riccardo.Elk_River_DIL_2019.Chat.Activity_Chat_Homepage;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Fragment_Employee.Fragment_Employee_FishingSpot;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Fragment_Employee.Fragment_Employee_History;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Fragment_Employee.Fragment_Employee_Home;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Activity_Employee extends AppCompatActivity
{
    private FirebaseAuth.AuthStateListener authStateListener;
    
    private byte backButton;
    private int currentMenu = R.id.bottomHome;
    
    private BottomNavigationView bottomMenu;
    private Menu topMenu;
    
    private Fragment selected;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        backButton = 0;
        setContentView(R.layout.activity_employee);
        currentMenu = R.id.bottomHome;
        
        bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setOnNavigationItemSelectedListener(selectedListener);
        
        bottomMenu.getMenu().getItem(2).setVisible(
                getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE)
                        .getBoolean(getString(R.string.current_employee_is_expert_key), false));
        
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Fragment_Employee_Home()).commit();
        setupFirebaseListener();
        
        final SharedPreferences sp = getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        
        if (sp.getString(getString(R.string.current_user_pic_key), "").isEmpty())
        {
            FirebaseFirestore.getInstance().collection("employees").document(sp.getString(getString(R.string.current_user_uid_key), "")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    sp.edit().putString(getString(R.string.current_user_pic_key), (String) documentSnapshot.get("profilePicUrl")).apply();
                }
            });
        }
        
    }
    
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            selected = null;
            
            currentMenu = menuItem.getItemId();
            
            switch (menuItem.getItemId())
            {
                case R.id.bottomHome:
                    selected = new Fragment_Employee_Home();
                    topMenu.getItem(1).setVisible(true);
                    break;
                case R.id.bottomHistory:
                    selected = new Fragment_Employee_History();
                    topMenu.getItem(1).setVisible(true);
                    break;
                case R.id.bottomNewFishing:
                    selected = new Fragment_Employee_FishingSpot();
                    topMenu.getItem(1).setVisible(false);
                    break;
            }
            
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selected).commit();
            return true;
        }
    };
    
    @Override
    public void onBackPressed()
    {
        if (currentMenu == R.id.profile_menu)
        {
            super.onBackPressed();
            currentMenu = bottomMenu.getSelectedItemId();
            return;
        }
        
        if (backButton > 0)
            finish();
        else
        {
            Toast.makeText(this, getString(R.string.pressBackToExit), Toast.LENGTH_LONG).show();
            backButton++;
            
            new CountDownTimer(2000, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished) {}
                
                @Override
                public void onFinish()
                {
                    backButton = 0;
                }
            }.start();
        }
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        if (authStateListener != null)
        {
            //On pause removes the listener for the authentication
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        //On resume adds again the listener for the authentication
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }
    
    private void setupFirebaseListener()
    {
        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                
                if (user == null)
                {
                    Toast.makeText(Activity_Employee.this, "Logging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), Activity_Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }
    
    
    /**
     * Shows the menu (3 dots) when touched
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        topMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        
        topMenu.getItem(2).setVisible(true);
        topMenu.getItem(3).setVisible(true);
        
        if (currentMenu != R.id.profile_menu)
            topMenu.getItem(1).setVisible(true);
        else
            topMenu.getItem(1).setVisible(false);
        
        return true;
    }
    
    /**
     * Action to perform when an option in the menu is selected
     *
     * @param item The selected option
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.sign_out_menu:
                SharedPreferences.Editor edit = getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE).edit();
                edit.remove(getString(R.string.current_user_uid_key));
                edit.remove(getString(R.string.isCustomer_key));
                edit.remove(getString(R.string.current_user_username_key)).apply();
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.chat_menu:
                Intent intent = new Intent(getBaseContext(), Activity_Chat_Homepage.class);
                startActivity(intent);
                return true;
            case R.id.refresh_menu:
                if (currentMenu == R.id.bottomHome)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Fragment_Employee_Home()).commit();
                else if (currentMenu == R.id.bottomHistory)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Fragment_Employee_History()).commit();
                return true;
            case R.id.profile_menu:
                currentMenu = R.id.profile_menu;
                Intent profileIntent = new Intent(getBaseContext(), Activity_Employee_Profile.class);
                startActivity(profileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
