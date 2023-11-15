package com.controle.bedrane.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controle.bedrane.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText nom, prenom;

    private Spinner serviceSpinner;
    //private DatePicker dateNaissance;
    private Button bnAdd;
    private String insertUrl = "http://192.168.138.191:8082/api/employees";
    private String servicesURL = "http://192.168.138.191:8082/api/services";

    private List<String> serviceList = new ArrayList();






    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        bnAdd = view.findViewById(R.id.bnAdd);
        serviceSpinner = view.findViewById(R.id.service);
        fetchServices();

        //dateNaissance = view.findViewById(R.id.dateNaissance);


        bnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomText = nom.getText().toString();
                String prenomText = prenom.getText().toString();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("nom", nomText);
                    jsonBody.put("prenom", prenomText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, insertUrl, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setMessage("Ajout avec succès")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                nom.setText("");
                                                prenom.setText("");
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

                requestQueue.add(request);
            }
        });

        return view;
    }

    private void fetchServices() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        Log.e("test","test");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, servicesURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response", response.toString());
                        serviceList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject serviceObject = response.getJSONObject(i);
                                String serviceNom = serviceObject.getString("nom");
                                serviceList.add(serviceNom);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        updateServiceSpinner();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EtudiantFragment", "Error fetching filiere names: " + error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    private void updateServiceSpinner() {
        ArrayAdapter<String> filiereSpinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, serviceList);
        filiereSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(filiereSpinnerAdapter);
    }

}
