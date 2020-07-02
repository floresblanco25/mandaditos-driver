package com.mandaditos.driver;

import android.app.*;
import android.content.*;
import android.net.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.*;
import android.view.animation.*;
import android.widget.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.mandaditos.driver.models.*;
import java.util.*;

import android.view.View.OnClickListener;

public class OrdersAdapter extends RecyclerView.Adapter<mViewHolder>
{

//Initialize
    private Context mContext;
    private List<OrderModel> mDataList;

	
//Constructor
    OrdersAdapter(Context mContext, List< OrderModel > mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }
	
//Nothing

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row, parent, false);
        return new mViewHolder(mView);
    }

//Bind
    @Override
    public void onBindViewHolder(final mViewHolder holder, final int position) {
		final LatLng latLngA = mDataList.get(position).getLatLngA();
		final LatLng latLngB = mDataList.get(position).getLatLngB();
		holder.clienteDeDestinoEd.setText(mDataList.get(position).getClienteDeDestino());
		holder.DestinoEd.setText(mDataList.get(position).getDireccionDeDestino());
		holder.CostoDelProductoEd.setText(mDataList.get(position).getCostoDelProducto());
		holder.CostoTotalTv.setText(mDataList.get(position).getCostoOrden());
		holder.CostoDelEnvioEd.setText(mDataList.get(position).getCostoDelEnvio());
		holder.EmpresaEd.setText(mDataList.get(position).getEmpresaDePartida());
		holder.direccionEmpresaEd.setText(mDataList.get(position).getDireccionEmpresaDePartida());
		holder.InstruccionesEd.setText(mDataList.get(position).getInstrucciones());
		holder.EstadoDeOrdenEd.setText(mDataList.get(position).getEstadoDeOrden());
		holder.NumeroDeOrdenEd.setText(mDataList.get(position).getNumeroDeOrden());
		holder.callTv.setText(mDataList.get(position).getTelefonoDeClienteDeDestino());
		holder.tiendaTelefono.setText(mDataList.get(position).getTelefonoTienda());
		holder.NumeroDeOrdenEd.setEnabled(false);
		holder.DestinoEd.setEnabled(false);
		holder.DistanciaEd.setEnabled(false);
		holder.FechaEtaEd.setEnabled(false);
		holder.DondeRecogerDineroEd.setEnabled(false);
		holder.EstadoDeOrdenEd.setEnabled(false);
		holder.DestinoEd.setTextIsSelectable(true);
		holder.EmpresaEd.setEnabled(false);
		holder.direccionEmpresaEd.setEnabled(false);
		holder.InstruccionesEd.setEnabled(false);
		holder.CostoDelProductoEd.setEnabled(false);
		holder.CostoDelEnvioEd.setEnabled(false);
		holder.tiendaTelefono.setEnabled(false);
		
		
		
		
		float CostProdNum=Float.parseFloat(mDataList.get(position).getCostoDelProducto());
		float CostEnvNum=Float.parseFloat(mDataList.get(position).getCostoDelEnvio());
		float resultadoDeProdMasEnv = CostProdNum + CostEnvNum;
		holder.CostoTotalTv.setText(String.valueOf(Float.toString(resultadoDeProdMasEnv)));
		
		
		
		
		
		
		
		//unfold button
		holder.unfoldButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (holder.layoutToCollapse.getVisibility() == View.GONE)
					{
						expand(holder.layoutToCollapse);
						holder.unfoldButton.setImageDrawable(holder.context.getResources().getDrawable(R.drawable.baseline_expand_less_black_24));
					}
					else
					{
						if (!(holder.layoutToCollapse.getVisibility() == View.GONE))
						{
							collapse(holder.layoutToCollapse);
							holder.unfoldButton.setImageDrawable(holder.context.getResources().getDrawable(R.drawable.baseline_expand_more_black_24));
						}
					}
				}
			});
		
		
		
		
		
//llamar
		holder.llamar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String phone = holder.callTv.getText().toString();
					holder.context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
				}
			});
		holder.llamarTienda.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String phone = holder.tiendaTelefono.getText().toString();
					holder.context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
				}
			});
			
			
			
			
			
			
			
//whatsapp 
		holder.whatsapp.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.setData(Uri.parse("https://wa.me/503"+mDataList.get(position).getTelefonoDeClienteDeDestino().toString()+"?text=Buen%20dia,%20le%20informo%20que%20su%20paquete%20de%20parte%20de%20"+mDataList.get(position).getEmpresaDePartida().toString()+"%20está%20en%20ruta%20hacia%20"+mDataList.get(position).getDireccionDeDestino()+".%20Solicito%20su%20ubicación%20por%20gps%20para%20llegar%20mas%20rapído.%20Att.%20Mario%20Mercaditos%20y%20envíos"));
					holder.context.startActivity(intent);
				}
			});
		holder.whatsappTienda.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.setData(Uri.parse("https://wa.me/503"+mDataList.get(position).getTelefonoTienda().toString()+"?text=Buen%20dia,%20me%20podría%20brindar%20ayuda%20con%20el%20cliente%20*"+mDataList.get(position).getClienteDeDestino().toString()+"*%20de%20"+mDataList.get(position).getDireccionDeDestino()+"%20att.%20mensajería."));
					holder.context.startActivity(intent);
				}
			});
			
			
			
