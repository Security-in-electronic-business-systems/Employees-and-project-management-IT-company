import { useState } from "react";
import { ResponseMessage } from "../model/ResponseMessage";
import { useNavigate, useParams } from "react-router-dom";

function ChangeForgotPassword () {
  const { token } = useParams();
  const navigate = useNavigate()

  const [newPassword, setNewPassword] = useState("");
  const [repeatedNewPassword, setRepeatedNewPassword] = useState("");

  const [newPasswordError, setNewPasswordError] = useState("");
  const [repeatedNewPasswordError, setRepeatedNewPasswordError] = useState("");

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault(); 

    setNewPasswordError("")
    setRepeatedNewPasswordError("")

    if ((!newPassword || newPassword === "") || (!repeatedNewPassword || repeatedNewPassword === "")) {
      if(!newPassword || newPassword === ""){
        setNewPasswordError("New password is required");
      }

      if(!repeatedNewPassword || repeatedNewPassword === ""){
        setRepeatedNewPasswordError("Old password is required");
      }
      return
    }

    console.log(isValidPassword(newPassword))
    if(isValidPassword(newPassword)===false){
      setNewPasswordError("The password must contain a minimum of 8 characters, an uppercase letter, a number and a special character(@#$%^&+=!)!")
      return;
    }

    console.log(isValidPassword(repeatedNewPassword))
    if(isValidPassword(repeatedNewPassword)===false){
      setRepeatedNewPasswordError("The password must contain a minimum of 8 characters, an uppercase letter, a number and a special character(@#$%^&+=!)!")
      return;
    }

    if(newPassword != repeatedNewPassword){
      setRepeatedNewPasswordError("Passwords do not match")
      return
    }
  
    fetch("https://localhost:8081/api/v1/user/changeForgottenPassword", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        "token": token,
        "newPassword": newPassword,
      }),
    }).then(res => res.json())
    .then(data => {
      const message: ResponseMessage = data

      if(message.message === "Successfully!"){
        navigate("/login")
        alert("Password changed successfully!")
      }else{
        alert(message.message)
      }
    })
  };

  const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!])(?=.*[a-zA-Z]).{8,}$/;

  const isValidPassword = (password: string): boolean => {
    return passwordRegex.test(password);
  };

  return (
    <form className="col-md-6 mx-auto" onSubmit={handleSubmit}>
        <blockquote className="blockquote text-center">
          <p className="mb-0">Change password</p>
        </blockquote>
        <div className="mb-3">
            <label className="form-label">New password</label>
            <input type="password" className="form-control" 
                  id="password1" value={newPassword}
                  onChange={(event) => setNewPassword(event.target.value)}/>
                  {newPasswordError && <div className="text-danger">{newPasswordError}</div>}
        </div>
        <div className="mb-3">
            <label className="form-label">Repeat new password</label>
            <input type="password" className="form-control" 
                  id="password2" value={repeatedNewPassword}
                  onChange={(event) => setRepeatedNewPassword(event.target.value)}/>
                  {repeatedNewPasswordError && <div className="text-danger">{repeatedNewPasswordError}</div>}
        </div>
        <button type="submit" className="btn btn-primary">Confirm</button>
    </form>
  );
}

export default ChangeForgotPassword;
