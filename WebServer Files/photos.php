<?php
if(!isset($_POST['username'])){
    echo "<script>window.open('http://localhost/mycustomcamera/','_parent');</script>";
} else {
   
}

?>

<html>
    <head>
    <meta http-equiv="expires" content="0"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>    
        
        #wel {
       display:block;
       position:relative;
      
color:white;
width:fit-content;
font-size:18px;
height:fit-content;

    }

   * {
       margin: 0;
       padding: 0;
       box-sizing: border-box;
   }
   
   body {
        display: block;
        overflow: hidden;
        position: relative;
background-color: #0c1022;
border-radius: 50px 5px;
height: fit-content;
width: fit-content;
margin-right:auto;
margin-left:auto;

    }

    #mytable {
        top:5px;
        width:fit-content;
        display: block;
        position: relative;
     
        
        
        
    }

    
            </style>

    </head>
    <body>
        <br>
        <div id="wel">Welcome <?php echo $_POST['username'];?>
</div>   
<table id="mytable">


</table>


<script>
var tab=document.getElementById("mytable");
var usern="<?php echo $_POST['username']; ?>";
    async function test(){
          var user="<?php 
          echo $_POST['username'];
          ?>";

        let x=await fetch("/mycustomcamera/"+user+"/num.txt");
        let y=await x.text();
        return y;   
    }

async function loadimages(){
let n=await test();
if(parseInt(n)==0){
    return;
}


if(navigator.platform=="Win32"){

var row=tab.insertRow(0);

var sh=screen.height;
    var sw=screen.width;
    var testw=Math.round(sw/100);
sw=sw-(4*testw);
    var imgheight=Math.round(calpercentage(19,sh));
    var imgwidth=Math.round(calpercentage(12,sw));

    var wnum=1;
    var testnum=1;

    
    while(wnum<=(Math.ceil(parseInt(n)/7))){
    var row=tab.insertRow(wnum-1);

    for(var i=1;i<=7;i++){
      if(testnum>parseInt(n)){
          continue;
      }
            
var cell=row.insertCell(i-1);
var iname="/mycustomcamera/"+usern+"/a"+(testnum)+".jpg";
cell.innerHTML="<img src='"+iname+"' height='"+imgheight+"' width='"+imgwidth+"'>";
cell.style.padding=testw+"px";
testnum=testnum+1;


}

wnum=wnum+1;
} 





} else {
    


var ash=screen.height;
    var asw=screen.width;

var aimgheight=Math.round(ash/6);
var aimgwidth=Math.round(asw/3);
var testnum=1;
var wnum=1;

while(wnum<=(Math.ceil(parseInt(n)/3))){
    var row=tab.insertRow(wnum-1);

    for(var i=1;i<=3;i++){
      if(testnum>parseInt(n)){
          continue;
      }
            
var cell=row.insertCell(i-1);
var iname="/mycustomcamera/"+usern+"/a"+(testnum)+".jpg";
cell.innerHTML="<img src='"+iname+"' height='"+aimgheight+"' width='"+aimgwidth+"'>";
testnum=testnum+1;


}

wnum=wnum+1;
}




}
}

window.onload=loadimages();



function calpercentage(per,val){
var total=(per/100)*val;
return total;
}
</script>
    </body>
</html>



