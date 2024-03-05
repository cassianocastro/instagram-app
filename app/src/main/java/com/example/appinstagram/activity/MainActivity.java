package com.example.appinstagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.appinstagram.R;
import com.example.appinstagram.fragment.FeedFragment;
import com.example.appinstagram.fragment.PerfilFragment;
import com.example.appinstagram.fragment.PesquisaFragment;
import com.example.appinstagram.fragment.PostagemFragment;
import com.example.appinstagram.helper.ConfigFireBase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewInner;

/**
 *
 */
public class MainActivity extends AppCompatActivity
{

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar t = findViewById(R.id.toolbarPrincipal);

        t.setTitle("Instagram");

        setSupportActionBar(t);

        auth = ConfigFireBase.getFireBaseAuth();

        configBottomNavigationView();

        FragmentManager fragment = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragment.beginTransaction();

        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
    }

    private void configBottomNavigationView()
    {
        BottomNavigationViewEx bottom = findViewById(R.id.bottomNavigation);

        bottom.enableAnimation(true);
        bottom.enableItemShiftingMode(true);
        bottom.enableShiftingMode(false);
        bottom.setTextVisibility(true);

        // Habilitar a Navegação:
        habilitNavigation(bottom);

        /*
            Configurar item selecionado inicialmente:

            Menu menu = bottom.getMenu();
            MenuItem item = menu.getItem(1);
            item.setChecked(true);
        */
    }

    private void habilitNavigation(BottomNavigationViewEx viewEx)
    {
        viewEx.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                {
                    FragmentManager fragment = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragment.beginTransaction();

                    switch ( item.getItemId() )
                    {
                        case R.id.ic_home:
                            fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                            return true;
                        case R.id.ic_pesquisa:
                            fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                            return true;
                        case R.id.ic_postagem:
                            fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                            return true;
                        case R.id.ic_perfil:
                            fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                            return true;
                    }

                    return false;
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch ( item.getItemId() )
        {
            case R.id.menu_sair:
                deslogarUser();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUser()
    {
        try
        {
            auth.signOut();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}