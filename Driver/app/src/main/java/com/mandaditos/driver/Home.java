package com.mandaditos.driver;

import android.*;
import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.mandaditos.driver.mUtilities.*;
import com.mandaditos.driver.models.*;
import java.util.*;

import android.Manifest;
import java.text.*;

public class Home extends Activity 
{
	private DatabaseReference mDataBase;
	private RequestPermissionHandler mRequestPermissionHandler;
	private String uId;
	private FirebaseAuth mFirebaseAuth;
	private Button logout;
	private TextView BienvenidoDriverTv,cuantasPorCategoria,totalAliquidar,pagoDriverTv;

	private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mDataBase = FirebaseDatabase.getInstance().getReference("Ordenes");
		mFirebaseAuth = FirebaseAuth.getInstance();
		logout = findViewById(R.id.CerrarSesionmainButton1);
		BienvenidoDriverTv = findViewById(R.id.BienvenidomainTextView);
		cuantasPorCategoria = findViewById(R.id.cuantasOrdenesPorDriverSinEntregarmainTextView1);
		totalAliquidar = findViewById(R.id.totalALiquidarmainTextView1);
		pagoDriverTv = findViewById(R.id.pagoDrivermainTextView1);
		
		
		
		
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);
		
		
		
		
		logout.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					FirebaseAuth.getInstance().signOut();
					finishAffinity();
					startActivity(new Intent(Home.this,LoginActivity.class));
				}
			});
		
		
//aqui obtenemos el uid
		mFirebaseAuth = FirebaseAuth.getInstance();
		FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
		uId = mFirebaseUser.getUid().toString();
		
		
