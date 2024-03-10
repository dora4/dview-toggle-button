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
   <dora.widget.DoraToggleButton
                android:id="@+id/tb_type_select"
                android:layout_width="41dp"
                android:layout_height="26dp"
                android:layout_alignParentEnd="true"
                android:layout_centerVertical="true"
                app:dview_checked="true"
                app:dview_uncheck_color="@color/light_gray"
                app:dview_background="@color/light_gray"
                app:dview_checked_color="@color/colorPrimary"
                app:dview_border_width="2dp"
                app:dview_show_indicator="false" />
```
