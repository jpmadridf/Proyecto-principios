
package pds;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import static pds.Procesamiento.proximidades;



public class ProcesamientoTS {

    static ArrayList<Pair<String, Integer>> proximidades = new ArrayList<>();
    
    
    public static String leerPaginaWeb() {
        String code = "";
        try {
            StringBuffer codeBuffered = new StringBuffer();

            URL url = new URL("https://thingspeak.com/channels/870216/feed.json");
            InputStream in = url.openStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));

            String line;
            int i = 0;
            while ((line = read.readLine()) != null) {
                codeBuffered.append(line).append("\n");

            }

            code = codeBuffered.toString(); 

            in.close();
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }
    
    
    
    public static void recibirParametros() {
        try {
            JSONObject obj = new JSONObject(leerPaginaWeb());
            JSONArray arr = obj.getJSONArray("feeds");
            for (int i = 0; i < arr.length(); i++) {
                String fecha = arr.getJSONObject(i).getString("created_at");
                int proximidad = (int) arr.getJSONObject(i).getDouble("field2");
                //System.out.println("fecha "+post_id+" proximidad:"+ field1);
                agregarDatos(fecha, proximidad);
            }
        } catch (JSONException e) {
            System.out.println("No pude leer la página web.");
        }

    }

   
    
    public static void agregarDatos(String fecha, int valorProximidad) {
        proximidades.add(new Pair(fecha, valorProximidad));
    }

  
    
    public static int cantidadProximidadF() {
        int sumatoria = 0;
        for (int i = 0; i < proximidades.size(); i++) {
            if ((proximidades.get(i).getValue())==1){
                if (i<proximidades.size()-1){
                    if ((proximidades.get(i+1).getValue())==0){
                        sumatoria += 1;
                    }
                } else{
                    if((proximidades.get(i).getValue())==1){
                        sumatoria++;
                    }
                }
            }
        }
        return sumatoria;

    }


    
    public static void escribirArchivo(int cantidadProximidadesF) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            System.out.println("voy acá");
            fichero = new FileWriter("/Users/juanpablomadridflorez/Downloads/ProximidadPersistenciaJson.txt");             
            pw = new PrintWriter(fichero);
            pw.println("Numero de veces que excedió la proximidad: " + cantidadProximidadesF);
            for (int i = 0; i < proximidades.size(); i++) {
                pw.print("Fecha : " + proximidades.get(i).getKey() + "    ");
                pw.println("Proximidad : " + proximidades.get(i).getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        recibirParametros();
        
        int cantidadProximidadesF = cantidadProximidadF();
        
        escribirArchivo(cantidadProximidadesF);
    }
}

