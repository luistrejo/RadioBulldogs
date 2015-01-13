package bulldogs.luistrejo.com.radiobulldogs;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;


public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Radio", "Chat"};

    SharedPreferences pref;
    SharedPreferences.Editor editor2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pref = getSharedPreferences("estatuslogin", MODE_PRIVATE);
        editor2 = pref.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager)findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
    //evitar que vuelva a la actividad de login
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //menu del action bar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //acciones del menu actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Calendario:
                Intent calendario = new Intent(this, Calendario.class);
                calendario.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(calendario);
                break;
            case R.id.Sugerencias:
                Intent sugerencias = new Intent(this, Sugerencias.class);
                sugerencias.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(sugerencias);
                break;
            case R.id.Acerca:
                Intent acerca = new Intent(this, Acerca.class);
                acerca.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(acerca);
                break;
            case R.id.Logout:
                Intent logout = new Intent(this, Login.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);

                // guardar true para login para no mostrar esta activity de nuevo
                editor2.putString("login","false");
                editor2.commit();

                //si esta activo el servicio de musica lo cerramos
                this.stopService(new Intent(this, Servicio.class));

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}