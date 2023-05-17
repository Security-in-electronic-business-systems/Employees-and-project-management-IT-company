import { SyntheticEvent, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "../model/User";

export function PasswordlessLogin() {
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");

  const navigate = useNavigate();
  var loginResponse: User

  const handleSubmit = async (event: SyntheticEvent) => {
    event.preventDefault();

    // Reset error messages
    setEmailError("");

    // Email and password required validation
    if ((!email || email === "") ) {
      if(!email || email === ""){
        setEmailError("Email is required");
      }
      return
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (email && !emailRegex.test(email)) {
      setEmailError("Invalid email format");
    }



    // If there are errors, don't submit the form
    if (emailError === "Email is required" ) {
      setEmailError(emailError)
      return;
    }

    // Send form data to server
    await fetch("http://localhost:8081/api/v1/auth/passwordless-authenticate", {
      method: "POST",
      headers: {
        "Content-type": "application/json",
      },
      body: JSON.stringify({
        "email": email,
      }),
    }).then(res => res.json())
      .then(data => {

        //loginResponse = data
        console.log(data)

        
    })

  };

  useEffect(() => {
    setEmailError("");
  }, [email]);

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
        <div className="d-grid mt-4">
          <button type="submit" className="btn btn-primary btn-sm">Confirm</button>
          <div className="text-center mt-2">
            <a href="">Login without password</a>
          </div>
        </div>
      </form>
    </div>
  );
}