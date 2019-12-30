package com.bt.demo;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.BtPrinterCommand;
import com.printer.PrintPort;
import com.bt.printer.R;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private PrintPort printPort;
    private static final boolean D = true;
    private boolean isConnected = false;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private String address = "";
    private String name = "";
    private BluetoothAdapter mBluetoothAdapter = null;
    // Layout Views
    private TextView mTitle, pagenumber;
    private Button mSendButton, commendSendButton, pdfButton;
    private EditText sampleEdit, startPageEdit, endPageEdit;
    private int sampleNumber;
    private int interval;
    private boolean isSending = false;
    private ImageButton searchButton;

    private String mFilePath;
    private Bitmap bmp;
    private int pageNumbers;
    private int startpage = 0, endpage = 0;
    private TableLayout tableLayout;

    public static final String Author = "zhougf(edivista@vip.qq.com)" +
            "微信：edivista" +
            "QQ: 77175792";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);

        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        sampleEdit = (EditText) findViewById(R.id.sampleEdit);
        startPageEdit = (EditText) findViewById(R.id.start_page);
        endPageEdit = (EditText) findViewById(R.id.end_page);
        tableLayout = (TableLayout) findViewById(R.id.pdfedit);
        pagenumber = (TextView) findViewById(R.id.pagenumber);

        //obtain the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //If the Bluetooth adapter is not supported,programmer is over
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

        printPort = new PrintPort();

        searchButton = (ImageButton) findViewById(R.id.search);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sampleEdit.getText().toString().trim().equals("")) {
                    sampleNumber = 1;
                } else {
                    sampleNumber = Integer.valueOf(sampleEdit.getText().toString().trim());
                }

                if (!isSending) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < sampleNumber; i++) {
                                Log.e(TAG, String.valueOf(i));
                                isSending = true;
                                if (isConnected) {
                                    printebmpData();

                                }

                                try {
                                    interval = 0;
                                    Thread.sleep(interval);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (i == (sampleNumber - 1)) {
                                    isSending = false;
                                }

                            }
                        }
                    }).start();
                }
            }
        });


        commendSendButton = (Button) findViewById(R.id.button_commendsend);
        commendSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sampleEdit.getText().toString().trim().equals("")) {
                    sampleNumber = 1;
                } else {
                    sampleNumber = Integer.valueOf(sampleEdit.getText().toString().trim());
                }

                if (!isSending) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < sampleNumber; i++) {
                                Log.e(TAG, String.valueOf(i));
                                isSending = true;
                                if (isConnected) {
                                    printecommendData();

                                }

                                try {
                                    interval = 0;
                                    Thread.sleep(interval);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (i == (sampleNumber - 1)) {
                                    isSending = false;
                                }

                            }
                        }
                    }).start();
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled
        // setupChat() will then be called during onActivityRe//sultsetupChat
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) {
            Log.d(TAG, "onActivityResult " + resultCode);
        }

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    if (isConnected & (printPort != null)) {
                        printPort.disconnect();
                        isConnected = false;
                    }
                    String sdata = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    address = sdata.substring(sdata.length() - 17);
                    name = sdata.substring(0, (sdata.length() - 17));
                    if (!isConnected) {
                        if (printPort.connect(address)) {
                            isConnected = true;
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(name);

                        } else {

                            isConnected = false;

                        }

                    }

                }
                break;
            case REQUEST_ENABLE_BT:

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    public void onPause() {
        super.onPause();
        isConnected = false;
        printPort.disconnect();
        mTitle.setText("已断开");

    }


    private void ensureDiscoverable() {
        if (D) {
            Log.d(TAG, "ensure discoverable");
        }
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    public void printebmpData() {
        if (isConnected) {

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test2);

            ArrayList<byte[]> data = new ArrayList<byte[]>();
            byte[] wakeup = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            BtPrinterCommand printer = new BtPrinterCommand();
            data.add(wakeup);
            data.add(printer.BtPrinter_CreatePage(100, 100));
            // 设置打印方向
            data.add(printer.BtPrinter_Direction(0, 0));
            // 始能切刀，没打完一张自动切纸
            data.add(printer.BtPrinter_Cut(true));
            // 设置缝隙定位
            data.add(printer.BtPrinter_SetGap(true));
            // 设置速度3
            data.add(printer.BtPrinter_Speed(3));
            // 设置浓度
            data.add(printer.BtPrinter_Density(5));
            // 清除页面缓冲区
            data.add(printer.BtPrinter_Cls());

            data.add(printer.BtPrinter_DrawPic(0, 0, bitmap));
            data.add(printer.BtPrinter_PrintPage(1));
            printPort.portSendCmd(data);

        }
    }

    public void printecommendData() {
        if (isConnected) {

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);

            ArrayList<byte[]> data = new ArrayList<byte[]>();
            byte[] wakeup = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            BtPrinterCommand printer = new BtPrinterCommand();
            data.add(wakeup);
            data.add(printer.BtPrinter_CreatePage(100, 175));
            // 设置打印方向
            data.add(printer.BtPrinter_Direction(0, 0));
            // 始能切刀，没打完一张自动切纸
            data.add(printer.BtPrinter_Cut(true));
            // 设置缝隙定位
            data.add(printer.BtPrinter_SetGap(true));
            // 设置速度3
            data.add(printer.BtPrinter_Speed(6));
            // 设置浓度
            data.add(printer.BtPrinter_Density(5));
            // 清除页面缓冲区
            data.add(printer.BtPrinter_Cls());

            // 画线
            data.add(printer.BtPrinter_DrawLine(300, 10, 4, 90, 0));
            data.add(printer.BtPrinter_DrawLine(320, 420, 600, 700, 5, 0));

            data.add(printer.BtPrinter_DrawLine(320, 780, 600, 460, 8, 3));

            data.add(printer.BtPrinter_Text(30, 120, "CH24.FNT", 0, 1, 1, true, "发  件  人：张三 (电话 874236021)"));

            data.add(printer.BtPrinter_Textbox(30, 800, "CH24.FNT", 0, false, 1, 1, 700, 24, "万琛电子根据物流行业对电子面单打印机的需求，推出全新的电子面单打印机—启锐电子面单打印机，针对物流行业对电子面单的高标准，该机型做了大量的调整和创新，推出了现行同规格电子面单打印机没有的全新功能，同时也对现有打印机存在的问题进行了转向改进。"));

            data.add(printer.BtPrinter_DrawLine(30, 100, 740, 4, 0));
            data.add(printer.BtPrinter_DrawLine(30, 880, 740, 4, 0));
            data.add(printer.BtPrinter_DrawLine(30, 1300, 740, 4, 0));

            // 打印文字
            data.add(printer.BtPrinter_Text(400, 25, "CH24.FNT", 0, 3, 3, "上海浦东"));
            data.add(printer.BtPrinter_Text(30, 120, "CH24.FNT", 0, 1, 1, "发  件  人：张三 (电话 874236021)"));
            data.add(printer.BtPrinter_Text(30, 150, "CH24.FNT", 0, 1, 1, "发件人地址：广州省 深圳市 福田区 思创路123号\"工业园\"1栋2楼"));
            data.add(printer.BtPrinter_Text(30, 200, "CH24.FNT", 0, 1, 1, "收  件  人：李四 (电话 13899658435)"));
            data.add(printer.BtPrinter_Text(30, 230, "CH24.FNT", 0, 1, 1, "收件人地址：上海市 浦东区 太仓路司务小区9栋1105室"));

            data.add(printer.BtPrinter_Text(30, 700, "CH16.FNT", 0, 1, 1, "各类邮件禁寄、限寄的范围，除上述规定外，还应参阅“中华人民共和国海关对"));
            data.add(printer.BtPrinter_Text(30, 720, "CH16.FNT", 0, 1, 1, "进出口邮递物品监管办法”和国家法令有关禁止和限制邮寄物品的规定，以及邮"));
            data.add(printer.BtPrinter_Text(30, 740, "CH16.FNT", 0, 1, 1, "寄物品的规定，以及邮电部转发的各国（地区）邮 政禁止和限制。"));
            data.add(printer.BtPrinter_Text(30, 760, "CH16.FNT", 0, 1, 1, "寄件人承诺不含有法律规定的违禁物品。"));

            // 打印一维条码
            data.add(printer.BtPrinter_DrawBar(80, 300, 0, 90, 2, 0, 4, "873456093465"));
            data.add(printer.BtPrinter_DrawBar(550, 910, 0, 50, 2, 0, 2, "873456093465"));

            // 画方框
            data.add(printer.BtPrinter_DrawBox(40, 500, 340, 650, 4, 20));

            data.add(printer.BtPrinter_Text(60, 520, "CH24.FNT", 0, 1, 1, "寄件人签字："));
            data.add(printer.BtPrinter_Text(130, 625, "CH24.FNT", 0, 1, 1, "2015-10-30 09:09"));
            data.add(printer.BtPrinter_Text(50, 1000, "CH32.FNT", 0, 2, 3, "广东 ---- 上海浦东"));

            // 画圆
            data.add(printer.BtPrinter_DrawCircle(700, 1200, 50, 6));

            data.add(printer.BtPrinter_Text(670, 1170, "CH24.FNT", 0, 3, 3, "碎"));

            // 打印qrcode
            data.add(printer.BtPrinter_DrawQRCode(620, 620, 3, 0, 4, "www.baidu.com   www.baidu.com   www.baidu.com"));

            // 打印datamatrix
            data.add(printer.BtPrinter_DrawDataMatrix(100, 1300, 400, 400, "www.baidu.com"));

            // 发送打印命令，打印整个标签
            data.add(printer.BtPrinter_PrintPage(1));
            printPort.portSendCmd(data);

        }
    }


}


