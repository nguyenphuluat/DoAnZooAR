<?php
// PHPMailer classes into the global namespace
use PHPMailer\PHPMailer\PHPMailer; 
use PHPMailer\PHPMailer\Exception;
// Base files 
require 'PHPMailer/src/Exception.php';
require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';
require "connect.php";
// create object of PHPMailer class with boolean parameter which sets/unsets exception.
$mail = new PHPMailer(true);                              
try {
    $mail->isSMTP(); // using SMTP protocol                                     
    $mail->Host = 'smtp.gmail.com'; // SMTP host as gmail 
    $mail->SMTPAuth = true;  // enable smtp authentication                             
    $mail->Username = 'ongluatlangvang2@gmail.com';  // sender gmail host              
    $mail->Password = 'luatqctb'; // sender gmail host password                          
    $mail->SMTPSecure = 'tls';  // for encrypted connection                           
    $mail->Port = 587;   // port for SMTP     

    $username=$_POST['username'];
    $cus_email = $_POST['email'];

    $query="SELECT password FROM `user` WHERE username='$username' AND email='$cus_email'";

    $data = mysqli_query($con,$query);
    if($data){
        if ($row=mysqli_fetch_assoc($data)) {
            $ans=$row['password'];
            $cus_mail = 'suport.zooar@gmail.com';
            $cus_sub = 'Your Recovery password';
            $cus_msg = 'this is your password: '.$ans;

            $mail->setFrom($cus_mail, "Suport ZooAr"); // sender's email and name
            $mail->addAddress('ongluatlangvang@gmail.com', "Receiver");// receiver's email and name

            $mail->Subject = $cus_sub;
            $mail->Body    = $cus_msg;

            if($mail->send()){
                echo "1";
            }else echo "0";
        }else echo "0";
    }else echo "0";
} catch (Exception $e) { // handle error.
    echo "error";
}
?>