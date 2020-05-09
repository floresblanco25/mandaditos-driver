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
import com.google.firebase.database.*;
import com.mandaditos.driver.models.*;
import java.util.*;

import android.view.View.OnClickListener;

public class mAdapter extends RecyclerView.Adapter<mViewHolder>
{

//Initialize
    private Context mContext;
    private List<mandaditosModel> mDataList;

	
//Constructor
    mAdapter(Context mContext, List< mandaditosModel > mDataList) {
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
		holder.PartidaEd.setText(mDataList.get(position).getPartida());
		holder.DestinoEd.setText(mDataList.get(position).getDestino());
		holder.DistanciaEd.setText(mDataList.get(position).getDistancia());
		holder.FechaEtaEd.setText(mDataList.get(position).getFecha()+" "+mDataList.get(position).getEta());
		holder.DondeRecogerDineroEd.setText(mDataList.get(position).getRecogerDineroEn());
		holder.CostoEd.setText(mDataList.get(position).getCosto());
		holder.EstadoDeOrdenEd.setText(mDataList.get(position).getEstadoDeOrden());
		holder.NumeroDeOrdenEd.setText(mDataList.get(position).getNumeroDeOrden());
		holder.NumeroDeOrdenEd.setEnabled(false);
		holder.PartidaEd.setEnabled(false);
		holder.DestinoEd.setEnabled(false);
		holder.DistanciaEd.setEnabled(false);
		holder.FechaEtaEd.setEnabled(false);
		holder.DondeRecogerDineroEd.setEnabled(false);
		holder.CostoEd.setEnabled(false);
		holder.EstadoDeOrdenEd.setEnabled(false);
		
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
		//Recibido boton
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
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
											   Uri.parse("http://maps.google.com/maps?daddr="+latLngB.latitude+","+latLngB.longitude));
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

		// Expansion speed of 1dp/ms
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

		// Collapse speed of 1dp/ms
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

    EditText NumeroDeOrdenEd,PartidaEd,DestinoEd,DistanciaEd,FechaEtaEd,DondeRecogerDineroEd,CostoEd,EstadoDeOrdenEd;
	Button PartidaBt,DestinoBt,ButtonPaqueteRecibido,ButtonPaqueteEntregado;
	Context context;

    mViewHolder(View v) {
        super(v);

		NumeroDeOrdenEd = v.findViewById(R.id.dashboarOrderTitle);
		PartidaEd = v.findViewById(R.id.dashboardAddressA);
		DestinoEd = v.findViewById(R.id.dashboardAddressB);
		DistanciaEd = v.findViewById(R.id.dashboardDistance);
		FechaEtaEd = v.findViewById(R.id.dashboardDateEta);
		DondeRecogerDineroEd = v.findViewById(R.id.dashboardWhereGetMoney);
		CostoEd = v.findViewById(R.id.dashboardTotalCost);
		EstadoDeOrdenEd = v.findViewById(R.id.dashboardOrderStatus);
		PartidaBt = v.findViewById(R.id.orderrowButtonPartida);
		DestinoBt = v.findViewById(R.id.orderrowButtonDestino);
		ButtonPaqueteRecibido = v.findViewById(R.id.orderrowButtonRecibido);
		ButtonPaqueteEntregado = v.findViewById(R.id.orderrowButtonEntregado);
		context = v.getContext();

    }
	
	

	




}

