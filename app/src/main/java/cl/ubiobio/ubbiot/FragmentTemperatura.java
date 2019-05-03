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
import android.widget.Toast;

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
import android.widget.Button;
import de.nitri.gauge.Gauge;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTemperatura.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTemperatura#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTemperatura extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String tokenAcceso = "Rg9Efs0vuz";
    private String tokenTemperatura = "E1yGxKAcrg";
    private String fecha;

    TextView promtemperatura;
    TextView actualtemperatura;
    int curValue;
    SpeedView speedView;
    Gauge gauge;

    private  int promedio;
    private int sumatemperatura;
    private int ultimopromedio=-100;
    private int ultimoactual=-100;
    private RequestQueue queue;
    Time today;
    String dia,mes,anio;

    public FragmentTemperatura() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTemperatura.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTemperatura newInstance(String param1, String param2) {
        FragmentTemperatura fragment = new FragmentTemperatura();
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

        View view=inflater.inflate(R.layout.fragment_fragment_temperatura, container, false);

        fecha = capturarFecha();

        promtemperatura = (TextView)view.findViewById(R.id.promediotemp) ;
        actualtemperatura = (TextView)view.findViewById(R.id.actualtemp) ;
        gauge= (Gauge)view.findViewById(R.id.gaugeTemperatura);
        queue = Volley.newRequestQueue(getContext());
       // speedView = (SpeedView) view.findViewById(R.id.gaugeTemperatura);
       // gauge();
      //  BottomNavigationView navTemperatura = view.findViewById(R.id.nav_temperatura);
        Button b1= view.findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ultimoDatoDelDia();
                obtenerPromedioDelDia();
            }
        });
        //ultimoDatoDelDia();
        ultimoDatoDelDia();
        obtenerPromedioDelDia();
        return  view;
    }

 /*   public void gauge(){
        speedView.setWithTremble(false);
        speedView.setMaxSpeed(60);
        speedView.setUnit(" °C");
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

        String url = "http://arrau.chillan.ubiobio.cl:8075/ubbiot/web/mediciones/medicionespordia/"+this.tokenAcceso+"/"+this.tokenTemperatura+"/"+this.fecha;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // speedView.speedTo(0);
                //crear JSON array
                try {

                    JSONArray mJsonArray = response.getJSONArray("data");
                    JSONObject mJsonObject = mJsonArray.getJSONObject(mJsonArray.length()-1);
                    String valor = mJsonObject.getString("valor");
                    actualtemperatura.setText("Temperatura actual: "+valor+"°C");
                    curValue= Integer.parseInt(valor);
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
                        if(ultimoactual>= -50){
                            actualtemperatura.setText("Temperatura actual: "+ultimoactual+"°C");
                           // speedView.speedTo(ultimoactual);
                            gauge.setValue(0);
                            gauge.moveToValue(ultimoactual);
                        }

                    }
                }

        );
        queue.add(request);
    }

    private void obtenerPromedioDelDia(){

        String url = "http://arrau.chillan.ubiobio.cl:8075/ubbiot/web/mediciones/medicionespordia/"+this.tokenAcceso+"/"+this.tokenTemperatura+"/"+this.fecha;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sumatemperatura=0;
                promedio=0;
                try {
                    JSONArray mJsonArray = response.getJSONArray("data");
                    for(int i=0; i < mJsonArray.length(); i++) {
                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                        String valor = mJsonObject.getString("valor");
                        int temperatura = Integer.parseInt(valor);
                        sumatemperatura+=temperatura;
                    }
                    promedio = sumatemperatura/mJsonArray.length();
                    ultimopromedio=promedio;
                    promtemperatura.setText("Promedio del día: "+promedio+"°C");
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(ultimopromedio>=-50){
                            promtemperatura.setText("Promedio del día: "+ultimopromedio+"°C");

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
