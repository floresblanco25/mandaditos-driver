package com.mandaditos.driver;

import android.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.mandaditos.driver.mUtilities.*;
import com.mandaditos.driver.models.*;
import java.util.*;
import android.support.design.internal.*;

public class Home extends Activity 
{
	private DatabaseReference mDataBase;
	private RequestPermissionHandler mRequestPermissionHandler;
	private String uId,name;
	private FirebaseAuth mFirebaseAuth;
	private Button logout;
	private TextView BienvenidoDriverTv,cuantasPorCategoria;
	private FireDataDb fireData;
	private ProgressDialog pDialog;
	private RecyclerView mRecyclerView;
	private int rowPos;

	private View bottomSheet;

	private BottomSheetBehavior<View> behavior;

	private TextView sheetTitle;

	private BottomNavigationView navigation;

	private View notificationBadge;
	LinearLayout netTv;
	
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
		mRecyclerView = findViewById(R.id.home_recycler);
		netTv = findViewById(R.id.internetLayout);
		fireData = new FireDataDb();
		
		
		
		
		
		
		
//red
		DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
		connectedRef.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					boolean connected = snapshot.getValue(Boolean.class);
					if (connected) {
						netTv.setVisibility(View.GONE);
					} else {
						netTv.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onCancelled(DatabaseError error) {
				}
			});
		netTv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					misOrdenesPendientes();
					MenuItem firstItem = navigation.getMenu().findItem(R.id.itemSinEntregar);
					firstItem.getIcon().setTint(Color.BLACK);
					firstItem.setChecked(true);
				}
			});
		
		
		
		
		
		
//Navigatiom
		navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		BottomNavigationViewHelper.removeShiftMode(navigation);
		//addBadgeView();
		
		
		
		
		
		
		
//HIDE KEYBOARD
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);








//logiut
		logout.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					FirebaseAuth.getInstance().signOut();
					finishAffinity();
					startActivity(new Intent(Home.this, LoginActivity.class));
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
				public void onDataChange(DataSnapshot dataSnapshot)
				{
					if (dataSnapshot.exists())
					{
						String t = dataSnapshot.getValue(String.class);
						setNombreUsuario(t);
					}


				}
				private void setNombreUsuario(String t)
				{
					String usuario = t;
					try
					{
						int index = usuario.indexOf(' ');
						usuario = usuario.substring(0, index);
						
						
					}
					catch (Exception e)
					{Log.e("Couldent split name", e.toString());}
					BienvenidoDriverTv.setText("Bienvenido " + usuario + "");
					name=usuario;
					sheetTitle.setText("Hola, "+name);
				}
				@Override
				public void onCancelled(DatabaseError databaseError)
				{
				}
			});
			
			
			
			
			
			
			
			
//Bottom sheet
		CoordinatorLayout coordinatorLayout= findViewById(R.id.main_content);
		bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
		behavior = BottomSheetBehavior.from(bottomSheet);
		behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
				@Override
				public void onStateChanged(@NonNull View bottomSheet, int newState) {

				}
				@Override
				public void onSlide(@NonNull View bottomSheet, float slideOffset) {
					// React to dragging events

				}
			});
		behavior.setPeekHeight(0);
		TextView liquidacion = coordinatorLayout.findViewById(R.id.bottomsheetLiquidacion);
		liquidacion.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent i = new Intent(Home.this,DriverDashboard.class);
					Bundle b = new Bundle();
					b.putString("driverName", name);
					b.putString("uid", uId);
					b.putString("todo","showLiquidacion");
					i.putExtras(b);
					startActivity(i);
				}
			});
		TextView ganancias = coordinatorLayout.findViewById(R.id.bottomsheetGanancias);
		ganancias.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent i = new Intent(Home.this,DriverDashboard.class);
					Bundle b = new Bundle();
					b.putString("driverName", name);
					b.putString("uid", uId);
					b.putString("todo","showPagoDriver");
					i.putExtras(b);
					startActivity(i);
				}
			});
		TextView help = coordinatorLayout.findViewById(R.id.bottomsheetHelp);
		help.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.setData(Uri.parse("https://chat.whatsapp.com/HC3IZwd123A0U1HYma9FK5"+"?text="));
					startActivity(intent);
				}
			});
			sheetTitle = coordinatorLayout.findViewById(R.id.bottomsheetTitle);
			

		







