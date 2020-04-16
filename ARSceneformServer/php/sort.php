<?php
	require "connect.php";
	$kieu=$_POST['kieu'];
	$idUser=$_POST['iduser'];

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

	if($kieu==0) $query="SELECT * FROM `model` WHERE status=1 ORDER BY view DESC ";
	else if($kieu==1) $query="SELECT * FROM model WHERE status=1 ORDER BY id ASC";
	else if($kieu==2) $query="SELECT * FROM `model` WHERE EXISTS (SELECT * FROM view WHERE view.idModel=model.id AND view.idUser=$idUser)
		UNION
		SELECT * FROM `model`";
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