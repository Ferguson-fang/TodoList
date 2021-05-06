# TodoList
红岩网校移动开发部中期考核


### **包含的功能点**

* 用户进行本地注册登入
* 添加任务
* 定时任务提醒

### **效果图**



# 方法实现

**利用litepal实现数据持久化**

> * 导包
>
> ```
> implementation 'org.litepal.android:core:1.5.0'
> ```
>
> * Manifest里声明
>
>   ```xml
>   <application
>          ... android:name="org.litepal.LitePalApplication"></application>
>   ```
>
> * assets里创建litepal.xml
>
>   ```xml
>   <litepal>
>       <!--数据库名称-->
>       <dbname value="data" />
>       <!--数据库版本号-->
>       <version value="1" />
>       <!--用于设定所有的映射模型,即你定义数据库表的类名路径-->
>       <list>
>           <mapping class="com.example.todolist.logic.dao.User" />
>           <mapping class="com.example.todolist.logic.dao.Target"/>
>       </list>
>   </litepal>
>   ```
>
>   



**用户账号的本地保存**

> * 定义User类
>
>   ```java
>   //继承DataSupport类，用于对数据进行操作
>   public class User extends DataSupport {
>       private String count;
>   
>       private String password;
>   
>       private boolean isRemember;
>       //是否记住密码
>   
>       public User() {
>   
>       }
>   
>       public boolean isRemember() {
>           return isRemember;
>       }
>   
>       public void setRemember(boolean remember) {
>           isRemember = remember;
>       }
>   
>       public User(String count, String password, boolean isRemember) {
>           this.count = count;
>           this.password = password;
>           this.isRemember = isRemember;
>       }
>   
>       public String getCount() {
>           return count;
>       }
>   
>       public void setCount(String id) {
>           this.count = id;
>       }
>   
>       public String getPassword() {
>           return password;
>       }
>   
>       public void setPassword(String password) {
>           this.password = password;
>       }
>   }
>   ```
>
> * 储存账号密码
>
>   > ```java
>   > USER.user.setCount(r_et_count.getText().toString());
>   >         USER.user.setPassword(r_et_password.getText().toString());
>   >         USER.user.setRemember(false);//默认不记住密码
>   >         USER.user.save();
>   > ```
>
> * 登录页面提取数据
>
>   ```java
>   if(users !=null){
>               for(User user:users){
>                   isRemember = user.isRemember();
>                   count = user.getCount();
>                   password = user.getPassword();
>               }
>           }
>   ```

**添加任务**

