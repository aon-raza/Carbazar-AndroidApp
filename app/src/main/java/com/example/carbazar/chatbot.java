package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.chatMessages;
import com.example.carbazar.Models.chatOptions;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterChatbot;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterChatbotOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static java.lang.Thread.sleep;

public class chatbot extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, OnOptionClickListener {

    private Toolbar toolbar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    private BottomNavigationView bottomNavigationView;

    private List<chatMessages> messagesList;
    private List<chatOptions> chatOptionsList;
    ProgressDialog mDialog;

    private RelativeLayout btnSend;
    private AppCompatEditText userMessage;
    private AppCompatTextView cbTyping;
    private String messageUserSent;

    private RelativeLayout layout_bottom;
    private String sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CarBazar Assistant");

        layout_bottom = findViewById(R.id.layout_bottom);

        messagesList = new ArrayList<chatMessages>();
        chatOptionsList = new ArrayList<chatOptions>();
        mDialog = new ProgressDialog(chatbot.this);

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        btnSend = findViewById(R.id.btnSend);
        userMessage = findViewById(R.id.edChat);
        cbTyping = findViewById(R.id.cbtyping);

        cbTyping.setVisibility(View.GONE);

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                if(isVisible){
                    bottomNavigationView.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) layout_bottom.getLayoutParams();
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }
                else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) layout_bottom.getLayoutParams();
                    rlp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }
            }
        });

        mDialog.setMessage("Please wait...");
        mDialog.show();
        compositeDisposable.add(iMyService.getSessionId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        sessionID = jsonObject.getJSONObject("result").getString("session_id");
                        compositeDisposable.add(iMyService.getChatbotResponse("hey", sessionID)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object response) throws Exception {
                                        mDialog.dismiss();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(chatbot.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(chatbot.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

        chatMessages chatMessagesObj = new chatMessages();
        chatMessagesObj.setChatbotMsg("Hello there...");
        chatMessagesObj.setUserMsg("");
        chatMessagesObj.setImage("");
        messagesList.add(chatMessagesObj);
        initRecyclerView();

        chatMessages chatMessagesObj2 = new chatMessages();
        chatMessagesObj2.setChatbotMsg("Car bazar virtual representative here, How can i help you?");
        chatMessagesObj2.setUserMsg("");
        chatMessagesObj2.setImage("");
        messagesList.add(chatMessagesObj2);
        initRecyclerView();

        chatOptions chatOptionsObj = new chatOptions();
        chatOptionsObj.setLabel("System information");
        chatOptionsObj.setText("AskSystemInformation");
        chatOptionsList.add(chatOptionsObj);

        chatOptions chatOptionsObj2 = new chatOptions();
        chatOptionsObj2.setLabel("Cars information");
        chatOptionsObj2.setText("AskCarInformation");
        chatOptionsList.add(chatOptionsObj2);
        initRecyclerViewChatOptions();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userMessage.getText().toString().contentEquals("")){
                    Toast.makeText(getApplicationContext(), "Enter message first", Toast.LENGTH_SHORT).show();
                }
                else{
                    chatMessages chatMessagesObj = new chatMessages();
                    chatMessagesObj.setUserMsg(userMessage.getText().toString());
                    chatMessagesObj.setChatbotMsg("");
                    chatMessagesObj.setImage("");

                    messagesList.add(chatMessagesObj);
                    initRecyclerView();
                    messageUserSent = userMessage.getText().toString();

                    cbTyping.setVisibility(View.VISIBLE);
//
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
                            getChatbotMessage(messageUserSent);
//                        }
//                    }, 1000);
                }
                userMessage.setText("");
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(chatbot.this, MainActivity.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(chatbot.this, MainActivity.class);
        startActivity(intent1);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                Intent intent3 = new Intent(chatbot.this, MainActivity.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                //
                Intent intent1 = new Intent(chatbot.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(chatbot.this, ARHome.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
                return true;

            case R.id.account_nav:
                //
                Intent intent = new Intent(chatbot.this, account.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
                return true;


        }
        return false;
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void getChatbotMessage(String userMsg){
        cbTyping.setVisibility(View.VISIBLE);
        chatOptionsList.clear();

        compositeDisposable.add(iMyService.getChatbotResponse(userMsg, sessionID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        JSONArray jsonArray =  jsonObject.getJSONObject("result").getJSONObject("output").getJSONArray("generic");

                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject1=  jsonArray.getJSONObject(i);
                            if(jsonObject1.getString("response_type").contentEquals("option")){
                                chatMessages chatMessagesObj = new chatMessages();
                                chatMessagesObj.setChatbotMsg(jsonObject1.getString("title"));
                                chatMessagesObj.setUserMsg("");
                                chatMessagesObj.setImage("");
                                messagesList.add(chatMessagesObj);
                                initRecyclerView();

                                chatMessages chatMessagesObj2 = new chatMessages();
                                if(jsonObject1.has("description")){
                                    chatMessagesObj2.setChatbotMsg(jsonObject1.getString("description"));
                                    chatMessagesObj2.setUserMsg("");
                                    chatMessagesObj2.setImage("");
                                    messagesList.add(chatMessagesObj2);
                                    initRecyclerView();
                                }

                                JSONArray options = jsonObject1.getJSONArray("options");
                                int j=0;
                                while (j<options.length()){
                                    chatOptions chatOptionsObj = new chatOptions();
                                    chatOptionsObj.setLabel(options.getJSONObject(j).getString("label"));
                                    chatOptionsObj.setText(options.getJSONObject(j).getJSONObject("value")
                                            .getJSONObject("input").getString("text"));
                                    chatOptionsList.add(chatOptionsObj);
                                    j++;
                                }
                                initRecyclerViewChatOptions();
                            }
                            else if(jsonObject1.getString("response_type").contentEquals("image")){
                                chatMessages chatMessagesObj = new chatMessages();
                                chatMessagesObj.setChatbotMsg(jsonObject1.getString("title"));
                                chatMessagesObj.setUserMsg("");
                                chatMessagesObj.setImage("");
                                if(!jsonObject1.has("description")){
                                    chatMessagesObj.setImage(jsonObject1.getString("source"));
                                }
                                messagesList.add(chatMessagesObj);
                                initRecyclerView();

                                if(jsonObject1.has("description")){
                                    chatMessages chatMessagesObj2 = new chatMessages();
                                    chatMessagesObj2.setChatbotMsg(jsonObject1.getString("description"));
                                    chatMessagesObj2.setUserMsg("");
                                    chatMessagesObj2.setImage(jsonObject1.getString("source"));
                                    messagesList.add(chatMessagesObj2);
                                    initRecyclerView();
                                }

                            }
                            else if(jsonObject1.getString("response_type").contentEquals("text")){
                                chatMessages chatMessagesObj = new chatMessages();
                                chatMessagesObj.setChatbotMsg(jsonObject1.getString("text"));
                                chatMessagesObj.setUserMsg("");
                                chatMessagesObj.setImage("");
                                messagesList.add(chatMessagesObj);
                                initRecyclerView();
                            }
                            i++;
                        }
                        cbTyping.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(chatbot.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
//        if(userMsg.contentEquals("Ok")){
//            chatMessages chatMessagesObj = new chatMessages();
//            chatMessagesObj.setChatbotMsg("Acha dimaagh naa khaa");
//            chatMessagesObj.setUserMsg("");
//
//            messagesList.add(chatMessagesObj);
//            initRecyclerView();
//        }
//        else if(userMsg.contentEquals("Tu or bhi baat krta hai?")){
//            chatMessages chatMessagesObj = new chatMessages();
//            chatMessagesObj.setChatbotMsg("nai");
//            chatMessagesObj.setUserMsg("");
//
//            messagesList.add(chatMessagesObj);
//            initRecyclerView();
//        }
//        else if(userMsg.contentEquals("Teri maa ka")){
//            chatMessages chatMessagesObj = new chatMessages();
//            chatMessagesObj.setChatbotMsg("zubaan sambhaal k...");
//            chatMessagesObj.setUserMsg("");
//
//            messagesList.add(chatMessagesObj);
//            initRecyclerView();
//        }
//        else {
//            chatMessages chatMessagesObj = new chatMessages();
//            chatMessagesObj.setChatbotMsg("Chal bey");
//            chatMessagesObj.setUserMsg("");
//
//            messagesList.add(chatMessagesObj);
//            initRecyclerView();
//        }
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.rvChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapterChatbot adapter = new recyclerViewAdapterChatbot(this,messagesList);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        //recyclerView.getLayoutManager().scrollToPosition(messagesList.size());
        mDialog.dismiss();

    }

    @Override
    public void onOptionClick(String optionData, String label) {
        cbTyping.setVisibility(View.VISIBLE);
        chatOptionsList.clear();

        chatMessages chatMessagesObj = new chatMessages();
        chatMessagesObj.setUserMsg(label);
        chatMessagesObj.setChatbotMsg("");
        chatMessagesObj.setImage("");
        messagesList.add(chatMessagesObj);
        initRecyclerView();

        compositeDisposable.add(iMyService.getChatbotResponse(optionData, sessionID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        JSONArray jsonArray =  jsonObject.getJSONObject("result").getJSONObject("output").getJSONArray("generic");

                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject1=  jsonArray.getJSONObject(i);
                            if(jsonObject1.getString("response_type").contentEquals("option")){
                                chatMessages chatMessagesObj = new chatMessages();
                                chatMessagesObj.setChatbotMsg(jsonObject1.getString("title"));
                                chatMessagesObj.setUserMsg("");
                                chatMessagesObj.setImage("");
                                messagesList.add(chatMessagesObj);
                                initRecyclerView();

                                chatMessages chatMessagesObj2 = new chatMessages();
                                if(jsonObject1.has("description")){
                                    chatMessagesObj2.setChatbotMsg(jsonObject1.getString("description"));
                                    chatMessagesObj2.setUserMsg("");
                                    chatMessagesObj2.setImage("");
                                    messagesList.add(chatMessagesObj2);
                                    initRecyclerView();
                                }

                                JSONArray options = jsonObject1.getJSONArray("options");
                                int j=0;
                                while (j<options.length()){
                                    chatOptions chatOptionsObj = new chatOptions();
                                    chatOptionsObj.setLabel(options.getJSONObject(j).getString("label"));
                                    chatOptionsObj.setText(options.getJSONObject(j).getJSONObject("value")
                                            .getJSONObject("input").getString("text"));
                                    chatOptionsList.add(chatOptionsObj);
                                    j++;
                                }
                                initRecyclerViewChatOptions();
                            }
                            else if(jsonObject1.getString("response_type").contentEquals("image")){
                                chatMessages chatMessagesObj = new chatMessages();
                                chatMessagesObj.setChatbotMsg(jsonObject1.getString("title"));
                                chatMessagesObj.setUserMsg("");
                                chatMessagesObj.setImage("");
                                if(!jsonObject1.has("description")){
                                    chatMessagesObj.setImage(jsonObject1.getString("source"));
                                }
                                messagesList.add(chatMessagesObj);
                                initRecyclerView();

                                if(jsonObject1.has("description")){
                                    chatMessages chatMessagesObj2 = new chatMessages();
                                    chatMessagesObj2.setChatbotMsg(jsonObject1.getString("description"));
                                    chatMessagesObj2.setUserMsg("");
                                    chatMessagesObj2.setImage(jsonObject1.getString("source"));
                                    messagesList.add(chatMessagesObj2);
                                    initRecyclerView();
                                }

                            }
                            else if(jsonObject1.getString("response_type").contentEquals("text")){
                                chatMessages chatMessagesObj = new chatMessages();
                                chatMessagesObj.setChatbotMsg(jsonObject1.getString("text"));
                                chatMessagesObj.setUserMsg("");
                                chatMessagesObj.setImage("");
                                messagesList.add(chatMessagesObj);
                                initRecyclerView();
                            }
                            i++;
                        }
                        cbTyping.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(chatbot.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public void onpostClick(String postID) {

    }

    private void initRecyclerViewChatOptions(){

        RecyclerView recyclerView = findViewById(R.id.recyclerViewOptions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapterChatbotOptions adapter = new recyclerViewAdapterChatbotOptions(this,chatOptionsList);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }
}
