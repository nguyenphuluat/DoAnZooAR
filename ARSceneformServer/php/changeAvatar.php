<?php
	require "connect.php";

	$iduser=$_POST['iduser'];
	$url= $_POST['path'];

	Class User{
		function User($id,$name,$username,$password,$urlAvatar,$email,$phone,$status,$level){
			$this->Id=$id;
			$this->Name=$name;
			$this->Username=$username;
			$this->Password=$password;
			$this->UrlAvatar=$urlAvatar;
			$this->Status=$status;
			$this->Level=$level;
			$this->Email=$email;
			$this->Phone=$phone;
		}
	}

	$query="UPDATE user set user.urlAvatar='$url' WHERE user.id=$iduser";
	$data=mysqli_query($con,$query);
	if($data){
		echo "1";
	}else{
		echo "0";
	}
?>