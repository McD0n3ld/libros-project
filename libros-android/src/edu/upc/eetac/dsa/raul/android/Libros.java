package edu.upc.eetac.dsa.raul.android;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import edu.upc.eetac.dsa.raul.android.libros.api.Libro;
import edu.upc.eetac.dsa.raul.android.libros.api.LibroCollection;
import edu.upc.eetac.dsa.raul.android.libros.api.LibrosAPI;

public class Libros extends ListActivity {
	private final static String TAG = Libros.class.toString();
	private ArrayList<Libro> libroList;
	private LibroAdapter adapter;

	// implementacion del fetchstingstask
	private LibrosAPI api;

	String serverAddress = "";
	String serverPort = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AssetManager assetManager = getAssets();
		Properties config = new Properties();
		try {
			config.load(assetManager.open("config.properties"));
			serverAddress = config.getProperty("server.address");
			serverPort = config.getProperty("server.port");

			Log.d(TAG, "Configured server " + serverAddress + ":" + serverPort);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			finish();
		}

		setContentView(R.layout.libros_layout);

		libroList = new ArrayList<Libro>();
		adapter = new LibroAdapter(this, libroList);
		setListAdapter(adapter);

		final String username = "test";
		final String password = "test";

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		});
		Log.d(TAG, "authenticated with " + username + ":" + password);

		api = new LibrosAPI();
		URL url = null;
		try {
			url = new URL("http://" + serverAddress + ":" + serverPort + "/libros-api/libros");
		} catch (MalformedURLException e) {
			Log.d(TAG, e.getMessage(), e);
			finish();
		}
		(new FetchStingsTask()).execute(url);
	}

	// Progresso void (indeterminado), result stingCollection (lo que devuelve)
	private class FetchStingsTask extends AsyncTask<URL, Void, LibroCollection> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(Libros.this);
			pd.setTitle("ProgressDialog...");
			pd.setCancelable(false); // no es cancelable
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected LibroCollection doInBackground(URL... params) {
			LibroCollection libros = api.getLibros(params[0]);
			return libros;
		}

		@Override
		protected void onPostExecute(LibroCollection result) {
			// ArrayList<Sting> stings = new
			// ArrayList<Sting>(result.getStings());
			// for (Sting s : stings) {
			// Log.d(TAG, s.getStingId() + "-" + s.getSubject());
			// }
			addLibros(result);
			if (pd != null) {
				pd.dismiss();
			}
		}
	}

	private void addLibros(LibroCollection stings) {
		libroList.addAll(stings.getLibros());
		adapter.notifyDataSetChanged();
	}
}
