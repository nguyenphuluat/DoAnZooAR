<?php
	require "connect.php";

	$idmodel=$_POST['idmodel'];
	$status=$_POST['status'];

	if(strlen($idmodel)>0 && strlen($status)>0){
		$query="UPDATE model SET status=$status WHERE id=$idmodel";
		$data=mysqli_query($con,$query);
		if($data){
			echo "0";
		}else{
			echo "1";
		}
	}else{
		echo "null";
	}
?>