//Cargamos los datos
		misOrdenesPendientes();
		MenuItem firstItem = navigation.getMenu().findItem(R.id.itemSinEntregar);
		firstItem.getIcon().setTint(Color.BLACK);
		firstItem.setChecked(true);

	}







//Completadas button
	public void misOrdenesCompletadas()
	{
		pDialog = new ProgressDialog(Home.this);
		pDialog.setMessage("Cargando datos de los servidores..");
		pDialog.setCancelable(false);
		pDialog.show();
		final List<OrderModel> filteredOrdersListFinal = new ArrayList<OrderModel>();
		DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Ordenes");
		mRef.addValueEventListener(new ValueEventListener(){
				private List<OrderModel> ordersList;
				@Override
				public void onDataChange(DataSnapshot rerSnapshot)
				{
					pDialog.dismiss();
					ordersList = fireData.getFireDataList(rerSnapshot);
					filteredOrdersListFinal.clear();
					for (OrderModel order : ordersList)
					{
						if (order.getDriverAsignado().equalsIgnoreCase(uId))
						{
							if (order.getEstadoDeOrden().equalsIgnoreCase("Completada"))
							{
								filteredOrdersListFinal.add(order);
							}
						} 

					}
					OrdersAdapter adapter = new OrdersAdapter(Home.this, filteredOrdersListFinal);
					mRecyclerView.setVisibility(View.VISIBLE);
					mRecyclerView.setHasFixedSize(true);
					final LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
					layoutManager.setReverseLayout(true);
					layoutManager.setStackFromEnd(true);
					mRecyclerView.setLayoutManager(layoutManager);
					mRecyclerView.setAdapter(adapter);
					mRecyclerView.scrollToPosition(rowPos);
					//SAVE SCROLL POSITION
					mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

							@Override
							public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
								rowPos = layoutManager.findFirstCompletelyVisibleItemPosition();
							}});
					
					int count = 0;
					if (adapter != null)
					{
						count = adapter.getItemCount();
					}
					cuantasPorCategoria.setText(adapter.getItemCount() + "");
				}


				@Override
				public void onCancelled(DatabaseError p1)
				{
				}
			});
	}










//Mostrar sin completar
	public void misOrdenesPendientes()
	{
		pDialog = new ProgressDialog(Home.this);
		pDialog.setMessage("Cargando datos de los servidores..");
		pDialog.setCancelable(false);
		pDialog.show();
		final List<OrderModel> filteredOrdersListFinal = new ArrayList<OrderModel>();
		DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Ordenes");
		mRef.addValueEventListener(new ValueEventListener(){
				private List<OrderModel> ordersList;
				@Override
				public void onDataChange(DataSnapshot rerSnapshot)
				{
					pDialog.dismiss();
					ordersList = fireData.getFireDataList(rerSnapshot);
					filteredOrdersListFinal.clear();
					for (OrderModel order : ordersList)
					{
						if (order.getDriverAsignado().equalsIgnoreCase(uId))
						{
							if (order.getEstadoDeOrden().equalsIgnoreCase("Sin Completar"))
							{
								filteredOrdersListFinal.add(order);
							}
						} 

					}
					OrdersAdapter adapter = new OrdersAdapter(Home.this, filteredOrdersListFinal);
					mRecyclerView.setVisibility(View.VISIBLE);
					mRecyclerView.setHasFixedSize(true);
					final LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
					layoutManager.setReverseLayout(true);
					layoutManager.setStackFromEnd(true);
					mRecyclerView.setLayoutManager(layoutManager);
					mRecyclerView.setAdapter(adapter);
					mRecyclerView.scrollToPosition(rowPos);
					//SAVE SCROLL POSITION
					mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

							@Override
							public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
								rowPos = layoutManager.findFirstCompletelyVisibleItemPosition();
							}});
					
					int count = 0;
					if (adapter != null)
					{
						count = adapter.getItemCount();
					}
					cuantasPorCategoria.setText(adapter.getItemCount() + "");
				}


				@Override
				public void onCancelled(DatabaseError p1)
				{
				}
			});
	}



















