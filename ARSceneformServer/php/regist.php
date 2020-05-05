<?php
	require "connect.php";

	$username=$_POST['username'];
	$password=$_POST['password'];
	$avatar=$_POST['urlavatar'];
	$displaypname=$_POST['displayname'];
	$email=$_POST['email'];
	$phone=$_POST['phone'];

	function checkExistUser($con,$username){
		$query="SELECT count(*) FROM `user` WHERE user.username='$username'";
		$data=mysqli_query($con,$query);
		if($data) {
			$var = mysqli_fetch_assoc($data);
			if($var['count(*)']==0) return 0;
			else return 1;
		}
		else return 0;
	}


	if(!checkExistUser($con,$username)){
		$query = "INSERT INTO user VALUES (null,'$displaypname','$username','$password','$avatar','$email','$phone',1,1)";

		$data = mysqli_query($con,$query);
		if($data){
			echo "1";
		}else{
			echo "0";
		}
	}else echo "2";
?>