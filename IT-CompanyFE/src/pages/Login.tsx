import { SyntheticEvent, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { LoginResponse } from "../model/login-response";

export function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");

  const navigate = useNavigate();
  let loginResponse: LoginResponse

  const handleSubmit = async (event: SyntheticEvent) => {
    event.preventDefault();

    // Reset error messages
    setEmailError("");
    setPasswordError("");

    // Email and password required validation
    if ((!email || email === "") || (!password || password === "")) {
      if(!email || email === ""){
        setEmailError("Email is required");
      }
      if(!password || password === ""){
        setPasswordError("Password is required");
      }
      return
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (email && !emailRegex.test(email)) {
      setEmailError("Invalid email format");
    }



    // If there are errors, don't submit the form
    if (emailError === "Email is required" || passwordError === "Password is required") {
      setEmailError(emailError)
      setPasswordError(passwordError)
      return;
    }

    // Send form data to server
    await fetch("https://localhost:8081/api/v1/auth/authenticate", {
      method: "POST",
      headers: {
        "Content-type": "application/json",
      },
      body: JSON.stringify({
        "email": email,
        "password": password
      }),
      credentials: "include"
    }).then(res => res.json())
      .then(data => {

        loginResponse = data
        if(loginResponse.message === "Successfully!"){
          localStorage.setItem('loggedUser', JSON.stringify(loginResponse));
          navigate("/")
          return
        }else if(loginResponse.message === "Email or password are not correct!"){
          setPasswordError("Email or password are not correct!")
          return
        }

        setPasswordError("Some error occured, please try again!")
    })

  };

  useEffect(() => {
    setPasswordError("");
  }, [password]);

  useEffect(() => {
    setEmailError("");
  }, [email]);

  const handlePasswordlessLogin = () => {
    navigate("/passwordless-login"); 
  };  

  const handleOnClick = async (event: SyntheticEvent) => {
    event.preventDefault()
    // Send form data to server
    await fetch("https://localhost:8081/api/v1/demo/endpoint", {
      method: "GET",
      headers: {
        "Content-type": "application/json",
      },
      //mode: "no-cors",
      credentials: "include"
    }).then(res => res.json())
      .then(data => {

        console.log(data)

        
    })

  };

  return (
    <div className="d-flex align-items-center justify-content-center" style={{ height: "80vh" }}>
  <form className="col-md-3" onSubmit={handleSubmit}>
    <blockquote className="blockquote text-center">
      <p className="mb-0">Login</p>
    </blockquote>
    <div className="mb-3">
      <label className="form-label">Email</label>
      <input
        className="form-control"
        id="departure"
        value={email}
        onChange={(event) => setEmail(event.target.value)}
      />
      {emailError && <div className="text-danger">{emailError}</div>}
    </div>
    <div className="mb-3">
      <label className="form-label">Password</label>
      <input
        className="form-control"
        type="password"
        id="destination"
        value={password}
        onChange={(event) => setPassword(event.target.value)}
      />
      {passwordError && <div className="text-danger">{passwordError}</div>}
    </div>
    <div className="d-grid mt-4">
      <button type="submit" className="btn btn-primary btn-sm">Confirm</button>
      <div className="text-center mt-2">
        <a href="" onClick={handlePasswordlessLogin}>Login without password</a>
        <button onClick={handleOnClick}>Klikni</button>
      </div>
    </div>
  </form>
</div>

  );
}