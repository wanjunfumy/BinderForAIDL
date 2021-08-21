package com.wjf.binderforaidl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wanjf.baseVO.Person;

public class UserService extends Service {

    private final Stub stub;

    public UserService() {
        this.stub = new Stub();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 客户端连接上来了，提示下而已
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String CHANNEL_NAME = "TEST";
        String CHANNEL_ID = "com.example.recyclerviewtest.N1";
        NotificationChannel notificationChannel = null;
        notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).
                setContentTitle("BinderForAIDL").
                setContentText("A Client connection!").
                setWhen(System.currentTimeMillis()).
                setSmallIcon(R.mipmap.ic_launcher).
                setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).
                setContentIntent(pendingIntent).build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("客户端连接成功");
        return stub;
    }

    static class Stub extends Binder implements UserImpl {
        private static final String SEND_TOKEN = "token_128379824234356345";
        private static final String REPLY_TOKEN = "token_4529342983402834";

        private final int get_person = 10;
        private final int get_persons = 11;

        private String person = "{\"name\":\"wanjunfu\", \"age\":\"18\", \"sex\":\"male\"}";


        @Override
        public Person getPerson(String name) {
            return new Person("wanjunfu", "18", "male");
        }

        @Override
        public Person[] getPersons() {
            Person person1 = new Person("wanjunfu", "18", "male");
            Person person2 = new Person("wanjunfu", "18", "male");
            Person person3 = new Person("wanjunfu", "18", "male");
            Person person4 = new Person("wanjunfu", "18", "male");
            return new Person[]{person1, person2, person3, person4};
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) {
            try {
                switch (code) {
                    case get_person:
                        data.enforceInterface(SEND_TOKEN); // 这个用于验证客户端传入的是否和这个一致，不一致就over，直接报错了。
                        if (reply != null) {
                            if (data.readInt() == 1) { // 没啥实际意义，注意一点，Parcel的顺序，先丢进来的，就先取，这里先取了，那客户端第一个发的，就是int类型，
                                reply.writeParcelable(getPerson(data.readString()), 0); // 把人打包发送出去。
                                System.out.println("打包发送成功");
                            } else {
                                reply.writeString("您输入的名字为空！");
                            }
                        }
                        return true;
                    case get_persons:
                        data.enforceInterface(REPLY_TOKEN);
                        if (reply != null) {
                            if (data.readInt() == 1) {
                                reply.writeParcelableArray(getPersons(), 0);
                                System.out.println("打包发送成功");
                            } else {
                                reply.writeString("查询不到人的列表！");
                            }
                        }
                        return true;
                }
                return super.onTransact(code, data, reply, flags);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
