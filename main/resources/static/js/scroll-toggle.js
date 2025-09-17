//直線のスクロール位置を記録する変数（初期値は0 = 一番上）
let lastScroll = 0;

// クラス名「search-bar」の要素を取得（ナビゲーションのボタン群）
const searchBar = document.querySelector('.search-bar');

//スクロールが発生したときに実行されるイベントリスナーを設定
window.addEventListener('scroll', () =>{
    //現在のスクロール位置（縦方向の位置）を取得
    const currentScroll = window.pageYOffset;

    // 今回のスクロール位置が前回よりも下なら「下にスクロールしている」と判定
    if(currentScroll > lastScroll){
        //クラス「hide」を追加→CSSで非表示になる
        searchBar.classList.add('hide');
    }else{
        //上にスクロールしているなら「hide」を削除→表示される
        searchBar.classList.remove('hide');
    }

    //今回の位置を次回用に記録
    lastScroll = currentScroll;
});