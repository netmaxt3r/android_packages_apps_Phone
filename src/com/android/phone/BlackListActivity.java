package com.android.phone;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.List;

public class BlackListActivity extends ListActivity {

    private static final int CONTACT_PICKER_RESULT = 0x112;
    private BlackListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blacklist_activity);
         adapter= new BlackListAdapter();
        setListAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.blacklist_menu, menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.blacklist_add){
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,  
                    Contacts.CONTENT_URI);  
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT); 
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode== CONTACT_PICKER_RESULT){
                Cursor cursor = null;  
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {  
                    Uri result = data.getData();  
                    String id = result.getLastPathSegment();  
                    cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);  
                    phoneIdx = cursor.getColumnIndex(Phone.DATA);
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            phoneNumber = cursor.getString(phoneIdx);
                            allNumbers.add(phoneNumber);
                            cursor.moveToNext();
                        }
                    } else {
                        //no results actions
                    }  
                } catch (Exception e) {  
                   //error actions
                } finally {  
                    if (cursor != null) {  
                        cursor.close();
                    }

                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlackListActivity.this);
                    builder.setTitle("Choose a number");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String selectedNumber = items[item].toString();
                            selectedNumber = selectedNumber.replace("-", "");
                            addtoBlackList(selectedNumber);
                        }
                    });
                    AlertDialog alert = builder.create();
                    if(allNumbers.size() > 1) {
                        alert.show();
                    } else {
                        if(phoneNumber==null||"".equalsIgnoreCase(phoneNumber)){
                            AlertDialog nonumber = builder.create();
                            nonumber.setTitle("No number found for the contact");
                            nonumber.show();
                        }else{
                            String selectedNumber = phoneNumber.toString();
                            selectedNumber = selectedNumber.replace("-", "");
                            addtoBlackList(selectedNumber);
                        }
                    }

                    if (phoneNumber.length() == 0) {  
                        //no numbers found actions  
                    }  
                }  
            }
        }
    }

    protected void addtoBlackList(String selectedNumber) {
         BlackListUtil.addBlackList(this,selectedNumber);
         adapter.notifyDataSetChanged();
    }
    protected void removeFromBlackList(String selectedNumber) {
        BlackListUtil.removeFromBlackList(this,selectedNumber);
        adapter.notifyDataSetChanged();
   }
    private class BlackListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return BlackListUtil.getCount(BlackListActivity.this);
        }

        @Override
        public Object getItem(int position) {
            return BlackListUtil.get(BlackListActivity.this,position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder h;
            if(convertView==null){
                convertView = LayoutInflater.from(BlackListActivity.this).inflate(R.layout.blacklist_item, null);
                h = new Holder();
                h.text1 = (TextView)convertView.findViewById(R.id.text1);
                h.text2 = (TextView)convertView.findViewById(R.id.text2);
                h.delete = (ImageButton) convertView.findViewById(R.id.button1);
                h.data="";
                h.delete.setOnClickListener(new ClickListner(h));
                convertView.setTag(h);
            }
            h = (Holder) convertView.getTag();
            String data = getItem(position).toString();
            h.text1.setText(data);
            h.data =data;
            return convertView;
        }

        
        
    }
    protected void onDelete(Holder h) {
        removeFromBlackList(h.data);
    }
    class ClickListner implements OnClickListener{
        Holder h;
        public ClickListner(Holder h) {
            super();
            this.h = h;
        }

        @Override
        public void onClick(View v) {
            onDelete(h);
        }
        
    }
     class Holder{
        TextView text1;
        TextView text2;
        ImageButton delete;
        String data;
    }
}
