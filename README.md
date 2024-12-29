dview-toggle-button
![Release](https://jitpack.io/v/dora4/dview-toggle-button.svg)
--------------------------------

##### 卡名：Dora视图 ToggleButton 
###### 卡片类型：效果怪兽
###### 属性：地
###### 星级：4
###### 种族：机械族
###### 攻击力/防御力：1500/1800
###### 效果：此卡不会因为对方卡的效果而破坏，并可使其无效化。此卡攻击里侧守备表示的怪兽时，若攻击力高于其守备力，则给予对方此卡原攻击力的伤害，并抽一张卡。此卡每次造成战斗伤害后，可选择墓地中的一张魔法或陷阱卡，将其加入手牌。

#### Gradle依赖配置

```groovy
// 添加以下代码到项目根目录下的build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// 添加以下代码到app模块的build.gradle
dependencies {
    def latest_version = '1.5'
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
                app:dview_uncheckColor="@color/light_gray"
                app:dview_backgroundColor="@color/light_gray"
                app:dview_checkedColor="@color/colorPrimary"
                app:dview_borderWidth="2dp"
                app:dview_showIndicator="false" />
```
