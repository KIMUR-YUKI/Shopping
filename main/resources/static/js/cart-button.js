//ページを離れる前にスクロール位置を保存

//ブラウザのウィンドウが閉じられる直前や再読み込みされる直前に「これをしてね」という命令を登録
window.addEventListener("beforeunload", function(){
    /*window.scrollYは画面が上からどれだけスクロールされたかのピクセル数。（300px下にスクロールしたら300）
    それをlocalStorageというブラウザが保存できる箱に、キー（名前）を"scrollY"として保存*/
    localStorage.setItem("scrollY", window.scrollY);
});
//→どこまでスクロールしてたかを一時的に記憶できる。

//ページ読み込み時に保存されたスクロール位置に戻す
window.addEventListener("load", function(){//ページが完全に読み込まれたときに実行する処理
    const scrollY = localStorage.getItem("scrollY");//さっき保存してたscrollYの値を取り出す
    if(scrollY !== null){//scrollYが空じゃないなら
        window.scrollTo(0, parseInt(scrollY));//scrollTo(x,y)はページを指定した位置までスクロールする関数
        localStorage.removeItem("scrollY");//スクロールの位置を使い終わり、削除
    }
});

/*
    注文手続き画面で数量増減を押したときの非同期処理
*/

//メタタグからトークン取得
const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

function changeQuantity(productId, action){//productId（商品ID）とaction（increase or decrease）を引数に取る関数
    fetch(`/cart/api/${action}/${productId}`,{//cart/api/increase/1または/cart/api/decrease/1などにPOSTリクエストを送信
        method: 'POST',                         //CartRestControllerがこれを受け取る
        headers:{
            [header]: token //ここでCSRFトークンを送る
        }
    })
    .then(response =>{
        if(!response.ok) throw new Error("通信エラーです");
        return response.json();//サーバーから返されたJSONをJSオブジェクトに変換
    })
    .then(data =>{
        if(data.quantity === 0){
            const itemElement = document.getElementById(`item-${productId}`);
            if(itemElement){
                itemElement.remove();
            }
        }else{
            //数量の表示を更新
            document.getElementById(`quantity-${productId}`).innerText = data.quantity;
            
            // 小計の表示を更新
            document.getElementById(`subtotal-${productId}`).innerText = `小計（税込み）¥${data.subtotal}`;
        }
        
        // 合計金額の表示
        document.getElementById(`total-price`).innerText = `¥${data.totalPrice}`;
    })
}

// 注文手続き画面で商品をカートから削除する非同期処理
function removeItem(productId){
    //DELETEメソッドで削除APIを呼び出す
    fetch(`/cart/api/remove/${productId}`,{
        method: 'DELETE',
        headers:{
            [header]: token //ここでCSRFトークンを送る
        }
    })
    .then(response =>{
        if(!response.ok)throw new Error("通信エラーです");
        return response.json();//JSONをパース
    })
    .then(data => {
        //DOMから該当の商品ブロック削除
        const itemElement = document.getElementById(`item-${productId}`);
        if(itemElement){
            itemElement.remove();
        }

        //合計金額を更新
        document.getElementById('total-price').innerText = `¥${data.totalPrice}`;
    })
    
}