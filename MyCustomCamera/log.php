<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "test";

$user=$_POST["user"];
$pass=$_POST["password"];


function checkuser($u,$p,$sqlobj){
$sql="SELECT * from camera WHERE username='$u' OR email='$u'";
$res=mysqli_query($sqlobj,$sql);

if(mysqli_num_rows($res) >0 ){
   $row=mysqli_fetch_assoc($res);  
     
if($row["password"]==$p){
echo $row['username'];
closesql($sqlobj);
exit();
        } else {
echo "Incorrect Password Entered";
closesql($sqlobj);
exit();
}
  

} else {
echo "Invalid Username Entered";
closesql($sqlobj);
exit();
}


}

function opensqlcon($sername,$uname,$p,$dbn){
$conn = mysqli_connect($sername, $uname, $p, $dbn);
if(!$conn){
die("Connection failed ".mysqli_connect_error());
}
return $conn;
}

function closesql($mysqliobj){
mysqli_close($mysqliobj);
}

$b=opensqlcon($servername,$username,$password,$dbname);
checkuser($user,$pass,$b);
?>