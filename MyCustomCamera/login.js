/*$(".input_text").focus(function(){
    $(this).prev('.fa').addclass('glowIcon')
})
$(".input_text").focusout(function(){
    $(this).prev('.fa').removeclass('glowIcon')
})
*/



if(getcookie("lremember")!=null){
    submitform(getcookie("lremember"));
    delcookie("remember");
}



var user=document.getElementById("user");
var pass=document.getElementById("pass");
var sub=document.getElementById("sub");
sub.onclick=onsubmit;




function onsubmit(){
if(user.value==""||pass.value==""){
alert("Plz Fill all the fields");
if(user.value==""){
    user.focus();
} else {
    pass.focus();
}

} else if(spaceval(user.value)!=1 || spaceval(pass.value)!=1){
alert("Username or Password cannot contain White spaces");
  if(spaceval(user.value)!=1 && spaceval(pass.value)!=1){
user.value="";
pass.value="";
user.focus();


  } else if(spaceval(user.value)!=1){
      user.value="";
      user.focus();
   } else {
       pass.value="";
       pass.focus();
   }



} else {
var norpass=pass.value;
pass.value=converttexttocipher(norpass);

var s="user="+user.value+"&password="+pass.value;
var xhttp=new XMLHttpRequest();
xhttp.open("POST","log.php",true);
xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");


 xhttp.onreadystatechange = function() {
         if (xhttp.readyState == XMLHttpRequest.DONE && xhttp.status == 200) {
            if(xhttp.responseText=="Invalid Username Entered"){
                //setTimeout(submitform,2000,user.value);

                user.value="";
                alert("Invalid Username Entered");
              



            } else if(xhttp.responseText=="Incorrect Password Entered"){
                pass.value="";
                alert("invalid Password Entered");

            } else {
                if(getcookie("remember")!=null){
                    setcookie("lremember",xhttp.responseText,7);
                     delcookie("remember");

                } else {

                }


                setTimeout(submitform,2000,xhttp.responseText);

                user.value="";
                pass.value="";
                alert("Login Successfully ");
                
            }
            
         }
      };
      xhttp.send(s);


}



}

function spaceval(x){
    var test=x.split(" ");
    return test.length;
}


function textshadow(y){
    (y.currentTarget.par).style.textShadow="1px 1px #00ff00";
}


function offtextshadow(y){
   // (y.currentTarget.par).style.textShadow="none";
   y.style.textShadow="none";
}

addListener(user);
addListener(pass);
function addListener(ele){
    ele.addEventListener("focus",function(){
        ele.style.textShadow="2px 2px 15px #00ccff";
    });
    
    
    ele.addEventListener("blur",function(){
    ele.style.textShadow="none";
    });
    }



 function converttexttocipher(ntext){
       if(ntext==""){
           return 0;
       }
var ctext="";
       for(var i=0;i<ntext.length;i++){
           var temp=ntext.charCodeAt(i);
           temp=temp+(ntext.length);
             if(temp>122){
                 var test=temp-122;
                 temp=test+65;
             }

ctext=ctext+String.fromCharCode(temp);

       }
return ctext;
    }



    function submitform(u){

    var form=document.createElement("form");
    form.setAttribute("method", "post");
form.setAttribute("action","photos.php");

var hiddenField = document.createElement("input"); 
hiddenField.setAttribute("type", "hidden");
hiddenField.setAttribute("name", "username");
hiddenField.setAttribute("value", u);
form.appendChild(hiddenField);
document.body.appendChild(form);
form.submit();

    }


    function getcookie(name){
        var str=decodeURIComponent(document.cookie);
        var arr=str.split(";");
    
   for(var i=0;i<arr.length;i++){
       arr[i]=arr[i].trim();
   arr[i]=arr[i].split("=");
  }

   for(var i=0;i<arr.length;i++)
   if(name==arr[i][0])
   return arr[i][1];


  return null; 
}


function setcookie(name,val,exday){
var d=new Date();
d.setTime(d.getTime+(exday*24*60*60*1000));

var cookstr=name+"="+val+";expires="+d.toUTCString()+";path=/;";

document.cookie=cookstr;
}


function delcookie(name){
var cookn=name;
var cookstr=cookn+"=";
var ex="expires=Thu, 18 Dec 2013 12:00:00 UTC;";
document.cookie=cookstr+";"+ex+"path=/;";
}
