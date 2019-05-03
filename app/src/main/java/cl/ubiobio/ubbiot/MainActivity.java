package cl.ubiobio.ubbiot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements FragmentTemperatura.OnFragmentInteractionListener, FragmentRadiacion.OnFragmentInteractionListener,FragmentHumedad.OnFragmentInteractionListener {
    FragmentTemperatura fragmentTemperatura;
    FragmentRadiacion fragmentRadiacion;
    FragmentHumedad fragmentHumedad;

    int cont=0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    transaction.replace(R.id.contenedorFragments,fragmentTemperatura).commit();
                    return true;

                case R.id.navigation_dashboard:
                  //  mTextMessage.setText("Radiación");
                    //getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, fragmentRadiacion).commit();
                    transaction.replace(R.id.contenedorFragments,fragmentRadiacion).commit();

                    return true;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.contenedorFragments,fragmentHumedad).commit();
                    return true;
            }
            transaction.commit();

            return false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        fragmentTemperatura= new FragmentTemperatura();
        fragmentRadiacion = new FragmentRadiacion();
        fragmentHumedad= new FragmentHumedad();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, fragmentTemperatura).commit();
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Esta seguro que desea salir?")
                .setTitle("Advertencia")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });

        AlertDialog dialogo = builder.create();
        dialogo.show();;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    /*
    public void onClick(View view){

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.navigation_home:
            transaction.replace(R.id.contenedorFragments,fragmentTemperatura);
            break;
            case R.id.navigation_dashboard:
                transaction.replace(R.id.contenedorFragments,fragmentRadiacion);
            break;
            case R.id.navigation_notifications:
                transaction.replace(R.id.contenedorFragments,fragmentHumedad);
            break;


        }
        transaction.commit();
    }
    */
}
