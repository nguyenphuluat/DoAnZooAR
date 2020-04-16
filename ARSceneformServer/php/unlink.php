<?php
	$link=$_POST['link'];

	if(strlen($link)>0){
		unlink($link);
		echo "ok";
	}else{
		echo "null";
	}
?>