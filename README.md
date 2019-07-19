# bottom-navigation

## 概要
BottomNavigationViewに6個以上のタブを追加できるようにする必要があったため作成しました。    

## タブの追加
動的にタブを増やすにあたりmenu.xmlで定義するのではなくコード上からaddTabできるようにしています。

```kotlin
bottomNavigation
    .addTab(R.drawable.ic_home, "ホーム")
    .addTab(R.drawable.ic_message, "メッセージ")
    .addTab(R.drawable.ic_people, "フレンド")
    .addTab(R.drawable.ic_shopping_cart, "買い物")
    .addTab(R.drawable.ic_train, "時刻表")
    .addTab(R.drawable.ic_map, "地図")
```

## バッジ表示
タブはpositionを指定して取得します。  
99以上の値をバッジにセットすると「99+」の表示になります。

```kotlin
bottomNavigation.getTab(1)?.showBadge()?.setNumber(5)
```
