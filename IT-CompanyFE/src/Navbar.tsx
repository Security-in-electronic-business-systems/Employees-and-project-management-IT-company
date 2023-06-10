import { Link, useNavigate  } from 'react-router-dom';
import { UseLoggedUser } from './hooks/UseLoggedUserInformation';
  

function Navbar() {
    const navigate = useNavigate();

    const stylesRight = {
      marginLeft: 'auto',
    };

  const stylesLeft  = {
      marginRight: 'auto',
    };

  const navStyle = {
      display: 'flex',
      justifyContent: 'space-between',
  }

  const handleButtonClick = () => {
    navigate("/register");
  };

  const handleLogout = async () => {
    console.log("aaaa")

    await fetch("https://localhost:8081/api/v1/auth/logout", {
    method: "GET",
    headers: {
      "Content-type": "application/json",
    },
    mode: "no-cors",
    credentials: "include",
  }).then(_ => {
    localStorage.setItem('loggedUser', '')
    navigate("/login")
  })
  };

  const loggedUser = UseLoggedUser()

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light" style={navStyle}>
      <ul className="navbar-nav mr-auto" style={stylesLeft}>
        <li className="nav-item active">
          <Link className="nav-link" to="/">Home</Link>
        </li>
        {loggedUser?.role.name.toString() === "SOFTWARE_ENGINEER"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/enginerProfil">Profile</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "SOFTWARE_ENGINEER"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/careerPage">Career page</Link>
        </li>)}

        {loggedUser?.role.name.toString() === "SOFTWARE_ENGINEER"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/employeeProjects">Projects</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "PROJECT_MANAGER"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/projectManagerProfil">Profile</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "PROJECT_MANAGER"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/managerProjects">Projects</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "HR_MANAGER"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/profil">Profile</Link>
        </li>)}

        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/profil">Profile</Link>
        </li>)}

        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/ViewAllRegistrationRequests">RegistrationRequests</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/ViewAll">ViewUsers</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/ViewAllProjects">ViewProjects</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/createProject">CreateProject</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/registerAdmin">RegisterAdmin</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/permissions">Permissions</Link>
        </li>)}
        {loggedUser?.role.name.toString() === "ADMINISTRATOR"  && (
            <li className="nav-item active">
          <Link className="nav-link" to="/search">SearchEngineers</Link>
        </li>)}
      </ul>
      <ul className="navbar-nav mr-auto" style={stylesRight}>
      <li className="nav-item active" >
          {loggedUser == null ? (
            <Link className="nav-link" to="/login">Login</Link>

          ) : (
            //<Link className="nav-link" to="/logout">Logout</Link>
            <button className="btn btn-light" type="submit" onClick={handleLogout}>Logout</button>
          )}
        </li>
        <li className="nav-item active" >
            <button className="btn btn-outline-success my-2 my-sm-0" type="submit" onClick={handleButtonClick}>Register</button>
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;