//aqui obtenemos el nombre del usuario
		mFirebaseAuth = FirebaseAuth.getInstance();
		String userId = mFirebaseUser.getUid().toString();
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Drivers/" + userId + "/Perfil").child("nombre");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String t = dataSnapshot.getValue(String.class);
					setNombreUsuario(t);

				}
				private void setNombreUsuario(String t)
				{

					String usuario = t;
					try{
						int index = usuario.indexOf(' ');
						usuario = usuario.substring(0, index);
					}catch(Exception e){Log.e("Couldent split name",e.toString());}


					BienvenidoDriverTv.setText("Bienvenido "+usuario+"");

				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});

		
			
		pDialog = new ProgressDialog(Home.this);
		pDialog.setMessage("Cargando datos de los servidores..");
		pDialog.show();
		mDataBase.addValueEventListener(new ValueEventListener(){


			
			
//Cargamos los datos
				@Override
				public void onDataChange(DataSnapshot p1)
				{
					List<CostoPorOrdenModel> costosEnvio = new ArrayList<CostoPorOrdenModel>();
					pDialog.dismiss();
//					try {
//						Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//						Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//						r.play();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					if(p1.exists()){
						List<CostoPorOrdenModel> items = new ArrayList<CostoPorOrdenModel>();
						List<mandaditosModel> ordersList = new ArrayList<mandaditosModel>();
						for (DataSnapshot postSnapshot : p1.getChildren()) {
							double latA = postSnapshot.child("latLngA/latitude").getValue();
							double lngA = postSnapshot.child("latLngA/longitude").getValue();
							double latB = postSnapshot.child("latLngB/latitude").getValue();
							double lngB = postSnapshot.child("latLngB/longitude").getValue();

							mandaditosModel m = new mandaditosModel();
							m.setUserId(postSnapshot.child("userId").getValue().toString());
							m.setUsuario(postSnapshot.child("usuario").getValue().toString());
							m.setPartida(postSnapshot.child("partida").getValue().toString());
							m.setDestino(postSnapshot.child("destino").getValue().toString());
							m.setDistancia(postSnapshot.child("distancia").getValue().toString());
							m.setFecha(postSnapshot.child("fecha").getValue().toString());
							m.setETA(postSnapshot.child("eta").getValue().toString());
							m.setRecogerDineroEn(postSnapshot.child("recogerDineroEn").getValue().toString());
							m.setCostoDelProducto(postSnapshot.child("costoDelProducto").getValue().toString());
							m.setCostoDelEnvio(postSnapshot.child("costoDelEnvio").getValue().toString());
							m.setCostoOrden(postSnapshot.child("costoOrden").getValue().toString());
							m.setEmpresa(postSnapshot.child("empresa").getValue().toString());
							m.setDireccionEmpresa(postSnapshot.child("direccionEmpresa").getValue().toString());
							m.setInstruccionesDeLlegada(postSnapshot.child("instruccionesDeLlegada").getValue().toString());
							m.setEstadoDeOrden(postSnapshot.child("estadoDeOrden").getValue().toString());
							m.setLatLngA(new LatLng(latA,lngA));
							m.setLatLngB(new LatLng(latB,lngB));
							m.setNumeroDeOrden(postSnapshot.getKey().toString());
							m.setDriverUid(postSnapshot.child("driverAsignado").getValue().toString());
							m.setTelefono(postSnapshot.child("telefono").getValue().toString());
							if(m.getEstadoDeOrden().toString().toLowerCase().matches("Sin completar".toLowerCase())){
								if(m.getDriverUid().toString().matches(uId)){
									ordersList.add(m);
									}
							}
							
//costo total
							if(m.getEstadoDeOrden().toString().toLowerCase().matches("Completada".toLowerCase())){
								if(m.getDriverUid().toString().matches(uId)){
									CostoPorOrdenModel precioModel = new CostoPorOrdenModel();
									float numbers = Float.valueOf(postSnapshot.child("costoOrden").getValue().toString());
									precioModel.setPrecioDeOrden(numbers);
									items.add(precioModel);
									//costo envio suma
									CostoPorOrdenModel precioModelEnvio = new CostoPorOrdenModel();
									float numbersEnvio = Float.valueOf(postSnapshot.child("costoDelEnvio").getValue().toString());
									if(Float.parseFloat(postSnapshot.child("costoDelEnvio").getValue().toString())>0.0f)
									{
										precioModelEnvio.setPrecioDeOrden(numbersEnvio-1);
										costosEnvio.add(precioModelEnvio);
									}
									
								}
							}
							
						}
						


						mAdapter adapter = new mAdapter(Home.this,ordersList);
						RecyclerView mRecyclerView = findViewById(R.id.home_recycler);
						mRecyclerView.setHasFixedSize(true);
						LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
						layoutManager.setReverseLayout(false);
						layoutManager.setStackFromEnd(false);
						mRecyclerView.setLayoutManager(layoutManager);
						mRecyclerView.setAdapter(adapter);
						totalAliquidar.setText(String.valueOf(grandTotal(items)));
						pagoDriverTv.setText(String.valueOf(grandTotal(costosEnvio)));
						cuantasPorCategoria.setText(adapter.getItemCount()+"");
					}
					
					else{}
				}

				private float grandTotal(List<CostoPorOrdenModel> items){

					float totalPrice = 0;
					for(int i = 0 ; i < items.size(); i++) {
						totalPrice += items.get(i).getPrecioDeOrden(); 
					}

					return totalPrice;
				}
				@Override
				public void onCancelled(DatabaseError p1){
				}
			});


	}
	
	public void mostrarCompletadas(View v) {
		pDialog = new ProgressDialog(Home.this);
		pDialog.setMessage("Cargando datos de los servidores..");
		pDialog.show();
		mDataBase.addValueEventListener(new ValueEventListener(){


				@Override
				public void onDataChange(DataSnapshot p1)
				{
					pDialog.dismiss();
					if(p1.exists()){

						List<mandaditosModel> ordersList = new ArrayList<mandaditosModel>();
						for (DataSnapshot postSnapshot : p1.getChildren()) {
							double latA = postSnapshot.child("latLngA/latitude").getValue();
							double lngA = postSnapshot.child("latLngA/longitude").getValue();
							double latB = postSnapshot.child("latLngB/latitude").getValue();
							double lngB = postSnapshot.child("latLngB/longitude").getValue();

							mandaditosModel m = new mandaditosModel();
							m.setUserId(postSnapshot.child("userId").getValue().toString());
							m.setUsuario(postSnapshot.child("usuario").getValue().toString());
							m.setPartida(postSnapshot.child("partida").getValue().toString());
							m.setDestino(postSnapshot.child("destino").getValue().toString());
							m.setDistancia(postSnapshot.child("distancia").getValue().toString());
							m.setFecha(postSnapshot.child("fecha").getValue().toString());
							m.setETA(postSnapshot.child("eta").getValue().toString());
							m.setRecogerDineroEn(postSnapshot.child("recogerDineroEn").getValue().toString());
							m.setCostoDelProducto(postSnapshot.child("costoDelProducto").getValue().toString());
							m.setCostoDelEnvio(postSnapshot.child("costoDelEnvio").getValue().toString());
							m.setCostoOrden(postSnapshot.child("costoOrden").getValue().toString());
							m.setEmpresa(postSnapshot.child("empresa").getValue().toString());
							m.setDireccionEmpresa(postSnapshot.child("direccionEmpresa").getValue().toString());
							m.setInstruccionesDeLlegada(postSnapshot.child("instruccionesDeLlegada").getValue().toString());
							m.setEstadoDeOrden(postSnapshot.child("estadoDeOrden").getValue().toString());
							m.setLatLngA(new LatLng(latA,lngA));
							m.setLatLngB(new LatLng(latB,lngB));
							m.setNumeroDeOrden(postSnapshot.getKey().toString());
							m.setDriverUid(postSnapshot.child("driverAsignado").getValue().toString());
							m.setTelefono(postSnapshot.child("telefono").getValue().toString());
							if(m.getEstadoDeOrden().toString().toLowerCase().matches("Completada".toLowerCase())){
								if(m.getDriverUid().toString().matches(uId)){
									ordersList.add(m);
								}
							}
						}


						mAdapter adapter = new mAdapter(Home.this,ordersList);
						RecyclerView mRecyclerView = findViewById(R.id.home_recycler);
						mRecyclerView.setHasFixedSize(true);
						LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
						layoutManager.setReverseLayout(false);
						layoutManager.setStackFromEnd(false);
						mRecyclerView.setLayoutManager(layoutManager);
						mRecyclerView.setAdapter(adapter);
						cuantasPorCategoria.setText(adapter.getItemCount()+"");
					}
					else{}
				}
				@Override
				public void onCancelled(DatabaseError p1){
				}
			});
		// does something very interesting
	}
	
	
	
	
	
	
	
	
	
	
