


var user=document.getElementById("u");
var email=document.getElementById("e");
var pass=document.getElementById("p");
var cpass=document.getElementById("cp");
var submit=document.getElementById("sub");
submit.addEventListener("click",onsub);

function addListener(ele){
ele.addEventListener("focus",function(){
    ele.style.textShadow="2px 2px 15px #00ccff";
});


ele.addEventListener("blur",function(){
ele.style.textShadow="none";
});
}

addListener(user);
addListener(email);
addListener(pass);
addListener(cpass);


function spaceval(x){
    var test=x.split(" ");
    return test.length;
}



function onsub(){
    if(user.value==""||pass.value==""||email.value==""||cpass.value==""){
    alert("Plz Fill all the fields");
    if(user.value==""){
        user.focus();
    } else if(pass.value==""){
        pass.focus();
    } else if(email.value==""){
        email.focus();
    } else {
        cpass.focus();
    }
    
    } else if(spaceval(user.value)!=1 || spaceval(user.value)!=1 || spaceval(email.value)!=1||spaceval(cpass.value)!=1){
    alert("Username or Password or Email cannot contain White spaces");
      if(spaceval(user.value)!=1 && spaceval(pass.value)!=1 && spaceval(email.value)!=1 &&spaceval(cpass.value)!=1){
    user.value="";
    pass.value="";
    email.value="";
    cpass.value="";
    user.focus();
    
    
      } else if(spaceval(user.value)!=1){
          user.value="";
          user.focus();
       } else if(spaceval(email.value)!=1){
           email.value="";
           email.focus();
       } else if(spaceval(pass.value)!=1){
           pass.value=""
           pass.focus();
       } else {
           cpass.value="";
           cpass.focus();
       }
    
    
    
    } else if(valemail(email.value)!=2){
    alert("Invalid email entered");
    email.value="";
    email.focus();
    } else if(valpass(pass.value)==false){
        alert("Your password should be more than 8 characters and less than 25 characters");
        pass.value="";
        pass.focus();
    } else if(pass.value!=cpass.value){
alert("your passwords are not matching");
pass.value="";
cpass.value="";
pass.focus();

    } else {
var cipherpassword=converttexttocipher(pass.value);
pass.value=cipherpassword;
cpass.value=cipherpassword;



var s="user="+user.value+"&password="+pass.value+"&email="+email.value;
var xhttp=new XMLHttpRequest();
xhttp.open("POST","regis.php",true);
xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");


 xhttp.onreadystatechange = function() {
         if (xhttp.readyState == XMLHttpRequest.DONE && xhttp.status == 200) {
            switch(xhttp.responseText) {
          case "1":
alert("Username "+user.value+" is already Registered with us");
user.value="";

if(document.getElementById("cook").checked){
   
    if(getcookie("remember")!=null){
        // nothing
    } else if(getcookie("remember")==""){
        setcookie("remember","remember",7);
    } else {
        setcookie("remember","remember",7);
        
    }

   
   
   }


break;                      
case "2":
alert("Email "+email.value+" is already Registered with us");
email.value="";

if(document.getElementById("cook").checked){
   
    if(getcookie("remember")!=null){
        // nothing
    } else if(getcookie("remember")==""){
        setcookie("remember","remember",7);
    } else {
        setcookie("remember","remember",7);
        
    }

   
   
   }

break;
case "3":
if(document.getElementById("cook").checked){
   
     if(getcookie("remember")!=null){
         // nothing
     } else if(getcookie("remember")==""){
         setcookie("remember","remember",7);
     } else {
         setcookie("remember","remember",7);
         
     }

    
    
    }


alert("You have been Successfully Registered with us\nLogin To Continue");
user.value="";
email.value="";
pass.value="";
cpass.value="";
setTimeout(gotologinpage,2000);
break;
default:
alert(xhttp.responseText);

}
            
         }
      };
      xhttp.send(s);


        //alert("Registration Successful");
    }
    
    
    
    }


    function valemail(r){
   var t=r.split("@");
   return t.length;
    }


    function valpass(t){
var c=t.split("");
var len=c.length;

if(len<8){
return false;
} else if(len>25){
return false;
} else {
    return true;
}

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


    function gotologinpage(){
        window.open("http://localhost/mycustomcamera/","_parent");
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