//Ordenes sin asignar
	public void conseguirOrdenes()
	{
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
					pDialog.setCancelable(false);
					pDialog.show();
					final List<OrderModel> filteredOrdersListFinal = new ArrayList<OrderModel>();
					DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Ordenes");
					mRef.addValueEventListener(new ValueEventListener(){
							private List<OrderModel> ordersList;
							@Override
							public void onDataChange(DataSnapshot rerSnapshot)
							{
								pDialog.dismiss();
								ordersList = fireData.getFireDataList(rerSnapshot);
								filteredOrdersListFinal.clear();
								for (OrderModel order : ordersList)
								{
									if (order.getDriverAsignado().equalsIgnoreCase("Sin Asignar"))
									{
										filteredOrdersListFinal.add(order);
									}

								}
								mAdapterPool adapter = new mAdapterPool(Home.this, filteredOrdersListFinal);
								mRecyclerView.setVisibility(View.VISIBLE);
								mRecyclerView.setHasFixedSize(true);
								final LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
								mRecyclerView.setLayoutManager(layoutManager);
								mRecyclerView.stopScroll();
								mRecyclerView.setAdapter(adapter);
								mRecyclerView.scrollToPosition(rowPos);
								//SAVE SCROLL POSITION
								mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

										@Override
										public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
											rowPos = layoutManager.findFirstCompletelyVisibleItemPosition();
											}});
											

								
								int count = 0;
								if (adapter != null)
								{
									count = adapter.getItemCount();
								}
								cuantasPorCategoria.setText(adapter.getItemCount() + "");
							}


							@Override
							public void onCancelled(DatabaseError p1)
							{
							}
						});
				}
			});
		dialog.show();

	}












//BACK PRESSED
	@Override
	public void onBackPressed()
	{
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Salir");
		dialog.setMessage("¿Seguro que quieres salir?");
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					dialog.dismiss();
				}
			});
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Salir", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					finishAffinity();
				}
			});
		dialog.show();
    } 
	
	
	
	
	
	




//Permissions
	private void mCheckPermission()
	{
		mRequestPermissionHandler.requestPermission(this, new String[] {
				Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
			}, 123, new RequestPermissionHandler.RequestPermissionListener() {
				@Override
				public void onSuccess()
				{
				}

				@Override
				public void onFailed()
				{
				}
			});

	}
	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults)
	{
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions,
															 grantResults);
    }
	
	
	
	
	
	
//bottom nav view
	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
	= new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
		{
			switch (item.getItemId())
			{
				case R.id.itemMas:{
						if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
							behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
							}else{
								behavior.setState(BottomSheetBehavior.STATE_COLLAPSED );
							}
							
						
				}
				return true;
                case R.id.itemConseguir:{
					conseguirOrdenes();
						if (behavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
							behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
						}
					}
                    return true;

				case R.id.itemEntregadas:{
						misOrdenesCompletadas();
						if (behavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
							behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
						}
					}
					return true;

				case R.id.itemSinEntregar:
					{
						misOrdenesPendientes();
						if (behavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
							behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
							//refreshBadgeView();
						}
					}
					return true;
					


            }
            return false;
        }

    };
	
	
	
	
	
	
	
//badge
	private void addBadgeView() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(0);

        notificationBadge = LayoutInflater.from(this).inflate(R.layout.count_layout, menuView, false);

        itemView.addView(notificationBadge);
    }
	
    private void refreshBadgeView() {
        boolean badgeIsVisible = notificationBadge.getVisibility() != View.VISIBLE;
        notificationBadge.setVisibility(badgeIsVisible ? View.VISIBLE : View.GONE);
    }


	
	



}
