dview-toggle-button
![Release](https://jitpack.io/v/dora4/dview-toggle-button.svg)
--------------------------------

#### gradle依赖配置

```groovy
// 添加以下代码到项目根目录下的build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// 添加以下代码到app模块的build.gradle
dependencies {
    def latest_version = '1.2'
    implementation 'com.github.dora4:dview-toggle-button:$latest_version'
}
```
#### 使用方式

```xml
   <com.dorachat.dorachat.widget.DoraToggleButton
        android:layout_width="41dp"
        android:layout_height="26dp"
        android:layout_alignParentEnd="true"
        android:layout_centerVertical="true"
        app:dora_checked="true"
        app:dora_uncheck_color="@color/light_gray"
        app:dora_background="@color/light_gray"
        app:dora_checked_color="@color/colorPrimary"
        app:dora_border_width="2dp"
        app:dora_show_indicator="false" />
```