//Mostrar sin comoletar
	public void mostrarSinCompletar(View v) {
				//dialog 
		pDialog = new ProgressDialog(Home.this);
		pDialog.setMessage("Cargando datos de los servidores..");
		pDialog.show();
		mDataBase.addValueEventListener(new ValueEventListener(){


				@Override
				public void onDataChange(DataSnapshot p1)
				{
					pDialog.dismiss();
					if(p1.exists()){

						List<mandaditosModel> ordersList = new ArrayList<mandaditosModel>();
						for (DataSnapshot postSnapshot : p1.getChildren()) {
							double latA = postSnapshot.child("latLngA/latitude").getValue();
							double lngA = postSnapshot.child("latLngA/longitude").getValue();
							double latB = postSnapshot.child("latLngB/latitude").getValue();
							double lngB = postSnapshot.child("latLngB/longitude").getValue();

							mandaditosModel m = new mandaditosModel();
							m.setUserId(postSnapshot.child("userId").getValue().toString());
							m.setUsuario(postSnapshot.child("usuario").getValue().toString());
							m.setPartida(postSnapshot.child("partida").getValue().toString());
							m.setDestino(postSnapshot.child("destino").getValue().toString());
							m.setDistancia(postSnapshot.child("distancia").getValue().toString());
							m.setFecha(postSnapshot.child("fecha").getValue().toString());
							m.setETA(postSnapshot.child("eta").getValue().toString());
							m.setRecogerDineroEn(postSnapshot.child("recogerDineroEn").getValue().toString());
							m.setCostoDelProducto(postSnapshot.child("costoDelProducto").getValue().toString());
							m.setCostoDelEnvio(postSnapshot.child("costoDelEnvio").getValue().toString());
							m.setCostoOrden(postSnapshot.child("costoOrden").getValue().toString());
							m.setEmpresa(postSnapshot.child("empresa").getValue().toString());
							m.setDireccionEmpresa(postSnapshot.child("direccionEmpresa").getValue().toString());
							m.setInstruccionesDeLlegada(postSnapshot.child("instruccionesDeLlegada").getValue().toString());
							m.setEstadoDeOrden(postSnapshot.child("estadoDeOrden").getValue().toString());
							m.setLatLngA(new LatLng(latA,lngA));
							m.setLatLngB(new LatLng(latB,lngB));
							m.setNumeroDeOrden(postSnapshot.getKey().toString());
							m.setDriverUid(postSnapshot.child("driverAsignado").getValue().toString());
							m.setTelefono(postSnapshot.child("telefono").getValue().toString());
							if(m.getEstadoDeOrden().toString().toLowerCase().matches("Sin completar".toLowerCase())){
								if(m.getDriverUid().toString().matches(uId)){
									ordersList.add(m);
								}
							}
						}


						mAdapter adapter = new mAdapter(Home.this,ordersList);
						RecyclerView mRecyclerView = findViewById(R.id.home_recycler);
						mRecyclerView.setHasFixedSize(true);
						LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
						layoutManager.setReverseLayout(false);
						layoutManager.setStackFromEnd(false);
						mRecyclerView.setLayoutManager(layoutManager);
						mRecyclerView.setAdapter(adapter);
						cuantasPorCategoria.setText(adapter.getItemCount()+"");
					}
					else{}
				}
				@Override
				public void onCancelled(DatabaseError p1){
				}
			});
		// does something very interesting
	}
	
	
	
	
	
	
	
