package edu.upc.eetac.dsa.raul.android.libros.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LibrosAPI {
	private final static String TAG = LibrosAPI.class.toString();
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LibroCollection getLibros(URL url) {	//URL direccion del recurso --> http://s:p/beeter-api/sings?o=x&&l=Y
		LibroCollection libros = new LibroCollection();

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();	//conexion http movil y el server REST

			urlConnection.setRequestProperty("Accept", MediaType.LIBROS_API_LIBRO_COLLECTION);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true); //Puedes leer de la InputString de la conexion que se establezca entre los dos.
			urlConnection.connect(); //se hace la conexion

			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); //lee la respuesta del servidor
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			JSONObject jsonObject = new JSONObject(sb.toString()); //org.json
			//JSONArray jsonLinks = jsonObject.getJSONArray("links"); //org.json
			//parseLinks(jsonLinks, stings.getLinks()); //pone los elementos del array links en el arraylist links definido en sting collection

			JSONArray jsonLibros = jsonObject.getJSONArray("libros");
			for (int i = 0; i < jsonLibros.length(); i++) {
				JSONObject jsonLibro = jsonLibros.getJSONObject(i);
				Libro libro = parseLibro(jsonLibro); //convertir cada elemento del array en un objeto sting
				libros.add(libro);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}

		return libros;
	}
//	
//	public Sting getSting(URL url) {
//		Sting sting = new Sting();
//	 
//		HttpURLConnection urlConnection = null;
//		try {
//			urlConnection = (HttpURLConnection) url.openConnection();
//			urlConnection.setRequestProperty("Accept",
//					MediaType.BEETER_API_STING);
//			urlConnection.setRequestMethod("GET");
//			urlConnection.setDoInput(true);
//			urlConnection.connect();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					urlConnection.getInputStream()));
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//	 
//			JSONObject jsonSting = new JSONObject(sb.toString());
//			sting = parseSting(jsonSting);
//		} catch (IOException e) {
//			Log.e(TAG, e.getMessage(), e);
//			return null;
//		} catch (JSONException e) {
//			Log.e(TAG, e.getMessage(), e);
//			return null;
//		} catch (ParseException e) {
//			Log.e(TAG, e.getMessage(), e);
//			return null;
//		}finally {
//			if (urlConnection != null)
//				urlConnection.disconnect();
//		}
//	 
//		return sting;
//	}

	private void parseLinks(JSONArray source, List<Link> links) throws JSONException {
		for (int i = 0; i < source.length(); i++) {
			JSONObject jsonLink = source.getJSONObject(i);
			Link link = new Link();
			link.setRel(jsonLink.getString("rel"));
			link.setTitle(jsonLink.getString("title"));
			link.setType(jsonLink.getString("type"));
			link.setUri(jsonLink.getString("uri"));
			links.add(link);
		}
	}

	private Libro parseLibro(JSONObject source) throws JSONException, ParseException {
		Libro libro = new Libro();
		libro.setAutor(source.getString("autor"));
		libro.setEdicion(source.getString("edicion"));
		libro.setEditorial(source.getString("editorial"));
		libro.setLengua(source.getString("lengua"));
		libro.setLibroid(source.getInt("libroid"));
		libro.setTitulo(source.getString("titulo"));
		String fecha_edicion = source.getString("fecha_edicion").replace("T", " ");
		libro.setFecha_edicion(sdf.parse(fecha_edicion));
		String fecha_impresion = source.getString("fecha_impresion").replace("T", " ");
		libro.setFecha_impresion(sdf.parse(fecha_impresion));
		String lastUpdate = source.getString("lastUpdate").replace("T", " ");
		libro.setLastUpdate(sdf.parse(lastUpdate));
		
		JSONArray jsonLibroLinks = source.getJSONArray("links");
		parseLinks(jsonLibroLinks, libro.getLinks());
		return libro;
	}
//	
//	public Sting createSting(URL url, String subject, String content) {
//		Sting sting = new Sting();
//		sting.setSubject(subject);
//		sting.setContent(content);
//		
//		HttpURLConnection urlConnection = null;
//		try {
//			JSONObject jsonSting = createJsonSting(sting);
//			urlConnection = (HttpURLConnection) url.openConnection();
//			urlConnection.setRequestProperty("Accept",
//					MediaType.BEETER_API_STING);
//			urlConnection.setRequestProperty("Content-Type",
//					MediaType.BEETER_API_STING);
//			urlConnection.setRequestMethod("POST");
//			urlConnection.setDoInput(true);
//			urlConnection.setDoOutput(true);
//			urlConnection.connect();
//		
//			PrintWriter writer = new PrintWriter(
//					urlConnection.getOutputStream());
//			writer.println(jsonSting.toString());
//			writer.close();
//			
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					urlConnection.getInputStream()));
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//		
//			jsonSting = new JSONObject(sb.toString());
//			sting = parseSting(jsonSting);
//		} catch (JSONException e) {
//			Log.e(TAG, e.getMessage(), e);
//			return null;
//		} catch (IOException e) {
//			Log.e(TAG, e.getMessage(), e);
//			return null;
//		} catch (ParseException e) {
//			Log.e(TAG, e.getMessage(), e);
//			return null;
//		} finally {
//			if (urlConnection != null)
//				urlConnection.disconnect();
//		}
//		
//		return sting;
//	}
//	 
//	private JSONObject createJsonSting(Sting sting) throws JSONException {
//		JSONObject jsonSting = new JSONObject();
//		jsonSting.put("subject", sting.getSubject());
//		jsonSting.put("content", sting.getContent());
//	 
//		return jsonSting;
//	}
}
