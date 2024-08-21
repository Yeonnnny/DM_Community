
$(function(){
    $("#submitBtn").click(submitBoardWrite);
    $("#cancelBtn").click(cancelBoardWrite);
    $("#resetBtn").click(resetBoardWrite);
    init();
});

// 게시글 등록 처리 요청
function submitBoardWrite(){
    // ckeditor 내용 content에 채워넣기
    const content = window.editor.getData();
    $("#content").val(content);
    // 유효성 검사
    
    
    console.log("게시글 내용 : "+content);
    alert("Submit!");
    $("#writeForm").submit();
}

// 게시글 등록 취소
function cancelBoardWrite(){

}

// 게시글 등록 초기화
function resetBoardWrite(){
    // ckeditor 내용 지우기
}

function init(){
    // memberGroup 가져오기
    memberId = $("#memberId").val();
    $.ajax({
    url:"/member/getMemberGroup",
    data:{"memberId":memberId},
    method:"GET",
    success : function (result) {
        console.log( "memberGroup : " + result);
        $("#memberGroup").val(result);
    }
    });
}
