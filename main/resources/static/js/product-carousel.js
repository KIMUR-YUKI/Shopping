//カルーセル画像の表示を制御するスクリプト
document.addEventListener('DOMContentLoaded', function(){//HTMLが読み込んだ後に実行されるようにする
    //クラス名がcarousel-itemのすべての要素（画像）を取得して定数itemsに格納する
    const items = document.querySelectorAll(".carousel-item");

    //インジケーターボタンをすべて取得
    const indicators = document.querySelectorAll(".thumbnail-indicators button");

    //IDがprevBtnの要素（「前へ」ボタン）を取得する
    const prevBtn = document.querySelector(".carousel-control-prev");

    //IDがnextBtnの要素（「次へ」ボタン）を取得する
    const nextBtn = document.querySelector(".carousel-control-next");

    //現在表示されている画像を示すインデックス（添え字）を０で初期化する
    let currentIndex = 0;

    //指定されたインデックスの画像を表示する関数を定義する
    function showImage(index){
        //すべての画像要素（items)をループ処理する
        items.forEach((item, i) => {
            //現在のループのインデックス（i）が、表示したいインデックス（index)と
            //一致する場合にactiveクラスをつけ、そうでなければ外す
            item.classList.toggle("active" , i === index);
        });

        //インジケーターボタンのactiveクラスを切り替える処理を追加
        indicators.forEach((indicator, i) =>{
            indicator.classList.toggle("active", i === index);
        });
    }

    //「前へ」ボタンがクリックされたときのイベントリスナーを設定する
    if(prevBtn){
        prevBtn.addEventListener("click", function(){
        //現在のインデックスを1つ減らし、画像リストの範囲内でループするように計算する
        //(currentIndex -1 + items.length) % items.length
        //例: 0 - 1 = -1となり、items.lengthを足すことで -1 + 3 = 2となり、インデックス2に戻る
        currentIndex = (currentIndex - 1 + items.length) % items.length;
        //新しいインデックスで画像を表示する
        showImage(currentIndex);
        });
    }

    //「次へ」ボタンがクリックされたときのイベントリスナーを設定する
    if(nextBtn){
        nextBtn.addEventListener("click", function(){
            //現在のインデックスを1つ増やし、画像リストの範囲内でループするように計算する
            //(currentIndex + 1) % items.length
            //例: 2 + 1 = 3 となり、itemslength(3)で割った余りは0となり、最初の画像に戻る
            currentIndex = (currentIndex + 1) % items.length;
            //新しいインデックスで画像を表示する
            showImage(currentIndex);
        });
    }

    //サムネイルインジケーターのクリックイベントを設定
    indicators.forEach((indicator, i) =>{
        indicator.addEventListener("click", function(){
            currentIndex = i; //クリックされたインジケーターのインデックスに設定
            showImage(currentIndex); //画像を切り替える
        });
    });

    //ページが読み込まれたときに最初の画像（インデックス0）を初期表示する
    showImage(currentIndex);
});