package com.soniya.sellersapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*
-this is payment screen showing the total amount and link to checkout
-also asks permission to read/receive text messages from phone
-on checkout, it redirects to paytm payment gateway to proceed for transaction
 */

public class PaymentGateway extends AppCompatActivity implements View.OnClickListener {

    private final String merchantId = "TtIKjC23738458476697";
    private final String merchantKey = "wz@7#@@GI8MRnB6W";
    PaytmPGService Service;
   // PaytmOrder Order;
    HashMap<String, String> paramMap;
    DatabaseReference db;
    FirebaseAdapter fbadapter;
    Button paymentone;
    Button paymenttwo;
    long count = 0;

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //continue to checkout process
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);

        paymentone = findViewById(R.id.payment1);
        paymentone.setOnClickListener(this);
        paymentone.setVisibility(View.INVISIBLE);
        paymenttwo = findViewById(R.id.payment2);
        paymenttwo.setOnClickListener(this);
        paymenttwo.setVisibility(View.INVISIBLE);

        if(getIntent() !=null) {
            Intent i = getIntent();
            if(i.getSerializableExtra("UserInfo") != null)  {
                UserInfoSerial userInformation = (UserInfoSerial) i.getSerializableExtra("UserInfo");
                if(userInformation !=null && userInformation.getEmailId() !=null)   {
                    Log.i("soni-", "payment gateway, we have userinfo object");
                }
            }
        }

        fbadapter = new FirebaseAdapter();

        db = FirebaseDatabase.getInstance().getReference();
        db.child("userInfo").child(decodeUsername(fbadapter.getCurrentUser())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {

                    db.child("paymentTransactions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot !=null && dataSnapshot.getValue() !=null){
                                if(dataSnapshot.hasChildren()/* && dataSnapshot.getValue() instanceof HashMap*/){
                                    //HashMap<String, HashMap<String,String>> hm1 = dataSnapshot.getValue();

                                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                                    while (iterator.hasNext())  {
                                        DataSnapshot ds = iterator.next();
                                        if(ds.getValue() instanceof HashMap){
                                            HashMap<String, String> hm = (HashMap<String, String>) ds.getValue();
                                            count += hm.keySet().size();
                                            Log.i("soni-", toStringHM(hm) + " " + String.valueOf(count));
                                        }
                                    }

                                    //count = dataSnapshot.getChildrenCount();
                                    //orderId = "Order"+String.valueOf(count+1);
                                    paramMap.put( "ORDER_ID" , "Order"+String.valueOf(count+1));
                                    paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+"Order"+String.valueOf(count+1));
                                }
                            }else{

                                paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+"Order"+String.valueOf(count+1));
                                //orderId = "Order"+String.valueOf(count+1);
                            }

                            Log.i("soni-", "Order Id  " + "Order"+String.valueOf(count+1));
                            paymentone.setVisibility(View.VISIBLE);
                            paymenttwo.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    UserInformation userinfo = dataSnapshot.getValue(UserInformation.class);
                    if (userinfo != null && userinfo.getEmailId() != null) {
//                        paramMap.put( "MOBILE_NO" , userinfo.getMobileNo());
                        paramMap.put( "EMAIL" , userinfo.getEmailId());
                        paramMap.put( "MOBILE_NO" , "7777777777");
//                        paramMap.put( "EMAIL" ,);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*db.child("paymentTransactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null){
                    if(dataSnapshot.hasChildren()){
                        count = dataSnapshot.getChildrenCount();
                        orderId = "Order"+String.valueOf(count+1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

        PaytmPGService Service = PaytmPGService.getStagingService();

        paramMap = new HashMap<String,String>();

        paramMap.put( "MID" , merchantId);
        // Key in your staging and production MID available in your dashboard

        paramMap.put( "CUST_ID" , "cust123");
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT" , "1");
        paramMap.put( "WEBSITE" , "WEBSTAGING");
        // This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
        // This is the staging value. Production value is available in your dashboard
//        paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");



        //PaytmClientCertificate Certificate = new PaytmClientCertificate(String inPassword, String inFileName);
        // inPassword is the password for client side certificate
        //inFileName is the file name of client side certificate

        //passing null below as there is no certificate

    }

    public String decodeUsername(String uname)  {
            return uname.replace(",", ".");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.payment1:
                Log.i("soni-", "Paying 500 rupees");
//                paramMap.put( "ORDER_ID" , orderId);
//                paramMap.put( "MOBILE_NO" , mobileNum);
//                paramMap.put( "EMAIL" , emailID);
//                paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderId);
                TreeMap<String, String> paytmParams = new TreeMap<String, String>();
                paytmParams.putAll(paramMap);

                try {
//                    String paytmChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(merchantKey, paytmParams);
//                    Log.i("soni-", "checksum= "+ paytmChecksum);
                    paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev5=");
                    PaytmOrder Order = new PaytmOrder(paramMap);
                    Service.initialize(Order, null);

                    Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
                        /*Call Backs*/
                        public void someUIErrorOccurred(String inErrorMessage) {}
                        public void onTransactionResponse(Bundle inResponse) {}
                        public void networkNotAvailable() {}
                        public void clientAuthenticationFailed(String inErrorMessage) {}
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {}
                        public void onBackPressedCancelTransaction() {}
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {}
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.payment2:
                Log.i("soni-", "Paying 3000 rupees");
                break;
        }
    }

    public void onTransactionResponse(Bundle inResponse) {

        /*Display the message as below */
        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    public void someUIErrorOccurred(String inErrorMessage) {
        /*Display the error message as below */
        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
    }

    public void networkNotAvailable() {
        /*Display the message as below */
        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
    }

    public void clientAuthenticationFailed(String inErrorMessage)  {
        /*Display the message as below */
        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
    }

    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl)  {
        /*Display the message as below */
        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
    }

    public void onBackPressedCancelTransaction(){
        /*Display the message as below */
        Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
    }
    public String genrateCheckSum(String Key, TreeMap<String, String> paramap){

        return null;
    }

    public String genrateRefundCheckSum(String Key, TreeMap<String, String> paramap){
        return null;
    }

    public boolean verifycheckSum(String masterKey, TreeMap<String, String> paramap,String responseCheckSumString){
        return true;
    }

    public String toStringHM(HashMap hm){
        String str = "";
        for(Object key: hm.keySet()){
            if(key instanceof String){
                str = str + " " + key;
            }
        }
        return str;
    }

}