//	public void mostrarSinAsignar(View v)
//	{
//		//dialog 
//		pDialog = new ProgressDialog(Home.this);
//		pDialog.setMessage("Cargando datos de los servidores..");
//		pDialog.show();
//
//
//		mDataBase = FirebaseDatabase.getInstance().getReference("Ordenes");
//		mDataBase.addListenerForSingleValueEvent(new ValueEventListener(){
//
//
//				@Override
//				public void onDataChange(DataSnapshot p1)
//				{
//					pDialog.dismiss();
//					if (p1.exists())
//					{
//
//						List<mandaditosModel> ordersList = new ArrayList<mandaditosModel>();
//						for (DataSnapshot postSnapshot : p1.getChildren())
//						{
//							double latA = postSnapshot.child("latLngA/latitude").getValue();
//							double lngA = postSnapshot.child("latLngA/longitude").getValue();
//							double latB = postSnapshot.child("latLngB/latitude").getValue();
//							double lngB = postSnapshot.child("latLngB/longitude").getValue();
//
//							mandaditosModel m = new mandaditosModel();
//							m.setUserId(postSnapshot.child("userId").getValue().toString());
//							m.setUsuario(postSnapshot.child("usuario").getValue().toString());
//							m.setPartida(postSnapshot.child("partida").getValue().toString());
//							m.setDestino(postSnapshot.child("destino").getValue().toString());
//							m.setDistancia(postSnapshot.child("distancia").getValue().toString());
//							m.setFecha(postSnapshot.child("fecha").getValue().toString());
//							m.setETA(postSnapshot.child("eta").getValue().toString());
//							m.setRecogerDineroEn(postSnapshot.child("recogerDineroEn").getValue().toString());
//							try
//							{
//								m.setCosto(postSnapshot.child("costo").getValue().toString());
//							}
//							catch (Exception e)
//							{}
//							m.setEstadoDeOrden(postSnapshot.child("estadoDeOrden").getValue().toString());
//							m.setLatLngA(new LatLng(latA, lngA));
//							m.setLatLngB(new LatLng(latB, lngB));
//							m.setNumeroDeOrden(postSnapshot.getKey().toString());
//							m.setDriverAsignado(postSnapshot.child("driverAsignado").getValue().toString());
//							if (m.getEstadoDeOrden().toString().toLowerCase().matches("Sin completar".toLowerCase()))
//							{
//								if (m.getDriverAsignado().toString().toLowerCase().matches("Sin asignar".toLowerCase()))
//								{
//									ordersList.add(m);
//								}
//
//							}
//						}
//
//
//						mAdapter adapter = new mAdapter(Home.this,ordersList);
//						RecyclerView mRecyclerView = findViewById(R.id.home_recycler);
//						mRecyclerView.setHasFixedSize(true);
//						LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
//						layoutManager.setReverseLayout(false);
//						layoutManager.setStackFromEnd(false);
//						mRecyclerView.setLayoutManager(layoutManager);
//						mRecyclerView.setAdapter(adapter);
//						cuantasPorCategoria.setText(adapter.getItemCount()+"");
//						}
//
//					else
//					{}
//				}
//				@Override
//				public void onCancelled(DatabaseError p1)
//				{
//				}
//			});
//	}
	
	
	
	
	
	
	
	
	
	
//Ordenes sin asignar
	public void ordenesSinAsignar(View v){
		final AlertDialog dialog = new AlertDialog.Builder(Home.this).create();
		dialog.setTitle("Alerta!");
		dialog.setMessage("Asegurate que el paquete quede en tu ruta");
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					dialog.dismiss();
				}
			});
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continuar", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					pDialog = new ProgressDialog(Home.this);
					pDialog.setMessage("Cargando datos de los servidores..");
					pDialog.show();
					mDataBase.addListenerForSingleValueEvent(new ValueEventListener(){


							@Override
							public void onDataChange(DataSnapshot p1)
							{
								pDialog.dismiss();
								if(p1.exists()){

									List<mandaditosModel> ordersList = new ArrayList<mandaditosModel>();
									for (DataSnapshot postSnapshot : p1.getChildren()) {
										double latA = postSnapshot.child("latLngA/latitude").getValue();
										double lngA = postSnapshot.child("latLngA/longitude").getValue();
										double latB = postSnapshot.child("latLngB/latitude").getValue();
										double lngB = postSnapshot.child("latLngB/longitude").getValue();

										mandaditosModel m = new mandaditosModel();
										m.setUserId(postSnapshot.child("userId").getValue().toString());
										m.setUsuario(postSnapshot.child("usuario").getValue().toString());
										m.setPartida(postSnapshot.child("partida").getValue().toString());
										m.setDestino(postSnapshot.child("destino").getValue().toString());
										m.setDistancia(postSnapshot.child("distancia").getValue().toString());
										m.setFecha(postSnapshot.child("fecha").getValue().toString());
										m.setETA(postSnapshot.child("eta").getValue().toString());
										m.setRecogerDineroEn(postSnapshot.child("recogerDineroEn").getValue().toString());
										m.setCostoDelProducto(postSnapshot.child("costoDelProducto").getValue().toString());
										m.setCostoDelEnvio(postSnapshot.child("costoDelEnvio").getValue().toString());
										m.setCostoOrden(postSnapshot.child("costoOrden").getValue().toString());
										m.setEmpresa(postSnapshot.child("empresa").getValue().toString());
										m.setDireccionEmpresa(postSnapshot.child("direccionEmpresa").getValue().toString());
										m.setInstruccionesDeLlegada(postSnapshot.child("instruccionesDeLlegada").getValue().toString());
										m.setEstadoDeOrden(postSnapshot.child("estadoDeOrden").getValue().toString());
										m.setLatLngA(new LatLng(latA,lngA));
										m.setLatLngB(new LatLng(latB,lngB));
										m.setNumeroDeOrden(postSnapshot.getKey().toString());
										m.setDriverUid(postSnapshot.child("driverAsignado").getValue().toString());
										m.setTelefono(postSnapshot.child("telefono").getValue().toString());
										if(m.getDriverUid().toString().toLowerCase().matches("Sin asignar".toLowerCase())){
											ordersList.add(m);
										}
									}


									mAdapterPool adapter = new mAdapterPool(Home.this,ordersList);
									RecyclerView mRecyclerView = findViewById(R.id.home_recycler);
									mRecyclerView.setHasFixedSize(true);
									LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
									layoutManager.setReverseLayout(false);
									layoutManager.setStackFromEnd(false);
									mRecyclerView.setLayoutManager(layoutManager);
									mRecyclerView.setAdapter(adapter);
									cuantasPorCategoria.setText(adapter.getItemCount()+"");
								}
								else{}
							}
							@Override
							public void onCancelled(DatabaseError p1){
							}
						});
				}
			});
		dialog.show();
		
	}

	
	
	
	
	