> * 子item显示目标时间和任务
>
>   ```java
>   //时间
>   private TimePicker mTimePicker;     
>   mTimePicker = view.findViewById(R.id.tp);
>   mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
>               @Override
>               public void onTimeChanged(TimePicker timePicker, int i, int i1) {
>                   mTimeTarget = i+"时"+i1+"分";
>   
>                   Calendar calendar = Calendar.getInstance();
>                   Calendar now = Calendar.getInstance();
>                   calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),mTimePicker.getCurrentHour(),mTimePicker.getCurrentMinute());
>                   timeTarget = SystemClock.elapsedRealtime()+calendar.getTimeInMillis() - now.getTimeInMillis();
>   
>               }
>           });
>   ```
>
>   ```java
>   //任务
>   private String mTaskTarget;
>   mTaskTarget = et.getText().toString();
>   ```
>
>   ```java
>   //存储
>   public class Target extends DataSupport {
>   
>       /**
>        * 目标任务
>        * */
>       private String taskTarget;
>   
>       /**
>        * 目标时间 long取毫秒值
>        */
>       private String timeTarget;
>       /**
>        * 过程时间
>        * */
>       private long process;
>       public Target(){}
>   
>       public Target( String taskTarget, String timeTarget, long process) {
>           this.taskTarget = taskTarget;
>           this.timeTarget = timeTarget;
>           this.process = process;
>       }
>   
>       public long getProcess() {
>           return process;
>       }
>   
>       public void setProcess(long process) {
>           this.process = process;
>       }
>   
>       public String getTaskTarget() {
>           return taskTarget;
>       }
>   
>       public void setTaskTarget(String taskTarget) {
>           this.taskTarget = taskTarget;
>       }
>   
>       public String getTimeTarget() {
>           return timeTarget;
>       }
>   
>       public void setTimeTarget(String timeTarget) {
>           this.timeTarget = timeTarget;
>       }
>   }
>   ```
>
>   ```java
>   //存储
>   target.setTaskTarget(mTaskTarget);
>                       target.setTimeTarget(mTimeTarget);
>                       target.setProcess(timeTarget);
>                       target.save();
>   ```
>
> * 定时提醒
>
>   ```java
>   processTime = target.getProcess();
>                   //创建一个Intent对象
>                   Intent intent = new Intent(HomeActivity.this,AlarmActivity.class);
>                   //获取显示时钟的Activity
>                   PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this,0,intent,0);
>                   //获取AlarmManager对象
>                   AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
>   
>                   alarmManager.set(AlarmManager.RTC_WAKEUP,processTime,pendingIntent);
>   
>                   Toast.makeText(this,"闹钟设置成功",Toast.LENGTH_SHORT).show();
>                   Log.e("SCCESS","闹钟设置成功");
>   ```
>
>   ```java
>   //AlarmActivity
>   @Override
>       protected void onCreate(Bundle savedInstanceState) {
>           super.onCreate(savedInstanceState);
>           setContentView(R.layout.activity_alarm);
>   
>           AlertDialog alertDialog =new AlertDialog.Builder(this).create();
>           alertDialog.setIcon(R.drawable.clock);
>           alertDialog.setTitle("到点了！！");
>           alertDialog.setMessage("该"+task+"啦");
>           Log.e("faffas", String.valueOf(musicService != null));
>          
>           //确定按钮
>           alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
>               @Override
>               public void onClick(DialogInterface dialog, int which) {
>                   Intent intent = new Intent(AlarmActivity.this,HomeActivity.class);
>                   startActivity(intent);
>               }
>           });
>           alertDialog.show();
>       }
>   ```
>
> * 提醒音乐
>
>   ```java
>   //Service
>   public class MusicService extends Service {
>   
>       private MediaPlayer mediaPlayer;
>       @Override
>       public void onCreate() {
>           super.onCreate();
>           //初始化音乐播放器
>           mediaPlayer = MediaPlayer.create(this, R.raw.fooling_mode);
>       }
>   
>       @Nullable
>       @Override
>       public IBinder onBind(Intent intent) {
>           return new MyBinder();
>       }
>   
>       public void playMusic(){
>               mediaPlayer.start();//播放
>       }
>   
>       public void pauseMusic(){
>           mediaPlayer.pause();
>       }
>   
>   
>       public class MyBinder extends Binder{
>           public MusicService getService(){
>               return MusicService.this;
>           }
>   
>       }
>   
>       @Override
>       public void onDestroy() {
>           super.onDestroy();
>           //释放mediaPlayer
>           mediaPlayer.release();
>       }
>   }
>   ```
>
>   ```java
>   //AlarmActivity
>   @Override
>       public void onCreate() {
>           super.onCreate();
>           ...
>               if(musicService != null){
>               musicService.playMusic();
>           }
>       }
>   ```
>   

**删除任务**

> ```java
> /**
> *长按item
> */
> //数据库中删除
> @Override
>             public void setLongClickListener(View view) {
>             ...
>                 if(targets!=null){
>                     for(Target target: targets){
>                         target.delete();
>                     }
>                 }
>             }
> ```
>
> ```java
> //RecyclerView中删除
> @Override
>             public void setLongClickListener(View view) {
>                 addViewed.remove(rvAddNewView.getChildLayoutPosition(view));
>                 addDataAdapter.notifyDataSetChanged();
>                ...
>             }
> ```
>
> 
# 心得体会

> 首先，还是太菜了.....
>
> 然后是通过这次几乎把之前学的都复习了一遍，图片加载器，音乐播放器，litepal数据库的使用，Acticvity之间，Activity与fragment之间的通信等等，还是加深了理解，只听课少实践，到用的时候bug就找上门了。
>
> 时间太紧了，但是对于大家都一样，至少尽力完成了.
