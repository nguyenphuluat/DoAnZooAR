<?php
	require "connect.php";

	$username= $_POST['username'];
	$password= $_POST['password'];

	function checkExistUser($con,$username,$password){
		$query="SELECT count(*) FROM user WHERE username='$username' AND password='$password' AND user.status=1";
		$data=mysqli_query($con,$query);
		if($data) {
			$var = mysqli_fetch_assoc($data);
			if($var['count(*)']==0) return 0;
			else return 1;
		}
		else return 0;
	}

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

	if(checkExistUser($con,$username,$password)){
		$query="SELECT * FROM user WHERE username='$username' AND password='$password' AND user.status=1";
		$data=mysqli_query($con,$query);
		if($data){
			if($row=mysqli_fetch_assoc($data)) {
				$user=new User($row['id'],
								$row['name'],
								$row['username'],
								$row['password'],
								$row['urlAvatar'],
								$row['email'], 
							 	$row['phone'],
								$row['status'],
								$row['level']);
				echo json_encode($user);
			}
		}else{
			echo "1";
		}
	}else{
		echo "0";
	}
?>