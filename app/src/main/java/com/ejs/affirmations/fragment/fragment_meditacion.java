package com.ejs.affirmations.fragment;

import static com.ejs.affirmations.utils.handler.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.ejs.affirmations.R;
import com.ejs.affirmations.adapter.adapter_meditacion;
import com.ejs.affirmations.model.categoriaMeditacionClass;
import com.ejs.affirmations.utils.ProgressAlert;
import com.ejs.affirmations.utils.volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class fragment_meditacion extends Fragment {
    //private final String TAG = this.getClass().getSimpleName();
    private final AppCompatActivity appCompatActivity;

    public fragment_meditacion(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_meditacion, container, false);

        String URLstring = getString(R.string.url_db) + getString(R.string.php_script_query_select);
        String table_categoria_meditacion = getString(R.string.pref_categoria_meditacion);
        String sql_Select = getString(R.string.pref_sql_cat_med_sel);

        ProgressAlert progressAlert = ProgressAlert.getInstance(appCompatActivity, true);
        progressAlert.setMessage(appCompatActivity.getString(R.string.lbl_dialog_wait));
        progressAlert.show();

        RecyclerView rv_meditacion = view.findViewById(R.id.rv_meditacion);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(appCompatActivity, 2);
        rv_meditacion.setLayoutManager(gridLayoutManager);

        Response.Listener<String> res = response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.optString("success").equals("1")){
                    JSONArray jaTable = obj.getJSONArray(table_categoria_meditacion);
                    JSONArray jaSP = jaTable.getJSONArray(0);

                    ArrayList<categoriaMeditacionClass> itemListCatMed = new ArrayList<>();
                    categoriaMeditacionClass itemcons;
                    for (int i=0; i<jaSP.length(); i++) {
                        itemcons = new categoriaMeditacionClass();
                        String chb_name = ("cat_med_" + i);
                        @SuppressLint("DiscouragedApi") int identifier = appCompatActivity.getResources().getIdentifier(chb_name, "string", appCompatActivity.getPackageName());
                        String nameResource = appCompatActivity.getString(identifier);

                        JSONObject joValue = jaSP.getJSONObject(i);
                        itemcons.setid(joValue.getInt("id"));
                        itemcons.setstring(nameResource);
                        itemListCatMed.add(itemcons);
                    }
                    final adapter_meditacion adaptador = new adapter_meditacion(appCompatActivity, itemListCatMed, progressAlert);
                    rv_meditacion.setAdapter(adaptador);
                }
                if(obj.optString("success").equals("2")){
                    progressAlert.dismiss();
                }
                if(obj.optString("success").equals("0")){
                    progressAlert.dismiss();
                }

            } catch (JSONException e) {
                progressAlert.dismiss();
                json(appCompatActivity, e);
            }

        };
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sql", sql_Select);
        hashMap.put("table",table_categoria_meditacion);
        volley.run(appCompatActivity, URLstring, res, volley.error(appCompatActivity, progressAlert), hashMap);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
