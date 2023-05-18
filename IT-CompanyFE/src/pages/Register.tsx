import { SyntheticEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaChalkboardTeacher, FaEnvelope, FaLock, FaPhone, FaSearchLocation, FaUser, FaUserTag } from "react-icons/fa";

export function Register() {
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [title, setTitle] = useState("");
  const [, setEmailError] = useState("");
  const [password, setPassword] = useState("");
  //const [inputValue, setInputValue] = useState('');
  const [repeatPassword, setRepeatPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [country, setCountry] = useState("");
  const [city, setCity] = useState("");
  const [street, setStreet] = useState("");
  const [number, setNumber] = useState("");
  const [role, setRole] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (event: SyntheticEvent) => {
    event.preventDefault();

    // Validation checks
    if (password === "") {
      setPasswordError("Please enter your password.");
      return;
    }
    console.log(isValidPassword(password))
    if(isValidPassword(password)===false){
      setPasswordError("Lozinka mora da sadrzi minimalno 8 karaktera, veliko slovo, broj i specijalni karakter(@#$%^&+=!)!")
      return;
    }
    if (password !== repeatPassword) {
      setPasswordError("Passwords do not match.");
      return;
    }

    setPasswordError("")

    await fetch("https://localhost:8081/api/v1/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        "firstname":name,
        "lastname": surname,
        "email": email,
        "password":password,
        "phoneNumber": phoneNumber,
        "address": {"country": country, "city": city, "street": street, "number": number},
        "role": role
      }),
    })
      .then((response) => {
        if (response.status === 302) {
          setEmailError("Email already exists.");
          return;
        }
        if (response.ok) {
          navigate("/Login");
        }
      })
      .catch((error) => console.log(error));
  };

  const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!])(?=.*[a-zA-Z]).{8,}$/;

  const isValidPassword = (password: string): boolean => {
    return passwordRegex.test(password);
  };

  return (
    <form className="col-md-6 mx-auto" onSubmit={handleSubmit}>
      <blockquote className="blockquote text-center">
        <p className="mb-0">Register as new user</p>
        <footer className="blockquote-footer">Welcome to <cite title="Source Title">ITCompany</cite></footer>
      </blockquote>
      <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="name">
            <FaUser className="me-2" /> Name
          </label>
          <input 
            className="form-control"
            id="name"
            value={name}
            onChange={(event) => setName(event.target.value)} required
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="surname">
            <FaUser className="me-2" /> Surname
          </label>
          <input 
            className="form-control"
            id="surname"
            value={surname}
            onChange={(event) => setSurname(event.target.value)} required
          />
        </div>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="email">
          <FaEnvelope className="me-2" /> Email address
        </label>
        <input 
          className="form-control"
          id="email" 
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)} required
        />
      </div>
      <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="password">
            <FaLock className="me-2" /> Password
          </label>
          <input 
            type="password" 
            className="form-control" 
            id="password" 
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
          </div>
          <div className="col">
            <label className="form-label" htmlFor="password1">
            <FaLock className="me-2" /> Repeated password
            </label>
            <input 
              type="password" 
              className="form-control" 
              id="password1" 
              value={repeatPassword}
              onChange={(event) => setRepeatPassword(event.target.value)}
              />
        </div>
        {passwordError && <div className="text-danger">{passwordError}</div>}
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="phoneNumber">
          <FaPhone className="me-2" /> Phone number
        </label>
        <input 
          className="form-control"
          id="phoneNumber" 
          type="text"
          value={phoneNumber}
          onChange={(event) => setPhoneNumber(event.target.value)} required
        />
      </div>
      <div className="row mb-3">
        <div className="col">
        <label className="form-label">
          <FaChalkboardTeacher className="me-2" />Professional title
        </label>
        <input 
          className="form-control"
          id="title" 
          type="text"
          value={title}
          onChange={(event) => setTitle(event.target.value)} required
        />
        </div>
        <div className="col">
          <label className="form-label">
            <FaUserTag className="me-2" />Role
          </label>
          <select
            className="form-control"
            id="role"
            onChange={(event) => setRole(event.target.value)}
            required
          >
            <option value="SOFTWARE_ENGINEER">SOFTWARE_ENGINEER</option>
            <option value="PROJECT_MANAGER">PROJECT_MANAGER</option>
            <option value="HR_MANAGER">HR_MANAGER</option>
          </select>
        </div>
      </div>
      <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="country">
            <FaSearchLocation className="me-2" /> Country
          </label>
          <input 
            className="form-control"
            id="country"
            value={country}
            onChange={(event) => setCountry(event.target.value)} required
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="city"> City
          </label>
          <input 
            className="form-control"
            id="city"
            value={city}
            onChange={(event) => setCity(event.target.value)} required
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="street">Street
          </label>
          <input 
            className="form-control"
            id="street"
            value={street}
            onChange={(event) => setStreet(event.target.value)} required
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="number">Number
          </label>
          <input 
            className="form-control"
            id="number"
            value={number}
            onChange={(event) => setNumber(event.target.value)} required
          />
        </div>
      </div>      
      <button type="submit" className="btn btn-primary">Submit</button>
    </form>

  );
  }  

  export default Register;