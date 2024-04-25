<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "test";

$user=$_POST["user"];
$email=strtolower($_POST["email"]);
$pass=$_POST["password"];

function opensqlcon($sername,$uname,$p,$dbn){
$conn = mysqli_connect($sername, $uname, $p, $dbn);
if(!$conn){
die("Connection failed ".mysqli_connect_error());
}
return $conn;
}

function checkusername($u,$e,$sqlobj){

$chk="SELECT * from camera WHERE username='$u' OR email='$e' ";
$res=mysqli_query($sqlobj,$chk);

if(mysqli_num_rows($res) >0 ){

$row=mysqli_fetch_assoc($res);

if($row["username"]==$u){
echo "1";
closesql($sqlobj);
exit();
} else if($row["email"]==$e){
echo "2";
closesql($sqlobj);
exit();
} 

} else {
regisnewacc($u,$e,$GLOBALS["pass"],$sqlobj);
closesql($sqlobj);
exit();
}

}

function regisnewacc($u,$e,$p,$c){
$sql = "INSERT INTO camera (username,email, password)
VALUES ('$u', '$e', '$p')";

if(mysqli_query($c,$sql)){
mkdir($u);
$file_name=$u."/num.txt";
$file_content="0";
$f=fopen($file_name,"w");
fwrite($f,$file_content);
fclose($f);
echo "3";
} else {
echo "Some error is there ".mysqli_error($conn);
}

}

function closesql($mysqliobj){
mysqli_close($mysqliobj);
}

$s=opensqlcon($servername,$username,$password,$dbname);
checkusername($user,$email,$s);






?>