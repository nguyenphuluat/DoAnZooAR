<?php
	require "connect.php";

	$iduser=$_POST['iduser'];
	$status=$_POST['status'];

	if(strlen($iduser)>0 && strlen($status)>0){
		$query="UPDATE user SET status=$status WHERE id=$iduser";
		$data=mysqli_query($con,$query);
		if($data){
			echo "1";
		}else{
			echo "0";
		}
	}else{
		echo "null";
	}
?>