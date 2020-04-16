<?php
	require "connect.php";

	$level=$_POST['level'];

	Class Animal{
		function Animal($id,$name,$urlimage,$urlmodel,$urlaudio,$infomation,$status,$level){
			$this->Id=$id;
			$this->Name=$name;
			$this->Urlimage=$urlimage;
			$this->Urlmodel=$urlmodel;
			$this->Urlaudio=$urlaudio;
			$this->Infomation=$infomation;
			$this->Status=$status;
			$this->Level=$level;
		}
	}

	if($level==2){
		$query="SELECT * FROM model WHERE run=1";
	}else{
		$query="SELECT * FROM model WHERE status=1 AND run=1";
	}

	$data=mysqli_query($con,$query);
	$manganimal=array();
	if($data){
		while ($row=mysqli_fetch_assoc($data)) {
			array_push($manganimal, new Animal($row['id'],
												$row['name'],
												$row['urlImage'],
												$row['urlModel'],
												$row['urlaudio'],
												$row['infomation'],
												$row['status'],
												$row['level']));
		}
		
		echo json_encode($manganimal);
		
	}else{
		echo "0";
	}
?>