//Recibido boton
		holder.ButtonPaqueteRecibido.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					final AlertDialog dialog = new AlertDialog.Builder(holder.context).create();
					dialog.setTitle("Alerta!");
					dialog.setMessage("Asegurate que el paquete sea el correcto");
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
								//TODO ACTUALIZAR DB QUE FUE RECIBIDO
								String ordrStat = holder.EstadoDeOrdenEd.getText().toString();
								DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Ordenes").child(mDataList.get(position).getNumeroDeOrden());
								mDatabase.child("estadoDeOrden").setValue(ordrStat);
								holder.ButtonPaqueteRecibido.setEnabled(false);
							}
						});
						dialog.show();

				}

			});
//entregado boton
		holder.ButtonPaqueteEntregado.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					final AlertDialog dialog = new AlertDialog.Builder(holder.context).create();
					dialog.setTitle("Alerta!");
					dialog.setMessage("Asegurate que el paquete sea el correcto");
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
								//TODO ACTUALIZAR DB QUE FUE entregado
								DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Ordenes").child(mDataList.get(position).getNumeroDeOrden());
								mDatabase.child("estadoDeOrden").setValue("Completada");
								holder.ButtonPaqueteRecibido.setEnabled(false);
							}
						});
					dialog.show();

				}

			});
//Mapa partida
		holder.PartidaBt.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
											   Uri.parse("http://maps.google.com/maps?daddr="+latLngA.latitude+","+latLngA.longitude));
					holder.context.startActivity(intent);
				}
			});
//mapa destino 
		holder.DestinoBt.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Uri gmmIntentUri = Uri.parse("geo:0,0?q="+Uri.parse(mDataList.get(position).getDireccionDeDestino()));
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
											   gmmIntentUri);
					holder.context.startActivity(intent);
				}
			});
    }
	
//Expand and collapse
	public static void expand(final View v) {
		int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
		int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
		final int targetHeight = v.getMeasuredHeight();

		// Older versions of android (pre API 21) cancel animations for views with a height of 0.
		v.getLayoutParams().height = 1;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
                    ? LayoutParams.WRAP_CONTENT
                    : (int)(targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				}else{
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}
	
	
	
	
//nothing
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
	
	
}


//Class virwholder
class mViewHolder extends RecyclerView.ViewHolder {

    EditText NumeroDeOrdenEd,DestinoEd,DistanciaEd,FechaEtaEd,DondeRecogerDineroEd,EstadoDeOrdenEd,tiendaTelefono;
	Button PartidaBt,DestinoBt,ButtonPaqueteRecibido,ButtonPaqueteEntregado,llamar,whatsapp,llamarTienda,whatsappTienda;
	Context context;
	TextView clienteDeDestinoEd,callTv;
	ImageView unfoldButton;
	LinearLayout layoutToCollapse;
	
	EditText CostoDelProductoEd,DriverAsignado,EmpresaEd,direccionEmpresaEd,InstruccionesEd,CostoDelEnvioEd;
	TextView CostoTotalTv;

	String user;

    mViewHolder(View v) {
        super(v);

		NumeroDeOrdenEd = v.findViewById(R.id.dashboarOrderTitle);
		clienteDeDestinoEd = v.findViewById(R.id.dashboardAddressA);
		DestinoEd = v.findViewById(R.id.dashboardAddressB);
		DistanciaEd = v.findViewById(R.id.dashboardDistance);
		FechaEtaEd = v.findViewById(R.id.dashboardDateEta);
		DondeRecogerDineroEd = v.findViewById(R.id.dashboardWhereGetMoney);
		EstadoDeOrdenEd = v.findViewById(R.id.dashboardOrderStatus);
		PartidaBt = v.findViewById(R.id.orderrowButtonPartida);
		DestinoBt = v.findViewById(R.id.orderrowButtonDestino);
		ButtonPaqueteRecibido = v.findViewById(R.id.orderrowButtonRecibido);
		ButtonPaqueteEntregado = v.findViewById(R.id.orderrowButtonEntregado);
		llamar = v.findViewById(R.id.llamarorderpoolrowButton1);
		callTv = v.findViewById(R.id.CelulardashboardAddressA);
		CostoDelProductoEd = v.findViewById(R.id.costodelproducto);
		CostoDelEnvioEd = v.findViewById(R.id.costodelenvio);
		CostoTotalTv = v.findViewById(R.id.totalCostSum);
		EmpresaEd = v.findViewById(R.id.empresaEd);
		direccionEmpresaEd = v.findViewById(R.id.direccionEmpresaEd);
		InstruccionesEd = v.findViewById(R.id.instruccionesEd);
		whatsapp = v.findViewById(R.id.whatsapporderrowButton1);
		unfoldButton = v.findViewById(R.id.ExpandorderrowImageView1);
		layoutToCollapse = v.findViewById(R.id.orderRowLayoutToCollapse);
		context = v.getContext();
		tiendaTelefono = v.findViewById(R.id.empresaEdPhone);
		whatsappTienda = v.findViewById(R.id.tiendaWhatsapp);
		llamarTienda = v.findViewById(R.id.tiendaLlamar);
		FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
		FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
		user = mFirebaseUser.getDisplayName().toString();

    }
	
	

	




}

