package edu.upc.eetac.dsa.raul.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.upc.eetac.dsa.raul.android.libros.api.Libro;

public class LibroAdapter extends BaseAdapter {

	private ArrayList<Libro> data;
	private LayoutInflater inflater;
	
	public LibroAdapter(Context context, ArrayList<Libro> data) {
		super();
		inflater = LayoutInflater.from(context);
		this.data = data;
	}
	
	private static class ViewHolder {	//elementos del View del elemento	
		TextView tvTitulo;
		TextView tvEditorial;
		TextView tvFechaImpresion;
		TextView tvAutor;
	}
	
		 //Metodos explicados en el PDF
	@Override
	public int getCount() {
		return data.size();
	}
	 
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
	 
	@Override
	public long getItemId(int position) {
		return ((Libro)getItem(position)).getLibroid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) { //cuando no hay nada que reciclar
			convertView = inflater.inflate(R.layout.list_row_libro, null); //inflamos
			viewHolder = new ViewHolder();
			viewHolder.tvTitulo = (TextView) convertView.findViewById(R.id.tvTitulo);
			viewHolder.tvEditorial = (TextView) convertView.findViewById(R.id.tvEditorial);
			viewHolder.tvFechaImpresion = (TextView) convertView.findViewById(R.id.tvFechaImpresion);
			viewHolder.tvAutor = (TextView) convertView.findViewById(R.id.tvAutor);
			convertView.setTag(viewHolder);	//le pone el tag a nivel de vista
		} else {
			viewHolder = (ViewHolder) convertView.getTag();	//recupera de la vista qe toca
		}
		String titulo = data.get(position).getTitulo();	//le coloca los datos que se visualizan
		String editorial = data.get(position).getEditorial();
		String date = SimpleDateFormat.getInstance().format(data.get(position).getFecha_impresion());
		String autor = data.get(position).getAutor();
		viewHolder.tvTitulo.setText(titulo); //representar los datos
		viewHolder.tvEditorial.setText(editorial);
		viewHolder.tvFechaImpresion.setText(date);
		viewHolder.tvAutor.setText("Autor: "+autor);
		return convertView;
	}
	
}
