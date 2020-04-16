<?php
	require "connect.php";
	$iduser= $_POST['iduser'];
	$idmodel= $_POST['idmodel'];

	function checkExistView($con,$iduser,$idmodel){
		$query="SELECT count(*) FROM `view` WHERE idUser=$iduser AND idModel=$idmodel";
		$data=mysqli_query($con,$query);
		if($data) {
			$var = mysqli_fetch_assoc($data);
			if($var['count(*)']==0) return 0;
			else return 1;
		}
		else return 0;
	}

	function numpp($con,$iduser,$idmodel){
		$query="UPDATE `view` SET `num` = `num`+1 WHERE `view`.`idUser` = $iduser AND `view`.`idModel`=$idmodel";
		$data=mysqli_query($con,$query);
		if($data) return 1;
		else return 0;
	}

	function createView($con,$iduser,$idmodel){
		$query="INSERT INTO `view` (`idView`, `idUser`, `idModel`, `num`) VALUES (NULL, '$iduser', '$idmodel', '1')";
		$data=mysqli_query($con,$query);
		if($data) return 1;
		else return 0;
	}

	function numPlus($con,$iduser,$idmodel){
		if(checkExistView($con,$iduser,$idmodel)){
			return numpp($con,$iduser,$idmodel);
		}else return createView($con,$iduser,$idmodel);
	}

	function updateView($con,$idmodel){
		$query="UPDATE model set model.view=model.view+1 WHERE model.id=$idmodel";
		$data=mysqli_query($con,$query);
		if($data) return 1;
		else return 0;
	}

	if(updateView($con,$idmodel) && numPlus($con,$iduser,$idmodel)){
		echo "1";
	}else echo "0";
?>