//Mostrar total
//	public void mostrarTotal(View v){
//		mDataBase.addValueEventListener(new ValueEventListener(){
//
//
//				@Override
//				public void onDataChange(DataSnapshot p1)
//				{
//					if(p1.exists()){
//
//						List<mandaditosModel> ordersList = new ArrayList<mandaditosModel>();
//						List<CostoPorOrdenModel> items = new ArrayList<CostoPorOrdenModel>();
//						for (DataSnapshot postSnapshot : p1.getChildren()) {
//							mandaditosModel m = new mandaditosModel();
//							m.setUserId(postSnapshot.child("userId").getValue().toString());
//							m.setUsuario(postSnapshot.child("usuario").getValue().toString());
//							m.setFecha(postSnapshot.child("fecha").getValue().toString());
//							m.setCosto(postSnapshot.child("costo").getValue().toString());
//							m.setEstadoDeOrden(postSnapshot.child("estadoDeOrden").getValue().toString());
//							m.setNumeroDeOrden(postSnapshot.getKey().toString());
//							m.setDriverUid(postSnapshot.child("driverAsignado").getValue().toString());
//							if(m.getEstadoDeOrden().toString().toLowerCase().matches("Completada".toLowerCase())){
//								if(m.getDriverUid().toString().matches(uId)){
//									CostoPorOrdenModel precioModel = new CostoPorOrdenModel();
//										float numbers = Float.valueOf(postSnapshot.child("costo").getValue().toString());
//										precioModel.setPrecioDeOrden(numbers);
//									items.add(precioModel);
//									ordersList.add(m);
//								}
//							}
//						}
//
//
//						mAdapter adapter = new mAdapter(Home.this,ordersList);
//						RecyclerView mRecyclerView = findViewById(R.id.home_recycler);
//						mRecyclerView.setHasFixedSize(true);
//						LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
//						layoutManager.setReverseLayout(false);
//						layoutManager.setStackFromEnd(false);
//						mRecyclerView.setLayoutManager(layoutManager);
//						mRecyclerView.setAdapter(adapter);
//						totalAliquidar.setText(String.valueOf(grandTotal(items)));
//					}
//					else{}
//				}
//				
//				private float grandTotal(List<CostoPorOrdenModel> items){
//
//					float totalPrice = 0;
//					for(int i = 0 ; i < items.size(); i++) {
//						totalPrice += items.get(i).getPrecioDeOrden(); 
//					}
//
//					return totalPrice;
//				}
//				@Override
//				public void onCancelled(DatabaseError p1){
//				}
//			});
//	}
	
	

	
	
	

	@Override
	public void onBackPressed() {
		finishAffinity();
        startActivity(new Intent(Home.this,Home.class));
    } 

	//Permissions
	private void mCheckPermission(){
		mRequestPermissionHandler.requestPermission(this, new String[] {
				Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
			}, 123, new RequestPermissionHandler.RequestPermissionListener() {
				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailed() {
				}
			});

	}

	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions,
															 grantResults);
    }
}
