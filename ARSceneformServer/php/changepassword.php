<?php
	require "connect.php";

	$iduser=$_POST['iduser'];
	$password= $_POST['password'];

	$query="UPDATE user set user.password='$password' WHERE user.id=$iduser";
	$data=mysqli_query($con,$query);
	if($data){
		echo "1";
	}else{
		echo "0";
	}
?>