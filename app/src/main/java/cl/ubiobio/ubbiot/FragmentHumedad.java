package cl.ubiobio.ubbiot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.SpeedView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.nitri.gauge.Gauge;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHumedad.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHumedad#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHumedad extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String tokenAcceso = "Rg9Efs0vuz";
    private String tokenHumedad = "VIbSnGKyLW";
    private String fecha;

    TextView promHumedad;
    TextView actualHumedad;
    int curValue;
    private int ultimoactual=-10;
    private int ultimopromedio=-10;
   // SpeedView speedView;
    Gauge gauge;

    private  int promedio ;
    private int sumaHumedad;
    private RequestQueue queue;
    Time today;
    String dia,mes,anio;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentHumedad() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHumedad.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHumedad newInstance(String param1, String param2) {
        FragmentHumedad fragment = new FragmentHumedad();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fragment_humedad, container, false);
        //setContentView(R.layout.activity_humedad);

        fecha = capturarFecha();
        promHumedad = (TextView)view.findViewById(R.id.promedioHumedad) ;
        actualHumedad = (TextView)view.findViewById(R.id.actualHumedad) ;
        //speedView = (SpeedView) view.findViewById(R.id.gaugeHumedad);
        gauge= (Gauge)view.findViewById(R.id.gaugeHumedad);
        //gauge();
        queue = Volley.newRequestQueue(getContext());

       // BottomNavigationView navHumedad = view.findViewById(R.id.nav_humedad);

        Button b1= view.findViewById(R.id.button3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ultimoDatoDelDia();
                obtenerPromedioDelDia();
            }
        });

        ultimoDatoDelDia();
        obtenerPromedioDelDia();

        //navHumedad.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return view;
    }

/*    public void gauge(){
        speedView.setWithTremble(false);
        speedView.setMaxSpeed(100);
        speedView.setUnit(" %RH");
    }*/

    public String capturarFecha(){

        today=new Time(Time.getCurrentTimezone());
        today.setToNow();
        dia=Integer.toString(today.monthDay);
        mes=Integer.toString(today.month+1);
        anio=Integer.toString(today.year);

        if(Integer.parseInt(dia)<10){
            dia="0"+dia;
        }

        if(Integer.parseInt(mes)<10){

            return ""+dia+"0"+mes+anio;
        }else{
            return ""+dia+mes+anio;

        }
    }

    private void ultimoDatoDelDia(){

        String url = "http://arrau.chillan.ubiobio.cl:8075/ubbiot/web/mediciones/medicionespordia/"+this.tokenAcceso+"/"+this.tokenHumedad+"/"+this.fecha;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //crear JSON array
                try {

                    JSONArray mJsonArray = response.getJSONArray("data");
                    JSONObject mJsonObject = mJsonArray.getJSONObject(mJsonArray.length()-1);
                    String valor = mJsonObject.getString("valor");
                    actualHumedad.setText("Humedad actual: "+valor+" %RH");
                    curValue= Integer.parseInt(valor);;
                   // speedView.setMaxSpeed(100);
                    ultimoactual=curValue;
                   // speedView.speedTo(curValue);
                    gauge.setValue(0);
                    gauge.moveToValue(curValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(ultimoactual>=0){
                            actualHumedad.setText("Humedad actual: "+ultimoactual+" %RH");
                            gauge.setValue(0);
                            gauge.moveToValue(ultimoactual);
                        }
                    }
                }

        );
        queue.add(request);
    }

    private void obtenerPromedioDelDia(){

        String url = "http://arrau.chillan.ubiobio.cl:8075/ubbiot/web/mediciones/medicionespordia/"+this.tokenAcceso+"/"+this.tokenHumedad+"/"+this.fecha;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                promedio=0;
                sumaHumedad=0;
                try {
                    JSONArray mJsonArray = response.getJSONArray("data");
                    for(int i=0; i < mJsonArray.length(); i++) {
                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                        String valor = mJsonObject.getString("valor");
                        int humedad = Integer.parseInt(valor);
                        sumaHumedad+=humedad;
                    }
                    promedio = sumaHumedad/mJsonArray.length();
                    ultimopromedio=promedio;
                    promHumedad.setText("Promedio del día: "+promedio+" %RH");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(ultimopromedio>=0){
                            promHumedad.setText("Promedio del día: "+ultimopromedio+" %RH");
                        }
                    }
                }
        );
        queue.add(request);
    }

    // otros metodos
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
