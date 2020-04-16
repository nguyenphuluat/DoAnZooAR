<?php
	require "connect.php";

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

	$query="SELECT * FROM user";
	$manguser=array();
	$data=mysqli_query($con,$query);
	if($data){
		while ($row=mysqli_fetch_assoc($data)) {
			array_push($manguser, new User($row['id'],
							$row['name'],
							$row['username'],
							$row['password'],
							$row['urlAvatar'],
							$row['email'], 
						 	$row['phone'],
							$row['status'],
							$row['level']));
		}

		echo json_encode($manguser);
	}else{
		echo "0";
	}
?>