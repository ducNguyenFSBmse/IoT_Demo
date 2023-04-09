package com.example.iotdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemper, txtHumid;

    LabeledSwitch buttonLed, buttonPump;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemper = findViewById(R.id.txtTemperature);
        txtHumid = findViewById(R.id.txtHumidity);
        buttonLed = findViewById(R.id.buttonLED);
        buttonPump = findViewById(R.id.buttonPump);

        buttonLed.setOnToggledListener(new OnToggledListener(){
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("ducNg22mse23059/feeds/images-dashboard", "1");
                } else {
                    sendDataMQTT("ducNg22mse23059/feeds/images-dashboard", "0");
                }
            }
        });

        buttonPump.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("ducNg22mse23059/feeds/images-dashboard", "1");
                } else {
                    sendDataMQTT("ducNg22mse23059/feeds/images-dashboard", "0");
                }
            }
        });
        this.startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " *** " + message.toString());
                if(topic.contains("cambien1")){
                    txtTemper.setText(message.toString() + "°C");
                } else if(topic.contains("cambien2")){
                    txtHumid.setText(message.toString() + "%");
                } else if(topic.contains("nutnhan1")){
                    if(message.toString().equals("1")){
                        buttonLed.setOn(true);
                    } else {
                        buttonLed.setOn(false);
                    }
                } else if(topic.contains("nutnhan2")){
                    if(message.toString().equals("1")){
                        buttonPump.setOn(true);
                    } else {
                        buttonPump.setOn(false);
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}