function openModal(){
    document.getElementById('logoutModal').style.display = 'block';
}

function closeModal(){
    document.getElementById('logoutModal').style.display = 'none';
}

//背景クリックで閉じる
window.onclick  = function(event){
    const modal = document.getElementById("logoutModal");
    if(event.target === modal){
        modal.style.display = "none";
    }
}